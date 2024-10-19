package ast;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.Stack;

public final class Runtime {
    /**
    private Stack<Map<String,ValueMeta>> symbolTables;
    private Queue<ValueMeta> injectedValues;
    private HashMap<Expr, ValueMeta> injectedHashMap = new HashMap<Expr, ValueMeta>();

    ////////////////////////////////////////////////////////////////////////////
    // Constructor

    public Runtime(Queue<ValueMeta> injectedValues) {
        this.symbolTables = new Stack<Map<String,ValueMeta>>();
        // global scope
        this.symbolTables.push(new HashMap<String,ValueMeta>());
        this.injectedValues = injectedValues;
    }

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

    private AstErrorHandler.ErrorCode putValue(String ident, ValueMeta value) {
        // if (getValue(ident) != null) {
        //     return AstErrorHandler.ErrorCode.STATIC_CHECKING_ERROR;
        // }
        this.symbolTables.peek().put(ident, value);
        return AstErrorHandler.ErrorCode.SUCCESS;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Runtime

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
    */
}
