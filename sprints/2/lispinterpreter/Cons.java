package lispinterpreter;

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
    public String toString() {
        return car + " " + cdr;
    }

}
