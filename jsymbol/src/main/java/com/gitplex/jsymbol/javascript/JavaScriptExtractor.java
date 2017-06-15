package com.gitplex.jsymbol.javascript;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Nullable;

import org.apache.commons.io.Charsets;
import org.apache.commons.lang.StringUtils;
import org.sonar.javascript.parser.JavaScriptParser;
import org.sonar.plugins.javascript.api.tree.ScriptTree;
import org.sonar.plugins.javascript.api.tree.Tree;
import org.sonar.plugins.javascript.api.tree.Tree.Kind;
import org.sonar.plugins.javascript.api.tree.declaration.AccessorMethodDeclarationTree;
import org.sonar.plugins.javascript.api.tree.declaration.BindingElementTree;
import org.sonar.plugins.javascript.api.tree.declaration.ExportClauseTree;
import org.sonar.plugins.javascript.api.tree.declaration.ExportDefaultBinding;
import org.sonar.plugins.javascript.api.tree.declaration.FunctionDeclarationTree;
import org.sonar.plugins.javascript.api.tree.declaration.FunctionTree;
import org.sonar.plugins.javascript.api.tree.declaration.ImportClauseTree;
import org.sonar.plugins.javascript.api.tree.declaration.ImportDeclarationTree;
import org.sonar.plugins.javascript.api.tree.declaration.InitializedBindingElementTree;
import org.sonar.plugins.javascript.api.tree.declaration.MethodDeclarationTree;
import org.sonar.plugins.javascript.api.tree.declaration.NamedExportDeclarationTree;
import org.sonar.plugins.javascript.api.tree.declaration.SpecifierListTree;
import org.sonar.plugins.javascript.api.tree.declaration.SpecifierTree;
import org.sonar.plugins.javascript.api.tree.expression.ArrowFunctionTree;
import org.sonar.plugins.javascript.api.tree.expression.AssignmentExpressionTree;
import org.sonar.plugins.javascript.api.tree.expression.CallExpressionTree;
import org.sonar.plugins.javascript.api.tree.expression.ClassTree;
import org.sonar.plugins.javascript.api.tree.expression.DotMemberExpressionTree;
import org.sonar.plugins.javascript.api.tree.expression.ExpressionTree;
import org.sonar.plugins.javascript.api.tree.expression.FunctionExpressionTree;
import org.sonar.plugins.javascript.api.tree.expression.IdentifierTree;
import org.sonar.plugins.javascript.api.tree.expression.LiteralTree;
import org.sonar.plugins.javascript.api.tree.expression.NewExpressionTree;
import org.sonar.plugins.javascript.api.tree.expression.ObjectLiteralTree;
import org.sonar.plugins.javascript.api.tree.expression.PairPropertyTree;
import org.sonar.plugins.javascript.api.tree.expression.ParenthesisedExpressionTree;
import org.sonar.plugins.javascript.api.tree.lexical.SyntaxToken;
import org.sonar.plugins.javascript.api.tree.statement.BlockTree;
import org.sonar.plugins.javascript.api.tree.statement.ExpressionStatementTree;
import org.sonar.plugins.javascript.api.tree.statement.StatementTree;
import org.sonar.plugins.javascript.api.tree.statement.VariableDeclarationTree;
import org.sonar.plugins.javascript.api.tree.statement.VariableStatementTree;

import com.gitplex.jsymbol.AbstractSymbolExtractor;
import com.gitplex.jsymbol.ExtractException;
import com.gitplex.jsymbol.TokenPosition;
import com.gitplex.jsymbol.javascript.symbols.ClassSymbol;
import com.gitplex.jsymbol.javascript.symbols.FunctionSymbol;
import com.gitplex.jsymbol.javascript.symbols.JavaScriptSymbol;
import com.gitplex.jsymbol.javascript.symbols.MethodAccess;
import com.gitplex.jsymbol.javascript.symbols.MethodSymbol;
import com.gitplex.jsymbol.javascript.symbols.ModuleAccess;
import com.gitplex.jsymbol.javascript.symbols.ObjectSymbol;
import com.gitplex.jsymbol.javascript.symbols.ReferenceSymbol;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.sonar.sslr.api.RecognitionException;

