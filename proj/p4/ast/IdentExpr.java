package ast;
import java.io.PrintStream;

public class IdentExpr extends Expr {
    public final String ident;

    public IdentExpr(String i, Location loc) {
        super(loc);
        ident = i;
    }

    public void print(PrintStream ps) {
        ps.print(ident);
    }

    public AstErrorHandler.ErrorCode checkType(TypeCheck tc) {
        return tc.checkIdentExpr(this);
    }

    public RuntimeMeta run(Runtime runtime) {
        return runtime.runIdentExpr(this);
    }
}
