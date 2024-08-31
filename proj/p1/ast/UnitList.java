package ast;
import java.io.PrintStream;

public class UnitList extends ASTNode {
    public final Unit unit;
    public final UnitList unitList; 
    public UnitList(Unit u, UnitList ul, Location loc) {
        super(loc);
        unit = u;
        unitList = ul;
    }
    public UnitList(Unit u, Location loc) { 
        super(loc);
        unit = u;
        unitList = null;
    }
    public void print(PrintStream ps) {
        unit.print(ps);
        ps.println();
        if (unitList != null) unitList.print(ps);
    }
    public boolean checkType(TypeCheck typeCheck) {
        // TODO: (POMV) not to use early return
        // return unit.checkType(typeCheck) && (unitList == null || unitList.checkType(typeCheck));

        if (!unit.checkType(typeCheck)) {
            return false;
        }
        if (unitList == null) {
            return true;
        }
        return unitList.checkType(typeCheck);
    }
}
