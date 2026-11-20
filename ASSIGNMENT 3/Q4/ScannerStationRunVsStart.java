package q4;

/**
 * Question 4: Demonstration of run() vs start()
 * 
 * Calling run(): Executes synchronously on the CALLING thread (main). No new thread is spawned!
 * Calling start(): Requests JVM to allocate native thread resources and invokes run() asynchronously on a NEW thread.
 * 
 * Student: Kattiripalli Dhanush (192311094)
 */
public class ScannerStationRunVsStart {

    static class WorkerThread extends Thread {
        public WorkerThread(String name) { super(name); }

        @Override
        public void run() {
            System.out.printf("Executing inside run() method. Active Thread: '%s' (Thread ID: %d)\n",
                    Thread.currentThread().getName(), Thread.currentThread().threadId());
        }
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("============================================================");
        System.out.println("        EVIDENCE: CALLING run() VS CALLING start()          ");
        System.out.println("============================================================");
        System.out.printf("Current Main Thread: '%s' (Thread ID: %d)\n\n",
                Thread.currentThread().getName(), Thread.currentThread().threadId());

        // Case 1: Calling run() directly
        System.out.println("--- Case 1: Invoking worker.run() directly ---");
        WorkerThread worker1 = new WorkerThread("Dedicated-Worker-1");
        worker1.run(); // DIRECT INVOCATION!
        System.out.println("Notice: Thread name did NOT change to Dedicated-Worker-1; it ran on 'main' synchronously!\n");

        // Case 2: Calling start()
        System.out.println("--- Case 2: Invoking worker.start() ---");
        WorkerThread worker2 = new WorkerThread("Dedicated-Worker-2");
        worker2.start(); // ASYNCHRONOUS SPAWN
        worker2.join();
        System.out.println("Notice: Thread name was 'Dedicated-Worker-2', proving a separate thread was spawned!");
        System.out.println("============================================================");
    }
}
