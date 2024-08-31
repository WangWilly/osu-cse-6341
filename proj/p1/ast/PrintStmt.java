package ast;
import java.io.PrintStream;

public class PrintStmt extends Stmt {
    public final Expr expr;
    public PrintStmt(Expr e, Location loc) {
        super(loc);
        expr = e;
    }
    public void print(PrintStream ps) { 
        ps.print("print ");
        expr.print(ps);
        ps.print(";");
    }
    public boolean checkType(TypeCheck typeCheck) {
        return typeCheck.checkPrintStmt(this);
    }
}
