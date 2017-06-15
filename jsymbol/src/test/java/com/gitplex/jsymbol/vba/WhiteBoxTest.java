package com.gitplex.jsymbol.vba;

import java.util.List;

import org.junit.Test;

import com.gitplex.jsymbol.DescriptableExtractorTest;
import com.gitplex.jsymbol.vba.vbaParser.ArgListContext;
import com.gitplex.jsymbol.vba.vbaParser.SubStmtContext;
import com.gitplex.jsymbol.vba.symbols.FunctionSymbol;
import com.gitplex.jsymbol.vba.symbols.SubSymbol;
import com.gitplex.jsymbol.vba.symbols.VbaSymbol;

public class WhiteBoxTest extends DescriptableExtractorTest<VbaSymbol>{
	
	private String capacity;

	/**
	 * 1.语句覆盖
	 */
	@Test
	public void test1(){
		verify(readFile("test1.outline"), new VbaExtractor().extract(null, readFile("test1.source")));
	}
	
	/**
	 * 2.判定覆盖(由于每个判定只有一个条件，故条件覆盖、条件组合覆盖雷同)
	 */
	//TestCase 1  R1、R2、R3、R4、R5 ---》a b d f h j
	@Test
	public void test2(){
		verify(readFile("test2.outline"), new VbaExtractor().extract(null, readFile("test2.source")));
	}
	//TestCase 2  -R1、-R2 ---》a c e
	@Test
	public void test3(){
		verify(readFile("test3.outline"), new VbaExtractor().extract(null, readFile("test3.source")));
	}
	//TestCase 3  R1、R2、-R3、-R4、R5 ---》a b d g i j
	@Test
	public void test4(){
		verify(readFile("test4.outline"), new VbaExtractor().extract(null, readFile("test4.source")));
	}
	//TestCase 4  R1、R2、R3、R4、-R5、R3、R4、R5 ---》a b d f h k f h j
	@Test
	public void test5(){
		verify(readFile("test5.outline"), new VbaExtractor().extract(null, readFile("test5.source")));
	}

	/**
	 * 3.路径覆盖
	 */
	@Test
	public void test6(){
		verify(readFile("test6.outline"), new VbaExtractor().extract(null, readFile("test6.source")));
	}

	@Test
	public void test7(){
		verify(readFile("test7.outline"), new VbaExtractor().extract(null, readFile("test7.source")));
	}
	@Test
	public void test8(){
		verify(readFile("test8.outline"), new VbaExtractor().extract(null, readFile("test8.source")));
	}
	@Test
	public void test9(){
		verify(readFile("test9.outline"), new VbaExtractor().extract(null, readFile("test9.source")));
	}
	@Test
	public void test10(){
		verify(readFile("test10.outline"), new VbaExtractor().extract(null, readFile("test10.source")));
	}
	@Test
	public void test11(){
		verify(readFile("test11.outline"), new VbaExtractor().extract(null, readFile("test11.source")));
	}
	@Test
	public void test12(){
		verify(readFile("test12.outline"), new VbaExtractor().extract(null, readFile("test12.source")));
	}
	
	@Override
	protected String describe(List<VbaSymbol> context, VbaSymbol symbol) {
		if (symbol instanceof SubSymbol) {
			SubSymbol subSymbol = (SubSymbol) symbol;
			capacity = subSymbol.getCapacity();
		}else {
			throw new RuntimeException("Unexpected symbol type: " + symbol.getClass());
		}
		return capacity;
	}
}
