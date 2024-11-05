package lispinterpreter;

import java.util.Map;

public class Symbol extends SExpr {
    private String value;

    public Symbol(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public Object accept(Interpreter visitor, Map<String, Object> localEnvironment) {
        return visitor.visitSymbol(this, localEnvironment);
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