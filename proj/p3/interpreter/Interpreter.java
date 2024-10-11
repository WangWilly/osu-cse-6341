package interpreter;

import java.io.*;
import java.util.*;
import parser.ParserWrapper;
import ast.Program;
import ast.TypeCheck;

////////////////////////////////////////////////////////////////////////////////

public class Interpreter {
    // Process return codes
    public static final int EXIT_SUCCESS = 0;
    public static final int EXIT_PARSING_ERROR = 1;
    public static final int EXIT_STATIC_CHECKING_ERROR = 2;
    public static final int EXIT_UNINITIALIZED_VAR_ERROR = 3;
    public static final int EXIT_DIV_BY_ZERO_ERROR = 4;
    public static final int EXIT_FAILED_STDIN_READ = 5;

    ////////////////////////////////////////////////////////////////////////////

    public static void main(String[] args) {
        String filename = args[0];
        Program astRoot = null;
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(filename));
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        try {
            astRoot = ParserWrapper.parse(reader, filename);
        } catch (Exception ex) {
            Interpreter.fatalError("Uncaught parsing error: " + ex, EXIT_PARSING_ERROR);
        }
        // for debugging
        // astRoot.print(System.out);

        // type checking. If the program does not typecheck,
        // call fatalError with return code EXIT_STATIC_CHECKING_ERROR
        // TypeCheck typeCheck = new TypeCheck();
        // if (!astRoot.checkType(typeCheck)) {
        //     Interpreter.fatalError(
        //         "Uncaught static checking error",
        //         Interpreter.EXIT_STATIC_CHECKING_ERROR
        //     );
        // }
    }

    ////////////////////////////////////////////////////////////////////////////

    public static void fatalError(String message, int processReturnCode) {
        System.out.println(message);
        System.exit(processReturnCode);
    }
}
