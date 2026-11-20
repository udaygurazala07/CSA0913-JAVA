# Task 1: GUI Design Review

## Task 1.1 – Diagnose the Design

### Original Junior Developer Code
```java
public class FeeWindow extends Frame implements ActionListener {
    TextField regNo = new TextField(); TextField amount = new TextField();
    Button pay = new Button("Pay"), clear = new Button("Clear"), receipt = new Button("Receipt");
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand() == "Pay") {
            double a = Double.parseDouble(amount.getText());
            Connection c = DriverManager.getConnection(URL, USER, PWD);
            c.createStatement().executeUpdate("INSERT INTO fees VALUES('" + regNo.getText() + "'," + a + ")");
            Thread.sleep(3000); // wait for bank confirmation
            System.out.println("Paid");
        } else { regNo.setText(""); amount.setText(""); }
    }
}
```

### Comprehensive Identification of Deficiencies

1. **Separation of Interface and Logic (Architectural Violation):**
   - The GUI class `FeeWindow` directly embeds business logic (fee validation), network/database logic (JDBC connection and SQL execution), and simulated third-party transaction delays (`Thread.sleep`).
   - *Impact:* The UI cannot be tested without a live database, database schemas cannot change without modifying the UI, and the code violates the Single Responsibility Principle (SRP).

2. **String Comparison of Action Command (`==` vs `.equals()`):**
   - The code uses `if (e.getActionCommand() == "Pay")`. In Java, `==` tests object reference identity, not string content equality.
   - *Impact:* While string literals in the same class may be interned, `getActionCommand()` can return dynamically created strings or modified commands where `==` evaluates to `false`. This causes the "Pay" action to fail silently and branch into the `else` block.

3. **Handling of Invalid or Empty Input:**
   - `Double.parseDouble(amount.getText())` is executed without prior validation.
   - If the user leaves the amount blank or enters non-numeric input (e.g., `"abc"` or `"12oo"`), an uncaught `NumberFormatException` is thrown.
   - There is also no check for empty `regNo` or negative/zero fee amounts.
   - *Impact:* The application abruptly crashes or aborts the event handler with a stack trace in the console, leaving the user with an unresponsive UI.

4. **Database Operations & Sleep on the Event Dispatch Thread (EDT):**
   - AWT/Swing uses a single-threaded execution model where all UI rendering, button repainting, and event dispatching occur on the Event Dispatch Thread (EDT).
   - Calling `DriverManager.getConnection()`, `c.createStatement().executeUpdate()`, and `Thread.sleep(3000)` directly inside `actionPerformed()` freezes the EDT for 3+ seconds plus database latency.
   - *Impact:* The window becomes completely frozen, stops repainting, cannot be dragged or minimized, and the OS marks the application as "Not Responding".

5. **Lack of User Feedback in the GUI:**
   - The success message is printed to the terminal console (`System.out.println("Paid")`).
   - *Impact:* Desktop end-users do not have access to standard output. From the user's perspective, clicking "Pay" freezes the window for 3 seconds and then provides zero visual confirmation whether the fee was accepted or failed.

6. **The Unused/Broken "Receipt" Button:**
   - Three buttons exist: `Pay`, `Clear`, and `Receipt`. However, the handler only checks `if (e.getActionCommand() == "Pay")`.
   - *Impact:* When the user clicks the "Receipt" button, execution falls into the `else` block, which wipes both input fields (`regNo.setText(""); amount.setText("");`). Instead of generating a receipt, clicking "Receipt" clears the form and loses the entered data!

7. **SQL Injection Vulnerability:**
   - The query is built using raw string concatenation: `"INSERT INTO fees VALUES('" + regNo.getText() + "'," + a + ")"`.
   - *Impact:* An input such as `REG101', 0); DROP TABLE fees; --` allows arbitrary SQL execution, data tampering, or unauthorized data destruction.

8. **Resource Leaks and Missing Window Closing:**
   - `Connection` and `Statement` objects are opened but never closed in a `finally` block or `try-with-resources`. Database cursors and connections leak, eventually exhausting the database connection pool.
   - `FeeWindow` extends `Frame` but registers no `WindowListener`. Clicking the window close button (`X`) does nothing, trapping the user.

9. **Unhandled Checked Exceptions:**
   - `DriverManager.getConnection()` and `executeUpdate()` throw `SQLException`. `Thread.sleep(3000)` throws `InterruptedException`.
   - In Java, `actionPerformed(ActionEvent e)` in `ActionListener` does not declare `throws Exception`. Because checked exceptions are neither caught nor handled, this code fails to compile.

### Deficiency vs User Experience Summary Table

