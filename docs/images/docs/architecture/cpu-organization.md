# CPU Organization and Program Counter

## Responsibility

My primary responsibility for the project is CPU Organization and
Program Counter implementation.

## CPU Organization

The CPU contains the following main components:

- W register
- STATUS register
- Program Counter (PC)
- Program Memory
- Data Memory
- Current Instruction
- CPU execution status

The CPU processes instructions using the logical:

FETCH → DECODE → EXECUTE

sequence.

## Program Counter

The Program Counter stores the address of the next instruction
to be fetched from program memory.

The Program Counter starts from address 0 during reset.

After an instruction is fetched, the Program Counter is incremented
to point to the next instruction.

For control-flow instructions such as GOTO, the Program Counter is
updated with the target address.

## ProgramCounter Class

The ProgramCounter class provides the following operations:

- getPC() - returns the current PC value
- setPC() - sets the PC value
- increment() - moves the PC to the next instruction
- jump() - changes the PC to a target address
- reset() - resets the PC to 0

## FETCH Operation

During FETCH:

1. The CPU reads the current PC.
2. The instruction at that address is obtained from program memory.
3. The PC is incremented.
4. The fetched instruction becomes the current instruction.

## Control Flow

The GOTO instruction changes the PC to the specified target
address.

## Reset

When the CPU is reset:

- W is reset to 0
- STATUS is reset to 0
- PC is reset to 0
- Current instruction is cleared
- CPU execution status is reset
