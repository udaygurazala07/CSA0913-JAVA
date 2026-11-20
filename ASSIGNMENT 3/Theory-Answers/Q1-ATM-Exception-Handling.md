# Question 1: Handling Failure in a Bank ATM Simulation

**Course Outcome:** CO3 – Exception Handling and Robust Application Design  
**Student Name:** Kattiripalli Dhanush  
**Register Number:** 192311094  
**Role:** Individual Execution (Fulfilling both Student A – Design and Student B – Testing/Tracing tracks)

---

## 1. Introduction
An Automated Teller Machine (ATM) is a mission-critical financial system where reliability, security, and hardware coordination are paramount. Every transaction involves multi-layered interactions between hardware peripherals (card reader, cash dispenser, receipt printer) and software systems (user input validation, account balance verification, network communication). In an ATM environment, software failures cannot simply crash the application or leave cash half-dispensed. Instead, the runtime environment must employ structured **Exception Handling** to gracefully intercept faults, notify the user, restore internal state, and guarantee hardware cleanup.

---

## 2. Problem Explanation
During an ATM cash withdrawal workflow, failures can occur at various points:
1. The user may insert a card whose configuration file cannot be read (`FileNotFoundException`).
2. The user might accidentally type alphabetic characters instead of numeric digits for the withdrawal amount (`NumberFormatException`).
3. The user could specify a negative amount or zero (`IllegalArgumentException`).
4. The user may request a withdrawal amount greater than their current account balance (`InsufficientFundsException`).
5. A severe hardware jam, memory exhaustion, or operating system fault could arise (`Error` / `VirtualMachineError`).

The program must distinguish between checked exceptions (recoverable external failures enforced by the compiler), unchecked exceptions (programming/input logic errors), and unrecoverable Errors. Furthermore, it must demonstrate correct catch ordering, multi-catch usage, stack trace interpretation, and the inviolable guarantee of the `finally` block.

---

## 3. Detailed Answer in Simple English

### 3.1 Relevant `Throwable` Exception Hierarchy
In Java, all errors and exceptions inherit from the root class `java.lang.Throwable`. The hierarchy divides into two major branches:

```
                          java.lang.Throwable
                                  │
         ┌────────────────────────┴────────────────────────┐
         │                                                 │
 java.lang.Error                                   java.lang.Exception
 (Unchecked / Fatal)                               (Application Issues)
   ├── OutOfMemoryError                                    │
   ├── StackOverflowError                 ┌────────────────┴────────────────┐
   └── InternalError                      │                                 │
                                java.lang.RuntimeException         Checked Exceptions
                                (Unchecked / Logic)                (Compiler Enforced)
                                  ├── NumberFormatException          ├── IOException
                                  ├── IllegalArgumentException       │     └── FileNotFoundException
                                  └── NullPointerException           └── InsufficientFundsException (Custom)
```

1. **`Throwable`**: The superclass of all errors and exceptions in Java.
2. **`Error`**: Represents severe conditions that reasonable applications should not attempt to catch (e.g., hardware fault, JVM out of memory). These are unchecked.
3. **`Exception`**: Represents conditions that a reasonable application might want to catch.
4. **Checked Exceptions**: Subclasses of `Exception` excluding `RuntimeException`. The Java compiler mandates that methods declare them via `throws` or handle them within `try-catch`.
5. **Unchecked Exceptions (`RuntimeException`)**: Subclasses of `RuntimeException`. They usually represent logical bugs, bad input formats, or illegal arguments that can happen anywhere. Compiler does not force explicit declaration.

---

### 3.2 Student A – Exception Design
As Student A, the focus is on architectural classification, catch block sequencing, and cleanup semantics.

#### Failure Point and Classification Table
| Failure Point | Specific Exception Type | Checked / Unchecked / Error | Root Cause / Condition | Where Caught in Design |
|---|---|---|---|---|
| Card Reader Missing / Defective | `java.io.FileNotFoundException` | Checked (`IOException`) | Physical hardware device node or mock configuration file not found on disk | `catch (FileNotFoundException \| InsufficientFundsException)` |
| Amount Input Contains Letters | `java.lang.NumberFormatException` | Unchecked (`IllegalArgumentException`) | User enters non-numeric text like `"five_thousand"` | `catch (NumberFormatException nfe)` |
| Negative / Zero Amount | `java.lang.IllegalArgumentException` | Unchecked (`RuntimeException`) | Input validation fails ($amount \le 0$) | `catch (IllegalArgumentException iae)` |
| Overdraft / Exceeds Balance | `InsufficientFundsException` | Checked (`Exception`) | Business logic check fails ($requested > balance$) | `catch (FileNotFoundException \| InsufficientFundsException)` |
| Critical Memory or VM Failure | `java.lang.OutOfMemoryError` | Unchecked (`Error`) | System memory exhausted | Uncaught / System level (JVM crashes) |
| Unexpected Runtime Glitch | `java.lang.Exception` | Checked / General Superclass | Fallback catch for unexpected failures | `catch (Exception e) [Fallback]` |

