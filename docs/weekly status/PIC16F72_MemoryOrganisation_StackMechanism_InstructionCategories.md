# My understanding of PIC16F72 – Memory Organization, Stack and Instruction Categories

### 1. Memory Organization

PIC16F72 mainly has Program Memory and Data Memory.

Program Memory: It is 2K × 14 bits and stores the instructions that the PIC has to execute.

Data Memory: It is 128 × 8 bits and is used to store data and different registers.

Data Memory contains SFRs (Special Function Registers) and GPRs (General Purpose Registers).

The Data Memory is divided into banks, and the RP1 and RP0 bits are used to select the required bank.

So basically, the program instructions and the data used by the program are stored separately.

**Example:**  
If the PIC has to add two numbers, the instruction for addition will be in Program Memory and the numbers being used will be handled through Data Memory.

### 2. Stack Mechanism

PIC16F72 has a hardware stack with 8 levels. It is mainly used to remember the address where the program has to return.

When a CALL instruction is used, the return address is stored in the stack.

It also stores the return address when an interrupt occurs.

When RETURN is executed, the saved address is taken from the stack and the program continues from there.

Each level of the stack can store a 13-bit program address.

The stack is managed by the PIC hardware, so we cannot access it like a normal register.

**Example:**  
If the main program calls another function, the PIC saves the return address in the stack. After the function is finished, it uses that address to come back to the main program.

### 3. Instruction Categories

PIC16F72 has 35 instructions. These instructions can be grouped based on what they do.

Byte-oriented instructions: These work with the complete 8-bit data. Examples are MOVWF, ADDWF and SUBWF.

Bit-oriented instructions: These work with one bit of a register. They can set, clear or check a particular bit. Examples are BSF, BCF, BTFSC and BTFSS.

Literal and control instructions: Literal instructions work with a fixed value, while control instructions are used to change the flow of the program. Examples are MOVLW, GOTO, CALL and RETURN.

**Example:**  
ADDWF can be used when we need to add values, BSF can be used to make a particular bit 1, and GOTO can be used to move the program to another location.

**In simple words:**  
Memory is where the PIC keeps its program and data, the stack helps it remember where to return, and instructions are the commands used to make the PIC perform different operations.