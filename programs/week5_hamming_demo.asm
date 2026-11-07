; WEEK 5 - HAMMING (12,8) SINGLE-BIT CORRECTION
; Data byte = 134 (0x86), stored at RAM address 0x20.
; 12-bit Hamming code stored in RAM[0x40] (low byte)
; and RAM[0x41] (upper four code bits).
; RAM[0x30]-RAM[0x37] are reserved for the Week 3 FIFO mirror.
;
; HENC and HDEC are simulator teaching instructions.
; FLIP is a demonstration-only single-bit fault injector.

        MOVLW 0x86
        MOVWF 0x20
        HENC 0x20,0x40
        FLIP 0x40,2
        HDEC 0x20,0x40
        SLEEP
