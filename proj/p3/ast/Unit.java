package ast;
import java.io.PrintStream;

public abstract class Unit extends ASTNode {
    public Unit(Location loc) {
        super(loc);
    }

    public abstract void print(PrintStream ps, String ident);

    public abstract ErrorHandler.ErrorCode checkType(TypeCheck tc);
}
