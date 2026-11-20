package q3;

/**
 * Demonstrates how try-with-resources attaches secondary closing exceptions
 * as Suppressed Exceptions to the primary exception via Throwable.addSuppressed().
 * 
 * In traditional manual try-finally, if finally throws an exception,
 * the primary business exception from the try block is SILENTLY LOST (masked)!
 * Try-with-resources completely eliminates this bug.
 */
public class SuppressedExceptionDemo {

    static class BadResource implements AutoCloseable {
        private final String name;
        public BadResource(String name) { this.name = name; }

        public void doWork() throws Exception {
            throw new Exception("Primary business logic failed in " + name);
        }

        @Override
        public void close() throws Exception {
            throw new Exception("Secondary I/O close() failed for " + name);
        }
    }

    public static void main(String[] args) {
        System.out.println("============================================================");
        System.out.println("     SUPPRESSED EXCEPTION DEMONSTRATION (JAVA 7+ TWR)       ");
        System.out.println("============================================================");

        try (BadResource res = new BadResource("StoreDatabaseConnection")) {
            res.doWork();
        } catch (Exception e) {
            System.out.println("[PRIMARY EXCEPTION CAUGHT]: " + e.getMessage());
            Throwable[] suppressed = e.getSuppressed();
            System.out.println("Number of Suppressed Exceptions: " + suppressed.length);
            for (int i = 0; i < suppressed.length; i++) {
                System.out.println("  -> Suppressed [" + (i + 1) + "]: " + suppressed[i].getMessage());
            }
        }
        System.out.println("============================================================");
    }
}
