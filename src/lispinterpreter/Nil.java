package lispinterpreter;

import java.util.Map;

public class Nil extends Atom {

    public static final Nil INSTANCE = new Nil();

    Nil() {
    }

    @Override
    public Object accept(Interpreter visitor, Map<String, Object> localEnvironment) {
        return visitor.visitNil();
    }

    @Override
    public String toString() {
        return "NIL";
    }

    @Override
    public boolean toBoolean() {
        return false;
    }
}