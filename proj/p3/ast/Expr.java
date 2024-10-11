package ast;
import java.io.PrintStream;

public abstract class Expr extends ASTNode {
    public Expr(Location loc) {
        super(loc);
    }

    public abstract ErrorHandler.ErrorCode checkType(TypeCheck tc);
}
