package com.gitplex.jsymbol;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;

import com.google.common.base.Splitter;
import com.google.common.io.Resources;

public abstract class DescriptableExtractorTest<T extends Symbol> {

	/**
	 * Describe specified symbol in specified context
	 * 
	 * @param context
	 * 			context to describe the symbol in. Non-leaf symbols will use context to describe 
	 * 			itself and all its child symbols. For instance, a Java class symbol description
	 * 			contains not only the class definition, but also all declaration symbols inside 
	 * 			the class including fields and methods. In this case, it will look inside the 
	 * 			context for symbols with parent pointing to it
	 * @param symbol
	 * 			symbol to be described. Non-leaf symbol should also describe its child symbols  
	 * 		
	 * @return
	 * 			Description of the symbol
	 */
	protected abstract String describe(List<T> context, T symbol);

	/**
	 * This method describes the list of symbols into a string and compares it with expected string
	 */
	protected void verify(String expected, List<T> symbols) {
		StringBuilder builder = new StringBuilder();
		for (T symbol: symbols) {
			if (symbol.getParent() == null)
				builder.append(describe(symbols, symbol));
		}
		
		Assert.assertEquals(StringUtils.replace(expected, "\r", "").trim(), builder.toString().toString().trim());
	}

	protected String readFile(String fileName) {
		try {
			return Resources.toString(Resources.getResource(getClass(), fileName), Charset.forName("UTF8"));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	protected void appendChildren(StringBuilder builder, List<T> context, T symbol) {
		List<T> children = new ArrayList<>();
		for (T each: context) {
			if (each.getParent() == symbol)
				children.add(each);
		}
		if (!children.isEmpty()) {
			builder.append(" {\n");
			for (T child: children) {
				for (String line: Splitter.on("\n").omitEmptyStrings().split(describe(context, child))) {
					builder.append("  ").append(line).append("\n");
				}
			}
			builder.append("}");
		}
		builder.append("\n");
	}
	
}
