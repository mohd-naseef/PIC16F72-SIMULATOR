# Programming Language Selection Document

## 1. Selected Language
* **Language:** Java (JDK 17 or higher).
* **Design Paradigm:** Object-Oriented Programming (OOP)



## 2. Reasons for Selection
* **Course Standard & Alignment:** Java is the officially preferred and recommended programming language for the educational microcontroller simulator project.
* **Modular Architecture Support:** The object-oriented model allows hardware components (such as the CPU, RAM, Call Stack, Timer0, and GPIO ports) to be encapsulated as distinct, reusable software classes.
* **Standard Data Structures:** Java provides robust, built-in collections (`Queue`, `LinkedList`, `ArrayList`, and `HashMap`) that simplify the implementation of Process Control Blocks (PCBs), the OS Ready Queue, and instruction lookup tables.
* **Cross-Platform Portability:** Java’s "Write Once, Run Anywhere" (WORA) model ensures that the simulator runs identically on Windows, macOS, and Linux without platform-dependent compilation issues.



## 3. Advantages for the PIC16F72 Simulator Project
* **Clear Hardware Emulation:** Registers like `STATUS`, `WREG`, and `INTCON` can be cleanly modeled using primitive integer types and bitwise masks (`&`, `|`, `^`, `<<`, `>>`).
* **OS & Scheduler Integration:** Designing multi-process scheduling algorithms (FCFS, Round Robin, Priority) alongside context switching is clean and maintainable using Java class hierarchies and queues.
* **Maintainability & Collaboration:** Strong typing, compile-time error checks, and standard package management make it easier for all 4 team members to develop and merge separate modules smoothly.
* **Testing & Extensibility:** Unit testing frameworks (such as JUnit) can be added directly in the `tests/` folder for verifying opcode behavior and register state transitions.



## 4. Limitations and Mitigation Strategies
* **Unsigned Arithmetic Handling:** 
  * *Limitation:* Java does not have native unsigned 8-bit byte types (`byte` is signed, $-128$ to $+127$).
  * *Mitigation:* Use standard `int` variables and mask with `& 0xFF` (for 8-bit values) and `& 0x1FFF` (for 13-bit PC addresses) to emulate exact unsigned register behavior.
* **Runtime Overhead:** 
  * *Limitation:* Java runs on the JVM, which introduces slightly higher memory usage compared to low-level languages like C/C++.
  * *Mitigation:* Because the simulator models an educational 8-bit microcontroller subset, the performance overhead is negligible and well within acceptable bounds.



## 5. Alternative Language Evaluation
* No alternative language was required since the team chose the default preferred language (**Java**).
* As Java was selected, no special faculty justification or exemption was needed.



## 6. Document Metadata
* **Prepared By:** Chinmay (Team Member)
* **Module:** Technology Selection & Environment Setup
* **Status:** Accepted & Finalized for Week 1
* **Branch:** `week-01`
