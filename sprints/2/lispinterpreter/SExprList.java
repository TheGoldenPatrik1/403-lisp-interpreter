package lispinterpreter;

import java.util.ArrayList;
import java.util.List;

public class SExprList extends SExpr {
    private List<SExpr> expressions;

    public SExprList() {
        this.expressions = new ArrayList<>();
    }

    public void add(SExpr expr) {
        expressions.add(expr);
    }

    public List<SExpr> getList() {
        return expressions;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        for (int i = 0; i < expressions.size(); i++) {
            sb.append(expressions.get(i).toString());
            if (i < expressions.size() - 1) {
                sb.append(" ");
            }
        }
        sb.append(")");
        return sb.toString();
    }
}
