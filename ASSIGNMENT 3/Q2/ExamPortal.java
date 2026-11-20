package q2;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Question 2: Three-Tier Online Examination Portal
 * 
 * Layers:
 * 1. Storage Layer (StorageLayer)
 * 2. Service Layer (ExamService)
 * 3. Controller Layer (ExamController)
 * 
 * Demonstrates:
 * - Checked vs Unchecked custom exceptions
 * - throw and throws propagation across layers
 * - Exception chaining with Throwable cause
 * - Information loss when cause is discarded
 * 
 * Student: Kattiripalli Dhanush (192311094)
 */
public class ExamPortal {

    // ==========================================
    // CUSTOM EXCEPTION HIERARCHY
    // ==========================================

    // Base Checked Exception for Portal Operations
    public static class PortalException extends Exception {
        public PortalException(String message) { super(message); }
        public PortalException(String message, Throwable cause) { super(message, cause); }
    }

    // Checked Exception: Window has elapsed
    public static class ExamWindowClosedException extends PortalException {
        public ExamWindowClosedException(String message) { super(message); }
    }

    // Checked Exception: Storage I/O failure (wraps low-level IOException)
    public static class FileStorageException extends PortalException {
        public FileStorageException(String message, Throwable cause) { super(message, cause); }
    }

    // Unchecked Exception: Illegal state / unauthenticated student (extends RuntimeException)
    public static class StudentNotRegisteredException extends RuntimeException {
        public StudentNotRegisteredException(String message) { super(message); }
    }

    // ==========================================
    // 1. STORAGE LAYER
    // ==========================================
    public static class StorageLayer {
        private static final long MAX_STORAGE_CAPACITY_KB = 500;

        /**
         * Simulates low-level file write to disk.
         * Throws raw IOException if storage limits are breached, then chains into FileStorageException.
         */
        public void writeAnswerScript(String filename, long sizeKb, boolean preserveCause) throws FileStorageException {
            try {
                if (sizeKb > MAX_STORAGE_CAPACITY_KB) {
                    throw new IOException("POSIX write() failed: Disk quota exceeded on sector /mnt/exams");
                }
                System.out.println("[StorageLayer] Successfully committed " + filename + " (" + sizeKb + " KB) to disk.");
            } catch (IOException rawIo) {
                if (preserveCause) {
                    // Exception Chaining: low-level cause preserved
                    throw new FileStorageException("Storage error writing script: " + filename, rawIo);
                } else {
                    // Anti-Pattern: Cause discarded! Information lost!
                    throw new FileStorageException("Storage error writing script: " + filename, null);
                }
            }
        }
    }

    // ==========================================
    // 2. SERVICE LAYER
    // ==========================================
    public static class ExamService {
        private final StorageLayer storageLayer = new StorageLayer();
        private final Set<String> registeredStudents = new HashSet<>();
        private final LocalDateTime examDeadline;

        public ExamService(LocalDateTime deadline) {
            this.examDeadline = deadline;
            registeredStudents.add("192311094"); // Registered student Kattiripalli Dhanush
            registeredStudents.add("192311095");
        }

        /**
         * Validates business rules: student registration, exam submission window, and storage.
         * Throws checked and unchecked domain exceptions up to the controller.
         */
        public void processSubmission(String studentId, String filename, long sizeKb, LocalDateTime submissionTime, boolean preserveCause)
                throws ExamWindowClosedException, FileStorageException {

            // Rule 1: Registration check (Unchecked Exception - precondition violation)
            if (!registeredStudents.contains(studentId)) {
                throw new StudentNotRegisteredException("Access Denied: Student ID " + studentId + " is not registered for this course.");
            }

            // Rule 2: Submission window check (Checked Exception - temporal condition)
            if (submissionTime.isAfter(examDeadline)) {
                throw new ExamWindowClosedException("Submission Rejected: Deadline was " + examDeadline + ", submission received at " + submissionTime);
            }

            // Rule 3: Persist through storage layer (Checked Exception chained)
            storageLayer.writeAnswerScript(filename, sizeKb, preserveCause);
        }
    }

    // ==========================================
    // 3. CONTROLLER LAYER
    // ==========================================
    public static class ExamController {
        private final ExamService examService;

        public ExamController(ExamService service) {
            this.examService = service;
        }

