# Task 2: From Requirement to Event-Driven Design

## Task 2.1 – Model the Events

The Hospital Outpatient Appointment Desk application requires reactive event handling across multiple interactive components. The table below details every event source, generated event object, listener interface, and the exact action executed:

| Event Source Component | Event Object Generated | Listener Interface | Action Taken / Handler Logic |
|---|---|---|---|
| **`patientIdField`** (`JTextField`) | `DocumentEvent` | `DocumentListener` (or `FocusListener`) | Detects user typing. Validates patient ID syntax (e.g., `PAT-XXXXX`), resets prior error labels, and marks the transaction state as `bookingInProgress = true`. |
| **`departmentCombo`** (`JComboBox<String>`) | `ItemEvent` / `ActionEvent` | `ItemListener` / `ActionListener` | Fires when the user chooses a department. Clears `doctorCombo`, queries the doctor repository for the selected department, populates doctors, and selects the first doctor. |
| **`doctorCombo`** (`JComboBox<Doctor>`) | `ItemEvent` / `ActionEvent` | `ItemListener` / `ActionListener` | Fires when a doctor is selected. Refreshes available appointment time slots in `slotCombo` according to the doctor's roster. |
| **`slotCombo`** (`JComboBox<String>`) | `ItemEvent` | `ItemListener` | Captures the selected time slot. Confirms slot availability and updates appointment preview. |
| **`bookButton`** (`JButton`) | `ActionEvent` | `ActionListener` | Validates patient ID, department, doctor, and slot. Verifies double-booking conflicts, creates appointment, displays confirmation dialog, and resets `bookingInProgress = false`. |
| **`clearButton`** (`JButton`) | `ActionEvent` | `ActionListener` | Clears all input fields, resets combo boxes to initial indices, and clears status messages. |
| **`mainFrame`** (`JFrame` Window 'X') | `WindowEvent` | `WindowListener` / `WindowAdapter` (`windowClosing`) | Intercepts closing. Checks `if (bookingInProgress)`. If true, displays a confirmation dialog (`JOptionPane.showConfirmDialog`). If confirmed, proceeds to `dispose()`; otherwise cancels closing. |

---

## Task 2.2 – Design the Component Hierarchy

The containment tree below represents the visual and structural hierarchy of the application.

```
JFrame (HospitalAppointmentFrame) [BorderLayout]
│
├── JPanel (headerPanel) [FlowLayout.CENTER] (BorderLayout.NORTH)
│   ├── JLabel (lblTitle: "Hospital Outpatient Appointment Desk")
│   └── JLabel (lblSubtitle: "Fast-Track Patient Registration")
│
├── JPanel (formContainerPanel) [BorderLayout] (BorderLayout.CENTER)
│   └── JPanel (gridFormPanel) [GridLayout(4, 2, 10, 10)]
│       ├── JLabel ("Patient ID:")
│       ├── JTextField (txtPatientId)
│       ├── JLabel ("Department:")
│       ├── JComboBox<String> (cmbDepartment)
│       ├── JLabel ("Doctor:")
│       ├── JComboBox<Doctor> (cmbDoctor)
│       ├── JLabel ("Time Slot:")
│       └── JComboBox<String> (cmbSlot)
│
└── JPanel (bottomContainerPanel) [BorderLayout] (BorderLayout.SOUTH)
    ├── JPanel (actionButtonPanel) [FlowLayout.CENTER] (BorderLayout.NORTH)
    │   ├── JButton (btnBook: "Book Appointment")
    │   └── JButton (btnClear: "Clear Form")
    └── JPanel (statusPanel) [FlowLayout.LEFT] (BorderLayout.SOUTH)
        └── JLabel (lblStatus: "Ready")
```

### Component Hierarchy Class Mapping Table

