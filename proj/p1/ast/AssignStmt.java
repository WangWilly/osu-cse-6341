package ast;
import java.io.PrintStream;

public class AssignStmt extends Stmt {
    public final String ident;
    public final Expr expr;
    public AssignStmt(String i, Expr e, Location loc) {
        super(loc);
        ident = i;
        expr = e;
    }
    public void print(PrintStream ps) { 
        ps.print(ident + " = ");
        expr.print(ps);
        ps.print(";");
    }
    public boolean checkType(TypeCheck typeCheck) {
        return typeCheck.checkAssignStmt(this);
    }
}
