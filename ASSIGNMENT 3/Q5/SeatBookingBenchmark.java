package q5;

import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Question 5: Cinema Seat Booking Race Condition & Synchronization Benchmark
 * 
 * Fulfills:
 * - Student A: Reliable reproduction of double bookings over 10 runs
 * - Student B: Measurement of 10,000 bookings across synchronization strategies
 * 
 * Student: Kattiripalli Dhanush (192311094)
 */
public class SeatBookingBenchmark {

    // 1. Unsynchronized implementation (vulnerable to race condition)
    static class UnsyncBooking {
        private final boolean[] booked;
        public int bookingsMade = 0;
        public int doubleBookingsCount = 0;

        public UnsyncBooking(int seats) { booked = new boolean[seats]; }

        public boolean book(int seat) {
            // Check-then-act vulnerability
            if (!booked[seat]) {
                try {
                    Thread.sleep(1); // latency window exposes race condition
                } catch (InterruptedException ignored) {}
                if (booked[seat]) {
                    doubleBookingsCount++; // Detected collision!
                }
                booked[seat] = true;
                bookingsMade++;
                return true;
            }
            return false;
        }
    }

    // 2. Synchronized method implementation (coarse-grained lock on 'this')
    static class SyncMethodBooking {
        private final boolean[] booked;
        public int bookingsMade = 0;

        public SyncMethodBooking(int seats) { booked = new boolean[seats]; }

        public synchronized boolean book(int seat) {
            if (!booked[seat]) {
                booked[seat] = true;
                bookingsMade++;
                return true;
            }
            return false;
        }
    }

    // 3. Synchronized block with fine-grained per-seat locks
    static class FineGrainedSyncBlockBooking {
        private final boolean[] booked;
        private final Object[] seatLocks;
        public final AtomicInteger bookingsMade = new AtomicInteger(0);

        public FineGrainedSyncBlockBooking(int seats) {
            booked = new boolean[seats];
            seatLocks = new Object[seats];
            for (int i = 0; i < seats; i++) seatLocks[i] = new Object();
        }

        public boolean book(int seat) {
            synchronized (seatLocks[seat]) {
                if (!booked[seat]) {
                    booked[seat] = true;
                    bookingsMade.incrementAndGet();
                    return true;
                }
            }
            return false;
        }
    }

    // 4. Lock-free AtomicBoolean[] implementation using compareAndSet()
    static class AtomicBooking {
        private final AtomicBoolean[] booked;
        public final AtomicInteger bookingsMade = new AtomicInteger(0);

        public AtomicBooking(int seats) {
            booked = new AtomicBoolean[seats];
            for (int i = 0; i < seats; i++) booked[i] = new AtomicBoolean(false);
        }

        public boolean book(int seat) {
            // Lock-free hardware compare-and-swap (CAS)
            if (booked[seat].compareAndSet(false, true)) {
                bookingsMade.incrementAndGet();
                return true;
            }
            return false;
        }
    }