public class JavaScriptExtractor extends AbstractSymbolExtractor<JavaScriptSymbol> {

	@Override
	public List<JavaScriptSymbol> extract(String fileName, String fileContent) throws ExtractException {
		List<JavaScriptSymbol> symbols = new ArrayList<>();
		try {
			Tree tree = new JavaScriptParser(Charsets.UTF_8).parse(fileContent);
			processTree(tree, null, symbols);
		} catch (RecognitionException e) {
			throw new ExtractException("Error parsing javascript", e);
		}

		// process CommonJS exports
		for (JavaScriptSymbol symbol: symbols) {
			if ("exports".equals(symbol.getName())) {
				if (symbol instanceof ReferenceSymbol) {
					ReferenceSymbol reference = (ReferenceSymbol) symbol;
					JavaScriptSymbol referenced = getSymbolInHierarchy(symbols, reference.getReferencedParent(), 
							reference.getReferencedPath().get(0));
					if (referenced != null && reference.getReferencedPath().size()>1) {
						List<String> childPath = reference.getReferencedPath().subList(1, 
								reference.getReferencedPath().size());
						referenced = getChild(symbols, referenced, childPath.toArray(new String[0]));
					}
					if (referenced != null)
						referenced.setModuleAccess(ModuleAccess.EXPORT);
				} 
				
				for (JavaScriptSymbol child: getChildren(symbols, symbol)) {
					if (child.isProperty())
						child.setModuleAccess(ModuleAccess.EXPORT);
				}
			}
		}

		/*
		 * Remove all symbols not contributing substantial information to outline
		 */
		for (Iterator<JavaScriptSymbol> it = symbols.iterator(); it.hasNext();) {
			JavaScriptSymbol symbol = it.next();
			if (symbol.getName() == null && getChildren(symbols, symbol).isEmpty() 
					|| symbol.isLocal() 
							&& getChildren(symbols, symbol).isEmpty() 
							&& (symbol instanceof ObjectSymbol || symbol instanceof ReferenceSymbol)
							&& symbol.getModuleAccess() == ModuleAccess.NORMAL) {
				it.remove();
			} 
		}
		return symbols;
	}
	
	private List<JavaScriptSymbol> getChildren(List<JavaScriptSymbol> symbols, @Nullable JavaScriptSymbol parent) {
		List<JavaScriptSymbol> children = new ArrayList<>();
		for (JavaScriptSymbol symbol: symbols) {
			if (symbol.getParent() == parent)
				children.add(symbol);
		}
		return children;
	}
	
	@Nullable
	private JavaScriptSymbol getChild(List<JavaScriptSymbol> symbols, 
			@Nullable JavaScriptSymbol parent, String... childPath) {
		Preconditions.checkArgument(childPath.length != 0);
		String childName = childPath[0];
		JavaScriptSymbol child = null;
		for (JavaScriptSymbol symbol: symbols) {
			if (symbol.getParent() == parent && childName.equals(symbol.getName())) {
				child = symbol;
				break;
			}
		}
		if (child != null && childPath.length > 1) {
			return getChild(symbols, child, Arrays.copyOfRange(childPath, 1, childPath.length));
		} else {
			return child;
		}
	}
	
	private void processTree(Tree tree, JavaScriptSymbol parent, List<JavaScriptSymbol> symbols) {
		if (tree instanceof ScriptTree) {
			ScriptTree script = (ScriptTree) tree;
			if (script.items() != null && script.items().items() != null) {
				for (Tree item: script.items().items()) {
					processTree(item, parent, symbols);
				}
			}
		} else if (tree instanceof StatementTree) {
			processStatement((StatementTree)tree, parent, symbols);
		} else if (tree instanceof NamedExportDeclarationTree) {
			processNamedExportDeclaration((NamedExportDeclarationTree)tree, symbols);
		} else if (tree instanceof ImportDeclarationTree) {
            processImportDeclaration((ImportDeclarationTree)tree, symbols);
        }
    }
	
