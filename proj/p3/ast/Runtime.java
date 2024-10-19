package ast;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.Stack;

public final class Runtime {
    private HashMap<Expr, RuntimeMeta> exprCache = new HashMap<Expr, RuntimeMeta>();
    private SymbolTableHelper stHelper;
    private Queue<ValueMeta> injectedValues;

    ////////////////////////////////////////////////////////////////////////////
    // Constructor

    public Runtime(SymbolTableHelper stHelper, Queue<ValueMeta> injectedValues) {
        this.stHelper = stHelper;
        this.injectedValues = injectedValues;

        stHelper.newScope(); // global scope
    }

    ////////////////////////////////////////////////////////////////////////////
    // Decl

    public RuntimeMeta runDecl(Decl decl) {
        if (decl.expr == null) {
            runVarDecl(decl.varDecl);
            return RuntimeMeta.createSuccess(null);
        }

        RuntimeMeta refer = runExpr(decl.expr);
        if (refer == null) {
            throw new RuntimeException("Expression is not valid");
        }
        if (!refer.isSuccessful()) {
            return refer;
        }

        ValueMeta value = refer.getValue().copyWithIdent(decl.varDecl.ident);
        stHelper.envalue(decl.varDecl.ident, value);
        return RuntimeMeta.createSuccess(null);
    }

    public RuntimeMeta runVarDecl(VarDecl varDecl) {
        if (varDecl instanceof IntVarDecl) {
            runIntVarDecl((IntVarDecl) varDecl);
            return RuntimeMeta.createSuccess(null);
        }
        if (varDecl instanceof FloatVarDecl) {
            runFloatVarDecl((FloatVarDecl) varDecl);
            return RuntimeMeta.createSuccess(null);
        }
        throw new RuntimeException("Unknown VarDecl type");
    }

    public RuntimeMeta runIntVarDecl(IntVarDecl varDecl) {
        if (stHelper.isLocalPlaned(varDecl.ident)) {
            throw new RuntimeException("Variable " + varDecl.ident + " already declared");
        }
        stHelper.planIdent(varDecl.ident, ValueMeta.ValueType.INT);
        return RuntimeMeta.createSuccess(null);
    }

    public RuntimeMeta runFloatVarDecl(FloatVarDecl varDecl) {
        if (stHelper.isLocalPlaned(varDecl.ident)) {
            throw new RuntimeException("Variable " + varDecl.ident + " already declared");
        }
        stHelper.planIdent(varDecl.ident, ValueMeta.ValueType.FLOAT);
        return RuntimeMeta.createSuccess(null);
    }

    ////////////////////////////////////////////////////////////////////////////
    // Expr

    public RuntimeMeta runExpr(Expr expr) {
        if (exprCache.containsKey(expr)) {
            return exprCache.get(expr);
        }

        RuntimeMeta resValue = null;
        if (expr instanceof IntConstExpr) {
            IntConstExpr intConstExpr = (IntConstExpr) expr;
            resValue = RuntimeMeta.createSuccess(ValueMeta.createInt(null, intConstExpr.ival));
        }
        if (expr instanceof FloatConstExpr) {
            FloatConstExpr floatConstExpr = (FloatConstExpr) expr;
            resValue = RuntimeMeta.createSuccess(ValueMeta.createFloat(null, floatConstExpr.fval));
        }
        if (expr instanceof IdentExpr) {
            IdentExpr identExpr = (IdentExpr) expr;
            ValueMeta val = stHelper.findValue(identExpr.ident);
            if (val == null) {
                throw new RuntimeException("Variable " + identExpr.ident + " not declared");
            }
            resValue = RuntimeMeta.createSuccess(val);
        }
        if (expr instanceof BinaryExpr) {
            BinaryExpr binExpr = (BinaryExpr) expr;
            resValue = runBinaryExpr(binExpr);
        }
        if (expr instanceof UnaryMinusExpr) {
            resValue = runUnaryMinusExpr((UnaryMinusExpr) expr);
        }
        if (expr instanceof ReadIntExpr) {
            resValue = runReadIntExpr((ReadIntExpr) expr);
        }
        if (expr instanceof ReadFloatExpr) {
            resValue = runReadFloatExpr((ReadFloatExpr) expr);
        }

        if (resValue == null) {
            throw new RuntimeException("Expression is not valid");
        }
        exprCache.put(expr, resValue);
        return resValue;
    }

