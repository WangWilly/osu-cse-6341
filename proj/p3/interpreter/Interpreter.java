package interpreter;

import java.io.*;
import java.util.*;
import parser.ParserWrapper;
import ast.Program;
import ast.TypeCheck;
import ast.AstErrorHandler;
import ast.ValueMeta;
import java.util.Scanner;

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

        // for ReadIntExpr, ReadFloatExpr
        Scanner s = new Scanner(System.in);
        Queue<ValueMeta> values = new LinkedList<>();
        while (s.hasNext()) {
            if (s.hasNextInt()) {
                values.add(new ValueMeta(null, ValueMeta.ValueType.INT, Long.valueOf(s.nextInt())));
            } else if (s.hasNextFloat()) {
                values.add(new ValueMeta(null, ValueMeta.ValueType.FLOAT, Double.valueOf(s.nextFloat())));
            } else {
                Interpreter.fatalError("Failed to read from stdin", EXIT_FAILED_STDIN_READ);
            }
        }

        // type checking. If the program does not typecheck,
        // call fatalError with return code EXIT_STATIC_CHECKING_ERROR
        TypeCheck typeCheck = new TypeCheck();
        AstErrorHandler.ErrorCode code = astRoot.checkType(typeCheck);
        if (!AstErrorHandler.isSuccessful(code)) {
            switch (code) {
                case AstErrorHandler.ErrorCode.STATIC_CHECKING_ERROR:
                    Interpreter.fatalError(
                        "Uncaught static checking error",
                        Interpreter.EXIT_STATIC_CHECKING_ERROR
                    );
                    break;
                case AstErrorHandler.ErrorCode.UNINITIALIZED_VAR_ERROR:
                    Interpreter.fatalError(
                        "Uninitialized variable error",
                        Interpreter.EXIT_UNINITIALIZED_VAR_ERROR
                    );
                    break;
                case AstErrorHandler.ErrorCode.DIV_BY_ZERO_ERROR:
                    Interpreter.fatalError(
                        "Division by zero error",
                        Interpreter.EXIT_DIV_BY_ZERO_ERROR
                    );
                    break;
                case AstErrorHandler.ErrorCode.FAILED_STDIN_READ:
                    Interpreter.fatalError(
                        "Failed stdin read error",
                        Interpreter.EXIT_FAILED_STDIN_READ
                    );
                    break;
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////

    public static void fatalError(String message, int processReturnCode) {
        System.out.println(message);
        System.exit(processReturnCode);
    }
}
