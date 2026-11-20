# Task Q7: Day-4 Design Challenge - Diagnose, Redesign and Validate

## Overview
This task reviews the clinical and algorithmic limitations of the Q6 Emergency Hospital Triage system (`PriorityQueue`), diagnoses the twin bottlenecks of patient starvation and $O(\log N)$ heap overhead on bounded priority domains ($1 \dots 5$), defines clear design objectives, designs a Multi-Level Aging Bucket Queue, and validates it experimentally.

## Files in this Directory
- `FairTriageRedesign.java`: Complete implementation of both the Original `PriorityQueue` solution and the Redesigned `FairMultiBucketTriageQueue`, running starvation stress tests and scaled throughput benchmarks.
- `input_redesign.txt`: Description of test workloads.
- `q7_results.csv`: Measured admission, consultation, throughput, and starvation metrics in CSV format.
- `q7_output.txt`: Raw console output verifying all experiments.

## How to Compile & Run
```bash
# From workspace root
javac -d bin Java-Assignment-2/Q7/*.java
java -cp bin q7.FairTriageRedesign
```

## Key Findings
- **Starvation Elimination:** In `PriorityQueue`, an S5 patient was never treated after 1,000 emergency consults. In the redesigned queue, dynamic aging safely promoted the patient to S2 and delivered fair treatment.
- **Throughput Scaling:**
  - 5,000 patients: 9.85 ms $\to$ 4.96 ms (**1.99x faster**)
  - 20,000 patients: 48.49 ms $\to$ 17.63 ms (**2.75x faster**)
  - 50,000 patients: 108.20 ms $\to$ 31.13 ms (**3.48x faster**)
- **Algorithmic Transition:** Replaced $O(\log N)$ heap sift operations with true $O(1)$ circular deque operations.
