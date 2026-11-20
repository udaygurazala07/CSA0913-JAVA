# Comprehensive Test Suite Specification

**Course:** Programming in Java (CSA09)  
**Student:** Kattiripalli Dhanush (192311094)  
**Assignment:** Assignment 4 – CO4 (Event-Driven GUI Design)  

---

## 1. Task 1: Student Fee Payment System (GUI Design Review)

| Test ID | Test Case Description | Test Input / Trigger | Expected Outcome | Execution Mode |
|---|---|---|---|---|
| **TC-01-01** | Empty Registration Number Validation | `regNo = ""` , `amount = "5000"` | Rejects with `IllegalArgumentException: "Registration number cannot be empty."` | Automated Unit Test |
| **TC-01-02** | Non-Numeric Amount Validation | `regNo = "192311094"`, `amount = "five_thousand"` | Rejects with `IllegalArgumentException: "Invalid amount format."` | Automated Unit Test |
| **TC-01-03** | Negative Fee Amount Validation | `regNo = "192311094"`, `amount = "-250"` | Rejects with `IllegalArgumentException: "Fee amount must be greater than zero."` | Automated Unit Test |
| **TC-01-04** | Valid Fee Payment Processing | `regNo = "192311094"`, `amount = "45000.00"` | Inserts into MySQL `fees` table via `PreparedStatement`, returns official receipt. | Automated Unit Test & GUI |
| **TC-01-05** | UI Responsiveness During Bank Delay | Click "Pay" | Background worker executes 1.5s delay; window remains draggable, buttons disable cleanly. | GUI Verification |
| **TC-01-06** | Receipt Button Functionality | Click "Receipt" after payment | Displays official formatted receipt dialog without clearing input fields. | GUI Verification |
| **TC-01-07** | Window Closing via WindowAdapter | Click window 'X' close box | Disposes frame and releases database connection cleanly. | GUI Verification |

---

## 2. Task 2: Hospital Outpatient Appointment Desk

| Test ID | Test Case Description | Test Input / Trigger | Expected Outcome | Execution Mode |
|---|---|---|---|---|
| **TC-02-01** | Initial Department Loading | Startup / Window Open | Loads 4 departments into `cmbDepartment`: Cardiology, Orthopaedics, Neurology, Paediatrics. | Automated Test & GUI |
| **TC-02-02** | Dynamic Specialist Filtering | Select "Cardiology" | Refreshes `cmbDoctor` to show only Dr. A. Sharma and Dr. R. Venkat. | Automated Test & GUI |
| **TC-02-03** | Normal Appointment Booking | Patient `PAT-1001`, Dr. A. Sharma, `09:00 AM - 09:30 AM` | Allocates Booking Reference `APT-1001`, updates status, displays confirmation dialog. | Automated Test & GUI |
| **TC-02-04** | Double-Booking Prevention | Patient `PAT-1002`, Dr. A. Sharma, same `09:00 AM - 09:30 AM` | Rejects booking with slot conflict notification dialog. | Automated Test & GUI |
| **TC-02-05** | In-Progress Window Closing Guard | Type in Patient ID, click window 'X' | Prompts confirmation dialog: *"A booking is currently in progress. Discard and exit?"* | GUI Verification |

---

## 3. Task 3: Campus Library Search Screen

| Test ID | Test Case Description | Test Input / Trigger | Expected Outcome | Execution Mode |
|---|---|---|---|---|
| **TC-03-01** | Keyword & Category Search Filtering | Query: `"Java"`, Category: `"Computer Science"` | Filters catalog to display Core Java and Effective Java. | Automated Test & GUI |
| **TC-03-02** | Table Pagination | Click `"Next >"` | Renders page 2 of results; disables Next button when reaching final page. | Automated Test & GUI |
| **TC-03-03** | Book Reservation Action | Select book row, click `"Reserve Selected Book"` | Updates status to `"Reserved"`, prevents double reservation of same book. | Automated Test & GUI |
| **TC-03-04** | Window Resizing Behavior (Compact $550 \times 350$) | Resize window to minimum size | Controls align neatly; table shows 4-5 rows with scrollbar. | Visual Inspection / Screenshot |
| **TC-03-05** | Window Resizing Behavior (Medium $800 \times 500$) | Resize window to medium size | Search bar and buttons maintain height; results table claims extra width/height. | Visual Inspection / Screenshot |
| **TC-03-06** | Window Resizing Behavior (Expanded $1050 \times 700$) | Resize window to large size | Results area expands to fill remaining space; buttons retain standard size. | Visual Inspection / Screenshot |

