# Week 3 – CPU and Instruction Execution Status

## 1. Objective

The main objective of Week 3 was to implement and verify the CPU instruction execution part of the PIC16F72 Simulator.

The work focused on:

- CPU instruction execution
- FETCH, DECODE and EXECUTE operations
- Program Counter (PC) updates
- Working Register (W) updates
- STATUS register updates
- Data memory interaction
- Testing the implemented instructions

---

## 2. CPU Implementation Status

The CPU implementation supports the basic instruction execution required for the simulator.

The CPU performs instructions using the following sequence:

**FETCH → DECODE → EXECUTE**

During execution, the CPU updates the required registers, memory locations and program counter.

No major changes to the existing CPU implementation were required because the implemented functionality passed the Week 3 tests.

---

## 3. FETCH – DECODE – EXECUTE

### FETCH

The CPU fetches the instruction from Program Memory using the current Program Counter (PC).

After fetching the instruction, the PC is updated to point to the next instruction.

### DECODE

The fetched instruction is decoded using its opcode.

The opcode determines which operation the CPU has to perform.

### EXECUTE

The CPU executes the decoded instruction and updates the required register, memory location or PC.

---

## 4. Implemented Instructions

The following 8 instructions were tested:

| Instruction | Purpose |
|-------------|---------|
| MOVLW | Move literal value to W |
| MOVWF | Move W value to file register |
| ADDLW | Add literal value to W |
| SUBLW | Subtract W from literal value |
| ANDLW | Perform AND operation with W |
| INCF | Increment a file register |
| GOTO | Change the Program Counter |
| SLEEP | Stop CPU execution |

---

## 5. Register and Memory Operations

The CPU uses the following important components during instruction execution:

- **W Register** – stores working data used by instructions.
- **PC (Program Counter)** – points to the next instruction in Program Memory.
- **STATUS Register** – stores CPU status flags.
- **Data Memory** – stores file register values used by instructions.

The implemented instructions can modify W, Data Memory, PC and STATUS depending on the operation.

---

## 6. Testing

### Instruction Tests

All 8 implemented instructions were tested individually.

Test results:

- MOVLW – PASS
- MOVWF – PASS
- ADDLW – PASS
- SUBLW – PASS
- ANDLW – PASS
- INCF – PASS
- GOTO – PASS
- SLEEP – PASS

**Result: 8/8 instruction tests passed.**

### CPU Week 3 Tests

Additional CPU tests were created to verify the basic CPU execution flow.

| Test Case | Description | Result |
|-----------|-------------|--------|
| TC01 | FETCH and PC update | PASS |
| TC02 | DECODE | PASS |
| TC03 | EXECUTE MOVLW | PASS |
| TC04 | PC and W update | PASS |

**Result: 4/4 CPU Week 3 tests passed.**

---

## 7. Demonstration Program

A demonstration program was used to verify that multiple instructions work together during execution.

The final observed values were:

- W = 8
- PC = 10
- STATUS = 0
- RAM[20] = 11

These results show that the CPU can execute multiple instructions and update the required registers and memory.

---

## 8. Compilation Check

The main Java source files were compiled successfully using:

`javac -d out src\*.java`

No compilation errors were produced.

The instruction test was also compiled and executed successfully.

---

## 9. Testing Environment Note

The `DemonstrationProgramTest.java` file uses JUnit 5.

When it was manually compiled using `javac`, the JUnit library was not available in the classpath, resulting in a missing `org.junit.jupiter.api` error.

This is a test-environment dependency issue and not an error in the CPU implementation.

---

## 10. Integration

The CPU instruction execution works with:

- Program Memory
- Data Memory
- Program Counter
- W Register
- STATUS Register
- Process execution

The CPU can fetch instructions from Program Memory and execute them while updating the required state.

---

## 11. Work Completed

The following Week 3 CPU tasks were completed:

- CPU instruction execution verified
- FETCH operation tested
- DECODE operation tested
- EXECUTE operation tested
- PC update verified
- W register update verified
- Data memory interaction verified
- STATUS handling verified through instruction tests
- All 8 selected instructions tested
- CPU Week 3 test cases completed
- Demonstration program executed
- Source compilation verified
- Week 3 documentation prepared

---

## 12. Known Issues / Limitations

- The JUnit 5 dependency is not available when running the JUnit test directly using `javac`.
- The simulator uses a simplified implementation of some PIC16F72 hardware behaviour.
- Further integration testing can be performed with the complete simulator.

---

## 13. Conclusion

The Week 3 CPU and Instruction Execution work was completed and tested.

The CPU successfully performs the FETCH → DECODE → EXECUTE cycle and supports the required 8 instructions.

The individual instruction tests and CPU execution tests passed successfully, and the demonstration program produced the expected final register and memory values.