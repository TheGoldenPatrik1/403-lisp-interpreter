package lispinterpreter;

public class GlobalFunction extends Function {
    public GlobalFunction(String value, SExprList parameters) {
        super(value, parameters);
    }

    @Override
    public Object accept(Interpreter interpreter) {
        return interpreter.visitGlobalFunction(this);
    }
}