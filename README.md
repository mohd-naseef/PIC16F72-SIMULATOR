
# Educational PIC16F72 Microcontroller Simulator

## Project Overview

This project is a Java-based educational simulator for the PIC16F72
8-bit microcontroller.

The project combines:

- Microprocessor Architecture
- Data Structures
- Operating Systems

## Problem Objective

The main objective is to create a simple simulator that helps students
understand how the PIC16F72 works and how CPU scheduling manages
multiple processes.

## Problem Statement

The simulator will model basic processor components such as registers,
Program Counter, memory, stack, and instruction execution.

It will also include basic GPIO, timer, and interrupt simulation.

The project will represent multiple programs as processes and demonstrate
FCFS, Round Robin, and Priority Scheduling.

## Project Scope

The project will cover:

- CPU and instruction execution
- Registers and memory
- Stack
- GPIO and Timer
- Interrupts
- Process management
- Ready queue and PCB
- Context switching
- CPU scheduling

## Microcontroller

**PIC16F72**

The simulator will focus on the important features of the PIC16F72
required for this project rather than the complete microcontroller.

## Programming Language

**Java**

### Why Java?

Java was selected because it:

- Supports object-oriented programming
- Provides useful data structures
- Makes scheduling algorithms easy to implement
- Supports GUI development
- Is suitable for building a simulator

### Limitation

Java can simulate the microcontroller, but it cannot completely
reproduce real hardware behaviour.

## System Architecture

![System Architecture](docs/images/simulator_diagram.jpeg)

## Team

| Member | Responsibility |
|--------|----------------|
| Naseef | CPU & Instruction Execution |
| Moksha | Memory & Stack |
| Anas | Scheduling & Context Switching |
| Chinmay | Data Structures & Process Management |

## Development Plan

### Week 1
- Project setup
- PIC16F72 architecture study
- Language selection
- Initial architecture
- Team responsibility allocation

### Week 2
- CPU and memory implementation
- Instruction execution

### Later
- Process management
- Scheduling algorithms
- Context switching
- Peripherals
- Testing and integration

## Week 2 – Memory and Stack Implementation

### 1. RAM Read/Write

The simulator contains RAM for storing data.

- `read(address)` reads the value stored at a given RAM address.
- `write(address, value)` stores a value at the given RAM address.
- Only valid memory addresses are accessed.
- Values are stored as 8-bit values using `& 0xFF`.

Example:

    write(10, 25)
    read(10) → 25

The RAM read/write test passed successfully.

### 2. Stack

The simulator contains a stack for temporary storage of values.

- `push(value)` adds a value to the top of the stack.
- `pop()` removes and returns the top value from the stack.
- The stack follows the LIFO (Last In, First Out) principle.

Example:

    PUSH 10
    PUSH 20
    POP → 20
    POP → 10

The PUSH/POP and multiple PUSH/POP tests passed successfully.

### 3. Stack Pointer

The Stack Pointer (SP) keeps track of the current position of the stack.

- During PUSH, the value is stored and the Stack Pointer increases.
- During POP, the top value is removed and the Stack Pointer decreases.
- The Stack Pointer is reset to its initial value during RESET.

The Stack Pointer test passed successfully.

### 4. RESET

RESET returns the CPU memory and stack-related state to the initial state.

During RESET:

- The Stack Pointer is reset.
- The CPU registers/status are reset.
- The Program Counter is reset.
- Memory is returned to the expected initial state used by the tests.

The RESET test passed successfully.

### 5. Testing

The following tests were performed:

- RAM READ/WRITE – PASS
- PUSH/POP – PASS
- MULTIPLE PUSH/POP – PASS
- STACK POINTER – PASS
- RESET – PASS

### 6. Simplifications and Limitations

The stack uses a fixed-size array, so its size cannot grow dynamically.

- Pushing when the stack is full results in a stack overflow exception.
- Values are handled as 8-bit values.
- The implementation is a simplified software model of the PIC16F72 stack and is intended for simulator use.
