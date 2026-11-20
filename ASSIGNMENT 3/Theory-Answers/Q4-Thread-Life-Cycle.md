# Question 4: Thread Creation and the Thread Life Cycle

**Course Outcome:** CO3 – Multithreading Fundamentals, Thread Life Cycle, Scheduling Nondeterminism, and Executor Framework  
**Student Name:** Kattiripalli Dhanush  
**Register Number:** 192311094  
**Role:** Individual Execution (Fulfilling both Student A – Design and Student B – Behavioral Experiments tracks)

---

## 1. Introduction
Concurrent programming enables a single process to execute multiple execution pathways simultaneously, maximizing modern multi-core CPU hardware utilization and preventing I/O blocking. In an examination evaluation center, thousands of student answer scripts must be digitized across multiple scanning stations. By modeling each scanner station as an independent concurrent **Thread**, scanning operations occur in parallel rather than sequentially. In Java, thread management is governed by the Java Virtual Machine (JVM) thread state machine and the underlying operating system thread scheduler.

---

## 2. Problem Explanation
The examination cell operates four parallel scanner stations (`Station 1`, `Station 2`, `Station 3`, `Station 4`). Each station scans a batch of scripts, pauses intermittently to simulate hardware scanning latency, and reports its count upon completion.
Key technical challenges and exploration goals:
1. Compare thread creation via `Thread` subclassing vs implementing the `Runnable` functional interface.
2. Track threads through their official six lifecycle states: `NEW`, `RUNNABLE`, `BLOCKED`, `WAITING`, `TIMED_WAITING`, and `TERMINATED`.
3. Provide concrete runtime proof demonstrating why invoking `run()` directly fails to create a thread, while `start()` spawns a native thread.
4. Demonstrate how `join()` guarantees that the grand total of scanned scripts is computed only after every station has completely finished.
5. Quantify the nondeterministic nature of the thread scheduler by measuring finish orders across 10 repeated executions and testing thread priorities (`MIN_PRIORITY` vs `MAX_PRIORITY`).
6. Observe JVM shutdown semantics with daemon threads.

---

## 3. Detailed Answer in Simple English

### 3.1 Thread Creation: `Thread` Class vs `Runnable` Interface
| Feature | Extending `java.lang.Thread` | Implementing `java.lang.Runnable` | Preferred Approach |
|---|---|---|---|
| **Inheritance Flexibility** | Consumes Java's single inheritance slot; class cannot extend any other parent class | Leaves the class free to extend other domain classes (e.g., `Station extends HardwareDevice implements Runnable`) | **`Runnable`** |
| **Separation of Concerns** | Tightly couples what work is executed (`run()`) with how it is executed (`Thread` machinery) | Clean separation: `Runnable` defines the task; `Thread` or `ExecutorService` acts as the execution runner | **`Runnable`** |
| **Thread Pool Compatibility** | Incompatible with thread pooling; thread instance cannot be submitted to an `ExecutorService` | Fully compatible with `ExecutorService`, `ForkJoinPool`, and modern reactive schedulers | **`Runnable`** |
| **Code Overhead** | Slightly less boilerplate for simple one-off classroom demos | Idiomatic in industry Java (lambda compatible: `() -> doWork()`) | **`Runnable`** |

---

### 3.2 Student A – Thread Design
As Student A, the focus is on lifecycle state modeling, execution semantics of `run()` vs `start()`, and coordination via `join()`.

#### Thread Life-Cycle State Diagram
A Java thread undergoes six states defined in `java.lang.Thread.State`:

```
          [ NEW ]
             │
             │ thread.start()
             ▼
        [ RUNNABLE ] ◄─────────────────────────────────────────────┐
        (Ready / Running in OS)                                    │
             │                                                     │
             ├──────► sleep(ms), wait(ms), join(ms) ──► [ TIMED_WAITING ]
             │                                                     │
             ├──────► object.wait(), thread.join() ───► [ WAITING ]│
             │                                                     │
             ├──────► waiting for synchronized lock ──► [ BLOCKED ]┘
             │
             │ run() method completes normally or throws unhandled exception
             ▼
       [ TERMINATED ] (Dead / Resources freed)
```

**State Transitions Observed in Scanner Stations:**
1. **`NEW`:** `ScannerStationThread station = new ScannerStationThread(1, 50);` The object exists on the Java heap, but no OS kernel thread is allocated. `isAlive()` returns `false`.
2. **`RUNNABLE`:** Invoking `station.start()` signals the JVM to allocate OS thread structures and enqueue the thread in the OS scheduler queue. `isAlive()` becomes `true`.
3. **`TIMED_WAITING`:** Inside the scanning loop, `Thread.sleep(5 + random.nextInt(11))` puts the station to sleep. It releases the CPU but remains alive in `TIMED_WAITING`.
4. **`WAITING`:** When the `main` thread calls `station.join()`, the `main` thread enters the `WAITING` state until the target station finishes.
5. **`TERMINATED`:** Once all 50 scripts are processed and the `run()` method returns, the thread enters `TERMINATED`. `isAlive()` becomes `false`.

