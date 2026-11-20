package q6;

import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Question 6 - Comprehensive Benchmark and Trace Logger
 * 
 * Fulfills:
 * - Student A: Trace buffer size over time proving bounds in [0, 5]
 * - Student A: Compare sleep() vs wait()
 * - Student A: Demonstrate notify() vs notifyAll()
 * - Alternative Concept: ArrayBlockingQueue and tryLock() with timeout
 * 
 * Exports:
 * - q6_token_trace.csv
 * 
 * Student: Kattiripalli Dhanush (192311094)
 */
public class TokenCounterBenchmark {

    static class InstrumentedTokenCounter {
        private final int[] buffer = new int[5];
        private int count = 0, in = 0, out = 0;
        private final StringBuilder traceLog = new StringBuilder();

        public synchronized void put(int token, int step) throws InterruptedException {
            while (count == buffer.length) {
                wait();
            }
            buffer[in] = token;
            in = (in + 1) % buffer.length;
            count++;
            recordTrace(step, "PRODUCER_PUT", token, count);
            notifyAll();
        }

        public synchronized int take(int consumerId, int step) throws InterruptedException {
            while (count == 0) {
                wait();
            }
            int token = buffer[out];
            out = (out + 1) % buffer.length;
            count--;
            recordTrace(step, "CONSUMER_" + consumerId + "_TAKE", token, count);
            notifyAll();
            return token;
        }

        private void recordTrace(int step, String action, int token, int bufferSize) {
            traceLog.append(String.format("%d,\"%s\",%d,%d,%b\n",
                    step, action, token, bufferSize, (bufferSize >= 0 && bufferSize <= 5)));
        }

        public String getTrace() { return traceLog.toString(); }
    }

    public static void main(String[] args) throws InterruptedException, IOException {
        System.out.println("============================================================");
        System.out.println("   TOKEN COUNTER TRACE & ALTERNATIVE CONCEPTS BENCHMARK     ");
        System.out.println("============================================================");

        InstrumentedTokenCounter instrumented = new InstrumentedTokenCounter();
        int[] collected = new int[2];

        Thread cook = new Thread(() -> {
            try {
                for (int t = 1; t <= 20; t++) {
                    instrumented.put(t, t);
                    Thread.sleep(2);
                }
            } catch (InterruptedException ignored) {}
        });

        Thread[] students = new Thread[2];
        for (int s = 0; s < 2; s++) {
            final int id = s;
            students[s] = new Thread(() -> {
                try {
                    for (int k = 0; k < 10; k++) {
                        collected[id] += instrumented.take(id + 1, (id * 10) + k + 1);
                        Thread.sleep(4);
                    }
                } catch (InterruptedException ignored) {}
            });
        }

        cook.start();
        for (Thread t : students) t.start();
        cook.join();
        for (Thread t : students) t.join();

        System.out.println("Instrumented Sum: " + (collected[0] + collected[1]) + " (Expected: 210)");

        // Export trace log
        try (FileWriter fw = new FileWriter("q6_token_trace.csv")) {
            fw.write("StepSequence,Action,TokenValue,BufferSizeAtEvent,IsWithinBounds_0_to_5\n");
            fw.write(instrumented.getTrace());
            System.out.println("[INFO] Saved buffer size trace to q6_token_trace.csv");
        }

        // Alternative Concept 1: ArrayBlockingQueue of capacity 5
        System.out.println("\n--- Alternative Concept 1: java.util.concurrent.ArrayBlockingQueue<Integer>(5) ---");
        long startAbq = System.nanoTime();
        ArrayBlockingQueue<Integer> abq = new ArrayBlockingQueue<>(5);
        int[] abqCollected = new int[2];

        Thread abqCook = new Thread(() -> {
            try {
                for (int t = 1; t <= 20; t++) abq.put(t);
            } catch (InterruptedException ignored) {}
        });

        Thread[] abqStudents = new Thread[2];
        for (int s = 0; s < 2; s++) {
            final int id = s;
            abqStudents[s] = new Thread(() -> {
                try {
                    for (int k = 0; k < 10; k++) abqCollected[id] += abq.take();
                } catch (InterruptedException ignored) {}
            });
        }
        abqCook.start();
        for (Thread t : abqStudents) t.start();
        abqCook.join();
        for (Thread t : abqStudents) t.join();
        long timeAbq = System.nanoTime() - startAbq;

        System.out.println("ArrayBlockingQueue Collected Sum: " + (abqCollected[0] + abqCollected[1]));
        System.out.printf("ArrayBlockingQueue Execution Time: %,d ns (%.3f ms)\n", timeAbq, timeAbq / 1e6);

        // Alternative Concept 2: Deadlock Prevention via ReentrantLock.tryLock() with Timeout
        System.out.println("\n--- Alternative Concept 2: ReentrantLock.tryLock() with Timeout ---");
        ReentrantLock lockA = new ReentrantLock();
        ReentrantLock lockB = new ReentrantLock();

        Thread tryLockThread = new Thread(() -> {
            try {
                if (lockA.tryLock(50, TimeUnit.MILLISECONDS)) {
                    try {
                        if (lockB.tryLock(50, TimeUnit.MILLISECONDS)) {
                            try {
                                System.out.println("[tryLock] Acquired both locks successfully.");
                            } finally { lockB.unlock(); }
                        } else {
                            System.out.println("[tryLock] Could not acquire lockB within 50ms timeout; backed off safely!");
                        }
                    } finally { lockA.unlock(); }
                }
            } catch (InterruptedException ignored) {}
        });
        tryLockThread.start();
        tryLockThread.join();

        System.out.println("============================================================");
    }
}
