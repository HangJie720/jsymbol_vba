package com.gitplex.jsymbol.python3;

import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;

import com.gitplex.jsymbol.AbstractSymbolExtractor;
import com.gitplex.jsymbol.ExtractException;
import com.gitplex.jsymbol.python3.Python3Parser.And_exprContext;
import com.gitplex.jsymbol.python3.Python3Parser.And_testContext;
import com.gitplex.jsymbol.python3.Python3Parser.ArglistContext;
import com.gitplex.jsymbol.python3.Python3Parser.ArgumentContext;
import com.gitplex.jsymbol.python3.Python3Parser.Arith_exprContext;
import com.gitplex.jsymbol.python3.Python3Parser.AtomContext;
import com.gitplex.jsymbol.python3.Python3Parser.ClassdefContext;
import com.gitplex.jsymbol.python3.Python3Parser.ComparisonContext;
import com.gitplex.jsymbol.python3.Python3Parser.Compound_stmtContext;
import com.gitplex.jsymbol.python3.Python3Parser.ExprContext;
import com.gitplex.jsymbol.python3.Python3Parser.FactorContext;
import com.gitplex.jsymbol.python3.Python3Parser.FuncdefContext;
import com.gitplex.jsymbol.python3.Python3Parser.Not_testContext;
import com.gitplex.jsymbol.python3.Python3Parser.Or_testContext;
import com.gitplex.jsymbol.python3.Python3Parser.ParametersContext;
import com.gitplex.jsymbol.python3.Python3Parser.PowerContext;
import com.gitplex.jsymbol.python3.Python3Parser.Shift_exprContext;
import com.gitplex.jsymbol.python3.Python3Parser.Star_exprContext;
import com.gitplex.jsymbol.python3.Python3Parser.StmtContext;
import com.gitplex.jsymbol.python3.Python3Parser.SuiteContext;
import com.gitplex.jsymbol.python3.Python3Parser.TermContext;
import com.gitplex.jsymbol.python3.Python3Parser.TestContext;
import com.gitplex.jsymbol.python3.Python3Parser.TfpdefContext;
import com.gitplex.jsymbol.python3.Python3Parser.TypedargslistContext;
import com.gitplex.jsymbol.python3.Python3Parser.Xor_exprContext;
import com.gitplex.jsymbol.python3.symbols.FunctionSymbol;
import com.gitplex.jsymbol.python3.symbols.Python3Symbol;
import com.gitplex.jsymbol.python3.symbols.TypeSymbol;
import com.gitplex.jsymbol.util.Utils;

public class Python3Extractor extends AbstractSymbolExtractor<Python3Symbol>
{

	@Override
	public List<Python3Symbol> extract(String fileName, String fileContent) throws ExtractException
	{
		List<Python3Symbol> symbols = new ArrayList<>();

		Python3Lexer lexer = new Python3Lexer(new ANTLRInputStream(fileContent));
		CommonTokenStream stream = new CommonTokenStream(lexer);
		Python3Parser parser = new Python3Parser(stream);

		for (StmtContext stmtContext : parser.file_input().stmt())
		{
			processStmt(null, stmtContext, symbols);
		}

		return symbols;
	}

	private void processStmt(Python3Symbol parent, StmtContext stmtContext, List<Python3Symbol> symbols)
	{
		// 获取复杂语句
		Compound_stmtContext compound_stmtContext = stmtContext.compound_stmt();
		if (compound_stmtContext != null)
		{
			// 获取类定义
			ClassdefContext classdefContext = compound_stmtContext.classdef();
			if (classdefContext != null)
			{
				processClassdef(parent, classdefContext, symbols);
			}
			// 获取函数定义
			FuncdefContext funcdefContext = compound_stmtContext.funcdef();
			if (funcdefContext != null)
			{
				processFunction(parent, funcdefContext, symbols);
			}
		}
	}

	private void processClassdef(Python3Symbol parent, ClassdefContext classdefContext, List<Python3Symbol> symbols)
	{
		// 获取父类-start
		StringBuilder para = new StringBuilder();
		ArglistContext arglistContext = classdefContext.arglist();
		if (arglistContext != null)
		{
			para.append("(");
			List<ArgumentContext> argumentContexts = arglistContext.argument();
			for (ArgumentContext argumentContext : argumentContexts)
			{
				TestContext testContext = argumentContext.test(0);
				Or_testContext or_testContext = testContext.or_test(0);
				And_testContext and_testContext = or_testContext.and_test(0);
				Not_testContext not_testContext = and_testContext.not_test(0);
				ComparisonContext comparisonContext = not_testContext.comparison();
				Star_exprContext star_exprContext = comparisonContext.star_expr(0);
				ExprContext exprContext = star_exprContext.expr();
				Xor_exprContext xor_exprContext = exprContext.xor_expr(0);
				And_exprContext and_exprContext = xor_exprContext.and_expr(0);
				Shift_exprContext shift_exprContext = and_exprContext.shift_expr(0);
				Arith_exprContext arith_exprContext = shift_exprContext.arith_expr(0);
				TermContext termContext = arith_exprContext.term(0);
				FactorContext factorContext = termContext.factor(0);
				PowerContext powerContext = factorContext.power();
				AtomContext atomContext = powerContext.atom();

				para.append(atomContext.NAME().getText() + ",");
			}
			para.setCharAt(para.length() - 1, ')');
		}
		// 获取父类-end
		TypeSymbol typeSymbol = new TypeSymbol(parent, classdefContext.NAME().getText() + para,
				Utils.getTokenPosition(classdefContext.NAME().getSymbol()),
				Utils.getTokenPosition(classdefContext.getStart(), classdefContext.getStop()));
		symbols.add(typeSymbol);
		// 获取类体里的类和方法
		processSuite(typeSymbol, classdefContext, symbols);
	}

	private void processSuite(Python3Symbol parent, ClassdefContext classdefContext, List<Python3Symbol> symbols)
	{
		SuiteContext suiteContext = classdefContext.suite();
		List<StmtContext> stmtContexts = suiteContext.stmt();
		for (StmtContext stmtContext : stmtContexts)
		{
			processStmt(parent, stmtContext, symbols);
		}
	}

	private void processFunction(Python3Symbol parent, FuncdefContext funcdefContext, List<Python3Symbol> symbols)
	{
		// 获取参数-start
		StringBuilder para = new StringBuilder();
		para.append("(");
		ParametersContext parametersContext = funcdefContext.parameters();
		TypedargslistContext typedargslistContext = parametersContext.typedargslist();
		if (typedargslistContext != null)
		{
			List<TfpdefContext> tfpdefContexts = typedargslistContext.tfpdef();
			for (TfpdefContext tfpdefContext : tfpdefContexts)
			{
				para.append(tfpdefContext.NAME().getText() + ",");
			}
			para.setCharAt(para.length() - 1, ')');
		} else
		{
			para.append(")");
		}
		// 获取参数-end
		FunctionSymbol functionSymbol = new FunctionSymbol(parent, funcdefContext.NAME().getText() + para,
				Utils.getTokenPosition(funcdefContext.NAME().getSymbol()),
				Utils.getTokenPosition(funcdefContext.getStart(), funcdefContext.getStop()));
		symbols.add(functionSymbol);
	}

	@Override
	public boolean accept(String fileName)
	{
		// TODO Auto-generated method stub
		return acceptExtensions(fileName, "py");
	}

	@Override
	public int getVersion()
	{
		// TODO Auto-generated method stub
		return 0;
	}

}
