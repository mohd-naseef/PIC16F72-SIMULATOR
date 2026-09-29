# Week 2 – CPU and Instruction Execution

## 1. Contribution

My Week 2 contribution is CPU and Instruction Execution.

The CPU was checked for instruction fetching, decoding and execution.

## 2. Instructions Implemented and Tested

| Instruction | Category | Operation | Result |
|---|---|---|---|
| MOVLW | Literal | Load literal value into W | PASS |
| MOVWF | Byte-oriented | Store W into file/register | PASS |
| ADDLW | Literal | Add literal to W | PASS |
| SUBLW | Literal | Subtract W from literal | PASS |
| ANDLW | Literal | AND literal with W | PASS |
| INCF | Byte-oriented | Increment file/register value | PASS |
| GOTO | Control | Change program counter | PASS |
| SLEEP | Control | Stop CPU execution | PASS |

## 3. CPU Execution Flow

The CPU follows this execution cycle:

FETCH → DECODE → EXECUTE

1. FETCH – Gets the instruction using the Program Counter (PC).
2. DECODE – Identifies the instruction and operand.
3. EXECUTE – Performs the required operation.
4. PC and CPU registers are updated.

## 4. Verification

The following were checked:

- All 8 instructions.
- PC updates.
- W register changes.
- STATUS changes.
- Memory changes.
- CPU integration with Memory.
- CPU integration with Scheduler/Task.

## 5. Testing

Individual instruction tests were executed.

Expected result:

**8/8 instruction tests PASS**

The complete demonstration program was also executed and checked for correct execution and termination.

## 6. Final Result

CPU and Instruction Execution testing for Week 2 was completed.

- 8 instructions tested.
- FETCH → DECODE → EXECUTE verified.
- CPU and Memory integration verified.
- CPU and Scheduler/Task integration verified.
- Demonstration program verified.
