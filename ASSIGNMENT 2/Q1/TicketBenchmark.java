package q1;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Q1: Railway e-Ticket Generation Benchmark
 * Investigates performance differences between String (+ operator),
 * StringBuilder, and StringBuffer under various ticket generation workloads.
 *
 * Student Name: Kattiripalli Dhanush
 * Register Number: 192311094
 */
public class TicketBenchmark {

    // Sample field template representing railway ticket fields
    private static final String[] FIELD_TEMPLATES = {
        "PNR: 8429103847", "TrainNo: 12626", "TrainName: Kerala Express",
        "From: NDLS", "To: TVC", "Date: 2026-11-20", "Class: 3A",
        "Quota: General", "Passenger1: K. Dhanush", "Age: 21", "Gender: M",
        "Coach: B4", "Berth: 37 (Side Lower)", "Status: Confirmed",
        "Passenger2: A. Sharma", "Age: 22", "Gender: F", "Coach: B4",
        "Berth: 38 (Side Upper)", "Status: Confirmed", "BaseFare: 3450.00",
        "ReservationFee: 40.00", "SuperfastCharge: 45.00", "GST: 176.75",
        "TotalFare: 3711.75", "PaymentMode: UPI", "TransactionID: TXN987654321",
        "BoardingPoint: NDLS", "ScheduledDeparture: 20:10", "ScheduledArrival: 18:00",
        "DistanceKm: 3036", "Catering: Included", "Bedroll: Yes",
        "IDProofType: Aadhaar", "IDNumber: XXXX-XXXX-1094", "Contact: 9876543210",
        "Email: passenger@railway.in", "IRCTC_Fee: 35.40", "Insurance: 0.90",
        "GateNo: 03", "Platform: 07", "SecurityChecked: Yes", "LuggagePieces: 2",
        "EmergencyContact: 9123456780", "Barcode: ||| | |||| || |", "QRToken: QRT789ABC",
        "TermsAccepted: Yes", "IssuedBy: CRIS_DELHI", "PrintedAt: 2026-11-17 09:15:00",
        "Message: Happy Journey Safe Travel"
    };

