package q6;

/**
 * Question 6 - Part A: Deadlock Prevention via Consistent Lock Ordering
 * 
 * Breaks the Circular Wait Coffman condition by enforcing a strict global lock hierarchy:
 * Always lock the account with the smaller Account ID first, then the larger Account ID.
 * 
 * Fulfills Student B's requirement: Demonstrate that corrected version works reliably.
 */
public class WalletTransferSafe {

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

    public static void transferSafe(Account from, Account to, double amount) {
        // Enforce global lock ordering based on Account ID
        Account firstLock = from.getId() < to.getId() ? from : to;
        Account secondLock = from.getId() < to.getId() ? to : from;

        synchronized (firstLock) {
            System.out.printf("[%s] Acquired primary lock on Account %d\n",
                    Thread.currentThread().getName(), firstLock.getId());

            try {
                Thread.sleep(10);
            } catch (InterruptedException ignored) {}

            synchronized (secondLock) {
                System.out.printf("[%s] Acquired secondary lock on Account %d. Executing transfer Rs. %.2f (From %d -> To %d)\n",
                        Thread.currentThread().getName(), secondLock.getId(), amount, from.getId(), to.getId());
                from.balance -= amount;
                to.balance += amount;
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("============================================================");
        System.out.println("  DEADLOCK-FREE TRANSFERS VIA CONSISTENT LOCK ORDERING      ");
        System.out.println("============================================================");

        Account accountA = new Account(101, 1000.0);
        Account accountB = new Account(102, 1000.0);

        // Simultaneous competing opposite transfers
        Thread t1 = new Thread(() -> transferSafe(accountA, accountB, 250.0), "SafeTransfer-A_to_B");
        Thread t2 = new Thread(() -> transferSafe(accountB, accountA, 150.0), "SafeTransfer-B_to_A");

        t1.start();
        t2.start();

        t1.join();
        t2.join();

        System.out.println("\n[SUCCESS] Both transfers completed without deadlock!");
        System.out.printf("Account A (ID: 101) Final Balance: Rs. %.2f (Expected: Rs. 900.00)\n", accountA.getBalance());
        System.out.printf("Account B (ID: 102) Final Balance: Rs. %.2f (Expected: Rs. 1100.00)\n", accountB.getBalance());
        System.out.println("============================================================");
    }
}