	private void processMethodDeclaration(MethodDeclarationTree methodDeclaration, JavaScriptSymbol parent, 
			List<JavaScriptSymbol> symbols) {
		Tree name = methodDeclaration.name(); 
		SyntaxToken nameToken = getNameToken(name);
		if (nameToken != null) {
			MethodAccess methodAccess = MethodAccess.NORMAL;
			if (methodDeclaration instanceof AccessorMethodDeclarationTree) {
				AccessorMethodDeclarationTree accessorMethodDeclaration = (AccessorMethodDeclarationTree) methodDeclaration;
				if (accessorMethodDeclaration.accessorToken().text().equals("get"))
					methodAccess = MethodAccess.GET;
				else
					methodAccess = MethodAccess.SET;
			}
			MethodSymbol methodSymbol = new MethodSymbol();
			methodSymbol.setMethodAccess(methodAccess);
			methodSymbol.setProperty(true);
			methodSymbol.setParent(parent);
			methodSymbol.setName(getName(nameToken));
			methodSymbol.setPosition(getPosition(nameToken));
			methodSymbol.setScope(getPosition(methodDeclaration.body().openCurlyBrace(), 
					methodDeclaration.body().closeCurlyBrace()));
			methodSymbol.setParameters(processParameters(methodDeclaration.parameterList(), methodSymbol, symbols));
			symbols.add(methodSymbol);
			processBlock(methodDeclaration.body(), methodSymbol, symbols);
		}
	}
	
	private String processParameters(List<Tree> parameters, JavaScriptSymbol parent, List<JavaScriptSymbol> symbols) {
		List<String> paramDescriptions = new ArrayList<>();
		for (Tree tree: parameters) {
			List<IdentifierTree> identifiers = new ArrayList<>();
			if (tree instanceof IdentifierTree) {
				IdentifierTree identifier = (IdentifierTree) tree;
				identifiers.add(identifier);
				paramDescriptions.add(getName(identifier));
			} else if (tree instanceof BindingElementTree) {
				BindingElementTree bindingElement = (BindingElementTree) tree;
				identifiers.addAll(bindingElement.bindingIdentifiers());
				if (bindingElement.bindingIdentifiers().size() == 1)
					paramDescriptions.add(bindingElement.bindingIdentifiers().get(0).name());
				else if (bindingElement.bindingIdentifiers().size()>1)
					paramDescriptions.add(bindingElement.bindingIdentifiers().toString());
			}
			for (IdentifierTree identifier: identifiers) {
				ObjectSymbol symbol = new ObjectSymbol();
				symbol.setName(getName(identifier));
				symbol.setParent(parent);
				symbol.setPosition(getPosition(identifier));
				symbols.add(symbol);
			}
		}
		return "(" + Joiner.on(", ").join(paramDescriptions) + ")";
	}

	/*
	 * process ES6 import statement
	 */
	private void processImportDeclaration(ImportDeclarationTree importDeclaration, List<JavaScriptSymbol> symbols) {
		if (importDeclaration.importClause() instanceof ImportClauseTree) {
			ImportClauseTree importClause = (ImportClauseTree) importDeclaration.importClause();
			if (importClause.namedImport() instanceof SpecifierListTree) {
				SpecifierListTree specifierList = (SpecifierListTree) importClause.namedImport();
				for (SpecifierTree specifier: specifierList.specifiers()) {
					processSpecifierTree(specifier, symbols, ModuleAccess.IMPORT);
				}
			} else if (importClause.namedImport() instanceof SpecifierTree) {
				processSpecifierTree((SpecifierTree)importClause.namedImport(), symbols, ModuleAccess.IMPORT);
			}
			IdentifierTree defaultImport = importClause.defaultImport();
			if (defaultImport != null) {
			    SyntaxToken token = defaultImport.identifierToken();
			    ObjectSymbol symbol = new ObjectSymbol();
			    symbol.setName(getName(token));
	            symbol.setModuleAccess(ModuleAccess.IMPORT);
			    symbol.setPosition(getPosition(token));
			    symbols.add(symbol);
			}
		}
	}

