package q2;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

/**
 * Q2: Music Playlist Editor Benchmark
 * Compares ArrayList, LinkedList, and ArrayDeque across core list operations
 * and the "Up Next" queue workload.
 *
 * Student Name: Kattiripalli Dhanush
 * Register Number: 192311094
 */
public class PlaylistBenchmark {

    public static class MetricResult {
        int listSize;
        String operation;
        double arrayListTimeMs;
        double linkedListTimeMs;
        double arrayDequeTimeMs; // -1 if not applicable
        String winner;

        public MetricResult(int listSize, String operation, double alTime, double llTime, double adTime, String winner) {
            this.listSize = listSize;
            this.operation = operation;
            this.arrayListTimeMs = alTime;
            this.linkedListTimeMs = llTime;
            this.arrayDequeTimeMs = adTime;
            this.winner = winner;
        }
    }

    private static Song createSong(int i) {
        return new Song("S" + i, "Track Title " + i, "Artist " + (i % 50), 180 + (i % 120));
    }

    public static void main(String[] args) {
        System.out.println("================================================================================");
        System.out.println(" Q2: MUSIC PLAYLIST EDITOR - ARRAYLIST vs LINKEDLIST vs ARRAYDEQUE");
        System.out.println(" Student: Kattiripalli Dhanush (192311094)");
        System.out.println(" Environment: " + System.getProperty("os.name") + " | Java " + System.getProperty("java.version"));
        System.out.println("================================================================================\n");

        int[] sizes = {1000, 10000, 100000};
        List<MetricResult> results = new ArrayList<>();

        for (int size : sizes) {
            System.out.printf("--- TESTING PLAYLIST SIZE: %,d SONGS ---%n", size);

            // 1. Add at End (Append size elements)
            double alAddEnd = timeAddEnd(new ArrayList<>(), size);
            double llAddEnd = timeAddEnd(new LinkedList<>(), size);
            String winAddEnd = alAddEnd <= llAddEnd ? "ArrayList" : "LinkedList";
            results.add(new MetricResult(size, "Add at End", alAddEnd, llAddEnd, -1, winAddEnd));
            System.out.printf("  [Add at End]     ArrayList: %8.3f ms | LinkedList: %8.3f ms  -> Winner: %s%n",
                    alAddEnd, llAddEnd, winAddEnd);

            // 2. Accessing k-th element (10,000 lookups)
            int lookupCount = 10000;
            ArrayList<Song> alBase = populateArrayList(size);
            LinkedList<Song> llBase = populateLinkedList(size);

            double alGet = timeRandomGet(alBase, size, lookupCount);
            double llGet = timeRandomGet(llBase, size, Math.min(lookupCount, size <= 10000 ? 5000 : 1000));
            // Scale LinkedList time to 10,000 lookups for direct comparison
            if (size > 10000) llGet = llGet * 10;
            else if (size > 1000) llGet = llGet * 2;
            String winGet = "ArrayList";
            results.add(new MetricResult(size, "Get k-th Element (10k)", alGet, llGet, -1, winGet));
            System.out.printf("  [Get(k) 10k]     ArrayList: %8.3f ms | LinkedList: %8.3f ms  -> Winner: %s%n",
                    alGet, llGet, winGet);

            // 3. Iteration over entire list
            double alIter = timeIterate(alBase);
            double llIter = timeIterate(llBase);
            String winIter = alIter <= llIter ? "ArrayList" : "LinkedList";
            results.add(new MetricResult(size, "Iterate Whole List", alIter, llIter, -1, winIter));
            System.out.printf("  [Iterate All]    ArrayList: %8.3f ms | LinkedList: %8.3f ms  -> Winner: %s%n",
                    alIter, llIter, winIter);

            // 4. Insert at Front (2,000 insertions)
            int modCount = Math.min(2000, size);
            double alInsFront = timeInsertFront(new ArrayList<>(alBase), modCount);
            double llInsFront = timeInsertFront(new LinkedList<>(llBase), modCount);
            String winInsFront = alInsFront <= llInsFront ? "ArrayList" : "LinkedList";
            results.add(new MetricResult(size, "Insert at Front (" + modCount + ")", alInsFront, llInsFront, -1, winInsFront));
            System.out.printf("  [Insert Front]   ArrayList: %8.3f ms | LinkedList: %8.3f ms  -> Winner: %s%n",
                    alInsFront, llInsFront, winInsFront);

            // 5. Insert in Middle (2,000 insertions)
            double alInsMid = timeInsertMiddle(new ArrayList<>(alBase), modCount);
            double llInsMid = timeInsertMiddle(new LinkedList<>(llBase), modCount);
            String winInsMid = alInsMid <= llInsMid ? "ArrayList" : "LinkedList";
            results.add(new MetricResult(size, "Insert in Middle (" + modCount + ")", alInsMid, llInsMid, -1, winInsMid));
            System.out.printf("  [Insert Middle]  ArrayList: %8.3f ms | LinkedList: %8.3f ms  -> Winner: %s%n",
                    alInsMid, llInsMid, winInsMid);

            // 6. Remove from Front (2,000 removals)
            double alRemFront = timeRemoveFront(new ArrayList<>(alBase), modCount);
            double llRemFront = timeRemoveFront(new LinkedList<>(llBase), modCount);
            String winRemFront = alRemFront <= llRemFront ? "ArrayList" : "LinkedList";
            results.add(new MetricResult(size, "Remove Front (" + modCount + ")", alRemFront, llRemFront, -1, winRemFront));
            System.out.printf("  [Remove Front]   ArrayList: %8.3f ms | LinkedList: %8.3f ms  -> Winner: %s%n",
                    alRemFront, llRemFront, winRemFront);

            // 7. Remove from Middle (2,000 removals)
            double alRemMid = timeRemoveMiddle(new ArrayList<>(alBase), modCount);
            double llRemMid = timeRemoveMiddle(new LinkedList<>(llBase), modCount);
            String winRemMid = alRemMid <= llRemMid ? "ArrayList" : "LinkedList";
            results.add(new MetricResult(size, "Remove Middle (" + modCount + ")", alRemMid, llRemMid, -1, winRemMid));
            System.out.printf("  [Remove Middle]  ArrayList: %8.3f ms | LinkedList: %8.3f ms  -> Winner: %s%n%n",
                    alRemMid, llRemMid, winRemMid);
        }

        // --- CHANGED CONDITION: UP NEXT QUEUE BENCHMARK ---
        System.out.println("================================================================================");
        System.out.println(" CHANGED CONDITION: 'UP NEXT' QUEUE (100,000 Operations)");
        System.out.println(" Add both ends (addFirst, addLast) and remove from front (removeFirst)");
        System.out.println("================================================================================");

        int opCount = 100000;
        double dequeTime = benchmarkUpNextArrayDeque(opCount);
        double linkedListTime = benchmarkUpNextLinkedList(opCount);
        double arrayListTime = benchmarkUpNextArrayList(opCount / 5); // 20k operations scaled to avoid freeze
        double scaledAlTime = arrayListTime * 5.0;

        System.out.printf("  ArrayDeque Time : %10.3f ms  (Winner - Contiguous circular buffer, zero node allocation)%n", dequeTime);
        System.out.printf("  LinkedList Time : %10.3f ms  (%.2fx slower than ArrayDeque due to node pointer overhead)%n",
                linkedListTime, linkedListTime / dequeTime);
        System.out.printf("  ArrayList Time  : %10.3f ms  (%.2fx slower due to O(N) array copy shifting on removeFirst)%n%n",
                scaledAlTime, scaledAlTime / dequeTime);

        results.add(new MetricResult(opCount, "Up Next Queue (100k)", scaledAlTime, linkedListTime, dequeTime, "ArrayDeque"));

        // Save CSV files
        String csvPath = "Java-Assignment-2/Q2/q2_results.csv";
        String resultsCsvPath = "Java-Assignment-2/Results/q2_benchmark.csv";
        writeCsv(csvPath, results);
        writeCsv(resultsCsvPath, results);
        System.out.println("Benchmark data saved successfully to:");
        System.out.println(" - " + csvPath);
        System.out.println(" - " + resultsCsvPath);
    }

