package ast;
import java.io.PrintStream;

public class WhileStmt extends Stmt {
    public final CondExpr expr;
    public final Stmt body;

    public WhileStmt(CondExpr e, Stmt s, Location loc) {
        super(loc);
        expr = e;
        body = s;
    }

    public void print(PrintStream ps, String indent) { 
        ps.print(indent + "while (");
        expr.print(ps);
        ps.print(")\n");
        body.print(ps, indent + "  ");
    }

    public void print(PrintStream ps) {     
        print(ps,"");
    }

    public AstErrorHandler.ErrorCode checkType(TypeCheck tc) {
        return tc.checkWhileStmt(this);
    }
}