    public RuntimeMeta runUnaryMinusExpr(UnaryMinusExpr unaryMinusExpr) {
        RuntimeMeta resStatus = runExpr(unaryMinusExpr.expr);
        if (resStatus == null) {
            throw new RuntimeException("UnaryMinusExpr is not valid");
        }
        if (!resStatus.isSuccessful()) {
            return resStatus;
        }

        if (resStatus.getValue().getType() == ValueMeta.ValueType.INT) {
            return RuntimeMeta.createSuccess(ValueMeta.createInt(null, -resStatus.getValue().getIntValue()));
        }
        if (resStatus.getValue().getType() == ValueMeta.ValueType.FLOAT) {
            return RuntimeMeta.createSuccess(ValueMeta.createFloat(null, -resStatus.getValue().getFloatValue()));
        }
        throw new RuntimeException("UnaryMinusExpr is not valid");
    }

    private RuntimeMeta runReadIntExpr(ReadIntExpr readIntExpr) {
        if (injectedValues == null || injectedValues.isEmpty()) {
            return RuntimeMeta.createError(AstErrorHandler.ErrorCode.FAILED_STDIN_READ);
        }

        if (injectedValues.peek().getType() != ValueMeta.ValueType.INT) {
            return RuntimeMeta.createError(AstErrorHandler.ErrorCode.FAILED_STDIN_READ);
        }

        ValueMeta value = injectedValues.poll();
        RuntimeMeta resStatus = RuntimeMeta.createSuccess(value);
        exprCache.put((Expr)readIntExpr, resStatus);
        return resStatus;
    }

    private RuntimeMeta runReadFloatExpr(ReadFloatExpr readFloatExpr) {
        if (injectedValues == null || injectedValues.isEmpty()) {
            return null;
        }

        if (injectedValues.peek().getType() != ValueMeta.ValueType.FLOAT) {
            return null;
        }

        ValueMeta value = injectedValues.poll();
        RuntimeMeta resStatus = RuntimeMeta.createSuccess(value);
        exprCache.put((Expr)readFloatExpr, resStatus);
        return resStatus;
    }

    ////////////////////////////////////////////////////////////////////////////

    public RuntimeMeta runBinaryExpr(BinaryExpr binExpr) {
        if (exprCache.containsKey((Expr)binExpr)) {
            return exprCache.get((Expr)binExpr);
        }

        RuntimeMeta leftStatus = runExpr(binExpr.expr1);
        if (leftStatus == null) {
            throw new RuntimeException("BinaryExpr is not valid");
        }
        if (!leftStatus.isSuccessful()) {
            return leftStatus;
        }
        RuntimeMeta rightStatus = runExpr(binExpr.expr2);
        if (rightStatus == null) {
            throw new RuntimeException("BinaryExpr is not valid");
        }
        if (!rightStatus.isSuccessful()) {
            return rightStatus;
        }

        ValueMeta left = leftStatus.getValue();
        ValueMeta right = rightStatus.getValue();
        RuntimeMeta resStatus = null;
        switch (binExpr.op) {
            case BinaryExpr.PLUS:
                resStatus = runPlus(left, right);
                break;
            case BinaryExpr.MINUS:
                resStatus = runMinus(left, right);
                break;
            case BinaryExpr.TIMES:
                resStatus = runTimes(left, right);
                break;
            case BinaryExpr.DIV:
                resStatus = runDiv(left, right);
                break;
        }

        if (resStatus == null) {
            throw new RuntimeException("BinaryExpr is not valid");
        }
        exprCache.put(binExpr, resStatus);
        return resStatus;
    }

