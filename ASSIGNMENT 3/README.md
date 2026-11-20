# ☕ Programming in Java — Assignment 3 (CO3)

<p align="center">
  <b>Java Programming | Exception Handling & Multithreading</b><br>
  <i>Individual Execution — Design, Implementation, Testing & Analysis</i>
</p>

---

## 🎓 Student Information

| Field | Details |
|---|---|
| **Student Name** | Gurazala Uday Siva Sai |
| **Register Number** | 192425259 |
| **Department** | Artificial Intelligence & Machine Learning (AI&ML) |
| **Institution** | Saveetha School of Engineering (SIMATS) |
| **Course Title** | Java Programming |
| **Course Outcome** | CO3 |
| **Execution Mode** | Individual Execution |
| **Java Environment** | Java SE 27 (27.0.1.0.3+1-2) |
| **Operating System** | Windows 11 (64-bit AMD64) |
| **Repository** | [CSA0913-JAVA](https://github.com/udaygurazala07/CSA0913-JAVA) |
| **Assignment Report** | [Assignment3-Report.pdf](./Report/Assignment3-Report.pdf) |

---

## 📌 Course Outcome (CO3)

**CO3 – Exception Handling and Multithreading**

This assignment focuses on implementing and analyzing advanced Java programming concepts, including:

### Exception Handling
- Exception hierarchy and Throwable class
- Checked and unchecked exceptions
- try-catch-finally blocks
- User-defined exceptions
- Exception chaining and propagation
- Try-with-resources
- Debugging and stack trace analysis

### Multithreading
- Thread life cycle
- Thread class and Runnable interface
- Thread methods and priorities
- Synchronization and race conditions
- Deadlock and prevention
- Inter-thread communication
- Executor framework
- Callable and Future
- Concurrent collections

---

# 1. Executive Summary & Contribution Matrix

In accordance with the mandatory pair-work specifications of CO3, every question requires evidence across four stages:

**Design → Implementation → Analysis → Evaluation**

Since this assignment was executed individually, all seven questions were independently designed, implemented, tested, traced, and analyzed by the student.

## Individual Contribution Record

| Question | Student A – Design & Architecture | Student B – Testing & Experiments | Status |
|---|---|---|---|
| **Q1** | Throwable hierarchy, failure-point table, catch ordering, finally guarantee | 5 test inputs, catch/finally execution, stack trace, incorrect catch order | 100% Completed |
| **Q2** | Custom exception hierarchy, checked vs unchecked, 3-tier propagation | 5 failure scenarios, exception chaining, lost cause analysis | 100% Completed |
| **Q3** | Debugging, resource leak localization, file handle tracking | Manual leak testing, reverse closing, suppressed exceptions | 100% Completed |
| **Q4** | Thread vs Runnable, lifecycle states, run() vs start(), join() | 10 repeated runs, priority experiments, daemon termination | 100% Completed |
| **Q5** | Race conditions, double bookings, critical sections | 10-run collision records, 10,000 booking benchmark | 100% Completed |
| **Q6** | wait(), notify(), notifyAll(), producer-consumer synchronization | Deadlock construction, Coffman conditions, lock ordering | 100% Completed |
| **Q7** | ExecutorService, Callable, Future, ConcurrentHashMap, LongAdder | 10,000-file workload, scaling benchmark, ground truth verification | 100% Completed |

---

# 2. Repository Directory Structure

```text
Java-Assignment-3/
│
├── README.md
├── .gitignore
│
├── Report/
│   └── Assignment3-Report.pdf
│
├── Theory-Answers/
│   ├── Q1-ATM-Exception-Handling.md
│   ├── Q2-Custom-Exceptions.md
│   ├── Q3-Resource-Safety.md
│   ├── Q4-Thread-Life-Cycle.md
│   ├── Q5-Seat-Booking.md
│   ├── Q6-Deadlock-Producer-Consumer.md
│   └── Q7-Concurrent-Processor.md
│
├── Q1/
│   ├── AtmWithdrawal.java
│   ├── AtmIncorrectCatchOrder.java
│   ├── AtmStacktraceDemo.java
│   ├── AtmErrorCodesAlternative.java
│   ├── valid_card.txt
│   ├── q1_results.csv
│   └── q1_output.txt
│
├── Q2/
│   ├── StorageDemo.java
│   ├── ExamPortal.java
│   ├── q2_results.csv
│   ├── q2_storage_output.txt
│   └── q2_output.txt
│
├── Q3/
│   ├── SalesReportManual.java
│   ├── SalesReportTryWithResources.java
│   ├── SuppressedExceptionDemo.java
│   ├── ResourceBenchmark.java
│   ├── store1.txt
│   ├── store2.txt
│   ├── store3_corrupt.txt
│   ├── store4.txt
│   ├── q3_results.csv
│   └── q3_output.txt
│
├── Q4/
│   ├── ScannerStationThread.java
│   ├── ScannerStationRunnable.java
│   ├── ScannerStationRunVsStart.java
│   ├── ScannerStationDaemonDemo.java
│   ├── ScannerStationBenchmark.java
│   ├── q4_finish_orders.csv
│   ├── q4_priorities.csv
│   └── q4_output.txt
│
├── Q5/
│   ├── SeatBooking.java
│   ├── SeatBookingBenchmark.java
│   ├── q5_race_runs.csv
│   ├── q5_benchmarks.csv
│   ├── q5_todo_output.txt
│   └── q5_output.txt
│
├── Q6/
│   ├── TokenCounter.java
│   ├── TokenCounterIfBug.java
│   ├── WalletTransferDeadlock.java
│   ├── WalletTransferSafe.java
│   ├── TokenCounterBenchmark.java
│   ├── q6_token_trace.csv
│   ├── q6_token_output.txt
│   └── q6_output.txt
│
├── Q7/
│   ├── DatasetGenerator.java
│   ├── SemesterResultProcessor.java
│   ├── q7_thread_scaling.csv
│   └── q7_output.txt
│
└── Test-Results/
    ├── CSV/
    ├── Graphs/
    ├── Tables/
    └── Screenshots/
```

---

# 3. How to Compile and Execute

All commands should be executed from the `Java-Assignment-3/` root directory using Windows PowerShell.

## Q1 – Bank ATM Simulation

**Concepts:** Exception hierarchy, multi-catch, finally, stack traces, and error codes.

```powershell
cd Q1

javac -d . AtmWithdrawal.java AtmIncorrectCatchOrder.java AtmStacktraceDemo.java AtmErrorCodesAlternative.java

java q1.AtmWithdrawal
java q1.AtmErrorCodesAlternative

cd ..
```

**Experimental Results:**
- Tested 5 failure scenarios.
- Multi-catch handled disjoint checked exceptions.
- Finally block executed in 100% of tested cases.
- Error codes: 168.14 ms.
- Exceptions: 204.39 ms.
- Exception handling showed approximately 1.22x overhead.

## Q2 – Online Examination Portal

**Concepts:** Custom exceptions, exception chaining, checked vs unchecked exceptions, and layered architecture.

```powershell
cd Q2

javac -d . StorageDemo.java ExamPortal.java

java q2.StorageDemo
java q2.ExamPortal

cd ..
```

**Expected TODO 1 Output:**

```text
Saved answer01.pdf
Error: Could not store answer02.pdf
Cause: Disk quota exceeded
```

**Experimental Results:**
- Completed StorageDemo.java TODO 1.
- Implemented a three-tier architecture.
- Demonstrated exception propagation through Controller, Service, and Storage layers.
- Verified exception chaining using `getCause()`.

## Q3 – Daily Sales Report Resource Safety

**Concepts:** File handling, resource leaks, try-with-resources, and suppressed exceptions.

```powershell
cd Q3

javac -d . SalesReportManual.java SalesReportTryWithResources.java SuppressedExceptionDemo.java ResourceBenchmark.java

java q3.SalesReportManual
java q3.SalesReportTryWithResources
java q3.SuppressedExceptionDemo
java q3.ResourceBenchmark

cd ..
```

**Experimental Results:**
- Manual resource handling reached 4 simultaneously open handles.
- Try-with-resources bounded open handles to 2 or fewer.
- Resources closed in reverse order.
- Closing failures were recorded as suppressed exceptions.
- Try-with-resources was approximately 5.36x faster in the recorded benchmark.

## Q4 – Answer-Script Scanning & Thread Life Cycle

**Concepts:** Thread class, Runnable interface, thread states, priorities, daemon threads, and join().

```powershell
cd Q4

javac -d . ScannerStationThread.java ScannerStationRunnable.java ScannerStationRunVsStart.java ScannerStationDaemonDemo.java ScannerStationBenchmark.java

java q4.ScannerStationThread
java q4.ScannerStationRunnable
java q4.ScannerStationRunVsStart
java q4.ScannerStationDaemonDemo
java q4.ScannerStationBenchmark

cd ..
```

**Experimental Results:**
- Demonstrated `run()` vs `start()`.
- Tracked all 6 Java thread states.
- Performed 10 repeated execution runs.
- Observed different finishing orders due to scheduler nondeterminism.
- MAX_PRIORITY won 60% of recorded runs, but thread priority does not guarantee execution order.
- Demonstrated daemon thread termination when non-daemon execution ends.

## Q5 – Cinema Seat Booking Race Conditions

**Concepts:** Race conditions, synchronization, intrinsic locks, and atomic operations.

```powershell
cd Q5

javac -d . SeatBooking.java SeatBookingBenchmark.java

java q5.SeatBooking
java q5.SeatBookingBenchmark

cd ..
```

**Expected TODO 1 Output:**

```text
Seats sold: 10
```

**Experimental Results:**
- Completed SeatBooking.java TODO 1.
- Unsynchronized execution produced 11–31 reported seat sales.
- Recorded up to 21 additional sales per run.
- Tested 10,000 booking operations.

| Synchronization Strategy | Execution Time |
|---|---:|
| Synchronized Method | 12.41 ms |
| Fine-Grained Synchronized Block | 10.51 ms |
| Atomic CAS | 12.49 ms |

**Observation:** Fine-grained synchronization achieved the lowest recorded latency in this benchmark.

## Q6 – Deadlock & Inter-Thread Communication

**Concepts:** wait(), notify(), notifyAll(), producer-consumer, deadlock, and synchronization.

```powershell
cd Q6

javac -d . TokenCounter.java TokenCounterIfBug.java WalletTransferDeadlock.java WalletTransferSafe.java TokenCounterBenchmark.java

java q6.TokenCounter
java q6.TokenCounterIfBug
java q6.WalletTransferDeadlock
java q6.WalletTransferSafe
java q6.TokenCounterBenchmark

cd ..
```

**Expected TODO Output:**

```text
Sum of tokens collected: 210 (expected 210)
```

**Experimental Results:**
- Completed TokenCounter.java TODO 1 and TODO 2.
- Demonstrated the importance of using `while` instead of `if` for guarded waiting.
- The incorrect implementation produced a corrupted sum of 27 instead of 210.
- Reproduced circular-wait deadlock using wallet transfers.
- Prevented deadlock through consistent lock ordering by Account ID.
- ArrayBlockingQueue(5) completed the recorded benchmark in 6.99 ms.

## Q7 – Concurrent Fault-Tolerant Result Processor

**Concepts:** ExecutorService, Callable, Future, ConcurrentHashMap, LongAdder, and concurrent processing.

```powershell
cd Q7

javac -d . DatasetGenerator.java SemesterResultProcessor.java

java q7.SemesterResultProcessor

cd ..
```

**Experimental Results:**
- Verified a 20-file control dataset.
- 14 successful files.
- 3 corrupt files.
- 3 missing files.
- Processed a workload of 10,000 files.
- Tested thread pools containing 1, 2, 4, and 8 threads.
- Achieved zero unhandled exceptions across the recorded workload, including 500 bad files.

### Thread Scaling Benchmark

| Thread Pool | Execution Time | Throughput | Speedup |
|---|---:|---:|---:|
| 1 Thread | 7,907 ms | 1,264.7 files/s | 1.00x |
| 2 Threads | 5,068 ms | 1,973.2 files/s | 1.56x |
| 4 Threads | 7,526 ms | 1,328.7 files/s | 1.05x |
| 8 Threads | 3,184 ms | 3,140.7 files/s | 2.48x |

**Observation:** The 8-thread configuration achieved the highest throughput in the recorded experiment. Performance did not scale linearly because of filesystem and directory contention.

---

# 4. Key Experimental Graphs

All experimental graphs are stored in:

`Test-Results/Graphs/`

| No. | Graph File | Description |
|---|---|---|
| 1 | `q1_error_codes_vs_exceptions.png` | Exception creation overhead comparison |
| 2 | `q3_resource_handles_comparison.png` | Peak file handles and execution time |
| 3 | `q4_thread_finish_order_distribution.png` | Thread scheduling nondeterminism |
| 4 | `q5_race_condition_double_bookings.png` | Unsynchronized double-booking analysis |
| 5 | `q5_synchronization_strategies_benchmark.png` | Synchronization strategy comparison |
| 6 | `q6_producer_consumer_buffer_trace.png` | Producer-consumer buffer size trace |
| 7 | `q7_thread_scaling_throughput_speedup.png` | Thread scaling and throughput |

---

# 5. Verification Checklist

- [x] All 7 questions completed.
- [x] All theoretical answers documented in Markdown.
- [x] Every assignment TODO completed and checked against expected outputs.
- [x] Java programs compiled using Java SE 27.
- [x] Experimental results recorded in CSV files.
- [x] Performance