| Hierarchy Level | Container / Component | Exact Java Class | Layout Manager | Purpose |
|---|---|---|---|---|
| **Top-Level Window** | Root Application Frame | `javax.swing.JFrame` | `BorderLayout` | Primary desktop window hosting root pane |
| **North Region** | Header Panel | `javax.swing.JPanel` | `FlowLayout` | Visual branding and desk title |
| **Center Region** | Form Container Panel | `javax.swing.JPanel` | `BorderLayout` / `EmptyBorder` | Padded wrapper ensuring clean spacing |
| **Center Inner** | Input Grid Panel | `javax.swing.JPanel` | `GridLayout(4, 2, 10, 10)` | Two-column alignment of labels and inputs |
| **Input Controls** | Patient ID Field | `javax.swing.JTextField` | None | Alphanumeric patient identifier input |
| **Selection Controls** | Department Selector | `javax.swing.JComboBox<String>` | None | Department dropdown list |
| **Selection Controls** | Doctor Selector | `javax.swing.JComboBox<Doctor>` | None | Department-dependent doctor dropdown |
| **Selection Controls** | Slot Selector | `javax.swing.JComboBox<String>` | None | Available consultation time slots |
| **South Region** | Bottom Panel | `javax.swing.JPanel` | `BorderLayout` | Houses buttons and status bar |
| **Action Controls** | Book & Clear Buttons | `javax.swing.JButton` | None | Action triggers for booking and reset |
| **Feedback Display** | Status Bar | `javax.swing.JLabel` | None | Instant status and error messages |

---

## Task 2.3 – Design the Handler Logic

The pseudocode below adheres to the **SIMATS Engineering Standard for Algorithmic Pseudocode**.

### (a) Pseudocode: Refreshing Doctor List on Department Change

```
ALGORITHM RefreshDoctorList
INPUT: selectedDepartment (STRING)
OUTPUT: Populated cmbDoctor dropdown and status indicator

BEGIN
    IF selectedDepartment IS NULL OR selectedDepartment IS EMPTY THEN
        CALL cmbDoctor.removeAllItems()
        CALL cmbDoctor.setEnabled(FALSE)
        CALL cmbSlot.removeAllItems()
        CALL cmbSlot.setEnabled(FALSE)
        RETURN
    ENDIF

    CALL cmbDoctor.removeAllItems()
    CALL cmbSlot.removeAllItems()

    // Retrieve cached doctor list for chosen department
    SET doctorList <- CALL doctorMap.GET(selectedDepartment)

    IF doctorList IS NOT NULL AND doctorList.SIZE() > 0 THEN
        FOR EACH doctor IN doctorList DO
            CALL cmbDoctor.addItem(doctor)
        ENDFOR
        CALL cmbDoctor.setEnabled(TRUE)
        CALL cmbDoctor.setSelectedIndex(0)
        CALL RefreshTimeSlots(cmbDoctor.getSelectedItem())
        SET lblStatus.text <- "Doctors loaded for department: " + selectedDepartment
    ELSE
        CALL cmbDoctor.addItem("No doctors available")
        CALL cmbDoctor.setEnabled(FALSE)
        CALL cmbSlot.setEnabled(FALSE)
        SET lblStatus.text <- "Warning: No active specialists found."
    ENDIF
END
```

---

### (b) Pseudocode: Validating and Booking the Appointment

