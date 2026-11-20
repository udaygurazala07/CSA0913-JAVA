package q4;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Question 4: Behavioral Experiments Harness (Student B)
 * 
 * Conducts:
 * 1. 10 repeated runs recording finish order of stations
 * 2. Priority experiments (MIN_PRIORITY vs MAX_PRIORITY)
 * 3. ExecutorService fixed-thread-pool alternative benchmark
 * 
 * Exports:
 * - q4_finish_orders.csv
 * - q4_priorities.csv
 * 
 * Student: Kattiripalli Dhanush (192311094)
 */
public class ScannerStationBenchmark {

    static class TrackedStation implements Runnable {
        private final int id;
        private final int scripts;
        private final List<Integer> finishOrderList;
        private final Random random = new Random();

        public TrackedStation(int id, int scripts, List<Integer> finishOrderList) {
            this.id = id;
            this.scripts = scripts;
            this.finishOrderList = finishOrderList;
        }

        @Override
        public void run() {
            for (int i = 0; i < scripts; i++) {
                try {
                    Thread.sleep(2 + random.nextInt(6)); // small sleep to allow interleaving
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
            synchronized (finishOrderList) {
                finishOrderList.add(id);
            }
        }
    }

    public static void main(String[] args) throws InterruptedException, IOException {
        System.out.println("============================================================");
        System.out.println("  MULTITHREADING EXPERIMENTAL HARNESS - 10 RUNS & PRIORITY   ");
        System.out.println("============================================================");

        // Experiment 1: 10 Repeated Runs to Record Finish Order
        StringBuilder csvOrders = new StringBuilder();
        csvOrders.append("RunNumber,Station1FinishPos,Station2FinishPos,Station3FinishPos,Station4FinishPos,FullFinishSequence\n");

        System.out.println("\n--- Experiment 1: 10 Repeated Runs (Equal Priority) ---");
        for (int run = 1; run <= 10; run++) {
            List<Integer> finishOrder = Collections.synchronizedList(new ArrayList<>());
            Thread[] threads = new Thread[4];
            for (int i = 0; i < 4; i++) {
                threads[i] = new Thread(new TrackedStation(i + 1, 30, finishOrder), "Station-" + (i + 1));
            }

            for (Thread t : threads) t.start();
            for (Thread t : threads) t.join();

            // Record positions
            int pos1 = finishOrder.indexOf(1) + 1;
            int pos2 = finishOrder.indexOf(2) + 1;
            int pos3 = finishOrder.indexOf(3) + 1;
            int pos4 = finishOrder.indexOf(4) + 1;

            String seq = finishOrder.toString().replace(",", " ->");
            System.out.printf("Run %2d Finish Order: %s\n", run, seq);

            csvOrders.append(String.format("%d,%d,%d,%d,%d,\"%s\"\n",
                    run, pos1, pos2, pos3, pos4, seq));
        }

        try (FileWriter fw = new FileWriter("q4_finish_orders.csv")) {
            fw.write(csvOrders.toString());
            System.out.println("[INFO] Saved 10-run finish orders to q4_finish_orders.csv");
        }

        // Experiment 2: Priority Experiments
        System.out.println("\n--- Experiment 2: Thread Priority Impact ---");
        StringBuilder csvPrio = new StringBuilder();
        csvPrio.append("Trial,Station1_Prio,Station2_Prio,Station3_Prio,Station4_Prio,WinningStation,FullFinishSequence\n");

        // Set Station 4 to MAX_PRIORITY, Station 1 to MIN_PRIORITY
        for (int trial = 1; trial <= 5; trial++) {
            List<Integer> finishOrder = Collections.synchronizedList(new ArrayList<>());
            Thread[] threads = new Thread[4];
            int[] priorities = {Thread.MIN_PRIORITY, Thread.NORM_PRIORITY, Thread.NORM_PRIORITY, Thread.MAX_PRIORITY};

            for (int i = 0; i < 4; i++) {
                threads[i] = new Thread(new TrackedStation(i + 1, 35, finishOrder), "Station-" + (i + 1));
                threads[i].setPriority(priorities[i]);
            }

            for (Thread t : threads) t.start();
            for (Thread t : threads) t.join();

            String seq = finishOrder.toString().replace(",", " ->");
            int winner = finishOrder.get(0);
            System.out.printf("Trial %d (Prio St1=%d, St4=%d): First to finish: Station %d | Sequence: %s\n",
                    trial, Thread.MIN_PRIORITY, Thread.MAX_PRIORITY, winner, seq);

            csvPrio.append(String.format("%d,%d,%d,%d,%d,%d,\"%s\"\n",
                    trial, priorities[0], priorities[1], priorities[2], priorities[3], winner, seq));
        }

        try (FileWriter fw = new FileWriter("q4_priorities.csv")) {
            fw.write(csvPrio.toString());
            System.out.println("[INFO] Saved priority results to q4_priorities.csv");
        }

        // Experiment 3: ExecutorService Alternative
        System.out.println("\n--- Experiment 3: ExecutorService FixedThreadPool Alternative ---");
        long startExec = System.currentTimeMillis();
        ExecutorService pool = Executors.newFixedThreadPool(4);
        List<Integer> execFinishOrder = Collections.synchronizedList(new ArrayList<>());

        for (int i = 0; i < 4; i++) {
            pool.submit(new TrackedStation(i + 1, 30, execFinishOrder));
        }
        pool.shutdown();
        pool.awaitTermination(5, TimeUnit.SECONDS);
        long timeExec = System.currentTimeMillis() - startExec;

        System.out.println("ExecutorService Pool completed in: " + timeExec + " ms");
        System.out.println("ExecutorService Finish Sequence: " + execFinishOrder.toString().replace(",", " ->"));
        System.out.println("============================================================");
    }
}
