package ast;
import java.io.PrintStream;

public class PrintStmt extends Stmt {
    public final Expr expr;

    public PrintStmt(Expr e, Location loc) {
        super(loc);
        expr = e;
    }

    public void print(PrintStream ps, String indent) { 
        ps.print(indent + "print ");
        expr.print(ps);
        ps.print(";");
    }

    public void print(PrintStream ps) { 
        print(ps,"");
    }

    public AstErrorHandler.ErrorCode checkType(TypeCheck tc) {
        return tc.checkPrintStmt(this);
    }

    public RuntimeMeta run(Runtime runtime) {
        return runtime.runPrintStmt(this);
    }
}