```
ALGORITHM ValidateAndBookAppointment
INPUT: patientId (STRING), department (STRING), doctor (DOCTOR), slot (STRING)
OUTPUT: Confirmation Dialog OR Error Notification

BEGIN
    // Step 1: Input Validation
    IF patientId IS NULL OR TRIM(patientId) IS EMPTY THEN
        CALL ShowErrorDialog("Validation Error", "Patient ID cannot be empty.")
        CALL txtPatientId.requestFocus()
        RETURN
    ENDIF

    IF NOT MatchesPattern(patientId, "^PAT-[0-9]{4,6}$") THEN
        CALL ShowErrorDialog("Format Error", "Patient ID must follow format: PAT-XXXX (e.g., PAT-1001).")
        CALL txtPatientId.requestFocus()
        RETURN
    ENDIF

    IF department IS NULL OR department EQUALS "Select Department" THEN
        CALL ShowErrorDialog("Selection Error", "Please select a valid medical department.")
        RETURN
    ENDIF

    IF doctor IS NULL THEN
        CALL ShowErrorDialog("Selection Error", "Please select an attending doctor.")
        RETURN
    ENDIF

    IF slot IS NULL OR slot EQUALS "Select Slot" THEN
        CALL ShowErrorDialog("Selection Error", "Please select an appointment time slot.")
        RETURN
    ENDIF

    // Step 2: Conflict Check & Booking Execution
    SET bookingInProgress <- TRUE
    SET lblStatus.text <- "Verifying slot availability and booking..."

    SET isSlotAvailable <- CALL appointmentService.CheckAvailability(doctor.getId(), slot)
    IF NOT isSlotAvailable THEN
        CALL ShowErrorDialog("Slot Conflict", "The selected slot is already booked. Please choose another slot.")
        SET bookingInProgress <- FALSE
        RETURN
    ENDIF

    SET appointmentId <- CALL appointmentService.CreateBooking(patientId, doctor.getId(), department, slot)

    IF appointmentId IS NOT NULL THEN
        SET bookingInProgress <- FALSE
        CALL ShowInformationDialog("Booking Confirmed", 
             "Appointment successfully scheduled!\nBooking ID: " + appointmentId + 
             "\nPatient: " + patientId + "\nDoctor: " + doctor.getName() + "\nSlot: " + slot)
        SET lblStatus.text <- "Appointment " + appointmentId + " confirmed."
        CALL ResetFormFields()
    ELSE
        SET bookingInProgress <- FALSE
        CALL ShowErrorDialog("Database Error", "Unable to finalize appointment. Please try again.")
        SET lblStatus.text <- "Booking failed due to an internal system error."
    ENDIF
END
```

---

## Task 2.4 – Trace the Event Sequence

### Sequence of Method Calls from Opening to Closing

1. **Initialization & Window Startup:**
   - `main()` invokes `SwingUtilities.invokeLater(...)`.
   - `HospitalAppointmentFrame.<init>()` allocates components.
   - `initComponents()` constructs container hierarchy and registers listeners.
   - `frame.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE)`.
   - `frame.addWindowListener(new WindowAdapter() { ... })`.
   - `frame.pack()` and `frame.setVisible(true)`.
   - The OS window peer is created $\to$ EDT dispatches `windowOpened(WindowEvent)`.
   - Initial department list loaded $\to$ fires `itemStateChanged(ItemEvent.SELECTED)` $\to$ populates default doctors $\to$ calls `paintComponent(Graphics)`.

2. **Receptionist Interaction Flow:**
   - Receptionist focuses and types into `txtPatientId`: `insertUpdate(DocumentEvent)` fires $\to$ sets `bookingInProgress = true`.
   - Receptionist changes department: `itemStateChanged(ItemEvent)` fires on `cmbDepartment` $\to$ calls `refreshDoctorList()` $\to$ `cmbDoctor.removeAllItems()` $\to$ `cmbDoctor.addItem(...)`.
   - Receptionist selects doctor: `itemStateChanged(ItemEvent)` fires on `cmbDoctor` $\to$ updates `cmbSlot`.
   - Receptionist clicks "Book Appointment": `actionPerformed(ActionEvent)` fires on `btnBook` $\to$ calls `validateAndBook()` $\to$ generates appointment ID $\to$ `JOptionPane.showMessageDialog()` displayed $\to$ sets `bookingInProgress = false`.

3. **Window Closing Flow:**
   - Receptionist clicks window 'X' close box.
   - OS emits close signal $\to$ AWT Event Queue catches native event $\to$ dispatches `windowClosing(WindowEvent)`.
   - Handler evaluates `if (bookingInProgress)`:
     - If `bookingInProgress == true`: Displays `JOptionPane.showConfirmDialog(frame, "A booking is currently in progress. Discard and exit?", "Confirm Exit", YES_NO_OPTION)`.
       - If user clicks **NO**: Handler returns; window remains open; operation continues unharmed.
       - If user clicks **YES**: Calls `frame.dispose()`.
     - If `bookingInProgress == false`: Calls `frame.dispose()` immediately.
   - Calling `dispose()` releases native display resources $\to$ dispatches `windowClosed(WindowEvent)` $\to$ JVM terminates cleanly.

