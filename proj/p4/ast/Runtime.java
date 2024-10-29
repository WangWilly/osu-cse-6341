package ast;

public interface Runtime {
    public RuntimeMeta runDecl(Decl decl);
    public RuntimeMeta runVarDecl(VarDecl varDecl);
    public RuntimeMeta runIntVarDecl(IntVarDecl intVarDecl);
    public RuntimeMeta runFloatVarDecl(FloatVarDecl floatVarDecl);
    public RuntimeMeta runExpr(Expr expr);
    public RuntimeMeta runIntConstExpr(IntConstExpr intConstExpr);
    public RuntimeMeta runFloatConstExpr(FloatConstExpr floatConstExpr);
    public RuntimeMeta runIdentExpr(IdentExpr identExpr);
    public RuntimeMeta runUnaryMinusExpr(UnaryMinusExpr unaryMinusExpr);
    public RuntimeMeta runReadIntExpr(ReadIntExpr readIntExpr);
    public RuntimeMeta runReadFloatExpr(ReadFloatExpr readFloatExpr);
    public RuntimeMeta runBinaryExpr(BinaryExpr binExpr);
    public RuntimeMeta runCondExpr(CondExpr condExpr);
    public RuntimeMeta runCompExpr(CompExpr compExpr);
    public RuntimeMeta runLogicalExpr(LogicalExpr logicalExpr);
    public RuntimeMeta runStmt(Stmt stmt);
    public RuntimeMeta runBlockStmt(BlockStmt blockStmt);
    public RuntimeMeta runIfStmt(IfStmt ifStmt);
    public RuntimeMeta runWhileStmt(WhileStmt whileStmt);
    public RuntimeMeta runAssignStmt(AssignStmt assignStmt);
    public RuntimeMeta runPrintStmt(PrintStmt printStmt);
    public RuntimeMeta runUnit(Unit unit);
    public RuntimeMeta runUnitList(UnitList unitList);
};
