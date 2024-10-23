package lispinterpreter;

import java.util.List;

public class Interpreter {

    List<SExpr> statements;

    public Interpreter(List<SExpr> statements) {
        this.statements = statements;
    }

    public void interpret() {
        for (SExpr statement : statements) {
            System.out.println(statement.getClass());
        }
    }

}