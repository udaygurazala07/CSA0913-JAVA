# Campus Smart-Parking System — Java Assignment 1

**Course Code:** CSA09 – Java Fundamentals & Object-Oriented Programming  
**Assignment Title:** Design, Implement and Validate an Object-Oriented Model for a Campus Smart-Parking System  
**Total Marks:** 50  
**Student Name:** Gurazala Uday Siva Sai  
**Register Number:** 192425259
**Department:** AI&ML, Saveetha School of Engineering (SIMATS)  
**Target Repository:** [https://github.com/udaygurazala07/CSA0913-JAVA)  
**Java Environment:** Java SE 17+ (Compatible with Java 8 through Java 21)

---

## 1. Project Overview
The **Campus Smart-Parking System** is an enterprise-grade object-oriented solution modeling a multi-level university parking facility serving students, faculty/staff, and visitors. The system automates vehicle entry, slot allocation across heterogeneous floors, ticket generation, duration and ceiling tariff calculation, EV charging metering, pass validation, and slot release.

The project demonstrates all core Object-Oriented Programming (OOP) concepts in Java:
- Data representation & array modeling (ragged/jagged arrays)
- Algorithmic control logic and decision branching
- Class design, constructor telescoping, and JVM memory lifecycle
- Static members and garbage collection mechanics
- Inheritance hierarchies (hierarchical and multilevel)
- Method overriding, `@Override`, `final`, and Object contracts (`equals`, `hashCode`, `toString`)
- Compile-time and runtime polymorphism with dynamic method dispatch
- Abstraction via abstract classes and interfaces (`Chargeable`)
- Encapsulation, defensive validation, and package architectures
- Day-4 Design Challenge: Adapting to e-bikes, delivery van dynamic surge pricing, visitor day-passes, and facility expansion to 5 floors.

---

## 2. Repository Structure

```
CSA09-RegisterNumber-Assignment1/
    |
    └── ASSIGNMENT 1/
          ├── Task01/
          │     ├── Task01_ProblemAnalysis.md
          │     ├── DomainModel.java
          │     └── Task01_Output.txt
          ├── Task02/
          │     ├── Task02_DataRepresentation.md
          │     ├── DataRepresentationDemo.java
          │     └── Task02_Output.txt
          ├── Task03/
          │     ├── Task03_ControlLogic.md
          │     ├── ParkingFacility.java      <-- Complete the Code
          │     └── Task03_Output.txt
          ├── Task04/
          │     ├── Task04_ClassDesignAndConstructors.md
          │     ├── ConstructorDemo.java
          │     └── Task04_Output.txt
          ├── Task05/
          │     ├── Task05_StaticAndLifeCycle.md
          │     ├── StaticLifeCycleDemo.java
          │     └── Task05_Output.txt
          ├── Task06/
          │     ├── Task06_InheritanceHierarchy.md
          │     ├── InheritanceDemo.java
          │     └── Task06_Output.txt
          ├── Task07/
          │     ├── Task07_MethodOverriding.md
          │     ├── OverridingDemo.java
          │     └── Task07_Output.txt
          ├── Task08/
          │     ├── Task08_Polymorphism.md
          │     ├── FeeDemo.java              <-- Complete the Code
          │     └── Task08_Output.txt
          ├── Task09/
          │     ├── Task09_Abstraction.md
          │     ├── AbstractionDemo.java
          │     └── Task09_Output.txt
          ├── Task10/
          │     ├── Task10_EncapsulationAndPackages.md
          │     ├── EncapsulationDemo.java
          │     └── Task10_Output.txt
          ├── Task11/
          │     ├── Task11_TestingAndDefense.md
          │     ├── SmartParkingSystem.java   <-- End-to-End Integrated Suite
          │     └── Task11_Output.txt
          ├── Task12/
          │     ├── Task12_DesignChallenge.md
          │     ├── AdaptedParkingSystem.java <-- Day-4 Adaptive Evolution
          │     └── Task12_Output.txt
          └── README.md
```

---

## 3. Task Summaries and Execution Results

| Task | Title & Concept | Key Files | Result Summary |
| :--- | :--- | :--- | :--- |
| **Task 01** | Problem Analysis & Objects (K4) | `Task01_ProblemAnalysis.md`, `DomainModel.java` | Identified candidate classes, nouns/verbs, business rules, and analyzed parallel arrays vs. OO. |
| **Task 02** | Data Representation (K4) | `Task02_DataRepresentation.md`, `DataRepresentationDemo.java` | Jagged array `String[floor][slot]` justification and resolution of the integer division billing truncation bug. |
| **Task 03** | Control Logic & Pseudocode (K4) | `Task03_ControlLogic.md`, `ParkingFacility.java` | **Completed Code:** Verified slot allocation, slot release, and full-capacity detection producing exact expected output. |
| **Task 04** | Classes & Constructors (K4) | `Task04_ClassDesignAndConstructors.md`, `ConstructorDemo.java` | Parameterized, chained (`this(...)`), and copy constructors with JVM stack vs. heap tracing. |
| **Task 05** | static Members & Object Life Cycle (K4) | `Task05_StaticAndLifeCycle.md`, `StaticLifeCycleDemo.java` | Global ticket counter, static methods/blocks, pass-by-value semantics, and GC eligibility on checkout. |
| **Task 06** | Inheritance Hierarchy (K4) | `Task06_InheritanceHierarchy.md`, `InheritanceDemo.java` | Hierarchical & multilevel taxonomy (`Vehicle` $\rightarrow$ `Car` $\rightarrow$ `ElectricCar`) with traced constructor execution. |
| **Task 07** | Method Overriding (K4) | `Task07_MethodOverriding.md`, `OverridingDemo.java` | Specialization of `ratePerHour()`, `@Override` enforcement, `final` immutability, and Object contract (`equals`/`hashCode`). |
| **Task 08** | Polymorphism (K4) | `Task08_Polymorphism.md`, `FeeDemo.java` | **Completed Code:** Verified dynamic method dispatch across upcast `Vehicle[]` array and justified why `Car.equals(ElectricCar)` is true. |
| **Task 09** | Abstraction (K4) | `Task09_Abstraction.md`, `AbstractionDemo.java` | Abstract class `Vehicle` vs. interface `Chargeable` with default telemetry method. |
| **Task 10** | Encapsulation & Packages (K4) | `Task10_EncapsulationAndPackages.md`, `EncapsulationDemo.java` | Packaging (`model`, `service`, `app`), validating setters, and wrapper autoboxing `==` cache pitfalls. |
| **Task 11** | Full Implementation & Defense (K5) | `Task11_TestingAndDefense.md`, `SmartParkingSystem.java` | Full end-to-end test suite: normal, boundary, invalid cases, and 100% reconciled hand-calculated Rs 320.0 audit. |
| **Task 12** | Day-4 Design Challenge (K5) | `Task12_DesignChallenge.md`, `AdaptedParkingSystem.java` | Integrated E-Bikes (flat Rs 15), Delivery Vans (2x peak 9AM–5PM), Visitor One-Day Pass, and 5-floor jagged expansion. |

---

## 4. Compilation and Execution Instructions

### Prerequisites
- JDK 8 or higher (JDK 17 or JDK 21 recommended).
- Ensure `javac` and `java` are available in your system path.

### Step-by-Step Execution Commands

```bash
# Navigate to the ASSIGNMENT 1 directory
cd "ASSIGNMENT 1"

# Task 01: Domain Model
javac Task01/DomainModel.java
java -cp Task01 DomainModel

# Task 02: Data Representation & Precision
javac Task02/DataRepresentationDemo.java
java -cp Task02 DataRepresentationDemo

# Task 03: Control Logic (Complete the Code)
javac Task03/ParkingFacility.java
java -cp Task03 ParkingFacility

# Task 04: Class Design & Constructors
javac Task04/ConstructorDemo.java
java -cp Task04 ConstructorDemo

# Task 05: Static Members & Lifecycle
javac Task05/StaticLifeCycleDemo.java
java -cp Task05 StaticLifeCycleDemo

# Task 06: Inheritance Hierarchy
javac Task06/InheritanceDemo.java
java -cp Task06 InheritanceDemo

# Task 07: Method Overriding & Equals
javac Task07/OverridingDemo.java
java -cp Task07 OverridingDemo

# Task 08: Polymorphism (Complete the Code)
javac Task08/FeeDemo.java
java -cp Task08 FeeDemo

# Task 09: Abstraction & Interfaces
javac Task09/AbstractionDemo.java
java -cp Task09 AbstractionDemo

# Task 10: Encapsulation & Data Validation
javac Task10/EncapsulationDemo.java
java -cp Task10 EncapsulationDemo

# Task 11: End-to-End System Implementation & Test Suite
javac Task11/SmartParkingSystem.java
java -cp Task11 SmartParkingSystem

# Task 12: Day-4 Design Challenge
javac Task12/AdaptedParkingSystem.java
java -cp Task12 AdaptedParkingSystem
```
