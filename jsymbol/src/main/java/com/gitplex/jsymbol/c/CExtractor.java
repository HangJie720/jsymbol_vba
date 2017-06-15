package com.gitplex.jsymbol.c;

import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.ANTLRErrorListener;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gitplex.jsymbol.AbstractSymbolExtractor;
import com.gitplex.jsymbol.ExtractException;
import com.gitplex.jsymbol.c.CDeclarationParser.AbstractDeclaratorContext;
import com.gitplex.jsymbol.c.CDeclarationParser.DeclarationContext;
import com.gitplex.jsymbol.c.CDeclarationParser.DeclarationSpecifierContext;
import com.gitplex.jsymbol.c.CDeclarationParser.DeclarationSpecifiers2Context;
import com.gitplex.jsymbol.c.CDeclarationParser.DeclarationSpecifiersContext;
import com.gitplex.jsymbol.c.CDeclarationParser.DeclaratorContext;
import com.gitplex.jsymbol.c.CDeclarationParser.DirectAbstractDeclaratorContext;
import com.gitplex.jsymbol.c.CDeclarationParser.DirectDeclaratorContext;
import com.gitplex.jsymbol.c.CDeclarationParser.EnumSpecifierContext;
import com.gitplex.jsymbol.c.CDeclarationParser.EnumeratorContext;
import com.gitplex.jsymbol.c.CDeclarationParser.EnumeratorListContext;
import com.gitplex.jsymbol.c.CDeclarationParser.ExternalDeclarationContext;
import com.gitplex.jsymbol.c.CDeclarationParser.FunctionDefinitionContext;
import com.gitplex.jsymbol.c.CDeclarationParser.InitDeclaratorContext;
import com.gitplex.jsymbol.c.CDeclarationParser.InitDeclaratorListContext;
import com.gitplex.jsymbol.c.CDeclarationParser.ParameterDeclarationContext;
import com.gitplex.jsymbol.c.CDeclarationParser.ParameterListContext;
import com.gitplex.jsymbol.c.CDeclarationParser.ParameterTypeListContext;
import com.gitplex.jsymbol.c.CDeclarationParser.SpecifierQualifierListContext;
import com.gitplex.jsymbol.c.CDeclarationParser.StorageClassSpecifierContext;
import com.gitplex.jsymbol.c.CDeclarationParser.StructDeclarationContext;
import com.gitplex.jsymbol.c.CDeclarationParser.StructDeclarationListContext;
import com.gitplex.jsymbol.c.CDeclarationParser.StructDeclaratorContext;
import com.gitplex.jsymbol.c.CDeclarationParser.StructDeclaratorListContext;
import com.gitplex.jsymbol.c.CDeclarationParser.StructOrUnionSpecifierContext;
import com.gitplex.jsymbol.c.CDeclarationParser.TypeSpecifierContext;
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
import com.gitplex.jsymbol.util.Utils;

/**
 * We relies on ANTLR to parse C header and source files, and then extract C symbols from the parse tree. Preprocessor 
 * directives except macro definition will be dropped at lexer stage, and macro definition tokens will be put into a 
 * separate channel. Since symbol extraction works on a per-file base, so we can not resolve macros. This can make some 
 * part of source file not conforming to the grammar, so we configure ANTLR error listener to ignore those errors, and 
 * also our symbol extraction logic catches and ignores exceptions
 */
public class CExtractor extends AbstractSymbolExtractor<CSymbol> {

	private static final Logger logger = LoggerFactory.getLogger(CExtractor.class);
	
