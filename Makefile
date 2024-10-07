# Define the source file and classpath
SRC = src/lispinterpreter/Lisp.java
CLASS = lispinterpreter.Lisp

# Extract all arguments after the target
ARGS = $(wordlist 2, $(words $(MAKECMDGOALS)), $(MAKECMDGOALS))

# Default target: compile the Java program and run it with command line arguments
run: $(SRC)
	@echo "Compiling $(SRC)..."
	@javac $(SRC)
	@if [ -n "$(ARGS)" ]; then \
		echo "Running $(CLASS) on $(ARGS)..."; \
	else \
		echo "Running $(CLASS)..."; \
	fi
	@cd src && (trap '' INT; java $(CLASS) $(ARGS);)

# Allow passing command-line arguments to the Makefile
.PHONY: run

# Test target: compile the Java program and run its tests
test:
	@echo "Compiling $(SRC)..."
	@javac $(SRC)
	@cd src && java $(CLASS) test

# Clean up compiled class files
clean:
	@echo "Cleaning up..."
	@rm -f src/lispinterpreter/*.class
	@echo "Done."

# Prevent make from interpreting arguments as targets
%:
	@:
