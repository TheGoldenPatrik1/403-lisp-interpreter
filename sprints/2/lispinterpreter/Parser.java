package lispinterpreter;

import java.util.LinkedList;
import java.util.Queue;

public class Parser {
    private String input;

    public Parser(String input) {
        this.input = input;
    }

    // Method to parse the LISP expression
    public SExpr parse() throws Exception {
        Queue<String> tokens = tokenize(this.input);
        System.out.println(tokens);
        return parseTokens(tokens);
    }

    // Tokenizer: split the input into individual tokens, including function calls
    // private Queue<String> tokenize(String input) {
    // Queue<String> tokens = new LinkedList<>();
    // StringBuilder token = new StringBuilder();

    // for (int i = 0; i < input.length(); i++) {
    // char c = input.charAt(i);

    // if (c == '(' || c == ')') {
    // if (token.length() > 0) {
    // tokens.add(token.toString());
    // token.setLength(0);
    // }
    // tokens.add(String.valueOf(c)); // Add '(' or ')'
    // } else if (Character.isWhitespace(c)) {
    // if (token.length() > 0) {
    // tokens.add(token.toString());
    // token.setLength(0);
    // }
    // } else if (c == '(' && token.length() > 0) {
    // // Handle function call: if a symbol is followed by '(' treat it as function
    // tokens.add(token.toString());
    // token.setLength(0);
    // tokens.add(String.valueOf(c)); // Add '(' separately
    // } else {
    // token.append(c); // Build atom or function name
    // }
    // }

    // if (token.length() > 0) {
    // tokens.add(token.toString());
    // }

    // return tokens;
    // }

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
            if (c == '(' || c == ')') {
                if (token.length() > 0) {
                    tokens.add(token.toString());
                    token.setLength(0);
                }
                tokens.add(String.valueOf(c)); // Add '(' or ')'
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

        SExprList list = new SExprList();

        if (token.equals("(")) {
            // Create a new SExprList for the list or function call
            System.out.println("Entering (");

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
        } else if (token.equals(")")) {
            throw new Exception("Unexpected closing parenthesis");
        } else {
            // Create an atom (number, symbol, or string), or handle function call
            list.add(parseAtom(token, tokens));
            System.out.println(list.getList());
            return list;
        }
    }

    // Helper method to create an Atom or handle function calls
    private SExpr parseAtom(String token, Queue<String> tokens) throws Exception {
        // Check if the token is a function name (symbol followed by '(')
        if (!tokens.isEmpty() && tokens.peek().equals("(")) {
            // It's a function call: fetch the name and create a list of parameters
            String name = token;
            SExprList parameters = new SExprList();

            tokens.poll(); // Consume '('

            // Parse the function arguments (list inside the parentheses)
            while (!tokens.peek().equals(")")) {
                parameters.add(parseTokens(tokens)); // Recursively parse function arguments
            }
            tokens.poll(); // Consume closing ')'

            // Return the atomized function
            return new Atom(new Function(name, parameters));
        }

        // If the token is enclosed in quotes, treat it as a string
        if (token.startsWith("'") && token.endsWith("'") && token.length() > 1) {
            String stringValue = token.substring(1, token.length() - 1);
            return new Atom(stringValue);
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
            return new Atom(new Symbol(token));
        }
    }
}
