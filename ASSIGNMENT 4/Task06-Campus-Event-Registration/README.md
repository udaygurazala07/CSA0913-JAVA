# Task 6: Design Challenge – Campus Event Registration Desktop Application

## Stage 1 – Interpret the Problem

### 1. Application Users
1. **Student / Registrant:** Enters personal and academic credentials (Roll Number, Full Name, Department, Academic Year, Email) and selects a target campus event from the published roster.
2. **Cultural Committee Member / Coordinator:** Reviews all event registrations in an administrative dashboard, filters records by event, sorts by participant details, and exports official rosters to CSV for event operations and certificate printing.
3. **System Administrator:** Oversees database integrity, audit logs, and system connectivity.

### 2. Required Screens
1. **Main Application Window (`MainFrame`):** Top-level desktop window containing the global `JMenuBar`, navigation tabs/card controls, and status bar.
2. **Student Registration Screen (`RegistrationPanel`):** Data entry form with structured inputs, instant field validation, and action buttons.
3. **Committee Registration Table Screen (`RegistrationTablePanel`):** Tabular management view featuring event filter dropdown, search query box, refresh trigger, export button, and scrollable `JTable`.
4. **Dialogs:** Modal dialogs for registration confirmation, validation errors, duplicate registration warnings, and CSV export file choosers.

### 3. Data Entities
- **`Student`:** `id` (INT PK), `roll_no` (VARCHAR 20 UNIQUE), `name` (VARCHAR 100), `email` (VARCHAR 100), `department` (VARCHAR 50), `year` (INT).
- **`Event`:** `id` (INT PK), `title` (VARCHAR 100), `category` (VARCHAR 50), `event_date` (VARCHAR 30), `venue` (VARCHAR 100), `capacity` (INT).
- **`Registration`:** `id` (INT PK), `student_id` (INT FK), `event_id` (INT FK), `registration_date` (TIMESTAMP), `status` (VARCHAR 20).

### 4. Business Rules & Constraints
1. **Input Completeness:** Roll number, student name, email, department, academic year, and event selection are strictly mandatory.
2. **Format Constraints:**
   - Roll number must be a valid alphanumeric identifier: `^[0-9A-Za-z]{6,12}$`.
   - Email must follow standard RFC-compliant format: `^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$`.
3. **Duplicate Registration Prevention:** A student is strictly prohibited from registering more than once for the same campus event. Enforced across two defense lines:
   - **Service Layer Pre-Check:** `RegistrationService.isDuplicate(studentId, eventId)` queries existing records before transaction dispatch.
   - **Database Constraint:** Composite unique index on `registrations(student_id, event_id)` guarantees ACID-level conflict rejection even under race conditions.
4. **EDT Responsiveness:** All database network calls and file I/O must run asynchronously via `SwingWorker` on background threads, ensuring the UI remains 100% responsive.

### 5. Measurable Requirements & Test Cases

| Req ID | Requirement Statement | Input Condition | Expected System Behavior |
|---|---|---|---|
| **REQ-01** | Successful Registration | Valid Roll No (`192311094`), Name, Email, Dept, Event | Inserts record into MySQL, clears form, increments counter, displays confirmation dialog with Booking ID. |
| **REQ-02** | Reject Empty Fields | Leave Student Name blank | Halts submission, displays `"Student Name is required"` dialog, focuses name field, zero DB calls made. |
| **REQ-03** | Reject Malformed Email | Email entered as `"dhanush#gmail"` | Catches validation error, displays `"Invalid email format"` dialog, prevents database mutation. |
| **REQ-04** | Prevent Duplicate Registration | Same student registers for the same event twice | Service/DB detects collision; displays `"Duplicate Registration: Student is already registered for this event!"` dialog. |
| **REQ-05** | Dynamic Filtering | Select `"Hackathon 2026"` from event filter combo | Background `SwingWorker` queries registrations; `JTable` updates to display only hackathon participants. |
| **REQ-06** | Export to CSV | Click `"Export to CSV"` button | Prompts file path, writes formatted CSV header and rows, shows success notification. |
| **REQ-07** | Database Fault Tolerance | DB connection unavailable | Catches `SQLException` gracefully; displays user-friendly error dialog without crashing JVM. |

---

## Stage 2 – Propose the Screen and Event Flow

### Screen Wireframe Sketches

