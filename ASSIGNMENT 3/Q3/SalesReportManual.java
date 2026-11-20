package q3;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Question 3: Daily Sales Report Generator - Manual Resource Management
 * 
 * Demonstrates:
 * - Resource leaks under traditional try-catch-finally
 * - How an exception during reading leaves readers/writers unclosed
 * - Masking of primary exception if finally block throws an exception
 * - Tracking open file handles during execution
 * 
 * Student: Kattiripalli Dhanush (192311094)
 */
public class SalesReportManual {

    public static int openFileHandles = 0;
    public static int totalFilesOpened = 0;
    public static int totalFilesClosed = 0;

    /**
     * Tracked reader wrapper to record OS file handle allocation and deallocation.
     */
    public static class TrackedBufferedReader extends BufferedReader {
        private final String resourceName;
        private boolean isClosed = false;

        public TrackedBufferedReader(FileReader reader, String name) {
            super(reader);
            this.resourceName = name;
            openFileHandles++;
            totalFilesOpened++;
            System.out.println("[OPEN] Acquired handle for: " + resourceName + " (Active handles: " + openFileHandles + ")");
        }

        @Override
        public void close() throws IOException {
            if (!isClosed) {
                super.close();
                isClosed = true;
                openFileHandles--;
                totalFilesClosed++;
                System.out.println("[CLOSE] Released handle for: " + resourceName + " (Active handles: " + openFileHandles + ")");
            }
        }
    }

    /**
     * Tracked writer wrapper.
     */
    public static class TrackedBufferedWriter extends BufferedWriter {
        private final String resourceName;
        private boolean isClosed = false;

        public TrackedBufferedWriter(FileWriter writer, String name) {
            super(writer);
            this.resourceName = name;
            openFileHandles++;
            totalFilesOpened++;
            System.out.println("[OPEN] Acquired handle for: " + resourceName + " (Active handles: " + openFileHandles + ")");
        }

        @Override
        public void close() throws IOException {
            if (!isClosed) {
                super.close();
                isClosed = true;
                openFileHandles--;
                totalFilesClosed++;
                System.out.println("[CLOSE] Released handle for: " + resourceName + " (Active handles: " + openFileHandles + ")");
            }
        }
    }

    /**
     * Flawed manual resource management approach:
     * Reads all store files into a list of readers and writes summary.
     * If an error occurs on store 3, closing in finally is vulnerable to partial failure.
     */
    public static boolean generateReport(List<String> storeFiles, String summaryPath) {
        TrackedBufferedWriter writer = null;
        List<TrackedBufferedReader> readers = new ArrayList<>();
        String currentFailingFile = "None";

        try {
            System.out.println("[START] SalesReportManual processing started.");
            writer = new TrackedBufferedWriter(new FileWriter(summaryPath), summaryPath);
            writer.write("================ DAILY SALES SUMMARY (MANUAL) ================\n");

            double grandTotal = 0.0;
            int totalItemsSold = 0;

            for (String storeFile : storeFiles) {
                currentFailingFile = storeFile;
                System.out.println("[PROCESSING] Opening and parsing: " + storeFile);
                TrackedBufferedReader reader = new TrackedBufferedReader(new FileReader(storeFile), storeFile);
                readers.add(reader); // Kept in list to close at end (common buggy pattern)

                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("ITEM:")) {
                        // Expected format: ITEM: Name, QTY: X, PRICE: Y
                        String[] tokens = line.split(",");
                        String itemName = tokens[0].split(":")[1].trim();
                        int qty = Integer.parseInt(tokens[1].split(":")[1].trim());
                        double price = Double.parseDouble(tokens[2].split(":")[1].trim());
                        double lineTotal = qty * price;

                        grandTotal += lineTotal;
                        totalItemsSold += qty;
                        writer.write(String.format("Store File: %s | %s | Qty: %d | Total: Rs. %.2f\n",
                                storeFile, itemName, qty, lineTotal));
                    }
                }
            }

            writer.write("--------------------------------------------------------------\n");
            writer.write(String.format("GRAND TOTAL REVENUE: Rs. %.2f | TOTAL ITEMS: %d\n", grandTotal, totalItemsSold));
            writer.write("==============================================================\n");
            System.out.println("[SUCCESS] Report generated successfully without exceptions.");
            return true;

        } catch (NumberFormatException nfe) {
            // Step-through debugging evidence: Corrupt record in store3
            System.err.println("[LOG ERROR] Parsing failure in file: " + currentFailingFile + " -> " + nfe.getMessage());
            return false;

        } catch (IOException ioe) {
            System.err.println("[LOG ERROR] I/O failure in file: " + currentFailingFile + " -> " + ioe.getMessage());
            return false;

        } finally {
            // FLAWED MANUAL CLEANUP:
            // If writer throws an exception or reader throws, closing terminates abruptly!
            System.out.println("[FINALLY] Commencing manual cleanup of file handles...");
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                System.err.println("Failed to close writer: " + e.getMessage());
            }

            // Flawed reader closing: Iterates and closes, but if any close() fails or if readers weren't tracked, handles leak!
            // Notice: If readers were opened inside loop without a dedicated inner finally, an exception during loop aborts
            // and unclosed readers stay open until GC.
            for (TrackedBufferedReader br : readers) {
                try {
                    br.close();
                } catch (IOException e) {
                    System.err.println("Failed to close reader: " + e.getMessage());
                }
            }
        }
    }

    public static void main(String[] args) {
        System.out.println("============================================================");
        System.out.println("  MANUAL RESOURCE MANAGEMENT EXPERIMENT (LEAK DEMONSTRATION) ");
        System.out.println("============================================================");

        List<String> validFiles = List.of("store1.txt", "store2.txt", "store4.txt");
        List<String> failingFiles = List.of("store1.txt", "store2.txt", "store3_corrupt.txt", "store4.txt");

        // Run 1: Valid Files
        System.out.println("\n--- RUN 1: Valid Files ---");
        openFileHandles = 0;
        generateReport(validFiles, "summary_manual_valid.txt");
        System.out.println("End of Run 1 -> Active Leaked Handles: " + openFileHandles);

        // Run 2: Failing Files (store3 is corrupt)
        System.out.println("\n--- RUN 2: Failing Files (Failure on Store 3) ---");
        openFileHandles = 0;
        totalFilesOpened = 0;
        totalFilesClosed = 0;
        generateReport(failingFiles, "summary_manual_failing.txt");
        System.out.println("End of Run 2 -> Opened: " + totalFilesOpened + ", Closed: " + totalFilesClosed + ", Active Leaked Handles: " + openFileHandles);
    }
}
