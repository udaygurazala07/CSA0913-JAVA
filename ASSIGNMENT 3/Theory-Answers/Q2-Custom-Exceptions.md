# Question 2: User-Defined Exceptions and Propagation in an Online Examination Portal

**Course Outcome:** CO3 – Exception Chaining, Custom Exception Hierarchy, and Multi-Tier Propagation  
**Student Name:** Kattiripalli Dhanush  
**Register Number:** 192311094  
**Role:** Individual Execution (Fulfilling both Student A – Design and Student B – Testing/Tracing tracks)

---

## 1. Introduction
Modern enterprise applications are structured in decoupled architectural tiers: Presentation / Controller, Business / Service, and Data / Storage. In an **Online Examination Portal**, thousands of concurrent students submit digital answer scripts. If an error occurs—such as a student submitting past the deadline, an unregistered candidate uploading files, or disk quota exhaustion—the system must handle these errors cleanly across architectural boundaries. Java's custom user-defined exception mechanisms and **Exception Chaining** allow low-level I/O faults to be translated into meaningful domain-level abstractions without losing root-cause diagnostics.

---

## 2. Problem Explanation
The examination portal must enforce three distinct categories of constraints:
1. **Temporal Constraint:** Reject submissions after the examination window has closed.
2. **Authorization / Identity Constraint:** Reject submissions from students not registered for the course.
3. **Infrastructure / Capacity Constraint:** Reject answer script files that exceed allowable storage size or trigger disk quota limits.

These checks originate in different layers:
- **Storage Layer:** Interacts with the physical file system and throws raw `IOException` when disk write quotas are exceeded.
- **Service Layer:** Evaluates business logic (deadlines and registration status) and orchestrates storage operations.
- **Controller Layer:** Receives HTTP/RPC requests, invokes the service layer, translates exceptions into standardized client responses, and logs diagnostics.

A critical design requirement is that low-level technical exceptions (e.g., POSIX disk quota `IOException`) must not leak unvarnished to the student. Instead, they must be wrapped in high-level domain exceptions (`SubmissionStorageException`) while preserving the underlying cause via exception chaining.

---

## 3. Detailed Answer in Simple English

### 3.1 Class Diagram & Exception Hierarchy
The custom exception hierarchy is organized logically around the root `ExamException`:

```
                           java.lang.Throwable
                                    │
                           java.lang.Exception
                                    │
                    ┌───────────────┴───────────────┐
                    │                               │
           ExamException (Checked)        java.lang.RuntimeException
                    │                               │
        ┌───────────┴───────────┐                   │
        │                       │                   │
ExamWindowClosedException  SubmissionStorageException  StudentNotRegisteredException
  (Deadline Exceeded)      (Wraps Low-Level I/O)         (Unchecked Precondition)
```

### 3.2 Student A – Exception Hierarchy and Propagation Design
As Student A, the focus is on architectural exception boundaries, layer responsibilities, and propagation flows.

#### Checked vs Unchecked Design Decisions
1. **`ExamException` (Checked):** Subclasses `java.lang.Exception`. Base type for portal operations. Forces calling services to handle portal-level operational failures.
2. **`ExamWindowClosedException` (Checked):** Subclasses `ExamException`. The closing of an exam window is an anticipated business scenario. The controller must catch this to provide the student with clear information (e.g., deadline elapsed, grace period option, or penalty scoring).
3. **`SubmissionStorageException` (Checked):** Subclasses `ExamException`. Disk or network I/O failures are external infrastructure anomalies. Making it checked forces developers to write fallback strategies (e.g., retrying upload, buffering to secondary storage, or alerting system operators).
4. **`StudentNotRegisteredException` (Unchecked - `RuntimeException`):** Subclasses `java.lang.RuntimeException`. An unregistered student attempting to submit an exam represents a security violation or authentication precondition failure. In clean architecture, programming or security precondition failures are typically unchecked because normal business logic cannot recover from them.

#### Propagation Path from Storage Layer to Controller
```
[Storage Layer]
  1. Low-level write fails: throws IOException("Disk quota exceeded")
  2. Storage catches IOException
  3. Wraps it into new SubmissionStorageException("Could not store answer02.pdf", ioException)
  4. Throws SubmissionStorageException via method signature 'throws SubmissionStorageException'
           │
           ▼
[Service Layer]
  5. ExamService.processSubmission() receives SubmissionStorageException
  6. Service does not suppress it; declares 'throws FileStorageException'
  7. Exception propagates untouched up the call stack
           │
           ▼
[Controller Layer]
  8. ExamController.handleSubmissionRequest() catches ExamException
  9. Extracts user-facing message: e.getMessage() -> "Error: Could not store answer02.pdf"
 10. Extracts root cause: e.getCause().getMessage() -> "Cause: Disk quota exceeded"
 11. Returns clean HTTP 500 JSON/HTML response to student; logs full chained trace internally.
```

---

### 3.3 Student B – Failure Scenarios and Chaining Evidence
As Student B, the focus is on scenario execution, chaining evidence, and diagnosing information loss.

#### Complete Test Table (Observed Behavior from `q2_results.csv`)
| Scenario | Student ID | Condition | Exception Thrown | Exception Category | Root Cause Preserved? | Response Status |
|---|---|---|---|---|---|---|
| 1. Valid Submission | `192311094` | Within window, registered, 250 KB | None | N/A | N/A | `200 OK: Submission successful for answer01.pdf` |
| 2. Unregistered Student | `192399999` | Student ID not in database | `StudentNotRegisteredException` | Unchecked (`RuntimeException`) | No cause needed | `403 Forbidden: Access Denied` |
| 3. Window Elapsed | `192311094` | Submitted at 10:15 AM (Deadline 10:00) | `ExamWindowClosedException` | Checked (`PortalException`) | No cause needed | `400 Window Closed: Submission Rejected` |
| 4. Storage Quota Exceeded (Chained) | `192311094` | File 850 KB > 500 KB limit | `FileStorageException` | Checked (`PortalException`) | **Yes** (`IOException` preserved) | `500 Storage Failed: Disk quota exceeded` |
| 5. Storage Quota Exceeded (Lost Cause) | `192311094` | File 850 KB > 500 KB limit | `FileStorageException` | Checked (`PortalException`) | **No** (`cause=null`, lost) | `500 Storage Failed: Cause [LOST / NULL]` |

