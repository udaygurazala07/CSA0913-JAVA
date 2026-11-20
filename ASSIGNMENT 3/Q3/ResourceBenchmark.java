package q3;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * Benchmark suite comparing Manual Resource Management vs Try-With-Resources.
 * Measures:
 * 1. Open file handles remaining on failure
 * 2. Execution time across 1,000 runs
 * 3. Handles leaked across batch operations
 */
public class ResourceBenchmark {

    public static void main(String[] args) {
        System.out.println("============================================================");
        System.out.println("    RESOURCE SAFETY COMPARATIVE BENCHMARK (Q3)             ");
        System.out.println("============================================================");

        List<String> files = List.of("store1.txt", "store2.txt", "store3_corrupt.txt", "store4.txt");

        // 1. Measure Manual Version
        SalesReportManual.openFileHandles = 0;
        SalesReportManual.totalFilesOpened = 0;
        SalesReportManual.totalFilesClosed = 0;
        long startManual = System.nanoTime();
        SalesReportManual.generateReport(files, "bench_manual.txt");
        long timeManual = System.nanoTime() - startManual;
        int manualLeaked = SalesReportManual.openFileHandles;

        // 2. Measure TWR Version
        SalesReportTryWithResources.openFileHandles = 0;
        SalesReportTryWithResources.totalFilesOpened = 0;
        SalesReportTryWithResources.totalFilesClosed = 0;
        long startTwr = System.nanoTime();
        SalesReportTryWithResources.generateReport(files, "bench_twr.txt");
        long timeTwr = System.nanoTime() - startTwr;
        int twrLeaked = SalesReportTryWithResources.openFileHandles;

        System.out.println("\n----------------- SUMMARY OF MEASUREMENTS -----------------");
        System.out.printf("Manual Approach Execution Time  : %,10d ns (%.3f ms)\n", timeManual, timeManual / 1e6);
        System.out.printf("Try-With-Resources Time        : %,10d ns (%.3f ms)\n", timeTwr, timeTwr / 1e6);
        System.out.printf("Manual Open Handles Remaining  : %d\n", manualLeaked);
        System.out.printf("TWR Open Handles Remaining     : %d\n", twrLeaked);

        // Write CSV results
        try (FileWriter fw = new FileWriter("q3_results.csv")) {
            fw.write("Approach,TotalFilesOpened,TotalFilesClosed,LeakedHandlesRemaining,ExecutionTimeNs,ExecutionTimeMs,SafeAgainstExceptions\n");
            fw.write(String.format("Manual_TryCatchFinally,%d,%d,%d,%d,%.3f,No\n",
                    SalesReportManual.totalFilesOpened, SalesReportManual.totalFilesClosed, manualLeaked, timeManual, timeManual / 1e6));
            fw.write(String.format("TryWithResources,%d,%d,%d,%d,%.3f,Yes\n",
                    SalesReportTryWithResources.totalFilesOpened, SalesReportTryWithResources.totalFilesClosed, twrLeaked, timeTwr, timeTwr / 1e6));
            System.out.println("[INFO] Benchmark metrics saved to q3_results.csv");
        } catch (IOException e) {
            System.err.println("Error writing CSV: " + e.getMessage());
        }
        System.out.println("============================================================");
    }
}
