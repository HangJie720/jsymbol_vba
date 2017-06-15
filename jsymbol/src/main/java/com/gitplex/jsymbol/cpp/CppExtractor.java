package com.gitplex.jsymbol.cpp;

import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.ANTLRErrorListener;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.Token;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gitplex.jsymbol.AbstractSymbolExtractor;
import com.gitplex.jsymbol.ExtractException;
import com.gitplex.jsymbol.cpp.CppDeclarationParser.DeclarationContext;
import com.gitplex.jsymbol.cpp.symbols.CppSymbol;
import com.gitplex.jsymbol.cpp.symbols.HeaderFileSymbol;
import com.gitplex.jsymbol.cpp.symbols.MacroSymbol;
import com.gitplex.jsymbol.cpp.symbols.SourceFileSymbol;
import com.gitplex.jsymbol.util.Utils;

/**
 * We relies on ANTLR to parse CPP header and source files, and then extract CPP symbols from the parse tree. 
 * Preprocessor directives except macro definition will be dropped at lexer stage, and macro definition tokens will be 
 * put into a separate channel. Since symbol extraction works on a per-file base, so we can not resolve macros. This 
 * can make some part of source file not conforming to the grammar, so we configure ANTLR error listener to ignore 
 * those errors, and also our symbol extraction logic catches and ignores exceptions
 */
public class CppExtractor extends AbstractSymbolExtractor<CppSymbol> {

	private static final Logger logger = LoggerFactory.getLogger(CppExtractor.class);
	
	@Override
	public List<CppSymbol> extract(String fileName, String fileContent) throws ExtractException {
		List<CppSymbol> symbols = new ArrayList<>();

		CppSymbol fileSymbol;
		if (fileName != null) {
			if (fileName.endsWith(".h"))
				fileSymbol = new HeaderFileSymbol(fileName);
			else
				fileSymbol = new SourceFileSymbol(fileName);
		} else {
			fileSymbol = null;
		}

		ANTLRErrorListener errorListener = new BaseErrorListener() {

			@Override
			public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line,
					int charPositionInLine, String msg, RecognitionException e) {
				logger.debug("{}: {}: {}", line, charPositionInLine, msg);
			}
			
		};
		
		CppDeclarationLexer lexer = new CppDeclarationLexer(new ANTLRInputStream(fileContent));
		lexer.removeErrorListeners();
		lexer.addErrorListener(errorListener);
		
		Token token = lexer.nextToken();
		while (token.getType() != Token.EOF) {
			if (token.getChannel() == CppDeclarationLexer.MACRO_DEFINITION_CHANNEL) {
				symbols.add(new MacroSymbol(fileSymbol, token.getText(), fileSymbol instanceof SourceFileSymbol, 
						Utils.getTokenPosition(token)));
			}
			token = lexer.nextToken();
		}
		lexer.reset();

		CommonTokenStream stream = new CommonTokenStream(lexer);
		CppDeclarationParser parser = new CppDeclarationParser(stream);
		parser.removeErrorListeners();
		parser.addErrorListener(errorListener);

		for (DeclarationContext declaration: parser.translationunit().declaration()) {
			// TODO: explore declaration node and its descendant nodes to find out symbol declarations
		}
		
		return symbols;
	}
	
	@Override
	public boolean accept(String fileName) {
		//return acceptExtensions(fileName, "cpp", "cxx", "c++", "cc", "hpp", "hxx", "h++", "hh");
		return false;
	}

	@Override
	public int getVersion() {
		return 0;
	}
	
}
