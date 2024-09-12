package ast;
import java.util.HashMap;
import java.util.Map;

////////////////////////////////////////////////////////////////////////////////

public final class TypeCheck {
    private Map < String, ValueMeta > symbolTable;

    public TypeCheck() {
        this.symbolTable = new HashMap < String, ValueMeta > ();
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

    public boolean checkDecl(Decl decl) {
        // Validate
        if (decl.expr == null) {
            return checkVarDecl(decl.varDecl);
        }
        if (!(checkVarDecl(decl.varDecl) && checkExpr(decl.expr))) {
            return false;
        }

        // Check
        ValueMeta.ValueType declType = getDeclType(decl);
        if (declType == ValueMeta.ValueType.UNDEFINED) {
            return false;
        }

        // Update
        if (declType == ValueMeta.ValueType.INT) {
            ValueMeta value = getExprValue(decl.expr).copyWithIdent(decl.varDecl.ident);
            this.symbolTable.put(decl.varDecl.ident, value);
            return true;
        }
        if (declType == ValueMeta.ValueType.FLOAT) {
            ValueMeta value = getExprValue(decl.expr).copyWithIdent(decl.varDecl.ident);
            this.symbolTable.put(decl.varDecl.ident, value);
            return true;
        }

        return false;
    }

    ////////////////////////////////////////////////////////////////////////////
    // VarDecl

    public boolean checkVarDecl(VarDecl varDecl) {
        if (varDecl instanceof IntVarDecl) {
            return checkIntVarDecl((IntVarDecl) varDecl);
        }
        if (varDecl instanceof FloatVarDecl) {
            return checkFloatVarDecl((FloatVarDecl) varDecl);
        }
        return false;
    }

    public boolean checkIntVarDecl(IntVarDecl varDecl) {
        if (this.symbolTable.containsKey(varDecl.ident)) {
            return false;
        }
        this.symbolTable.put(varDecl.ident, new ValueMeta(varDecl.ident, ValueMeta.ValueType.INT));
        return true;
    }

    public boolean checkFloatVarDecl(FloatVarDecl varDecl) {
        if (this.symbolTable.containsKey(varDecl.ident)) {
            return false;
        }
        this.symbolTable.put(varDecl.ident, new ValueMeta(varDecl.ident, ValueMeta.ValueType.FLOAT));
        return true;
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
            ValueMeta meta = this.symbolTable.get(identExpr.ident);
            if (meta == null) {
                return ValueMeta.ValueType.UNDEFINED;
            }
            return meta.getType();
        }
        if (expr instanceof BinaryExpr) {
            BinaryExpr binExpr = (BinaryExpr) expr;
            return getLeftRightExprType(binExpr.expr1, binExpr.expr2);
            /**
            ValueMeta.ValueType left = getExprType(binExpr.expr1);
            ValueMeta.ValueType right = getExprType(binExpr.expr2);
            if (left == ValueMeta.ValueType.INT && right == ValueMeta.ValueType.INT) {
                return ValueMeta.ValueType.INT;
            }
            if (left == ValueMeta.ValueType.FLOAT && right == ValueMeta.ValueType.FLOAT) {
                return ValueMeta.ValueType.FLOAT;
            }
            */
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
            LogicalExpr logicalExpr = (LogicalExpr) condExpr;
            if (logicalExpr.op == LogicalExpr.NOT) {
                return getCondExprType(logicalExpr.expr1);
            }
            return getLeftRightCondExprType(logicalExpr.expr1, logicalExpr.expr2);
        }

        return ValueMeta.ValueType.UNDEFINED;
    }

    private ValueMeta getBinaryExprValue(BinaryExpr binExpr) {
        ValueMeta left = getExprValue(binExpr.expr1);
        ValueMeta right = getExprValue(binExpr.expr2);

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
            return this.symbolTable.get(identExpr.ident);
        }
        if (expr instanceof BinaryExpr) {
            BinaryExpr binExpr = (BinaryExpr) expr;
            return getBinaryExprValue(binExpr);
        }

        return null;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Expr

    public boolean checkExpr(Expr expr) {
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

        return false;
    }

    public boolean checkIntConstExpr(IntConstExpr intConstExpr) {
        return true;
    }

    public boolean checkFloatConstExpr(FloatConstExpr floatConstExpr) {
        return true;
    }

    public boolean checkIdentExpr(IdentExpr identExpr) {
        ValueMeta meta = this.symbolTable.get(identExpr.ident);
        if (meta == null) {
            return false;
        }
        return true;
    }

