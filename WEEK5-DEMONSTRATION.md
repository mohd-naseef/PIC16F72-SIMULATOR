# Week 5 Demonstration – Hamming (12,8)

## Purpose

This demonstration adds single-bit error correction to the existing PIC16F72
educational simulator while retaining the earlier CPU, scheduler, FIFO, Core,
Logger and Swing GUI work.

## How to run it

1. Start the simulator using the project's existing launch procedure.
2. Click **LOAD**. The Program Memory list includes the two earlier scheduler
   tasks and a third task named **Week 5 Hamming**.
3. Double-click any Program Memory row to edit its opcode and operand(s), if needed.
4. Click **STEP** repeatedly, or click **RUN**, to execute all tasks.
5. Watch the execution console and the **ERRORS / LOGGER** panel for the
   Hamming encode, injected error, syndrome and recovered byte messages.

## Hamming instruction sequence

```asm
        MOVLW 0x86
        MOVWF 0x20
        HENC 0x20,0x40
        FLIP 0x40,2
        HDEC 0x20,0x40
        SLEEP
```

- `MOVLW 0x86` loads the demonstration byte into W.
- `MOVWF 0x20` stores it in RAM address `0x20`.
- `HENC 0x20,0x40` creates a 12-bit Hamming codeword. The low 8 bits are
  stored at `RAM[0x40]`; the upper 4 bits are stored at `RAM[0x41]`.
- `FLIP 0x40,2` flips codeword bit 2 to simulate a transmission/storage fault.
- `HDEC 0x20,0x40` calculates the syndrome, corrects the single flipped bit,
  and writes the recovered byte back to `RAM[0x20]`.

## Expected result

- Original data: `0x86` (decimal 134).
- Encoded 12-bit codeword: `0x8BB`.
- Injected error: codeword bit index 2.
- Syndrome: 3 (Hamming positions are one-based).
- Corrected codeword: `0x8BB`.
- Recovered data at `RAM[0x20]`: `0x86`.

## RAM address note

The Week 3 FIFO mirrors its eight queue slots into `RAM[0x30]` through
`RAM[0x37]`. The Hamming example therefore uses `RAM[0x40]` and `RAM[0x41]`
for its 12-bit codeword so that it does not overwrite the FIFO mirror.

## Scope and limitation

This is Hamming (12,8) single-error correction (SEC). It corrects one flipped
bit. It is not an extended SECDED code and does not guarantee detection of
double-bit errors.
