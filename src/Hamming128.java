/** Hamming (12,8) SEC codec. Corrects one flipped bit in a 12-bit codeword. */
public final class Hamming128 {
    private Hamming128() {}

    // Positions 1,2,4,8 are parity; remaining positions carry D0..D7.
    private static final int[] DATA_POS = {3,5,6,7,9,10,11,12};

    public static int encode(int value) {
        int code = 0;
        value &= 0xFF;
        for (int i = 0; i < 8; i++) {
            if (((value >> i) & 1) != 0) code |= 1 << (DATA_POS[i] - 1);
        }
        for (int parityPos : new int[]{1,2,4,8}) {
            int parity = 0;
            for (int pos = 1; pos <= 12; pos++) {
                if ((pos & parityPos) != 0 && pos != parityPos)
                    parity ^= (code >> (pos - 1)) & 1;
            }
            if (parity != 0) code |= 1 << (parityPos - 1);
        }
        return code & 0xFFF;
    }

    public static int decode(int codeword) {
        int syndrome = syndrome(codeword);
        if (syndrome >= 1 && syndrome <= 12) codeword ^= 1 << (syndrome - 1);
        int data = 0;
        for (int i = 0; i < 8; i++)
            data |= ((codeword >> (DATA_POS[i] - 1)) & 1) << i;
        return data & 0xFF;
    }

    public static int syndrome(int codeword) {
        int syndrome = 0;
        for (int parityPos : new int[]{1,2,4,8}) {
            int parity = 0;
            for (int pos = 1; pos <= 12; pos++)
                if ((pos & parityPos) != 0) parity ^= (codeword >> (pos - 1)) & 1;
            if (parity != 0) syndrome |= parityPos;
        }
        return syndrome;
    }

    public static int flip(int codeword, int bitIndex) {
        if (bitIndex < 0 || bitIndex > 11) throw new IllegalArgumentException("Bit index must be 0..11");
        return codeword ^ (1 << bitIndex);
    }
}
