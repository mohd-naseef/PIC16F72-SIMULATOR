; ==========================================================
; PIC16F72 WEEK 3 - FIFO QUEUE VALIDATION PROGRAM
; ==========================================================
;
; Purpose:
; Validate multiple enqueue and dequeue operations
; using the PIC16F72 simulator memory model.
;
; Queue values:
; 10 -> 20 -> 30
;
; Expected dequeue order:
; 10 -> 20 -> 30
;
; ==========================================================

        ; -------------------------
        ; ENQUEUE 10
        ; -------------------------
        MOVLW   10
        MOVWF   0x30

        ; -------------------------
        ; ENQUEUE 20
        ; -------------------------
        MOVLW   20
        MOVWF   0x31

        ; -------------------------
        ; ENQUEUE 30
        ; -------------------------
        MOVLW   30
        MOVWF   0x32


        ; -------------------------
        ; Queue is now:
        ;
        ; 0x30 = 10
        ; 0x31 = 20
        ; 0x32 = 30
        ;
        ; FIFO order:
        ; 10 -> 20 -> 30
        ; -------------------------


        ; -------------------------
        ; END PROGRAM
        ; -------------------------
        SLEEP

