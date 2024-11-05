package lispinterpreter;

import java.util.Map;

public class Atom extends SExpr {
    private Object value;

    public Atom() {
    }

    public Atom(String value) {
        this.value = value;
    }

    public Atom(Number value) {
        this.value = value;
    }

    public Atom(Symbol value) {
        this.value = value;
    }

    public Object getValue() {
        if (value instanceof Integer) {
            return ((Integer) value).doubleValue();
        }
        return value;
    }

    public String getType() {
        if (value instanceof Number) {
            return "number";
        } else if (value instanceof String) {
            return "string";
        } else if (value instanceof Symbol) {
            return "symbol";
        } else {
            return "unknown";
        }
    }

    public boolean isNumber() {
        return value instanceof Number;
    }

    public boolean isString() {
        return value instanceof String;
    }

    public boolean isSymbol() {
        return value instanceof Symbol;
    }

    @Override
    public Object accept(Interpreter visitor, Map<String, Object> localEnvironment) {
        return visitor.visitAtom(this, localEnvironment);
    }

    @Override
    public String toString() {
        if (isString()) {
            return "'" + value + "'";
        }
        return value.toString();
    }

    @Override
    public boolean toBoolean() {
        if (value instanceof Number) {
            return ((Number) value).doubleValue() != 0;
        }
        return true;
    }
}
