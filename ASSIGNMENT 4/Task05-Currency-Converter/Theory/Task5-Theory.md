# Task 5: Same Screen, Different Event-Handling Styles

## Task 5.1 – Four Implementations of Event Handling

In Java GUI programming, event listeners can be implemented using multiple structural paradigms. Below is the technical description of the four implementations:

### Style (a): Frame Itself Implements `ActionListener`
- The top-level class declares `public class ConverterFrameActionListener extends JFrame implements ActionListener`.
- The frame registers itself as the listener: `convertButton.addActionListener(this);`.
- All events converge into a single `public void actionPerformed(ActionEvent e)` method declared at the class level.
- Must inspect `e.getSource()` to distinguish event triggers.

### Style (b): Named Inner Class
- A dedicated inner class is defined inside the outer frame: `private class ConvertHandler implements ActionListener`.
- An instance is created and attached: `ConvertHandler handler = new ConvertHandler(); convertButton.addActionListener(handler);`.
- Encapsulates action handling in a named, distinct type while retaining direct access to outer frame private members (`amount`, `target`, `result`).

### Style (c): Anonymous Inner Class
- An inline, nameless class is instantiated directly at the point of listener registration:
  ```java
  convertButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
          convert();
      }
  });
  ```
- No separate class definition needed; code resides directly alongside component instantiation.

### Style (d): Lambda Expression
- Utilizing Java 8+ functional programming, `ActionListener` is a `@FunctionalInterface` containing exactly one abstract method (`actionPerformed`).
- A concise lambda replaces the boilerplate:
  ```java
  ActionListener listener = e -> convert();
  convertButton.addActionListener(listener);
  amount.addActionListener(listener);
  ```
- Delivers maximum brevity and clarity without verbose class syntax.

---

## Task 5.2 – Changing the Requirement (Impact Analysis)

### Requirement Modification:
1. Pressing **Enter** in the amount field (`JTextField`) must trigger conversion.
2. The converted result must appear in a dialog box (`JOptionPane.showMessageDialog`) in addition to updating the UI.

### Code Change Metrics Across All Four Styles

| Style | Modifications Required | Lines Added | Lines Modified | Refactoring Effort & Complexity |
|---|---|---|---|---|
| **Style (a): Frame Implements `ActionListener`** | 1. Add `amount.addActionListener(this);`<br>2. Update `actionPerformed()` with `if (e.getSource() == convertButton \|\| e.getSource() == amount)`<br>3. Add `JOptionPane.showMessageDialog` | +5 lines | 3 lines | **Moderate:** Conditionals in `actionPerformed` grow increasingly convoluted as sources multiply. |
| **Style (b): Named Inner Class** | 1. Reuse existing handler: `amount.addActionListener(new ConvertHandler());`<br>2. Insert `JOptionPane.showMessageDialog` inside `ConvertHandler.actionPerformed` | +3 lines | 2 lines | **Low to Moderate:** Clean reuse of the existing named handler instance without touching outer frame logic. |
| **Style (c): Anonymous Inner Class** | 1. Must either duplicate the entire 6-line anonymous inner class onto `amount`, OR extract it into an anonymous variable `ActionListener l = new ActionListener() {...}` and attach to both.<br>2. Add `JOptionPane` | +8 lines | 6 lines | **High:** Highly verbose and prone to code duplication if not manually extracted into a reusable reference. |
| **Style (d): Lambda Expression** | 1. Define single lambda: `ActionListener l = e -> convert();`<br>2. Register on both: `convertButton.addActionListener(l); amount.addActionListener(l);`<br>3. Add dialog inside `convert()` | +2 lines | 1 line | **Minimal:** Zero boilerplate; one reference bound effortlessly to both triggers. |

---

## Task 5.3 – Comprehensive Comparison of Styles

