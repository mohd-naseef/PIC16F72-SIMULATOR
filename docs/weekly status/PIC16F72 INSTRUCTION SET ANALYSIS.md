# PIC16F72 Quick-Reference Cheat Sheet

## 1. What are we building?
We’re making a software simulator for the PIC16F72 microcontroller. The chip understands 35 instructions—each one is 14 bits wide, and they’re stored in a 2K × 14-bit memory.

## 2. Important bitfields, fast:
- **d (Destination):** 0 means save the result in the Working Register (W), 1 means save it to a specific memory spot (f).
- **b (Bit Select):** Picks which bit to use, from 0 to 7.
- **k (Literal/Address):** Holds a hardcoded value or tells where to jump.

## 3. Four types of instructions:
- **Byte:** Moves data or does math between W and memory. Examples: `ADDWF count, 0` (Add to W), `MOVWF count` (Save W), `CLRF count` (Clear to zero).
- **Bit:** Reads or writes a single pin or flag. Try `BSF PORTB, 0` (Set pin high), `BCF PORTB, 0` (Set pin low), `BTFSC PORTA, 0` (Skip next if pin is low).
- **Literal:** Works straight on numbers. For example, `MOVLW 5` (Set W to 5), `ADDLW 10` (Add 10 to W).
- **Control:** Handles jumps, subroutine calls, and returns. Like `GOTO main`, `CALL delay`, `RETURN`.

## 4. How fast does it run? What about Timer0?
- **Standard instructions (`ADDWF`, `BSF`, etc.):** Finish in one cycle (that is 4 clock ticks), Timer0 adds 1.
- **Jump, call, or skip operations that succeed:** These use two cycles, and Timer0 bumps up by 2. This happens because the instruction pipeline reloads.

## 5. How the simulator is built (the four core modules):
- **Execution Core:** Handles math and logic, updates W, and deals with RAM.
- **Control Logic:** Watches over the Program Counter (PC) and manages the 8-level call stack.
- **Status Flags:** Sets the Zero (Z) and Carry (C) flags after math.
- **Timing Tracker:** Keeps count of 1- and 2-cycle operations to keep Timer0 accurate.
