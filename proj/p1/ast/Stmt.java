package ast;
import java.io.PrintStream;

public abstract class Stmt extends Unit {
    public Stmt(Location loc) {
        super(loc);
    }
}
