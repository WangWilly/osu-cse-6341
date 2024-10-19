package ast;
import java.util.Stack;
import java.util.Map;
import java.util.HashMap;

public class TypeHelper {
    Stack<Map<String,ValueMeta>> symbolTables;

    ////////////////////////////////////////////////////////////////////////////

    public TypeHelper(Stack<Map<String,ValueMeta>> symbolTables) {
        this.symbolTables = symbolTables;
    }

    ////////////////////////////////////////////////////////////////////////////

    private ValueMeta findValue(String ident) {
        for (int i = symbolTables.size() - 1; i >= 0; i--) {
            ValueMeta value = symbolTables.get(i).get(ident);
            if (value == null || !value.hasValue()) {
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

    ////////////////////////////////////////////////////////////////////////////

    public boolean newScope() {
        symbolTables.push(new HashMap<String,ValueMeta>());
        return true;
    }

    public boolean exitScope() {
        if (symbolTables.size() <= 1) {
            return false;
        }
        symbolTables.pop();
        return true;
    }

    public boolean newIdent(String ident, ValueMeta value) {
        symbolTables.peek().put(ident, value);
        return true;
    }

    public boolean replaceIdent(String ident, ValueMeta value) {
        for (int i = symbolTables.size() - 1; i >= 0; i--) {
            if (!symbolTables.get(i).containsKey(ident)) {
                continue;
            }
            symbolTables.get(i).put(ident, value);
            return true;
        }
        return false;
    }

    ////////////////////////////////////////////////////////////////////////////

    public boolean hasValue(String ident) {
        return findValue(ident) != null;
    }

    public ValueMeta.ValueType getType(String ident) {
        ValueMeta value = findValue(ident);
        if (value == null) {
            return ValueMeta.ValueType.UNDEFINED;
        }
        return value.getType();
    }

    public boolean isLocalDeclared(String ident) {
        return symbolTables.peek().containsKey(ident);
    }

    public boolean isDeclared(String ident) {
        for (int i = this.symbolTables.size() - 1; i >= 0; i--) {
            if (!this.symbolTables.get(i).containsKey(ident)) {
                continue;
            }
            return true;
        }
        return false;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Decl

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
    // Expr

    public ValueMeta.ValueType getLeftRightExprType(Expr expr1, Expr expr2) {
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

    public ValueMeta.ValueType getExprType(Expr expr) {
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

    public ValueMeta.ValueType getLeftRightCondExprType(CondExpr expr1, CondExpr expr2) {
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

    public ValueMeta.ValueType getCondExprType(CondExpr condExpr) {
        if (condExpr instanceof CompExpr) {
            CompExpr compExpr = (CompExpr) condExpr;
            return getLeftRightExprType(compExpr.expr1, compExpr.expr2);
        }
        if (condExpr instanceof LogicalExpr) {
            return ValueMeta.ValueType.BOOL;
        }

        return ValueMeta.ValueType.UNDEFINED;
    }
}