#### Catch Block Order Justification
Java enforces that catch blocks are evaluated top-to-bottom. A catch block for a subclass must always appear before a catch block for its superclass.
1. `NumberFormatException` and `IllegalArgumentException` are caught first to deliver specific guidance for format vs magnitude errors.
2. Disjoint checked exceptions (`FileNotFoundException` and `InsufficientFundsException`) are handled with Java 7+ multi-catch syntax (`catch (A | B)`).
3. The broad `catch (Exception e)` is placed strictly at the bottom as a defensive fallback to ensure no unforeseen exception terminates the ATM process without cleanup.

#### The Inviolable Guarantee of the `finally` Block
In an ATM application, the `finally` block is essential for hardware safety and session security. Regardless of whether:
- The transaction completes successfully,
- A handled exception occurs, or
- An unexpected runtime exception occurs,

the `finally` block **always executes**. In our ATM simulation, the `finally` block guarantees:
1. Physical ejection of the user's ATM card so it is not retained in the slot.
2. Erasure of sensitive security data (e.g., PIN buffer in RAM).
3. Writing a persistent transaction audit log to permanent storage.
4. Returning the physical hardware components (shutter, motors) to a safe idle state.

---

### 3.3 Student B – Testing and Tracing
As Student B, the focus is on constructing concrete failure scenarios, inspecting runtime stack traces, and testing incorrect compiler ordering.

#### Five Executed Test Scenarios (Evidence from `q1_results.csv`)
| Test Case | Description | Input Card File | Input Amount | Outcome | Catch Block Executed | Finally Executed |
|---|---|---|---|---|---|---|
| Test 1 | Valid cash withdrawal | `valid_card.txt` | `5000` | `SUCCESS` | `NONE` | `true` |
| Test 2 | Non-numeric string | `valid_card.txt` | `five_thousand` | `FAILED_NUMBER_FORMAT` | `catch (NumberFormatException)` | `true` |
| Test 3 | Negative withdrawal | `valid_card.txt` | `-1500` | `FAILED_ILLEGAL_AMOUNT` | `catch (IllegalArgumentException)` | `true` |
| Test 4 | Overdraft request | `valid_card.txt` | `35000` | `FAILED_INSUFFICIENT_FUNDS` | `catch (FileNotFoundException \| InsufficientFundsException)` | `true` |
| Test 5 | Missing card file | `missing_card_reader.txt` | `2000` | `FAILED_CARD_READ` | `catch (FileNotFoundException \| InsufficientFundsException)` | `true` |

Notice that in **all five scenarios**, `FinallyExecuted` is `true`.

#### Dissection of an Uncaught Exception Stack Trace
Running `AtmStacktraceDemo` generated the following actual uncaught stack trace:

```
Exception in thread "main" java.lang.NullPointerException: Hardware card chip PIN buffer is null at slot 0
	at q1.AtmStacktraceDemo.checkHardware(AtmStacktraceDemo.java:19)
	at q1.AtmStacktraceDemo.validatePin(AtmStacktraceDemo.java:14)
	at q1.AtmStacktraceDemo.processTransaction(AtmStacktraceDemo.java:10)
	at q1.AtmStacktraceDemo.main(AtmStacktraceDemo.java:25)
```

**Line-by-Line Breakdown:**
1. **Line 1 (`Exception in thread "main" ...`)**: Identifies the thread where the fault occurred (`main`), the exception class (`java.lang.NullPointerException`), and the descriptive error message describing the exact state violation.
2. **Line 2 (`at q1.AtmStacktraceDemo.checkHardware(AtmStacktraceDemo.java:19)`)**: The deepest active frame on the call stack at the instant the exception was thrown. It reveals method `checkHardware()` at source line 19.
3. **Line 3 (`at q1.AtmStacktraceDemo.validatePin(AtmStacktraceDemo.java:14)`)**: The caller method that invoked `checkHardware()`.
4. **Line 4 (`at q1.AtmStacktraceDemo.processTransaction(AtmStacktraceDemo.java:10)`)**: The intermediate coordinator method that called `validatePin()`.
5. **Line 5 (`at q1.AtmStacktraceDemo.main(AtmStacktraceDemo.java:25)`)**: The root entry point of the application call stack.

