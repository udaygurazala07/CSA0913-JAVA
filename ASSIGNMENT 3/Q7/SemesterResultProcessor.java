package q7;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.LongAdder;

/**
 * Question 7: Concurrent, Fault-Tolerant Semester Result Processor
 * 
 * Features:
 * - Thread-safe concurrent processing of 10,000 student answer-script score files
 * - Individual task exception isolation (Callable returns ResultRecord, never halts pool)
 * - Shared data: ConcurrentHashMap<String, ResultRecord>, LongAdder counters, ConcurrentLinkedQueue for failure logs
 * - Scaling benchmark across 1, 2, 4, 8 threads
 * - Verification against a 20-file hand-checked control dataset
 * 
 * Student: Kattiripalli Dhanush (192311094)
 */
public class SemesterResultProcessor {

    public enum Status {
        SUCCESS,
        MISSING_FILE,
        CORRUPT_FORMAT,
        INVALID_SCORE_RANGE,
        EMPTY_FILE,
        UNEXPECTED_ERROR
    }

    public static class ResultRecord {
        public final String filename;
        public final String studentId;
        public final double averageScore;
        public final String letterGrade;
        public final Status status;
        public final String errorMessage;

        public ResultRecord(String filename, String studentId, double avg, String grade, Status status, String error) {
            this.filename = filename;
            this.studentId = studentId;
            this.averageScore = avg;
            this.letterGrade = grade;
            this.status = status;
            this.errorMessage = error;
        }

        public static ResultRecord failure(String filename, Status status, String error) {
            return new ResultRecord(filename, "UNKNOWN", 0.0, "N/A", status, error);
        }

        public static ResultRecord success(String filename, String studentId, double avg, String grade) {
            return new ResultRecord(filename, studentId, avg, grade, Status.SUCCESS, "None");
        }
    }

    /**
     * Individual file processing Callable task.
     * Enforces complete exception isolation: every fault is captured and converted to a ResultRecord.
     */
    public static class FileProcessorTask implements Callable<ResultRecord> {
        private final String filePath;

        public FileProcessorTask(String filePath) {
            this.filePath = filePath;
        }

        @Override
        public ResultRecord call() {
            File file = new File(filePath);

            // Check 1: File existence (Missing File Failure)
            if (!file.exists()) {
                return ResultRecord.failure(file.getName(), Status.MISSING_FILE, "File not found on filesystem: " + filePath);
            }

            // Check 2: File non-empty
            if (file.length() == 0) {
                return ResultRecord.failure(file.getName(), Status.EMPTY_FILE, "File is zero bytes: " + filePath);
            }

            // Process file using try-with-resources
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                String studentId = null;
                List<Integer> marks = new ArrayList<>();

                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    if (line.startsWith("STUDENT_ID:")) {
                        studentId = line.substring("STUDENT_ID:".length()).trim();
                    } else if (line.startsWith("MARKS:")) {
                        String marksStr = line.substring("MARKS:".length()).trim();
                        String[] tokens = marksStr.split(",");
                        for (String token : tokens) {
                            try {
                                int score = Integer.parseInt(token.trim());
                                if (score < 0 || score > 100) {
                                    return ResultRecord.failure(file.getName(), Status.INVALID_SCORE_RANGE,
                                            "Score " + score + " outside legal range [0, 100]");
                                }
                                marks.add(score);
                            } catch (NumberFormatException nfe) {
                                return ResultRecord.failure(file.getName(), Status.CORRUPT_FORMAT,
                                        "Non-numeric score token encountered: '" + token.trim() + "'");
                            }
                        }
                    }
                }

                if (studentId == null || marks.isEmpty()) {
                    return ResultRecord.failure(file.getName(), Status.CORRUPT_FORMAT, "Missing STUDENT_ID or MARKS header");
                }

                // Compute student average and grade
                double sum = 0;
                for (int m : marks) sum += m;
                double avg = sum / marks.size();
                String grade = (avg >= 90) ? "S" : (avg >= 80) ? "A" : (avg >= 70) ? "B" : (avg >= 60) ? "C" : (avg >= 50) ? "D" : "F";

