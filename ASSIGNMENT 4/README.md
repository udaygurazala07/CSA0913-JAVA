# Programming in Java – Assignment 4 (CO4)
## Event-Driven GUI Design & JDBC Integration

**Institution:** SIMATS Engineering, Saveetha Institute of Medical and Technical Sciences  
**Department:** Computer Science and Engineering  
**Course Code & Title:** CSA09 – Programming in Java  
**Course Outcome:** CO4 – Applets, the event delegation model, listeners and adapter classes, inner and anonymous classes, AWT components and controls, layout managers, menus and dialogs, Swing components and GUI–JDBC integration (Sessions 49–56)  
**Nature of Assignment:** Individual  
**Total Marks:** 50 Marks  
**Student Name:** Kattiripalli Dhanush  
**Register Number:** 192311094  
**GitHub Username:** Dhanushssmb  
**GitHub Repository:** [https://github.com/Dhanushssmb/java.git](https://github.com/Dhanushssmb/java.git)  
**Java Version:** `java version "27.0.1.0.3" 2026-10-25` (Oracle Corporation)  
**MySQL Server:** `MySQL Server 8.0.40` on localhost:3306  

---

## Executive Summary & Mark Distribution

| Task | Competency Assessed | Bloom's Level | Max Marks | Status | Evidence Location |
|---|---|---|---|---|---|
| **Task 1** | Diagnose and repair a poor event-handling design | K4 – Analyse | 6 | **Completed & Verified** | [`Task01-GUI-Design-Review/`](./Task01-GUI-Design-Review/) |
| **Task 2** | Design an event-driven interface from a requirement | K4 – Analyse | 8 | **Completed & Verified** | [`Task02-Hospital-Appointment/`](./Task02-Hospital-Appointment/) |
| **Task 3** | Design and implement layouts (Library Search) | K4 – Analyse | 9 | **Completed & Verified** | [`Task03-Library-Search/`](./Task03-Library-Search/) |
| **Task 4** | Debug event handling using failing interactions | K4 – Analyse | 7 | **Completed & Verified** | [`Task04-Drawing-Pad/`](./Task04-Drawing-Pad/) |
| **Task 5** | Compare event-handling styles under changing requirements | K5 – Evaluate | 7 | **Completed & Verified** | [`Task05-Currency-Converter/`](./Task05-Currency-Converter/) |
| **Task 6** | Independently design and build a layered GUI–JDBC application | K5 – Evaluate | 13 | **Completed & Verified** | [`Task06-Campus-Event-Registration/`](./Task06-Campus-Event-Registration/) |
| **TOTAL** | **Comprehensive Assignment 4 Suite** | **K4 & K5** | **50** | **100% COMPLETE** | **Report in `Report/Assignment4-Report.pdf`** |

---

## Repository Structure

```
Java-Assignment-4/
│
├── README.md                               <-- Master Project Documentation & Execution Guide
├── lib/
│   └── mysql-connector-j-26.10.0.jar       <-- MySQL JDBC Driver (Bundled)
│
├── Task01-GUI-Design-Review/
│   ├── Theory/
│   │   └── Task1-Theory.md                 <-- Diagnoses (9 deficiencies), Table, Defense (<100w), O(k) vs O(1)
│   └── src/
│       ├── FeeService.java                 <-- Refactored Service Layer with PreparedStatement
│       ├── FeeWindow.java                  <-- Decoupled Presentation Frame with Background Worker
│       └── Task01Test.java                 <-- Automated Test Suite (Validation & DB tests)
│
├── Task02-Hospital-Appointment/
│   ├── Theory/
│   │   └── Task2-Theory.md                 <-- Event Table, Component Hierarchy, SIMATS Pseudocode, Lifecycle Trace
│   ├── Diagrams/
│   │   └── Task02-Component-Hierarchy.png  <-- Component Containment Tree Diagram
│   └── src/
│       ├── Doctor.java                     <-- Specialist Model
│       ├── AppointmentService.java         <-- In-Memory Doctor Cache & Conflict Detector
│       ├── HospitalAppointmentFrame.java   <-- Working Hospital Outpatient Booking GUI
│       └── Task02Test.java                 <-- Automated Test Suite
│
├── Task03-Library-Search/
│   ├── Theory/
│   │   └── Task3-Theory.md                 <-- Layout Manager Justification, Resizing Analysis, Big-O comparison
│   ├── Screenshots/
│   │   ├── Task03-Size1-Compact-550x350.png
│   │   ├── Task03-Size2-Medium-800x500.png
│   │   └── Task03-Size3-Expanded-1050x700.png
│   └── src/
│       ├── LibrarySearchFrame.java         <-- Nested BorderLayout + FlowLayout Search & Table GUI
│       └── Task03Capture.java              <-- Multi-Resolution Automated Capture Utility
│
├── Task04-Drawing-Pad/
│   ├── Theory/
│   │   └── Task4-Theory.md                 <-- Forensics, Failing Interaction, @Override Analysis, Big-O Vector vs Raster
│   ├── Screenshots/
│   │   ├── Task04-Before-Clicks.png        <-- Initial window (0 clicks)
│   │   ├── Task04-After-Clicks.png         <-- 6 points clicked & drawn
│   │   └── Task04-After-Minimise-Restore.png <-- Window minimized & restored (Points survive!)
│   └── src/
│       ├── DrawPad.java                    <-- Completed TODO 1 & 2 DrawPad Implementation
│       ├── TestMisspelledOverride.java     <-- Evidence of @Override compilation error
│       └── Task04Capture.java              <-- Automated Click & Minimize-Restore Test Runner
│
├── Task05-Currency-Converter/
│   ├── Theory/
│   │   └── Task5-Theory.md                 <-- Comparison of 4 Styles, Code Change Metrics, Shared Action Big-O
│   ├── Screenshots/
│   │   ├── Task05-Valid-Conversion.png     <-- 10000 INR -> 120.00 USD
│   │   └── Task05-Invalid-Amount.png       <-- Invalid amount error handling
│   ├── FrameActionListener/
│   │   └── ConverterFrameActionListener.java <-- Style (a): Frame implements ActionListener
│   ├── NamedInnerClass/
│   │   └── ConverterNamedInnerClass.java    <-- Style (b): Named Inner Class
│   ├── AnonymousInnerClass/
│   │   └── ConverterAnonymousInnerClass.java <-- Style (c): Anonymous Inner Class
│   └── LambdaExpression/
│       └── Converter.java                  <-- Style (d): Completed TODO 1 & 2 Lambda Implementation
│
├── Task06-Campus-Event-Registration/
│   ├── README.md                           <-- Stages 1-4 Complete Specifications, Testable Statements, Big-O Analysis
│   ├── database/
│   │   ├── schema.sql                      <-- MySQL DDL with Composite UNIQUE constraint
│   │   └── sample-data.sql                 <-- 6 Events & Sample Student Registrations
│   ├── Screenshots/
│   │   ├── Task06-01-Registration-Form.png
│   │   ├── Task06-02-Committee-Table-View.png
│   │   ├── Task06-03-Filtered-By-Event.png
│   │   ├── Task06-04-Duplicate-Alert.png
│   │   └── Task06-05-Validation-Alert.png
│   └── src/
│       ├── model/
│       │   ├── Student.java
│       │   ├── Event.java
│       │   └── Registration.java
│       ├── dao/
│       │   ├── DatabaseConnection.java
│       │   ├── EventDAO.java
│       │   └── RegistrationDAO.java
│       ├── service/
│       │   ├── ValidationService.java
│       │   └── RegistrationService.java
│       └── presentation/
│           ├── RegistrationPanel.java
│           ├── RegistrationTablePanel.java
│           ├── MenuController.java
│           ├── MainFrame.java
│           └── Task06Demonstration.java     <-- Automated Full-Suite Verification
│
├── Diagrams/
│   ├── Task02-Component-Hierarchy.png      <-- High-Resolution Hierarchy Tree
│   └── Task06-Layered-Architecture.png     <-- 4-Layer UML Architecture Diagram
│
├── Test-Results/
│   ├── Test-Cases.md                       <-- 35 Exhaustive Test Case Specifications
│   └── Execution-Results.md                <-- Raw Execution Logs & MySQL Verification
│
└── Report/
    └── Assignment4-Report.pdf              <-- Publication-Grade Comprehensive PDF Report
```

---

## Compilation & Execution Instructions

Ensure `mysql-connector-j-26.10.0.jar` in `lib/` is on the classpath.

### Task 1: Student Fee Payment
```bash
# Compile
javac -cp "lib/mysql-connector-j-26.10.0.jar" -d Task01-GUI-Design-Review/bin Task01-GUI-Design-Review/src/*.java

# Run Automated Test Suite
java -cp "lib/mysql-connector-j-26.10.0.jar;Task01-GUI-Design-Review/bin" task01.Task01Test

# Launch GUI
java -cp "lib/mysql-connector-j-26.10.0.jar;Task01-GUI-Design-Review/bin" task01.FeeWindow
```

### Task 2: Hospital Outpatient Appointment Desk
```bash
# Compile
javac -d Task02-Hospital-Appointment/bin Task02-Hospital-Appointment/src/*.java

# Run Automated Test Suite
java -ea -cp Task02-Hospital-Appointment/bin task02.Task02Test

# Launch GUI
java -cp Task02-Hospital-Appointment/bin task02.HospitalAppointmentFrame
```

### Task 3: Campus Library Search Screen
```bash
# Compile
javac -d Task03-Library-Search/bin Task03-Library-Search/src/*.java

# Run Multi-Resolution Screenshot Capture Utility
java -cp Task03-Library-Search/bin task03.Task03Capture

# Launch Interactive GUI
java -cp Task03-Library-Search/bin task03.LibrarySearchFrame
```

### Task 4: Drawing Pad Forensics
```bash
# Compile
javac -d Task04-Drawing-Pad/bin Task04-Drawing-Pad/src/DrawPad.java Task04-Drawing-Pad/src/Task04Capture.java

# Run Automated Repaint & State Persistence Verification
java -cp Task04-Drawing-Pad/bin task04.Task04Capture

# Verify @Override Compiler Guard (produces compiler error)
javac Task04-Drawing-Pad/src/TestMisspelledOverride.java
```

### Task 5: Currency Converter (All 4 Styles)
```bash
# Compile All Styles
javac -d Task05-Currency-Converter/bin Task05-Currency-Converter/*/*.java

# Run Automated Screenshot & Test Capture
java -cp Task05-Currency-Converter/bin task05.Task05Capture

# Launch Style (d) Official Completed Code
java -cp Task05-Currency-Converter/bin task05.lambdaexpression.Converter
```

### Task 6: Campus Event Registration Desktop Application
```bash
# 1. Setup Database in MySQL
mysql -u root -p < Task06-Campus-Event-Registration/database/schema.sql
mysql -u root -p < Task06-Campus-Event-Registration/database/sample-data.sql

# 2. Compile All Layers
javac -cp "lib/mysql-connector-j-26.10.0.jar" -d Task06-Campus-Event-Registration/bin Task06-Campus-Event-Registration/src/*/*.java

# 3. Run Automated Demonstration Suite (Normal, Duplicate, Invalid, Filter, Export)
java -cp "lib/mysql-connector-j-26.10.0.jar;Task06-Campus-Event-Registration/bin" task06.presentation.Task06Demonstration

# 4. Launch Desktop Application
java -cp "lib/mysql-connector-j-26.10.0.jar;Task06-Campus-Event-Registration/bin" task06.presentation.MainFrame
```