#### Proof: Why Calling `run()` Does NOT Create a New Thread
In our `ScannerStationRunVsStart` execution:
```
Current Main Thread: 'main' (Thread ID: 3)

--- Case 1: Invoking worker.run() directly ---
Executing inside run() method. Active Thread: 'main' (Thread ID: 3)
Notice: Thread name did NOT change to Dedicated-Worker-1; it ran on 'main' synchronously!

--- Case 2: Invoking worker.start() ---
Executing inside run() method. Active Thread: 'Dedicated-Worker-2' (Thread ID: 26)
Notice: Thread name was 'Dedicated-Worker-2', proving a separate thread was spawned!
```
**Explanation:** `run()` is a standard Java instance method. Invoking `worker.run()` simply executes the method on the current calling thread (`main`) in sequential, synchronous order. In contrast, `start()` is a native method that invokes JVM internal routines (`JVM_StartThread`), creates an OS-level thread stack, registers it with the OS kernel, and schedules its `run()` method to execute asynchronously on the new thread.

#### How `join()` Ensures Synchronization and Correct Summary
Without `join()`, the `main` thread would continue immediately after issuing `start()` commands. Because the child scanner threads take $\approx 300\text{–}600\text{ ms}$ to scan scripts, the `main` thread would read the uncompleted totals (yielding $0$ or a partial count) and terminate the program prematurely. Calling `station.join()` halts the calling thread until the target thread's `isAlive()` becomes `false`, guaranteeing that all 200 scripts across the four stations have been processed.

---

### 3.3 Student B – Behavioural Experiments
As Student B, the focus is on empirical measurement: finish order variance, priority evaluation, daemon thread behavior, and scheduler dynamics.

#### Ten Repeated Runs: Finish Order Variance (Evidence from `q4_finish_orders.csv`)
All four stations were assigned identical workloads (30 scripts) with identical thread priority (`NORM_PRIORITY = 5`):

| Run # | Station 1 Finish Pos | Station 2 Finish Pos | Station 3 Finish Pos | Station 4 Finish Pos | Complete Finish Sequence |
|---|---|---|---|---|---|
| **Run 1** | 4 | 3 | 1 | 2 | `[3 -> 4 -> 2 -> 1]` |
| **Run 2** | 2 | 4 | 1 | 3 | `[3 -> 1 -> 4 -> 2]` |
| **Run 3** | 1 | 2 | 4 | 3 | `[1 -> 2 -> 4 -> 3]` |
| **Run 4** | 1 | 3 | 2 | 4 | `[1 -> 3 -> 2 -> 4]` |
| **Run 5** | 1 | 2 | 4 | 3 | `[1 -> 2 -> 4 -> 3]` |
| **Run 6** | 4 | 1 | 2 | 3 | `[2 -> 3 -> 4 -> 1]` |
| **Run 7** | 4 | 2 | 1 | 3 | `[3 -> 2 -> 4 -> 1]` |
| **Run 8** | 4 | 3 | 2 | 1 | `[4 -> 3 -> 2 -> 1]` |
| **Run 9** | 3 | 1 | 4 | 2 | `[2 -> 4 -> 1 -> 3]` |
| **Run 10** | 3 | 2 | 1 | 4 | `[3 -> 2 -> 1 -> 4]` |

**Observations:** Out of 10 runs, almost every single run produced a different finish sequence! Station 1 finished first in 3 runs; Station 2 finished first in 2 runs; Station 3 finished first in 4 runs; Station 4 finished first in 1 run.

#### Thread Priority Experiments (`q4_priorities.csv`)
We set Station 1 to `MIN_PRIORITY` (1), Stations 2 & 3 to `NORM_PRIORITY` (5), and Station 4 to `MAX_PRIORITY` (10):
- **Trial 1:** Winner was Station 3 (`[3 -> 1 -> 4 -> 2]`).
- **Trial 2:** Winner was Station 4 (`[4 -> 1 -> 2 -> 3]`).
- **Trial 3:** Winner was Station 3 (`[3 -> 2 -> 4 -> 1]`).
- **Trial 4:** Winner was Station 4 (`[4 -> 1 -> 2 -> 3]`).
- **Trial 5:** Winner was Station 4 (`[4 -> 1 -> 2 -> 3]`).

