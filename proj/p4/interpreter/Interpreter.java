package interpreter;

import ast.AstErrorHandler;
import ast.Program;
import ast.SymbolTableHelper;
import ast.TypeCheck;
import ast.ValueMeta;
import ast.RuntimeMeta;
import ast.RealRuntime;
import ast.AbstRuntime;
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

        /**
        // for ReadIntExpr, ReadFloatExpr
        Queue<ValueMeta> values = new LinkedList<>();
        try {
            if (System.in.available() != 0) {
                Scanner s = new Scanner(System.in);
                while (s.hasNext()) {
                    if (s.hasNextInt()) {
                        values.add(ValueMeta.createInt(null, Long.valueOf(s.nextInt())));
                    } else if (s.hasNextFloat()) {
                        values.add(ValueMeta.createFloat(null, Double.valueOf(s.nextFloat())));
                    } else {
                        throw new RuntimeException("Invalid input");
                    }
                }
                s.close();
            }
        } catch (IOException ex) {
            // do nothing
        }
        */


        // type checking. If the program does not typecheck,
        // call fatalError with return code EXIT_STATIC_CHECKING_ERROR
        Stack<Map<String,ValueMeta>> symbolTables = new Stack<Map<String,ValueMeta>>();
        SymbolTableHelper symbolTableHelper = new SymbolTableHelper(symbolTables);
        TypeCheck typeCheck = new TypeCheck(symbolTableHelper);
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

        /**
        // run the program
        Stack<Map<String,ValueMeta>> symbolTablesRun = new Stack<Map<String,ValueMeta>>();
        SymbolTableHelper symbolTableHelperRun = new SymbolTableHelper(symbolTablesRun);
        RealRuntime runtime = new RealRuntime(symbolTableHelperRun, values);
        RuntimeMeta runtimeMeta = astRoot.run(runtime);
        if (runtimeMeta == null) {
            throw new RuntimeException("Failed to run the program");
        }
        if (!runtimeMeta.isSuccessful()) {
            if (runtimeMeta.getErrorCode() == AstErrorHandler.ErrorCode.FAILED_STDIN_READ) {
                Interpreter.fatalError("Failed to read from stdin", EXIT_FAILED_STDIN_READ);
            }
            if (runtimeMeta.getErrorCode() == AstErrorHandler.ErrorCode.DIV_BY_ZERO_ERROR) {
                Interpreter.fatalError("Division by zero error", EXIT_DIV_BY_ZERO_ERROR);
            }
            if (runtimeMeta.getErrorCode() == AstErrorHandler.ErrorCode.UNINITIALIZED_VAR_ERROR) {
                Interpreter.fatalError("Uninitialized variable error", EXIT_UNINITIALIZED_VAR_ERROR);
            }
        }
        */

        // run the program w/ AbstRuntime
        Stack<Map<String,ValueMeta>> symbolTablesRun = new Stack<Map<String,ValueMeta>>();
        SymbolTableHelper symbolTableHelperRun = new SymbolTableHelper(symbolTablesRun);
        AbstRuntime runtime = new AbstRuntime(symbolTableHelperRun);
        RuntimeMeta runtimeMeta = astRoot.run(runtime);
        if (runtimeMeta == null) {
            throw new RuntimeException("Failed to run the program");
        }
        if (!runtimeMeta.isSuccessful()) {
            if (runtimeMeta.getErrorCode() == AstErrorHandler.ErrorCode.FAILED_STDIN_READ) {
                Interpreter.fatalError("Failed to read from stdin", EXIT_FAILED_STDIN_READ);
            }
            if (runtimeMeta.getErrorCode() == AstErrorHandler.ErrorCode.DIV_BY_ZERO_ERROR) {
                Interpreter.fatalError("Division by zero error", EXIT_DIV_BY_ZERO_ERROR);
            }
            if (runtimeMeta.getErrorCode() == AstErrorHandler.ErrorCode.UNINITIALIZED_VAR_ERROR) {
                Interpreter.fatalError("Uninitialized variable error", EXIT_UNINITIALIZED_VAR_ERROR);
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////

    public static void fatalError(String message, int processReturnCode) {
        System.out.println(message);
        System.exit(processReturnCode);
    }
}
