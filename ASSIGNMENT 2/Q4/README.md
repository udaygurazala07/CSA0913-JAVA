# Task Q4: Choosing a Map for a Pharmacy Inventory

## Overview
This task evaluates `HashMap`, `TreeMap`, and `LinkedHashMap` for managing a hospital pharmacy inventory across 100,000 lookups, natural vs. sorted report generation (code order vs. insertion order), and compares them against `Hashtable` and a sorted array using binary search.

## Files in this Directory
- `Medicine.java`: Domain model implementing `Comparable<Medicine>` for pharmaceutical inventory.
- `PharmacyBenchmark.java`: Benchmark suite evaluating `put`, `get` (100k queries), Code-Order Report, and Insertion-Order Report across 1,000, 10,000, and 100,000 medicines.
- `input_inventory.txt`: Description of inventory scale datasets.
- `q4_results.csv`: Measured timings and order compliance in CSV format.
- `q4_output.txt`: Raw console output from running the benchmark.

## How to Compile & Run
```bash
# From workspace root
javac -d bin Java-Assignment-2/Q4/*.java
java -cp bin q4.PharmacyBenchmark
```

## Key Findings
- **Lookup Latency (100,000 medicines):**
  - `HashMap`: 33.36 ms (333.6 ns/op)
  - `LinkedHashMap`: 41.49 ms (414.9 ns/op)
  - `TreeMap`: 150.80 ms (1,508.0 ns/op - 4.5x slower)
  - `Hashtable`: 179.69 ms (Synchronized contention)
  - `Sorted Array BinarySearch`: 146.08 ms (1,460.8 ns/op)
- **Natural Reporting:**
  - `TreeMap` naturally produces Report 1 (Code Order) in 32.05 ms ($O(N)$).
  - `LinkedHashMap` naturally produces Report 2 (Insertion Order) in 4.34 ms ($O(N)$).
- **Deployment Recommendation:** Deploy `LinkedHashMap<String, Medicine>`. It provides near-instant daytime lookups (414.9 ns), natural zero-cost insertion-order reporting, and an evening sort for code order takes only ~210 ms.
