# PIC16F72 – Point 3 & 4

## 3. Stack / Stack Pointer

PIC16F72 has a hardware stack of 8 levels inbuilt.

It holds the return address during CALL or interrupt.

RETURN gets the address from the stack and the program proceeds from there.

Stack pointer basically keeps track of the stack location, but in PIC16F72, the stack pointer is not a regular register. It is a hardware function.

Each stack level contains a 13-bit program address.

## 4. Status / Flags

PIC16F72 has an 8-bit STATUS register which holds information about the result of operations.

Main status flags are:

- Z (Zero): 1 when the result is zero.
- C (Carry): 1 if carry is generated in an 8-bit operation.
- DC (Digit Carry): 1 if carry is generated from the lower 4 bits.
- RP1 & RP0: Used for memory bank selection.

Simple examples:

In case of 5 - 5 = 0, Z = 1.

255 + 1 generates an 8-bit result of 0, hence C = 1.

So, the Stack is used to keep track of the return address of the processor, and the STATUS register tells the processor about the result of an operation.