| Evaluation Dimension | (a) Frame Implements Listener | (b) Named Inner Class | (c) Anonymous Inner Class | (d) Lambda Expression |
|---|---|---|---|---|
| **Readability** | Poor: pollutes top-level frame class with event dispatch logic; requires cascading `if-else` | Moderate: clean separation into an explicit named type, but adds class overhead | Low: verbose syntax obscures simple business logic with boilerplate | **Outstanding:** Clean, declarative, and immediate intent; minimal syntax noise |
| **Access to Frame Fields** | Direct (`this.field`) | Direct (outer class lexical scope `OuterClass.this.field`) | Direct (outer class scope; requires captured locals to be `final`) | Direct (lexical scoping; requires captured locals to be effectively final) |
| **Reusability of Handler** | Poor: tightly coupled to the single frame instance | **High:** Can be instantiated multiple times or subclassed | Very Low: bound strictly to the single attachment point | **High:** Single lambda variable can be attached across multiple buttons, fields, and menus |
| **Ease of Modification** | Fragile: editing one handler risks breaking other branches in `actionPerformed` | High: logic is encapsulated in an isolated class unit | Cumbersome: repetitive editing if duplicated across triggers | **Seamless:** One-line edits; effortless binding to new triggers |
| **Suitability for Large Apps** | Unacceptable (anti-pattern in enterprise code) | Good for complex handlers (> 50 lines) with state | Acceptable for tiny one-off legacy code | **Optimal for modern Java; paired with Swing `Action` for complex workflows** |

### Professional Recommendation for Large-Scale Applications:
For large-scale enterprise Swing applications, the recommended standard is **Swing `Action` (`AbstractAction`) combined with Lambdas / Method References**.
- A Swing `Action` unifies text, icons, tooltips, enabled/disabled states, and action dispatch into a single reusable object.
- It can be attached simultaneously to a `JButton`, a `JMenuItem`, and bound to global keyboard accelerators (`KeyStroke` via `InputMap` and `ActionMap`).
- For lightweight triggers, **Lambdas / Method References** (`this::convert`) provide the most maintainable, defect-free code with zero architectural overhead.

---

## Alternative Concept and Complexity Analysis

### Comparison: Lambda vs. Method Reference (`this::convert`) vs. Shared Swing `Action`

| Architectural Pattern | Mechanism | Object Allocations (Space) | Work to Add New Trigger (Button, Menu, KeyStroke) |
|---|---|---|---|
| **Lambda Expression**<br>`e -> convert()` | Compiler generates a synthetic method and a lightweight bootstrap callsite via `invokedynamic`. | **1 instance:** $O(1)$ functional object (~24 bytes). | Minimal: Call `.addActionListener(lambda)` on the new component. Keyboard shortcuts require manual wiring. |
| **Method Reference**<br>`this::convert` | Syntactic sugar for lambda. Directly references instance method with zero parameter forwarding boilerplate. | **1 instance:** $O(1)$ functional object (~24 bytes). | Minimal: Identical to lambda; slightly cleaner syntax and zero synthetic wrapping. |
| **Shared Swing `Action`**<br>`class ConvertAction extends AbstractAction` | Encapsulates behavior, state, icon, name, and shortcut bindings into a unified Swing model. | **1 instance:** $O(1)$ Action object (~64 bytes). | **Trivial & Unified:** Directly pass to `new JButton(action)`, `new JMenuItem(action)`, and `InputMap/ActionMap`. Automatically syncs enabled state across all triggers! |

### Complexity and Memory Evaluation:
1. **Time Complexity:** All three options execute in **$O(1)$** constant time upon trigger dispatch.
2. **Space Complexity:**
   - A single shared `Action` or Lambda instance creates exactly $1$ object in heap memory: $S = O(1)$.
   - In contrast, creating distinct anonymous classes for $k$ triggers allocates $k$ separate `.class` metadata definitions in Metaspace and $k$ instances on heap ($O(k)$ space).
3. **Conclusion:** A shared Swing `Action` with method references delivers maximum reusability, lowest maintenance cost, and unified trigger management for professional GUI design.