    public static List<String> getFields(int count) {
        List<String> list = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            list.add(FIELD_TEMPLATES[i % FIELD_TEMPLATES.length]);
        }
        return list;
    }

    // 1. String Concatenation using + operator in a loop
    public static String buildTicketWithString(List<String> fields) {
        String ticket = "";
        for (String field : fields) {
            ticket = ticket + field + "\n";
        }
        return ticket;
    }

    // 2. StringBuilder approach (unsynchronized, fast)
    public static String buildTicketWithStringBuilder(List<String> fields) {
        StringBuilder sb = new StringBuilder();
        for (String field : fields) {
            sb.append(field).append("\n");
        }
        return sb.toString();
    }

    // 3. StringBuffer approach (synchronized, thread-safe overhead)
    public static String buildTicketWithStringBuffer(List<String> fields) {
        StringBuffer sb = new StringBuffer();
        for (String field : fields) {
            sb.append(field).append("\n");
        }
        return sb.toString();
    }

    public static class BenchmarkResult {
        String workloadName;
        int numTickets;
        int fieldsPerTicket;
        String approach;
        double avgTimeMs;
        long estimatedIntermediateStrings;
        long memoryUsedBytes;

        public BenchmarkResult(String workloadName, int numTickets, int fieldsPerTicket,
                               String approach, double avgTimeMs,
                               long estimatedIntermediateStrings, long memoryUsedBytes) {
            this.workloadName = workloadName;
            this.numTickets = numTickets;
            this.fieldsPerTicket = fieldsPerTicket;
            this.approach = approach;
            this.avgTimeMs = avgTimeMs;
            this.estimatedIntermediateStrings = estimatedIntermediateStrings;
            this.memoryUsedBytes = memoryUsedBytes;
        }
    }

    public static void runWorkload(String workloadName, int numTickets, int fieldsPerTicket,
                                   int repetitions, List<BenchmarkResult> results) {
        List<String> fields = getFields(fieldsPerTicket);

        // Warm up JIT compiler
        for (int i = 0; i < 200; i++) {
            buildTicketWithStringBuilder(fields);
            buildTicketWithStringBuffer(fields);
            if (numTickets <= 100) {
                buildTicketWithString(fields);
            }
        }

        // --- Benchmark String (+) ---
        // For large runs, calculate estimated intermediate objects:
        // Each + in loop creates: (field + "\n") -> 1 String, then ticket + (...) -> 1 String.
        // For N fields, (2 * N) intermediate String objects per ticket.
        long stringIntermediate = (long) numTickets * fieldsPerTicket * 2;
        long builderIntermediate = (long) numTickets; // only the final .toString()
        long bufferIntermediate = (long) numTickets;  // only the final .toString()

        System.gc();
        try { Thread.sleep(100); } catch (InterruptedException ignored) {}

        long totalTimeString = 0;
        long memBeforeString = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        for (int r = 0; r < repetitions; r++) {
            long start = System.nanoTime();
            for (int t = 0; t < numTickets; t++) {
                String res = buildTicketWithString(fields);
            }
            long end = System.nanoTime();
            totalTimeString += (end - start);
        }
        long memAfterString = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        double avgTimeStringMs = (totalTimeString / (double) repetitions) / 1_000_000.0;
        long memUsedString = Math.max(0, memAfterString - memBeforeString);
        results.add(new BenchmarkResult(workloadName, numTickets, fieldsPerTicket, "String (+)", avgTimeStringMs, stringIntermediate, memUsedString));

        // --- Benchmark StringBuilder ---
        System.gc();
        try { Thread.sleep(100); } catch (InterruptedException ignored) {}

        long totalTimeBuilder = 0;
        long memBeforeBuilder = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        for (int r = 0; r < repetitions; r++) {
            long start = System.nanoTime();
            for (int t = 0; t < numTickets; t++) {
                String res = buildTicketWithStringBuilder(fields);
            }
            long end = System.nanoTime();
            totalTimeBuilder += (end - start);
        }
        long memAfterBuilder = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        double avgTimeBuilderMs = (totalTimeBuilder / (double) repetitions) / 1_000_000.0;
        long memUsedBuilder = Math.max(0, memAfterBuilder - memBeforeBuilder);
        results.add(new BenchmarkResult(workloadName, numTickets, fieldsPerTicket, "StringBuilder", avgTimeBuilderMs, builderIntermediate, memUsedBuilder));

        // --- Benchmark StringBuffer ---
        System.gc();
        try { Thread.sleep(100); } catch (InterruptedException ignored) {}

        long totalTimeBuffer = 0;
        long memBeforeBuffer = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        for (int r = 0; r < repetitions; r++) {
            long start = System.nanoTime();
            for (int t = 0; t < numTickets; t++) {
                String res = buildTicketWithStringBuffer(fields);
            }
            long end = System.nanoTime();
            totalTimeBuffer += (end - start);
        }
        long memAfterBuffer = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        double avgTimeBufferMs = (totalTimeBuffer / (double) repetitions) / 1_000_000.0;
        long memUsedBuffer = Math.max(0, memAfterBuffer - memBeforeBuffer);
        results.add(new BenchmarkResult(workloadName, numTickets, fieldsPerTicket, "StringBuffer", avgTimeBufferMs, bufferIntermediate, memUsedBuffer));

        System.out.printf("[Workload: %s] %d tickets x %d fields | Repetitions: %d%n", workloadName, numTickets, fieldsPerTicket, repetitions);
        System.out.printf("  String (+)     : %10.3f ms | Intermediates: %,12d | MemDelta: %,10d bytes%n", avgTimeStringMs, stringIntermediate, memUsedString);
        System.out.printf("  StringBuilder  : %10.3f ms | Intermediates: %,12d | MemDelta: %,10d bytes%n", avgTimeBuilderMs, builderIntermediate, memUsedBuilder);
        System.out.printf("  StringBuffer   : %10.3f ms | Intermediates: %,12d | MemDelta: %,10d bytes%n", avgTimeBufferMs, bufferIntermediate, memUsedBuffer);
        System.out.printf("  Speedup (Builder vs String) : %.2fx faster%n%n", (avgTimeStringMs > 0 && avgTimeBuilderMs > 0) ? avgTimeStringMs / avgTimeBuilderMs : 1.0);
    }

    public static void main(String[] args) {
        System.out.println("================================================================================");
        System.out.println(" Q1: BUILDING TEXT UNDER LOAD - STRING vs STRINGBUILDER vs STRINGBUFFER");
        System.out.println(" Student: Kattiripalli Dhanush (192311094)");
        System.out.println(" Environment: " + System.getProperty("os.name") + " | Java " + System.getProperty("java.version"));
        System.out.println("================================================================================\n");

        List<BenchmarkResult> results = new ArrayList<>();

        // Workload 1: One short ticket (1 ticket, 10 fields, 50 repetitions)
        runWorkload("Workload 1 (Single Short Ticket)", 1, 10, 50, results);

        // Workload 2: 10,000 tickets with 50 fields each (5 repetitions)
        runWorkload("Workload 2 (10k Tickets x 50 Fields)", 10000, 50, 5, results);

        // Workload 3: Batch ticket generation under load (50,000 tickets x 25 fields each, 3 repetitions)
        runWorkload("Workload 3 (50k Tickets x 25 Fields)", 50000, 25, 3, results);

        // Print Sample Ticket Output
        System.out.println("--- SAMPLE GENERATED TICKET (First 8 fields) ---");
        List<String> sampleFields = getFields(8);
        System.out.println(buildTicketWithStringBuilder(sampleFields));
        System.out.println("------------------------------------------------\n");

        // Write CSV Results
        String csvPath = "Java-Assignment-2/Q1/q1_results.csv";
        String resultsCsvPath = "Java-Assignment-2/Results/q1_benchmark.csv";
        writeCsv(csvPath, results);
        writeCsv(resultsCsvPath, results);
        System.out.println("Benchmark data saved successfully to:");
        System.out.println(" - " + csvPath);
        System.out.println(" - " + resultsCsvPath);
    }

    private static void writeCsv(String path, List<BenchmarkResult> results) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(path))) {
            pw.println("Workload,NumTickets,FieldsPerTicket,Approach,AvgTimeMs,IntermediateObjects,MemoryDeltaBytes");
            for (BenchmarkResult r : results) {
                pw.printf("\"%s\",%d,%d,\"%s\",%.4f,%d,%d%n",
                        r.workloadName, r.numTickets, r.fieldsPerTicket, r.approach,
                        r.avgTimeMs, r.estimatedIntermediateStrings, r.memoryUsedBytes);
            }
        } catch (IOException e) {
            System.err.println("Error writing CSV: " + e.getMessage());
        }
    }
}
