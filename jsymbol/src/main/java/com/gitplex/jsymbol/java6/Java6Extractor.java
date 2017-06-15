package com.gitplex.jsymbol.java6;

import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;

import com.gitplex.jsymbol.AbstractSymbolExtractor;
import com.gitplex.jsymbol.ExtractException;
import com.gitplex.jsymbol.java6.Java6Parser.NormalClassDeclarationContext;
import com.gitplex.jsymbol.java6.Java6Parser.TypeDeclarationContext;
import com.gitplex.jsymbol.java6.Symbols.Java6Symbol;
import com.gitplex.jsymbol.java6.Symbols.TypeSymbol;
import com.gitplex.jsymbol.util.Utils;

public class Java6Extractor extends AbstractSymbolExtractor<Java6Symbol>{

	@Override
	public List<Java6Symbol> extract(String fileName, String fileContent) throws ExtractException {
		List<Java6Symbol> symbols= new ArrayList<>();
		
		Java6Lexer lexer = new Java6Lexer(new ANTLRInputStream(fileContent));
		CommonTokenStream stream = new CommonTokenStream(lexer);
		Java6Parser parser = new Java6Parser(stream);
		
		for(TypeDeclarationContext typeDeclaration:parser.compilationUnit().typeDeclaration()){
			NormalClassDeclarationContext normalClassDeclaration = typeDeclaration.classDeclaration().normalClassDeclaration();
			TypeSymbol typesymbol = new TypeSymbol(null, normalClassDeclaration.Identifier().getText(),
					Utils.getTokenPosition(normalClassDeclaration.Identifier().getSymbol()),
					Utils.getTokenPosition(normalClassDeclaration.getStart(), normalClassDeclaration.getStop()));
			
			symbols.add(typesymbol);	
			
		}
		
		return symbols;
	}

	@Override
	public boolean accept(String fileName) {
		// TODO Auto-generated method stub
		return acceptExtensions(fileName, "java");
	}

	@Override
	public int getVersion() {
		// TODO Auto-generated method stub
		return 0;
	}

}