    private void processSpecifierTree(SpecifierTree specifier, List<JavaScriptSymbol> symbols, 
    		ModuleAccess moduleAccess) {
    	SyntaxToken token = null;
	    IdentifierTree identifier = specifier.localName();
        if (identifier != null && !getName(identifier).equals("default")) {
            token = identifier.identifierToken();
        } else if (specifier.name() instanceof IdentifierTree) {
            token = ((IdentifierTree)specifier.name()).identifierToken();
        }
        if (token != null) {
            ObjectSymbol symbol = new ObjectSymbol();
            symbol.setName(getName(token));
            symbol.setModuleAccess(moduleAccess);
            symbol.setPosition(getPosition(token));
            symbols.add(symbol);
        }
    }

    /*
	 * process ES6 export statements
	 */
	private void processNamedExportDeclaration(NamedExportDeclarationTree namedExportDeclaration, 
			List<JavaScriptSymbol> symbols) {
		Tree object = namedExportDeclaration.object();
		if (object instanceof ExportDefaultBinding) {
			ExportDefaultBinding exportDefaultBinding = (ExportDefaultBinding) object;
			IdentifierTree identifier = exportDefaultBinding.exportedDefaultIdentifier();
			if (!identifier.name().equals("default")) {
				ObjectSymbol symbol = new ObjectSymbol();
				symbol.setName(getName(identifier.identifierToken()));
				symbol.setPosition(getPosition(identifier.identifierToken()));
				symbol.setModuleAccess(ModuleAccess.EXPORT);
			}
		} if (object instanceof FunctionDeclarationTree) {
			JavaScriptSymbol symbol = processFunctionTree((FunctionDeclarationTree)object, null, symbols);
			symbol.setModuleAccess(ModuleAccess.EXPORT);
		} else if (object instanceof VariableStatementTree) {
			for (JavaScriptSymbol symbol: processVariableDeclaration(((VariableStatementTree)object).declaration(), 
					null, symbols)) {
				symbol.setModuleAccess(ModuleAccess.EXPORT);
			}
		} else if (object instanceof ExportClauseTree) {
			ExportClauseTree exportClause = (ExportClauseTree) object;
			for (SpecifierTree specifier: exportClause.exports().specifiers()) {
				processSpecifierTree(specifier, symbols, ModuleAccess.EXPORT);
			}
		} else if (object instanceof ClassTree) {
			processClassTree((ClassTree)object, null, symbols).setModuleAccess(ModuleAccess.EXPORT);
		}
	}
	
	private void processBlock(BlockTree body, JavaScriptSymbol parent, List<JavaScriptSymbol> symbols) {
		for (StatementTree statement: body.statements()) {
			processStatement(statement, parent, symbols);
		}
	}
	
	private FunctionSymbol processFunctionTree(FunctionTree function, JavaScriptSymbol parent, List<JavaScriptSymbol> symbols) {
		IdentifierTree identifier;
		if (function instanceof FunctionDeclarationTree) {
			FunctionDeclarationTree functionDeclaration = (FunctionDeclarationTree) function;
			identifier = functionDeclaration.name();
		} else if (function instanceof FunctionExpressionTree) {
			FunctionExpressionTree functionExpression = (FunctionExpressionTree) function;
			identifier = functionExpression.name();
		} else {
			identifier = null;
		}
		FunctionSymbol symbol = new FunctionSymbol();
		symbol.setParent(parent);
		symbol.setName(getName(identifier));
		symbol.setPosition(getPosition(identifier));
		symbol.setParameters(processParameters(function.parameterList(), symbol, symbols));
		if (function.body() instanceof BlockTree) {
			BlockTree body = (BlockTree) function.body();
			symbol.setScope(getPosition(body.openCurlyBrace(), body.closeCurlyBrace()));
			processBlock(body, symbol, symbols);
		}
		symbols.add(symbol);
		return symbol;
	}

	private List<JavaScriptSymbol> processVariableDeclaration(VariableDeclarationTree variableDeclaration, 
			JavaScriptSymbol parent, List<JavaScriptSymbol> symbols) {
		List<JavaScriptSymbol> declared = new ArrayList<>();
		for (BindingElementTree bindingElement: variableDeclaration.variables()) {
			declared.addAll(processBindingElement(bindingElement, parent, symbols));
		}
		return declared;
	}
	
