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
    private SymbolTableHelper symbolTableHelper;

    ////////////////////////////////////////////////////////////////////////////
    // Constructor

    public TypeCheck(SymbolTableHelper symbolTableHelper) {
        this.symbolTableHelper = symbolTableHelper;
        symbolTableHelper.newScope(); // global scope
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
        ValueMeta.ValueType declType = symbolTableHelper.getDeclType(decl);
        if (declType == ValueMeta.ValueType.UNDEFINED) {
            return AstErrorHandler.ErrorCode.STATIC_CHECKING_ERROR;
        }

        // Update
        if (declType == ValueMeta.ValueType.INT || declType == ValueMeta.ValueType.FLOAT) {
            ValueMeta.ValueType exprType = symbolTableHelper.getExprType(decl.expr);
            if (declType != exprType) {
                return AstErrorHandler.ErrorCode.STATIC_CHECKING_ERROR;
            }
            // the ident is declared in the current scope so it's safe to replace
            symbolTableHelper.concreteIdent(decl.varDecl.ident, declType);
            return AstErrorHandler.ErrorCode.SUCCESS;
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
        if (symbolTableHelper.isLocalPlaned(varDecl.ident)) {
            return AstErrorHandler.ErrorCode.STATIC_CHECKING_ERROR;
        }
        symbolTableHelper.planIdent(varDecl.ident, ValueMeta.ValueType.INT);
        return AstErrorHandler.ErrorCode.SUCCESS;
    }

    public AstErrorHandler.ErrorCode checkFloatVarDecl(FloatVarDecl varDecl) {
        if (symbolTableHelper.isLocalPlaned(varDecl.ident)) {
            return AstErrorHandler.ErrorCode.STATIC_CHECKING_ERROR;
        }
        symbolTableHelper.planIdent(varDecl.ident, ValueMeta.ValueType.FLOAT);
        return AstErrorHandler.ErrorCode.SUCCESS;
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
        // ValueMeta meta = findValue(identExpr.ident);
        // if (meta == null) {
        if (!symbolTableHelper.isConcreted(identExpr.ident)) {
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

        ValueMeta.ValueType left = symbolTableHelper.getExprType(binExpr.expr1);
        ValueMeta.ValueType right = symbolTableHelper.getExprType(binExpr.expr2);

        if (left == ValueMeta.ValueType.UNDEFINED || right == ValueMeta.ValueType.UNDEFINED) {
            return AstErrorHandler.ErrorCode.STATIC_CHECKING_ERROR;
        }
        
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

        ValueMeta.ValueType left = symbolTableHelper.getExprType(compExpr.expr1);
        ValueMeta.ValueType right = symbolTableHelper.getExprType(compExpr.expr2);

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
        return AstErrorHandler.ErrorCode.SUCCESS;
    }

    public AstErrorHandler.ErrorCode checkReadFloatExpr(ReadFloatExpr readFloatExpr) {
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
        if (stmt instanceof WhileStmt) {
            return checkWhileStmt((WhileStmt) stmt);
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
        // pushSymbolTable();
        symbolTableHelper.newScope();
        AstErrorHandler.ErrorCode code = checkUnitList(blockStmt.block);
        return symbolTableHelper.exitScope() ? code : AstErrorHandler.ErrorCode.STATIC_CHECKING_ERROR;
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
        // ValueMeta meta = findValue(assignStmt.ident);
        // if (meta == null) {
        if (!symbolTableHelper.isPlaned(assignStmt.ident)) {
            return AstErrorHandler.ErrorCode.UNINITIALIZED_VAR_ERROR;
        }

        // if (!checkExpr(assignStmt.expr)) {
        AstErrorHandler.ErrorCode code = checkExpr(assignStmt.expr);
        if (!AstErrorHandler.isSuccessful(code)) {
            return code;
        }

        ValueMeta.ValueType identType = symbolTableHelper.getType(assignStmt.ident);
        ValueMeta.ValueType exprType = symbolTableHelper.getExprType(assignStmt.expr);
        if (identType != exprType) {
            return AstErrorHandler.ErrorCode.STATIC_CHECKING_ERROR;
        }

        // Update
        // ValueMeta value = getExprValue(assignStmt.expr).copyWithIdent(assignStmt.ident);
        symbolTableHelper.concreteIdent(assignStmt.ident, identType);

        return AstErrorHandler.ErrorCode.SUCCESS;
    }

    public AstErrorHandler.ErrorCode checkPrintStmt(PrintStmt printStmt) {
        // boolean status = checkExpr(printStmt.expr);
        AstErrorHandler.ErrorCode code = checkExpr(printStmt.expr);
        if (!AstErrorHandler.isSuccessful(code)) {
            return code;
        }

        ValueMeta.ValueType type = symbolTableHelper.getExprType(printStmt.expr);
        /**
        if (type == ValueMeta.ValueType.INT) {
            System.out.println(getExprValue(printStmt.expr).getIntValue());
        } else if (type == ValueMeta.ValueType.FLOAT) {
            System.out.println(getExprValue(printStmt.expr).getFloatValue());
        } else {
            return AstErrorHandler.ErrorCode.STATIC_CHECKING_ERROR;
        }
        */

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