    private RuntimeMeta runPlus(ValueMeta left, ValueMeta right) {
        if (left.getType() == ValueMeta.ValueType.INT && right.getType() == ValueMeta.ValueType.INT) {
            return RuntimeMeta.createSuccess(ValueMeta.createInt(null, left.getIntValue() + right.getIntValue()));
        }
        if (left.getType() == ValueMeta.ValueType.FLOAT && right.getType() == ValueMeta.ValueType.FLOAT) {
            return RuntimeMeta.createSuccess(ValueMeta.createFloat(null, left.getFloatValue() + right.getFloatValue()));
        }
        throw new RuntimeException("Plus is not valid");
    }

    private RuntimeMeta runMinus(ValueMeta left, ValueMeta right) {
        if (left.getType() == ValueMeta.ValueType.INT && right.getType() == ValueMeta.ValueType.INT) {
            return RuntimeMeta.createSuccess(ValueMeta.createInt(null, left.getIntValue() - right.getIntValue()));
        }
        if (left.getType() == ValueMeta.ValueType.FLOAT && right.getType() == ValueMeta.ValueType.FLOAT) {
            return RuntimeMeta.createSuccess(ValueMeta.createFloat(null, left.getFloatValue() - right.getFloatValue()));
        }
        throw new RuntimeException("Minus is not valid");
    }

    private RuntimeMeta runTimes(ValueMeta left, ValueMeta right) {
        if (left.getType() == ValueMeta.ValueType.INT && right.getType() == ValueMeta.ValueType.INT) {
            return RuntimeMeta.createSuccess(ValueMeta.createInt(null, left.getIntValue() * right.getIntValue()));
        }
        if (left.getType() == ValueMeta.ValueType.FLOAT && right.getType() == ValueMeta.ValueType.FLOAT) {
            return RuntimeMeta.createSuccess(ValueMeta.createFloat(null, left.getFloatValue() * right.getFloatValue()));
        }
        throw new RuntimeException("Times is not valid");
    }

    private RuntimeMeta runDiv(ValueMeta left, ValueMeta right) {
        if (left.getType() == ValueMeta.ValueType.INT && right.getType() == ValueMeta.ValueType.INT) {
            if (right.getIntValue() == 0) {
                RuntimeMeta.createError(AstErrorHandler.ErrorCode.DIV_BY_ZERO_ERROR);
            }
            return RuntimeMeta.createSuccess(ValueMeta.createInt(null, left.getIntValue() / right.getIntValue()));
        }
        if (left.getType() == ValueMeta.ValueType.FLOAT && right.getType() == ValueMeta.ValueType.FLOAT) {
            if (right.getFloatValue() == 0.0) {
                RuntimeMeta.createError(AstErrorHandler.ErrorCode.DIV_BY_ZERO_ERROR);
            }
            return RuntimeMeta.createSuccess(ValueMeta.createFloat(null, left.getFloatValue() / right.getFloatValue()));
        }
        throw new RuntimeException("Div is not valid");
    }

    ////////////////////////////////////////////////////////////////////////////

    public RuntimeMeta runCondExpr(CondExpr condExpr) {
        if (condExpr instanceof CompExpr) {
            return runCompExpr((CompExpr) condExpr);
        }
        if (condExpr instanceof LogicalExpr) {
            return runLogicalExpr((LogicalExpr) condExpr);
        }

        throw new RuntimeException("CondExpr is not valid");
    }