	private void processStatement(StatementTree statement, JavaScriptSymbol parent, List<JavaScriptSymbol> symbols) {
		if (statement instanceof FunctionDeclarationTree) {
			processFunctionTree((FunctionTree) statement, parent, symbols);
		} else if (statement instanceof VariableStatementTree) {
			VariableStatementTree variableStatement = (VariableStatementTree) statement;
			processVariableDeclaration(variableStatement.declaration(), parent, symbols);
		} else if (statement instanceof ExpressionStatementTree) {
			Tree expression = ((ExpressionStatementTree)statement).expression();
			if (expression instanceof ExpressionTree) {
				processExpression((ExpressionTree) expression, parent, symbols);
			}
		} else if (statement instanceof BlockTree) {
			processBlock((BlockTree)statement, parent, symbols);
		} else if (statement instanceof ClassTree) {
			processClassTree((ClassTree) statement, parent, symbols);
		}
	}
		
	private JavaScriptSymbol processClassTree(ClassTree classTree, JavaScriptSymbol parent, 
			List<JavaScriptSymbol> symbols) {
		ClassSymbol symbol = new ClassSymbol();
		symbol.setParent(parent);
		symbol.setName(getName(classTree.name()));
		symbol.setPosition(getPosition(classTree.name()));
		symbol.setScope(getPosition(classTree.openCurlyBraceToken(), classTree.closeCurlyBraceToken()));
		for (MethodDeclarationTree methodDeclaration: classTree.methods()) {
			processMethodDeclaration(methodDeclaration, symbol, symbols);
		}
		symbols.add(symbol);
		return symbol;
	}
	
	/*
	 * BindingElementTree represents variable binding such as "var a" or "var [a,b]"
	 */
	private List<JavaScriptSymbol> processBindingElement(BindingElementTree bindingElement, JavaScriptSymbol parent, 
			List<JavaScriptSymbol> symbols) {
		List<JavaScriptSymbol> binded = new ArrayList<>();
		if (bindingElement.bindingIdentifiers().size() == 1) {
			IdentifierTree identifier = bindingElement.bindingIdentifiers().get(0);
			JavaScriptSymbol symbol = new ObjectSymbol();
			symbol.setParent(parent);
			symbol.setName(getName(identifier));
			symbol.setPosition(getPosition(identifier));
			symbols.add(symbol);
			
			if (bindingElement instanceof InitializedBindingElementTree) {
				InitializedBindingElementTree initializedBindingElement = 
						(InitializedBindingElementTree) bindingElement;
				binded.add(assignSymbol(initializedBindingElement.right(), parent, symbol, symbols));
			}  else {
				binded.add(symbol);
			}
		}
		return binded;
	}

	/**
	 * Assign specified symbol to evaluation result of specified expression. Considering below code:
	 * <pre><code>
	 * var f = function() {};
	 * </code></pre>
	 * Here <i>f</i> is the symbol to be assigned, and <i>function() {}</i> is another symbol representing the 
	 * expression evaluation result. The function symbol will replace symbol <i>f</i>, but some attributes will be 
	 * taken from <i>f</i>, including name, parent, position, etc. Note that our logic will override name of 
	 * expression symbols, so symbol _g_ will be missing for below code:
	 * <pre><code>
	 * var f=g=function() {};
	 * </pre></code>
	 * We do not handle these cases as our extractor is designed to only handle common cases. It does not try to and 
	 * is not possible to capture all declarations accurately for a dynamic language
	 * 
	 * @param expressionTree 
	 * 			the expression to be evaluated
	 * @param parent
	 * 			parent symbol to be used when evaluate the expression
	 * @param symbol
	 * 			symbol to be assigned
	 * @param symbols
	 * 			context of symbol
	 * 
	 * @return
	 * 			result of assignment 			
	 */
	private JavaScriptSymbol assignSymbol(ExpressionTree expressionTree, JavaScriptSymbol parent, 
			JavaScriptSymbol symbol, List<JavaScriptSymbol> symbols) {
		JavaScriptSymbol expression = processExpression(expressionTree, parent, symbols);
		if (expression != null) {
			if (symbol.getModuleAccess() != ModuleAccess.NORMAL) 
				expression.setModuleAccess(symbol.getModuleAccess());
			expression.setName(symbol.getName());
			expression.setParent(symbol.getParent());
			expression.setPosition(symbol.getPosition());
			expression.setProperty(symbol.isProperty());
				
			// we will be using the expression symbol carrying more detailed information, so let's remove the original 
			// one
			symbols.remove(symbol);
			return expression;
		} else {
			return symbol;
		}
	}
	
