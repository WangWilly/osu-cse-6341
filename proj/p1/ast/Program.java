package ast;
import java.io.PrintStream;

public class Program extends ASTNode {
    public final UnitList unitList;
    public Program(UnitList ul, Location loc) {
        super(loc);
        unitList = ul;
    }
    public void print(PrintStream ps) {
        unitList.print(ps);
    }
    public boolean checkType(TypeCheck typeCheck) {
        return unitList.checkType(typeCheck);
    }
}
