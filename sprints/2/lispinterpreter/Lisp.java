package lispinterpreter;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Lisp {
    public static void main(String[] args) {
        if (args.length > 0) {
            String argument = args[0];
            if (argument.equalsIgnoreCase("test")) {
                runTests();
            } else {
                runFile(argument);
            }
        } else {
            runPrompt();
        }
    }

    private static void runFile(String filename) {
        try {
            String content = Files.readString(Paths.get(filename));
            run(content);
        } catch (IOException e) {
            System.out.println("Error reading file: " + filename);
        }
    }

    private static void runPrompt() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please enter input (type 'exit' to stop):");
        System.out.print("> ");
        String userInput;
        while (!(userInput = scanner.nextLine()).equalsIgnoreCase("exit")) {
            run(userInput);
            System.out.print("> ");
        }
        scanner.close();
    }

    private static void run(String input) {
        try {
            Parser parser = new Parser(input);
            SExpr statements = parser.parse();
            System.out.println(statements);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void runTests() {
        testSprint2();
    }

    private static SExpr atom(String value) {
        try {
            if (value.contains(".")) {
                return new Atom(Double.parseDouble(value)); // Floating point number
            } else {
                return new Atom(Integer.parseInt(value)); // Integer
            }
        } catch (NumberFormatException e) {
            // If it's not a number, treat it as a symbol
            return new Atom(new Symbol(value));
        }
    }

    private static SExpr cons(SExpr car, SExpr cdr) {
        return new Cons(car, cdr);
    }

    private static void print(SExpr expr) {
        if (expr instanceof Nil) {
            System.out.println("nil");
        } else if (expr instanceof Truth) {
            System.out.println("truth");
        } else if (expr instanceof Atom) {
            System.out.println(((Atom) expr).getValue());
        } else if (expr instanceof Cons) {
            Cons cons = (Cons) expr;
            System.out.print("(");
            print(cons.getCar());
            System.out.print(" . ");
            print(cons.getCdr());
            System.out.print(")");
        }
    }

    private static void testSprint2() {
        print(Nil.INSTANCE); // Should print "nil"
        print(Truth.INSTANCE); // Should print "truth"
        print(atom("symbol")); // Should print the symbol "symbol"
        print(atom("411")); // Should print the number "411"

        // Build and print a list: (one . (two . (three . nil)))
        print(cons(atom("one"),
                cons(atom("two"),
                        cons(atom("three"), Nil.INSTANCE))));
    }
}
