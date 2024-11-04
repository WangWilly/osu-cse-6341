package ast;
import java.io.PrintStream;

public class IfStmt extends Stmt {
    public final CondExpr expr; 
    public final Stmt thenstmt, elsestmt;

    public IfStmt(CondExpr e, Stmt s, Location loc) {
        super(loc);
        expr = e;
        thenstmt = s;
        elsestmt = null;
    }

    public IfStmt(CondExpr e, Stmt s1, Stmt s2, Location loc) {
        super(loc);
        expr = e;
        thenstmt = s1;
        elsestmt = s2;
    }

    public void print(PrintStream ps, String indent) { 
        ps.print(indent + "if (");
        expr.print(ps);
        ps.print(")\n");
        thenstmt.print(ps, indent+"  ");
        if (elsestmt != null) {
            ps.print("\n" + indent + "else\n");            
            elsestmt.print(ps, indent + "  ");
        }
    }

    public void print(PrintStream ps) {     
        print(ps,"");
    }

	public AstErrorHandler.ErrorCode checkType(TypeCheck tc) {
		return tc.checkIfStmt(this);
	}

    public RuntimeMeta run(Runtime runtime) {
        return runtime.runIfStmt(this);
    }
}