```
+===================================================================================+
| JMenuBar: [File (Export, Exit)]  [View (Registration Form, Committee Table)] [Help]|
+===================================================================================+
| [Tab 1: Student Registration Form] | [Tab 2: Committee Registration Table]        |
|-----------------------------------------------------------------------------------|
|                                                                                   |
|  CAMPUS EVENT REGISTRATION FORM                                                   |
|                                                                                   |
|  Student Roll No:   [ 192311094                 ]                                 |
|  Full Name:         [ Kattiripalli Dhanush      ]                                 |
|  Email Address:     [ dhanush@simats.edu        ]                                 |
|  Department:        [ Computer Science & Engg |v]                                 |
|  Academic Year:     [ 3rd Year                |v]                                 |
|  Campus Event:      [ National AI Hackathon 2026|v]                               |
|                                                                                   |
|               [ Register for Event ]     [ Clear Form ]                           |
|                                                                                   |
|  Status: Ready to accept student registration.                                    |
+===================================================================================+
```

```
+===================================================================================+
| [Tab 1: Student Registration Form] | [Tab 2: Committee Registration Table]        |
|-----------------------------------------------------------------------------------|
|  Filter by Event: [ All Events             |v]  Search: [                  ] [Q]  |
|  [ Refresh Data ]                                         [ Export to CSV File ]  |
|-----------------------------------------------------------------------------------|
|  Reg ID | Roll No   | Student Name         | Event Name         | Date Registered |
|---------+-----------+----------------------+--------------------+-----------------|
|  REG-1  | 192311094 | Kattiripalli Dhanush | AI Hackathon 2026  | 2026-11-17 ...  |
|  REG-2  | 192311082 | Priya Sharma         | Web Dev BootCamp   | 2026-11-17 ...  |
|  REG-3  | 192311045 | Rahul Verma          | Robotics Expo      | 2026-11-17 ...  |
|-----------------------------------------------------------------------------------|
|  Status: Showing 18 registered participants across 6 events.                      |
+===================================================================================+
```

### Screen Navigation and Event Flow Diagram

```
[Application Startup]
        │
        ▼
 [Initialize MainFrame] ──> [Build JMenuBar & JTabbedPane]
        │
        ├─────────────────────────────┬─────────────────────────────┐
        ▼                             ▼                             ▼
[Registration Screen]      [Committee Table Screen]         [JMenuBar Actions]
   │                          │                                │
   ├─► Enter Student Details  ├─► Filter Dropdown Changed      ├─► File > Export CSV
   │     │                    │     │                          │     │
   │     ▼                    │     ▼                          │     ▼
   ├─► Click "Register"       │   [SwingWorker: Fetch Filtered]│   [JFileChooser Dialog]
   │     │                    │     │                          │     │
   │     ▼                    │     ▼                          │     ▼
   │   [ValidationService]    │   [Update JTable Model (EDT)]  │   [Write CSV File]
   │     │                    │                                │
   │     ├─► Invalid ──> [Show Error Dialog]                   └─► File > Exit
   │     │                                                           │
   │     ▼                                                           ▼
   │   [SwingWorker: RegistrationService]                      [Clean Disposal]
   │     │
   │     ├─► Duplicate Found ──> [Show Warning Dialog]
   │     │
   │     ▼
   │   [Insert DB via PreparedStatement]
   │     │
   │     ▼
   │   [Show Confirmation Dialog (EDT)] ──> [Refresh Table View]
```

---

## Stage 3 – Identify the Critical Design Decisions

### 1. Where Input Validation Occurs
- **Location:** `ValidationService` located strictly before any service or data-access invocation.
- **Rationale:** Validating at the presentation/service boundary ensures that malformed data (such as non-numeric roll numbers, blank names, or illegal characters) is rejected locally without wasting database connections, query parsing cycles, or network bandwidth.

### 2. Where Duplicate Checking Occurs
- **Dual-Layer Defense:**
  1. **Application Layer (`RegistrationService`):** Before executing an insertion, the service queries `RegistrationDAO.existsRegistration(rollNo, eventId)`. If true, an informative `DuplicateRegistrationException` is thrown, allowing the UI to present a clear, polite warning dialog.
  2. **Database Layer (`UNIQUE KEY`):** A compound database constraint `UNIQUE KEY uq_student_event (student_id, event_id)` is defined in the MySQL schema. This acts as an infallible guarantee against race conditions in multi-user concurrent environments.

### 3. Where Database Access Occurs
- **Location:** Dedicated Data Access Objects (`RegistrationDAO`, `EventDAO`) using `DatabaseConnection`.
- **Rationale:** The GUI classes never import `java.sql.*` and contain zero SQL statements. All database access is encapsulated inside DAOs utilizing `PreparedStatement` with parameterized placeholders and `try-with-resources` for automatic connection closing.

