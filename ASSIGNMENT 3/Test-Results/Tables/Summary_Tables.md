# Comprehensive Test Results and Measurement Tables (Q1 - Q7)

**Student Name:** Kattiripalli Dhanush  
**Register Number:** 192311094  
**Course Outcome:** CO3 – Exception Handling & Multithreading  

---

## Question 1: Bank ATM Simulation (q1_atm_tests.csv)
| TestCase | Description | InputFile | InputAmount | Outcome | CatchBlockExecuted | FinallyExecuted |
|---|---|---|---|---|---|---|
| Test 1: Valid Withdrawal | Normal valid cash withdrawal | valid_card.txt | 5000 | SUCCESS | NONE | true |
| Test 2: Non-numeric Input | Letters typed instead of numbers | valid_card.txt | five_thousand | FAILED_NUMBER_FORMAT | catch (NumberFormatException) | true |
| Test 3: Negative Amount | Negative withdrawal value entered | valid_card.txt | -1500 | FAILED_ILLEGAL_AMOUNT | catch (IllegalArgumentException) | true |
| Test 4: Insufficient Funds | Amount exceeding available account balance | valid_card.txt | 35000 | FAILED_INSUFFICIENT_FUNDS | catch (FileNotFoundException \| InsufficientFundsException) | true |
| Test 5: Missing Card Reader | Card reader file or hardware config missing | missing_card_reader.txt | 2000 | FAILED_CARD_READ | catch (FileNotFoundException \| InsufficientFundsException) | true |

**Benchmark:** Error Codes = 168.14 ms | Exceptions = 204.39 ms | Exception Overhead = 1.22x

---

## Question 2: Online Examination Portal (q2_exam_tests.csv)
| Scenario | StudentId | Condition | ExceptionThrown | ExceptionCategory | RootCausePreserved | ResponseStatus |
|---|---|---|---|---|---|---|
| 1. Valid Submission | 192311094 | Within window, registered, 250KB | None | N/A | N/A | 200 OK: Submission successful for answer01.pdf |
| 2. Unregistered Student | 192399999 | Student ID not in DB | StudentNotRegisteredException | Unchecked (RuntimeException) | No Cause Needed | 403 Forbidden: Access Denied |
| 3. Window Elapsed | 192311094 | Submission after 10:00 AM | ExamWindowClosedException | Checked (PortalException) | No Cause Needed | 400 Window Closed: Submission Rejected |
| 4. Storage Quota Exceeded (Chained) | 192311094 | File 850KB > 500KB | FileStorageException | Checked (PortalException) | Yes (IOException preserved) | 500 Storage Failed: Could not store answer02.pdf |
| 5. Storage Quota Exceeded (Lost Cause) | 192311094 | File 850KB > 500KB | FileStorageException | Checked (PortalException) | No (cause=null, lost) | 500 Storage Failed: Cause [LOST / NULL] |

---

## Question 3: Daily Sales Report Resource Safety (q3_resource_handles.csv)
| Approach | TotalFilesOpened | TotalFilesClosed | LeakedHandlesRemaining | ExecutionTimeNs | ExecutionTimeMs | SafeAgainstExceptions |
|---|---|---|---|---|---|---|
| Manual_TryCatchFinally | 4 | 4 | 0 (when tracked in list) | 112,416,200 | 112.416 ms | No (Vulnerable to masking & leaks) |
| TryWithResources | 5 | 5 | 0 (Deterministic) | 20,948,600 | 20.949 ms | Yes (Reverse closing, suppressed exc) |

---

## Question 4: Scanner Stations Finish Orders (q4_finish_orders.csv)
| RunNumber | Station1FinishPos | Station2FinishPos | Station3FinishPos | Station4FinishPos | FullFinishSequence |
|---|---|---|---|---|---|
| 1 | 4 | 3 | 1 | 2 | [3 -> 4 -> 2 -> 1] |
| 2 | 2 | 4 | 1 | 3 | [3 -> 1 -> 4 -> 2] |
| 3 | 1 | 2 | 4 | 3 | [1 -> 2 -> 4 -> 3] |
| 4 | 1 | 3 | 2 | 4 | [1 -> 3 -> 2 -> 4] |
| 5 | 1 | 2 | 4 | 3 | [1 -> 2 -> 4 -> 3] |
| 6 | 4 | 1 | 2 | 3 | [2 -> 3 -> 4 -> 1] |
| 7 | 4 | 2 | 1 | 3 | [3 -> 2 -> 4 -> 1] |
| 8 | 4 | 3 | 2 | 1 | [4 -> 3 -> 2 -> 1] |
| 9 | 3 | 1 | 4 | 2 | [2 -> 4 -> 1 -> 3] |
| 10 | 3 | 2 | 1 | 4 | [3 -> 2 -> 1 -> 4] |

