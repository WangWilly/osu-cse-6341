package ast;
import java.io.PrintStream;

public abstract class CondExpr extends ASTNode {
    public CondExpr(Location loc) {
        super(loc);
    }

    public abstract AstErrorHandler.ErrorCode checkType(TypeCheck tc);

    public abstract RuntimeMeta run(Runtime runtime);
}
