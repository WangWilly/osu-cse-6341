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
    public boolean checkType(TypeCheck typeCheck) {
        return typeCheck.checkIdentExpr(this);
    }
}
