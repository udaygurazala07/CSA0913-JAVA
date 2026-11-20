# Programming in Java — Assignment 2 (CO2)

## Student Information

| Field | Details |
|---|---|
| **Course Outcome** | CO2 – Strings, StringBuilder, Regular Expressions, Collections Framework, Generics, Comparable, Comparator and Collections Utility Class |
| **Student Name** | G Uday Siva Sai |
| **Register Number** | 192425259 |
| **Total Marks** | 50 |
| **Java Environment** | Java SE 27 (build 27.0.1.0.3+1-2, mixed mode, sharing) |
| **Operating System** | Microsoft Windows 11 (64-bit AMD64) |
| **GitHub Repository** | [CSA0913-JAVA](https://github.com/udaygurazala07/CSA0913-JAVA) |

---

## 1. Assignment Overview

This assignment focuses on the practical implementation and analysis of Java programming concepts under Course Outcome CO2.

The major concepts covered include:

- String, StringBuilder and StringBuffer
- Regular Expressions
- Java Collections Framework
- List, Queue, Deque and PriorityQueue
- Set and Map
- hashCode() and equals() contract
- Generics
- Comparable and Comparator
- Collections Utility Class
- Performance benchmarking and analysis

The assignment consists of seven practical tasks, theoretical answers, execution outputs, benchmark results and graphical comparisons.

---

## 2. Project Directory Structure

```text
Java-Assignment-2/
│
├── README.md
├── .gitignore
├── Java_Assignment_2_Report_192311094.pdf
│
├── Theory-Answers/
│   ├── Q1.md
│   ├── Q2.md
│   ├── Q3.md
│   ├── Q4.md
│   ├── Q5.md
│   ├── Q6.md
│   └── Q7.md
│
├── Q1/  Railway e-Ticket Generation
│   ├── TicketBenchmark.java
│   ├── workloads.txt
│   ├── q1_results.csv
│   ├── q1_output.txt
│   ├── q1_string_comparison.png
│   └── README.md
│
├── Q2/  Music Playlist Editor
│   ├── Song.java
│   ├── PlaylistBenchmark.java
│   ├── input_playlists.txt
│   ├── q2_results.csv
│   ├── q2_output.txt
│   ├── q2_list_comparison.png
│   └── README.md
│
├── Q3/  Event Attendance De-duplication
│   ├── AttendanceBeforeFix.java
│   ├── Attendance.java
│   ├── ContractInvestigationBenchmark.java
│   ├── input_scans.txt
│   ├── q3_results.csv
│   ├── q3_output.txt
│   ├── q3_contract_comparison.png
│   └── README.md
│
├── Q4/  Pharmacy Stock Lookup & Reports
│   ├── Medicine.java
│   ├── PharmacyBenchmark.java
│   ├── input_inventory.txt
│   ├── q4_results.csv
│   ├── q4_output.txt
│   ├── q4_map_comparison.png
│   └── README.md
│
├── Q5/  Generic Ranking Component
│   ├── LeaderboardDemo.java
│   ├── RankingInvestigationBenchmark.java
│   ├── input_teams.txt
│   ├── q5_results.csv
│   ├── q5_output.txt
│   └── README.md
│
├── Q6/  Hospital Emergency Triage
│   ├── Triage.java
│   ├── TriageInvestigationBenchmark.java
│   ├── input_patients.txt
│   ├── q6_results.csv
│   ├── q6_output.txt
│   ├── q6_triage_scaling.png
│   └── README.md
│
├── Q7/  Day-4 Design Challenge
│   ├── FairTriageRedesign.java
│   ├── input_redesign.txt
│   ├── q7_results.csv
│   ├── q7_output.txt
│   ├── q7_redesign_comparison.png
│   └── README.md
│
└── Results/
    ├── q1_benchmark.csv
    ├── q2_benchmark.csv
    ├── q3_benchmark.csv
    ├── q4_benchmark.csv
    ├── q5_benchmark.csv
    ├── q6_benchmark.csv
    ├── q7_benchmark.csv
    ├── q1_string_comparison.png
    ├── q2_list_comparison.png
    ├── q3_contract_comparison.png
    ├── q4_map_comparison.png
    ├── q6_triage_scaling.png
    └── q7_redesign_comparison.png
```

---

## 3. Task Descriptions

### Q1 – Railway e-Ticket Generation

**Concepts:** String, StringBuilder and StringBuffer.

- Compared different string manipulation techniques.
- Conducted benchmarks across three workloads.
- Measured execution time and intermediate object creation.
- Analyzed performance and memory efficiency.

### Q2 – Music Playlist Editor

**Concepts:** ArrayList, LinkedList and ArrayDeque.

- Implemented music playlist management.
- Compared seven operations across 1K, 10K and 100K records.
- Evaluated random access and queue operations.
- Analyzed Up Next functionality.

### Q3 – Event Attendance De-duplication

**Concepts:** hashCode(), equals() and HashSet.

- Investigated the hashCode–equals contract.
- Compared unfixed and corrected implementations.
- Tested duplicate student records.
- Analyzed the impact of mutable keys.

### Q4 – Pharmacy Stock Lookup & Reports

**Concepts:** HashMap, TreeMap and LinkedHashMap.

- Implemented pharmacy inventory management.
- Benchmarked 100K stock lookups.
- Compared lookup and reporting performance.
- Evaluated ordering and efficiency.

### Q5 – Generic Ranking Component

**Concepts:** Generics, Comparable and Comparator.

- Implemented a generic ranking component.
- Created sports team ranking functionality.
- Tested multiple ranking criteria.
- Investigated comparator overflow and sorting correctness.

### Q6 – Hospital Emergency Triage

**Concept:** PriorityQueue.

- Implemented emergency patient prioritization.
- Evaluated priority-based admission and processing.
- Investigated heap ordering and starvation.
- Conducted performance and heap growth benchmarks.

### Q7 – Day-4 Design Challenge

**Concepts:** Fair Multi-Bucket Queue and Aging.

- Redesigned the original priority-based triage system.
- Implemented five priority buckets using ArrayDeque.
- Introduced aging to reduce patient starvation.
- Compared throughput and validated the redesigned system.

---

## 4. Compilation and Execution Guide

All programs can be compiled and executed using standard Java command-line tools.

### Step 1: Compile All Tasks

Run the following commands from the parent directory containing `Java-Assignment-2`.

```bash
javac -d bin Java-Assignment-2/Q1/*.java
javac -d bin Java-Assignment-2/Q2/*.java
javac -d bin Java-Assignment-2/Q3/*.java
javac -d bin Java-Assignment-2/Q4/*.java
javac -d bin Java-Assignment-2/Q5/*.java
javac -d bin Java-Assignment-2/Q6/*.java
javac -d bin Java-Assignment-2/Q7/*.java
```

### Step 2: Execute Individual Tasks

**Q1 – Railway e-Ticket Benchmark**

```bash
java -cp bin q1.TicketBenchmark
```

**Q2 – Music Playlist Editor**

```bash
java -cp bin q2.PlaylistBenchmark
```

**Q3 – Attendance De-duplication**

```bash
java -cp bin q3.AttendanceBeforeFix
java -cp bin q3.Attendance
java -cp bin q3.ContractInvestigationBenchmark
```

Expected outputs:

```text
Before Fix: Unique students: 3
After Fix:  Unique students: 2
```

**Q4 – Pharmacy Stock Lookup Benchmark**

```bash
java -cp bin q4.PharmacyBenchmark
```

**Q5 – Generic Ranking Component**

```bash
java -cp bin q5.LeaderboardDemo
java -cp bin q5.RankingInvestigationBenchmark
```

Expected output:

```text
[Eagles(9,6), Bisons(9,4), Panthers(9,4), Cobras(6,1)]
```

**Q6 – Hospital Emergency Triage**

```bash
java -cp bin q6.Triage
java -cp bin q6.TriageInvestigationBenchmark
```

Expected output:

```text
Meena(S1) Kumar(S1) Lata(S2) Ravi(S3) John(S3) Asha(S5)
```

**Q7 – Day-4 Design Challenge**

```bash
java -cp bin q7.FairTriageRedesign
```

---

## 5. Executive Summary of Benchmark Results

### Q1 – String vs StringBuilder vs StringBuffer

**Measured Results:**

| Implementation | Execution Time | Intermediate Objects |
|---|---:|---:|
| String | 316.02 ms | 1,000,000 |
| StringBuilder | 55.43 ms | 10,000 |

**Outcome:**

- StringBuilder achieved approximately 5.7x better performance.
- Demonstrated O(n) amortized space behavior.

**Verdict:** StringBuilder is recommended for repeated per-ticket text construction.

### Q2 – ArrayList vs LinkedList vs ArrayDeque

**Measured Results:**

| Operation | Collection | Execution Time |
|---|---|---:|
| Random Access | ArrayList | 2.79 ms |
| Random Access | LinkedList | 1,893.12 ms |
| Up Next Queue | ArrayDeque | 27.51 ms |
| Up Next Queue | ArrayList | 218.18 ms |

**Outcome:**

- ArrayList performs better for random access.
- ArrayDeque performs better for queue operations.

**Verdict:** ArrayList is recommended for playback random access, while ArrayDeque is recommended for double-ended queue operations.

### Q3 – hashCode–equals Contract

**Measured Results:**

| Implementation | Unique Students |
|---|---:|
| Without hashCode() | 3 |
| With corrected hashCode() | 2 |

**Outcome:**

- Demonstrated the importance of the hashCode–equals contract.
- Identified the problem of mutable keys and zombie entries.

**Verdict:** equals() and hashCode() must evaluate identical fields. Keys used in hash-based collections should remain immutable.

### Q4 – Pharmacy Inventory Map Selection

**Measured Results:**

| Collection | 100K Lookup Time |
|---|---:|
| HashMap | 33.36 ms |
| LinkedHashMap | 41.49 ms |
| TreeMap | 150.80 ms |

LinkedHashMap generated intake-order reports in 4.34 ms.

**Outcome:**

- HashMap provided the fastest measured lookup.
- LinkedHashMap maintained insertion order.
- TreeMap provided sorted key ordering.

**Verdict:** LinkedHashMap is recommended for this use case because it combines efficient lookups with predictable intake-order reporting.

### Q5 – Generic Ranking Component

**Verified Output:**

```text
[Eagles(9,6), Bisons(9,4), Panthers(9,4), Cobras(6,1)]
```

**Outcome:**

- Verified multi-level ranking.
- Identified integer overflow caused by subtraction-based comparison.
- Demonstrated Comparator chaining.

**Verdict:** Comparator chaining provides consistent multi-level ordering without modifying domain entities.

### Q6 – Priority, Order and Fairness in Queue

**Verified Output:**

```text
Meena(S1) Kumar(S1) Lata(S2) Ravi(S3) John(S3) Asha(S5)
```

**Measured Result:**

- PriorityQueue processed 5K patients in 10.73 ms.
- Directly printing the heap array does not produce sorted output.
- S5 patients can experience starvation under heavy loads.

**Verdict:** PriorityQueue provides efficient priority processing, but dynamic aging is required to improve fairness.

### Q7 – Day-4 Design Challenge

**Measured Results:**

- Replaced the O(log N) heap approach with a Fair Multi-Bucket Queue.
- Implemented ArrayDeque[5] with Aging.
- Eliminated starvation in the validation scenario.
- Achieved 3.48x throughput improvement at 50K patients.

**Verdict:** The Fair Multi-Bucket Queue provides O(1) queue operations under the designed bucket-based approach and improves fairness through aging.

---

## 6. Key Learning Outcomes

Through this assignment, the following concepts were practically explored:

1. Efficient string manipulation using StringBuilder.
2. Selecting suitable data structures based on workload.
3. Implementing correct equals() and hashCode() methods.
4. Understanding Java Map implementations.
5. Creating generic ranking systems using Comparator.
6. Implementing priority-based queue processing.
7. Designing fair queue systems using aging techniques.
8. Measuring and analyzing program performance.

---

## 7. Final Conclusion

This assignment demonstrates the practical application of Java Strings, Collections Framework, Generics, Comparable, Comparator and PriorityQueue.

Through seven practical tasks, theoretical explanations, implementation, performance benchmarking and graphical analysis, the assignment highlights the importance of selecting appropriate data structures based on performance, memory usage, ordering requirements and fairness.

The completed work combines theoretical understanding with practical Java programming and experimental validation.

---

## 8. Repository Information

**GitHub Repository:** [CSA0913-JAVA](https://github.com/udaygurazala07/CSA0913-JAVA)

**Student Name:** G Uday Siva Sai

**Register Number:** 192425259

**Course:** Programming in Java

**Course Code:** CSA0913
