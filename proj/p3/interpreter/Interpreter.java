package interpreter;

import ast.AstErrorHandler;
import ast.Program;
import ast.TypeCheck;
import ast.TypeHelper;
import ast.ValueMeta;
import java.io.*;
import java.util.*;
import java.util.Scanner;
import parser.ParserWrapper;

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
                values.add(ValueMeta.createInt(null, Long.valueOf(s.nextInt())));
            } else if (s.hasNextFloat()) {
                values.add(ValueMeta.createFloat(null, Double.valueOf(s.nextFloat())));
            } else {
                Interpreter.fatalError("Failed to read from stdin", EXIT_FAILED_STDIN_READ);
            }
        }

        // type checking. If the program does not typecheck,
        // call fatalError with return code EXIT_STATIC_CHECKING_ERROR
        Stack<Map<String,ValueMeta>> symbolTables = new Stack<Map<String,ValueMeta>>();
        TypeHelper typeHelper = new TypeHelper(symbolTables);
        TypeCheck typeCheck = new TypeCheck(typeHelper, symbolTables, values);
        AstErrorHandler.ErrorCode code = astRoot.checkType(typeCheck);
        if (!AstErrorHandler.isSuccessful(code)) {
            if (code == AstErrorHandler.ErrorCode.STATIC_CHECKING_ERROR) {
                Interpreter.fatalError("Uncaught static checking error", EXIT_STATIC_CHECKING_ERROR);
            } else if (code == AstErrorHandler.ErrorCode.UNINITIALIZED_VAR_ERROR) {
                Interpreter.fatalError("Uncaught uninitialized variable error", EXIT_UNINITIALIZED_VAR_ERROR);
            } else if (code == AstErrorHandler.ErrorCode.DIV_BY_ZERO_ERROR) {
                Interpreter.fatalError("Uncaught division by zero error", EXIT_DIV_BY_ZERO_ERROR);
            } else if (code == AstErrorHandler.ErrorCode.FAILED_STDIN_READ) {
                Interpreter.fatalError("Failed to read from stdin", EXIT_FAILED_STDIN_READ);
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////

    public static void fatalError(String message, int processReturnCode) {
        System.out.println(message);
        System.exit(processReturnCode);
    }
}
