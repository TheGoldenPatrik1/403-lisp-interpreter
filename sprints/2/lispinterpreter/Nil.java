package lispinterpreter;

public class Nil extends SExpr {

    public static final Nil INSTANCE = new Nil();

    Nil() {
    }

    @Override
    public String toString() {
        return "NIL";
    }

}