**Analysis:** Setting `MAX_PRIORITY` increases the likelihood of a thread receiving larger or more frequent CPU time slices (Station 4 won 60% of trials). However, Java thread priority is **merely a hint** to the operating system. Modern preemptive OS schedulers (such as the Windows CFS/Priority Preemptive scheduler) prevent thread starvation by periodically boosting lower-priority threads. Therefore, Java thread priorities **cannot** be relied upon for program correctness or deterministic sequencing.

#### Daemon Thread Experiment Evidence
When `ScannerStationDaemonDemo` was executed:
```
Thread set to Daemon: true
[MAIN THREAD] Doing 350 ms of scanning work...
  [DAEMON LOGGER] Periodic heartbeat tick #1 (Alive & Logging)...
  [DAEMON LOGGER] Periodic heartbeat tick #2 (Alive & Logging)...
  [DAEMON LOGGER] Periodic heartbeat tick #3 (Alive & Logging)...
[MAIN THREAD] Main thread has completed and will now exit.
[JVM NOTICE] With no non-daemon threads remaining, JVM halts instantly.
```
**Conclusion:** The daemon thread was abruptly killed right after tick 3. The JVM does not wait for daemon threads to finish. They are intended strictly for background background housekeeping (e.g., GC cleaners, telemetry heartbeats).

---

## 4. Joint Conclusion
**Question:** Why can the same multithreaded program produce different output orders on different runs, and why must a correct program not depend on that order?

**Answer:**
1. **Operating System Scheduling Nondeterminism:** The OS kernel uses preemptive time-slicing. Thread scheduling depends on external environmental factors: CPU core temperature, power states, background OS services, context-switch interrupts, hardware cache misses, and memory bus contention. Even a microsecond difference in when a thread is dispatched alters the execution interleaving.
2. **Design for Correctness:** Any concurrent program that relies on Thread A finishing before Thread B without explicit coordination primitives (such as `join()`, `CountDownLatch`, `CyclicBarrier`, or locks) suffers from an implicit **Race Condition**. A robust concurrent design must be structurally independent of execution order.

---

## 5. Alternative Concept and Complexity Analysis
### Alternative: `ExecutorService` with a Fixed Thread Pool
Instead of manually instantiating and starting `Thread` objects:
```java
ExecutorService pool = Executors.newFixedThreadPool(4);
for (int i = 0; i < 4; i++) {
    pool.submit(new ScannerTask(i));
}
pool.shutdown();
pool.awaitTermination(1, TimeUnit.HOURS);
```

#### Memory and Resource Trade-Offs: Manual Threads vs `ExecutorService`
For $n$ scripts divided into tasks processed by $p$ threads:
1. **Stack Memory Allocation:** In Java on 64-bit platforms, every `new Thread()` allocates a private native call stack (default `-Xss` is $1\text{ MB}$). Creating 10,000 individual `Thread` objects requires $10,000 \times 1\text{ MB} = 10\text{ GB}$ of native RAM, causing an immediate `OutOfMemoryError: unable to create native thread`.
2. **Fixed Thread Pool Efficiency:** An `ExecutorService` pre-allocates exactly $p$ worker threads (e.g., $p = 4$, consuming only $4\text{ MB}$ of stack memory) and reuses them to process all $n$ script tasks from an internal blocking queue.
3. **Completion Time:** Expected wall-clock time is $\mathcal{O}(n/p \cdot \bar{t}_{\text{script}})$, where $\bar{t}_{\text{script}}$ is the average time to scan one script. Thread pools eliminate thread creation and teardown overhead.

---

## 6. Time and Space Complexity

### Time Complexity
- **Sequential Execution (1 Station):** $\mathcal{O}(n)$ where $n$ is total scripts.
- **Concurrent Execution ($p$ Stations):** $\mathcal{O}(n/p)$ theoretical time on a $p$-core CPU, achieving near-linear speedup when tasks are CPU/I/O independent.
- **Coordination (`join()`):** $\mathcal{O}(p)$ time to join all station threads sequentially.

### Space Complexity
- **Heap Space:** $\mathcal{O}(p)$ for the task objects and shared counter references.
- **Native Stack Space:** $\mathcal{O}(p \times S_{\text{stack}})$ where $S_{\text{stack}} \approx 1\text{ MB}$ per thread. For $p=4$, total native stack footprint is only $\approx 4\text{ MB}$.

---

## 7. Conclusion
Question 4 proved that multithreading dramatically accelerates batched batch scanning by distributing workloads across parallel threads. Implementing `Runnable` ensures clean architectural decoupling and thread pool compatibility. The empirical experiments confirmed that thread execution order is inherently nondeterministic and that priorities cannot guarantee sequence, underscoring why synchronization primitives like `join()` are essential for deterministic results.
