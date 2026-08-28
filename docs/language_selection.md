# Programming Language Selection Document

## 1. Selected Language
* **Language:** Java (JDK 17 or higher)
* **Approach:** Object-Oriented Programming (OOP)


## 2. Why We Chose Java
* **Course Requirement:** Java is the officially suggested and preferred language for this simulator project.
* **Easy to Model Hardware:** Using classes and objects makes it simple to break down parts of the chip (like CPU, Memory, Stack, and Peripherals) into separate files.
* **Built-in Data Structures:** Java has ready-to-use collections like `Queue`, `LinkedList`, and `ArrayList`, which makes building the process scheduler and ready queue much easier.
* **Runs Anywhere:** It works smoothly across Windows, Mac, and Linux, so our entire team can run the same code without setup issues.


## 3. Benefits for Our PIC16F72 Simulator
* **Clear Logic:** We can represent registers and flags using standard integers and bitwise operations (`&`, `|`, `^`, `<<`, `>>`).
* **Clean OS Implementation:** Writing scheduling algorithms (FCFS, Round Robin, Priority) and managing Process Control Blocks (PCBs) is straightforward using standard Java classes.
* **Team Collaboration:** Java's package system and strict type checking make it easier for the four of us to work on separate modules and merge them without breaking things.
* **Easy Testing:** We can write simple test cases in the `tests/` folder to check if our instruction decoding works properly.


## 4. Challenges and How We Will Handle Them
* **Signed Bytes in Java:**
  * *Problem:* Java bytes are signed (-128 to +127), but the PIC16F72 uses unsigned 8-bit values (0 to 255).
  * *Solution:* We will store register values in standard `int` variables and use `& 0xFF` to keep them within the 8-bit unsigned range.
* **Performance:**
  * *Problem:* Java is slightly heavier on memory compared to C/C++.
  * *Solution:* Since we are building an educational simulator for a simple 8-bit processor, performance differences will not be an issue.


## 5. Other Languages Considered
* We stuck with Java because it is the default preferred choice given in the project guidelines.
* Since we are using Java, we do not need extra faculty approval or justifications for choosing an alternative language.


## 6. Document Info
* **Prepared By:** Chinmay
* **Topic:** Language Selection & Setup
* **Status:** Finalized for Week 1
* **Branch:** week-01