| No. | Design Deficiency | Technical Root Cause | What the User Experiences |
|---|---|---|---|
| 1 | Tight Coupling | UI contains DB, business, and sleep logic | Application is brittle, untestable, and impossible to maintain or upgrade. |
| 2 | Reference Equality (`==`) | Compares references instead of String contents via `.equals()` | Clicking "Pay" may unpredictably fail and clear the screen instead of paying. |
| 3 | No Input Validation | Missing try-catch for `NumberFormatException` and null/blank checks | Typing letters or leaving blanks crashes the event thread with no error message. |
| 4 | Blocking the EDT | Heavy DB calls and `Thread.sleep(3000)` execute on the UI thread | GUI freezes, turns white, and OS shows "Application Not Responding". |
| 5 | Missing GUI Feedback | Output sent to `System.out.println` instead of Dialog/Label | User has no idea if the payment was successful, duplicate payments may occur. |
| 6 | Broken Receipt Button | Missing conditional branch for "Receipt"; falls into `else` | Clicking "Receipt" unexpectedly clears the entire form and deletes entered data. |
| 7 | SQL Injection | Direct string concatenation in SQL `Statement` | Malicious inputs can delete records, corrupt the database, or expose student data. |
| 8 | Resource Leaks & No Close | Missing `try-with-resources` and missing `WindowListener` | Database runs out of connections over time; window 'X' button does not close app. |
| 9 | Unhandled Checked Exceptions | `SQLException` and `InterruptedException` not enclosed in `try-catch` | Code fails compilation entirely; runtime errors cannot recover cleanly. |

---

## Task 1.2 – Repair the Design

### Architectural Design
To resolve these deficiencies, the design is refactored into a clean **Model-View-Service** architecture:
1. **`FeeService` (Service Layer):** Contains pure business logic. Validates registration numbers and fee amounts, performs database persistence using parameterized `PreparedStatement`, handles transaction logic, and simulates external payment confirmation asynchronously without touching UI elements.
2. **`FeeWindow` (Presentation Layer):** A user-friendly GUI built with AWT/Swing components. Collects inputs, updates labels, shows dialogs (`JOptionPane`), delegates processing to `FeeService` on a background thread (`Thread` / `SwingWorker`), and safely dispatches results back to the GUI.
3. **Dedicated Listeners / Lambdas:** Replaces the messy if-else chain with dedicated handlers for `Pay`, `Clear`, and `Receipt`.
4. **Window Management:** Registers `WindowAdapter` to properly dispose resources and terminate the application on close.

---

## Task 1.3 – Design Defence (Concise Justification)

> **Design Defence (< 100 words):**  
> "The redesigned architecture separates presentation from business logic by introducing `FeeService`. Because `FeeService` contains no GUI dependencies, validation rules and database transactions can be independently unit-tested using automated JUnit tests and mock objects without instantiating graphical frames. Furthermore, parameterized queries eliminate SQL injection risks, `try-with-resources` prevents connection leaks, and executing payment tasks on a background worker ensures the interface remains fully responsive. Modifying business rules, payment gateways, or UI layout can now be performed independently without risking collateral bugs in the other layer." (87 words)

---

## Alternative Concept and Complexity Analysis

### Comparison: Single Listener with If-Else Chain vs. Dedicated Listener Objects / Swing Actions

| Criterion | Single Listener + `if-else` Chain | Dedicated Listener Objects / `Action` |
|---|---|---|
| **Dispatch Mechanism** | Sequentially evaluates `getActionCommand().equals(...)` for each button | Direct virtual method dispatch (`invokevirtual`) via the component's registered listener reference |
| **Time Complexity** | $O(k)$ comparisons where $k$ is the number of buttons in the container | $O(1)$ constant-time invocation directly targeting the relevant callback |
| **Heap Memory (Space)** | $O(1)$ listener instance shared by all $k$ controls (~24 bytes total) | $O(k)$ listener instances (~24 to 32 bytes per listener object) |
| **Maintainability** | Poor: modifying or removing a button requires editing a monolithic `if-else` chain | Excellent: each button's logic is isolated, reusable, and cleanly decoupled |
| **Extensibility** | Violates Open-Closed Principle; adding a button alters existing conditional logic | Conforms to Open-Closed Principle; new controls add independent listeners |

### Detailed Big-O Complexity Derivation:
- **Time Complexity:**
  - In a single monolithic listener with $k$ buttons, an incoming `ActionEvent` must be matched against up to $k$ branches:
    $$T_{\text{chain}}(k) = \sum_{i=1}^{k} c_{\text{str}} \implies O(k)$$
    where $c_{\text{str}}$ represents the string comparison overhead. In the worst case (the last button or an unmatched command), $k$ string comparisons are evaluated.
  - With dedicated listener instances or `Action` bindings, the component directly holds a reference to its specific listener:
    $$T_{\text{direct}} = O(1)$$
    The AWT event queue retrieves the source button and immediately invokes its specific `actionPerformed()` method without linear searching.

- **Space Complexity:**
  - A single listener creates 1 object on the heap: $S_{\text{single}} = 1 \times 24\text{ bytes} \implies O(1)$.
  - Individual listeners allocate $k$ lightweight objects: $S_{\text{multi}} = k \times 24\text{ bytes} \implies O(k)$.
  - For typical GUIs with $k \le 50$, $50 \times 24\text{ bytes} \approx 1.2\text{ KB}$, an utterly negligible memory overhead on modern JVMs with gigabytes of heap, while eliminating $O(k)$ runtime dispatch latency and drastically reducing code defects.
