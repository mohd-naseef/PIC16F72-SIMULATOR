# PIC16F72 Processor Architecture – Basic Study

## 1. What are we studying?

Our project is a **software simulator for the PIC16F72 microcontroller**.

Before starting the actual implementation, we need to understand the main parts of the processor and decide which parts have to be represented in our simulator.

The main areas are:

- CPU
- Memory
- Peripherals
- Clock and timing

---

## 2. CPU / Core

The CPU is basically the part that **executes the instructions**.

The important parts we need to model are:

### 8-bit ALU
The ALU does the actual calculations and logical operations.

For example:
- Addition
- Subtraction
- AND
- OR
- Other logical operations

### W Register
The **W (Working) Register** is used while executing instructions.

It temporarily holds values that the CPU needs during an operation.

### Program Counter (PC)
The **PC** tells the CPU which instruction it should work on next.

So, during execution, the PC keeps track of the current instruction location.

### Status Flags
The status register contains information about the result or state of an operation.

The study identifies:
- C
- Z
- P
- TO

as status information that needs to be represented.

### 8-Level Stack
The stack stores **return information** during program execution.

This is useful when the program goes to another location and later needs to return back.

So, in our simulator, the stack will also be represented.

---

## 3. Memory

The simulator has to represent two main types of memory.

### Program Memory

- Size: **2K × 14 bits**
- Stores the instructions that the processor executes.

In simple words, this is where the program/instructions are kept.

### Data Memory

- Size: **128 × 8 bits**
- Used to store data while the program is running.

The simulator will also maintain a **memory map** and a **register/SFR map** so that the CPU can access the required memory locations and registers.

---

## 4. How an Instruction is Executed

The basic flow is:

**Fetch → Decode → Execute → Update CPU State**

### Fetch
The **Program Counter (PC)** tells us where the required instruction is located.

The simulator gets that instruction from program memory.

### Decode
The instruction is checked to understand **what operation it is asking the CPU to perform**.

### Execute
The CPU performs the operation using the ALU, W register, memory, or other required components.

### Update CPU State
After execution, the required registers, flags, PC, memory, etc. are updated.

So the simple idea is:

> **PC finds the instruction → instruction is understood → operation is performed → CPU state is updated**

---

## 5. Peripherals

Apart from the CPU and memory, the PIC16F72 also has peripherals.

The main ones considered for our simulator are:

### PORTA and PORTB
These are used for **input and output**.

For example, the simulator can use them to represent external inputs or outputs connected to the microcontroller.

### Timer0
Timer0 is an **8-bit timer**.

It is used for timing-related operations.

### Watchdog Timer (WDT)
The Watchdog Timer is another timing-related part of the microcontroller.

### Interrupt Controller
Interrupts allow the processor to respond to certain events while a program is running.

The simulator needs to represent this interrupt-related behavior.

---

## 6. Clock and Timing

The simulator will also have a **Clock & Timing** part.

Its purpose is to represent:

- Instruction cycles
- Processor timing
- Timer operations

This helps the simulator behave more like the actual processor instead of simply running instructions one after another without timing.

---

## 7. Main Components We Need in the Simulator

| Area | Components |
|---|---|
| CPU | ALU, W Register, PC, Status Flags, Stack |
| Memory | Program Memory, Data Memory |
| Peripherals | PORTA, PORTB, Timer0, WDT, Interrupt Controller |
| Timing | Clock and Instruction Cycle |

---

## 8. Simple Overall Picture

The simulator can be understood like this:

```text
              PIC16F72 Simulator
                     |
        +------------+------------+
        |            |            |
       CPU         Memory      Peripherals
        |            |            |
   +----+----+    +--+--+    +----+---------+
   |    |    |    |     |    |    |    |    |
  ALU   W    PC  Program Data PORTA PORTB Timer0
              |   Memory Memory             |
           Status                          WDT
           Flags                           |
           Stack                     Interrupts

                     +
                Clock & Timing
```

The main idea is that all these parts work together to execute the program.

---

## 9. What We Actually Need to Implement

For our simulator, the important modules are:

1. **CPU module** – handles instructions and CPU operations.
2. **Register module** – represents W, PC and status information.
3. **Stack module** – handles the 8-level stack.
4. **Memory module** – handles program and data memory.
5. **Peripheral module** – handles PORTA, PORTB, Timer0, WDT and interrupts.
6. **Clock/Timing module** – handles instruction-cycle timing.
7. **Instruction execution module** – follows the Fetch → Decode → Execute process.

---

## 10. In Short

The PIC16F72 simulator is basically going to imitate the important parts of the real microcontroller.

The CPU executes the instructions, memory stores the program and data, peripherals handle things like I/O and timers, and the clock controls the timing.

The basic execution idea is:

**Get instruction → Understand instruction → Execute it → Update everything → Move to next instruction**

This gives us the basic architecture we can use before starting the actual implementation.

**Individual Contribution: Processor Architecture Study**
