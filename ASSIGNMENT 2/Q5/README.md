# Task Q5: Generic Ranking Component from a Principle

## Overview
This task designs and implements a generic, principle-driven ranking component (`Leaderboard<T>`) for an inter-college sports meet, verifies the expected output, explores multi-tier tie-breaking, exposes faulty comparators, and compares a single-pass chained comparator against a 3-pass stable sort.

## Files in this Directory
- `LeaderboardDemo.java`: Completed code replacing TODO 1 and TODO 2, producing expected output `[Eagles(9,6), Bisons(9,4), Panthers(9,4), Cobras(6,1)]`.
- `RankingInvestigationBenchmark.java`: Verification suite testing multi-tier ties, integer subtraction overflow, incomplete comparators, and 1k-100k benchmarks.
- `input_teams.txt`: Team dataset definitions.
- `q5_results.csv`: Measured benchmarking data in CSV format.
- `q5_output.txt`: Raw console output from running the benchmark.

## How to Compile & Run
```bash
# From workspace root
javac -d bin Java-Assignment-2/Q5/*.java

# Run completed LeaderboardDemo
java -cp bin q5.LeaderboardDemo

# Run full investigation benchmark
java -cp bin q5.RankingInvestigationBenchmark
```

## Key Findings
- **Expected Output:** `[Eagles(9,6), Bisons(9,4), Panthers(9,4), Cobras(6,1)]` verified.
- **Tie-Breaking Reason:** Bisons and Panthers tie on points (9) and goal difference (4). Bisons ranks higher because `"Bisons" < "Panthers"` alphabetically.
- **Faulty Subtraction Bug:** `(a, b) -> b.points - a.points` overflows when negative numbers/extreme values occur, corrupting transitivity.
- **Performance:** Single chained comparator executes in 1 pass ($O(N \log N)$), outperforming 3-pass stable sorting.