    public RuntimeMeta runCompExpr(CompExpr compExpr) {
        RuntimeMeta left = runExpr(compExpr.expr1);
        RuntimeMeta right = runExpr(compExpr.expr2);
        if (left == null || right == null) {
            throw new RuntimeException("CompExpr is not valid");
        }

        if (compExpr.op == CompExpr.EQ) {
            return RuntimeMeta.createSuccess(ValueMeta.createBool(null, ValueMeta.equals(left.getValue(), right.getValue())));
        }
        if (compExpr.op == CompExpr.NE) {
            return RuntimeMeta.createSuccess(ValueMeta.createBool(null, ValueMeta.notEquals(left.getValue(), right.getValue())));
        }
        if (compExpr.op == CompExpr.LT) {
            return RuntimeMeta.createSuccess(ValueMeta.createBool(null, ValueMeta.lessThan(left.getValue(), right.getValue())));
        }
        if (compExpr.op == CompExpr.GT) {
            return RuntimeMeta.createSuccess(ValueMeta.createBool(null, ValueMeta.greaterThan(left.getValue(), right.getValue())));
        }
        if (compExpr.op == CompExpr.LE) {
            return RuntimeMeta.createSuccess(ValueMeta.createBool(null, ValueMeta.lessThanOrEqual(left.getValue(), right.getValue())));
        }
        if (compExpr.op == CompExpr.GE) {
            return RuntimeMeta.createSuccess(ValueMeta.createBool(null, ValueMeta.greaterThanOrEqual(left.getValue(), right.getValue())));
        }

        throw new RuntimeException("CompExpr is not valid");
    }

    public RuntimeMeta runLogicalExpr(LogicalExpr logicalExpr) {
        if (logicalExpr.op == LogicalExpr.NOT) {
            RuntimeMeta resStatus = runCondExpr(logicalExpr.expr1);
            if (resStatus == null) {
                throw new RuntimeException("LogicalExpr is not valid");
            }
            if (!resStatus.isSuccessful()) {
                return resStatus;
            }

            return RuntimeMeta.createSuccess(ValueMeta.createBool(null, !resStatus.getValue().getBoolValue().booleanValue()));
        }

        if (logicalExpr.op == LogicalExpr.AND) {
            RuntimeMeta left = runCondExpr(logicalExpr.expr1);
            if (left == null) {
                throw new RuntimeException("LogicalExpr is not valid");
            }
            if (!left.isSuccessful()) {
                return left;
            }
            if (!left.getValue().getBoolValue().booleanValue()) {
                return RuntimeMeta.createSuccess(ValueMeta.createBool(null, false));
            }

            RuntimeMeta right = runCondExpr(logicalExpr.expr2);
            if (right == null) {
                throw new RuntimeException("LogicalExpr is not valid");
            }
            if (!right.isSuccessful()) {
                return right;
            }

            if (!right.getValue().getBoolValue().booleanValue()) {
                return RuntimeMeta.createSuccess(ValueMeta.createBool(null, false));
            }

            return RuntimeMeta.createSuccess(ValueMeta.createBool(null, true));
        }

        if (logicalExpr.op == LogicalExpr.OR) {
            RuntimeMeta left = runCondExpr(logicalExpr.expr1);
            if (left == null) {
                throw new RuntimeException("LogicalExpr is not valid");
            }
            if (!left.isSuccessful()) {
                return left;
            }

            if (left.getValue().getBoolValue().booleanValue()) {
                return RuntimeMeta.createSuccess(ValueMeta.createBool(null, true));
            }

            RuntimeMeta right = runCondExpr(logicalExpr.expr2);
            if (right == null) {
                throw new RuntimeException("LogicalExpr is not valid");
            }
            if (!right.isSuccessful()) {
                return right;
            }

            if (right.getValue().getBoolValue().booleanValue()) {
                return RuntimeMeta.createSuccess(ValueMeta.createBool(null, true));
            }

            return RuntimeMeta.createSuccess(ValueMeta.createBool(null, false));
        }

        throw new RuntimeException("LogicalExpr is not valid");
    }

    ////////////////////////////////////////////////////////////////////////////

