# PIC16F72-SIMULATOR
## Selected Programming Language

* **Language:** Java (JDK 17 or higher)
* **Design Paradigm:** Object-Oriented Programming (OOP)

### Why We Selected Java
We chose Java because it is the officially recommended language for this simulator and fits our requirements well:
1. **Clear Modular Design:** With Java’s OOP support, we can create individual classes for each microcontroller component (like `CPU.java`, `Memory.java`, `Stack.java`, and `Peripherals.java`). This keeps our codebase organized and lets each team member work on separate modules without merge conflicts.
2. **Built-in Collections for OS Modeling:** Java provides native data structures like `Queue`, `LinkedList`, and `ArrayList`. These make it straightforward to implement the OS Ready Queue, Process Control Blocks (PCBs), and scheduling algorithms (Round Robin, FCFS, Priority).
3. **Cross-Platform Compatibility:** Because Java runs on the JVM, our entire team can write, build, and test the simulator seamlessly across Windows, macOS, and Linux without platform-specific build issues.
4. **Handling Unsigned 8-bit Math:** Although Java uses signed bytes, we can easily handle 8-bit register values by storing them in standard `int` types and using bitwise masks (`& 0xFF`).


