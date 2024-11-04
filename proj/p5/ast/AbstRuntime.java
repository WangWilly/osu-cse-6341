package ast;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.Stack;

public final class AbstRuntime implements Runtime {
    private HashMap<Expr, RuntimeMeta> exprCache = new HashMap<Expr, RuntimeMeta>();
    private SymbolTableHelper stHelper;

    ////////////////////////////////////////////////////////////////////////////
    // Constructor

    public AbstRuntime(SymbolTableHelper stHelper) {
        this.stHelper = stHelper;

        stHelper.newScope(); // global scope
    }

    ////////////////////////////////////////////////////////////////////////////
    // Decl

    public RuntimeMeta runDecl(Decl decl) {
        runVarDecl(decl.varDecl);

        if (decl.expr == null) {
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
        stHelper.planIdent(varDecl.ident, ValueMeta.ValueType.ABST_INT);
        return RuntimeMeta.createSuccess(null);
    }

    public RuntimeMeta runFloatVarDecl(FloatVarDecl varDecl) {
        if (stHelper.isLocalPlaned(varDecl.ident)) {
            throw new RuntimeException("Variable " + varDecl.ident + " already declared");
        }
        stHelper.planIdent(varDecl.ident, ValueMeta.ValueType.ABST_FLOAT);
        return RuntimeMeta.createSuccess(null);
    }

    ////////////////////////////////////////////////////////////////////////////
    // Expr

    public RuntimeMeta runExpr(Expr expr) {
        RuntimeMeta resValue = null;
        if (expr instanceof IntConstExpr) {
            resValue = runIntConstExpr((IntConstExpr) expr);
        }
        if (expr instanceof FloatConstExpr) {
            resValue = runFloatConstExpr((FloatConstExpr) expr);
        }
        if (expr instanceof IdentExpr) {
            resValue = runIdentExpr((IdentExpr) expr);
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
        return resValue;
    }

    public RuntimeMeta runIntConstExpr(IntConstExpr intConstExpr) {
        return RuntimeMeta.createSuccess(ValueMeta.createAbstInt(null, intConstExpr.ival));
    }

    public RuntimeMeta runFloatConstExpr(FloatConstExpr floatConstExpr) {
        return RuntimeMeta.createSuccess(ValueMeta.createAbstFloat(null, floatConstExpr.fval));
    }

    public RuntimeMeta runIdentExpr(IdentExpr identExpr) {
        ValueMeta val = stHelper.findValue(identExpr.ident);
        if (val == null) {
            // return RuntimeMeta.createError(AstErrorHandler.ErrorCode.UNINITIALIZED_VAR_ERROR);
            throw new RuntimeException("Variable " + identExpr.ident + " not declared");
        }
        return RuntimeMeta.createSuccess(val);
    }

    public RuntimeMeta runUnaryMinusExpr(UnaryMinusExpr unaryMinusExpr) {
        RuntimeMeta resStatus = runExpr(unaryMinusExpr.expr);
        if (resStatus == null) {
            throw new RuntimeException("UnaryMinusExpr is not valid");
        }
        if (!resStatus.isSuccessful()) {
            return resStatus;
        }

        return RuntimeMeta.createSuccess(ValueMeta.neg(resStatus.getValue()));
    }

    public RuntimeMeta runReadIntExpr(ReadIntExpr readIntExpr) {
        if (exprCache.containsKey((Expr)readIntExpr)) {
            return exprCache.get((Expr)readIntExpr);
        }

        ValueMeta value = ValueMeta.createAbstInt(null, ValueMeta.AbstValue.ANY_INT);
        RuntimeMeta resStatus = RuntimeMeta.createSuccess(value);
        exprCache.put((Expr)readIntExpr, resStatus);
        return resStatus;
    }

    public RuntimeMeta runReadFloatExpr(ReadFloatExpr readFloatExpr) {
        if (exprCache.containsKey((Expr)readFloatExpr)) {
            return exprCache.get((Expr)readFloatExpr);
        }

        ValueMeta value = ValueMeta.createAbstFloat(null, ValueMeta.AbstValue.ANY_FLOAT);
        RuntimeMeta resStatus = RuntimeMeta.createSuccess(value);
        exprCache.put((Expr)readFloatExpr, resStatus);
        return resStatus;
    }

    ////////////////////////////////////////////////////////////////////////////

    public RuntimeMeta runBinaryExpr(BinaryExpr binExpr) {
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
        return resStatus;
    }

    private RuntimeMeta runPlus(ValueMeta left, ValueMeta right) {
        return RuntimeMeta.createSuccess(ValueMeta.add(left, right));
    }

    private RuntimeMeta runMinus(ValueMeta left, ValueMeta right) {
        return RuntimeMeta.createSuccess(ValueMeta.sub(left, right));
    }

    private RuntimeMeta runTimes(ValueMeta left, ValueMeta right) {
        return RuntimeMeta.createSuccess(ValueMeta.mul(left, right));
    }

    private RuntimeMeta runDiv(ValueMeta left, ValueMeta right) {
        ValueMeta res = ValueMeta.div(left, right);
        if (res.isIllegal()) {
            return RuntimeMeta.createError(AstErrorHandler.ErrorCode.DIV_BY_ZERO_ERROR);
        }
        return RuntimeMeta.createSuccess(res);
    }

    ////////////////////////////////////////////////////////////////////////////

    public RuntimeMeta runCondExpr(CondExpr condExpr) {
        /**
        if (condExpr instanceof CompExpr) {
            return runCompExpr((CompExpr) condExpr);
        }
        if (condExpr instanceof LogicalExpr) {
            return runLogicalExpr((LogicalExpr) condExpr);
        }
        */

        throw new RuntimeException("CondExpr is not valid");
    }

    public RuntimeMeta runCompExpr(CompExpr compExpr) {
        /**
        RuntimeMeta left = runExpr(compExpr.expr1);
        if (left == null) {
            throw new RuntimeException("CompExpr is not valid");
        }
        if (!left.isSuccessful()) {
            return left;
        }
        RuntimeMeta right = runExpr(compExpr.expr2);
        if (right == null) {
            throw new RuntimeException("CompExpr is not valid");
        }
        if (!right.isSuccessful()) {
            return right;
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
        */

        throw new RuntimeException("CompExpr is not valid");
    }

    public RuntimeMeta runLogicalExpr(LogicalExpr logicalExpr) {
        /**
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
        */

        /** 
         * Short-circuit evaluation
         * 
         * If the first expression is false, the second expression is not evaluated.
         */
        /**
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
        */

        throw new RuntimeException("LogicalExpr is not valid");
    }

    ////////////////////////////////////////////////////////////////////////////
    // Stmt

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
        /**
        RuntimeMeta resStatus = runCondExpr(ifStmt.expr);
        if (resStatus == null) {
            throw new RuntimeException("IfStmt is not valid");
        }
        if (!resStatus.isSuccessful()) {
            return resStatus;
        }
        */

        stHelper.useTwin();
        RuntimeMeta resStatus = runStmt(ifStmt.thenstmt);
        if (resStatus == null) {
            throw new RuntimeException("thenStmt is not valid");
        }
        SymbolTableHelper twin = stHelper.popTwin();
        if (ifStmt.elsestmt != null) {
            stHelper.useTwin();
            resStatus = runStmt(ifStmt.elsestmt);
            if (resStatus == null) {
                throw new RuntimeException("elseStmt is not valid");
            }
            SymbolTableHelper elseTwin = stHelper.popTwin();
            twin.mergeTwin(elseTwin);
            stHelper = twin;
        } else {
            stHelper.mergeTwin(twin);
        }

        return RuntimeMeta.createSuccess(null);
    }

    public RuntimeMeta runWhileStmt(WhileStmt whileStmt) {
        /**
        RuntimeMeta resStatus = runCondExpr(whileStmt.expr);
        if (resStatus == null) {
            throw new RuntimeException("WhileStmt is not valid");
        }
        if (!resStatus.isSuccessful()) {
            return resStatus;
        }
        */

        while (true) {
            stHelper.useTwin();
            RuntimeMeta resStatus = runStmt(whileStmt.body);
            if (resStatus == null) {
                throw new RuntimeException("WhileStmt is not valid");
            }
            if (!resStatus.isSuccessful()) {
                return resStatus;
            }
            SymbolTableHelper twin = stHelper.popTwin();
            if (stHelper.hasIdenticalVal(twin)) {
                break;
            }
            stHelper.mergeTwin(twin);

            /**
            resStatus = runCondExpr(whileStmt.expr);
            if (resStatus == null) {
                throw new RuntimeException("WhileStmt is not valid");
            }
            if (!resStatus.isSuccessful()) {
                return resStatus;
            }
            */
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
        stHelper.envalue(assignStmt.ident, value);
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
    // Unit

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
