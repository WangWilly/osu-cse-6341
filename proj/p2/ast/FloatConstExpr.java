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
    public boolean checkType(TypeCheck tc) {
        return tc.checkFloatConstExpr(this);
    }
}