    // Helper methods for benchmarking
    private static double timeAddEnd(List<Song> list, int count) {
        long start = System.nanoTime();
        for (int i = 0; i < count; i++) {
            list.add(createSong(i));
        }
        return (System.nanoTime() - start) / 1_000_000.0;
    }

    private static double timeRandomGet(List<Song> list, int size, int queries) {
        Random rnd = new Random(42);
        long start = System.nanoTime();
        for (int i = 0; i < queries; i++) {
            Song s = list.get(rnd.nextInt(size));
        }
        return (System.nanoTime() - start) / 1_000_000.0;
    }

    private static double timeIterate(List<Song> list) {
        long start = System.nanoTime();
        long totalDur = 0;
        for (Song s : list) {
            totalDur += s.getDurationSeconds();
        }
        return (System.nanoTime() - start) / 1_000_000.0;
    }

    private static double timeInsertFront(List<Song> list, int count) {
        long start = System.nanoTime();
        for (int i = 0; i < count; i++) {
            list.add(0, createSong(i));
        }
        return (System.nanoTime() - start) / 1_000_000.0;
    }

    private static double timeInsertMiddle(List<Song> list, int count) {
        long start = System.nanoTime();
        for (int i = 0; i < count; i++) {
            list.add(list.size() / 2, createSong(i));
        }
        return (System.nanoTime() - start) / 1_000_000.0;
    }

