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

    public void print(PrintStream ps, String indent) {
        ps.print(indent + ident + " = ");
        expr.print(ps);
        ps.print(";");
    }

    public void print(PrintStream ps) {    
        print(ps,"");
    }

    public AstErrorHandler.ErrorCode checkType(TypeCheck tc) {
        return tc.checkAssignStmt(this);
    }

    public RuntimeMeta run(Runtime runtime) {
        return runtime.runAssignStmt(this);
    }
}
