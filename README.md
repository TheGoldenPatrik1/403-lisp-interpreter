# Lisp Interpreter

This is a basic interpreter for the [Lisp](https://en.wikipedia.org/wiki/Lisp_(programming_language)) programming language, implemented using [Java](https://www.java.com/en/).

## The Team

- Malachi Crain (CS-403)
- Sawyer Kent (CS-503)

## Steps to Run

1. Install [Java](https://www.java.com/en/download/).
2. Execute `make` or `make run` for a REPL environment.
3. Alternately, execute `make run <input filepath>` to run a file of Lisp code.
4. If you are on Windows, you can compile and run the program with the following commands:
    - `cd src`
    - `javac lispinterpreter/Lisp.java`
    - `java lispinterpreter.Lisp [input filepath]`

## Testing

### Steps to Run Test Harness

1. Execute `make test`.
2. If you are on Windows, you can compile and run the tests with the following commands:
    - `cd src`
    - `javac lispinterpreter/Lisp.java`
    - `java lispinterpreter.Lisp test`