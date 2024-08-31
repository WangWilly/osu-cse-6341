package ast;
import java.io.PrintStream;

public class Decl extends Unit {
    public final VarDecl varDecl;
    public final Expr expr;
    public Decl(VarDecl d, Location loc) {
        super(loc);
        varDecl = d;
        expr = null;
    }
    public Decl(VarDecl d, Expr e, Location loc) {
        super(loc);
        varDecl = d;
        expr = e;
    }
    public void print(PrintStream ps) { 
        varDecl.print(ps); 
        if (expr != null) {
            ps.print(" = ");
            expr.print(ps);
        }
        ps.print(";");
    }
	public boolean checkType(TypeCheck typeCheck) {
		return typeCheck.checkDecl(this);
	}
}
