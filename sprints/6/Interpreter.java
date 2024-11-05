package lispinterpreter;

import java.util.List;
import java.util.Map;
import java.rmi.RMISecurityException;
import java.util.Arrays;
import java.util.HashMap;

public class Interpreter {

    List<SExpr> statements;
    Map<String, Object> environment = new HashMap<>();

    public Interpreter(List<SExpr> statements) {
        this.statements = statements;
    }

    public void interpret() {
        int lineNumber = 1;
        try {
            for (SExpr statement : statements) {
                statement.accept(this, new HashMap<String, Object>());
                lineNumber++;
            }
        } catch (Exception e) {
            System.out.println("Error on line " + lineNumber + ": " + e.getMessage());
        }
    }

    public Object visitDefine(List<SExpr> parameters) {
        if (parameters.size() < 3) {
            throw new RuntimeException("define function must have at least three parameters");
        }
        String name = parameters.get(0).toString();
        SExprList following = new SExprList();
        following.add(parameters.get(1));
        SExprList body = new SExprList();
        for (int i = 2; i < parameters.size(); i++) {
            body.add(parameters.get(i));
        }
        following.add(body);
        Function func = new Function(name, following);
        environment.put(name, func);
        return null;
    }

    public Object visitFunction(Function function, Map<String, Object> prevEnv) {
        if (!environment.containsKey(function.getValue())) {
            throw new RuntimeException("called undefined function '" + function.getValue() + "'");
        }
        Function definition = (Function) environment.get(function.getValue());
        SExprList param = definition.getParameters();
        String variable = param.getList().get(0).toString();

        String stripped = variable.replaceAll("[()]", "");
        String[] split = stripped.trim().split("\\s+");
        List<String> listVar = Arrays.asList(split);

        List<SExpr> listIn = function.getParameters().getList();

        Map<String, Object> localEnvironment = new HashMap<>();
        if (listVar.size() != listIn.size()) {
            throw new RuntimeException("function '" + function.getValue() + "' expects " + listVar.size()
                    + " parameters, but " + listIn.size() + " were given");
        } 
        for (int i = 0; i < listVar.size(); i++) {
            localEnvironment.put(listVar.get(i), listIn.get(i).accept(this, prevEnv));
        }

        List<SExpr> body = param.getList().subList(1, param.getList().size());
        List<SExpr> list = ((SExprList) body.get(0)).getList();
        Object result = null;
        for (SExpr statement : list) {
            result = statement.accept(this, localEnvironment);
        }
        return result;
    }

    public Object visitSExprList(SExprList list, Map<String, Object> localEnvironment) {
        return list.getList().stream().map(expr -> expr.accept(this, localEnvironment)).toList();
    }

    public Object visitSymbol(Symbol symbol, Map<String, Object> localEnvironment) {
        if (localEnvironment.containsKey(symbol.getValue())) {
            Object value = localEnvironment.get(symbol.getValue());
            if (value instanceof SExpr) {
                return ((SExpr) value).accept(this, localEnvironment);
            }
            return value;
        }
        if (environment.containsKey(symbol.getValue())) {
            Object value = environment.get(symbol.getValue());
            if (value instanceof SExpr) {
                return ((SExpr) value).accept(this, localEnvironment);
            }
            return value;
        }
        return symbol.getValue();
    }

    public Object visitNil() {
        return false;
    }

    public Object visitTruth() {
        return true;
    }

    public Object visitCons(Cons cons, Map<String, Object> localEnvironment) {
        return cons;
    }

    public Object visitAtom(Atom atom, Map<String, Object> localEnvironment) {
        if (atom.getType().equals("symbol")) {
            return visitSymbol((Symbol) atom.getValue(), localEnvironment);
        }
        return atom.getValue();
    }

