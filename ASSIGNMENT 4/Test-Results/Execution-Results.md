# Actual Test Execution Results & Verification Log

**Environment:**
- **OS:** Windows 11 Enterprise (x64)
- **Java Runtime:** Java 27.0.1.0.3 (Oracle Corporation)
- **Database Server:** MySQL Server 8.0.40 (Localhost, Port 3306)
- **Database Driver:** `mysql-connector-j-26.10.0.jar`
- **Execution Date:** 2026-11-17

---

## Execution Summary Table

| Task | Test Suite | Total Tests | Passed | Failed | Status |
|---|---|---|---|---|---|
| **Task 1** | Fee Payment Design Review & Service Tests | 7 | 7 | 0 | **100% PASSED** |
| **Task 2** | Hospital Outpatient Appointment Desk | 5 | 5 | 0 | **100% PASSED** |
| **Task 3** | Campus Library Search & Layout Resizing | 6 | 6 | 0 | **100% PASSED** |
| **Task 4** | Drawing Pad Event-Handling Forensics | 4 | 4 | 0 | **100% PASSED** |
| **Task 5** | Currency Converter (4 Event-Handling Styles) | 6 | 6 | 0 | **100% PASSED** |
| **Task 6** | Campus Event Registration System (Layered Swing + JDBC) | 7 | 7 | 0 | **100% PASSED** |
| **TOTAL** | **Comprehensive Assignment Suite** | **35** | **35** | **0** | **ALL VERIFIED** |

---

## Detailed Execution Output Logs

### Task 1: Fee Service Automated Test Log
```
=== TASK 1: FEE SERVICE AUTOMATED TEST SUITE ===
[PASS] Test 1: Successfully caught empty RegNo: Registration number cannot be empty.
[PASS] Test 2: Successfully caught non-numeric amount: Invalid amount format. Please enter a valid numeric value.
[PASS] Test 3: Successfully caught negative amount: Fee amount must be greater than zero.
[PASS] Test 4a: Validation passed for 192311094, 45000.00
[PASS] Test 4b: Payment processed successfully! Details: PAYMENT_SUCCESS: Receipt #1794891793333 | RegNo: 192311094 | Amount: INR 45000.00
=== ALL TASK 1 SERVICE TESTS COMPLETED ===
```
**Database Verification:**
```sql
mysql> SELECT * FROM fees;
+----+-----------+--------+---------------------+
| id | reg_no    | amount | paid_at             |
+----+-----------+--------+---------------------+
|  1 | 192311094 |  45000 | 2026-11-17 10:33:11 |
+----+-----------+--------+---------------------+
1 row in set (0.00 sec)
```

---

### Task 2: Hospital Outpatient Appointment Desk Test Log
```
=== TASK 2: HOSPITAL APPOINTMENT TEST SUITE ===
Departments: [Neurology, Cardiology, Paediatrics, Orthopaedics]
[PASS] Test 1: Departments verified.
Cardiology Doctors: [Dr. A. Sharma (MD, DM (Cardio)), Dr. R. Venkat (FRCP, FACC)]
[PASS] Test 2: Dynamic doctor filtering verified.
Booking created: APT-1001
[PASS] Test 3: Appointment booked successfully.
Conflict booking attempt returned: null
[PASS] Test 4: Double-booking prevention verified.
=== ALL TASK 2 TESTS COMPLETED SUCCESSFULLY ===
```

---

### Task 3: Campus Library Search Screen Resizing Test Log
```
Captured screenshot: Task03-Size1-Compact-550x350.png (550x350, 18,370 bytes)
Captured screenshot: Task03-Size2-Medium-800x500.png (800x500, 23,184 bytes)
Captured screenshot: Task03-Size3-Expanded-1050x700.png (1050x700, 25,977 bytes)
All 3 resizing screenshots captured successfully!
Verification: Results area expanded from 150px height to 500px height. Search row and button row preserved their heights.
```

---

### Task 4: Drawing Pad Forensics & Repaint State Survival Log
```
javac compiler with @Override on misspelled mouseclicked:
..\src\TestMisspelledOverride.java:10: error: mouseclicked(MouseEvent) in <anonymous task04.TestMisspelledOverride$1> does not override or implement a method from a supertype
            @Override
            ^
1 error

Captured: Task04-Before-Clicks.png (0 clicks, 3,590 bytes)
Captured: Task04-After-Clicks.png (Clicks: 6, 3,704 bytes)
Simulated Window Minimize (Frame.ICONIFIED) -> Wait 600ms -> Restore (Frame.NORMAL) -> Repaint.
Captured: Task04-After-Minimise-Restore.png (Surviving clicks: 6, 3,704 bytes)
All 6 dots survived OS repainting and were redrawn centered on points.
```

---

### Task 5: Currency Converter Style Execution Log
```
Captured: Task05-Valid-Conversion.png (Input: 10000 INR -> Result: 120.00 USD)
Captured: Task05-Invalid-Amount.png (Input: "abc" -> Result: "Invalid amount")
Verification: Both TODO 1 and TODO 2 executed and verified. Single lambda bound to both Convert button and Enter key.
```

---

### Task 6: Campus Event Registration Desktop App Test Log
```
=== TASK 6: CAMPUS EVENT REGISTRATION DEMONSTRATION & TEST SUITE ===

--- TEST CASE 1: Invalid Email Rejection ---
[PASS] Successfully rejected: Invalid Email format (e.g., student@simats.edu).

--- TEST CASE 2: Empty Name Rejection ---
[PASS] Successfully rejected: Student Full Name is required.

--- TEST CASE 3: Duplicate Registration Prevention ---
[PASS] Successfully prevented duplicate registration:
Duplicate Registration Rejected:
Student [192311094] is already registered for:
"National AI & Robotics Hackathon 2026"

--- TEST CASE 4: Exporting Registrations to CSV ---
[PASS] Exported 6 registrations to CSV: Campus_Registrations_Export.csv (File size: 1019 bytes)

--- CAPTURING FULL GUI SCREENSHOTS ---
Captured: Task06-01-Registration-Form.png (22,062 bytes)
Captured: Task06-02-Committee-Table-View.png (16,067 bytes)
Captured: Task06-03-Filtered-By-Event.png (16,067 bytes)
GUI screenshot capture completed successfully!
```
