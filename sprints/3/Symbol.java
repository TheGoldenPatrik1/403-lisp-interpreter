package lispinterpreter;

public class Symbol extends SExpr {
    private String value;

    public Symbol(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public Object accept(Interpreter visitor) {
        return visitor.visitSymbol(this);
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean toBoolean() {
        return true;
    }
}