    public Object visitGlobalFunction(GlobalFunction globalFunction, Map<String, Object> localEnvironment) {
        List<SExpr> parameters = globalFunction.getParameters().getList();
        String functionName = globalFunction.getValue().toLowerCase();
        switch (functionName) {
            case "+":
            case "-":
            case "*":
            case "/":
            case "%":
            case "<":
            case ">":
            case "<=":
            case ">=":
                return visitArithmeticOperation(functionName, parameters, localEnvironment);
            case "nil?":
            case "number?":
            case "list?":
            case "symbol?":
                return visitTypeCheckingFunction(functionName, parameters);
            case "eq?":
            case "=":
                if (parameters.size() != 2) {
                    throw new RuntimeException(functionName + " function must have exactly two parameters");
                }
                return parameters.get(0).accept(this, localEnvironment)
                        .equals(parameters.get(1).accept(this, localEnvironment));
            case "cons":
                return visitConsStatement(parameters, localEnvironment);
            case "car":
                return visitCarStatement(parameters);
            case "cdr":
                return visitCdrStatement(parameters);
            case "print":
                return visitPrintStatement(parameters, localEnvironment);
            case "quote":
            case "'":
                if (parameters.size() != 1) {
                    throw new RuntimeException("quote function must have exactly one parameter");
                }
                return parameters.get(0);
            case "eval":
                if (parameters.size() != 1) {
                    throw new RuntimeException("eval function must have exactly one parameter");
                }
                Object result = parameters.get(0).accept(this, localEnvironment);
                while (result instanceof SExpr) {
                    result = ((SExpr) result).accept(this, localEnvironment);
                }
                return result;
            case "not":
                return visitNotStatement(parameters, localEnvironment);
            case "cond":
                return visitCondStatement(parameters, localEnvironment);
            case "and?":
            case "or?":
                return visitConditionalStatement(functionName, parameters, localEnvironment);
            case "if":
                return visitIfStatement(parameters, localEnvironment);
            case "set":
                return visitSetStatement(parameters, localEnvironment);
            case "define":
                return visitDefine(parameters);
            default:
                throw new RuntimeException("Unknown global function: " + functionName);
        }
    }

    public Object visitArithmeticOperation(String operation, List<SExpr> parameters,
            Map<String, Object> localEnvironment) {
        if (parameters.size() < 2) {
            throw new RuntimeException("Arithmetic operations must have at least two parameters");
        }
        List<Object> results = parameters.stream().map(expr -> expr.accept(this, localEnvironment)).toList();
        if (results.stream().anyMatch(result -> !(result instanceof Number))) {
            throw new RuntimeException("Parameters for arithmetic operations must be numbers");
        }
        List<Double> numbers = results.stream().map(result -> ((Number) result).doubleValue()).toList();
        switch (operation) {
            case "+":
                return numbers.stream().reduce(0.0, Double::sum);
            case "-":
                return numbers.stream().reduce((a, b) -> a - b).get();
            case "*":
                return numbers.stream().reduce(1.0, (a, b) -> a * b);
            case "/":
                return numbers.stream().reduce((a, b) -> a / b).get();
            case "%":
                if (numbers.size() != 2) {
                    throw new RuntimeException("% function must have exactly two parameters");
                }
                return numbers.get(0) % numbers.get(1);
            case "<":
                if (numbers.size() != 2) {
                    throw new RuntimeException("< function must have exactly two parameters");
                }
                return numbers.get(0) < numbers.get(1);
            case ">":
                if (numbers.size() != 2) {
                    throw new RuntimeException("> function must have exactly two parameters");
                }
                return numbers.get(0) > numbers.get(1);
            case "<=":
                if (numbers.size() != 2) {
                    throw new RuntimeException("<= function must have exactly two parameters");
                }
                return numbers.get(0) <= numbers.get(1);
            case ">=":
                if (numbers.size() != 2) {
                    throw new RuntimeException(">= function must have exactly two parameters");
                }
                return numbers.get(0) >= numbers.get(1);
            default:
                throw new RuntimeException("Unknown arithmetic operation: " + operation);
        }
    }

    private boolean visitTypeCheckingFunction(String functionName, List<SExpr> parameters) {
        if (parameters.size() != 1) {
            throw new RuntimeException(functionName + " function must have exactly one parameter");
        }
        SExpr param = parameters.get(0);
        switch (functionName) {
            case "nil?":
                return param instanceof Nil;
            case "number?":
                return param instanceof Atom && ((Atom) param).isNumber();
            case "list?":
                return param instanceof SExprList;
            case "symbol?":
                return param instanceof Symbol;
            default:
                throw new RuntimeException("Unknown type checking function: " + functionName);
        }
    }

    private boolean visitNotStatement(List<SExpr> parameters, Map<String, Object> localEnvironment) {
        if (parameters.size() != 1) {
            throw new RuntimeException("not function must have exactly one parameter");
        }

        SExpr param = parameters.get(0);
        return !paramToBoolean(param, localEnvironment);
    }

    private Object visitPrintStatement(List<SExpr> parameters, Map<String, Object> localEnvironment) {
        if (parameters.size() == 0) {
            throw new RuntimeException("print function must have at least one parameter");
        }
        for (SExpr param : parameters) {
            Object result = param.accept(this, localEnvironment);
            if (result == null) {
                return null;
            } else if (result instanceof Cons) {
                Cons cons = (Cons) result;
                System.out.println(cons.toString());
                return null;
            } else if (result.equals(true)) {
                System.out.println("TRUTH");
            } else if (result.equals(false)) {
                System.out.println("NIL");
            } else {
                System.out.println(result);
            }
        }
        return null;
    }

