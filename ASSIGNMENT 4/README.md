# Programming in Java – Assignment 4 (CO4)

## Event-Driven GUI Design & JDBC Integration

### Student Details

| Details | Information |
|---|---|
| Student Name | G Uday Siva Sai |
| Register Number | 192425259 |
| Department | Computer Science and Engineering |
| Institution | SIMATS Engineering |
| Course | CSA09 – Programming in Java |
| Course Outcome | CO4 |
| Java Version | Java 27 |
| Database | MySQL 8.0.40 |
| GitHub | https://github.com/udaygurazala07/CSA0913-JAVA |

---

## 1. About the Assignment

This assignment covers Java GUI development, event handling, Swing, AWT, and JDBC database connectivity.

### Topics Covered
- Event Delegation Model
- Event Listeners
- Inner and Anonymous Classes
- AWT Components
- Swing Components
- Layout Managers
- Menus and Dialogs
- Lambda Expressions
- JDBC and MySQL Integration

---

## 2. Tasks

| Task | Title | Marks |
|---|---|---:|
| Task 1 | GUI Design Review | 6 |
| Task 2 | Hospital Appointment System | 8 |
| Task 3 | Library Search Interface | 9 |
| Task 4 | Drawing Pad | 7 |
| Task 5 | Currency Converter | 7 |
| Task 6 | Campus Event Registration System | 13 |
| **Total** | | **50** |

---

## 3. Project Structure

```text
Java-Assignment-4/
│
├── README.md
├── lib/
│   └── mysql-connector-j-26.10.0.jar
│
├── Task01-GUI-Design-Review/
│   ├── Theory/
│   └── src/
│
├── Task02-Hospital-Appointment/
│   ├── Theory/
│   ├── Diagrams/
│   └── src/
│
├── Task03-Library-Search/
│   ├── Theory/
│   ├── Screenshots/
│   └── src/
│
├── Task04-Drawing-Pad/
│   ├── Theory/
│   ├── Screenshots/
│   └── src/
│
├── Task05-Currency-Converter/
│   ├── Theory/
│   ├── Screenshots/
│   ├── FrameActionListener/
│   ├── NamedInnerClass/
│   ├── AnonymousInnerClass/
│   └── LambdaExpression/
│
├── Task06-Campus-Event-Registration/
│   ├── README.md
│   ├── database/
│   ├── Screenshots/
│   └── src/
│
├── Diagrams/
├── Test-Results/
│   ├── Test-Cases.md
│   └── Execution-Results.md
│
└── Report/
    └── Assignment4-Report.pdf
```

---

## 4. Task Descriptions

### Task 1 – GUI Design Review
- Identify GUI design problems.
- Improve event handling.
- Separate GUI and service logic.
- Implement database operations using PreparedStatement.

### Task 2 – Hospital Appointment System
- Create a hospital appointment GUI.
- Display doctor information.
- Manage appointments.
- Detect appointment conflicts.

### Task 3 – Library Search Interface
- Create a library search screen.
- Use BorderLayout and FlowLayout.
- Test different window sizes.
- Capture screenshots.

### Task 4 – Drawing Pad
- Create a drawing application.
- Handle mouse-click events.
- Implement drawing functionality.
- Test repainting and state persistence.

### Task 5 – Currency Converter
- Create a currency converter.
- Implement four event-handling methods:
  1. ActionListener
  2. Named Inner Class
  3. Anonymous Inner Class
  4. Lambda Expression
- Validate user input.

### Task 6 – Campus Event Registration System
- Develop a Java Swing application.
- Connect the application with MySQL using JDBC.
- Register students for events.
- Display registration details.
- Prevent duplicate registrations.
- Implement event filtering and validation.

---

## 5. Compilation and Execution

Run the commands from the project root directory using Windows PowerShell.

### Task 1

```powershell
javac -cp "lib/mysql-connector-j-26.10.0.jar" -d Task01-GUI-Design-Review/bin Task01-GUI-Design-Review/src/*.java

java -cp "lib/mysql-connector-j-26.10.0.jar;Task01-GUI-Design-Review/bin" task01.Task01Test
```

### Task 2

```powershell
javac -d Task02-Hospital-Appointment/bin Task02-Hospital-Appointment/src/*.java

java -ea -cp Task02-Hospital-Appointment/bin task02.Task02Test
```

### Task 3

```powershell
javac -d Task03-Library-Search/bin Task03-Library-Search/src/*.java

java -cp Task03-Library-Search/bin task03.LibrarySearchFrame
```

### Task 4

```powershell
javac -d Task04-Drawing-Pad/bin Task04-Drawing-Pad/src/DrawPad.java Task04-Drawing-Pad/src/Task04Capture.java

java -cp Task04-Drawing-Pad/bin task04.Task04Capture
```

### Task 5

```powershell
javac -d Task05-Currency-Converter/bin Task05-Currency-Converter/*/*.java

java -cp Task05-Currency-Converter/bin task05.lambdaexpression.Converter
```

### Task 6 – Database Setup

```sql
mysql -u root -p
```

Run the database files:

```powershell
mysql -u root -p < Task06-Campus-Event-Registration/database/schema.sql

mysql -u root -p < Task06-Campus-Event-Registration/database/sample-data.sql
```

Compile and execute:

```powershell
javac -cp "lib/mysql-connector-j-26.10.0.jar" -d Task06-Campus-Event-Registration/bin Task06-Campus-Event-Registration/src/*/*.java

java -cp "lib/mysql-connector-j-26.10.0.jar;Task06-Campus-Event-Registration/bin" task06.presentation.MainFrame
```

---

## 6. Technologies Used

- Java 27
- Java AWT
- Java Swing
- Event Handling
- Lambda Expressions
- JDBC
- MySQL 8.0.40
- Git and GitHub

---

## 7. Project Verification

- [x] All 6 tasks completed.
- [x] Java programs implemented.
- [x] GUI applications developed.
- [x] MySQL database connected.
- [x] Test cases prepared.
- [x] Screenshots captured.
- [x] Theory documents completed.
- [x] Assignment PDF report prepared.

---


## 9. GitHub Repository

https://github.com/udaygurazala07/CSA0913-JAVA

---

<p align="center">
  <b>Programming in Java – Assignment 4</b><br>
  CO4 – Event-Driven GUI Design & JDBC Integration<br><br>
  Submitted by: <b>G Uday Siva Sai</b><br>
  Register Number: 192425259<br><br>
  <i>SIMATS Engineering</i>
</p>
