# PIC16F72 Instruction Set Analysis

Instruction set analysis breaks down how a microcontroller like the PIC16F72 understands and runs its instructions. If you ever build a simulator for one of these chips, think of the instruction set as the microcontroller’s grammar—it shapes everything the chip does.

## 1. Ground Rules

- Every instruction is exactly **14 bits** long.
- The chip can store up to **2,048** of these instructions.
- The chip understands **35 unique commands**.

## 2. Instruction Bitfields

Each time the chip reads a 14-bit instruction, it splits that number into smaller parts to figure out exactly what it is supposed to do.

- **d bit — Destination:** Says where to put the result—either in the Working Register (W) or back into a memory location.
- **b bit — Bit Selection:** Picks out a single bit inside a byte, from 0 to 7.
- **k field — Number/Address:** Contains a plain number or an address to jump to.

## 3. Four Instruction Families

All 35 instructions fall into four basic families.

### Byte-Oriented Instructions

These work on a whole byte, shuffling values between memory and the W register.

**Example:**  
ADDWF count, 0 adds a memory value to W.

### Bit-Oriented Instructions

These work with individual bits, flipping them or checking their values.

**Example:**  
BSF PORTB, 0 sets a particular bit to 1.

### Literal Instructions

These use hardcoded numbers.

**Example:**  
MOVLW 5 loads the number 5 into W.

### Control Instructions

These handle jumps, calls, and returns—anything that changes how the program runs.

**Examples:**  
GOTO main  
CALL delay

## 4. Instruction Execution and Timing

- Most instructions take **one machine cycle**, which is **four clock ticks**.
- Each one-cycle instruction moves **Timer0 forward by 1**.
- If an instruction jumps or calls a function, it needs **two cycles**.
- Jump and call instructions take two cycles because the chip has to **clear out its pipeline and reload with the new address**.
- Therefore, a jump or call moves **Timer0 forward by 2**.

## 5. Why Instruction Set Analysis Matters for a Simulator

To build a simulator, every instruction must be handled correctly:

1. Decode the **14-bit instruction**.
2. Figure out its meaning.
3. Identify which **instruction family** it belongs to.
4. Determine what each instruction field does.
5. Run the required **math or logic**.
6. Update the **Working Register (W)** or memory.
7. Set flags such as **Zero** or **Carry** when needed.
8. Keep the internal **timing** accurate.

Getting all of this right makes the simulated chip act just like the real thing.