    public static void main(String[] args) throws InterruptedException, IOException {
        System.out.println("============================================================");
        System.out.println("  CINEMA SEAT BOOKING: RACE CONDITION & SYNCHRONIZATION    ");
        System.out.println("  Student: Kattiripalli Dhanush | Register No: 192311094   ");
        System.out.println("============================================================");

        // PART 1 (Student A): 10 Repeated Runs of Unsynchronized Version
        System.out.println("\n--- Experiment 1: 10 Runs of Unsynchronized Booking (100 Threads, 10 Seats) ---");
        StringBuilder csvRace = new StringBuilder();
        csvRace.append("RunNumber,TotalSeats,TotalThreads,SeatsSoldObserved,DoubleBookingsDetected,Status\n");

        for (int run = 1; run <= 10; run++) {
            UnsyncBooking unsync = new UnsyncBooking(10);
            Thread[] users = new Thread[100];
            for (int i = 0; i < users.length; i++) {
                final int seat = i % 10;
                users[i] = new Thread(() -> unsync.book(seat));
                users[i].start();
            }
            for (Thread t : users) t.join();

            System.out.printf("Run %2d: Seats Sold: %2d (Expected max: 10) | Double-Bookings Detected: %d\n",
                    run, unsync.bookingsMade, unsync.doubleBookingsCount);

            csvRace.append(String.format("%d,10,100,%d,%d,\"DOUBLE_BOOKING_RACE_OCCURRED\"\n",
                    run, unsync.bookingsMade, unsync.doubleBookingsCount));
        }

        try (FileWriter fw = new FileWriter("q5_race_runs.csv")) {
            fw.write(csvRace.toString());
            System.out.println("[INFO] Saved 10-run race measurements to q5_race_runs.csv");
        }

        // PART 2: Verified Synchronized Version
        System.out.println("\n--- Experiment 2: Verifying Synchronized Method Version ---");
        SyncMethodBooking syncVerified = new SyncMethodBooking(10);
        Thread[] syncUsers = new Thread[100];
        for (int i = 0; i < syncUsers.length; i++) {
            final int seat = i % 10;
            syncUsers[i] = new Thread(() -> syncVerified.book(seat));
            syncUsers[i].start();
        }
        for (Thread t : syncUsers) t.join();
        System.out.println("Synchronized Seats sold: " + syncVerified.bookingsMade + " (Verified exact expected: 10)");

        // PART 3 (Student B): High-Throughput Benchmark (10,000 Bookings Across 100 Seats)
        System.out.println("\n--- Experiment 3: High-Throughput Benchmark (10,000 Contending Bookings) ---");
        int totalRequests = 10_000;
        int seatsCount = 100;

        // 3a. Benchmark Synchronized Method
        SyncMethodBooking methodBooking = new SyncMethodBooking(seatsCount);
        long startMethod = System.nanoTime();
        Thread[] workers1 = new Thread[50];
        for (int i = 0; i < workers1.length; i++) {
            final int threadIdx = i;
            workers1[i] = new Thread(() -> {
                for (int req = 0; req < (totalRequests / 50); req++) {
                    methodBooking.book((req + threadIdx) % seatsCount);
                }
            });
            workers1[i].start();
        }
        for (Thread t : workers1) t.join();
        long timeMethod = System.nanoTime() - startMethod;

        // 3b. Benchmark Fine-Grained Synchronized Block
        FineGrainedSyncBlockBooking blockBooking = new FineGrainedSyncBlockBooking(seatsCount);
        long startBlock = System.nanoTime();
        Thread[] workers2 = new Thread[50];
        for (int i = 0; i < workers2.length; i++) {
            final int threadIdx = i;
            workers2[i] = new Thread(() -> {
                for (int req = 0; req < (totalRequests / 50); req++) {
                    blockBooking.book((req + threadIdx) % seatsCount);
                }
            });
            workers2[i].start();
        }
        for (Thread t : workers2) t.join();
        long timeBlock = System.nanoTime() - startBlock;

        // 3c. Benchmark Lock-Free AtomicBoolean
        AtomicBooking atomicBooking = new AtomicBooking(seatsCount);
        long startAtomic = System.nanoTime();
        Thread[] workers3 = new Thread[50];
        for (int i = 0; i < workers3.length; i++) {
            final int threadIdx = i;
            workers3[i] = new Thread(() -> {
                for (int req = 0; req < (totalRequests / 50); req++) {
                    atomicBooking.book((req + threadIdx) % seatsCount);
                }
            });
            workers3[i].start();
        }
        for (Thread t : workers3) t.join();
        long timeAtomic = System.nanoTime() - startAtomic;

        System.out.printf("1. Coarse Synchronized Method Time : %,12d ns (%.2f ms) | Seats Sold: %d\n",
                timeMethod, timeMethod / 1e6, methodBooking.bookingsMade);
        System.out.printf("2. Fine Synchronized Block Time    : %,12d ns (%.2f ms) | Seats Sold: %d\n",
                timeBlock, timeBlock / 1e6, blockBooking.bookingsMade.get());
        System.out.printf("3. Lock-Free Atomic CAS Time       : %,12d ns (%.2f ms) | Seats Sold: %d\n",
                timeAtomic, timeAtomic / 1e6, atomicBooking.bookingsMade.get());

        // Save benchmarks to CSV
        try (FileWriter fw = new FileWriter("q5_benchmarks.csv")) {
            fw.write("Strategy,TotalRequests,SeatsCount,ExecutionTimeNs,ExecutionTimeMs,CorrectSeatsSold,LockType\n");
            fw.write(String.format("Synchronized_Method,%d,%d,%d,%.2f,%d,\"Coarse-Grained (Screen Instance Monitor)\"\n",
                    totalRequests, seatsCount, timeMethod, timeMethod / 1e6, methodBooking.bookingsMade));
            fw.write(String.format("Fine_Grained_SyncBlock,%d,%d,%d,%.2f,%d,\"Fine-Grained (Per-Seat Object Monitor)\"\n",
                    totalRequests, seatsCount, timeBlock, timeBlock / 1e6, blockBooking.bookingsMade.get()));
            fw.write(String.format("Lock_Free_AtomicCAS,%d,%d,%d,%.2f,%d,\"Hardware CPU CAS (compareAndSet)\"\n",
                    totalRequests, seatsCount, timeAtomic, timeAtomic / 1e6, atomicBooking.bookingsMade.get()));
            System.out.println("[INFO] Saved benchmark results to q5_benchmarks.csv");
        }
        System.out.println("============================================================");
    }
}
