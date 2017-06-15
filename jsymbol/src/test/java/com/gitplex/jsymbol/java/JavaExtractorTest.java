package com.gitplex.jsymbol.java;

import java.util.List;

import org.junit.Test;

import com.github.javaparser.ast.Modifier;
import com.gitplex.jsymbol.DescriptableExtractorTest;
import com.gitplex.jsymbol.java.symbols.CompilationUnitSymbol;
import com.gitplex.jsymbol.java.symbols.FieldSymbol;
import com.gitplex.jsymbol.java.symbols.JavaSymbol;
import com.gitplex.jsymbol.java.symbols.MethodSymbol;
import com.gitplex.jsymbol.java.symbols.TypeSymbol;
import com.gitplex.jsymbol.java.symbols.TypeSymbol.Kind;

public class JavaExtractorTest extends DescriptableExtractorTest<JavaSymbol> {

	@Test
	public void test() {
		verify(readFile("test.outline"), new JavaExtractor().extract(null, readFile("test.source")));
		verify(readFile("composite.outline"), new JavaExtractor().extract(null, readFile("composite.source")));
		verify(readFile("lcount.outline"), new JavaExtractor().extract(null, readFile("lcount.source")));
		verify(readFile("resource.outline"), new JavaExtractor().extract(null, readFile("resource.source")));
	}

	@Override
	protected String describe(List<JavaSymbol> context, JavaSymbol symbol) {
		StringBuilder builder = new StringBuilder();
		if (symbol instanceof CompilationUnitSymbol) {
			CompilationUnitSymbol compilationUnit = (CompilationUnitSymbol) symbol;
			if (compilationUnit.getName() != null)
				builder.append("package ").append(compilationUnit.getName());
		} else if (symbol instanceof TypeSymbol) {
			TypeSymbol typeDef = (TypeSymbol) symbol;
			for (Modifier modifier: typeDef.getModifiers()) 
				builder.append(modifier.name().toLowerCase()).append(" ");

			if (typeDef.getKind() == Kind.ANNOTATION)
				builder.append("@interface").append(" ");
			else
				builder.append(typeDef.getKind().toString().toLowerCase()).append(" ");
			builder.append(typeDef.getName());
			if (typeDef.getTypeParams() != null)
				builder.append(typeDef.getTypeParams());
		} else if (symbol instanceof FieldSymbol) {
			FieldSymbol fieldDef = (FieldSymbol) symbol;
			for (Modifier modifier: fieldDef.getModifiers()) 
				builder.append(modifier.name().toLowerCase()).append(" ");
			if (fieldDef.getType() != null)
				builder.append(fieldDef.getType()).append(" ");
			builder.append(fieldDef.getName());
		} else if (symbol instanceof MethodSymbol) {
			MethodSymbol methodDef = (MethodSymbol) symbol;
			for (Modifier modifier: methodDef.getModifiers()) 
				builder.append(modifier.name().toLowerCase()).append(" ");
			if (methodDef.getTypeParams() != null)
				builder.append(methodDef.getTypeParams()).append(" ");
			if (methodDef.getType() != null)
				builder.append(methodDef.getType()).append(" ");
			builder.append(methodDef.getName());
			if (methodDef.getMethodParams() != null)
				builder.append("(").append(methodDef.getMethodParams()).append(")");
			else
				builder.append("()");
		} else {
			throw new RuntimeException("Unexpected symbol type: " + symbol.getClass());
		}
		
		appendChildren(builder, context, symbol);
		return builder.toString();
	}

}