	/**
	 * Create a faked symbol to group all discovered vue components, as otherwise discovered vue components may have 
	 * name collisions with other symbols if we put them in global namespace
	 */
	private JavaScriptSymbol getVueComponents(List<JavaScriptSymbol> symbols) {
		String name = "vueComponents";
		JavaScriptSymbol vueComponents = getChild(symbols, null, name);
		if (vueComponents == null) {
			vueComponents = new ObjectSymbol();
			vueComponents.setName(name);
			vueComponents.setSearchable(false);
			symbols.add(vueComponents);
		}
		return vueComponents;
	}
	
	/*
	 * Process specified expression and return a symbol representing the expression. Return <tt>null</tt> if the 
	 * expression does not represent a declared structure such as class, function, literal object, etc. For instance 
	 * expression <i>1+1</i> does not represent any static structures and will return <tt>null</tt>
	 * 
	 */
	@Nullable
	private JavaScriptSymbol processExpression(ExpressionTree expression, JavaScriptSymbol parent, 
			List<JavaScriptSymbol> symbols) {
		if (expression instanceof AssignmentExpressionTree) {
			return processAssignmentExpression((AssignmentExpressionTree)expression, parent, symbols);
		} else if (expression instanceof ObjectLiteralTree) {
			return processObjectLiteral((ObjectLiteralTree)expression, symbols, parent);
		} else if (expression instanceof ParenthesisedExpressionTree) {
			ParenthesisedExpressionTree parenthesisedExpression = (ParenthesisedExpressionTree) expression;
			return processExpression(parenthesisedExpression.expression(), parent, symbols);
		} else if (expression instanceof NewExpressionTree) { // new SomeClass(...)
			NewExpressionTree newExpression = (NewExpressionTree) expression;
			if (newExpression.arguments() != null) {
				for (Tree parameter: newExpression.arguments().parameters()) {
					if (parameter instanceof ExpressionTree) {
						// parameter may contain interesting structures, let's dig into it
						processExpression((ExpressionTree)parameter, parent, symbols);
					} 
				}
			}
			return processExpression(newExpression.expression(), parent, symbols);
		} else if (expression instanceof CallExpressionTree) { // call a function
			CallExpressionTree callExpression = (CallExpressionTree) expression;
			
			// CommonJS require statement
			if (callExpression.callee() instanceof IdentifierTree) {  
				IdentifierTree callingFunction = (IdentifierTree) callExpression.callee();
				if (callingFunction.name().equals("require")) {
					ObjectSymbol symbol = new ObjectSymbol();
					symbol.setParent(parent);
					symbol.setModuleAccess(ModuleAccess.IMPORT);
					symbols.add(symbol);
					return symbol;
				} 
			} 
			
			// Vue.js component registration
			if (callExpression.callee() instanceof DotMemberExpressionTree 
					&& StringUtils.deleteWhitespace(callExpression.callee().toString()).equals("Vue.component") 
					&& callExpression.arguments().parameters().size()>=2
					&& callExpression.arguments().parameters().get(0) instanceof LiteralTree
					&& ((LiteralTree) callExpression.arguments().parameters().get(0)).is(Kind.STRING_LITERAL)) {
				JavaScriptSymbol vueComponents = getVueComponents(symbols);
				LiteralTree vueComponent = (LiteralTree) callExpression.arguments().parameters().get(0);
				JavaScriptSymbol symbol = new ObjectSymbol();
				symbol.setParent(vueComponents);
				symbol.setName(getName(vueComponent.token()));
				symbol.setPosition(getPosition(vueComponent.token()));
				symbol.setModuleAccess(ModuleAccess.EXPORT);
				symbols.add(symbol);
				if (callExpression.arguments().parameters().get(1) instanceof ExpressionTree) {
					assignSymbol((ExpressionTree)callExpression.arguments().parameters().get(1), parent, symbol, 
							symbols);
				} 
				return null;
			} 
			if (callExpression.callee() instanceof DotMemberExpressionTree 
					&& StringUtils.deleteWhitespace(callExpression.callee().toString()).equals("Vue.extend") 
					&& !callExpression.arguments().parameters().isEmpty()) {
				if (callExpression.arguments().parameters().get(0) instanceof ExpressionTree) {
					// parameter may contain interesting structures, let's dig into it
					processExpression((ExpressionTree)callExpression.arguments().parameters().get(0), 
							parent, symbols);
				}
				return null;
			} 
			
			// callee may contain interesting structures, let's dig into it
			processExpression(callExpression.callee(), parent, symbols);
			
			for (Tree parameter: callExpression.arguments().parameters()) {
				if (parameter instanceof ExpressionTree) {
					// parameter may contain interesting structures, let's dig into it
					processExpression((ExpressionTree)parameter, parent, symbols);
				} 
			}
			return null;
		} else if (expression instanceof FunctionExpressionTree) { // an inline function declaration
			return processFunctionTree((FunctionTree) expression, parent, symbols);
		} else if (expression instanceof ArrowFunctionTree) {
			return processFunctionTree((FunctionTree) expression, parent, symbols);
		} else if (expression instanceof ClassTree) {
			return processClassTree((ClassTree)expression, parent, symbols);
		} else {
			List<IdentifierTree> identifierPath = getIdentifierPath(expression);
 			if (!identifierPath.isEmpty()) {
				ReferenceSymbol symbol = new ReferenceSymbol();
				List<String> referencedPath = new ArrayList<>();
				for (IdentifierTree identifier: identifierPath) {
					referencedPath.add(getName(identifier));
				}
				symbol.setReferencedPath(referencedPath);
				symbol.setReferencedParent(parent);
				symbols.add(symbol);
				return symbol;
			} else {
				return null;
			}
		}
	}

