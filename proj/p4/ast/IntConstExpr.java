package ast;
import java.io.PrintStream;

public class IntConstExpr extends Expr {
    public final Long ival; 

    public IntConstExpr(Long i, Location loc) {
        super(loc);
        ival = i;
    }

    public void print(PrintStream ps) {
        ps.print(ival);
    }

    public AstErrorHandler.ErrorCode checkType(TypeCheck tc) {
        return tc.checkIntConstExpr(this);
    }

    public RuntimeMeta run(Runtime runtime) {
        return runtime.runIntConstExpr(this);
    }
}
