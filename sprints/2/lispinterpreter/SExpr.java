package lispinterpreter;

public abstract class SExpr {
    public static final SExpr nil = new Nil();
    public static final SExpr truth = new Truth();

    public abstract String toString();
}
