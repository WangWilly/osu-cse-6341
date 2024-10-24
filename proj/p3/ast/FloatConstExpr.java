package ast;
import java.io.PrintStream;

public class FloatConstExpr extends Expr {
    public final Double fval; 
    public FloatConstExpr(Double f, Location loc) {
        super(loc);
        fval = f;
    }
    public void print(PrintStream ps) {
        ps.print(fval);
    }
    public AstErrorHandler.ErrorCode checkType(TypeCheck tc) {
        return tc.checkFloatConstExpr(this);
    }
    public RuntimeMeta run(Runtime runtime) {
        return runtime.runFloatConstExpr(this);
    }
}
