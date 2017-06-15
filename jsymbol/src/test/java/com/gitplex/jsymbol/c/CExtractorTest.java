package com.gitplex.jsymbol.c;

import java.util.List;

import org.junit.Test;

import com.gitplex.jsymbol.DescriptableExtractorTest;
import com.gitplex.jsymbol.c.symbols.CSymbol;
import com.gitplex.jsymbol.c.symbols.EnumSymbol;
import com.gitplex.jsymbol.c.symbols.FunctionSymbol;
import com.gitplex.jsymbol.c.symbols.HeaderFileSymbol;
import com.gitplex.jsymbol.c.symbols.MacroSymbol;
import com.gitplex.jsymbol.c.symbols.MemberSymbol;
import com.gitplex.jsymbol.c.symbols.SourceFileSymbol;
import com.gitplex.jsymbol.c.symbols.StructSymbol;
import com.gitplex.jsymbol.c.symbols.TypedefSymbol;
import com.gitplex.jsymbol.c.symbols.UnionSymbol;
import com.gitplex.jsymbol.c.symbols.VariableSymbol;

public class CExtractorTest extends DescriptableExtractorTest<CSymbol> {

	@Test
	public void test() {
		verify(readFile("test.outline"), new CExtractor().extract(null, readFile("test.source")));
	}

	@Override
	protected String describe(List<CSymbol> context, CSymbol symbol) {
		StringBuilder builder = new StringBuilder();
		if (symbol instanceof EnumSymbol) {
			EnumSymbol enumSymbol = (EnumSymbol) symbol;
			builder.append("enum ").append(enumSymbol.getName());
		} else if (symbol instanceof FunctionSymbol) {
			FunctionSymbol functionSymbol = (FunctionSymbol) symbol;
			if (functionSymbol.getType() != null) 
				builder.append(functionSymbol.getType()).append(" ");
			builder.append(functionSymbol.getName());
			builder.append("(");
			if (functionSymbol.getParams() != null) 
				builder.append(functionSymbol.getParams());
			builder.append(")");
			if (functionSymbol.isDefinition())
				builder.append("{...}");
		} else if (symbol instanceof HeaderFileSymbol) {
			HeaderFileSymbol headerFileSymbol = (HeaderFileSymbol) symbol;
			builder.append(headerFileSymbol.getName());
		} else if (symbol instanceof SourceFileSymbol) {
			SourceFileSymbol sourceFileSymbol = (SourceFileSymbol) symbol;
			builder.append(sourceFileSymbol.getName());
		} else if (symbol instanceof MacroSymbol) {
			MacroSymbol macroSymbol = (MacroSymbol) symbol;
			builder.append("macro ").append(macroSymbol.getName());
		} else if (symbol instanceof MemberSymbol) {
			MemberSymbol memberSymbol = (MemberSymbol) symbol;
			if (memberSymbol.getType() != null)
				builder.append(memberSymbol.getType()).append(" ");
			builder.append(memberSymbol.getName());
		} else if (symbol instanceof StructSymbol) {
			StructSymbol structSymbol = (StructSymbol) symbol;
			builder.append("struct ").append(structSymbol.getName());
		} else if (symbol instanceof TypedefSymbol) {
			TypedefSymbol typedefSymbol = (TypedefSymbol) symbol;
			builder.append("typedef ").append(typedefSymbol.getType()).append(" ").append(typedefSymbol.getName());
		} else if (symbol instanceof UnionSymbol) {
			UnionSymbol unionSymbol = (UnionSymbol) symbol;
			builder.append("union ").append(unionSymbol.getName());
		} else if (symbol instanceof VariableSymbol) {
			VariableSymbol variableSymbol = (VariableSymbol) symbol;
			builder.append(variableSymbol.getType()).append(" ").append(variableSymbol.getName());
		} else {
			throw new RuntimeException("Unexpected symbol type: " + symbol.getClass());
		}
		
		appendChildren(builder, context, symbol);
		return builder.toString();
	}

}
