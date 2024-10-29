package lispinterpreter;

public class Truth extends Atom {

    public static final Truth INSTANCE = new Truth();

    Truth() {
    }

    @Override
    public Object accept(Interpreter visitor) {
        return visitor.visitTruth();
    }

    @Override
    public String toString() {
        return "TRUTH";
    }

    @Override
    public boolean toBoolean() {
        return true;
    }
}