    private Object visitSetStatement(List<SExpr> parameters, Map<String, Object> localEnvironment) {
        if (parameters.size() != 2) {
            throw new RuntimeException("set function must have exactly two parameters");
        }
        if (!(parameters.get(0) instanceof Symbol)) {
            throw new RuntimeException("First parameter of set function must be a symbol");
        }
        String variableName = ((Symbol) parameters.get(0)).getValue();
        Object value = parameters.get(1).accept(this, localEnvironment);
        environment.put(variableName, value);
        return null;
    }

    private boolean paramToBoolean(SExpr param, Map<String, Object> localEnvironment) {
        boolean first = false;
        if (param instanceof Atom) {
            first = ((Atom) param).toBoolean();
        } else if (param instanceof Function) {
            Object firstValue = param.accept(this, localEnvironment);
            first = firstValue != null && firstValue.equals(true);
        } else if (param instanceof Symbol) {
            first = ((Symbol) param).toBoolean();
        } else if (param instanceof SExprList) {
            first = ((SExprList) param).toBoolean();
        }
        return first;
    }

    private Object visitConditionalStatement(String conditional, List<SExpr> parameters,
            Map<String, Object> localEnvironment) {
        if (parameters.size() != 2) {
            throw new RuntimeException("conditional function must have exactly two parameters");
        }

        SExpr param = parameters.get(0);
        SExpr paramSecond = parameters.get(1);

        boolean first = paramToBoolean(param, localEnvironment);

        if (conditional.equals("and?") && !first) {
            return false;
        } else if (conditional.equals("or?") && first) {
            return true;
        }
        
        boolean second = paramToBoolean(paramSecond, localEnvironment);

        if (conditional.equals("and?")) {
            return first && second;
        } else {
            return first || second;
        }
    }

    private Object visitIfStatement(List<SExpr> parameters, Map<String, Object> localEnvironment) {
        if (parameters.size() != 3) {
            throw new RuntimeException("if function must have exactly three parameters");
        }

        SExpr param = parameters.get(0);
        SExpr paramSecond = parameters.get(1);
        SExpr paramThird = parameters.get(2);

        boolean first = paramToBoolean(param, localEnvironment);

        if (first) {
            return paramSecond.accept(this, localEnvironment);
        }
        return paramThird.accept(this, localEnvironment);
    }

    private Object visitCondStatement(List<SExpr> parameters, Map<String, Object> localEnvironment) {
        if (parameters.size() % 2 != 0) {
            throw new RuntimeException("cond statement must have an even number of parameters");
        }

        for (int i = 0; i < parameters.size(); i += 2) {
            SExpr condition = parameters.get(i);
            SExpr result = parameters.get(i + 1);

            if (condition instanceof Symbol && condition.toString().equals("else")) {
                return result.accept(this, localEnvironment);
            }

            if (paramToBoolean(condition, localEnvironment)) {
                return result.accept(this, localEnvironment);
            }
        }

        return Nil.INSTANCE;
    }

    private Object visitConsStatement(List<SExpr> parameters, Map<String, Object> localEnvironment) {
        if (parameters.size() != 2) {
            throw new RuntimeException("cons function must have exactly two parameters");
        }
        SExpr first = parameters.get(0);
        SExpr second = parameters.get(1);
        if (second instanceof SExprList) {
            SExprList list = (SExprList) second;
            list.prepend(first);
            return list.accept(this, localEnvironment);
        }
        return new Cons(first, second);
    }

    private Object visitCarStatement(List<SExpr> parameters) {
        SExpr param = parameters.get(0);
        Object paramValue = param.accept(this, new HashMap<>());
        if (paramValue instanceof Cons) {
            return ((Cons) paramValue).getCar();
        }
        SExprList list = (SExprList) param;
        List<SExpr> separate = list.getList();
        if (separate.isEmpty())
            return Nil.INSTANCE;
        return separate.get(0);
    }

    private Object visitCdrStatement(List<SExpr> parameters) {
        SExpr param = parameters.get(0);
        Object paramValue = param.accept(this, new HashMap<>());
        if (paramValue instanceof Cons) {
            return ((Cons) paramValue).getCdr();
        }
        SExprList list = (SExprList) parameters.get(0);
        List<SExpr> separate = list.getList();
        SExprList newList = new SExprList();
        for (int i = 1; i < separate.size(); i++) {
            newList.add(separate.get(i));
        }
        if (newList.getList().isEmpty()) {
            return Nil.INSTANCE;
        }
        return newList;
    }
}