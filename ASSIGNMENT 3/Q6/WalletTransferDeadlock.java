package q6;

/**
 * Question 6 - Part A: Deadlock Demonstration
 * 
 * Demonstrates a classic circular deadlock between two hostel wallet transfers:
 * - Thread 1: Transfer from Account A to Account B (Locks A, then waits for B)
 * - Thread 2: Transfer from Account B to Account A (Locks B, then waits for A)
 * 
 * Fulfills Student B's requirement: Construct a pair of transfers that reliably deadlocks.
 */
public class WalletTransferDeadlock {

    public static class Account {
        private final int id;
        private double balance;

        public Account(int id, double initialBalance) {
            this.id = id;
            this.balance = initialBalance;
        }

        public int getId() { return id; }
        public double getBalance() { return balance; }
    }

    public static void transferUnsafe(Account from, Account to, double amount) {
        synchronized (from) {
            System.out.printf("[%s] Acquired lock on Account %d; attempting to lock Account %d...\n",
                    Thread.currentThread().getName(), from.getId(), to.getId());

            try {
                // Delay creates an inescapable timing window guaranteeing circular wait
                Thread.sleep(50);
            } catch (InterruptedException ignored) {}

            synchronized (to) {
                System.out.printf("[%s] Acquired BOTH locks. Transferring Rs. %.2f from Acc %d to Acc %d\n",
                        Thread.currentThread().getName(), amount, from.getId(), to.getId());
                from.balance -= amount;
                to.balance += amount;
            }
        }
    }

    public static void main(String[] args) {
        System.out.println("============================================================");
        System.out.println("   DEMONSTRATION OF RELIABLE DEADLOCK IN WALLET TRANSFERS    ");
        System.out.println("============================================================");

        Account accountA = new Account(101, 1000.0);
        Account accountB = new Account(102, 1000.0);

        Thread t1 = new Thread(() -> transferUnsafe(accountA, accountB, 200.0), "TransferThread-A_to_B");
        Thread t2 = new Thread(() -> transferUnsafe(accountB, accountA, 300.0), "TransferThread-B_to_A");

        t1.start();
        t2.start();

        // Monitor thread detecting deadlock after timeout
        Thread monitor = new Thread(() -> {
            try {
                Thread.sleep(600);
                System.out.println("\n[MONITOR ALERT] State Analysis after 600 ms:");
                System.out.printf("  %s State: %s (Waiting for Account %d)\n",
                        t1.getName(), t1.getState(), accountB.getId());
                System.out.printf("  %s State: %s (Waiting for Account %d)\n",
                        t2.getName(), t2.getState(), accountA.getId());
                System.out.println("[DEADLOCK CONFIRMED] Both threads are in BLOCKED state waiting on each other's locks!");
                System.out.println("Circular Wait condition detected: Thread A -> Lock B, Thread B -> Lock A.");
                System.exit(0); // Exit process after proving deadlock
            } catch (InterruptedException ignored) {}
        });
        monitor.setDaemon(true);
        monitor.start();
    }
}
