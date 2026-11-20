package q4;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

/**
 * Q4: Hospital Pharmacy Inventory Map Comparison Benchmark
 * Evaluates HashMap, TreeMap, and LinkedHashMap for stock lookup (100k queries),
 * code-order report generation, and insertion-order report generation.
 * Also benchmarks Hashtable and BinarySearch on a sorted array.
 *
 * Student Name: Kattiripalli Dhanush
 * Register Number: 192311094
 */
public class PharmacyBenchmark {

    private static final String[] CATEGORIES = {"Antibiotic", "Analgesic", "Antipyretic", "Antihistamine", "Cardiovascular", "Diabetic"};

    public static class InventoryResult {
        int inventorySize;
        String mapType;
        double putTimeMs;
        double lookup100kTimeMs;
        double avgLookupNs;
        double codeOrderReportMs;
        boolean naturalCodeOrder;
        double insertionOrderReportMs;
        boolean naturalInsertionOrder;

        public InventoryResult(int size, String mapType, double putTimeMs, double lookup100kTimeMs,
                               double codeOrderReportMs, boolean naturalCodeOrder,
                               double insertionOrderReportMs, boolean naturalInsertionOrder) {
            this.inventorySize = size;
            this.mapType = mapType;
            this.putTimeMs = putTimeMs;
            this.lookup100kTimeMs = lookup100kTimeMs;
            this.avgLookupNs = (lookup100kTimeMs * 1_000_000.0) / 100_000.0;
            this.codeOrderReportMs = codeOrderReportMs;
            this.naturalCodeOrder = naturalCodeOrder;
            this.insertionOrderReportMs = insertionOrderReportMs;
            this.naturalInsertionOrder = naturalInsertionOrder;
        }
    }

    private static List<Medicine> generateMedicines(int count) {
        List<Medicine> list = new ArrayList<>(count);
        Random rnd = new Random(1094);
        for (int i = 0; i < count; i++) {
            // Generate pseudo-random medicine code e.g. MED-847291
            String code = String.format("MED-%06d", rnd.nextInt(count * 5));
            String name = "PharmaDrug_" + i;
            String cat = CATEGORIES[i % CATEGORIES.length];
            int qty = 10 + rnd.nextInt(500);
            double price = 5.0 + rnd.nextDouble() * 150.0;
            list.add(new Medicine(code, name, cat, qty, price));
        }
        return list;
    }

    public static void main(String[] args) {
        System.out.println("================================================================================");
        System.out.println(" Q4: PHARMACY INVENTORY - HASHMAP vs TREEMAP vs LINKEDHASHMAP");
        System.out.println(" Student: Kattiripalli Dhanush (192311094)");
        System.out.println(" Environment: " + System.getProperty("os.name") + " | Java " + System.getProperty("java.version"));
        System.out.println("================================================================================\n");

        int[] sizes = {1000, 10000, 100000};
        List<InventoryResult> results = new ArrayList<>();

        for (int size : sizes) {
            System.out.printf("--- TESTING INVENTORY SIZE: %,d MEDICINES ---%n", size);
            List<Medicine> medicineList = generateMedicines(size);

            // Pre-generate 100,000 query keys
            List<String> queryKeys = new ArrayList<>(100000);
            Random qRnd = new Random(42);
            for (int i = 0; i < 100000; i++) {
                queryKeys.add(medicineList.get(qRnd.nextInt(medicineList.size())).getCode());
            }

            // 1. HashMap Evaluation
            testMap(new HashMap<>(), "HashMap", medicineList, queryKeys, results, false, false);

            // 2. TreeMap Evaluation (Natural Code Order)
            testMap(new TreeMap<>(), "TreeMap", medicineList, queryKeys, results, true, false);

            // 3. LinkedHashMap Evaluation (Natural Insertion Order)
            testMap(new LinkedHashMap<>(), "LinkedHashMap", medicineList, queryKeys, results, false, true);

            // 4. Comparison with Hashtable (for Scalability Inquiry)
            testHashtable(new Hashtable<>(), medicineList, queryKeys);

            // 5. Comparison with Sorted Array + binarySearch (for Alternative Concept)
            testBinarySearchArray(medicineList, queryKeys);

            System.out.println();
        }

        // Write CSV files
        String csvPath = "Java-Assignment-2/Q4/q4_results.csv";
        String resultsCsvPath = "Java-Assignment-2/Results/q4_benchmark.csv";
        writeCsv(csvPath, results);
        writeCsv(resultsCsvPath, results);
        System.out.println("Benchmark data saved successfully to:");
        System.out.println(" - " + csvPath);
        System.out.println(" - " + resultsCsvPath);
    }

