package lispinterpreter;

import java.util.LinkedList;
import java.util.Queue;
import java.util.List;
import java.util.ArrayList;

public class Parser {
    private String input;

    public Parser(String input) {
        this.input = input;
    }

    // Method to parse the LISP expression
    public List<SExpr> parse() throws Exception {
        Queue<String> tokens = tokenize(this.input);
        List<SExpr> statements = new ArrayList<>();
        while (!tokens.isEmpty()) {
            statements.add(parseTokens(tokens));
        }
        return statements;
    }

    private Queue<String> tokenize(String input) {
        Queue<String> tokens = new LinkedList<>();
        StringBuilder token = new StringBuilder();
        boolean inString = false;

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);

            // Handle string literals
            if (c == '"') {
                if (inString) {
                    // End of string
                    token.append(c);
                    tokens.add(token.toString());
                    token.setLength(0);
                    inString = false;
                } else {
                    // Start of string
                    if (token.length() > 0) {
                        tokens.add(token.toString());
                        token.setLength(0);
                    }
                    token.append(c);
                    inString = true;
                }
                continue;
            }

            // If we are in a string, just keep appending characters
            if (inString) {
                token.append(c);
                continue;
            }

            // Handle parentheses
            if (c == '(' || c == ')' || c == ',') {
                if (token.length() > 0) {
                    tokens.add(token.toString());
                    token.setLength(0);
                }
                tokens.add(String.valueOf(c)); // Add '(' or ')' or ','
            } else if (Character.isWhitespace(c)) {
                if (token.length() > 0) {
                    tokens.add(token.toString());
                    token.setLength(0);
                }
            } else {
                token.append(c); // Build atom or function name
            }
        }

        // Add any remaining token at the end
        if (token.length() > 0) {
            tokens.add(token.toString());
        }

        return tokens;
    }

    // Recursive method to parse tokens into an SExpr
    private SExpr parseTokens(Queue<String> tokens) throws Exception {
        if (tokens.isEmpty()) {
            throw new Exception("Unexpected end of input");
        }

        String token = tokens.poll();

        if (token.equals("(")) {
            // Make sure it's not an empty or invalid list
            if (tokens.isEmpty()) {
                throw new Exception("Unexpected end of input");
            }
            if (tokens.peek().equals(")")) {
                tokens.poll(); // Remove the closing ')'
                return new SExprList();
            }

            // Check if it's a function or a list
            SExpr first = parseTokens(tokens);
            if (first instanceof Symbol) {
                return parseFunction(first, tokens);
            } else {
                return parseList(first, tokens);
            }
        } else if (token.equals(")")) {
            throw new Exception("Unexpected closing parenthesis");
        } else {
            // Create an atom (number, symbol, or string)
            return parseAtom(token);
        }
    }

    // Helper method to create an Atom or handle function calls
    private SExpr parseAtom(String token) throws Exception {
        // If the token is enclosed in quotes, treat it as a string
        if (token.startsWith("\"") && token.endsWith("\"") && token.length() > 1) {
            String stringValue = token.substring(1, token.length() - 1);
            return new Atom(stringValue);
        }

        if (token.equals("nil")) {
            return new Nil();
        }
        if (token.equals("truth")) {
            return new Truth();
        }

        // Try to parse it as a number
        try {
            if (token.contains(".")) {
                return new Atom(Double.parseDouble(token)); // Floating point number
            } else {
                return new Atom(Integer.parseInt(token)); // Integer
            }
        } catch (NumberFormatException e) {
            // If it's not a number, treat it as a symbol
            return new Symbol(token);
        }
    }

    private SExpr parseFunction(SExpr first, Queue<String> tokens) throws Exception {
        // Parse the arguments of the function
        SExprList arguments = new SExprList();
        while (true) {
            if (tokens.isEmpty()) {
                throw new Exception("Unexpected end of input");
            }
            if (tokens.peek().equals(")")) {
                break; // End of list
            }
            arguments.add(parseTokens(tokens));
        }

        tokens.poll(); // Remove the closing ')'
        String functionName = first.toString();

        // Check if it's a global function
        List<String> globalFunctions = List.of(
            "+", "-", "*", "/", "%", "<", ">", "<=", ">=", "=", // Arithmetic and comparison functions
            "cons", "car", "cdr", // List manipulation functions
            "print", "quote", "'", "eval", // Utility functions
            "not", "cond", "and?", "or?", "if", "eq", // Logic functions
            "nil?", "number?", "list?", "symbol?", // Type checking functions
            "set", "define" // Assignment functions
        );
        if (globalFunctions.contains(functionName.toLowerCase())) {
            return new GlobalFunction(functionName, arguments);
        }

        // Otherwise, it's a user-defined function
        return new Function(functionName, arguments);
    }

    private SExpr parseList(SExpr first, Queue<String> tokens) throws Exception {
        SExprList list = new SExprList();
        list.add(first);
        while (true) {
            if (tokens.isEmpty()) {
                throw new Exception("Unexpected end of input");
            }
            if (tokens.peek().equals(")")) {
                break; // End of list
            }
            list.add(parseTokens(tokens)); // Recursively parse each element in the list or function call
        }
        tokens.poll(); // Remove the closing ')'
        return list;
    }
}
