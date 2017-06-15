package com.gitplex.jsymbol.vba;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.junit.Test;

import com.gitplex.jsymbol.DescriptableExtractorTest;
import com.gitplex.jsymbol.vba.symbols.FunctionSymbol;
import com.gitplex.jsymbol.vba.symbols.SubSymbol;
import com.gitplex.jsymbol.vba.symbols.VbaSymbol;

public class VbaExtractorTest extends DescriptableExtractorTest<VbaSymbol>{
	
	private String path = "C:\\Users\\JackHay\\workspace\\jsymbol\\example";
	
	/*@Test
	public void Test(){
		verify(readFile("test.outline"), new VbaExtractor().extract(null, readFile("test.source")));
	}*/

	@Override
	protected String describe(List<VbaSymbol> context, VbaSymbol symbol) {
		StringBuilder builder = new StringBuilder();
		if (symbol instanceof SubSymbol) {
			SubSymbol subSymbol = (SubSymbol) symbol;
			if (subSymbol.getKind()!=null) {
				builder.append(subSymbol.getKind()).append(" ");
			}
			builder.append("Sub ");
			builder.append(subSymbol.getName()+"\n");
			builder.append("End Sub");
		}else if (symbol instanceof FunctionSymbol) {
			FunctionSymbol functionSymbol = (FunctionSymbol) symbol;
			if (functionSymbol.getKind() != null) {
				builder.append(functionSymbol.getKind()).append(" ");
			}
			builder.append("Function ");
			builder.append(functionSymbol.getName()+"\n");
			builder.append("End Function");
		}else {
			throw new RuntimeException("Unexpected symbol type: " + symbol.getClass());
		}
		appendChildren(builder, context, symbol);
		return builder.toString();
	}
	
	@Test
	public void VbaExtractor1() throws FileNotFoundException, IOException{
		ReadFile.readfile(path);
		List<String> context = ReadFile.getContexts();
		System.out.println(ReadFile.getCount());
		long startTime = System.currentTimeMillis();//获取开始时间
		for(int i = 0; i < context.size();i += 2){
			System.out.println(context.get(i));
			vbaLexer vbalexer = new vbaLexer(new ANTLRInputStream(context.get(i+1)));
			CommonTokenStream stream = new CommonTokenStream(vbalexer);
			vbaParser vbaparser = new vbaParser(stream);
			vbaparser.startRule();
			/*List<VbaSymbol> symbols = new VbaExtractor().extract(null, context.get(i+1));
			
			Token token = vbalexer.nextToken();
			while (token.getType() != Token.EOF) {
				token = vbalexer.nextToken();
			}*/
			
			long endTime = System.currentTimeMillis(); // 获取结束时间
			System.out.println("程序运行时间： " + (endTime - startTime) + "ms");
			System.out.println("平均时间： "
					+ ((endTime - startTime) / (context.size() / 2)) + "ms");
			System.out.println("文件数" + context.size() / 2);
			// System.out.println(account);
		}
	}
	
	/*@Test
	public void VbaExtractor2() throws FileNotFoundException, IOException{
		ReadFile.readfile(path);
		List<String> context = ReadFile.getContexts();
		System.out.println(ReadFile.getCount());
		long startTime = System.currentTimeMillis();//获取开始时间
		for (int j = 0; j < 100; j++) {
			for(int i = 0; i < context.size();i += 2){
				System.out.println(context.get(i));
				vbaLexer vbalexer = new vbaLexer(new ANTLRInputStream(context.get(i+1)));
				CommonTokenStream stream = new CommonTokenStream(vbalexer);
				vbaParser vbaparser = new vbaParser(stream);
				vbaparser.startRule();
				List<VbaSymbol> symbols = new VbaExtractor().extract(null, context.get(i+1));
				
				Token token = vbalexer.nextToken();
				while (token.getType() != Token.EOF) {
					token = vbalexer.nextToken();
				}
			}
		}
			long endTime = System.currentTimeMillis(); // 获取结束时间
			System.out.println("程序运行时间： " + (endTime - startTime) + "ms");
			System.out.println("平均时间： "
					+ ((endTime - startTime) / (context.size() / 2)) + "ms");
			System.out.println("文件数" + (context.size() / 2)*100);
			// System.out.println(account);
		
	}*/
	
	@Test
	public void getContext() throws FileNotFoundException, IOException {
		String context = ReadFile.getContext(path);
		System.out.println(context);
	}

}
