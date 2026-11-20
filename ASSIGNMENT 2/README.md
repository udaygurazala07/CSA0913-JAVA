# Programming in Java — Assignment 2 (CO2)

**Course Outcome:** CO2 – Strings and StringBuilder, regular expressions, the Collection Framework (List, Queue, Deque, PriorityQueue, Set, Map), the hashCode–equals contract, Generics, Comparable and Comparator, and the Collections utility class.  
**Student Name:** Kattiripalli Dhanush  
**Register Number:** 192311094  
**Total Marks:** 50 Marks  
**Java Environment:** Java SE 27 (build 27.0.1.0.3+1-2, mixed mode, sharing)  
**Operating System:** Microsoft Windows 11 (64-bit AMD64)  
**GitHub Repository:** [https://github.com/Dhanushssmb/java.git](https://github.com/Dhanushssmb/java.git)  
**Assignment Report PDF:** [Java_Assignment_2_Report_192311094.pdf](./Java_Assignment_2_Report_192311094.pdf)  

---

## Directory Structure

```
Java-Assignment-2/
├── README.md                                    # Master documentation & instructions
├── .gitignore                                   # Ignore class files, logs, metadata
├── Java_Assignment_2_Report_192311094.pdf      # Complete PDF report with embedded charts
│
├── Theory-Answers/                              # In-depth theory answers (Q1 to Q7)
│   ├── Q1.md                                    # String vs StringBuilder vs StringBuffer
│   ├── Q2.md                                    # ArrayList vs LinkedList vs ArrayDeque
│   ├── Q3.md                                    # hashCode–equals contract & HashSet
│   ├── Q4.md                                    # HashMap vs TreeMap vs LinkedHashMap
│   ├── Q5.md                                    # Generic ranking component & Comparator
│   ├── Q6.md                                    # PriorityQueue, triage & fairness
│   └── Q7.md                                    # Day-4 Design Challenge: Diagnose & Redesign
│
├── Q1/                                          # Railway e-Ticket Generation
│   ├── TicketBenchmark.java                     # Benchmark suite for 3 workloads
│   ├── workloads.txt                            # Workload definitions
│   ├── q1_results.csv                           # Measured timing & intermediate objects
│   ├── q1_output.txt                            # Raw execution output
│   ├── q1_string_comparison.png                 # Visualization chart
│   └── README.md                                # Task summary & instructions
│
├── Q2/                                          # Music Playlist Editor
│   ├── Song.java                                # Track model
│   ├── PlaylistBenchmark.java                   # 7 operations across 1k, 10k, 100k + Up Next
│   ├── input_playlists.txt                      # Playlist scale descriptions
│   ├── q2_results.csv                           # Measured operation times
│   ├── q2_output.txt                            # Raw execution output
│   ├── q2_list_comparison.png                   # Latency comparison chart
│   └── README.md                                # Task summary & instructions
│
├── Q3/                                          # Event Attendance De-duplication
│   ├── AttendanceBeforeFix.java                 # Unfixed code (Output: Unique students: 3)
│   ├── Attendance.java                          # Completed code (Output: Unique students: 2)
│   ├── ContractInvestigationBenchmark.java      # 3 designed tests & scale benchmark
│   ├── input_scans.txt                          # Scan stream definitions
│   ├── q3_results.csv                           # De-duplication timings
│   ├── q3_output.txt                            # Raw execution output
│   ├── q3_contract_comparison.png               # De-duplication scaling chart
│   └── README.md                                # Task summary & instructions
│
├── Q4/                                          # Pharmacy Stock Lookup & Reports
│   ├── Medicine.java                            # Pharmaceutical domain entity
│   ├── PharmacyBenchmark.java                   # 100k lookups & dual report benchmark
│   ├── input_inventory.txt                      # Inventory dataset definitions
│   ├── q4_results.csv                           # Lookup and reporting timings
│   ├── q4_output.txt                            # Raw execution output
│   ├── q4_map_comparison.png                    # Map latency comparison chart
│   └── README.md                                # Task summary & instructions
│
├── Q5/                                          # Generic Ranking Component
│   ├── LeaderboardDemo.java                     # Completed code (Output: [Eagles, Bisons, Panthers, Cobras])
│   ├── RankingInvestigationBenchmark.java       # Multi-tier tie check & faulty comparator test
│   ├── input_teams.txt                          # Sports team datasets
│   ├── q5_results.csv                           # Sorting pass timings
│   ├── q5_output.txt                            # Raw execution output
│   └── README.md                                # Task summary & instructions
│
├── Q6/                                          # Hospital Emergency Triage
│   ├── Triage.java                              # Completed code (Output: Meena Kumar Lata Ravi John Asha)
│   ├── TriageInvestigationBenchmark.java        # 8-patient trace, heap inspection & growth
│   ├── input_patients.txt                       # Patient admission records
│   ├── q6_results.csv                           # Heap admission and poll timings
│   ├── q6_output.txt                            # Raw execution output
│   ├── q6_triage_scaling.png                    # Heap scaling chart
│   └── README.md                                # Task summary & instructions
│
├── Q7/                                          # Day-4 Design Challenge
│   ├── FairTriageRedesign.java                  # Original vs Fair Multi-Bucket Redesign
│   ├── input_redesign.txt                       # Workload configurations
│   ├── q7_results.csv                           # Redesign validation metrics
│   ├── q7_output.txt                            # Raw execution output
│   ├── q7_redesign_comparison.png               # Throughput speedup chart
│   └── README.md                                # Task summary & instructions
│
└── Results/                                     # Consolidated benchmark data & plots
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

## Quick Compilation & Execution Guide

All programs can be compiled and executed from the command line using standard Java tools:

```bash
# 1. Compile all tasks
javac -d bin Java-Assignment-2/Q1/*.java
javac -d bin Java-Assignment-2/Q2/*.java
javac -d bin Java-Assignment-2/Q3/*.java
javac -d bin Java-Assignment-2/Q4/*.java
javac -d bin Java-Assignment-2/Q5/*.java
javac -d bin Java-Assignment-2/Q6/*.java
javac -d bin Java-Assignment-2/Q7/*.java

# 2. Run Task Q1 (Railway e-Ticket Benchmark)
java -cp bin q1.TicketBenchmark

# 3. Run Task Q2 (Playlist Editor Benchmark)
java -cp bin q2.PlaylistBenchmark

# 4. Run Task Q3 (Attendance De-duplication)
java -cp bin q3.AttendanceBeforeFix   # Outputs: Unique students: 3
java -cp bin q3.Attendance            # Outputs: Unique students: 2
java -cp bin q3.ContractInvestigationBenchmark

# 5. Run Task Q4 (Pharmacy Stock Lookup Benchmark)
java -cp bin q4.PharmacyBenchmark

# 6. Run Task Q5 (Inter-College Sports Leaderboard)
java -cp bin q5.LeaderboardDemo       # Outputs: [Eagles(9,6), Bisons(9,4), Panthers(9,4), Cobras(6,1)]
java -cp bin q5.RankingInvestigationBenchmark

# 7. Run Task Q6 (Emergency Department Triage)
java -cp bin q6.Triage                # Outputs: Meena(S1) Kumar(S1) Lata(S2) Ravi(S3) John(S3) Asha(S5)
java -cp bin q6.TriageInvestigationBenchmark

# 8. Run Task Q7 (Day-4 Design Challenge Validation)
java -cp bin q7.FairTriageRedesign
```

---

## Executive Summary of Results

| Task | Topic | Key Evidence & Measured Result | Outcome / Verdict |
| :--- | :--- | :--- | :--- |
| **Q1** | String vs StringBuilder vs StringBuffer | Workload 2 (10k x 50 fields): String 316.02 ms (1,000,000 objects) vs StringBuilder 55.43 ms (10,000 objects). | **StringBuilder is 5.7x faster** with $O(n)$ amortized space. Recommended for per-ticket text. |
| **Q2** | ArrayList vs LinkedList vs ArrayDeque | At 100k songs, `get(k)` took 2.79 ms in ArrayList vs 1,893.12 ms in LinkedList. "Up Next" queue: ArrayDeque took 27.51 ms vs 218.18 ms in ArrayList. | **ArrayList** is superior for playback random access; **ArrayDeque** is superior for double-ended queues. |
| **Q3** | hashCode–equals Contract | Without `hashCode()`, set size was 3. Overriding `hashCode()` produced **Unique students: 2**. Key mutation creates zombie entries. | **Contract confirmed:** Equals and hashCode must evaluate identical fields; keys must be immutable. |
| **Q4** | Pharmacy Inventory Map Selection | 100k lookups: HashMap (33.36 ms), LinkedHashMap (41.49 ms), TreeMap (150.80 ms). LinkedHashMap naturally yields intake order in 4.34 ms. | **LinkedHashMap** recommended: sub-microsecond lookups (414.9 ns) + zero-cost intake reporting. |
| **Q5** | Generic Ranking Component | Verified expected output: **`[Eagles(9,6), Bisons(9,4), Panthers(9,4), Cobras(6,1)]`**. Subtraction comparison overflows into negative values. | **Comparator chaining** guarantees total ordering and transitivity without modifying domain entities. |
| **Q6** | Priority, Order and Fairness in Queue | Verified expected output: **`Meena(S1) Kumar(S1) Lata(S2) Ravi(S3) John(S3) Asha(S5)`**. Heap array direct print is not sorted. S5 patients starve under heavy loads. | **PriorityQueue** handles 5k patients in 10.73 ms, but requires dynamic aging to prevent starvation. |
| **Q7** | Day-4 Design Challenge | Replaced $O(\log N)$ heap with **Fair Multi-Bucket Queue** (`ArrayDeque[5]` + Aging). Starvation eliminated (Asha treated); throughput increased by **3.48x** at 50k patients. | **Design validated:** $O(1)$ constant-time multi-level bucket queue solves starvation and accelerates throughput. |
