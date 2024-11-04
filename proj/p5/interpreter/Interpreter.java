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

        // type checking. If the program does not typecheck,
        // call fatalError with return code EXIT_STATIC_CHECKING_ERROR
        Stack<Map<String,ValueMeta>> symbolTables = new Stack<Map<String,ValueMeta>>();
        SymbolTableHelper symbolTableHelper = new SymbolTableHelper(symbolTables);
        TypeCheck typeCheck = new TypeCheck(symbolTableHelper);
        AstErrorHandler.ErrorCode code = astRoot.checkType(typeCheck);
        if (!AstErrorHandler.isSuccessful(code)) {
            if (code == AstErrorHandler.ErrorCode.STATIC_CHECKING_ERROR) {
                Interpreter.fatalError("Uncaught static checking error", EXIT_STATIC_CHECKING_ERROR);
            }
            if (code == AstErrorHandler.ErrorCode.UNINITIALIZED_VAR_ERROR) {
                Interpreter.fatalError("Uncaught uninitialized variable error", EXIT_UNINITIALIZED_VAR_ERROR);
            }
        }

        // run the program w/ AbstRuntime
        Stack<Map<String,ValueMeta>> symbolTablesRun = new Stack<Map<String,ValueMeta>>();
        SymbolTableHelper symbolTableHelperRun = new SymbolTableHelper(symbolTablesRun);
        AbstRuntime runtime = new AbstRuntime(symbolTableHelperRun);
        RuntimeMeta runtimeMeta = astRoot.run(runtime);
        if (runtimeMeta == null) {
            throw new RuntimeException("Failed to run the program");
        }
        if (!runtimeMeta.isSuccessful()) {
            if (runtimeMeta.getErrorCode() == AstErrorHandler.ErrorCode.DIV_BY_ZERO_ERROR) {
                Interpreter.fatalError("Division by zero error", EXIT_DIV_BY_ZERO_ERROR);
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////

    public static void fatalError(String message, int processReturnCode) {
        System.out.println(message);
        System.exit(processReturnCode);
    }
}
