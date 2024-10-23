package lispinterpreter;

public class Function extends Symbol {
    private SExprList parameters;

    public Function(String value, SExprList parameters) {
        super(value);
        this.parameters = parameters;
    }

    public SExprList getParameters() {
        return parameters;
    }

    public Object accept(Interpreter interpreter) {
        return interpreter.visitFunction(this);
    }

    @Override
    public String toString() {
        return super.getValue() + parameters;
    }
}