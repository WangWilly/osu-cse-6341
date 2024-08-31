package ast;
import java.util.HashMap;
import java.util.Map;

////////////////////////////////////////////////////////////////////////////////

public final class TypeCheck {
    private Map < String, ValueMeta > symbolTable;

    public TypeCheck() {
        this.symbolTable = new HashMap < String, ValueMeta > ();
    }

    public void addSymbol(String name, ValueMeta meta) {
        this.symbolTable.put(name, meta);
    }

    public ValueMeta getSymbol(String name) {
        return this.symbolTable.get(name);
    }

    ////////////////////////////////////////////////////////////////////////////
    // Decl

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

    public ValueMeta.ValueType getExprType(Expr expr) {
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
        if (expr instanceof PlusExpr) {
            PlusExpr plusExpr = (PlusExpr) expr;
            ValueMeta.ValueType left = getExprType(plusExpr.expr1);
            ValueMeta.ValueType right = getExprType(plusExpr.expr2);
            if (left == ValueMeta.ValueType.INT && right == ValueMeta.ValueType.INT) {
                return ValueMeta.ValueType.INT;
            }
            if (left == ValueMeta.ValueType.FLOAT && right == ValueMeta.ValueType.FLOAT) {
                return ValueMeta.ValueType.FLOAT;
            }
        }

        return ValueMeta.ValueType.UNDEFINED;
    }

    public ValueMeta getExprValue(Expr expr) {
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
        if (expr instanceof PlusExpr) {
            PlusExpr plusExpr = (PlusExpr) expr;
            ValueMeta left = getExprValue(plusExpr.expr1);
            ValueMeta right = getExprValue(plusExpr.expr2);
            if (left.getType() == ValueMeta.ValueType.INT && right.getType() == ValueMeta.ValueType.INT) {
                return new ValueMeta(null, ValueMeta.ValueType.INT, left.getIntValue() + right.getIntValue());
            }
            if (left.getType() == ValueMeta.ValueType.FLOAT && right.getType() == ValueMeta.ValueType.FLOAT) {
                return new ValueMeta(null, ValueMeta.ValueType.FLOAT, left.getFloatValue() + right.getFloatValue());
            }
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
        if (expr instanceof PlusExpr) {
            return checkPlusExpr((PlusExpr) expr);
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

    public boolean checkPlusExpr(PlusExpr plusExpr) {
        if (plusExpr.expr1 == null || plusExpr.expr2 == null) {
            return false;
        }
        if (checkExpr(plusExpr.expr1) == false || checkExpr(plusExpr.expr2) == false) {
            return false;
        }

        ValueMeta.ValueType left = getExprType(plusExpr.expr1);
        ValueMeta.ValueType right = getExprType(plusExpr.expr2);

        if (left == ValueMeta.ValueType.UNDEFINED || right == ValueMeta.ValueType.UNDEFINED) {
            return false;
        }
        
        if (left == right) {
            return true;
        }

        return false;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Stmt

    public boolean checkAssignStmt(AssignStmt assignStmt) {
        // Validate
        ValueMeta meta = this.symbolTable.get(assignStmt.ident);
        if (meta == null) {
            return false;
        }

        if (checkExpr(assignStmt.expr) == false) {
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
}
