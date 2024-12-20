package ast;
import java.io.PrintStream;

public class UnaryMinusExpr extends Expr {
    public final Expr expr;

    public UnaryMinusExpr(Expr e, Location loc) {
        super(loc);
        expr = e; 
    }

    public void print(PrintStream ps) {
        ps.print("-(");
        expr.print(ps);
        ps.print(")");
    }

    public AstErrorHandler.ErrorCode checkType(TypeCheck tc) {
        return tc.checkUnaryMinusExpr(this);
    }

    public RuntimeMeta run(Runtime runtime) {
        return runtime.runUnaryMinusExpr(this);
    }
}