	@Override
	public List<CSymbol> extract(String fileName, String fileContent) throws ExtractException {
		List<CSymbol> symbols = new ArrayList<>();

		CSymbol fileSymbol;
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
		
		CDeclarationLexer lexer = new CDeclarationLexer(new ANTLRInputStream(fileContent));
		lexer.removeErrorListeners();
		lexer.addErrorListener(errorListener);
		
		Token token = lexer.nextToken();
		while (token.getType() != Token.EOF) {
			if (token.getChannel() == CDeclarationLexer.MACRO_DEFINITION_CHANNEL) {
				symbols.add(new MacroSymbol(fileSymbol, token.getText(), fileSymbol instanceof SourceFileSymbol, 
						Utils.getTokenPosition(token)));
			}
			token = lexer.nextToken();
		}
		lexer.reset();

		CommonTokenStream stream = new CommonTokenStream(lexer);
		CDeclarationParser parser = new CDeclarationParser(stream);
		parser.removeErrorListeners();
		parser.addErrorListener(errorListener);
		
		for (ExternalDeclarationContext externalDeclaration: parser.compilationUnit().externalDeclaration()) {
			try {
				if (externalDeclaration.functionDefinition() != null) {
					FunctionDefinitionContext functionDefinition = externalDeclaration.functionDefinition();
					TerminalNode identifier = getIdentifier(functionDefinition.declarator());
					String params;
					if (functionDefinition.declarationList() != null) {
						StringBuilder paramsBuilder = new StringBuilder();
						for (DeclarationContext declaration: functionDefinition.declarationList().declaration()) {
							String type = getType(declaration.declarationSpecifiers());
							if (paramsBuilder.length() != 0)
								paramsBuilder.append(", ");
							paramsBuilder.append(type);
						}
						params = paramsBuilder.toString();
					} else {
						params = getParameters(functionDefinition.declarator().directDeclarator());
					}
					if (params.length() == 0)
						params = null;

					StringBuilder typeBuilder = new StringBuilder();
					if (functionDefinition.declarationSpecifiers() != null)
						typeBuilder.append(getType(functionDefinition.declarationSpecifiers()));
					typeBuilder.append(getTypeDecorator(functionDefinition.declarator()));
					String type;
					if (typeBuilder.length() != 0)
						type = typeBuilder.toString();
					else
						type = null;
					
					/*
					 * Static or extern symbols defined in header file is not considered local as they can be 
					 * referenced by other files via header file including
					 */
					boolean local = (fileSymbol instanceof SourceFileSymbol) 
							&& functionDefinition.declarationSpecifiers() != null 
							&& (isStatic(functionDefinition.declarationSpecifiers()) 
									|| isExtern(functionDefinition.declarationSpecifiers()));
					FunctionSymbol symbol = new FunctionSymbol(fileSymbol, identifier.getText(), local, 
							true, params, type, Utils.getTokenPosition(identifier.getSymbol()), 
							Utils.getTokenPosition(functionDefinition.start, functionDefinition.stop));
					symbols.add(symbol);
				} else if (externalDeclaration.declaration() != null 
						&& externalDeclaration.declaration().declarationSpecifiers() != null) {
					DeclarationSpecifiersContext declarationSpecifiers = 
							externalDeclaration.declaration().declarationSpecifiers();
					
					List<TypeSpecifierContext> typeSpecifiers = new ArrayList<>();
					for (DeclarationSpecifierContext declarationSpecifier: declarationSpecifiers.declarationSpecifier()) {
						if (declarationSpecifier.typeSpecifier() != null)
							typeSpecifiers.add(declarationSpecifier.typeSpecifier());
					}
					
					/*
					 * Static or extern symbols defined in header file is not considered local as they can be 
					 * referenced by other files via header file including
					 */
					boolean isLocal = (fileSymbol instanceof SourceFileSymbol) 
							&& (isStatic(declarationSpecifiers) || isExtern(declarationSpecifiers));
					
					boolean isTypedef = isTypedef(declarationSpecifiers);
					List<DeclaratorContext> declarators = new ArrayList<>();
					InitDeclaratorListContext initDeclaratorList = externalDeclaration.declaration().initDeclaratorList();
					if (initDeclaratorList != null) {
						for (InitDeclaratorContext initDeclarator: getInitDeclarators(initDeclaratorList)) {
							declarators.add(initDeclarator.declarator());
						}
					} 
					processDeclarators(typeSpecifiers, declarators, fileSymbol, symbols, isLocal, isTypedef);
				}
			} catch (Exception e) {
				// Exceptions may get thrown even for a valid C file due to our inability to resolve macros, so just 
				// log the error and continue with next declaration
				logger.debug("Error extracting symbols", e);
			}
		}
		
		return symbols;
	}
	