	@Nullable
	private SyntaxToken getNameToken(Tree nameTree) {
		if (nameTree instanceof IdentifierTree) {
			IdentifierTree identifier = (IdentifierTree) nameTree;
			return identifier.identifierToken();
		} else if (nameTree instanceof LiteralTree) {
			LiteralTree literal = (LiteralTree) nameTree;
			if (literal.is(Kind.STRING_LITERAL)) {
				return literal.token();
			}
		}
		return null;
	}
	
	private JavaScriptSymbol processObjectLiteral(ObjectLiteralTree objectLiteral, List<JavaScriptSymbol> symbols, 
			JavaScriptSymbol parent) {
		ObjectSymbol symbol = new ObjectSymbol();
		symbol.setParent(parent);
		symbol.setScope(getPosition(objectLiteral.openCurlyBrace(), objectLiteral.closeCurlyBrace()));
		symbols.add(symbol);
		for (Tree property: objectLiteral.properties()) {
			if (property instanceof PairPropertyTree) {
				PairPropertyTree pairProperty = (PairPropertyTree) property;
				SyntaxToken nameToken = getNameToken(pairProperty.key());
				if (nameToken != null) {
					JavaScriptSymbol propertySymbol = new ObjectSymbol();
					propertySymbol.setProperty(true);
					propertySymbol.setName(getName(nameToken));
					propertySymbol.setPosition(getPosition(nameToken));
					propertySymbol.setParent(symbol);
					symbols.add(propertySymbol);
					assignSymbol(pairProperty.value(), symbol, propertySymbol, symbols);
				}
			} else if (property instanceof MethodDeclarationTree) {
				processMethodDeclaration((MethodDeclarationTree) property, symbol, symbols);
			}
		}
		return symbol;
	}
	
	/*
	 * Recursively find declared symbols in specified parent and all its ancestors
	 */
	@Nullable
	private JavaScriptSymbol getSymbolInHierarchy(List<JavaScriptSymbol> symbols, @Nullable JavaScriptSymbol parent, 
			String symbolName) {
		while (true) {
			JavaScriptSymbol symbol = getChild(symbols, parent, symbolName);
			if (symbol != null)
				return symbol;
			else if (parent == null)
				return null;
			parent = (JavaScriptSymbol) parent.getParent();
		}
	}

