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

    */
}
