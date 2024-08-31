package ast;
import java.io.PrintStream;

public class PlusExpr extends Expr {
    public final Expr expr1, expr2;
    public PlusExpr(Expr e1, Expr e2, Location loc) {
        super(loc);
        expr1 = e1; 
        expr2 = e2;
    }
    public void print(PrintStream ps) {
        ps.print("(");
        expr1.print(ps);
        ps.print("+");
        expr2.print(ps);
        ps.print(")");
    }
	public boolean checkType(TypeCheck typeCheck) {
		return typeCheck.checkPlusExpr(this);
	}
}
