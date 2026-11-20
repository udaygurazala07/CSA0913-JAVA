# Question 5: Race Conditions in a Cinema Seat-Booking System

**Course Outcome:** CO3 – Race Conditions, Critical Sections, Intrinsic Locks, and Synchronization Strategies  
**Student Name:** Kattiripalli Dhanush  
**Register Number:** 192311094  
**Role:** Individual Execution (Fulfilling both Student A – Producing/Explaining Race and Student B – Comparing Synchronization Strategies tracks)

---

## 1. Introduction
In multi-user transactional applications such as cinema ticketing, airline reservation, or stock trading, multiple concurrent threads frequently access shared mutable state. When two or more threads attempt to read and mutate the same variable simultaneously without coordination, the outcome depends on the nondeterministic execution interleaving dictated by the operating system thread scheduler. This bug is known as a **Race Condition**. In a cinema seat-booking portal, race conditions lead to the disastrous business failure of **Double Booking**, where the same physical cinema seat is sold to multiple independent customers.

---

## 2. Problem Explanation
A cinema app maintains a screen with 10 physical seats represented by `boolean[] booked`. When 100 concurrent user threads compete for seats, each user selects a seat index ($seat = i \pmod{10}$), checks whether it is available, and if free, marks it as booked and increments the total counter `bookingsMade`.
Without synchronization:
1. The read-check operation (`!booked[seat]`) and the write-mutate operation (`booked[seat] = true; bookingsMade++`) are **not atomic**.
2. Multiple threads can simultaneously evaluate the seat as unbooked, enter the booking block, and mark the same seat as sold multiple times.
3. The counter increment `bookingsMade++` is a compound 3-step bytecode operation (`ILOAD`, `IADD`, `ISTORE`) which itself suffers from lost updates under concurrent access.

---

## 3. Detailed Answer in Simple English

### 3.1 Step-by-Step Interleaving: Producing the Race
Consider two threads ($T_A$ and $T_B$) competing for physical Seat #3:

```
Time   Thread A (User 3)                       Thread B (User 13)                    Seat State (booked[3])
───────────────────────────────────────────────────────────────────────────────────────────────────────────
t0     Invokes book(3)                         -                                     false
t1     Checks: if (!booked[3]) -> TRUE         Invokes book(3)                       false
t2     Enters delay / preemption               Checks: if (!booked[3]) -> TRUE       false
t3     Preempted by OS scheduler               Enters delay / preemption             false
t4     Resumes; sets booked[3] = true          Preempted                             true
t5     Increments bookingsMade (0 -> 1)        Resumes; sets booked[3] = true        true  (OVERWRITTEN!)
t6     Returns TRUE (Seat 3 Sold to User 3!)   Increments bookingsMade (1 -> 2)      true
t7     -                                       Returns TRUE (Seat 3 Sold to User 13! COLLISION!)
```
Both customers receive confirmation receipts for the exact same physical seat!

---

### 3.2 Student A – Producing and Explaining the Race
As Student A, the focus is on reliably reproducing double booking, tracking collision statistics over repeated trials, and mathematically isolating the critical section.

#### Ten Unsynchronized Executions (Evidence from `q5_race_runs.csv`)
We executed the unsynchronized system with 100 concurrent threads competing for 10 seats across 10 independent runs:

| Run # | Total Physical Seats | Competing Threads | Observed Seats Sold | Double Bookings Detected | Failure Severity |
|---|---|---|---|---|---|
| **Run 1** | 10 | 100 | **31** | **21 double bookings** | Critical (310% overcapacity) |
| **Run 2** | 10 | 100 | **21** | **11 double bookings** | High (210% overcapacity) |
| **Run 3** | 10 | 100 | **20** | **10 double bookings** | High (200% overcapacity) |
| **Run 4** | 10 | 100 | **20** | **10 double bookings** | High (200% overcapacity) |
| **Run 5** | 10 | 100 | **22** | **12 double bookings** | High (220% overcapacity) |
| **Run 6** | 10 | 100 | **11** | **1 double booking** | Moderate (110% overcapacity) |
| **Run 7** | 10 | 100 | **14** | **4 double bookings** | Moderate (140% overcapacity) |
| **Run 8** | 10 | 100 | **20** | **10 double bookings** | High (200% overcapacity) |
| **Run 9** | 10 | 100 | **18** | **8 double bookings** | High (180% overcapacity) |
| **Run 10** | 10 | 100 | **18** | **8 double bookings** | High (180% overcapacity) |