	private void processDeclarators(List<TypeSpecifierContext> typeSpecifiers, List<DeclaratorContext> declarators, 
			CSymbol parent, List<CSymbol> symbols, boolean isLocal, boolean isTypedef) {
		
		List<CSymbol> typeSymbols = new ArrayList<>();
		
		for (TypeSpecifierContext typeSpecifier: typeSpecifiers) {
			if (typeSpecifier.structOrUnionSpecifier() != null) {
				StructOrUnionSpecifierContext structOrUnionSpecifier = typeSpecifier.structOrUnionSpecifier();
				TerminalNode identifier = structOrUnionSpecifier.Identifier();
				CSymbol symbol;
				if (identifier != null && structOrUnionSpecifier.structDeclarationList() != null) {
					if (structOrUnionSpecifier.structOrUnion().getText().equals("struct")) {
						symbol = new StructSymbol(parent, identifier.getText(), isLocal, 
								Utils.getTokenPosition(identifier.getSymbol()), 
								Utils.getTokenPosition(structOrUnionSpecifier.start, structOrUnionSpecifier.stop));
					} else {
						symbol = new UnionSymbol(parent, identifier.getText(), isLocal, 
								Utils.getTokenPosition(identifier.getSymbol()), 
								Utils.getTokenPosition(structOrUnionSpecifier.start, structOrUnionSpecifier.stop));
					}
					symbols.add(symbol);
					typeSymbols.add(symbol);
				}
			} else if (typeSpecifier.enumSpecifier() != null) {
				EnumSpecifierContext enumSpecifier = typeSpecifier.enumSpecifier();
				TerminalNode identifier = enumSpecifier.Identifier();
				if (identifier != null && enumSpecifier.enumeratorList() != null) {
					CSymbol symbol = new EnumSymbol(parent, identifier.getText(), isLocal, 
							Utils.getTokenPosition(identifier.getSymbol()), 
							Utils.getTokenPosition(enumSpecifier.start, enumSpecifier.stop));
					symbols.add(symbol);
					typeSymbols.add(symbol);
				}
			}
		}
		
		for (DeclaratorContext declarator: declarators) {
			TerminalNode identifier = getIdentifier(declarator);
			StringBuilder typeBuilder = new StringBuilder();
			boolean isFunction = isFunction(declarator.directDeclarator());
			typeBuilder.append(getType(typeSpecifiers)).append(getTypeDecorator(declarator));
			String type = typeBuilder.length()!=0?typeBuilder.toString():null;
			CSymbol symbol;
			if (isFunction) {
				String params = getParameters(declarator.directDeclarator());
				if (params.length() == 0)
					params = null;
				symbol = new FunctionSymbol(parent, identifier.getText(), isLocal, false, params, type, 
						Utils.getTokenPosition(identifier.getSymbol()), null);
			} else if (isTypedef) {
				symbol = new TypedefSymbol(parent, identifier.getText(), isLocal, type, 
						Utils.getTokenPosition(identifier.getSymbol()));
				typeSymbols.add(symbol);
			} else {
				symbol = new VariableSymbol(parent, identifier.getText(), isLocal, type, 
						Utils.getTokenPosition(identifier.getSymbol()));
			}
			symbols.add(symbol);
		}
		
		for (CSymbol symbol: typeSymbols) {
			for (TypeSpecifierContext typeSpecifier: typeSpecifiers) {
				if (typeSpecifier.structOrUnionSpecifier() != null) {
					StructOrUnionSpecifierContext structOrUnionSpecifier = typeSpecifier.structOrUnionSpecifier();
					if (structOrUnionSpecifier.structDeclarationList() != null) {
						for (StructDeclarationContext structDeclaration: 
								getStructDeclarations(structOrUnionSpecifier.structDeclarationList())) {
							List<TypeSpecifierContext> memberTypeSpecifiers = 
									getTypeSpecifiers(structDeclaration.specifierQualifierList());
							List<DeclaratorContext> memberDeclaractors = new ArrayList<>();
							if (structDeclaration.structDeclaratorList() != null) {
								for (StructDeclaratorContext structDeclarator: 
										getStructDeclarators(structDeclaration.structDeclaratorList())) {
									if (structDeclarator.declarator() != null)
										memberDeclaractors.add(structDeclarator.declarator());
								}
							}
							processDeclarators(memberTypeSpecifiers, memberDeclaractors, symbol, symbols, false, false);
						}
					}
					break;
				} else if (typeSpecifier.enumSpecifier() != null) {
					EnumSpecifierContext enumSpecifier = typeSpecifier.enumSpecifier();
					if (enumSpecifier.enumeratorList() != null) {
						for (EnumeratorContext enumerator: getEnumerators(enumSpecifier.enumeratorList())) {
							TerminalNode identifier = enumerator.enumerationConstant().Identifier();
							CSymbol memberSymbol = new MemberSymbol(symbol, identifier.getText(), null, 
									Utils.getTokenPosition(identifier.getSymbol()));
							symbols.add(memberSymbol);
						}
					}
					break;
				}
			}
		}
	}
	
