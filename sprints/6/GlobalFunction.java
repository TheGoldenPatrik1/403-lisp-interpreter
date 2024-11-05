package lispinterpreter;

import java.util.Map;

public class GlobalFunction extends Function {
    public GlobalFunction(String value, SExprList parameters) {
        super(value, parameters);
    }

    @Override
    public Object accept(Interpreter interpreter, Map<String, Object> localEnvironment) {
        return interpreter.visitGlobalFunction(this, localEnvironment);
    }
}