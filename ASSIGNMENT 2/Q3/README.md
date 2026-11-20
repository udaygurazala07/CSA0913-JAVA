# Task Q3: Engineer the hashCode-equals Contract

## Overview
This task investigates how the contract between `equals()` and `hashCode()` governs duplicate detection in hash-based collections (`HashSet`), demonstrates three experimental tests, and compares alternative de-duplication structures.

## Files in this Directory
- `AttendanceBeforeFix.java`: Demonstrates the duplicate scan bug when `hashCode()` is not overridden (Output: `Unique students: 3`).
- `Attendance.java`: Completed code with `hashCode()` properly overridden (Expected Output: `Unique students: 2`).
- `ContractInvestigationBenchmark.java`: Implements Test 1 (identical reference), Test 2 (distinct instances), Test 3 (mutable key trap), and a comparative benchmark of `HashSet` vs `TreeSet` vs `HashMap` across 1k, 10k, and 100k scans.
- `input_scans.txt`: Description of input scan datasets.
- `q3_results.csv`: Measured de-duplication timings in CSV format.
- `q3_output.txt`: Raw console output verifying all tests.

## How to Compile & Run
```bash
# From workspace root
javac -d bin Java-Assignment-2/Q3/*.java

# Run uncompleted code
java -cp bin q3.AttendanceBeforeFix

# Run completed code
java -cp bin q3.Attendance

# Run full investigation benchmark
java -cp bin q3.ContractInvestigationBenchmark
```

## Key Findings
- **Unfixed Attendance Output:** `Unique students: 3` (Failed to de-duplicate Arun).
- **Fixed Attendance Output:** `Unique students: 2` (De-duplication succeeded).
- **Test 1:** Identical references pass without `hashCode()` because reference identity (`==`) matches.
- **Test 2:** Distinct instances with identical fields fail de-duplication because different hash codes route them to different buckets.
- **Test 3:** Mutating key fields after insertion causes `contains()` to fail and creates zombie duplicates.