                return ResultRecord.success(file.getName(), studentId, avg, grade);

            } catch (IOException e) {
                return ResultRecord.failure(file.getName(), Status.UNEXPECTED_ERROR, "I/O error reading file: " + e.getMessage());
            } catch (Exception e) {
                return ResultRecord.failure(file.getName(), Status.UNEXPECTED_ERROR, "Unexpected runtime error: " + e.getMessage());
            }
        }
    }

    public static class BatchSummary {
        public final int totalSubmitted;
        public final int successCount;
        public final int failureCount;
        public final int missingCount;
        public final int corruptCount;
        public final long executionTimeMs;
        public final double throughputFilesPerSec;

        public BatchSummary(int total, int success, int failure, int missing, int corrupt, long timeMs) {
            this.totalSubmitted = total;
            this.successCount = success;
            this.failureCount = failure;
            this.missingCount = missing;
            this.corruptCount = corrupt;
            this.executionTimeMs = timeMs;
            this.throughputFilesPerSec = (timeMs > 0) ? (total * 1000.0 / timeMs) : 0.0;
        }
    }

    /**
     * Executes batch processing using ExecutorService with specified thread count.
     */
    public static BatchSummary processWorkload(List<String> filePaths, int numThreads) throws InterruptedException {
        ExecutorService pool = Executors.newFixedThreadPool(numThreads);
        List<Future<ResultRecord>> futures = new ArrayList<>(filePaths.size());

        ConcurrentHashMap<String, ResultRecord> sharedResults = new ConcurrentHashMap<>(filePaths.size());
        ConcurrentLinkedQueue<ResultRecord> failureLog = new ConcurrentLinkedQueue<>();
        LongAdder successAdder = new LongAdder();
        LongAdder missingAdder = new LongAdder();
        LongAdder corruptAdder = new LongAdder();

        long startTime = System.currentTimeMillis();

        // Submit all Callable tasks to the ExecutorService
        for (String path : filePaths) {
            futures.add(pool.submit(new FileProcessorTask(path)));
        }

        // Collect Future results
        for (Future<ResultRecord> future : futures) {
            try {
                ResultRecord record = future.get(); // waits for individual file result
                if (record.status == Status.SUCCESS) {
                    successAdder.increment();
                    sharedResults.put(record.studentId, record);
                } else {
                    failureLog.add(record);
                    if (record.status == Status.MISSING_FILE) {
                        missingAdder.increment();
                    } else {
                        corruptAdder.increment();
                    }
                }
            } catch (ExecutionException e) {
                System.err.println("Task execution exception: " + e.getCause());
            }
        }

        pool.shutdown();
        long totalTime = System.currentTimeMillis() - startTime;

        int successes = successAdder.intValue();
        int missing = missingAdder.intValue();
        int corrupt = corruptAdder.intValue();
        int totalFailures = missing + corrupt;

        return new BatchSummary(filePaths.size(), successes, totalFailures, missing, corrupt, totalTime);
    }

    public static void main(String[] args) throws Exception {
        System.out.println("============================================================");
        System.out.println("   SEMESTER RESULT PROCESSOR - CONCURRENT & FAULT-TOLERANT   ");
        System.out.println("   Student: Kattiripalli Dhanush | Register No: 192311094   ");
        System.out.println("============================================================");

        // 1. Generate Datasets
        DatasetGenerator.generateDataset("data_hand_checked", 20, 3, 3);
        DatasetGenerator.generateDataset("data_10k_workload", 10000, 300, 200);

        // 2. Prepare 20 Hand-Checked Files List
        List<String> handCheckedPaths = new ArrayList<>();
        for (int i = 1; i <= 20; i++) {
            handCheckedPaths.add("data_hand_checked/student_" + String.format("%05d", i) + ".txt");
        }

        System.out.println("\n--- STAGE 3: Hand-Checked Control Dataset Verification (20 Files) ---");
        System.out.println("Ground Truth: 14 Valid Files, 3 Corrupt Files, 3 Missing Files (Total: 20)");
        BatchSummary hcSummary = processWorkload(handCheckedPaths, 4);
        System.out.printf("Hand-Checked Summary: Processed: %d | Success: %d | Missing: %d | Corrupt: %d | Total Failures: %d\n",
                hcSummary.totalSubmitted, hcSummary.successCount, hcSummary.missingCount, hcSummary.corruptCount, hcSummary.failureCount);

        boolean verified = (hcSummary.totalSubmitted == 20 && hcSummary.successCount == 14 && hcSummary.missingCount == 3 && hcSummary.corruptCount == 3);
        System.out.println("Verification Status: " + (verified ? "[PASS - EXACT MATCH TO GROUND TRUTH]" : "[FAIL]"));

        // 3. Prepare Full 10,000 Files List
        List<String> workloadPaths = new ArrayList<>();
        for (int i = 1; i <= 10000; i++) {
            workloadPaths.add("data_10k_workload/student_" + String.format("%05d", i) + ".txt");
        }

        // 4. Measure Scaling across 1, 2, 4, and 8 Threads
        System.out.println("\n--- STAGE 3: Thread Pool Scaling Benchmark (10,000 Files) ---");
        int[] threadConfigs = {1, 2, 4, 8};
        StringBuilder csvData = new StringBuilder();
        csvData.append("ThreadPoolSize,TotalFiles,SuccessCount,FailureCount,MissingFiles,CorruptFiles,ExecutionTimeMs,ThroughputFilesPerSec,SpeedupRatio\n");

        double baselineTime = 0.0;
        for (int threads : threadConfigs) {
            System.out.printf("\nExecuting with %d worker thread(s)...\n", threads);
            BatchSummary summary = processWorkload(workloadPaths, threads);

            if (threads == 1) {
                baselineTime = summary.executionTimeMs;
            }
            double speedup = (summary.executionTimeMs > 0) ? (baselineTime / summary.executionTimeMs) : 1.0;

            System.out.printf("  Threads: %d | Time: %,d ms | Throughput: %,.1f files/s | Speedup: %.2fx\n",
                    threads, summary.executionTimeMs, summary.throughputFilesPerSec, speedup);
            System.out.printf("  Summary: Successes: %d, Failures: %d (Missing: %d, Corrupt: %d)\n",
                    summary.successCount, summary.failureCount, summary.missingCount, summary.corruptCount);

            csvData.append(String.format("%d,%d,%d,%d,%d,%d,%d,%.2f,%.2f\n",
                    threads, summary.totalSubmitted, summary.successCount, summary.failureCount,
                    summary.missingCount, summary.corruptCount, summary.executionTimeMs,
                    summary.throughputFilesPerSec, speedup));
        }

        // Export CSV
        String csvPath = "q7_thread_scaling.csv";
        try (FileWriter fw = new FileWriter(csvPath)) {
            fw.write(csvData.toString());
            System.out.println("\n[INFO] Scaling benchmark results saved to: " + csvPath);
        }

        System.out.println("============================================================");
    }
}