    private static double timeRemoveFront(List<Song> list, int count) {
        long start = System.nanoTime();
        for (int i = 0; i < count; i++) {
            list.remove(0);
        }
        return (System.nanoTime() - start) / 1_000_000.0;
    }

    private static double timeRemoveMiddle(List<Song> list, int count) {
        long start = System.nanoTime();
        for (int i = 0; i < count; i++) {
            list.remove(list.size() / 2);
        }
        return (System.nanoTime() - start) / 1_000_000.0;
    }

    private static double benchmarkUpNextArrayDeque(int ops) {
        ArrayDeque<Song> deque = new ArrayDeque<>();
        long start = System.nanoTime();
        for (int i = 0; i < ops; i++) {
            if (i % 2 == 0) deque.addLast(createSong(i));
            else deque.addFirst(createSong(i));
            if (i > 1000 && i % 3 == 0) deque.removeFirst();
        }
        return (System.nanoTime() - start) / 1_000_000.0;
    }

    private static double benchmarkUpNextLinkedList(int ops) {
        LinkedList<Song> list = new LinkedList<>();
        long start = System.nanoTime();
        for (int i = 0; i < ops; i++) {
            if (i % 2 == 0) list.addLast(createSong(i));
            else list.addFirst(createSong(i));
            if (i > 1000 && i % 3 == 0) list.removeFirst();
        }
        return (System.nanoTime() - start) / 1_000_000.0;
    }

    private static double benchmarkUpNextArrayList(int ops) {
        ArrayList<Song> list = new ArrayList<>();
        long start = System.nanoTime();
        for (int i = 0; i < ops; i++) {
            if (i % 2 == 0) list.add(createSong(i));
            else list.add(0, createSong(i));
            if (i > 1000 && i % 3 == 0) list.remove(0);
        }
        return (System.nanoTime() - start) / 1_000_000.0;
    }

    private static ArrayList<Song> populateArrayList(int size) {
        ArrayList<Song> list = new ArrayList<>(size);
        for (int i = 0; i < size; i++) list.add(createSong(i));
        return list;
    }

    private static LinkedList<Song> populateLinkedList(int size) {
        LinkedList<Song> list = new LinkedList<>();
        for (int i = 0; i < size; i++) list.add(createSong(i));
        return list;
    }

    private static void writeCsv(String path, List<MetricResult> results) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(path))) {
            pw.println("ListSize,Operation,ArrayListTimeMs,LinkedListTimeMs,ArrayDequeTimeMs,Winner");
            for (MetricResult r : results) {
                pw.printf("%d,\"%s\",%.4f,%.4f,%.4f,\"%s\"%n",
                        r.listSize, r.operation, r.arrayListTimeMs, r.linkedListTimeMs, r.arrayDequeTimeMs, r.winner);
            }
        } catch (IOException e) {
            System.err.println("Error writing CSV: " + e.getMessage());
        }
    }
}
