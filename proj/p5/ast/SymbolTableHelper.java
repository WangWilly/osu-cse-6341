package ast;
import java.util.Stack;
import java.util.Map;
import java.util.HashMap;

public class SymbolTableHelper {
    ////////////////////////////////////////////////////////////////////////////
    // Injection
    private Stack<Map<String,ValueMeta>> stashed;
    private Stack<Map<String,ValueMeta>> symbolTables;

    ////////////////////////////////////////////////////////////////////////////

    public SymbolTableHelper(Stack<Map<String,ValueMeta>> symbolTables) {
        this.symbolTables = symbolTables;
    }

    ////////////////////////////////////////////////////////////////////////////

    public ValueMeta findValue(String ident) {
        for (int i = symbolTables.size() - 1; i >= 0; i--) {
            ValueMeta value = symbolTables.get(i).get(ident);
            if (value == null || !value.hasValue()) {
                continue;
            }
            return value;
        }
        return null;
    }

    public ValueMeta findPlaned(String ident) {
        for (int i = this.symbolTables.size() - 1; i >= 0; i--) {
            if (!this.symbolTables.get(i).containsKey(ident)) {
                continue;
            }
            return this.symbolTables.get(i).get(ident);
        }
        return null;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Ident control (w/o value)

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

    public void planIdent(String ident, ValueMeta.ValueType type) {
        if (isLocalPlaned(ident)) {
            throw new RuntimeException("Variable " + ident + " already declared");
        }
        ValueMeta value = ValueMeta.createNull(ident, type);
        symbolTables.peek().put(ident, value);
    }

    public void concreteIdent(String ident, ValueMeta.ValueType type) {
        ValueMeta value = ValueMeta.createZero(ident, type);
        for (int i = symbolTables.size() - 1; i >= 0; i--) {
            if (!symbolTables.get(i).containsKey(ident)) {
                continue;
            }
            symbolTables.get(i).put(ident, value);
            return;
        }
        throw new RuntimeException("Variable " + ident + " not declared");
    }

    ////////////////////////////////////////////////////////////////////////////

    public boolean isLocalPlaned(String ident) {
        return symbolTables.peek().containsKey(ident);
    }

    public boolean isPlaned(String ident) {
        ValueMeta value = findPlaned(ident);
        return value != null;
    }

    public boolean isConcreted(String ident) {
        return findValue(ident) != null;
    }

    ////////////////////////////////////////////////////////////////////////////

    public ValueMeta.ValueType getType(String ident) {
        ValueMeta value = findPlaned(ident);
        if (value == null) {
            return ValueMeta.ValueType.UNDEFINED;
        }
        return value.getType();
    }

    ////////////////////////////////////////////////////////////////////////////
    // Decl type

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
    // Expr type

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
            ValueMeta meta = findValue(identExpr.ident); // TODO:
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

    ////////////////////////////////////////////////////////////////////////////
    // Envalue

    public void envalue(String ident, ValueMeta value) {
        for (int i = symbolTables.size() - 1; i >= 0; i--) {
            if (!symbolTables.get(i).containsKey(ident)) {
                continue;
            }
            if (symbolTables.get(i).get(ident).getType() != value.getType()) {
                throw new RuntimeException("Variable " + ident + " type mismatch");
            }
            symbolTables.get(i).put(ident, value);
            return;
        }
        throw new RuntimeException("Variable " + ident + " not declared");
    }

    ////////////////////////////////////////////////////////////////////////////

    public void useTwin() {
        Stack<Map<String, ValueMeta>> newSymbolTables = new Stack<Map<String, ValueMeta>>();
        for (Map<String, ValueMeta> symbolTable : symbolTables) {
            newSymbolTables.push(new HashMap<String, ValueMeta>());
            for (Map.Entry<String, ValueMeta> entry : symbolTable.entrySet()) {
                newSymbolTables.peek().put(entry.getKey(), entry.getValue().copy());
            }
        }
        stashed = symbolTables;
        symbolTables = newSymbolTables;
    }

    public SymbolTableHelper popTwin() {
        SymbolTableHelper res = new SymbolTableHelper(symbolTables);
        symbolTables = stashed;
        stashed = null;
        return res;
    }

    public void mergeTwin(SymbolTableHelper twin) {
        for (Map<String, ValueMeta> symbolTable : symbolTables) {
            for (Map.Entry<String, ValueMeta> entry : symbolTable.entrySet()) {
                if (twin.isConcreted(entry.getKey())) {
                    entry.setValue(ValueMeta.merge(entry.getValue(), twin.findValue(entry.getKey())));
                }
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////

    public boolean hasIdenticalVal(SymbolTableHelper twin) {
        for (Map<String, ValueMeta> symbolTable : symbolTables) {
            for (Map.Entry<String, ValueMeta> entry : symbolTable.entrySet()) {
                if (!twin.isConcreted(entry.getKey()) || !ValueMeta.equals(entry.getValue(), twin.findValue(entry.getKey()))) {
                    return false;
                }
            }
        }
        return true;
    }
}