    public RuntimeMeta runStmt(Stmt stmt) {
        if (stmt instanceof BlockStmt) {
            return runBlockStmt((BlockStmt) stmt);
        }
        if (stmt instanceof IfStmt) {
            return runIfStmt((IfStmt) stmt);
        }
        if (stmt instanceof WhileStmt) {
            return runWhileStmt((WhileStmt) stmt);
        }
        if (stmt instanceof AssignStmt) {
            return runAssignStmt((AssignStmt) stmt);
        }
        if (stmt instanceof PrintStmt) {
            return runPrintStmt((PrintStmt) stmt);
        }

        throw new RuntimeException("Unknown Stmt type");
    }

    public RuntimeMeta runBlockStmt(BlockStmt blockStmt) {
        stHelper.newScope();
        RuntimeMeta resStatus = runUnitList(blockStmt.block);
        boolean exitOk = stHelper.exitScope();
        if (!exitOk) {
            throw new RuntimeException("BlockStmt is not valid");
        }
        return resStatus;
    }

    public RuntimeMeta runIfStmt(IfStmt ifStmt) {
        RuntimeMeta resStatus = runCondExpr(ifStmt.expr);
        if (resStatus == null) {
            throw new RuntimeException("IfStmt is not valid");
        }
        if (!resStatus.isSuccessful()) {
            return resStatus;
        }

        if (resStatus.getValue().getBoolValue().booleanValue()) {
            return runStmt(ifStmt.thenstmt);
        }
        if (ifStmt.elsestmt != null && !resStatus.getValue().getBoolValue().booleanValue()) {
            return runStmt(ifStmt.elsestmt);
        }
        return RuntimeMeta.createSuccess(null);
    }

    public RuntimeMeta runWhileStmt(WhileStmt whileStmt) {
        RuntimeMeta resStatus = runCondExpr(whileStmt.expr);
        if (resStatus == null) {
            throw new RuntimeException("WhileStmt is not valid");
        }
        if (!resStatus.isSuccessful()) {
            return resStatus;
        }

        while (resStatus.getValue().getBoolValue().booleanValue()) {
            resStatus = runStmt(whileStmt.body);
            if (resStatus == null) {
                throw new RuntimeException("WhileStmt is not valid");
            }
            if (!resStatus.isSuccessful()) {
                return resStatus;
            }

            resStatus = runCondExpr(whileStmt.expr);
            if (resStatus == null) {
                throw new RuntimeException("WhileStmt is not valid");
            }
            if (!resStatus.isSuccessful()) {
                return resStatus;
            }
        }

        return RuntimeMeta.createSuccess(null);
    }

    public RuntimeMeta runAssignStmt(AssignStmt assignStmt) {
        RuntimeMeta resStatus = runExpr(assignStmt.expr);
        if (resStatus == null) {
            throw new RuntimeException("AssignStmt is not valid");
        }
        if (!resStatus.isSuccessful()) {
            return resStatus;
        }

        ValueMeta value = resStatus.getValue().copyWithIdent(assignStmt.ident);
        stHelper.concreteIdent(assignStmt.ident, value.getType());
        return RuntimeMeta.createSuccess(null);
    }

    public RuntimeMeta runPrintStmt(PrintStmt printStmt) {
        RuntimeMeta resStatus = runExpr(printStmt.expr);
        if (resStatus == null) {
            throw new RuntimeException("PrintStmt is not valid");
        }
        if (!resStatus.isSuccessful()) {
            return resStatus;
        }

        resStatus.getValue().print();
        return RuntimeMeta.createSuccess(null);
    }

    ////////////////////////////////////////////////////////////////////////////

    public RuntimeMeta runUnit(Unit unit) {
        return unit.run(this);
    }

    public RuntimeMeta runUnitList(UnitList unitList) {
        if (unitList == null) {
            return RuntimeMeta.createSuccess(null);
        }

        RuntimeMeta resStatus = runUnit(unitList.unit);
        if (resStatus == null) {
            throw new RuntimeException("UnitList is not valid");
        }
        if (!resStatus.isSuccessful()) {
            return resStatus;
        }

        return runUnitList(unitList.unitList);
    }
}
