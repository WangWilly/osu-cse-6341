package parser;
import java.io.Reader;
import java_cup.runtime.*;
import interpreter.Interpreter;
import ast.Program;

public class ParserWrapper {
    public static Program parse(Reader reader, String filename) throws Exception {
        ComplexSymbolFactory csf = new ComplexSymbolFactory();
        Lexer scanner = new Lexer(reader, csf, filename);
        Parser parser = new Parser(scanner, csf);
	return (Program)parser.parse().value;
    }
}