	private List<TypeSpecifierContext> getTypeSpecifiers(SpecifierQualifierListContext specifierQualifierList) {
		List<TypeSpecifierContext> typeSpecifiers = new ArrayList<>();
		if (specifierQualifierList.specifierQualifierList() != null)
			typeSpecifiers.addAll(getTypeSpecifiers(specifierQualifierList.specifierQualifierList()));
		if (specifierQualifierList.typeSpecifier() != null)
			typeSpecifiers.add(specifierQualifierList.typeSpecifier());
		return typeSpecifiers;
	}
	
	private List<StructDeclarationContext> getStructDeclarations(StructDeclarationListContext structDeclarationList) {
		List<StructDeclarationContext> structDeclarations = new ArrayList<>();
		if (structDeclarationList.structDeclarationList() != null)
			structDeclarations.addAll(getStructDeclarations(structDeclarationList.structDeclarationList()));
		structDeclarations.add(structDeclarationList.structDeclaration());
		return structDeclarations;
	}
	
	private List<StructDeclaratorContext> getStructDeclarators(StructDeclaratorListContext structDeclaratorList) {
		List<StructDeclaratorContext> structDeclarators = new ArrayList<>();
		if (structDeclaratorList.structDeclaratorList() != null)
			structDeclarators.addAll(getStructDeclarators(structDeclaratorList.structDeclaratorList()));
		structDeclarators.add(structDeclaratorList.structDeclarator());
		return structDeclarators;
	}
	
	private List<EnumeratorContext> getEnumerators(EnumeratorListContext enumeratorList) {
		List<EnumeratorContext> enumerators = new ArrayList<>();
		if (enumeratorList.enumeratorList() != null)
			enumerators.addAll(getEnumerators(enumeratorList.enumeratorList()));
		enumerators.add(enumeratorList.enumerator());
		return enumerators;
	}
	
	private List<InitDeclaratorContext> getInitDeclarators(InitDeclaratorListContext initDeclaratorList) {
		List<InitDeclaratorContext> initDeclarators = new ArrayList<>();
		if (initDeclaratorList.initDeclaratorList() != null)
			initDeclarators.addAll(getInitDeclarators(initDeclaratorList.initDeclaratorList()));
		initDeclarators.add(initDeclaratorList.initDeclarator());
		return initDeclarators;
	}
	
