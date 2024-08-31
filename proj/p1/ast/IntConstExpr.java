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
    public boolean checkType(TypeCheck typeCheck) {
        return typeCheck.checkIntConstExpr(this);
    }
}