**Conclusion:** In **100% of unsynchronized runs**, double booking occurred. The system sold up to 31 tickets for a 10-seat hall.

#### Identification of the Exact Critical Section
A **Critical Section** is a code region accessing shared mutable variables that must be executed atomically by at most one thread at any given time.
In `SeatBooking.java`, the exact critical section is:
```java
// CRITICAL SECTION BEGINS
if (!booked[seat]) {
    booked[seat] = true;
    bookingsMade++;
    return true;
}
// CRITICAL SECTION ENDS
```
This is a classic **Check-Then-Act** compound state transition. Because the check (`!booked[seat]`) and the mutation (`booked[seat] = true`) are separate operations, atomicity must be enforced using mutual exclusion locks.

---

### 3.3 Student B – Comparing Synchronization Strategies
As Student B, the focus is on benchmarking synchronization techniques under 10,000 requests, analyzing intrinsic locks, and evaluating coarse-grained vs fine-grained locking.

#### Complete the Code Solution (`SeatBooking.java`)
In `SeatBooking.java`, TODO 1 was completed by applying the `synchronized` keyword to the method:
```java
public synchronized boolean book(int seat) {
    if (!booked[seat]) {
        try { Thread.sleep(1); } catch (InterruptedException ignored) {}
        booked[seat] = true;
        bookingsMade++;
        return true;
    }
    return false;
}
```
**Execution Output:**
```
Seats sold: 10
```
This verified the exact output required by the PDF. Across all synchronized tests, seats sold never exceeded 10.

#### High-Throughput Contention Benchmark (10,000 Bookings)
From our actual execution of `SeatBookingBenchmark` across 50 concurrent threads submitting 10,000 booking requests for 100 seats:

| Synchronization Strategy | Intrinsic Lock Used | Execution Time (ns) | Execution Time (ms) | Correct Seats Sold | Lock Granularity |
|---|---|---|---|---|---|
| **Synchronized Method** | Screen instance monitor (`this`) | $12,408,800 \text{ ns}$ | **$12.41 \text{ ms}$** | 100 (100% correct) | Coarse-Grained (Entire Screen) |
| **Fine-Grained Sync Block** | Per-seat object monitor (`seatLocks[seat]`) | $10,509,800 \text{ ns}$ | **$10.51 \text{ ms}$** | 100 (100% correct) | Fine-Grained (Per Seat) |
| **Lock-Free Atomic CAS** | Hardware CPU compare-and-swap (`AtomicBoolean`) | $12,494,900 \text{ ns}$ | **$12.49 \text{ ms}$** | 100 (100% correct) | Lock-Free Hardware Primitive |

**Key Finding:** Fine-grained locking on `seatLocks[seat]` achieved the lowest latency ($10.51 \text{ ms}$), running **18% faster** than the coarse-grained synchronized method. Why? Under fine-grained locking, User A booking Seat 1 does not block User B booking Seat 2! Contention only occurs when two users book the *exact same seat*.

#### Intrinsic Locks (Monitors) and Static Synchronization
- **Instance Intrinsic Lock:** In `public synchronized boolean book(int seat)`, the thread acquires the intrinsic monitor lock of the `SeatBooking` object instance (`this`). Any thread attempting to book *any* seat on that screen must wait until the lock is released.
- **Static Synchronization:** A method declared `public static synchronized void book(...)` acquires the lock on the `Class` object (`SeatBooking.class`).
  * **When needed:** Static synchronization is required only when synchronizing access to **class-level static variables** shared across all screens—for instance, a centralized global ticket transaction sequence generator or total daily revenue counter for the entire cinema complex.
  * **When NOT to use:** Static synchronization should never be used for individual seat bookings, because locking `SeatBooking.class` would mean a user booking a ticket in Screen 1 would unnecessarily block an entirely unrelated user booking a movie in Screen 20!

