# Lisp Interpreter

This is a basic interpreter for the [Lisp](https://en.wikipedia.org/wiki/Lisp_(programming_language)) programming language, implemented using [Java](https://www.java.com/en/).

## The Team

- Malachi Crain (CS-403)
- Sawyer Kent (CS-503)

## Steps to Run

1. Install [Java](https://www.java.com/en/download/).
2. Execute `make` or `make run` for a REPL environment.
3. Alternately, execute `make run <input filepath>` to run a file of Lisp code. Note that the Makefile will search the repository for the file, so there's no need to provide the full filepath.
4. If you are on Windows, you can compile and run the program manually using `javac` and `java lispinterpreter.Lisp [input filepath]`.

## Challenges

In addition to implementing the basic requirements for this project, we also extended the language by implementing the following "challenge" features:
* `+`, `-`, etc can take more than two arguments.

## Testing

We added tests as we proceeded through each sprint, in order to test features as we added them. We also drew inspiration from the [test folder](https://github.com/munificent/craftinginterpreters/tree/master/test) of the GitHub Repository for the [Crafting Interpreters](https://craftinginterpreters.com/index.html) textbook.

### Testing Plan

We have **TBD** tests, covering every aspect of the Lisp programming language that we implemented. They are divided into the following categories:
* **arithmetic**
    * Tests `+`, `-`, `/`, `*`, `%`, and combinations thereof.
* **equality** -
    * Tests `>`, `>=`, `<`, and `<=` for numbers.
    * Tests `=` for numbers, strings, and lists.
* **functions** -
    * Tests `define` and user-defined function calls.
    * Tests different number of parameters.
* **logic** -
    * Tests `and?`, `or?`, `cond`, `if`, and `not`.
    * Tests for proper short-circuting.
* **operators** -
    * Tests the main global-defined functions: `cons`, `car`, `cdr`, `eval`, `quote`, and `set`.
* **type_checking** -
    * Tests `list?`, `nil?`, `symbol?`, and `number?`.

### Sample Test Run

An example test run is provided in `sample_test_run.txt`. This file was automatically generated using `make test > sample_test_run.txt`.

### Test Harness

1. The `/tests` directory contains `.lisp` files which our test harness executes.
2. For each file, the output of the `print` statements is written to a corresponding `.txt` file in the `/output/actual` directory.
3. The test harness then compares this output file to a corresponding `.txt` file in the `/output/expected` directory to assert that the two files match exactly. The `expected` file's contents are manually created based on what the test out to output.
4. In the event that the `.lisp` test file generates an error, it will output the error into the `/output/actual` file, allowing the test harness to anticipate, expect, and gracefully handle errors.

### Steps to Run Test Harness

1. Execute `make test`.
2. If you are on Windows, you can compile manually using `javac` and run the tests using `java lispinterpreter.Lisp test`.