	private boolean isFunction(DirectDeclaratorContext directDeclarator) {
		if (directDeclarator.Identifier() != null) {
			return false;
		} else if (directDeclarator.declarator() != null) {
			return isFunction(directDeclarator.declarator().directDeclarator());
		} else if (directDeclarator.arrayDeclarator() != null) {
			return isFunction(directDeclarator.directDeclarator());
		} else {
			directDeclarator = directDeclarator.directDeclarator();
			if (directDeclarator.Identifier() != null) {
				return true;
			} else if (directDeclarator.declarator() != null) {
				if (onlyContainsIdentifier(directDeclarator.declarator())) {
					return true;
				} else {
					/*
					 * Check if it is a function returning a function, for instance below is a function declaration:
					 * void (*signal(int sig, void (*func)(int)))(int);
					 * 
					 * Function name: signal
					 * Return type: void(*)(int)
					 * Function parameter: (int, void(*)(int))
					 */
					return isFunction(directDeclarator.declarator().directDeclarator());
				}
			} else {
				// invalid C statement, so simply return false
				return false;
			}
		}
	}
	
	private boolean onlyContainsIdentifier(DeclaratorContext declarator) {
		if (declarator.pointer() != null) {
			return false;
		} else {
			DirectDeclaratorContext directDeclarator = declarator.directDeclarator();
			if (directDeclarator.Identifier() != null)
				return true;
			else if (directDeclarator.declarator() != null)
				return onlyContainsIdentifier(directDeclarator.declarator());
			else
				return false;
		}
	}
	
	private TerminalNode getIdentifier(DeclaratorContext declarator) {
		return getIdentifier(declarator.directDeclarator());
	}
	
	private TerminalNode getIdentifier(DirectDeclaratorContext directDeclarator) {
		if (directDeclarator.Identifier() != null) {
			return directDeclarator.Identifier();
		} else if (directDeclarator.declarator() != null) {
			return getIdentifier(directDeclarator.declarator());
		} else {
			return getIdentifier(directDeclarator.directDeclarator());
		}
	}
	
	/**
	 * Type decorator is information appended to a type of a variable. For instance, considering below variable 
	 * declaration:
	 * 
	 * <pre><code>
	 * char *value;
	 * </code></pre>
	 * 
	 * Variable &quot;value&quot; will have type &quot;char*&quot;, where &quot;char&quot; is parsed from typeSpecifier, 
	 * and &quot;*&quot; is parsed from type decorator 
	 * 
	 * @param declarator
	 * 			the grammar declarator construct to parse type decorator from
	 * @return
	 * 			type decorator
	 */
	private String getTypeDecorator(DeclaratorContext declarator) {
		StringBuilder builder = new StringBuilder();
		if (declarator.pointer() != null)
			builder.append(declarator.pointer().getText());
		builder.append(getTypeDecorator(declarator.directDeclarator()));
		return builder.toString().trim();
	}
	
	private String getTypeDecorator(AbstractDeclaratorContext abstractDeclarator) {
		StringBuilder builder = new StringBuilder();
		if (abstractDeclarator.pointer() != null)
			builder.append(abstractDeclarator.pointer().getText());
		if (abstractDeclarator.directAbstractDeclarator() != null)
			builder.append(getTypeDecorator(abstractDeclarator.directAbstractDeclarator()));
		return builder.toString();
	}
	
