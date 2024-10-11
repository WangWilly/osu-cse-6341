package ast;
import java.util.function.Function;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.Stack;

////////////////////////////////////////////////////////////////////////////////

public final class TypeCheck {
    private Stack <Map<String,ValueMeta>> symbolTables;
    private Queue <ValueMeta> injectedValues;

    public TypeCheck() {
        this.symbolTables = new Stack <Map<String,ValueMeta>>();
        // global scope
        this.symbolTables.push(new HashMap<String,ValueMeta>());
    }

    ////////////////////////////////////////////////////////////////////////////
    // SymbolTable (helper)

    private void pushSymbolTable() {
        this.symbolTables.push(new HashMap<String,ValueMeta>());
    }

    private boolean popSymbolTable() {
        if (this.symbolTables.size() <= 1) {
            return false;
        }

        this.symbolTables.pop();
        return true;
    }

    private boolean hasIdent(String ident) {
        for (int i = this.symbolTables.size() - 1; i >= 0; i--) {
            if (!this.symbolTables.get(i).containsKey(ident)) {
                continue;
            }
            return true;
        }
        return false;
    }

    private ValueMeta getValue(String ident) {
        for (int i = this.symbolTables.size() - 1; i >= 0; i--) {
        ValueMeta value = this.symbolTables.get(i).get(ident);
            if (value == null) {
                continue;
            }
            return value;
        }
        return null;
    }
                
    /**
    private ValueMeta getValidValue(String ident) {
        ValueMeta refer = getValue(ident);
        if (refer.getIntValue() == null && refer.getFloatValue() == null) {
            return null;
        }
        return refer;
    }
    */

    private ValueMeta findValue(String ident) {
        for (int i = this.symbolTables.size() - 1; i >= 0; i--) {
            ValueMeta value = this.symbolTables.get(i).get(ident);
            if (value == null || !value.hasValue()) {
                continue;
            }
            return value;
        }
        return null;
    }

