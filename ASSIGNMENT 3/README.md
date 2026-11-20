# Programming in Java — Assignment 3 (CO3)

**Course Title:** Java Programming  
**Course Outcome:** CO3 – Exception handling (hierarchy, checked/unchecked, try-catch-finally, user-defined exceptions, chaining, try-with-resources, debugging) and Multithreading (thread life cycle, Thread and Runnable, thread methods, synchronization, deadlock, inter-thread communication, Executor framework, Callable and Future, and concurrent collections).  
**Student Name:** Kattiripalli Dhanush  
**Register Number:** 192311094  
**GitHub Username:** Dhanushssmb  
**Repository:** [https://github.com/Dhanushssmb/java.git](https://github.com/Dhanushssmb/java.git)  
**Execution Mode:** Individual Execution (Fulfilling both Student A – Design/Architecture and Student B – Testing/Experiments/Tracing tracks across all seven questions)  
**Java Environment:** Java SE 27 (build 27.0.1.0.3+1-2, mixed mode, sharing)  
**Operating System:** Microsoft Windows 11 (64-bit AMD64)  
**Assignment Report PDF:** [Assignment3-Report.pdf](./Report/Assignment3-Report.pdf)  

---

## 1. Executive Summary & Contribution Matrix

In accordance with the mandatory pair-work specifications of CO3, every question requires evidence across **Design → Implementation → Analysis → Evaluation**. Since this submission was executed individually, Kattiripalli Dhanush (192311094) has independently designed, implemented, tested, traced, and analyzed all seven tasks.

### Individual Contribution Record
| Question | Student A – Primary Responsibility | Student B – Primary Responsibility | Joint Responsibilities | Individual Execution Status |
|---|---|---|---|---|
| **Q1** | Throwable hierarchy, failure-point table, catch ordering justification, finally block guarantee | 5 test inputs, catch/finally execution recording, uncaught stack trace dissection, wrong catch order demo | Single `catch (Exception e)` evaluation, error codes vs exceptions benchmark | **Executed 100% by Kattiripalli Dhanush** |
| **Q2** | Custom exception hierarchy class diagram, checked vs unchecked rationale, 3-tier propagation path | 5 failure scenarios, `getCause()` chaining demonstration, lost cause analysis, test matrix | Storage abstraction justification, `StorageDemo.java` TODO 1 completion | **Executed 100% by Kattiripalli Dhanush** |
| **Q3** | Step-through debugging, leak point localization, failing input crafting, file handle counters, logging | Resource identification, manual leak explanation, try-with-resources reverse closing, suppressed exceptions | Try-with-resources robustness limits, file handle complexity analysis | **Executed 100% by Kattiripalli Dhanush** |
| **Q4** | Thread vs Runnable comparison, 6 lifecycle states diagram, `run()` vs `start()` evidence, `join()` coordination | 10 repeated runs finish order recording, thread priority experiments, daemon thread termination | Scheduler nondeterminism analysis, ExecutorService fixed pool analysis | **Executed 100% by Kattiripalli Dhanush** |
| **Q5** | Reproduce race condition & double bookings, 10-run collision records, step-by-step interleaving, critical section | High-throughput benchmark (10k bookings), method vs block locking, static sync, intrinsic monitors | 20-screen cinema deployment strategy, `SeatBooking.java` TODO 1 completion | **Executed 100% by Kattiripalli Dhanush** |
| **Q6** | `wait()` in while loop & synchronized block, `notify()` vs `notifyAll()` lost signal, buffer size trace [0, 5], sleep vs wait | Reliable deadlock construction, 4 Coffman conditions, consistent lock ordering prevention rule | Deadlock & producer-consumer commonalities, `TokenCounter.java` TODO 1 & 2 | **Executed 100% by Kattiripalli Dhanush** |
| **Q7** | Stage 2: `ExecutorService` + `Callable` + `Future` architecture, `ConcurrentHashMap` & `LongAdder`, class diagram | Stage 3: Implementation, 10k workload injection, scaling benchmark (1, 2, 4, 8 threads), 20-file ground truth | Stage 1: Problem interpretation; Stage 4: Optimal pool size defense & Amdahl's Law | **Executed 100% by Kattiripalli Dhanush** |

---

## 2. Directory Structure

```
Java-Assignment-3/
├── README.md                                    # Master documentation & compilation guide
├── .gitignore                                   # Git configuration ignoring binaries and large datasets
├── Report/
│   └── Assignment3-Report.pdf                  # Comprehensive formal college PDF report
├── Theory-Answers/                              # In-depth theoretical write-ups (10-section format)
│   ├── Q1-ATM-Exception-Handling.md             # ATM failure points, Throwable tree, error codes
│   ├── Q2-Custom-Exceptions.md                  # 3-tier portal, chaining, checked vs unchecked
│   ├── Q3-Resource-Safety.md                    # File descriptors, try-with-resources, suppressed exceptions
│   ├── Q4-Thread-Life-Cycle.md                  # Thread states, run vs start, scheduler nondeterminism
│   ├── Q5-Seat-Booking.md                       # Race conditions, double booking, intrinsic locks
│   ├── Q6-Deadlock-Producer-Consumer.md         # Coffman conditions, lock ordering, guarded suspension
│   └── Q7-Concurrent-Processor.md               # 10k scripts, Executor framework, fault tolerance
├── Q1/                                          # Bank ATM Simulation
│   ├── AtmWithdrawal.java                       # Main simulation with multi-catch & finally
│   ├── AtmIncorrectCatchOrder.java              # Demonstration of compiler error on wrong catch order
│   ├── AtmStacktraceDemo.java                   # Uncaught stack trace generator
│   ├── AtmErrorCodesAlternative.java            # Defensive validation benchmark (100k iterations)
│   ├── valid_card.txt                           # Mock hardware card configuration
│   ├── q1_results.csv                           # 5 test scenarios execution results
│   └── q1_output.txt                            # Raw terminal execution capture
├── Q2/                                          # Online Examination Portal
│   ├── StorageDemo.java                         # TODO 1 completed (Expected: Saved / Error / Cause)
│   ├── ExamPortal.java                          # Full 3-layer architecture (Controller, Service, Storage)
│   ├── q2_results.csv                           # 5 submission scenarios test matrix
│   ├── q2_storage_output.txt                    # Verified TODO 1 output
│   └── q2_output.txt                            # Chained stack trace & propagation logs
├── Q3/                                          # Daily Sales Report Resource Safety
│   ├── SalesReportManual.java                   # Manual try-catch-finally with tracked file handles
│   ├── SalesReportTryWithResources.java         # Try-with-resources with reverse closing & logging
│   ├── SuppressedExceptionDemo.java             # Demonstration of Throwable.addSuppressed()
│   ├── ResourceBenchmark.java                   # Comparative benchmark of handle safety & time
│   ├── store1.txt, store2.txt, store4.txt       # Valid store data files
│   ├── store3_corrupt.txt                       # Intentionally corrupt data file (NaN / Invalid)
│   ├── q3_results.csv                           # Benchmark metrics table
│   └── q3_output.txt                            # Raw terminal logs
├── Q4/                                          # Answer-Script Scanning & Thread Life Cycle
│   ├── ScannerStationThread.java                # Stations extending java.lang.Thread
│   ├── ScannerStationRunnable.java              # Stations implementing java.lang.Runnable
│   ├── ScannerStationRunVsStart.java            # Concrete runtime proof of run() vs start()
│   ├── ScannerStationDaemonDemo.java            # Demonstration of abrupt daemon thread termination
│   ├── ScannerStationBenchmark.java             # 10-run finish order & priority experiment harness
│   ├── q4_finish_orders.csv                     # 10 runs sequence records
│   ├── q4_priorities.csv                        # Thread priority experiment records
│   └── q4_output.txt                            # Terminal execution logs
├── Q5/                                          # Cinema Seat-Booking Race Conditions
│   ├── SeatBooking.java                         # TODO 1 completed (Expected: Seats sold: 10)
│   ├── SeatBookingBenchmark.java                # 10-run unsynchronized race & 10,000 bookings benchmark
│   ├── q5_race_runs.csv                         # 10 runs double-booking measurements
│   ├── q5_benchmarks.csv                        # High-throughput synchronization comparison
│   ├── q5_todo_output.txt                       # Verified TODO 1 output
│   └── q5_output.txt                            # Terminal logs
├── Q6/                                          # Deadlock & Inter-Thread Communication
│   ├── TokenCounter.java                        # TODO 1 & 2 completed (Expected: Sum: 210)
│   ├── TokenCounterIfBug.java                   # Empirical proof of while vs if bug (underflow)
│   ├── WalletTransferDeadlock.java              # Reliable circular deadlock demonstration
│   ├── WalletTransferSafe.java                  # Deadlock prevention via consistent lock ordering
│   ├── TokenCounterBenchmark.java               # Bounded buffer trace & ArrayBlockingQueue alternative
│   ├── q6_token_trace.csv                       # Buffer size trace over 40 events
│   ├── q6_token_output.txt                      # Verified TODO 1 & 2 output
│   └── q6_output.txt                            # Terminal logs
├── Q7/                                          # Concurrent Fault-Tolerant Result Processor
│   ├── DatasetGenerator.java                    # Generator for 20-file control & 10,000-file workloads
│   ├── SemesterResultProcessor.java             # Full concurrent fault-tolerant engine
│   ├── q7_thread_scaling.csv                    # 1, 2, 4, 8 threads scaling benchmark metrics
│   └── q7_output.txt                            # Ground truth verification & scaling logs
└── Test-Results/
    ├── CSV/                                     # Raw experimental data files (Q1 to Q7)
    ├── Graphs/                                  # 7 High-resolution (300 DPI) publication-quality charts
    ├── Tables/                                  # Formatted markdown summary tables
    └── Screenshots/                             # Terminal execution captures
```

---

## 3. How to Compile and Execute Each Program

All commands should be executed from the `Java-Assignment-3/` root directory.

### Question 1: Bank ATM Simulation
```powershell
cd Q1
javac -d . AtmWithdrawal.java AtmIncorrectCatchOrder.java AtmStacktraceDemo.java AtmErrorCodesAlternative.java
java q1.AtmWithdrawal
java q1.AtmErrorCodesAlternative
cd ..
```
* **Result Note:** Tested 5 failure scenarios. Multi-catch handled disjoint checked exceptions per JLS §14.20. `finally` block executed in 100% of cases. Error codes ran in 168.14 ms vs 204.39 ms for exceptions (1.22x overhead due to `fillInStackTrace()`).

### Question 2: Online Examination Portal
```powershell
cd Q2
javac -d . StorageDemo.java ExamPortal.java
java q2.StorageDemo
java q2.ExamPortal
cd ..
```
* **Result Note:** Completed TODO 1. Output matched expected:
  ```
  Saved answer01.pdf
  Error: Could not store answer02.pdf
  Cause: Disk quota exceeded
  ```
  Examined 3-tier architecture with `ExamException` hierarchy and chained stack trace.

### Question 3: Daily Sales Report Resource Safety
```powershell
cd Q3
javac -d . SalesReportManual.java SalesReportTryWithResources.java SuppressedExceptionDemo.java ResourceBenchmark.java
java q3.SalesReportManual
java q3.SalesReportTryWithResources
java q3.SuppressedExceptionDemo
java q3.ResourceBenchmark
cd ..
```
* **Result Note:** Manual approach held up to 4 open handles simultaneously and leaked on unhandled parse crashes. Try-With-Resources (TWR) bounded open handles to $\le 2$, closed resources in reverse order, and attached closing errors as suppressed exceptions via `Throwable.getSuppressed()`. TWR was 5.36x faster.

### Question 4: Answer-Script Scanning & Thread Life Cycle
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
* **Result Note:** Proved `run()` executes on `main` thread synchronously, while `start()` creates a native OS thread. Tracked all 6 lifecycle states. 10 repeated runs produced varying finish orders due to OS scheduler nondeterminism. Setting `MAX_PRIORITY` gave priority boost (won 60% of runs) but remained non-guaranteed. Daemon thread terminated instantly when `main` ended.

### Question 5: Cinema Seat Booking Race Conditions
```powershell
cd Q5
javac -d . SeatBooking.java SeatBookingBenchmark.java
java q5.SeatBooking
java q5.SeatBookingBenchmark
cd ..
```
* **Result Note:** Completed TODO 1. Output matched expected:
  ```
  Seats sold: 10
  ```
  Unsynchronized version sold between 11 and 31 seats (up to 21 double bookings per run). Synchronized block with per-seat fine-grained locking achieved lowest latency (10.51 ms for 10,000 bookings), outperforming coarse synchronized method (12.41 ms) and lock-free CAS (12.49 ms).

### Question 6: Deadlock & Inter-Thread Communication
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
* **Result Note:** Completed TODO 1 and TODO 2. Output matched expected:
  ```
  Sum of tokens collected: 210 (expected 210)
  ```
  Changing `while` to `if` resulted in buffer underflow and sum corruption (27 instead of 210). Wallet transfer reproduced Coffman circular wait deadlock; consistent lock ordering by Account ID completely eliminated deadlocks. `ArrayBlockingQueue(5)` executed in 6.99 ms.

### Question 7: Concurrent Fault-Tolerant Result Processor
```powershell
cd Q7
javac -d . DatasetGenerator.java SemesterResultProcessor.java
java q7.SemesterResultProcessor
cd ..
```
* **Result Note:** Ground-truth control dataset of 20 files verified with 100% precision (14 successes, 3 corrupt, 3 missing). Processed full 10,000 files across thread pools of 1, 2, 4, 8 threads:
  * 1 Thread: 7,907 ms (1,264.7 files/s)
  * 2 Threads: 5,068 ms (1,973.2 files/s, 1.56x speedup)
  * 4 Threads: 7,526 ms (1,328.7 files/s, NTFS directory contention)
  * 8 Threads: 3,184 ms (3,140.7 files/s, 2.48x speedup)
  * Zero unhandled exceptions (100% fault-tolerant across all 500 bad files).

---

## 4. Key Experimental Graphs

All graphs are stored as high-resolution (300 DPI) images in [Test-Results/Graphs/](./Test-Results/Graphs/):
1. **`q1_error_codes_vs_exceptions.png`**: Exception creation overhead (1.22x slower than error codes due to stack traversal).
2. **`q3_resource_handles_comparison.png`**: Peak open file handles (4 vs 2) and execution time comparison (112.42 ms vs 20.95 ms).
3. **`q4_thread_finish_order_distribution.png`**: Frequency of finishing first across 10 runs illustrating scheduler nondeterminism.
4. **`q5_race_condition_double_bookings.png`**: Severe double booking (11 to 31 seats sold) across 10 unsynchronized runs.
5. **`q5_synchronization_strategies_benchmark.png`**: Latencies for 10,000 bookings across Coarse Method, Fine Block, and Atomic CAS.
6. **`q6_producer_consumer_buffer_trace.png`**: Step trace proving bounded buffer size strictly within $[0, 5]$.
7. **`q7_thread_scaling_throughput_speedup.png`**: Execution time reduction and throughput scaling up to 3,140 files/sec on 8 threads.

---

## 5. Verification Checklist
- [x] All 7 questions fully answered in comprehensive Markdown theory documents.
- [x] Every TODO from the PDF completed and verified against expected outputs.
- [x] Code compiled with `javac 27.0.1.0.3` without warnings or errors.
- [x] Empirical test results saved in CSV format with zero fabricated measurements.
- [x] Publication-quality charts generated from raw test data.
- [x] Full PDF report generated and stored in `Report/Assignment3-Report.pdf`.
- [x] Progressive Git commits created for each task preserving Assignment 1 & 2 untouched.