        /**
         * HTTP endpoint handler equivalent. Catches domain exceptions and prepares student response.
         */
        public String handleSubmissionRequest(String studentId, String filename, long sizeKb, LocalDateTime time, boolean preserveCause) {
            System.out.println("\n------------------------------------------------------------");
            System.out.println("[Controller] Received submission request from Student: " + studentId + " | File: " + filename);

            try {
                examService.processSubmission(studentId, filename, sizeKb, time, preserveCause);
                return "200 OK: Submission successful for " + filename;

            } catch (StudentNotRegisteredException snre) {
                // Handled unchecked domain exception
                System.err.println("[Controller - 403 Forbidden] " + snre.getMessage());
                return "403 Forbidden: " + snre.getMessage();

            } catch (ExamWindowClosedException ewce) {
                // Handled checked window exception
                System.err.println("[Controller - 400 Bad Request] " + ewce.getMessage());
                return "400 Window Closed: " + ewce.getMessage();

            } catch (FileStorageException fse) {
                // Handled checked storage exception with chain analysis
                System.err.println("[Controller - 500 Internal Error] " + fse.getMessage());
                if (fse.getCause() != null) {
                    System.err.println("  -> Original Root Cause: " + fse.getCause().getClass().getName() + ": " + fse.getCause().getMessage());
                } else {
                    System.err.println("  -> Original Root Cause: [LOST / NULL - Exception thrown without cause!]");
                }
                return "500 Storage Failed: " + fse.getMessage();

            } catch (Exception e) {
                System.err.println("[Controller - 500 Unknown] Unexpected server error: " + e.getMessage());
                return "500 Internal Server Error";
            }
        }
    }

    // ==========================================
    // MAIN BENCHMARK & TEST HARNESS
    // ==========================================
    public static void main(String[] args) {
        System.out.println("============================================================");
        System.out.println("   ONLINE EXAMINATION PORTAL - EXCEPTION PROPAGATION DEMO   ");
        System.out.println("   Student: Kattiripalli Dhanush | Register No: 192311094   ");
        System.out.println("============================================================");

        LocalDateTime deadline = LocalDateTime.of(2026, 11, 17, 10, 0, 0);
        ExamService service = new ExamService(deadline);
        ExamController controller = new ExamController(service);

        // Test Scenarios
        // 1. Success
        String r1 = controller.handleSubmissionRequest("192311094", "answer01.pdf", 250,
                LocalDateTime.of(2026, 11, 17, 9, 30, 0), true);

        // 2. Unregistered Student (Unchecked)
        String r2 = controller.handleSubmissionRequest("192399999", "answer_guest.pdf", 250,
                LocalDateTime.of(2026, 11, 17, 9, 30, 0), true);

        // 3. Late Submission Window Closed (Checked)
        String r3 = controller.handleSubmissionRequest("192311094", "answer_late.pdf", 250,
                LocalDateTime.of(2026, 11, 17, 10, 15, 0), true);

        // 4. File Exceeds Limit With Cause Preserved (Checked Chained)
        String r4 = controller.handleSubmissionRequest("192311094", "answer_oversize_chained.pdf", 850,
                LocalDateTime.of(2026, 11, 17, 9, 45, 0), true);

        // 5. File Exceeds Limit With Cause OMITTED (Information Loss Demonstration)
        String r5 = controller.handleSubmissionRequest("192311094", "answer_oversize_lost.pdf", 850,
                LocalDateTime.of(2026, 11, 17, 9, 45, 0), false);

        // Export test table to CSV
        try (FileWriter fw = new FileWriter("q2_results.csv")) {
            fw.write("Scenario,StudentId,Condition,ExceptionThrown,ExceptionCategory,RootCausePreserved,ResponseStatus\n");
            fw.write("\"1. Valid Submission\",\"192311094\",\"Within window, registered, 250KB\",\"None\",\"N/A\",\"N/A\",\"" + r1 + "\"\n");
            fw.write("\"2. Unregistered Student\",\"192399999\",\"Student ID not in DB\",\"StudentNotRegisteredException\",\"Unchecked (RuntimeException)\",\"No Cause Needed\",\"" + r2 + "\"\n");
            fw.write("\"3. Window Elapsed\",\"192311094\",\"Submission after 10:00 AM\",\"ExamWindowClosedException\",\"Checked (PortalException)\",\"No Cause Needed\",\"" + r3 + "\"\n");
            fw.write("\"4. Storage Quota Exceeded (Chained)\",\"192311094\",\"File 850KB > 500KB\",\"FileStorageException\",\"Checked (PortalException)\",\"Yes (IOException preserved)\",\"" + r4 + "\"\n");
            fw.write("\"5. Storage Quota Exceeded (Lost Cause)\",\"192311094\",\"File 850KB > 500KB\",\"FileStorageException\",\"Checked (PortalException)\",\"No (cause=null, lost)\",\"" + r5 + "\"\n");
            System.out.println("\n[INFO] Test table saved to q2_results.csv");
        } catch (IOException e) {
            System.err.println("Failed to write CSV: " + e.getMessage());
        }

        // Generate full chained stack trace demonstration
        System.out.println("\n============================================================");
        System.out.println("     DEMONSTRATION OF FULL CHAINED STACK TRACE EVIDENCE     ");
        System.out.println("============================================================");
        try {
            StorageLayer st = new StorageLayer();
            st.writeAnswerScript("corrupt_test.pdf", 999, true);
        } catch (FileStorageException fse) {
            StringWriter sw = new StringWriter();
            fse.printStackTrace(new PrintWriter(sw));
            System.out.println(sw.toString());
        }
    }
}