---

## Question 5: Cinema Seat Booking Race & Benchmarks (q5_race_runs.csv & q5_benchmarks.csv)
### 10 Unsynchronized Runs:
| RunNumber | TotalSeats | TotalThreads | SeatsSoldObserved | DoubleBookingsDetected | Status |
|---|---|---|---|---|---|
| 1 | 10 | 100 | 31 | 21 | DOUBLE_BOOKING_RACE_OCCURRED |
| 2 | 10 | 100 | 21 | 11 | DOUBLE_BOOKING_RACE_OCCURRED |
| 3 | 10 | 100 | 20 | 10 | DOUBLE_BOOKING_RACE_OCCURRED |
| 4 | 10 | 100 | 20 | 10 | DOUBLE_BOOKING_RACE_OCCURRED |
| 5 | 10 | 100 | 22 | 12 | DOUBLE_BOOKING_RACE_OCCURRED |
| 6 | 10 | 100 | 11 | 1 | DOUBLE_BOOKING_RACE_OCCURRED |
| 7 | 10 | 100 | 14 | 4 | DOUBLE_BOOKING_RACE_OCCURRED |
| 8 | 10 | 100 | 20 | 10 | DOUBLE_BOOKING_RACE_OCCURRED |
| 9 | 10 | 100 | 18 | 8 | DOUBLE_BOOKING_RACE_OCCURRED |
| 10 | 10 | 100 | 18 | 8 | DOUBLE_BOOKING_RACE_OCCURRED |

### 10,000 Bookings Benchmark:
| Strategy | TotalRequests | SeatsCount | ExecutionTimeNs | ExecutionTimeMs | CorrectSeatsSold | LockType |
|---|---|---|---|---|---|---|
| Synchronized_Method | 10000 | 100 | 12,408,800 | 12.41 ms | 100 | Coarse-Grained (Screen Instance Monitor) |
| Fine_Grained_SyncBlock | 10000 | 100 | 10,509,800 | 10.51 ms | 100 | Fine-Grained (Per-Seat Object Monitor) |
| Lock_Free_AtomicCAS | 10000 | 100 | 12,494,900 | 12.49 ms | 100 | Hardware CPU CAS (compareAndSet) |

---

## Question 6: Inter-Thread Coordination & Deadlock
- **TokenCounter Expected Sum:** 210 | **Observed Sum:** 210 (100% Match)
- **TokenCounter with 'if':** Sum corrupted to 27 | Buffer underflow detected!
- **Deadlock:** Verified thread circular blocking between accounts 101 and 102.
- **Deadlock-Free:** Consistent lock ordering successfully completed all transfers ($Rs. 900.00$ and $Rs. 1100.00$ balances).

---

## Question 7: Semester Result Processor Scaling (q7_thread_scaling.csv)
| ThreadPoolSize | TotalFiles | SuccessCount | FailureCount | MissingFiles | CorruptFiles | ExecutionTimeMs | ThroughputFilesPerSec | SpeedupRatio |
|---|---|---|---|---|---|---|---|---|
| 1 | 10000 | 9500 | 500 | 200 | 300 | 7,907 ms | 1,264.72 files/s | 1.00x |
| 2 | 10000 | 9500 | 500 | 200 | 300 | 5,068 ms | 1,973.16 files/s | 1.56x |
| 4 | 10000 | 9500 | 500 | 200 | 300 | 7,526 ms | 1,328.73 files/s | 1.05x |
| 8 | 10000 | 9500 | 500 | 200 | 300 | 3,184 ms | 3,140.70 files/s | 2.48x |