	private String getTypeDecorator(DirectDeclaratorContext directDeclarator) {
		StringBuilder builder = new StringBuilder();
		if (directDeclarator.declarator() != null) {
			builder.append(getTypeDecorator(directDeclarator.declarator()));
		} else if (directDeclarator.arrayDeclarator() != null) {
			builder.append(getTypeDecorator(directDeclarator.directDeclarator()));
			builder.append("[]");
		} else if (directDeclarator.directDeclarator() != null) {
			String typeParams;
			if (directDeclarator.parameterTypeList() != null)
				typeParams = "(" + getParameters(directDeclarator.parameterTypeList()) + ")";
			else 
				typeParams = "()";
			
			directDeclarator = directDeclarator.directDeclarator();
			
			if (directDeclarator.Identifier() != null) {
				return "";
			} else if (directDeclarator.declarator() != null) {
				if (onlyContainsIdentifier(directDeclarator.declarator())) {
					return "";
				} else {
					/*
					 * It is either a function returning a function, or a function pointer/array returning a function. 
					 * In either case, we append the type parameters to form type decorator. For instance for below 
					 * statement:
					 * 
					 * void (*signal(int sig, void (*func)(int)))(int);
					 * 
					 * It defines the signal function returning type "void(*)(int)"
					 */
					builder.append("(")
							.append(getTypeDecorator(directDeclarator.declarator()))
							.append(")")
							.append(typeParams);
				}
			} else { 
				// invalid C statement, so simply return an empty string
				return "";
			}
		} 
		return builder.toString();
	}
	
	private String getTypeDecorator(DirectAbstractDeclaratorContext directAbstractDeclarator) {
		if (directAbstractDeclarator.abstractDeclarator() != null) {
			return "(" + getTypeDecorator(directAbstractDeclarator.abstractDeclarator()) + ")";
		} else if (directAbstractDeclarator.abstractArrayDeclarator() != null) {
			if (directAbstractDeclarator.directAbstractDeclarator() != null) {
				return getTypeDecorator(directAbstractDeclarator.directAbstractDeclarator()) + "[]";
			} else {
				return "[]";
			}
		} else if (directAbstractDeclarator.directAbstractDeclarator() != null) {
			if (directAbstractDeclarator.parameterTypeList() != null) { 
				return getTypeDecorator(directAbstractDeclarator.directAbstractDeclarator()) + 
						"(" + getParameters(directAbstractDeclarator.parameterTypeList()) + ")";
			} else {
				return getTypeDecorator(directAbstractDeclarator.directAbstractDeclarator()) +"()";
			}
		} else if (directAbstractDeclarator.parameterTypeList() != null) {
			return "(" + getParameters(directAbstractDeclarator.parameterTypeList()) + ")";
		} else {
			return "()";
		}
	}
	
	private boolean isStatic(DeclarationSpecifiersContext declarationSpecifiers) {
		for (DeclarationSpecifierContext declarationSpecifier: declarationSpecifiers.declarationSpecifier()) {
			boolean isStatic = isStatic(declarationSpecifier);
			if (isStatic)
				return true;
		}
		return false;
	}
	
	private boolean isExtern(DeclarationSpecifiersContext declarationSpecifiers) {
		for (DeclarationSpecifierContext declarationSpecifier: declarationSpecifiers.declarationSpecifier()) {
			boolean isExtern = isExtern(declarationSpecifier);
			if (isExtern)
				return true;
		}
		return false;
	}
	
	private boolean isTypedef(DeclarationSpecifiersContext declarationSpecifiers) {
		for (DeclarationSpecifierContext declarationSpecifier: declarationSpecifiers.declarationSpecifier()) {
			boolean isTypedef = isTypedef(declarationSpecifier);
			if (isTypedef)
				return true;
		}
		return false;
	}
	
	private String getType(DeclarationSpecifiersContext declarationSpecifiers) {
		List<TypeSpecifierContext> typeSpecifiers = new ArrayList<>();
		for (DeclarationSpecifierContext declarationSpecifier: declarationSpecifiers.declarationSpecifier()) {
			if (declarationSpecifier.typeSpecifier() != null)
				typeSpecifiers.add(declarationSpecifier.typeSpecifier());
		}
		return getType(typeSpecifiers);
	}
	
	private String getType(List<TypeSpecifierContext> typeSpecifiers) {
		StringBuilder builder = new StringBuilder();
		
		for (TypeSpecifierContext typeSpecifier: typeSpecifiers) {
			builder.append(getType(typeSpecifier)).append(" ");
		}
		
		return builder.toString().trim();
	}

