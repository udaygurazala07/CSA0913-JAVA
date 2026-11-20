# Task Q1: Building Text Under Load

## Overview
This task investigates the performance and memory behavior of `String` (+ operator), `StringBuilder`, and `StringBuffer` in a Railway e-Ticket Generation system.

## Files in this Directory
- `TicketBenchmark.java`: Benchmark suite implementing 3 workloads and measuring execution time, object churn, and memory.
- `workloads.txt`: Description of the three test workloads (1 ticket, 10k tickets, 50k tickets).
- `q1_results.csv`: Measured benchmark numbers in CSV format.
- `q1_output.txt`: Raw console output from running the benchmark.

## How to Compile & Run
```bash
# From workspace root
javac -d bin Java-Assignment-2/Q1/TicketBenchmark.java
java -cp bin q1.TicketBenchmark
```

## Summary of Results
- Workload 1 (1 ticket × 10 fields): String 0.053 ms vs StringBuilder 0.021 ms (2.5x faster).
- Workload 2 (10,000 tickets × 50 fields): String 316.02 ms vs StringBuilder 55.43 ms (5.7x faster). String created 1,000,000 intermediate objects.
- Workload 3 (50,000 tickets × 25 fields): String 357.28 ms vs StringBuilder 94.43 ms (3.8x faster). String created 2,500,000 intermediate objects.
- Recommendation: Use `StringBuilder` for local ticket construction and thread-safe queues or buffers for shared logs.
