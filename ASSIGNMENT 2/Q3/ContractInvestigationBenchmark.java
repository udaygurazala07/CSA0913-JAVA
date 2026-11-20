package q3;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

/**
 * Q3: Empirical Investigation of hashCode-equals Contract,
 * Three Experimental Tests, and Alternative De-duplication Benchmarking.
 *
 * Student Name: Kattiripalli Dhanush
 * Register Number: 192311094
 */
public class ContractInvestigationBenchmark {

    // Mutable Student class for Test 3 demonstration
    static class MutableStudent {
        String regNo;
        String name;

        MutableStudent(String regNo, String name) {
            this.regNo = regNo;
            this.name = name;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof MutableStudent)) return false;
            return Objects.equals(regNo, ((MutableStudent) o).regNo);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(regNo);
        }

        @Override
        public String toString() {
            return regNo + " " + name;
        }
    }

    public static class BenchmarkRecord {
        String dataStructure;
        int totalScans;
        int uniqueStudents;
        double scanTimeMs;
        long memoryBytes;

        public BenchmarkRecord(String dataStructure, int totalScans, int uniqueStudents, double scanTimeMs, long memoryBytes) {
            this.dataStructure = dataStructure;
            this.totalScans = totalScans;
            this.uniqueStudents = uniqueStudents;
            this.scanTimeMs = scanTimeMs;
            this.memoryBytes = memoryBytes;
        }
    }

    public static void main(String[] args) {
        System.out.println("================================================================================");
        System.out.println(" Q3: ENGINEER THE HASHCODE-EQUALS CONTRACT & DE-DUPLICATION");
        System.out.println(" Student: Kattiripalli Dhanush (192311094)");
        System.out.println(" Environment: " + System.getProperty("os.name") + " | Java " + System.getProperty("java.version"));
        System.out.println("================================================================================\n");

        // -------------------------------------------------------------------------
        // EXPERIMENTAL TEST 1: HashSet appears to work correctly without hashCode()
        // -------------------------------------------------------------------------
        System.out.println("--- EXPERIMENTAL TEST 1: Set of Scans Where HashSet Appears to Work ---");
        System.out.println("Prediction: Size = 2. When the exact same object reference is added twice,");
        System.out.println("HashSet checks reference identity (e.hash == hash && ((k = e.key) == key)).");
        System.out.println("Since references are identical, the duplicate is successfully detected without hashCode() override.");

        Set<StudentUnfixed> set1 = new HashSet<>();
        StudentUnfixed s1 = new StudentUnfixed("192311001", "Arun");
        StudentUnfixed s2 = new StudentUnfixed("192311002", "Divya");
        set1.add(s1); // Gate 1
        set1.add(s2); // Gate 2
        set1.add(s1); // Gate 3 (Passing same reference)

        System.out.printf("Observed Result: Set size = %d (Prediction Verified: %b)%n%n",
                set1.size(), set1.size() == 2);

        // -------------------------------------------------------------------------
        // EXPERIMENTAL TEST 2: Deliberately Designed to Expose Duplicate Entries
        // -------------------------------------------------------------------------
        System.out.println("--- EXPERIMENTAL TEST 2: Deliberately Expose Duplicate Entries ---");
        System.out.println("Prediction: Size = 3. Distinct 'new' object instances are passed for Arun.");
        System.out.println("Without overriding hashCode(), Object.hashCode() returns distinct memory addresses / PRNG hashes.");
        System.out.println("The two Arun objects land in completely different hash buckets; equals() is never even called.");

        Set<StudentUnfixed> set2 = new HashSet<>();
        set2.add(new StudentUnfixed("192311001", "Arun"));  // Gate 1 (Instance A)
        set2.add(new StudentUnfixed("192311002", "Divya")); // Gate 2 (Instance B)
        set2.add(new StudentUnfixed("192311001", "Arun"));  // Gate 3 (Instance C - distinct object)

        System.out.printf("Observed Result: Set size = %d (Duplicate NOT prevented! Prediction Verified: %b)%n%n",
                set2.size(), set2.size() == 3);

        // -------------------------------------------------------------------------
        // EXPERIMENTAL TEST 3: Additional Test - The Mutable Key Trap
        // -------------------------------------------------------------------------
        System.out.println("--- EXPERIMENTAL TEST 3: The Mutable Key Trap (Mutating regNo Post-Insertion) ---");
        System.out.println("Prediction: Student is physically inside the set, but contains() returns false and duplicate insertion succeeds!");
        System.out.println("Reason: Mutating regNo alters the hashCode. HashSet looks in the new bucket index where nothing exists.");

        Set<MutableStudent> set3 = new HashSet<>();
        MutableStudent ms1 = new MutableStudent("192311001", "Arun");
        set3.add(ms1);
        System.out.printf("  Added Student: %s | Hash: %d | Size: %d%n", ms1, ms1.hashCode(), set3.size());
        System.out.println("  Checking contains() immediately: " + set3.contains(ms1));

        // Mutating key field
        ms1.regNo = "192311999"; // RegNo changed post-insertion
        System.out.printf("  Mutated regNo to: %s | New Hash: %d%n", ms1.regNo, ms1.hashCode());
        boolean foundAfterMutation = set3.contains(ms1);
        System.out.println("  Checking contains() after mutation: " + foundAfterMutation);

        // Try adding another student with the mutated regNo
        set3.add(new MutableStudent("192311999", "Arun"));
        System.out.printf("  Added duplicate with new regNo. Set size = %d (Zombie duplicate created!)%n", set3.size());
        System.out.println("  Conclusion: Key fields in hash-based collections MUST be immutable (final).\n");

        // -------------------------------------------------------------------------
        // COMPARATIVE BENCHMARK: HashSet vs TreeSet vs HashMap for De-duplication
        // -------------------------------------------------------------------------
        System.out.println("================================================================================");
        System.out.println(" ALTERNATIVE DE-DUPLICATION BENCHMARK (1k, 10k, 100k Scans with 50% Duplicates)");
        System.out.println("================================================================================");

        int[] scanSizes = {1000, 10000, 100000};
        List<BenchmarkRecord> benchmarkRecords = new ArrayList<>();

        for (int totalScans : scanSizes) {
            int uniqueCount = totalScans / 2; // 50% duplicate scans
            List<Student> scanStream = generateScans(totalScans, uniqueCount);

            // 1. HashSet<Student> (Fixed hashCode)
            System.gc();
            long memBefore = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
            long start = System.nanoTime();
            Set<Student> hashSet = new HashSet<>();
            for (Student s : scanStream) hashSet.add(s);
            long timeHashSetNs = System.nanoTime() - start;
            long memAfter = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
            double hsTimeMs = timeHashSetNs / 1_000_000.0;
            long hsMem = Math.max(0, memAfter - memBefore);
            benchmarkRecords.add(new BenchmarkRecord("HashSet<Student>", totalScans, hashSet.size(), hsTimeMs, hsMem));

            // 2. TreeSet<Student> with Comparator on regNo
            System.gc();
            memBefore = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
            start = System.nanoTime();
            Set<Student> treeSet = new TreeSet<>(Comparator.comparing(Student::getRegNo));
            for (Student s : scanStream) treeSet.add(s);
            long timeTreeSetNs = System.nanoTime() - start;
            memAfter = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
            double tsTimeMs = timeTreeSetNs / 1_000_000.0;
            long tsMem = Math.max(0, memAfter - memBefore);
            benchmarkRecords.add(new BenchmarkRecord("TreeSet<Student>", totalScans, treeSet.size(), tsTimeMs, tsMem));

            // 3. HashMap<String, Student> keyed by regNo
            System.gc();
            memBefore = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
            start = System.nanoTime();
            Map<String, Student> hashMap = new HashMap<>();
            for (Student s : scanStream) hashMap.put(s.getRegNo(), s);
            long timeHashMapNs = System.nanoTime() - start;
            memAfter = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
            double hmTimeMs = timeHashMapNs / 1_000_000.0;
            long hmMem = Math.max(0, memAfter - memBefore);
            benchmarkRecords.add(new BenchmarkRecord("HashMap<String,Student>", totalScans, hashMap.size(), hmTimeMs, hmMem));

            System.out.printf("[Scans: %,d | Unique: %,d]%n", totalScans, uniqueCount);
            System.out.printf("  HashSet (O(1) avg)        : %8.3f ms | Size: %d | MemDelta: %,d bytes%n", hsTimeMs, hashSet.size(), hsMem);
            System.out.printf("  TreeSet (O(log n) tree)   : %8.3f ms | Size: %d | MemDelta: %,d bytes%n", tsTimeMs, treeSet.size(), tsMem);
            System.out.printf("  HashMap (O(1) key map)    : %8.3f ms | Size: %d | MemDelta: %,d bytes%n%n", hmTimeMs, hashMap.size(), hmMem);
        }

        // Write CSV files
        String csvPath = "Java-Assignment-2/Q3/q3_results.csv";
        String resultsCsvPath = "Java-Assignment-2/Results/q3_benchmark.csv";
        writeCsv(csvPath, benchmarkRecords);
        writeCsv(resultsCsvPath, benchmarkRecords);
        System.out.println("Benchmark data saved successfully to:");
        System.out.println(" - " + csvPath);
        System.out.println(" - " + resultsCsvPath);
    }

    private static List<Student> generateScans(int totalScans, int uniqueCount) {
        List<Student> list = new ArrayList<>(totalScans);
        for (int i = 0; i < totalScans; i++) {
            int studentId = i % uniqueCount;
            list.add(new Student("REG" + String.format("%06d", studentId), "Student_" + studentId));
        }
        return list;
    }

    private static void writeCsv(String path, List<BenchmarkRecord> records) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(path))) {
            pw.println("DataStructure,TotalScans,UniqueStudents,ScanTimeMs,MemoryDeltaBytes");
            for (BenchmarkRecord r : records) {
                pw.printf("\"%s\",%d,%d,%.4f,%d%n",
                        r.dataStructure, r.totalScans, r.uniqueStudents, r.scanTimeMs, r.memoryBytes);
            }
        } catch (IOException e) {
            System.err.println("Error writing CSV: " + e.getMessage());
        }
    }
}
