package ast;
import java.io.PrintStream;

public class ReadIntExpr extends Expr {
    public ReadIntExpr(Location loc) {
        super(loc);
    }

    public void print(PrintStream ps) {
        ps.print("readint");
    }

    public AstErrorHandler.ErrorCode checkType(TypeCheck tc) {
        return tc.checkReadIntExpr(this);
    }

    public RuntimeMeta run(Runtime runtime) {
        return runtime.runReadIntExpr(this);
    }
}