---

## 4. Task 4: Drawing Pad (Event-Handling Forensics)

| Test ID | Test Case Description | Test Input / Trigger | Expected Outcome | Execution Mode |
|---|---|---|---|---|
| **TC-04-01** | `@Override` Compiler Guard | Misspell `mouseclicked` with `@Override` | Compiler halts with error: `method does not override or implement a method from a supertype`. | `javac` Verification |
| **TC-04-02** | Correct Method Overriding | Correctly spell `mouseClicked` | Clicking mouse adds coordinates to `points` list, increments counter. | Automated Test & GUI |
| **TC-04-03** | Click Count Label Update | Click 6 distinct locations | Status label updates dynamically to `"Clicks: 6"`. | GUI Verification |
| **TC-04-04** | State Persistence Across Minimize/Restore | Minimize window to taskbar, restore back to desktop | All 6 dots survive OS repainting and are redrawn centered on stored points. | Visual Inspection / Screenshot |

---

## 5. Task 5: Currency Converter (Event-Handling Styles)

| Test ID | Test Case Description | Test Input / Trigger | Expected Outcome | Execution Mode |
|---|---|---|---|---|
| **TC-05-01** | Style A: Frame Implements ActionListener | Enter 10000 INR, select USD, click Convert | Result displays `120.00 USD`; shows dialog confirmation. | Automated Test & GUI |
| **TC-05-02** | Style B: Named Inner Class | Enter 10000 INR, select EUR, click Convert | Result displays `110.00 EUR`; shows dialog confirmation. | Automated Test & GUI |
| **TC-05-03** | Style C: Anonymous Inner Class | Enter 5000 INR, select USD, click Convert | Result displays `60.00 USD`; shows dialog confirmation. | Automated Test & GUI |
| **TC-05-04** | Style D: Lambda Expression (TODO 1 & 2) | Enter 10000 INR, select USD, click Convert | Result displays `120.00 USD`. | Automated Test & GUI |
| **TC-05-05** | Enter Key Trigger | Type amount and press Enter key | Triggers conversion immediately without requiring mouse click. | GUI Verification |
| **TC-05-06** | Invalid Number Format Handling | Enter `"abc"` or `""` | Result label displays `"Invalid amount"` without crashing. | Automated Test & GUI |

---

## 6. Task 6: Campus Event Registration Desktop Application

| Test ID | Test Case Description | Test Input / Trigger | Expected Outcome | Execution Mode |
|---|---|---|---|---|
| **TC-06-01** | Valid Student Registration | Roll: `192311094`, Valid Email, Event 2 | Inserts into MySQL, displays confirmation dialog, refreshes table. | Integration Test & GUI |
| **TC-06-02** | Reject Malformed Email | Email: `"invalid_email_no_at"` | Rejects with `ValidationException: "Invalid Email format"`. | Automated Test & GUI |
| **TC-06-03** | Reject Blank Name | Name: `""` | Rejects with `ValidationException: "Student Full Name is required"`. | Automated Test & GUI |
| **TC-06-04** | Prevent Duplicate Event Registration | Re-register `192311094` for Hackathon 2026 | Rejects with `DuplicateRegistrationException`; displays duplicate alert dialog. | Automated Test & GUI |
| **TC-06-05** | View All Registrations in Table | Select Committee Table Tab | Asynchronously queries DB via `SwingWorker` and renders all registrations. | GUI Verification |
| **TC-06-06** | Dynamic Event Filtering | Filter by `"National AI & Robotics Hackathon 2026"` | Updates `JTable` to display only students registered for Hackathon. | GUI Verification |
| **TC-06-07** | Export Registration Roster to CSV | Click `"Export to CSV"` | Generates formatted CSV file with headers and proper escaping. | Automated Test & File Check |
