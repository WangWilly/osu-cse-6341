package ast;
import java.io.PrintStream;

public class IntVarDecl extends VarDecl {
    public IntVarDecl(String i, Location loc) {
        super(i,loc);
    }

    public void print(PrintStream ps) {
        ps.print("int " + ident);
    }

    public AstErrorHandler.ErrorCode checkType(TypeCheck tc) {
        return tc.checkIntVarDecl(this);
    }

    public RuntimeMeta run(Runtime runtime) {
        return runtime.runIntVarDecl(this);
    }
}
