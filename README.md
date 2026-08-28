# 🖥️ Educational PIC16F72 Microcontroller Simulator with Process Scheduling

An educational software simulator that helps students understand the internal working of the **PIC16F72 8-bit microcontroller** while demonstrating **CPU scheduling algorithms** in a simple and interactive way.

---

# 📌 Project Overview

The PIC16F72 executes instructions, manages memory, controls peripherals, and responds to interrupts. These internal operations are often difficult to visualize in hardware. This simulator recreates them in software and combines concepts from:

- Microprocessor Architecture
- Data Structures
- Operating Systems

---

# 🎯 Problem Objective

The objective of this project is to develop a software-based simulator that enables users to:

- Execute PIC16F72 instructions
- Understand the Program Counter and CPU organization
- Perform register and memory operations
- Visualize stack operations and STATUS flags
- Simulate GPIO, timers, and interrupts
- Create and manage processes
- Demonstrate FCFS, Round Robin, and Priority Scheduling
- Understand context switching

---

# ✨ Features

- 8-bit CPU Simulation
- Working Register (W)
- Program Counter (PC)
- STATUS Register
- Program & Data Memory
- Hardware Stack
- GPIO Ports
- Timer0 & Interrupt Controller
- Process Control Block (PCB)
- Ready Queue & Context Switching
- FCFS, Round Robin & Priority Scheduling

---

# 🏗️ System Architecture

> Insert the architecture image below in your repository as `docs/images/system_architecture.jpg`.

![PIC16F72 System Architecture](docs/images/system_architecture.jpg)

---

# 👥 Team Responsibilities

> Insert the responsibility table image below as `docs/images/team_responsibility.jpg`.

![Team Responsibilities](docs/images/team_responsibility.jpg)

### Team Members

| Member | Primary Responsibility | Supporting Responsibility |
|---------|------------------------|---------------------------|
| **Naseef** | CPU & Instruction Execution | Integration & GitHub |
| **Moksha** | Memory & Stack | CPU Support |
| **Anas** | Data Structures & Process Management | Testing |
| **Chinmay** | OS Scheduling & Context Switching | UI & Integration |

---

# 💻 Programming Language

**Java**

Java has been selected because it provides:

- Object-Oriented Programming
- Modular class-based design
- Easy implementation of queues and scheduling algorithms
- Efficient memory management
- Simple GUI development using Java Swing/JavaFX

---

# 📂 Project Structure

```text
Educational-PIC16F72-Simulator/

docs/
 ├── images/
 ├── Meeting_Minutes/
 ├── Weekly_Status/
 └── Design_Documents/

src/
 ├── cpu/
 ├── memory/
 ├── scheduler/
 ├── peripherals/
 └── Main.java

tests/
programs/
README.md
```

---

# 🚀 Development Plan

### Week 1
- Repository setup
- Documentation
- Architecture study
- Team responsibility allocation

### Week 2
- CPU implementation
- Memory module
- Instruction execution
- Timer & interrupt simulation

### Future Phases
- PCB & Ready Queue
- Context Switching
- FCFS, Round Robin & Priority Scheduling
- Integration & Testing

---

# 🎓 Expected Outcome

The completed simulator will serve as an educational learning tool where students can observe how the **PIC16F72 microcontroller** executes instructions while understanding **process scheduling and context switching** through practical simulation.

**Microprocessor Architecture + Data Structures + Operating Systems**
