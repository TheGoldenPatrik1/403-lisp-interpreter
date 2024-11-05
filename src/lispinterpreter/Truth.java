package lispinterpreter;

import java.util.Map;

public class Truth extends Atom {

    public static final Truth INSTANCE = new Truth();

    Truth() {
    }

    @Override
    public Object accept(Interpreter visitor, Map<String, Object> localEnvironment) {
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