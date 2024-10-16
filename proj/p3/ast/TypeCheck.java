package ast;
import java.util.function.Supplier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.Stack;

////////////////////////////////////////////////////////////////////////////////

public final class TypeCheck {
    private Stack<Map<String,ValueMeta>> symbolTables; // TODO: to be removed
    private TypeHelper typeHelper;
    private Queue<ValueMeta> injectedValues;
    private HashMap<Expr, ValueMeta> injectedHashMap = new HashMap<Expr, ValueMeta>();

    ////////////////////////////////////////////////////////////////////////////
    // Constructor

    public TypeCheck(TypeHelper typeHelper, Stack<Map<String,ValueMeta>> symbolTables, Queue<ValueMeta> injectedValues) {
        this.symbolTables = symbolTables;
        this.typeHelper = typeHelper;
        // global scope
        this.symbolTables.push(new HashMap<String,ValueMeta>());
        this.injectedValues = injectedValues;
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

    // TODO:
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

    // TODO:
    private AstErrorHandler.ErrorCode putValue(String ident, ValueMeta value) {
        // if (getValue(ident) != null) {
        //     return AstErrorHandler.ErrorCode.STATIC_CHECKING_ERROR;
        // }
        this.symbolTables.peek().put(ident, value);
        return AstErrorHandler.ErrorCode.SUCCESS;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Decl

    public AstErrorHandler.ErrorCode checkDecl(Decl decl) {
        // Validate
        if (decl.expr == null) {
            return checkVarDecl(decl.varDecl);
        }
        // if (!(checkVarDecl(decl.varDecl) && checkExpr(decl.expr))) {
        ArrayList<Supplier<AstErrorHandler.ErrorCode>> functions = new ArrayList<Supplier<AstErrorHandler.ErrorCode>>();
        functions.add(() -> checkVarDecl(decl.varDecl));
        functions.add(() -> checkExpr(decl.expr));
        AstErrorHandler.ErrorCode code = AstErrorHandler.handle(functions);
        if (!AstErrorHandler.isSuccessful(code)) {
            return code;
        }
        

        // Check
        ValueMeta.ValueType declType = typeHelper.getDeclType(decl);
        if (declType == ValueMeta.ValueType.UNDEFINED) {
            return AstErrorHandler.ErrorCode.STATIC_CHECKING_ERROR;
        }

        // Update
        if (declType == ValueMeta.ValueType.INT || declType == ValueMeta.ValueType.FLOAT) {
            /** TODO: move to runtime
            ValueMeta referVal = getExprValue(decl.expr);
            if (referVal == null) {
                return AstErrorHandler.ErrorCode.UNINITIALIZED_VAR_ERROR;
            }
            ValueMeta value = referVal.copyWithIdent(decl.varDecl.ident);
            */
            ValueMeta.ValueType exprType = typeHelper.getExprType(decl.expr);
            if (declType != exprType) {
                return AstErrorHandler.ErrorCode.STATIC_CHECKING_ERROR;
            }
            return putValue(decl.varDecl.ident, ValueMeta.createZero(decl.varDecl.ident, declType));
        }

        return AstErrorHandler.ErrorCode.STATIC_CHECKING_ERROR;
    }

    ////////////////////////////////////////////////////////////////////////////
    // VarDecl

    public AstErrorHandler.ErrorCode checkVarDecl(VarDecl varDecl) {
        if (varDecl instanceof IntVarDecl) {
            return checkIntVarDecl((IntVarDecl) varDecl);
        }
        if (varDecl instanceof FloatVarDecl) {
            return checkFloatVarDecl((FloatVarDecl) varDecl);
        }
        return AstErrorHandler.ErrorCode.STATIC_CHECKING_ERROR;
    }

    public AstErrorHandler.ErrorCode checkIntVarDecl(IntVarDecl varDecl) {
        if (this.symbolTables.peek().containsKey(varDecl.ident)) {
            return AstErrorHandler.ErrorCode.STATIC_CHECKING_ERROR;
        }
        return putValue(varDecl.ident, ValueMeta.createNull(varDecl.ident, ValueMeta.ValueType.INT));
    }

    public AstErrorHandler.ErrorCode checkFloatVarDecl(FloatVarDecl varDecl) {
        if (this.symbolTables.peek().containsKey(varDecl.ident)) {
            return AstErrorHandler.ErrorCode.STATIC_CHECKING_ERROR;
        }
        return putValue(varDecl.ident, ValueMeta.createNull(varDecl.ident, ValueMeta.ValueType.FLOAT));
    }

    ////////////////////////////////////////////////////////////////////////////
    // Expr (helper)

    private ValueMeta getBinaryExprValue(BinaryExpr binExpr) {
        ValueMeta left = getExprValue(binExpr.expr1);
        ValueMeta right = getExprValue(binExpr.expr2);
        if (left == null || right == null) {
            return null;
        }

        switch (binExpr.op) {
            case BinaryExpr.PLUS:
                if (left.getType() == ValueMeta.ValueType.INT && right.getType() == ValueMeta.ValueType.INT) {
                    return ValueMeta.createInt(null, left.getIntValue() + right.getIntValue());
                }
                if (left.getType() == ValueMeta.ValueType.FLOAT && right.getType() == ValueMeta.ValueType.FLOAT) {
                    return ValueMeta.createFloat(null, left.getFloatValue() + right.getFloatValue());
                }
                break;
            case BinaryExpr.MINUS:
                if (left.getType() == ValueMeta.ValueType.INT && right.getType() == ValueMeta.ValueType.INT) {
                    return ValueMeta.createInt(null, left.getIntValue() - right.getIntValue());
                }
                if (left.getType() == ValueMeta.ValueType.FLOAT && right.getType() == ValueMeta.ValueType.FLOAT) {
                    return ValueMeta.createFloat(null, left.getFloatValue() - right.getFloatValue());
                }
                break;
            case BinaryExpr.TIMES:
                if (left.getType() == ValueMeta.ValueType.INT && right.getType() == ValueMeta.ValueType.INT) {
                    return ValueMeta.createInt(null, left.getIntValue() * right.getIntValue());
                }
                if (left.getType() == ValueMeta.ValueType.FLOAT && right.getType() == ValueMeta.ValueType.FLOAT) {
                    return ValueMeta.createFloat(null, left.getFloatValue() * right.getFloatValue());
                }
                break;
            case BinaryExpr.DIV:
                if (left.getType() == ValueMeta.ValueType.INT && right.getType() == ValueMeta.ValueType.INT) {
                    if (right.getIntValue() == 0) {
                        // TODO:
                        return null;
                    }
                    return ValueMeta.createInt(null, left.getIntValue() / right.getIntValue());
                }
                if (left.getType() == ValueMeta.ValueType.FLOAT && right.getType() == ValueMeta.ValueType.FLOAT) {
                    if (right.getFloatValue() == 0.0) {
                        // TODO:
                        return null;
                    }
                    return ValueMeta.createFloat(null, left.getFloatValue() / right.getFloatValue());
                }
                break;
        }

        return null;
    }

    private ValueMeta getExprValue(Expr expr) {
        if (expr instanceof IntConstExpr) {
            IntConstExpr intConstExpr = (IntConstExpr) expr;
            return ValueMeta.createInt(null, intConstExpr.ival);
        }
        if (expr instanceof FloatConstExpr) {
            FloatConstExpr floatConstExpr = (FloatConstExpr) expr;
            return ValueMeta.createFloat(null, floatConstExpr.fval);
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
                return ValueMeta.createInt(null, -value.getIntValue());
            }
            if (value.getType() == ValueMeta.ValueType.FLOAT) {
                return ValueMeta.createFloat(null, -value.getFloatValue());
            }
        }
        if (expr instanceof ReadIntExpr) {
            if (injectedHashMap.containsKey(expr)) {
                return injectedHashMap.get(expr);
            }

            if (injectedValues == null || injectedValues.isEmpty()) {
                return null;
            }

            if (injectedValues.peek().getType() != ValueMeta.ValueType.INT) {
                return null;
            }

            ValueMeta value = injectedValues.poll();
            injectedHashMap.put(expr, value);
            return value;
        }
        if (expr instanceof ReadFloatExpr) {
            if (injectedHashMap.containsKey(expr)) {
                return injectedHashMap.get(expr);
            }

            if (injectedValues == null || injectedValues.isEmpty()) {
                return null;
            }

            if (injectedValues.peek().getType() != ValueMeta.ValueType.FLOAT) {
                return null;
            }

            ValueMeta value = injectedValues.poll();
            injectedHashMap.put(expr, value);
            return value;
        }

        return null;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Expr

    private AstErrorHandler.ErrorCode checkExpr(Expr expr) {
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

        return AstErrorHandler.ErrorCode.STATIC_CHECKING_ERROR;
    }

    public AstErrorHandler.ErrorCode checkIntConstExpr(IntConstExpr intConstExpr) {
        return AstErrorHandler.ErrorCode.SUCCESS;
    }

    public AstErrorHandler.ErrorCode checkFloatConstExpr(FloatConstExpr floatConstExpr) {
        return AstErrorHandler.ErrorCode.SUCCESS;
    }

    public AstErrorHandler.ErrorCode checkIdentExpr(IdentExpr identExpr) {
        ValueMeta meta = findValue(identExpr.ident);
        if (meta == null) {
            return AstErrorHandler.ErrorCode.UNINITIALIZED_VAR_ERROR;
        }
        return AstErrorHandler.ErrorCode.SUCCESS;
    }

    public AstErrorHandler.ErrorCode checkUnaryMinusExpr(UnaryMinusExpr unaryMinusExpr) {
        return checkExpr(unaryMinusExpr.expr);
    }

    public AstErrorHandler.ErrorCode checkBinExpr(BinaryExpr binExpr) {
        if (binExpr.expr1 == null || binExpr.expr2 == null) {
            return AstErrorHandler.ErrorCode.STATIC_CHECKING_ERROR;
        }
        // if (!checkExpr(binExpr.expr1) || !checkExpr(binExpr.expr2)) {
        ArrayList<Supplier<AstErrorHandler.ErrorCode>> functions = new ArrayList<Supplier<AstErrorHandler.ErrorCode>>();
        functions.add(() -> checkExpr(binExpr.expr1));
        functions.add(() -> checkExpr(binExpr.expr2));
        AstErrorHandler.ErrorCode code = AstErrorHandler.handle(functions);
        if (!AstErrorHandler.isSuccessful(code)) {
            return code;
        }

        ValueMeta.ValueType left = typeHelper.getExprType(binExpr.expr1);
        ValueMeta.ValueType right = typeHelper.getExprType(binExpr.expr2);

        if (left == ValueMeta.ValueType.UNDEFINED || right == ValueMeta.ValueType.UNDEFINED) {
            return AstErrorHandler.ErrorCode.STATIC_CHECKING_ERROR;
        }

        /**
        // TODO:
        if (binExpr.op == BinaryExpr.DIV) {
            if (right == ValueMeta.ValueType.INT && getExprValue(binExpr.expr2).getIntValue() == 0) {
                return AstErrorHandler.ErrorCode.DIV_BY_ZERO_ERROR;
            }
            if (right == ValueMeta.ValueType.FLOAT && getExprValue(binExpr.expr2).getFloatValue() == 0.0) {
                return AstErrorHandler.ErrorCode.DIV_BY_ZERO_ERROR;
            }
        }
        */
        
        if (left == right) {
            return AstErrorHandler.ErrorCode.SUCCESS;
        }

        return AstErrorHandler.ErrorCode.STATIC_CHECKING_ERROR;
    }

    private AstErrorHandler.ErrorCode checkCondExpr(CondExpr condExpr) {
        if (condExpr instanceof CompExpr) {
            return checkCompExpr((CompExpr) condExpr);
        }
        if (condExpr instanceof LogicalExpr) {
            return checkLogicalExpr((LogicalExpr) condExpr);
        }

        return AstErrorHandler.ErrorCode.STATIC_CHECKING_ERROR;
    }

    public AstErrorHandler.ErrorCode checkCompExpr(CompExpr compExpr) {
        if (compExpr.expr1 == null || compExpr.expr2 == null) {
            return AstErrorHandler.ErrorCode.STATIC_CHECKING_ERROR;
        }
        // if (!checkExpr(compExpr.expr1) || !checkExpr(compExpr.expr2)) {
        ArrayList<Supplier<AstErrorHandler.ErrorCode>> functions = new ArrayList<Supplier<AstErrorHandler.ErrorCode>>();
        functions.add(() -> checkExpr(compExpr.expr1));
        functions.add(() -> checkExpr(compExpr.expr2));
        AstErrorHandler.ErrorCode code = AstErrorHandler.handle(functions);
        if (!AstErrorHandler.isSuccessful(code)) {
            return code;
        }

        ValueMeta.ValueType left = typeHelper.getExprType(compExpr.expr1);
        ValueMeta.ValueType right = typeHelper.getExprType(compExpr.expr2);

        if (left == ValueMeta.ValueType.UNDEFINED || right == ValueMeta.ValueType.UNDEFINED) {
            return AstErrorHandler.ErrorCode.STATIC_CHECKING_ERROR;
        }
        
        if (left == right) {
            return AstErrorHandler.ErrorCode.SUCCESS;
        }

        return AstErrorHandler.ErrorCode.STATIC_CHECKING_ERROR;
    }

    public AstErrorHandler.ErrorCode checkLogicalExpr(LogicalExpr logicalExpr) {
        if (logicalExpr.expr1 == null) {
            return AstErrorHandler.ErrorCode.STATIC_CHECKING_ERROR;
        }
        if (logicalExpr.op == LogicalExpr.NOT) {
            return checkCondExpr(logicalExpr.expr1);
        }
        if (logicalExpr.expr2 == null) {
            return AstErrorHandler.ErrorCode.STATIC_CHECKING_ERROR;
        }
        // if (!checkCondExpr(logicalExpr.expr1) || !checkCondExpr(logicalExpr.expr2)) {
        ArrayList<Supplier<AstErrorHandler.ErrorCode>> functions = new ArrayList<Supplier<AstErrorHandler.ErrorCode>>();
        functions.add(() -> checkCondExpr(logicalExpr.expr1));
        functions.add(() -> checkCondExpr(logicalExpr.expr2));
        AstErrorHandler.ErrorCode code = AstErrorHandler.handle(functions);
        if (!AstErrorHandler.isSuccessful(code)) {
            return code;
        }

        // ValueMeta.ValueType leftRight = getLeftRightCondExprType(logicalExpr.expr1, logicalExpr.expr2);
        // if (leftRight == ValueMeta.ValueType.UNDEFINED) {
        //     return false;
        // }

        return AstErrorHandler.ErrorCode.SUCCESS;
    }

    public AstErrorHandler.ErrorCode checkReadIntExpr(ReadIntExpr readIntExpr) {
        if (getExprValue(readIntExpr) == null) {
            return AstErrorHandler.ErrorCode.FAILED_STDIN_READ;
        }
        return AstErrorHandler.ErrorCode.SUCCESS;
    }

    public AstErrorHandler.ErrorCode checkReadFloatExpr(ReadFloatExpr readFloatExpr) {
        if (getExprValue(readFloatExpr) == null) {
            return AstErrorHandler.ErrorCode.FAILED_STDIN_READ;
        }
        return AstErrorHandler.ErrorCode.SUCCESS;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Stmt

    private AstErrorHandler.ErrorCode checkStmt(Stmt stmt) {
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

        return AstErrorHandler.ErrorCode.STATIC_CHECKING_ERROR;
    }

    public AstErrorHandler.ErrorCode checkBlockStmt(BlockStmt blockStmt) {
        pushSymbolTable();
        AstErrorHandler.ErrorCode code = checkUnitList(blockStmt.block);
        return popSymbolTable() ? code : AstErrorHandler.ErrorCode.STATIC_CHECKING_ERROR;
    }

    public AstErrorHandler.ErrorCode checkIfStmt(IfStmt ifStmt) {
        AstErrorHandler.ErrorCode code = checkCondExpr(ifStmt.expr);
        if (!AstErrorHandler.isSuccessful(code)) {
            return code;
        }
        code = checkStmt(ifStmt.thenstmt);
        if (!AstErrorHandler.isSuccessful(code)) {
            return code;
        }
        if (ifStmt.elsestmt != null) {
            code = checkStmt(ifStmt.elsestmt);
            if (!AstErrorHandler.isSuccessful(code)) {
                return code;
            }
        }

        return AstErrorHandler.ErrorCode.SUCCESS;
    }

    public AstErrorHandler.ErrorCode checkWhileStmt(WhileStmt whileStmt) {
        // if (!checkCondExpr(whileStmt.expr)) {
        AstErrorHandler.ErrorCode code = checkCondExpr(whileStmt.expr);
        if (!AstErrorHandler.isSuccessful(code)) {
            return code;
        }
        // if (!checkStmt(whileStmt.body)) {
        code = checkStmt(whileStmt.body);
        if (!AstErrorHandler.isSuccessful(code)) {
            return code;
        }

        return AstErrorHandler.ErrorCode.SUCCESS;
    }

    public AstErrorHandler.ErrorCode checkAssignStmt(AssignStmt assignStmt) {
        // Validate
        ValueMeta meta = findValue(assignStmt.ident);
        if (meta == null) {
            return AstErrorHandler.ErrorCode.UNINITIALIZED_VAR_ERROR;
        }

        // if (!checkExpr(assignStmt.expr)) {
        AstErrorHandler.ErrorCode code = checkExpr(assignStmt.expr);
        if (!AstErrorHandler.isSuccessful(code)) {
            return code;
        }

        ValueMeta.ValueType exprType = typeHelper.getExprType(assignStmt.expr);
        if (meta.getType() != exprType) {
            return AstErrorHandler.ErrorCode.STATIC_CHECKING_ERROR;
        }

        // Update
        // ValueMeta value = getExprValue(assignStmt.expr).copyWithIdent(assignStmt.ident);
        putValue(assignStmt.ident, ValueMeta.createZero(assignStmt.ident, meta.getType()));

        return AstErrorHandler.ErrorCode.SUCCESS;
    }

    public AstErrorHandler.ErrorCode checkPrintStmt(PrintStmt printStmt) {
        // boolean status = checkExpr(printStmt.expr);
        AstErrorHandler.ErrorCode code = checkExpr(printStmt.expr);
        if (!AstErrorHandler.isSuccessful(code)) {
            return code;
        }

        ValueMeta.ValueType type = typeHelper.getExprType(printStmt.expr);
        if (type == ValueMeta.ValueType.INT) {
            System.out.println(getExprValue(printStmt.expr).getIntValue());
        } else if (type == ValueMeta.ValueType.FLOAT) {
            System.out.println(getExprValue(printStmt.expr).getFloatValue());
        } else {
            return AstErrorHandler.ErrorCode.STATIC_CHECKING_ERROR;
        }

        return AstErrorHandler.ErrorCode.SUCCESS;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Unit

    private AstErrorHandler.ErrorCode checkUnit(Unit unit) {
        return unit.checkType(this);
    }

    public AstErrorHandler.ErrorCode checkUnitList(UnitList ul) {
        if (ul == null) {
            return AstErrorHandler.ErrorCode.SUCCESS;
        }

        // if (!checkUnit(ul.unit)) {
        AstErrorHandler.ErrorCode code = checkUnit(ul.unit);
        if (!AstErrorHandler.isSuccessful(code)) {
            return code;
        }

        return checkUnitList(ul.unitList);
    }
}
