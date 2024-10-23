package lispinterpreter;

public class Truth extends SExpr {

    public static final Truth INSTANCE = new Truth();

    Truth() {
    }

    @Override
    public String toString() {
        return "True";
    }

}