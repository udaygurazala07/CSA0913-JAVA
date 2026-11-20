# Programming in Java — Assignment 2 (CO2)

## Course Information

**Course Outcome:** CO2 – Strings and StringBuilder, Regular Expressions, Collection Framework (List, Queue, Deque, PriorityQueue, Set, Map), hashCode–equals Contract, Generics, Comparable and Comparator, and Collections Utility Class.

| Details | Information |
|---|---|
| Student Name | G Uday Siva Sai |
| Register Number | 192425259 |
| Total Marks | 50 |
| Java Environment | Java SE 27 |
| Operating System | Microsoft Windows 11 (64-bit AMD64) |
| GitHub Repository | [CSA0913-JAVA](https://github.com/udaygurazala07/CSA0913-JAVA) |

---

## 1. Project Overview

This assignment explores important Java programming concepts through seven practical tasks. It includes theoretical explanations, implementation, performance benchmarking, output analysis, and graphical comparisons.

The assignment focuses on selecting appropriate Java data structures, understanding their performance characteristics, and designing efficient object-oriented solutions.

---

## 2. Directory Structure

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

**Concept:** String, StringBuilder and StringBuffer.

- Compared string concatenation techniques.
- Conducted performance benchmarks across three workloads.
- Measured execution time and intermediate object creation.
- Analyzed memory efficiency and performance.

### Q2 – Music Playlist Editor

**Concept:** ArrayList, LinkedList and ArrayDeque.

- Implemented playlist management.
- Compared seven operations across 1K, 10K and 100K records.
- Evaluated random access and queue operations.
- Analyzed the Up Next functionality.

### Q3 – Event Attendance De-duplication

**Concept:** hashCode(), equals() and HashSet.

- Investigated the hashCode–equals contract.
- Compared incorrect and corrected implementations.
- Tested duplicate student records.
- Analyzed the impact of mutable keys.

### Q4 – Pharmacy Stock Lookup & Reports

**Concept:** HashMap, TreeMap and LinkedHashMap.

- Implemented a pharmacy inventory system.
- Benchmarked 100K stock lookups.
- Compared lookup and reporting performance.
- Evaluated ordering and efficiency.

### Q5 – Generic Ranking Component

**Concept:** Generics, Comparable and Comparator.

- Implemented a generic ranking component.
- Created sports team ranking functionality.
- Tested multiple ranking criteria.
- Investigated comparator overflow and sorting correctness.

### Q6 – Hospital Emergency Triage

**Concept:** PriorityQueue.

- Implemented emergency patient prioritization.
- Evaluated priority-based admission and processing.
- Investigated heap ordering and starvation.
- Conducted heap growth and performance benchmarks.

### Q7 – Day-4 Design Challenge

**Concept:** Fair Multi-Bucket Queue and Aging.

- Redesigned the original priority-based triage system.
- Implemented five priority buckets using ArrayDeque.
- Introduced aging to reduce patient starvation.
- Compared throughput and validated the redesigned system.

---

## 4. Compilation and Execution Guide

All programs can be compiled and executed using standard Java command-line tools.

### Step 1 – Compile all tasks

Run these commands from the repository's parent directory:

```bash
javac -d bin Java-Assignment-2/Q1/*.java
javac -d bin Java-Assignment-2/Q2/*.java
javac -d bin Java-Assignment-2/Q3/*.java
javac -d bin Java-Assignment-2/Q4/*.java
javac -d bin Java-Assignment-2/Q5/*.java
javac -d bin Java-Assignment-2/Q6/*.java
javac -d bin Java-Assignment-2/Q7/*.java
```

### Step 2 – Execute individual tasks

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

- Before fix: `Unique students: 3`
- After fix: `Unique students: 2`

**Q4 – Pharmacy Stock Lookup**

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

**Measured Result:**

- String: 316.02 ms and 1,000,000 intermediate objects.
- StringBuilder: 55.43 ms and 10,000 intermediate objects.
- StringBuilder achieved approximately 5.7x better performance.

**Conclusion:** StringBuilder is recommended for repeated text construction.

### Q2 – ArrayList vs LinkedList vs ArrayDeque

**Measured Result:**

- ArrayList get(k): 2.79 ms.
- LinkedList get(k): 1,893.12 ms.
- ArrayDeque Up Next: 27.51 ms.
- ArrayList Up Next: 218.18 ms.

**Conclusion:** ArrayList is suitable for random access, while ArrayDeque is better for queue operations.

### Q3 – hashCode–equals Contract

**Measured Result:**

- Without hashCode(): Unique students = 3.
- With corrected hashCode(): Unique students = 2.
- Key mutation can create zombie entries.

**Conclusion:** equals() and hashCode() must evaluate identical fields, and hash-based keys should be immutable.

### Q4 – Pharmacy Inventory Map Selection

**Measured Result:**

| Collection | Lookup Time |
|---|---:|
| HashMap | 33.36 ms |
| LinkedHashMap | 41.49 ms |
| TreeMap | 150.80 ms |

LinkedHashMap generated intake-order reports in 4.34 ms.

**Conclusion:** LinkedHashMap provides a useful balance between lookup performance and predictable insertion order.

### Q5 – Generic Ranking Component

**Verified Output:**

```text
[Eagles(9,6), Bisons(9,4), Panthers(9,4), Cobras(6,1)]
```

**Conclusion:** Comparator chaining provides consistent multi-level ranking. Subtraction-based comparison can cause integer overflow and incorrect ordering.

### Q6 – Priority, Order and Fairness in Queue

**Verified Output:**

```text
Meena(S1) Kumar(S1) Lata(S2) Ravi(S3) John(S3) Asha(S5)
```

**Measured Result:**

- PriorityQueue handled 5K patients in 10.73 ms.
- Heap array representation is not necessarily sorted.
- Lower-priority patients may experience starvation.

**Conclusion:** Dynamic aging is required to improve fairness.

### Q7 – Day-4 Design Challenge

**Measured Result:**

- Replaced the original O(log N) heap approach with a Fair Multi-Bucket Queue.
- Used ArrayDeque[5] with aging.
- Eliminated starvation in the validation scenario.
- Achieved 3.48x throughput improvement at 50K patients.

**Conclusion:** A multi-level bucket queue can provide constant-time queue operations while improving fairness through aging.

---

## 6. Final Conclusion

This assignment demonstrates practical applications of Java Collections, Strings, Generics, Comparator, Comparable, and PriorityQueue.

Through implementation and benchmarking, the assignment highlights the importance of selecting appropriate data structures based on performance, memory usage, ordering requirements, and fairness.

The seven tasks combine theoretical knowledge with practical implementation and experimental validation.

---

## 7. Repository

**GitHub Repository:** [CSA0913-JAVA](https://github.com/udaygurazala07/CSA0913-JAVA)

**Student:** G Uday Siva Sai

**Register Number:** 192425259

**Course:** CSA0913 – Programming in Java