    private static void testMap(Map<String, Medicine> map, String mapName,
                                List<Medicine> medicines, List<String> queryKeys,
                                List<InventoryResult> results,
                                boolean isNaturallySorted, boolean isNaturallyInsertionOrdered) {
        // Measure Insertion (Put)
        long startPut = System.nanoTime();
        for (Medicine m : medicines) {
            map.put(m.getCode(), m);
        }
        double putTimeMs = (System.nanoTime() - startPut) / 1_000_000.0;

        // Measure 100,000 Lookups
        long startLookup = System.nanoTime();
        for (String key : queryKeys) {
            Medicine m = map.get(key);
        }
        double lookupTimeMs = (System.nanoTime() - startLookup) / 1_000_000.0;
        double avgLookupNs = (lookupTimeMs * 1_000_000.0) / queryKeys.size();

        // Report 1: Code Order Generation
        long startReport1 = System.nanoTime();
        List<Medicine> codeOrderList;
        if (isNaturallySorted) {
            // Natural iteration produces code order directly in O(N)
            codeOrderList = new ArrayList<>(map.values());
        } else {
            // Unordered map must extract entries and sort them in O(N log N)
            codeOrderList = new ArrayList<>(map.values());
            codeOrderList.sort(Comparator.comparing(Medicine::getCode));
        }
        double report1TimeMs = (System.nanoTime() - startReport1) / 1_000_000.0;

        // Report 2: Order Added (Insertion Order) Generation
        long startReport2 = System.nanoTime();
        List<Medicine> insertionOrderList;
        if (isNaturallyInsertionOrdered) {
            // LinkedHashMap naturally preserves insertion order in O(N)
            insertionOrderList = new ArrayList<>(map.values());
        } else {
            // HashMap or TreeMap cannot restore insertion order without external arrival sequence mapping
            // Simulating sorting by external arrival order
            insertionOrderList = new ArrayList<>(medicines);
        }
        double report2TimeMs = (System.nanoTime() - startReport2) / 1_000_000.0;

        results.add(new InventoryResult(medicines.size(), mapName, putTimeMs, lookupTimeMs,
                report1TimeMs, isNaturallySorted, report2TimeMs, isNaturallyInsertionOrdered));

        System.out.printf("  [%-13s] Put: %7.2f ms | 100k Get: %7.2f ms (%.1f ns/op) | Report1(Code): %6.2f ms (Natural: %5b) | Report2(Insert): %6.2f ms (Natural: %5b)%n",
                mapName, putTimeMs, lookupTimeMs, avgLookupNs, report1TimeMs, isNaturallySorted, report2TimeMs, isNaturallyInsertionOrdered);
    }

    private static void testHashtable(Hashtable<String, Medicine> table, List<Medicine> medicines, List<String> queryKeys) {
        long start = System.nanoTime();
        for (Medicine m : medicines) table.put(m.getCode(), m);
        for (String k : queryKeys) { Medicine m = table.get(k); }
        double totalTime = (System.nanoTime() - start) / 1_000_000.0;
        System.out.printf("  [Hashtable    ] Put+Get100k Total: %7.2f ms (Synchronized legacy overhead, null keys forbidden)%n", totalTime);
    }

    private static void testBinarySearchArray(List<Medicine> medicines, List<String> queryKeys) {
        long startSort = System.nanoTime();
        Medicine[] array = medicines.toArray(new Medicine[0]);
        Arrays.sort(array, Comparator.comparing(Medicine::getCode));
        double sortTimeMs = (System.nanoTime() - startSort) / 1_000_000.0;

        long startLookup = System.nanoTime();
        Medicine dummy = new Medicine("", "", "", 0, 0);
        for (String key : queryKeys) {
            int idx = Arrays.binarySearch(array, new Medicine(key, "", "", 0, 0), Comparator.comparing(Medicine::getCode));
        }
        double lookupTimeMs = (System.nanoTime() - startLookup) / 1_000_000.0;
        System.out.printf("  [Sorted Array ] Sort: %7.2f ms | 100k BinarySearch: %7.2f ms (O(log n) lookup, O(n) insert shifting)%n",
                sortTimeMs, lookupTimeMs);
    }

    private static void writeCsv(String path, List<InventoryResult> results) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(path))) {
            pw.println("InventorySize,MapType,PutTimeMs,Lookup100kTimeMs,AvgLookupNs,CodeOrderReportMs,NaturalCodeOrder,InsertionOrderReportMs,NaturalInsertionOrder");
            for (InventoryResult r : results) {
                pw.printf("%d,\"%s\",%.4f,%.4f,%.2f,%.4f,%b,%.4f,%b%n",
                        r.inventorySize, r.mapType, r.putTimeMs, r.lookup100kTimeMs, r.avgLookupNs,
                        r.codeOrderReportMs, r.naturalCodeOrder, r.insertionOrderReportMs, r.naturalInsertionOrder);
            }
        } catch (IOException e) {
            System.err.println("Error writing CSV: " + e.getMessage());
        }
    }
}
