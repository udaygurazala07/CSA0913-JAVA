package q3;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * Question 3: Daily Sales Report Generator - Try-With-Resources Implementation
 * 
 * Demonstrates:
 * - Deterministic, guaranteed resource closure (AutoCloseable)
 * - Reverse order of resource closure
 * - Suppressed exceptions handling (Throwable.getSuppressed())
 * - Prevention of "Too many open files" OS file descriptor exhaustion
 * 
 * Student: Kattiripalli Dhanush (192311094)
 */
public class SalesReportTryWithResources {

    public static int openFileHandles = 0;
    public static int totalFilesOpened = 0;
    public static int totalFilesClosed = 0;

    /**
     * Instrumented AutoCloseable reader to observe exact close timing and order.
     */
    public static class AutoTrackedReader extends BufferedReader {
        private final String resourceName;
        private boolean isClosed = false;

        public AutoTrackedReader(FileReader fr, String name) {
            super(fr);
            this.resourceName = name;
            openFileHandles++;
            totalFilesOpened++;
            System.out.println("[TWR OPEN] Opened resource: " + resourceName + " (Active handles: " + openFileHandles + ")");
        }

        @Override
        public void close() throws IOException {
            if (!isClosed) {
                super.close();
                isClosed = true;
                openFileHandles--;
                totalFilesClosed++;
                System.out.println("[TWR CLOSE] Automatically closed resource: " + resourceName + " (Active handles: " + openFileHandles + ")");
            }
        }
    }

    /**
     * Instrumented AutoCloseable writer.
     */
    public static class AutoTrackedWriter extends BufferedWriter {
        private final String resourceName;
        private boolean isClosed = false;

        public AutoTrackedWriter(FileWriter fw, String name) {
            super(fw);
            this.resourceName = name;
            openFileHandles++;
            totalFilesOpened++;
            System.out.println("[TWR OPEN] Opened resource: " + resourceName + " (Active handles: " + openFileHandles + ")");
        }

        @Override
        public void close() throws IOException {
            if (!isClosed) {
                super.close();
                isClosed = true;
                openFileHandles--;
                totalFilesClosed++;
                System.out.println("[TWR CLOSE] Automatically closed resource: " + resourceName + " (Active handles: " + openFileHandles + ")");
            }
        }
    }

    /**
     * Fault-tolerant, resource-safe report generation using Try-With-Resources.
     */
    public static boolean generateReport(List<String> storeFiles, String summaryPath) {
        System.out.println("[START] SalesReportTryWithResources started.");
        double grandTotal = 0.0;
        int totalItemsSold = 0;
        int successfulStores = 0;
        int failedStores = 0;

        // Outer try-with-resources managing the master summary output stream
        try (AutoTrackedWriter writer = new AutoTrackedWriter(new FileWriter(summaryPath), summaryPath)) {
            writer.write("============ DAILY SALES SUMMARY (TRY-WITH-RESOURCES) ============\n");

            for (String storeFile : storeFiles) {
                System.out.println("[TWR] Processing store file: " + storeFile);

                // Per-file try-with-resources: Reader is GUARANTEED to close upon loop iteration exit or exception
                try (AutoTrackedReader reader = new AutoTrackedReader(new FileReader(storeFile), storeFile)) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (line.startsWith("ITEM:")) {
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
                    successfulStores++;

                } catch (NumberFormatException nfe) {
                    failedStores++;
                    System.err.println("[LOG TWR] Handled corrupt data in store file: " + storeFile + " (" + nfe.getMessage() + ")");
                    // Reader was automatically closed by TWR BEFORE entering this catch block!
                    System.out.println("[VERIFY] Active open handles right inside catch: " + openFileHandles);

                } catch (IOException ioe) {
                    failedStores++;
                    System.err.println("[LOG TWR] Handled I/O failure in store file: " + storeFile + " (" + ioe.getMessage() + ")");
                }
            }

            writer.write("------------------------------------------------------------------\n");
            writer.write(String.format("GRAND TOTAL REVENUE: Rs. %.2f | TOTAL ITEMS: %d\n", grandTotal, totalItemsSold));
            writer.write(String.format("STORES PROCESSED: %d (Success: %d, Failed: %d)\n",
                    storeFiles.size(), successfulStores, failedStores));
            writer.write("==================================================================\n");
            System.out.println("[SUCCESS] Summary report compiled safely.");
            return true;

        } catch (IOException e) {
            System.err.println("[LOG CRITICAL] Could not write to master summary file: " + e.getMessage());
            // Inspect suppressed exceptions if any
            for (Throwable suppressed : e.getSuppressed()) {
                System.err.println("  [SUPPRESSED EXCEPTION]: " + suppressed.getMessage());
            }
            return false;
        }
    }

    public static void main(String[] args) {
        System.out.println("============================================================");
        System.out.println("  TRY-WITH-RESOURCES RESOURCE MANAGEMENT EXPERIMENT         ");
        System.out.println("============================================================");

        List<String> failingFiles = List.of("store1.txt", "store2.txt", "store3_corrupt.txt", "store4.txt");

        openFileHandles = 0;
        totalFilesOpened = 0;
        totalFilesClosed = 0;

        generateReport(failingFiles, "summary_twr_output.txt");

        System.out.println("\n------------------------------------------------------------");
        System.out.println("End of TWR Run -> Opened: " + totalFilesOpened +
                ", Closed: " + totalFilesClosed +
                ", Active Leaked Handles: " + openFileHandles);
        System.out.println("============================================================");
    }
}