    private ErrorHandler.ErrorCode putValue(String ident, ValueMeta value) {
        if (getValue(ident) != null) {
            return ErrorHandler.ErrorCode.STATIC_CHECKING_ERROR;
        }
        this.symbolTables.peek().put(ident, value);
        return ErrorHandler.ErrorCode.SUCCESS;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Decl (helper)

    public ValueMeta.ValueType getVarDeclType(VarDecl varDecl) {
        if (varDecl instanceof IntVarDecl) {
            return ValueMeta.ValueType.INT;
        }
        if (varDecl instanceof FloatVarDecl) {
            return ValueMeta.ValueType.FLOAT;
        }
        return ValueMeta.ValueType.UNDEFINED;
    }

    public ValueMeta.ValueType getDeclType(Decl decl) {
        if (decl.expr == null) {
            return getVarDeclType(decl.varDecl);
        }
        
        ValueMeta.ValueType varDeclType = getVarDeclType(decl.varDecl);
        ValueMeta.ValueType exprType = getExprType(decl.expr);

        if (varDeclType == ValueMeta.ValueType.UNDEFINED || exprType == ValueMeta.ValueType.UNDEFINED) {
            return ValueMeta.ValueType.UNDEFINED;
        }

        if (varDeclType == exprType) {
            return varDeclType;
        }

        return ValueMeta.ValueType.UNDEFINED;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Decl

    public ErrorHandler.ErrorCode checkDecl(Decl decl) {
        // Validate
        if (decl.expr == null) {
            return checkVarDecl(decl.varDecl);
        }
        // if (!(checkVarDecl(decl.varDecl) && checkExpr(decl.expr))) {
        Function<Void, Integer>[] functions = new Function[2];
        functions[0] = () -> checkVarDecl(decl.varDecl);
        functions[1] = () -> checkExpr(decl.expr);
        ErrorHandler.ErrorCode code = ErrorHandler.handle(functions);
        if (!ErrorHandler.isSuccessful(code)) {
            return code;
        }
        

        // Check
        ValueMeta.ValueType declType = getDeclType(decl);
        if (declType == ValueMeta.ValueType.UNDEFINED) {
            return ErrorHandler.ErrorCode.STATIC_CHECKING_ERROR;
        }

        // Update
        if (declType == ValueMeta.ValueType.INT || declType == ValueMeta.ValueType.FLOAT) {
            ValueMeta referVal = getExprValue(decl.expr);
            if (referVal == null) {
                return ErrorHandler.ErrorCode.UNINITIALIZED_VAR_ERROR;
            }
            ValueMeta value = referVal.copyWithIdent(decl.varDecl.ident);
            return putValue(decl.varDecl.ident, value);
        }

        return ErrorHandler.ErrorCode.STATIC_CHECKING_ERROR;
    }

    ////////////////////////////////////////////////////////////////////////////
    // VarDecl

    public ErrorHandler.ErrorCode checkVarDecl(VarDecl varDecl) {
        if (varDecl instanceof IntVarDecl) {
            return checkIntVarDecl((IntVarDecl) varDecl);
        }
        if (varDecl instanceof FloatVarDecl) {
            return checkFloatVarDecl((FloatVarDecl) varDecl);
        }
        return ErrorHandler.ErrorCode.STATIC_CHECKING_ERROR;
    }

    public ErrorHandler.ErrorCode checkIntVarDecl(IntVarDecl varDecl) {
        if (this.symbolTables.peek().containsKey(varDecl.ident)) {
            return ErrorHandler.ErrorCode.STATIC_CHECKING_ERROR;
        }
        return putValue(varDecl.ident, new ValueMeta(varDecl.ident, ValueMeta.ValueType.INT));
    }

    public boolean checkFloatVarDecl(FloatVarDecl varDecl) {
        if (this.symbolTables.peek().containsKey(varDecl.ident)) {
            return ErrorHandler.ErrorCode.STATIC_CHECKING_ERROR;
        }
        return putValue(varDecl.ident, new ValueMeta(varDecl.ident, ValueMeta.ValueType.FLOAT));
    }

    ////////////////////////////////////////////////////////////////////////////
    // Expr (helper)

    private ValueMeta.ValueType getLeftRightExprType(Expr expr1, Expr expr2) {
        ValueMeta.ValueType left = getExprType(expr1);
        ValueMeta.ValueType right = getExprType(expr2);

        if (left == ValueMeta.ValueType.UNDEFINED || right == ValueMeta.ValueType.UNDEFINED) {
            return ValueMeta.ValueType.UNDEFINED;
        }

        if (left == right) {
            return left;
        }

        return ValueMeta.ValueType.UNDEFINED;
    }

    private ValueMeta.ValueType getExprType(Expr expr) {
        if (expr instanceof IntConstExpr) {
            return ValueMeta.ValueType.INT;
        }
        if (expr instanceof FloatConstExpr) {
            return ValueMeta.ValueType.FLOAT;
        }
        if (expr instanceof IdentExpr) {
            IdentExpr identExpr = (IdentExpr) expr;
            ValueMeta meta = findValue(identExpr.ident);
            if (meta == null) {
                return ValueMeta.ValueType.UNDEFINED;
            }
            return meta.getType();
        }
        if (expr instanceof BinaryExpr) {
            BinaryExpr binExpr = (BinaryExpr) expr;
            return getLeftRightExprType(binExpr.expr1, binExpr.expr2);
        }
        if (expr instanceof UnaryMinusExpr) {
            UnaryMinusExpr unaryMinusExpr = (UnaryMinusExpr) expr;
            ValueMeta.ValueType type = getExprType(unaryMinusExpr.expr);
            if (type == ValueMeta.ValueType.INT || type == ValueMeta.ValueType.FLOAT) {
                return type;
            }
        }
        if (expr instanceof ReadIntExpr) {
            return ValueMeta.ValueType.INT;
        }
        if (expr instanceof ReadFloatExpr) {
            return ValueMeta.ValueType.FLOAT;
        }

        return ValueMeta.ValueType.UNDEFINED;
    }

    private ValueMeta.ValueType getLeftRightCondExprType(CondExpr expr1, CondExpr expr2) {
        ValueMeta.ValueType left = getCondExprType(expr1);
        ValueMeta.ValueType right = getCondExprType(expr2);

        if (left == ValueMeta.ValueType.UNDEFINED || right == ValueMeta.ValueType.UNDEFINED) {
            return ValueMeta.ValueType.UNDEFINED;
        }

        if (left == right) {
            return left;
        }

        return ValueMeta.ValueType.UNDEFINED;
    }

    private ValueMeta.ValueType getCondExprType(CondExpr condExpr) {
        if (condExpr instanceof CompExpr) {
            CompExpr compExpr = (CompExpr) condExpr;
            return getLeftRightExprType(compExpr.expr1, compExpr.expr2);
        }
        if (condExpr instanceof LogicalExpr) {
            return ValueMeta.ValueType.BOOL;
        }

        return ValueMeta.ValueType.UNDEFINED;
    }

    private ValueMeta getBinaryExprValue(BinaryExpr binExpr) {
        ValueMeta left = getExprValue(binExpr.expr1);
        ValueMeta right = getExprValue(binExpr.expr2);
        if (left == null || right == null) {
            return null;
        }

        switch (binExpr.op) {
            case BinaryExpr.PLUS:
                if (left.getType() == ValueMeta.ValueType.INT && right.getType() == ValueMeta.ValueType.INT) {
                    return new ValueMeta(null, ValueMeta.ValueType.INT, left.getIntValue() + right.getIntValue());
                }
                if (left.getType() == ValueMeta.ValueType.FLOAT && right.getType() == ValueMeta.ValueType.FLOAT) {
                    return new ValueMeta(null, ValueMeta.ValueType.FLOAT, left.getFloatValue() + right.getFloatValue());
                }
                break;
            case BinaryExpr.MINUS:
                if (left.getType() == ValueMeta.ValueType.INT && right.getType() == ValueMeta.ValueType.INT) {
                    return new ValueMeta(null, ValueMeta.ValueType.INT, left.getIntValue() - right.getIntValue());
                }
                if (left.getType() == ValueMeta.ValueType.FLOAT && right.getType() == ValueMeta.ValueType.FLOAT) {
                    return new ValueMeta(null, ValueMeta.ValueType.FLOAT, left.getFloatValue() - right.getFloatValue());
                }
                break;
            case BinaryExpr.TIMES:
                if (left.getType() == ValueMeta.ValueType.INT && right.getType() == ValueMeta.ValueType.INT) {
                    return new ValueMeta(null, ValueMeta.ValueType.INT, left.getIntValue() * right.getIntValue());
                }
                if (left.getType() == ValueMeta.ValueType.FLOAT && right.getType() == ValueMeta.ValueType.FLOAT) {
                    return new ValueMeta(null, ValueMeta.ValueType.FLOAT, left.getFloatValue() * right.getFloatValue());
                }
                break;
            case BinaryExpr.DIV:
                if (left.getType() == ValueMeta.ValueType.INT && right.getType() == ValueMeta.ValueType.INT) {
                    return new ValueMeta(null, ValueMeta.ValueType.INT, left.getIntValue() / right.getIntValue());
                }
                if (left.getType() == ValueMeta.ValueType.FLOAT && right.getType() == ValueMeta.ValueType.FLOAT) {
                    return new ValueMeta(null, ValueMeta.ValueType.FLOAT, left.getFloatValue() / right.getFloatValue());
                }
                break;
        }

        return null;
    }

    private ValueMeta getExprValue(Expr expr) {
        if (expr instanceof IntConstExpr) {
            IntConstExpr intConstExpr = (IntConstExpr) expr;
            return new ValueMeta(null, ValueMeta.ValueType.INT, intConstExpr.ival);
        }
        if (expr instanceof FloatConstExpr) {
            FloatConstExpr floatConstExpr = (FloatConstExpr) expr;
            return new ValueMeta(null, ValueMeta.ValueType.FLOAT, floatConstExpr.fval);
        }
        if (expr instanceof IdentExpr) {
            IdentExpr identExpr = (IdentExpr) expr;
            ValueMeta val = findValue(identExpr.ident);
            if (val == null) {
                return null;
            }
            return val;
        }
        if (expr instanceof BinaryExpr) {
            BinaryExpr binExpr = (BinaryExpr) expr;
            return getBinaryExprValue(binExpr);
        }
        if (expr instanceof UnaryMinusExpr) {
            UnaryMinusExpr unaryMinusExpr = (UnaryMinusExpr) expr;
            ValueMeta value = getExprValue(unaryMinusExpr.expr);
            if (value.getType() == ValueMeta.ValueType.INT) {
                return new ValueMeta(null, ValueMeta.ValueType.INT, -value.getIntValue());
            }
            if (value.getType() == ValueMeta.ValueType.FLOAT) {
                return new ValueMeta(null, ValueMeta.ValueType.FLOAT, -value.getFloatValue());
            }
        }
        if (expr instanceof ReadIntExpr) {
            return injectedValues.poll();
        }
        if (expr instanceof ReadFloatExpr) {
            return injectedValues.poll();
        }

        return null;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Expr

    private ErrorHandler.ErrorCode checkExpr(Expr expr) {
        if (expr instanceof IntConstExpr) {
            return checkIntConstExpr((IntConstExpr) expr);
        }
        if (expr instanceof FloatConstExpr) {
            return checkFloatConstExpr((FloatConstExpr) expr);
        }
        if (expr instanceof IdentExpr) {
            return checkIdentExpr((IdentExpr) expr);
        }
        if (expr instanceof BinaryExpr) {
            return checkBinExpr((BinaryExpr) expr);
        }
        if (expr instanceof UnaryMinusExpr) {
            return checkUnaryMinusExpr((UnaryMinusExpr) expr);
        }
        if (expr instanceof ReadIntExpr) {
            return checkReadIntExpr((ReadIntExpr) expr);
        }
        if (expr instanceof ReadFloatExpr) {
            return checkReadFloatExpr((ReadFloatExpr) expr);
        }

        return ErrorHandler.ErrorCode.STATIC_CHECKING_ERROR;
    }

    public ErrorHandler.ErrorCode checkIntConstExpr(IntConstExpr intConstExpr) {
        return ErrorHandler.ErrorCode.SUCCESS;
    }

    public ErrorHandler.ErrorCode checkFloatConstExpr(FloatConstExpr floatConstExpr) {
        return ErrorHandler.ErrorCode.SUCCESS;
    }

    public ErrorHandler.ErrorCode checkIdentExpr(IdentExpr identExpr) {
        ValueMeta meta = findValue(identExpr.ident);
        if (meta == null) {
            return ErrorHandler.ErrorCode.UNINITIALIZED_VAR_ERROR;
        }
        return ErrorHandler.ErrorCode.SUCCESS;
    }

    public ErrorHandler.ErrorCode checkUnaryMinusExpr(UnaryMinusExpr unaryMinusExpr) {
        return checkExpr(unaryMinusExpr.expr);
    }

    public ErrorHandler.ErrorCode checkBinExpr(BinaryExpr binExpr) {
        if (binExpr.expr1 == null || binExpr.expr2 == null) {
            return ErrorHandler.ErrorCode.STATIC_CHECKING_ERROR;
        }
        // if (!checkExpr(binExpr.expr1) || !checkExpr(binExpr.expr2)) {
        Function<Void, Integer>[] functions = new Function[2];
        functions[0] = () -> checkExpr(binExpr.expr1);
        functions[1] = () -> checkExpr(binExpr.expr2);
        ErrorHandler.ErrorCode code = ErrorHandler.handle(functions);
        if (!ErrorHandler.isSuccessful(code)) {
            return code;
        }

        ValueMeta.ValueType left = getExprType(binExpr.expr1);
        ValueMeta.ValueType right = getExprType(binExpr.expr2);

        if (left == ValueMeta.ValueType.UNDEFINED || right == ValueMeta.ValueType.UNDEFINED) {
            return ErrorHandler.ErrorCode.STATIC_CHECKING_ERROR;
        }
        
        if (left == right) {
            return ErrorHandler.ErrorCode.SUCCESS;
        }

        return ErrorHandler.ErrorCode.STATIC_CHECKING_ERROR;
    }

    private ErrorHandler.ErrorCode checkCondExpr(CondExpr condExpr) {
        if (condExpr instanceof CompExpr) {
            return checkCompExpr((CompExpr) condExpr);
        }
        if (condExpr instanceof LogicalExpr) {
            return checkLogicalExpr((LogicalExpr) condExpr);
        }

        return ErrorHandler.ErrorCode.STATIC_CHECKING_ERROR;
    }

    public ErrorHandler.ErrorCode checkCompExpr(CompExpr compExpr) {
        if (compExpr.expr1 == null || compExpr.expr2 == null) {
            return ErrorHandler.ErrorCode.STATIC_CHECKING_ERROR;
        }
        // if (!checkExpr(compExpr.expr1) || !checkExpr(compExpr.expr2)) {
        Function<Void, Integer>[] functions = new Function[2];
        functions[0] = () -> checkExpr(compExpr.expr1);
        functions[1] = () -> checkExpr(compExpr.expr2);
        ErrorHandler.ErrorCode code = ErrorHandler.handle(functions);
        if (!ErrorHandler.isSuccessful(code)) {
            return code;
        }

        ValueMeta.ValueType left = getExprType(compExpr.expr1);
        ValueMeta.ValueType right = getExprType(compExpr.expr2);

        if (left == ValueMeta.ValueType.UNDEFINED || right == ValueMeta.ValueType.UNDEFINED) {
            return ErrorHandler.ErrorCode.STATIC_CHECKING_ERROR;
        }
        
        if (left == right) {
            return ErrorHandler.ErrorCode.SUCCESS;
        }

        return ErrorHandler.ErrorCode.STATIC_CHECKING_ERROR;
    }

    public ErrorHandler.ErrorCode checkLogicalExpr(LogicalExpr logicalExpr) {
        if (logicalExpr.expr1 == null) {
            return ErrorHandler.ErrorCode.STATIC_CHECKING_ERROR;
        }
        if (logicalExpr.op == LogicalExpr.NOT) {
            return checkCondExpr(logicalExpr.expr1);
        }
        if (logicalExpr.expr2 == null) {
            return ErrorHandler.ErrorCode.STATIC_CHECKING_ERROR;
        }
        // if (!checkCondExpr(logicalExpr.expr1) || !checkCondExpr(logicalExpr.expr2)) {
        Function<Void, Integer>[] functions = new Function[2];
        functions[0] = () -> checkCondExpr(logicalExpr.expr1);
        functions[1] = () -> checkCondExpr(logicalExpr.expr2);
        ErrorHandler.ErrorCode code = ErrorHandler.handle(functions);
        if (!ErrorHandler.isSuccessful(code)) {
            return code;
        }

        // ValueMeta.ValueType leftRight = getLeftRightCondExprType(logicalExpr.expr1, logicalExpr.expr2);
        // if (leftRight == ValueMeta.ValueType.UNDEFINED) {
        //     return false;
        // }

        return ErrorHandler.ErrorCode.SUCCESS;
    }

    public ErrorHandler.ErrorCode checkReadIntExpr(ReadIntExpr readIntExpr) {
        if (injectedValues == null || injectedValues.isEmpty()) {
            return ErrorHandler.ErrorCode.FAILED_STDIN_READ;
        }
        if (injectedValues.peek().getType() != ValueMeta.ValueType.INT) {
            return ErrorHandler.ErrorCode.FAILED_STDIN_READ;
        }
        return ErrorHandler.ErrorCode.SUCCESS;
    }

    public ErrorHandler.ErrorCode checkReadFloatExpr(ReadFloatExpr readFloatExpr) {
        if (injectedValues == null || injectedValues.isEmpty()) {
            return ErrorHandler.ErrorCode.FAILED_STDIN_READ;
        }
        if (injectedValues.peek().getType() != ValueMeta.ValueType.FLOAT) {
            return ErrorHandler.ErrorCode.FAILED_STDIN_READ;
        }
        return ErrorHandler.ErrorCode.SUCCESS;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Stmt

    private ErrorHandler.ErrorCode checkStmt(Stmt stmt) {
        if (stmt instanceof BlockStmt) {
            return checkBlockStmt((BlockStmt) stmt);
        }
        if (stmt instanceof IfStmt) {
            return checkIfStmt((IfStmt) stmt);
        }
        if (stmt instanceof AssignStmt) {
            return checkAssignStmt((AssignStmt) stmt);
        }
        if (stmt instanceof PrintStmt) {
            return checkPrintStmt((PrintStmt) stmt);
        }

        return ErrorHandler.ErrorCode.STATIC_CHECKING_ERROR;
    }

    public ErrorHandler.ErrorCode checkBlockStmt(BlockStmt blockStmt) {
        pushSymbolTable();
        ErrorHandler.ErrorCode code = checkUnitList(blockStmt.block);
        return popSymbolTable() ? code : ErrorHandler.ErrorCode.STATIC_CHECKING_ERROR;
    }

    public ErrorHandler.ErrorCode checkIfStmt(IfStmt ifStmt) {
        ErrorHandler.ErrorCode code = checkCondExpr(ifStmt.expr);
        if (!ErrorHandler.isSuccessful(code)) {
            return code;
        }
        code = checkStmt(ifStmt.thenstmt);
        if (!ErrorHandler.isSuccessful(code)) {
            return code;
        }
        if (ifStmt.elsestmt != null) {
            code = checkStmt(ifStmt.elsestmt);
            if (!ErrorHandler.isSuccessful(code)) {
                return code;
            }
        }

        return ErrorHandler.ErrorCode.SUCCESS;
    }

    public ErrorHandler.ErrorCode checkWhileStmt(WhileStmt whileStmt) {
        // if (!checkCondExpr(whileStmt.expr)) {
        ErrorHandler.ErrorCode code = checkCondExpr(whileStmt.expr);
        if (!ErrorHandler.isSuccessful(code)) {
            return code;
        }
        // if (!checkStmt(whileStmt.body)) {
        code = checkStmt(whileStmt.body);
        if (!ErrorHandler.isSuccessful(code)) {
            return code;
        }

        return ErrorHandler.ErrorCode.SUCCESS;
    }

    public ErrorHandler.ErrorCode checkAssignStmt(AssignStmt assignStmt) {
        // Validate
        ValueMeta meta = findValue(assignStmt.ident);
        if (meta == null) {
            return ErrorHandler.ErrorCode.UNINITIALIZED_VAR_ERROR;
        }

        // if (!checkExpr(assignStmt.expr)) {
        ErrorHandler.ErrorCode code = checkExpr(assignStmt.expr);
        if (!ErrorHandler.isSuccessful(code)) {
            return code;
        }

        ValueMeta.ValueType exprType = getExprType(assignStmt.expr);
        if (meta.getType() != exprType) {
            return ErrorHandler.ErrorCode.STATIC_CHECKING_ERROR;
        }

        // Update
        ValueMeta value = getExprValue(assignStmt.expr).copyWithIdent(assignStmt.ident);
        putValue(assignStmt.ident, value);

        return ErrorHandler.ErrorCode.SUCCESS;
    }

    public ErrorHandler.ErrorCode checkPrintStmt(PrintStmt printStmt) {
        // boolean status = checkExpr(printStmt.expr);
        ErrorHandler.ErrorCode code = checkExpr(printStmt.expr);
        if (!ErrorHandler.isSuccessful(code)) {
            return code;
        }

        ValueType type = getExprType(printStmt.expr);
        switch (type) {
            case ValueType.INT:
                System.out.println(getExprValue(printStmt.expr).getIntValue());
                break;

            case ValueType.FLOAT:
                System.out.println(getExprValue(printStmt.expr).getFloatValue());
                break;
        
            default:
                return ErrorHandler.ErrorCode.STATIC_CHECKING_ERROR;
        }

        return ErrorHandler.ErrorCode.SUCCESS;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Unit

    private ErrorHandler.ErrorCode checkUnit(Unit unit) {
        return unit.checkType(this);
    }

    public ErrorHandler.ErrorCode checkUnitList(UnitList ul) {
        if (ul == null) {
            return ErrorHandler.ErrorCode.STATIC_CHECKING_ERROR;
        }

        // if (!checkUnit(ul.unit)) {
        ErrorHandler.ErrorCode code = checkUnit(ul.unit);
        if (!ErrorHandler.isSuccessful(code)) {
            return code;
        }

        return checkUnitList(ul.unitList);
    }
}
