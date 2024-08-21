package ast;
import java.io.PrintStream;

public abstract class VarDecl extends ASTNode {
    public final String ident;
    public VarDecl(String i, Location loc) {
	super(loc);
	ident = i;
    }
}
