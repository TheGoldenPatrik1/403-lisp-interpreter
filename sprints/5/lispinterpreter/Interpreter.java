package lispinterpreter;

import java.util.List;
import java.util.Map;
import java.rmi.RMISecurityException;
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
                statement.accept(this);
                lineNumber++;
            }
        } catch (Exception e) {
            System.out.println("Error on line " + lineNumber + ": " + e.getMessage());
        }
    }

    public Object visitFunction(Function function) {
        // TODO: implement user-defined functions
        return null;
    }

    public Object visitSExprList(SExprList list) {
        return list.getList().stream().map(expr -> expr.accept(this)).toList();
    }

    public Object visitSymbol(Symbol symbol) {
        if (environment.containsKey(symbol.getValue())) {
            Object value = environment.get(symbol.getValue());
            if (value instanceof SExpr) {
                return ((SExpr) value).accept(this);
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

    public Object visitCons(Cons cons) {
        return cons;
    }

    public Object visitAtom(Atom atom) {
        if (atom.getType().equals("symbol")) {
            return visitSymbol((Symbol) atom.getValue());
        }
        return atom.getValue();
    }

    public Object visitGlobalFunction(GlobalFunction globalFunction) {
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
                return visitArithmeticOperation(functionName, parameters);
            case "nil?":
            case "number?":
            case "list?":
            case "symbol?":
                return visitTypeCheckingFunction(functionName, parameters);
            case "=":
                if (parameters.size() != 2) {
                    throw new RuntimeException("= function must have exactly two parameters");
                }
                return parameters.get(0).accept(this).equals(parameters.get(1).accept(this));
            case "cons":
                if (parameters.size() != 2) {
                    throw new RuntimeException("cons function must have exactly two parameters");
                }
                // should we accept the parameters here?
                return new Cons(parameters.get(0), parameters.get(1)).accept(this);
            case "car":
                // TODO: implement car
                return null;
            case "cdr":
                // TODO: implement cdr
                return null;
            case "print":
                return visitPrintStatement(parameters);
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
                Object result = parameters.get(0).accept(this);
                while (result instanceof SExpr) {
                    result = ((SExpr) result).accept(this);
                }
                return result;
            case "not":
                return visitNotStatement(parameters);
            case "cond":
                return visitCondPairsStatement(parameters);
            case "and?":
            case "or?":
                return visitConditionalStatement(functionName, parameters);
            case "if":
                return visitIfStatement(parameters);
            case "eq":
                // TODO: implement eq
                return null;
            case "set":
                return visitSetStatement(parameters);
            case "define":
                // TODO: implement define
                return null;
            default:
                throw new RuntimeException("Unknown global function: " + functionName);
        }
    }

    public Object visitArithmeticOperation(String operation, List<SExpr> parameters) {
        if (parameters.size() < 2) {
            throw new RuntimeException("Arithmetic operations must have at least two parameters");
        }
        List<Object> results = parameters.stream().map(expr -> expr.accept(this)).toList();
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

    private boolean visitNotStatement(List<SExpr> parameters) {
        if (parameters.size() != 1) {
            throw new RuntimeException("not function must have exactly one parameter");
        }
        SExpr param = parameters.get(0);
        if (param instanceof Atom) {
            return !((Atom) param).toBoolean();
        }
        if (param instanceof Function) {
            return !param.accept(this).equals(true);
        }
        if (param instanceof Symbol) {
            return !((Symbol) param).toBoolean();
        }
        if (param instanceof SExprList) {
            return !((SExprList) param).toBoolean();
        }
        return !param.accept(this).equals(true);
    }

    private Object visitPrintStatement(List<SExpr> parameters) {
        if (parameters.size() == 0) {
            throw new RuntimeException("print function must have at least one parameter");
        }
        for (SExpr param : parameters) {
            Object result = param.accept(this);
            if (result == null) {
                return null;
            } else if (result instanceof Cons) {
                SExprList list = new SExprList();
                Cons cons = (Cons) result;
                list.add(cons.getCar());
                list.add(cons.getCdr());
                System.out.println(list.accept(this));
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

    private Object visitSetStatement(List<SExpr> parameters) {
        if (parameters.size() != 2) {
            throw new RuntimeException("set function must have exactly two parameters");
        }
        if (!(parameters.get(0) instanceof Symbol)) {
            throw new RuntimeException("First parameter of set function must be a symbol");
        }
        String variableName = ((Symbol) parameters.get(0)).getValue();
        Object value = parameters.get(1).accept(this);
        environment.put(variableName, value);
        return null;
    }

    private Object visitConditionalStatement(String conditional, List<SExpr> parameters) {
        if (parameters.size() != 2) {
            throw new RuntimeException("conditional function must have exactly two parameters");
        }
        boolean first, second;
        SExpr param = parameters.get(0);
        SExpr paramSecond = parameters.get(1);

        if ((param instanceof Atom && !(paramSecond instanceof Atom)) ||
                (param instanceof Function && !(paramSecond instanceof Function)) ||
                (param instanceof Symbol && !(paramSecond instanceof Symbol)) ||
                (param instanceof SExprList && !(paramSecond instanceof SExprList))) {
            throw new RuntimeException("conditional parameters do not match type");
        }

        if (param instanceof Atom) {
            first = ((Atom) param).toBoolean();
            if (conditional.equals("and?")) {
                if (first) {
                    second = ((Atom) paramSecond).toBoolean();
                    if (second)
                        return true;
                }
                return false;
            } else {
                if (first) {
                    return true;
                } else {
                    second = ((Atom) paramSecond).toBoolean();
                    return second;
                }
            }
        }
        if (param instanceof Function) {
            first = param.accept(this).equals(true);
            if (conditional.equals("and?")) {
                if (first) {
                    second = paramSecond.accept(this).equals(true);
                    if (second)
                        return true;
                }
                return false;
            } else {
                if (first) {
                    return true;
                } else {
                    second = paramSecond.accept(this).equals(true);
                    return second;
                }
            }
        }
        if (param instanceof Symbol) {
            first = ((Symbol) param).toBoolean();
            if (conditional.equals("and?")) {
                if (first) {
                    second = ((Symbol) paramSecond).toBoolean();
                    if (second)
                        return true;
                }
                return false;
            } else {
                if (first) {
                    return true;
                } else {
                    second = ((Symbol) paramSecond).toBoolean();
                    return second;
                }
            }
        }
        if (param instanceof SExprList) {
            first = ((SExprList) param).toBoolean();
            if (conditional.equals("and?")) {
                if (first) {
                    second = ((SExprList) paramSecond).toBoolean();
                    if (second)
                        return true;
                }
                return false;
            } else {
                if (first) {
                    return true;
                } else {
                    second = ((SExprList) paramSecond).toBoolean();
                    return second;
                }
            }
        }
        first = param.accept(this).equals(true);
        if (conditional.equals("and?")) {
            if (first) {
                second = paramSecond.accept(this).equals(true);
                if (second)
                    return true;
            }
            return false;
        } else {
            if (first) {
                return true;
            } else {
                second = paramSecond.accept(this).equals(true);
                return second;
            }
        }
    }

    private Object visitIfStatement(List<SExpr> parameters) {
        if (parameters.size() != 3) {
            throw new RuntimeException("if function must have exactly three parameters");
        }
        boolean first;
        SExpr param = parameters.get(0);
        SExpr paramSecond = parameters.get(1);
        SExpr paramThird = parameters.get(2);

        if (param instanceof Atom) {
            first = ((Atom) param).toBoolean();
        } else if (param instanceof Function) {
            first = param.accept(this).equals(true);
        } else if (param instanceof Symbol) {
            first = ((Symbol) param).toBoolean();
        } else if (param instanceof SExprList) {
            first = ((SExprList) param).toBoolean();
        } else {
            first = param.accept(this).equals(true);
        }

        if (first) {
            return paramSecond.accept(this);
        }
        return paramThird.accept(this);
    }

    private Object visitCondPairsStatement(List<SExpr> parameters) {
        if (parameters.size() != 2) {
            throw new RuntimeException("Cond statement must have exactly two parameters");
        }
        boolean first;
        SExpr param = parameters.get(0);
        SExpr paramSecond = parameters.get(1);

        if (param instanceof Atom) {
            first = ((Atom) param).toBoolean();
        } else if (param instanceof Function) {
            first = param.accept(this).equals(true);
        } else if (param instanceof Symbol) {
            first = ((Symbol) param).toBoolean();
        } else if (param instanceof SExprList) {
            first = ((SExprList) param).toBoolean();
        } else {
            first = param.accept(this).equals(true);
        }
        if (first) {
            return paramSecond.accept(this);
        }
        return Nil.INSTANCE;
    }
}