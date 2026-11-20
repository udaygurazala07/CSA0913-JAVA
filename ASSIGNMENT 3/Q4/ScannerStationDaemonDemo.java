package q4;

/**
 * Question 4: Demonstration of Daemon Thread Behavior
 * 
 * A daemon thread is a service provider thread.
 * When all user (non-daemon) threads terminate, the JVM immediately exits,
 * killing all daemon threads abruptly even if their run() methods are halfway done.
 * 
 * Student: Kattiripalli Dhanush (192311094)
 */
public class ScannerStationDaemonDemo {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("============================================================");
        System.out.println("      DEMONSTRATION OF DAEMON THREAD TERMINATION            ");
        System.out.println("============================================================");

        Thread backgroundLogger = new Thread(() -> {
            int tick = 1;
            while (true) {
                System.out.printf("  [DAEMON LOGGER] Periodic heartbeat tick #%d (Alive & Logging)...\n", tick++);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    System.out.println("  [DAEMON LOGGER] Interrupted.");
                    break;
                }
            }
        }, "BackgroundAuditDaemon");

        // Mark as daemon thread
        backgroundLogger.setDaemon(true);
        System.out.println("Thread set to Daemon: " + backgroundLogger.isDaemon());
        backgroundLogger.start();

        // Main thread does some brief work then exits
        System.out.println("[MAIN THREAD] Doing 350 ms of scanning work...");
        Thread.sleep(350);
        System.out.println("[MAIN THREAD] Main thread has completed and will now exit.");
        System.out.println("[JVM NOTICE] With no non-daemon threads remaining, JVM halts instantly.");
        System.out.println("============================================================");
    }
}
