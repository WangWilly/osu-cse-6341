package ast;
import java.io.PrintStream;

public class ReadFloatExpr extends Expr {
    public ReadFloatExpr(Location loc) {
        super(loc);
    }

    public void print(PrintStream ps) {
        ps.print("readfloat");
    }

    public boolean checkType(TypeCheck tc) {
        return tc.checkReadFloatExpr(this);
    }
}