    public boolean checkUnaryMinusExpr(UnaryMinusExpr unaryMinusExpr) {
        return checkExpr(unaryMinusExpr.expr);
    }

    public boolean checkBinExpr(BinaryExpr binExpr) {
        if (binExpr.expr1 == null || binExpr.expr2 == null) {
            return false;
        }
        if (!checkExpr(binExpr.expr1) || !checkExpr(binExpr.expr2)) {
            return false;
        }

        ValueMeta.ValueType left = getExprType(binExpr.expr1);
        ValueMeta.ValueType right = getExprType(binExpr.expr2);

        if (left == ValueMeta.ValueType.UNDEFINED || right == ValueMeta.ValueType.UNDEFINED) {
            return false;
        }
        
        if (left == right) {
            return true;
        }

        return false;
    }

    private boolean checkCondExpr(CondExpr condExpr) {
        if (condExpr instanceof CompExpr) {
            return checkCompExpr((CompExpr) condExpr);
        }
        if (condExpr instanceof LogicalExpr) {
            return checkLogicalExpr((LogicalExpr) condExpr);
        }

        return false;
    }

    public boolean checkCompExpr(CompExpr compExpr) {
        if (compExpr.expr1 == null || compExpr.expr2 == null) {
            return false;
        }
        if (!checkExpr(compExpr.expr1) || !checkExpr(compExpr.expr2)) {
            return false;
        }

        ValueMeta.ValueType left = getExprType(compExpr.expr1);
        ValueMeta.ValueType right = getExprType(compExpr.expr2);

        if (left == ValueMeta.ValueType.UNDEFINED || right == ValueMeta.ValueType.UNDEFINED) {
            return false;
        }
        
        if (left == right) {
            return true;
        }

        return false;
    }

    public boolean checkLogicalExpr(LogicalExpr logicalExpr) {
        if (logicalExpr.expr1 == null) {
            return false;
        }
        if (logicalExpr.op == LogicalExpr.NOT) {
            return checkCondExpr(logicalExpr.expr1);
        }
        if (logicalExpr.expr2 == null) {
            return false;
        }
        if (!checkCondExpr(logicalExpr.expr1) || !checkCondExpr(logicalExpr.expr2)) {
            return false;
        }

        ValueMeta.ValueType leftRight = getLeftRightCondExprType(logicalExpr.expr1, logicalExpr.expr2);
        if (leftRight == ValueMeta.ValueType.UNDEFINED) {
            return false;
        }

        return true;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Stmt

    private boolean checkStmt(Stmt stmt) {
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

        return false;
    }

    public boolean checkBlockStmt(BlockStmt blockStmt) {
        return checkUnitList(blockStmt.block);
    }

    public boolean checkIfStmt(IfStmt ifStmt) {
        if (!checkCondExpr(ifStmt.expr)) {
            return false;
        }
        if (!checkStmt(ifStmt.thenstmt)) {
            return false;
        }
        if (ifStmt.elsestmt != null && !checkUnit(ifStmt.elsestmt)) {
            return false;
        }

        return true;
    }

    public boolean checkWhileStmt(WhileStmt whileStmt) {
        if (!checkCondExpr(whileStmt.expr)) {
            return false;
        }
        if (!checkStmt(whileStmt.body)) {
            return false;
        }

        return true;
    }

    public boolean checkAssignStmt(AssignStmt assignStmt) {
        // Validate
        ValueMeta meta = this.symbolTable.get(assignStmt.ident);
        if (meta == null) {
            return false;
        }

        if (!checkExpr(assignStmt.expr)) {
            return false;
        }

        ValueMeta.ValueType exprType = getExprType(assignStmt.expr);
        if (meta.getType() != exprType) {
            return false;
        }

        // Update
        ValueMeta value = getExprValue(assignStmt.expr).copyWithIdent(assignStmt.ident);
        this.symbolTable.put(assignStmt.ident, value);

        return true;
    }

    public boolean checkPrintStmt(PrintStmt printStmt) {
        return checkExpr(printStmt.expr);
    }

    ////////////////////////////////////////////////////////////////////////////
    // Unit

    private boolean checkUnit(Unit unit) {
        return unit.checkType(this);
    }

    public boolean checkUnitList(UnitList ul) {
        if (ul == null) {
            return true;
        }

        if (!checkUnit(ul.unit)) {
            return false;
        }

        return checkUnitList(ul.unitList);
    }
}
