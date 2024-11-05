package lispinterpreter;

import java.util.Map;

public class Cons extends SExpr {

    private SExpr car;
    private SExpr cdr;

    Cons(SExpr car, SExpr cdr) {
        this.car = car;
        this.cdr = cdr;
    }

    public SExpr getCar() {
        return this.car;
    }

    public SExpr getCdr() {
        return this.cdr;
    }

    @Override
    public Object accept(Interpreter visitor, Map<String, Object> localEnvironment) {
        return visitor.visitCons(this, localEnvironment);
    }

    @Override
    public String toString() {
        return "(" + car + " . " + cdr + ")";
    }

    @Override
    public boolean toBoolean() {
        return true;
    }
}
