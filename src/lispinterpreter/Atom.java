package lispinterpreter;

public class Atom extends SExpr {
    private Object value;

    public Atom(String value) {
        this.value = value;
    }

    public Atom(Number value) {
        this.value = value;
    }

    public Atom(Symbol value) {
        this.value = value;
    }

    public Atom(Nil value) {
        this.value = value;
    }

    public Atom(Truth value) {
        this.value = value;
    }

    public Object getValue() {
        return value;
    }

    public String getType() {
        if (value instanceof Number) {
            return "number";
        } else if (value instanceof String) {
            return "string";
        } else if (value instanceof Symbol) {
            return "symbol";
        } else if (value instanceof Nil) {
            return "nil";
        } else if (value instanceof Truth) {
            return "truth";
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

    public boolean isNil() {
        return value instanceof Nil;
    }

    public boolean isTruth() {
        return value instanceof Truth;
    }

    @Override
    public String toString() {
        if (isString()) {
            return "'" + value + "'";
        }
        return value.toString();
    }
}
