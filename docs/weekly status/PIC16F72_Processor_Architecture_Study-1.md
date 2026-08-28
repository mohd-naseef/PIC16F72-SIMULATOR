# PIC16F72 Processor Architecture – Basic Study

## 1. Overview

Our project is a **software simulator for the PIC16F72 microcontroller**.  
Before implementation, we identified the main processor parts that need to be represented.

Main areas:
- CPU
- Memory
- Peripherals
- Clock and timing

## 2. CPU

The CPU executes the instructions. The main parts are:

- **ALU** – performs arithmetic and logical operations.
- **W Register** – temporarily holds values during operations.
- **Program Counter (PC)** – keeps track of the instruction location.
- **Status Flags** – stores information about the result/state of operations.
- **8-Level Stack** – stores return information during program execution.

## 3. Memory

### Program Memory
- **2K × 14 bits**
- Stores the program instructions.

### Data Memory
- **128 × 8 bits**
- Stores data used while the program is running.

The simulator will use memory and register/SFR maps to access these locations.

## 4. Instruction Execution

The basic execution process is:

**Fetch → Decode → Execute → Update**

The PC identifies the instruction, the instruction is decoded, and then the CPU performs the required operation. After that, the CPU state is updated.

## 5. Peripherals

The main peripherals included in the simulator are:

- **PORTA and PORTB** – input/output
- **Timer0** – 8-bit timer
- **WDT** – watchdog timer
- **Interrupt Controller** – handles interrupt-related operations

## 6. Clock and Timing

The simulator will include clock and timing logic to represent **instruction cycles and timer operations**.

## 7. Main Components

| Part | Components |
|---|---|
| CPU | ALU, W Register, PC, Flags, Stack |
| Memory | Program Memory, Data Memory |
| Peripherals | PORTA, PORTB, Timer0, WDT, Interrupts |
| Timing | Clock, Instruction Cycle |

## 8. Conclusion

These are the main PIC16F72 components we need to represent in our simulator. This gives us a basic reference for developing the CPU, memory and peripheral parts.

**Individual Contribution: Processor Architecture Study**
