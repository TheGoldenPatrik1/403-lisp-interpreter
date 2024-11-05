package lispinterpreter;

import java.util.Map;

public class Function extends Symbol {
    private SExprList parameters;

    public Function(String value, SExprList parameters) {
        super(value);
        this.parameters = parameters;
    }

    public SExprList getParameters() {
        return parameters;
    }

    public Object accept(Interpreter interpreter, Map<String, Object> localEnvironment) {
        return interpreter.visitFunction(this, localEnvironment);
    }

    @Override
    public String toString() {
        return "(" + super.getValue() + " " + parameters.toString().substring(1);
    }
}