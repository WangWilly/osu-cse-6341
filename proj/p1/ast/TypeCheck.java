package ast;
import java.util.HashMap;
import java.util.Map;

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
}