### Comparison with Applet Life-Cycle Methods

| Applet Life-Cycle Method | Purpose in Applet | Corresponding Swing GUI Sequence / Method |
|---|---|---|
| **`init()`** | One-time initialization when browser loads the applet | Constructor (`HospitalAppointmentFrame()`) and `initComponents()` where controls are created and layout is configured. |
| **`start()`** | Invoked when applet becomes visible or user navigates to page | `frame.setVisible(true)` and `windowOpened(WindowEvent)`. Triggers initial data loading and starts active timers. |
| **`paint(Graphics g)`** | Renders the visual appearance of the applet | `paint(Graphics)` / `paintComponent(Graphics)` dispatched automatically by the EDT whenever regions require redrawing. |
| **`stop()`** | Suspends execution when user navigates away | Window iconified (`windowIconified`) or focus lost; active background polling threads suspended. |
| **`destroy()`** | Final cleanup before applet is unloaded from memory | `windowClosing()` confirmation check followed by `frame.dispose()` and `windowClosed()`, terminating worker threads and DB pools. |

---

## Alternative Concept and Complexity Analysis

### Comparison: In-Memory Map Cache vs. Database Query per Department Change

Let $d$ be the total number of departments and $m$ be the total number of doctors across all departments.

| Metric | Approach A: In-Memory Cache (`Map<String, List<Doctor>>`) | Approach B: Query Database on Each Change |
|---|---|---|
| **Query Strategy** | Pre-fetches all departments and doctors once at startup into memory | Issues `SELECT * FROM doctors WHERE department_id = ?` on every dropdown selection |
| **Time per Department Change** | **$O(1)$** hash table lookup time: $T = O(1) + O(m_d)$ to populate dropdown | **$O(\log m)$** (indexed) or **$O(m)$** scan + network latency $R \approx 10-100\text{ ms}$ |
| **Client Memory Footprint (Space)** | **$O(d + m)$** heap memory retained in client JVM | **$O(1)$** transient space; only the active department's doctors are retained |
| **UI Responsiveness** | Instantaneous (< 1 millisecond); zero perceived UI delay | Noticeable lag or UI stutter if executed on EDT; requires background thread |
| **Data Freshness / Consistency** | May become stale if a new doctor is added/removed in the DB during runtime | Always 100% up-to-date with current database state |
| **Database Server Load** | Minimal: 1 query at startup per client application session | High: $N$ clients switching departments generate continuous query traffic |

### Mathematical Big-O Evaluation:
1. **Time Complexity:**
   - **Approach A (Map Cache):**
     $$\text{Time}_{\text{cache}} = T_{\text{hash}}(1) + k \cdot T_{\text{insert}} \implies O(m_d)$$
     where $m_d$ is the number of doctors in the selected department ($m_d \ll m$). Typical execution time: $\approx 0.05\text{ ms}$.
   - **Approach B (Database Lookup):**
     $$\text{Time}_{\text{db}} = t_{\text{network\_RTT}} + t_{\text{SQL\_parse}} + O(\log m) + O(m_d) \implies O(R + \log m)$$
     where $R$ is round-trip network latency. Typical execution time: $15\text{ ms} - 120\text{ ms}$.

2. **Space Complexity:**
   - **Approach A (Map Cache):**
     Each `Doctor` object requires $\approx 64$ bytes. For $d = 20$ departments and $m = 200$ doctors:
     $$\text{Space}_{\text{cache}} = (20 \times 32\text{ bytes}) + (200 \times 64\text{ bytes}) \approx 13.4\text{ KB}$$
     $13.4\text{ KB}$ is completely negligible on modern client machines with 8 GB to 32 GB RAM.
   - **Conclusion:** Pre-loading doctors into an in-memory `Map` yields superior UX and $O(1)$ lookup performance, making it the industry standard for relatively static reference datasets.