### 4. How Long-Running Database Work is Kept Off the Event Dispatch Thread (EDT)
- **`SwingWorker<T, V>` Architectural Pattern:**
  - When the user triggers an action (such as Register, Filter, or Refresh), the event handler launches a subclass of `SwingWorker`.
  - **`doInBackground()`:** Executes on a managed background worker thread from Swing's internal thread pool. The JDBC network connection, statement preparation, query execution, and result set mapping run here without hindering the EDT.
  - **`done()`:** Invoked automatically on the EDT upon task completion. Retrieves the background result via `get()`, updates `JTable` / `DefaultTableModel`, re-enables UI controls, and displays feedback dialogs safely without race conditions.

---

## Stage 4 – Class Design and Handlers

### Layered Architecture Structure

```
+------------------------------------------------------------------------+
|                          PRESENTATION LAYER                            |
|  - MainFrame: Top-level JFrame, JMenuBar, JTabbedPane                  |
|  - RegistrationPanel: Student entry form, input validation triggers    |
|  - RegistrationTablePanel: JTable, JScrollPane, event filters, export  |
|  - MenuController: Menu bar action listener and dispatch               |
+------------------------------------------------------------------------+
                                    │
                                    ▼
+------------------------------------------------------------------------+
|                            SERVICE LAYER                               |
|  - ValidationService: Regex and completeness validator                 |
|  - RegistrationService: Business rules, duplicate checks, transactions |
+------------------------------------------------------------------------+
                                    │
                                    ▼
+------------------------------------------------------------------------+
|                          DATA ACCESS LAYER                             |
|  - DatabaseConnection: Singleton JDBC connection provider              |
|  - RegistrationDAO: CRUD operations for registrations table            |
|  - EventDAO: CRUD operations for events catalog                        |
+------------------------------------------------------------------------+
                                    │
                                    ▼
+------------------------------------------------------------------------+
|                            MODEL LAYER                                 |
|  - Student: id, rollNo, name, email, department, year                  |
|  - Event: id, title, category, eventDate, venue, capacity              |
|  - Registration: id, student, event, registeredAt, status              |
+------------------------------------------------------------------------+
```

### Algorithmic Pseudocode (SIMATS Engineering Standard)

#### (a) Pseudocode: Register Action

```
ALGORITHM HandleEventRegistration
INPUT: rollNo (STRING), name (STRING), email (STRING), dept (STRING), year (INT), event (EVENT)
OUTPUT: Confirmation Dialog OR Error Notification

BEGIN
    // Step 1: Pre-flight Validation
    SET validationResult <- CALL ValidationService.Validate(rollNo, name, email, dept, year, event)
    IF NOT validationResult.IsValid() THEN
        CALL ShowWarningDialog("Input Validation Failed", validationResult.GetErrorMessage())
        RETURN
    ENDIF

    // Step 2: Lock UI and Show Progress
    CALL SetControlsEnabled(FALSE)
    SET lblStatus.text <- "Registering student for " + event.GetTitle() + "... Please wait."

    // Step 3: Execute in Background Worker
    CREATE SwingWorker WORKER
        DO IN BACKGROUND:
            // Check for duplicate registration
            SET alreadyRegistered <- CALL RegistrationService.IsStudentRegistered(rollNo, event.GetId())
            IF alreadyRegistered THEN
                THROW DuplicateRegistrationException("Student " + rollNo + " is already registered for " + event.GetTitle())
            ENDIF

            // Persist Registration
            SET regId <- CALL RegistrationService.RegisterStudent(rollNo, name, email, dept, year, event.GetId())
            RETURN regId

        ON DONE (ON EDT):
            TRY
                SET newRegId <- CALL WORKER.GET()
                CALL ShowInformationDialog("Registration Confirmed", 
                     "Registration Successful!\nRegistration ID: " + newRegId + 
                     "\nStudent: " + name + " (" + rollNo + ")\nEvent: " + event.GetTitle())
                CALL ResetForm()
                CALL RefreshTableData()
                SET lblStatus.text <- "Registration " + newRegId + " confirmed successfully."
            CATCH DuplicateRegistrationException dupEx
                CALL ShowWarningDialog("Duplicate Registration", dupEx.GetMessage())
                SET lblStatus.text <- "Registration rejected: Duplicate entry detected."
            CATCH Exception ex
                CALL ShowErrorDialog("System Error", "Database transaction failed: " + ex.GetMessage())
                SET lblStatus.text <- "Error: Registration failed."
            FINALLY
                CALL SetControlsEnabled(TRUE)
            ENDTRY
    END SwingWorker

    CALL WORKER.EXECUTE()
END
```