---

## 4. Joint Engineering Decision
**Question:** Which synchronization strategy would you deploy for a cinema with 20 screens, and why?

**Answer:**
We would deploy **Screen-Isolated Fine-Grained Locking or `ConcurrentHashMap` with `AtomicBoolean[]`**:
1. **Screen Independence:** Each cinema screen must have its own distinct `ScreenBookingManager` instance. Lock contention in Audi 1 (e.g., *Avatar*) should have zero latency impact on Audi 20 (e.g., *Oppenheimer*).
2. **Per-Seat Locking vs Coarse Screen Lock:** Within each screen, maintaining an array of `AtomicBoolean` or `ReentrantLock[]` per seat ensures maximum throughput. Hundreds of users can simultaneously select different seats in parallel without thread contention.
3. **Lock Striping:** If memory overhead for 20 screens $\times$ 300 seats ($6,000$ lock objects) is a concern, **Lock Striping** (e.g., 16 locks per screen, hashing $seat \pmod{16}$) offers near fine-grained concurrency with minimal heap allocation.

---

## 5. Alternative Concept and Complexity Analysis
### Lock-Free Alternatives: `AtomicBoolean[]` and `ConcurrentHashMap`
1. **`AtomicBoolean[]` using `compareAndSet(false, true)`:**
   ```java
   public boolean book(int seat) {
       return booked[seat].compareAndSet(false, true);
   }
   ```
   Uses native CPU atomic instructions (`CMPXCHG` on x86-64). No thread ever enters the `BLOCKED` OS state. Threads do not undergo context switches or OS monitor acquisition.
2. **`ConcurrentHashMap<Integer, String>.putIfAbsent(seat, userId)`:**
   Stores the seat reservation mapping in a partitioned lock-striped hash table. If `putIfAbsent()` returns `null`, the reservation succeeded; otherwise, the seat was already claimed.

#### Space and Time Trade-Offs for $s$ Seats
| Approach | Time per Booking (Contended) | Extra Space for $s$ Seats | Complexity / Scalability |
|---|---|---|---|
| `boolean[]` + `synchronized` method | $\mathcal{O}(T)$ (High wait times with $T$ threads) | $\mathcal{O}(1)$ extra space | Simplest code; poor concurrency under high user load |
| `boolean[]` + `seatLocks[s]` | $\mathcal{O}(1)$ (Contention only on same seat) | $\mathcal{O}(s)$ lock objects ($s \times 16$ bytes) | High throughput; moderate memory footprint |
| `AtomicBoolean[s]` | $\mathcal{O}(1)$ lock-free CAS | $\mathcal{O}(s)$ wrapper objects ($s \times 24$ bytes) | Maximum throughput; zero thread blocking |
| `ConcurrentHashMap` | $\mathcal{O}(1)$ average | $\mathcal{O}(k)$ where $k \le s$ booked seats | Dynamic memory; tracks user ID per seat directly |

---

## 6. Time and Space Complexity

### Time Complexity
- **Unsynchronized:** $\mathcal{O}(1)$ per operation, but mathematically **corrupt and incorrect**.
- **Synchronized Method:** $\mathcal{O}(1)$ amortized per operation, but serializes all concurrent bookings, degrading throughput to $\mathcal{O}(T)$ queue latency under $T$ competing threads.
- **Fine-Grained / Atomic CAS:** $\mathcal{O}(1)$ per operation with parallel throughput proportional to CPU cores.

### Space Complexity
- **Basic Array:** $\mathcal{O}(s)$ space for $s$ seats ($1$ bit to $1$ byte per seat).
- **Fine-Grained Locks:** $\mathcal{O}(s)$ auxiliary heap space for monitor objects.

---

## 7. Conclusion
Question 5 established that unsynchronized check-then-act operations inevitably cause severe race conditions and double bookings under concurrent load. Synchronizing the booking method on the screen monitor completely eliminates double bookings, guaranteeing exact seat counts. Fine-grained locking and lock-free `AtomicBoolean` operations provide superior scalability for high-concurrency commercial booking portals by confining contention strictly to customers selecting the exact same seat.