	@Nullable
	private JavaScriptSymbol processAssignmentExpression(AssignmentExpressionTree assignmentExpression, 
			JavaScriptSymbol parent, List<JavaScriptSymbol> symbols) {
		if (assignmentExpression.is(Kind.ASSIGNMENT)) {
			List<IdentifierTree> identifierPath = getIdentifierPath(assignmentExpression.variable());
			if (!identifierPath.isEmpty()) {
				IdentifierTree identifier = identifierPath.get(0);
				JavaScriptSymbol symbol;

				/* 
				 * Find root symbol of a property path. Root symbol is the symbol representing first encountered 
				 * identifier in a identifier path. For instance root symbol of "person.name" is "person"
				 */
				boolean isThis = getName(identifier).equals("this");
				if (isThis) {
					// in case of the special "this" symbol, we always locate it inside current parent
					symbol = getChild(symbols, parent, getName(identifier));
				} else {
					symbol = getSymbolInHierarchy(symbols, parent, getName(identifier));
				}
				if (symbol == null) {
					symbol = new ObjectSymbol();
					symbol.setParent(isThis?parent:null);
					symbol.setName(getName(identifier));
					symbol.setPosition(getPosition(identifier));
					symbol.setProperty(isThis);
					symbol.setSearchable(!isThis);
					symbols.add(symbol);
				}
				
				/*
				 * Then we iterate over all subsequent identifiers to find the child symbol under root symbol
				 */
				for (int i=1; i<identifierPath.size(); i++) {
					identifier = identifierPath.get(i);
					JavaScriptSymbol child = getChild(symbols, symbol, getName(identifier));
					if (child == null) {
						child = new ObjectSymbol();
						child.setParent(symbol);
						child.setName(getName(identifier));
						child.setPosition(getPosition(identifier));
						child.setProperty(true);
						symbols.add(child);
					}
					symbol = child;
				}
				return assignSymbol(assignmentExpression.expression(), parent, symbol, symbols);
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	private List<IdentifierTree> getIdentifierPath(ExpressionTree expressionTree) {
		List<IdentifierTree> identifierPath = new ArrayList<>();
		if (expressionTree instanceof IdentifierTree) {
			IdentifierTree identifier = (IdentifierTree) expressionTree;
			identifierPath.add(identifier);
		} else if (expressionTree instanceof DotMemberExpressionTree) {
			DotMemberExpressionTree dotMemberExpressionTree = (DotMemberExpressionTree) expressionTree;
			identifierPath.addAll(getIdentifierPath(dotMemberExpressionTree.object()));
			if (!identifierPath.isEmpty()) {
				IdentifierTree identifier = dotMemberExpressionTree.property(); 
				identifierPath.add(identifier);
			}
		}
		return identifierPath;
	}

    private TokenPosition getPosition(SyntaxToken from, SyntaxToken to) {
    	return new TokenPosition(from.line()-1, from.column(), to.endLine()-1, to.endColumn());
    }

    @Nullable
    private String getName(@Nullable SyntaxToken token) {
        return token!=null?removeQuotes(token.text()):null;
    }

    @Nullable
    private String getName(@Nullable IdentifierTree tree) {
        return tree!=null?getName(tree.identifierToken()):null;
    }

    @Nullable
    private TokenPosition getPosition(@Nullable SyntaxToken token) {
        return new TokenPosition(token.line()-1, token.column(), token.endLine()-1, token.endColumn());
    }

    @Nullable
    private TokenPosition getPosition(@Nullable IdentifierTree tree) {
        return tree!=null?getPosition(tree.identifierToken()):null;
    }
    
    private String removeQuotes(String name) {
        return StringUtils.stripEnd(StringUtils.stripStart(name, "'\""), "'\"");
    }

    @Override
	public int getVersion() {
		return 5;
	}

	@Override
	public boolean accept(String fileName) {
		return acceptExtensions(fileName, "js", "jsx") 
				&& !fileName.contains(".min.") 
				&& !fileName.contains("-min.")
				&& !fileName.contains("_min.");
	}
	
}