	private String getType(DeclarationSpecifiers2Context declarationSpecifiers2) {
		StringBuilder builder = new StringBuilder();
		
		for (DeclarationSpecifierContext declarationSpecifier: declarationSpecifiers2.declarationSpecifier()) {
			if (declarationSpecifier.typeSpecifier() != null)
				builder.append(getType(declarationSpecifier.typeSpecifier())).append(" ");
		}
		
		return builder.toString().trim();
	}
	
	private boolean isStatic(DeclarationSpecifierContext declarationSpecifier) {
		StorageClassSpecifierContext storageClassSpecifier = declarationSpecifier.storageClassSpecifier();
		if (storageClassSpecifier != null) {
			return storageClassSpecifier.getText().equals("static");
		} else {
			return false;
		}
	}
	
	private boolean isExtern(DeclarationSpecifierContext declarationSpecifier) {
		StorageClassSpecifierContext storageClassSpecifier = declarationSpecifier.storageClassSpecifier();
		if (storageClassSpecifier != null) {
			return storageClassSpecifier.getText().equals("extern");
		} else {
			return false;
		}
	}
	
	private boolean isTypedef(DeclarationSpecifierContext declarationSpecifier) {
		StorageClassSpecifierContext storageClassSpecifier = declarationSpecifier.storageClassSpecifier();
		if (storageClassSpecifier != null) {
			return storageClassSpecifier.getText().equals("typedef");
		} else {
			return false;
		}
	}
	
	private String getType(TypeSpecifierContext typeSpecifier) {
		if (typeSpecifier.structOrUnionSpecifier() != null) {
			String type = typeSpecifier.structOrUnionSpecifier().structOrUnion().getText();
			TerminalNode identifier = typeSpecifier.structOrUnionSpecifier().Identifier();
			if (identifier != null)
				return type + " " + identifier.getText();
			else
				return type;
		} else if (typeSpecifier.enumSpecifier() != null) {
			String type = "enum";
			TerminalNode identifier = typeSpecifier.enumSpecifier().Identifier();
			if (identifier != null)
				return type + " " + identifier.getText();
			else
				return type;
		} else {
			return typeSpecifier.getText();
		}
	}
	
	private String getParameters(DirectDeclaratorContext directDeclarator) {
		if (directDeclarator.declarator() != null) {
			return getParameters(directDeclarator.declarator().directDeclarator());
		} else if (directDeclarator.arrayDeclarator() != null || isFunction(directDeclarator.directDeclarator())) {
			return getParameters(directDeclarator.directDeclarator());
		} else if (directDeclarator.parameterTypeList() != null) {
			return getParameters(directDeclarator.parameterTypeList());
		} else {
			return "";
		}
	}
	
	private String getParameters(ParameterTypeListContext parameterTypeList) {
		String params = getParameters(parameterTypeList.parameterList());
		Token varArgs = parameterTypeList.varArgs;
		if (varArgs != null)
			return params + ", " + varArgs.getText();
		else
			return params;
	}
	
	private String getParameters(ParameterListContext parameterList) {
		String lastParam = getType(parameterList.parameterDeclaration());
		if (parameterList.parameterList() != null) {
			return getParameters(parameterList.parameterList()) + ", " + lastParam;
		} else {
			return lastParam;
		}
	}
	
	private String getType(ParameterDeclarationContext parameterDeclaration) {
		StringBuilder builder = new StringBuilder();
		if (parameterDeclaration.declarationSpecifiers() != null) {
			builder.append(getType(parameterDeclaration.declarationSpecifiers()));
			builder.append(getTypeDecorator(parameterDeclaration.declarator()));
		} else {
			builder.append(getType(parameterDeclaration.declarationSpecifiers2()));
			if (parameterDeclaration.abstractDeclarator() != null)
				builder.append(getTypeDecorator(parameterDeclaration.abstractDeclarator()));
		}
		return builder.toString();
	}
	
	@Override
	public boolean accept(String fileName) {
		return acceptExtensions(fileName, "c", "h");
	}

	@Override
	public int getVersion() {
		return 0;
	}
	
}