---

#### (b) Pseudocode: Filter Action

```
ALGORITHM HandleEventFilter
INPUT: selectedEventTitle (STRING)
OUTPUT: Updated JTable with filtered registrations

BEGIN
    CALL SetFilterControlsEnabled(FALSE)
    SET lblStatus.text <- "Fetching registrations for: " + selectedEventTitle + "..."

    CREATE SwingWorker FILTER_WORKER
        DO IN BACKGROUND:
            IF selectedEventTitle EQUALS "All Events" THEN
                SET records <- CALL RegistrationService.GetAllRegistrations()
            ELSE
                SET records <- CALL RegistrationService.GetRegistrationsByEventTitle(selectedEventTitle)
            ENDIF
            RETURN records

        ON DONE (ON EDT):
            TRY
                SET list <- CALL FILTER_WORKER.GET()
                CALL tableModel.SetRowCount(0)
                FOR EACH reg IN list DO
                    CALL tableModel.AddRow([
                        reg.GetId(),
                        reg.GetStudentRoll(),
                        reg.GetStudentName(),
                        reg.GetEventTitle(),
                        reg.GetFormattedDate(),
                        reg.GetStatus()
                    ])
                ENDFOR
                SET lblStatus.text <- "Showing " + list.SIZE() + " registrations for " + selectedEventTitle + "."
            CATCH Exception ex
                CALL ShowErrorDialog("Data Error", "Unable to load registrations: " + ex.GetMessage())
                SET lblStatus.text <- "Error fetching data."
            FINALLY
                CALL SetFilterControlsEnabled(TRUE)
            ENDTRY
    END SwingWorker

    CALL FILTER_WORKER.EXECUTE()
END
```

---

## Alternative Concept and Complexity Analysis

### Comparison: Loading All 100,000 Records at Once vs. Paginated Loading via SQL `LIMIT` & `OFFSET` in `SwingWorker`

| Parameter | Monolithic Loading (All 100,000 Rows at Once) | Paginated Loading (SQL `LIMIT` & `OFFSET` via `SwingWorker`) |
|---|---|---|
| **Query Strategy** | `SELECT * FROM registrations JOIN students JOIN events` | `SELECT * FROM registrations ... LIMIT ? OFFSET ?` |
| **Initial Query & Render Latency** | **Extremely Slow:** 8 to 25 seconds to transfer, parse, and construct 100,000 entity rows | **Instantaneous:** 10 to 40 milliseconds to fetch exactly one page (e.g., 50 rows) |
| **Client JVM Heap Consumption** | **Catastrophic ($> 120\text{ MB}$):** Allocates 100,000 entity objects + table model vectors, triggering major GC pauses | **Minimal ($< 50\text{ KB}$):** Only 50 active rows exist in memory at any instant |
| **GUI Responsiveness** | **Freezes / Stutters:** Even on background thread, inserting 100,000 rows into `DefaultTableModel` locks the EDT during layout validation | **100% Fluid:** Tiny page updates execute instantaneously on the EDT without dropped frames |
| **Database Server Load** | High memory buffer spike on MySQL; heavy network packet serialization across socket | Minimal server memory overhead; index-supported seek with bounded result sets |

### Mathematical Big-O Complexity Derivation:
- **Monolithic Retrieval:**
  $$\text{Time}_{\text{mono}}(N) = O(N) + O(N \cdot c_{\text{net}}) + O(N \cdot c_{\text{JVM}}) \implies O(N)$$
  For $N = 100,000$, total memory is $S_{\text{mono}} = O(N)$. With an average row size of $250\text{ bytes}$, $100,000 \times 250\text{ bytes} \approx 25\text{ MB}$ of raw data plus object overhead ($\approx 75-100\text{ MB}$ total heap).

- **Paginated Retrieval (Page Size $P \ll N$, e.g., $P = 50$):**
  $$\text{Time}_{\text{page}}(P) = O(\log N + P) \approx O(1)\text{ relative to } N$$
  $$S_{\text{page}} = O(P) \approx 50 \times 250\text{ bytes} \approx 12.5\text{ KB}$$
  Using a B-Tree clustered index on `registration_id`, the database seeks directly to the offset in $O(\log N)$ and streams $P$ rows.
- **Conclusion:** Paginated loading with `SwingWorker` provides bounded memory consumption, prevents Out-Of-Memory (OOM) errors, and ensures seamless responsiveness.
