package ast;
import java.io.PrintStream;

public abstract class Unit extends ASTNode {
    public Unit(Location loc) {
        super(loc);
    }
}
