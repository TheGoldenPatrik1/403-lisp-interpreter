package lispinterpreter;

public class Truth extends SExpr {

    public static final Truth INSTANCE = new Truth();

    private Truth() {
    }

    @Override
    public String toString() {
        return "True";
    }

}