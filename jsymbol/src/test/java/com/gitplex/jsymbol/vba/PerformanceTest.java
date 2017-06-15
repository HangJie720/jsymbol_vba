package com.gitplex.jsymbol.vba;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.Token;
import org.junit.Test;

import com.gitplex.jsymbol.vba.symbols.VbaSymbol;

public class PerformanceTest {
	private List<String> fileContentList = new LinkedList<>();
	private List<VbaSymbol> symbols;
	
	public static void main(String[] args) throws IOException {
		new PerformanceTest().process();
	}
	
	@Test
	public void process() throws IOException{
		//read file
		long start = System.currentTimeMillis();
		this.traverseFolder(new File("SmartQQCode"));
		long end = System.currentTimeMillis();
		System.out.println("readfile time cost: " + (end - start) + "ms");
		System.out.println("file num:" + fileContentList.size());
		
		//extract
		VbaExtractor vbaExtractor = new VbaExtractor();
		int symbolCount = 0;
		start = System.currentTimeMillis();
		for(String fileContent : fileContentList){
			vbaLexer vbalexer = new vbaLexer(new ANTLRInputStream(fileContent));
			Token token = vbalexer.nextToken();
			while (token.getType() != Token.EOF) {
				token = vbalexer.nextToken();
			}
			//symbols = vbaExtractor.extract(null, fileContent);
			symbolCount++;
		}
		end = System.currentTimeMillis();
		System.out.println("average extract time cost: " + (end-start)/symbolCount + "ms");
		System.out.println("symbol num:" + symbolCount);
	}
	
	
	private void traverseFolder(File src){
		for (File file : src.listFiles()) {
			if (file.isDirectory()) {
				traverseFolder(file);
			}else if (file.getName().endsWith(".bas")) {
				fileContentList.add(readToString(file));
			}
		}
	}

	private String readToString(File file) {
		if (file == null || !file.canRead()) {
			return "";
		}
		Long filelength = file.length();
		byte[] fileContent = new byte[filelength.intValue()];
		try {
			FileInputStream inputStream = new FileInputStream(file);
			inputStream.read(fileContent);
			inputStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return new String(fileContent, StandardCharsets.UTF_8);
		
	}
}
