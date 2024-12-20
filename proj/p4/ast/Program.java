package ast;
import java.io.PrintStream;

public class Program extends ASTNode {
    public final UnitList unitList;
    public Program(UnitList ul, Location loc) {
        super(loc);
        unitList = ul;
    }

    public void print(PrintStream ps) {
        unitList.print(ps,"");
    }

    public AstErrorHandler.ErrorCode checkType(TypeCheck tc) {
        return unitList.checkType(tc);
    }

    public RuntimeMeta run(Runtime runtime) {
        return unitList.run(runtime);
    }
}
