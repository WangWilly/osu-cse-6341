package ast;
import java.io.PrintStream;

public abstract class CondExpr extends ASTNode {
    public CondExpr(Location loc) {
        super(loc);
    }

    public abstract boolean checkType(TypeCheck tc);
}