#### Demonstration of Incorrect Catch-Block Ordering
In `AtmIncorrectCatchOrder.java`, if a broader catch block is positioned ahead of a narrower one:
```java
try {
    String str = null;
    str.length();
} catch (Exception e) {
    System.out.println("Generic catch");
} catch (NullPointerException npe) { // Subclass placed AFTER superclass
    System.out.println("Specific catch");
}
```
The Java compiler rejects this code during compilation:
```
AtmIncorrectCatchOrder.java:21: error: exception NullPointerException has already been caught
        } catch (NullPointerException npe) {
          ^
1 error
```
**Explanation:** Because `NullPointerException` inherits from `RuntimeException` which inherits from `Exception`, any `NullPointerException` is already caught by `catch (Exception e)`. The second block is completely unreachable, which violates the Java Language Specification (JLS §14.20).

---

## 4. Joint Design Question
**Question:** Should the ATM program catch every exception with a single `catch (Exception e)`? Under what conditions would that be acceptable, and what is lost by doing so?

**Answer:**
- **Why it is generally bad:** Catching every exception with `catch (Exception e)` masks the root cause of failures. The program cannot distinguish between an input typing error (where the user should be prompted to re-enter digits), an overdraft (where a mini-statement should be offered), and a missing hardware device (where the ATM must switch to "Out of Service" mode and alert technical dispatch).
- **When it is acceptable:** A broad `catch (Exception e)` is acceptable **only as an outer fallback** placed after all specific catch blocks, or at the top-level event loop of a daemon thread to prevent the application thread from dying completely.
- **What is lost:**
  1. **Granular recovery:** Unable to take domain-specific corrective actions.
  2. **Security & Auditability:** Masking genuine internal security violations or system corruptions.
  3. **User feedback quality:** The user receives a vague "An error occurred" instead of actionable instructions.

---

## 5. Alternative Concept and Complexity Analysis
### Avoiding Exceptions: Defensive Pre-validation and Error Codes
Instead of relying on exceptions, the ATM program could validate all inputs using defensive guards (`Scanner.hasNextInt()`, string regex matching, and explicit range checks) and return numeric error codes (e.g., `0` for success, `-1` for invalid format, `-2` for negative amount, `-3` for insufficient funds, `-4` for hardware offline).

#### Comparison of Approaches
```
                  Defensive Error Codes                     Exception Handling
                 ───────────────────────                   ────────────────────
Code Structure : Requires nested if-else checks          Separates normal logic from
                 at every single step.                   error handling into try-catch.
Readability    : Low; "happy path" is obscured           High; linear happy path with
                 by repeated status checks.              isolated recovery blocks.
Propagation    : Caller must explicitly pass and         Exceptions automatically unwind
                 check return codes up the stack.        the call stack to the handler.
```

#### Measured Benchmark Data (100,000 Iterations)
From our actual execution of `AtmErrorCodesAlternative`:
- **Error Codes Execution Time:** $168.14 \text{ ms}$ ($168,141,500 \text{ ns}$)
- **Exception Creation & Catch Execution Time:** $204.39 \text{ ms}$ ($204,390,600 \text{ ns}$)
- **Measured Overhead:** Exception throwing took **1.22x** the time of error code returns.

**Why Exceptions Incur Overhead:** When an exception is instantiated via `new Exception()`, the JVM invokes native method `Throwable.fillInStackTrace()`. This method traverses the thread's execution call stack, walks every active frame, records class names, method names, source files, and line numbers, and allocates array objects on the heap. In contrast, returning an integer primitive error code simply writes to a CPU register with zero heap allocation.

---

## 6. Time and Space Complexity

### Time Complexity
- **Happy Path (Valid Transaction):** $\mathcal{O}(1)$ time. Parsing the input and updating account balance takes constant time.
- **Error Handling via Exceptions:** $\mathcal{O}(d)$ time, where $d$ is the depth of the call stack. The JVM must traverse $d$ stack frames to construct the stack trace and unwind the stack to the matching catch block.
- **Alternative Error Codes:** $\mathcal{O}(1)$ time. Branch comparisons and primitive return codes do not inspect the stack.

### Space Complexity
- **Happy Path:** $\mathcal{O}(1)$ auxiliary memory.
- **Exception Handling Path:** $\mathcal{O}(d)$ heap space. The `Throwable` object holds an array of `StackTraceElement` references proportional to call stack depth $d$.
- **Alternative Error Codes:** $\mathcal{O}(1)$ space. Only primitive registers are used.

---

## 7. Conclusion
Question 1 demonstrated that robust ATM transaction handling requires clear architectural separation of checked exceptions, unchecked exceptions, and errors. Multi-catch statements streamline disjoint error paths, catch order enforcement prevents dead code, and the `finally` block guarantees hardware state cleanup even during critical faults. While defensive error codes offer minimal execution overhead, exceptions provide indispensable maintainability and clean stack propagation for complex enterprise workflows.
