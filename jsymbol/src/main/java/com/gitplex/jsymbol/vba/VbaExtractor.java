package com.gitplex.jsymbol.vba;

import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;

import com.gitplex.jsymbol.AbstractSymbolExtractor;
import com.gitplex.jsymbol.ExtractException;
import com.gitplex.jsymbol.util.Utils;
import com.gitplex.jsymbol.vba.vbaParser.ArgContext;
import com.gitplex.jsymbol.vba.vbaParser.ArgListContext;
import com.gitplex.jsymbol.vba.vbaParser.AsTypeClauseContext;
import com.gitplex.jsymbol.vba.vbaParser.BaseTypeContext;
import com.gitplex.jsymbol.vba.vbaParser.FunctionStmtContext;
import com.gitplex.jsymbol.vba.vbaParser.ModuleBodyContext;
import com.gitplex.jsymbol.vba.vbaParser.ModuleBodyElementContext;
import com.gitplex.jsymbol.vba.vbaParser.ModuleHeaderContext;
import com.gitplex.jsymbol.vba.vbaParser.SubStmtContext;
import com.gitplex.jsymbol.vba.vbaParser.TypeContext;
import com.gitplex.jsymbol.vba.symbols.FunctionSymbol;
import com.gitplex.jsymbol.vba.symbols.TypeSymbol;
import com.gitplex.jsymbol.vba.symbols.SubSymbol;
import com.gitplex.jsymbol.vba.symbols.VbaSymbol;

public class VbaExtractor extends AbstractSymbolExtractor<VbaSymbol> {

	/**
	 * 为VBA(Visual Basic for Application)这门语言设计提取方法
	 */
	@Override
	public List<VbaSymbol> extract(String fileName, String fileContent) throws ExtractException {
		// TODO Auto-generated method stub
		List<VbaSymbol> symbols = new ArrayList<>();

		vbaLexer lexer = new vbaLexer(new ANTLRInputStream(fileContent));
		CommonTokenStream stream = new CommonTokenStream(lexer);
		vbaParser parser = new vbaParser(stream);
		//开始规则，获取模型体对象
		ModuleBodyContext moduleBodyContext = parser.startRule().module().moduleBody();
		if (moduleBodyContext != null) {
			List<ModuleBodyElementContext> moduleBodyElementContexts = moduleBodyContext.moduleBodyElement();
			for (ModuleBodyElementContext moduleBodyElementContext : moduleBodyElementContexts) {
				// 函数声明
				FunctionStmtContext functionStmtContext = moduleBodyElementContext.functionStmt();
				if (functionStmtContext != null) {
					//处理函数声明
					processFunctionStmt(null, functionStmtContext, symbols);
				}
				// 过程声明
				SubStmtContext subStmtContext = moduleBodyElementContext.subStmt();
				if (subStmtContext != null) {
					//处理过程声明
					processSubStmt(null, subStmtContext, symbols);
				}
			}
		}

		return symbols;
	}
	/**
	 * Function函数声明
	 * @param parent
	 * @param functionStmtContext
	 * @param symbols
	 */
	private void processFunctionStmt(VbaSymbol parent, FunctionStmtContext functionStmtContext,
			List<VbaSymbol> symbols) {
		//创建字符串变量，用于存储参数类型
		StringBuilder para = new StringBuilder();
		int argLength=0;
		//创建字符串kind,用于判断当前方法访问修饰符（Public、Private）
		String kind = null;
		para.append('(');
		//1、获取Function()函数内容，例如 Class1_OnError(ByVal Number As Long, ByVal Description As String)
		String funName = functionStmtContext.ambiguousIdentifier().getText();
		//2、获取函数参数内容，比如ByVal Number As Long, ByVal Description As String
		ArgListContext argListContext = functionStmtContext.argList();
		//3、判断方法访问修饰符是否为空，若不为空取之放在kind变量中，若为空则kind置空
		if (functionStmtContext.visibility()!=null) {
			kind = functionStmtContext.visibility().getText();
		}else {
			kind = null;
		}
		//4、判断方法参数是否为空，若不为空进一步循环获取参数类型并判断是否为空，若不为空则获取出参数类型存储在para中
		if (!argListContext.arg().isEmpty()) {
			//获取参数存储在链表argContexts中
			List<ArgContext> argContexts = argListContext.arg();
			//循环获取每个参数类型，例如 Long,String
			for (ArgContext argContext : argContexts) {
				//首先判断参数类型是否存在，若为空argLength++,若不为空则取出参数类型并加逗号添加到para中,即 Long,
				if (argContext.asTypeClause()!=null) {
					AsTypeClauseContext asTypeClauseContext = argContext.asTypeClause();
					TypeContext typeContext = asTypeClauseContext.type();
					BaseTypeContext baseTypeContext = typeContext.baseType();
					para.append(baseTypeContext.getText() + ",");
				}else{
					argLength ++;
				}
			}
			//如果参数类型都为空，例如Class1_OnError(ByVal Number, ByVal Description),该条件满足，仅添加逗号到para中
			if (argLength == argContexts.size()) {
				para.append(",");
			}
			//当获取参数结束后，添加右括号结束
			para.setCharAt(para.length() - 1, ')');
		}else{
			//若判断方法参数为空，直接添加右括号结束
			para.append(")");
		}
		//5、通过自定义的FunctionSymbol函数在网页显示提取结果
		FunctionSymbol functionSymbol = new FunctionSymbol(parent, funName + para,
				Utils.getTokenPosition(functionStmtContext.ambiguousIdentifier().IDENTIFIER(0).getSymbol()),
				Utils.getTokenPosition(functionStmtContext.getStart(), functionStmtContext.getStop()),kind);
		symbols.add(functionSymbol);
	}

