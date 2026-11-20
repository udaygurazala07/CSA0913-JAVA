# Task Q6: Priority, Order and Fairness in a Queue

## Overview
This task evaluates Java's `PriorityQueue` in the context of an Emergency Department Hospital Triage system, investigates the mathematical properties of binary min-heaps, explains why direct queue printing does not reflect treatment order, analyzes starvation, and designs a fair aging mechanism.

## Files in this Directory
- `Triage.java`: Completed code replacing TODO 1, verifying expected output `Meena(S1) Kumar(S1) Lata(S2) Ravi(S3) John(S3) Asha(S5)`.
- `TriageInvestigationBenchmark.java`: Verification suite executing the 8-patient trace, direct heap inspection, 100 to 50k patient benchmarks, starvation simulation, and alternative architecture comparison.
- `input_patients.txt`: Patient admission data.
- `q6_results.csv`: Measured admission, polling, and scaling timings in CSV format.
- `q6_output.txt`: Raw console output verifying all tests.

## How to Compile & Run
```bash
# From workspace root
javac -d bin Java-Assignment-2/Q6/*.java

# Run completed Triage
java -cp bin q6.Triage

# Run full investigation benchmark
java -cp bin q6.TriageInvestigationBenchmark
```

## Key Findings
- **Expected Output:** `Meena(S1) Kumar(S1) Lata(S2) Ravi(S3) John(S3) Asha(S5)` matched exactly.
- **Heap Fallacy:** Direct printing outputs level-order heap array indices, where parent <= children, not sequential sorted order. Polling is required to extract true sorted priority.
- **Scalability for 5,000 Patients:** Takes only 10.73 ms, consuming negligible CPU (< 0.00001%) and ~240 KB memory.
- **Fairness:** Fixed-priority queue causes starvation for low-severity cases (S5), which is resolved using dynamic priority aging.
