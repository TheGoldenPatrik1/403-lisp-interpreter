package lispinterpreter;

import java.util.Map;

public abstract class SExpr {
    public static final SExpr nil = new Nil();
    public static final SExpr truth = new Truth();

    public abstract Object accept(Interpreter visitor, Map<String, Object> localEnvironment);

    public abstract String toString();

    public abstract boolean toBoolean();
}