#### Complete the Code Solution (`StorageDemo.java`)
In `StorageDemo.java`, TODO 1 was completed as:
```java
} catch (IOException e) {
    // TODO 1: Wrap the IOException in a SubmissionStorageException, keeping the
    // IOException as its cause
    throw new SubmissionStorageException("Could not store " + fileName, e);
}
```
**Actual Output Produced:**
```
Saved answer01.pdf
Error: Could not store answer02.pdf
Cause: Disk quota exceeded
```
This output matches the exact requirement specified in the PDF.

#### What Happens When the Original Cause is Omitted?
If the constructor does not receive the `IOException` (e.g., `new SubmissionStorageException("Could not store " + fileName)` without passing `e`):
1. The internal `cause` field inside `Throwable` remains `null`.
2. `e.getCause()` returns `null`.
3. In `StorageDemo.java`, executing `e.getCause().getMessage()` will immediately throw a **`NullPointerException`**, crashing the error reporting code itself!
4. The sysadmin and software engineers lose all debugging visibility: they will see "Could not store file", but will have no idea whether it was due to a full hard disk, permission denied, bad sector, or network partition.

#### Full Chained Stack Trace Evidence
Running our test harness produced the following chained stack trace:
```
q2.ExamPortal$FileStorageException: Storage error writing script: corrupt_test.pdf
	at q2.ExamPortal$StorageLayer.writeAnswerScript(ExamPortal.java:73)
	at q2.ExamPortal.main(ExamPortal.java:219)
Caused by: java.io.IOException: POSIX write() failed: Disk quota exceeded on sector /mnt/exams
	at q2.ExamPortal$StorageLayer.writeAnswerScript(ExamPortal.java:67)
	... 1 more
```
Notice the `Caused by:` clause generated by the JVM. It points directly to line 67 in `StorageLayer` where the original low-level `IOException` occurred.

---

## 4. Joint Conclusion
**Question:** Why is it better for the storage layer to throw a meaningful `SubmissionStorageException` than to let an `IOException` travel unchanged to the user interface?

**Answer:**
1. **Architectural Abstraction and Encapsulation:** The user interface / controller should be decoupled from implementation details of the persistence mechanism. Today submissions may be stored on a local ext4 disk (`IOException`); tomorrow they may be saved to an Amazon S3 bucket (`SdkClientException`) or MongoDB (`MongoException`). If raw exceptions travel to the UI, the UI layer becomes tightly coupled to storage technologies.
2. **Security & Information Disclosure:** Raw I/O exceptions frequently expose internal server paths (e.g., `/var/storage/exams/confidential/`), server hostnames, or OS version details. Exposing raw stack traces to students presents a significant security risk.
3. **Actionable User Experience:** A student cannot fix a "POSIX error 28: ENOSPC". A clear domain message ("Submission storage error: Please contact exam admin") is user-friendly and actionable.

---

## 5. Alternative Concept and Complexity Analysis
### Making Custom Exceptions Unchecked (`RuntimeException`)
If all custom exceptions extended `RuntimeException` instead of `Exception`:
- **Method Signatures:** The `throws` clause could be removed from method declarations in `StorageLayer` and `ExamService`.
- **Calling Code:** The compiler would no longer force callers to wrap invocations in `try-catch`.
- **Trade-off:** While this reduces boilerplate code in large codebases, it introduces the danger of silent failures: developers may forget to handle known operational failures like exam window closure, leading to unhandled runtime crashes during peak exam hours.

### Memory Overhead of Exception Chaining
When an exception $E_1$ chains $E_2$, which chains $E_3$:
- Each `Throwable` maintains its own array of `StackTraceElement[]`.
- For a stack of depth $d$, each exception retains approximately $d \times 32$ bytes of stack references plus message strings.
- Chaining keeps the entire causal graph reachable on the heap until the top-level exception is garbage-collected. This is an intentional trade-off: small heap retention ($\approx 1\text{–}4 \text{ KB}$ per exception) in exchange for complete diagnostic traceability.

---

## 6. Time and Space Complexity

### Time Complexity
- **Throwing and Chaining an Exception:** $\mathcal{O}(d)$ where $d$ is call stack depth. Each chained exception captures stack frames via `fillInStackTrace()`.
- **Handling and Walking the Cause Chain:** $\mathcal{O}(k)$ where $k$ is the number of chained links (typically $1 \le k \le 4$).
- **Overall Request Processing:** $\mathcal{O}(1)$ for happy path validation and file lookup.

### Space Complexity
- **Happy Path:** $\mathcal{O}(1)$ auxiliary memory.
- **Chained Exception Object Graph:** $\mathcal{O}(k \cdot d)$ auxiliary heap space, retaining $k$ exception objects, each holding $d$ stack frame descriptors.

---

## 7. Conclusion
Question 2 demonstrated the practical value of custom exception hierarchies and exception chaining in multi-layered enterprise systems. By wrapping low-level `IOException` instances into domain-specific `SubmissionStorageException` objects, the application preserves architectural encapsulation while maintaining full root-cause diagnostic fidelity through `getCause()` and chained stack traces.