	/**
	 * Sub过程声明
	 * @param parent
	 * @param subStmtContext
	 * @param symbols
	 */
	private void processSubStmt(VbaSymbol parent, SubStmtContext subStmtContext, List<VbaSymbol> symbols) {
		//创建字符串变量，用于存储参数类型
		StringBuilder para = new StringBuilder();
		StringBuilder capacity = new StringBuilder();
		int argLength = 0;
		//创建字符串kind,用于判断当前方法访问修饰符（Public、Private）
		String kind = null;
		para.append("(");
		capacity.append("a");
		//1、获取Sub()过程内容，例如CommandButton1_Click(ByVal Description as String)  
		String subName = subStmtContext.ambiguousIdentifier().getText();
		//2、获取过程参数内容，比如ByVal Number As Long, ByVal Description As String
		ArgListContext argListContext = subStmtContext.argList();
		//3、判断过程访问修饰符是否为空，若不为空取之放在kind变量中，若为空则kind置空
		if (subStmtContext.visibility()!= null) {
			kind = subStmtContext.visibility().getText();
			capacity.append("b");
		}else {
			kind = null;
			capacity.append("c");
		}
		//4、判断过程参数是否为空，若不为空进一步循环获取参数类型并判断是否为空，若不为空则获取出参数类型存储在para中
		if (!argListContext.arg().isEmpty()) {
			capacity.append("d");
			//获取参数存储在链表argContexts中
			List<ArgContext> arg = argListContext.arg();
			//循环获取每个参数类型，例如String,
			for (ArgContext argContext : arg) {
				//首先判断参数类型是否存在，若为空argLength++,若不为空则取出参数类型并加逗号添加到para中,即 String,
				if (argContext.asTypeClause() != null) {
					capacity.append("f");
					AsTypeClauseContext asTypeClause = argContext.asTypeClause();
					TypeContext type = asTypeClause.type();
					BaseTypeContext baseType = type.baseType();
					para.append(baseType.getText() + ",");
				}else {
					argLength++;
					capacity.append("g");
				}
				//如果参数类型都为空，例如CommandButton1_Click(ByVal Description),该条件满足，仅添加逗号到para中
				if (argLength == arg.size()) {
					para.append(",");
					capacity.append("i");
				}else{
					capacity.append("h");
				}
				capacity.append("k");
			}
			capacity.setCharAt(capacity.length()-1, 'j');
			//当获取参数结束后，添加右括号结束
			para.setCharAt(para.length()-1, ')');
		}else {
			//若判断方法参数为空，直接添加右括号结束
			para.append(")");
			capacity.append("e");
		}
		//5、通过自定义的FunctionSymbol函数在网页显示提取结果
		SubSymbol subSymbol = new SubSymbol(parent, subName + para,
				Utils.getTokenPosition(subStmtContext.ambiguousIdentifier().IDENTIFIER(0).getSymbol()),
				Utils.getTokenPosition(subStmtContext.getStart(), subStmtContext.getStop()),kind,capacity.toString());
		symbols.add(subSymbol);
	}

	@Override
	public boolean accept(String fileName) {
		// TODO Auto-generated method stub
		return acceptExtensions(fileName, "bas");
	}

	@Override
	public int getVersion() {
		// TODO Auto-generated method stub
		return 0;
	}

}
