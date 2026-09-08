public class ProgramCounter {

    private int pc = 0;

    public int getPC() {
        return pc;
    }

    public void setPC(int value) {
        pc = value;
    }

    public void increment() {
        pc++;
    }

    public void jump(int address) {
        pc = address;
    }

    public void reset() {
        pc = 0;
    }
}
