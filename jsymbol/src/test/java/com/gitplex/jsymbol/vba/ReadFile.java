package com.gitplex.jsymbol.vba;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;


public class ReadFile {
	
	//private static StringBuffer filecontext = new StringBuffer();
	private static List<String> contexts =new ArrayList<String>();
	private static int count;
	
	public ReadFile() {
	}

	/**
	 * 读取某个文件夹下的所有文件
	 */
	public static void readfile(String filepath)
			throws FileNotFoundException, IOException {
		StringBuffer filecontext = new StringBuffer();
		try {
			File file = new File(filepath);
			if (!file.isDirectory()) {
				if (file.getName().endsWith(".bas")) {
				//if (file.getName().indexOf("test") > -1) {
					System.out.println(file.getName());
					InputStreamReader read = new InputStreamReader(
							new FileInputStream(file), "UTF-8");// 考虑到编码格式
					BufferedReader bufferedReader = new BufferedReader(read);
					String lineTxt = null;
					while ((lineTxt = bufferedReader.readLine()) != null) {
						filecontext.append("\n" + lineTxt);
						count +=1;
					}
					read.close();
					contexts.add(file.getAbsolutePath());
					contexts.add(filecontext.toString()+"\n");
					//List<PythonSymbol> symbols = new PythonExtrator().extract(null, filecontext);
				}

			} else if (file.isDirectory()) {
				String[] filelist = file.list();
				for (int i = 0; i < filelist.length; i++) {
					File readfile = new File(filepath + "\\" + filelist[i]);
					if (!readfile.isDirectory()) {
						if (readfile.getName().endsWith(".bas")) {
						//if (readfile.getName().indexOf("test") > -1) {
							System.out.println(readfile.getName());
							InputStreamReader read = new InputStreamReader(
									new FileInputStream(readfile), "UTF-8");// 考虑到编码格式
							BufferedReader bufferedReader = new BufferedReader(read);
							String lineTxt = null;
							while ((lineTxt = bufferedReader.readLine()) != null) {
								filecontext.append("\n" + lineTxt);
								count +=1;
							}
							read.close();
							contexts.add(readfile.getAbsolutePath());
							contexts.add(filecontext.toString()+"\n");
							System.out.println(readfile.getAbsolutePath());
							//List<PythonSymbol> symbols = new PythonExtrator().extract(null, filecontext);
						}
					} else if (readfile.isDirectory()) {
						readfile(filepath + "\\" + filelist[i]);
					}
				}

			}

		} catch (FileNotFoundException e) {
			System.out.println("readfile()   Exception:" + e.getMessage());
		}
		//return filecontext;
	}

	public static String getContext(String filepath) throws FileNotFoundException, IOException{
		StringBuffer filecontext = new StringBuffer();
		try {
			
			File file = new File(filepath);
			if (!file.isDirectory()) {
				if (file.getName().endsWith(".bas")) {
				//if (file.getName().indexOf("test") > -1) {
					InputStreamReader read = new InputStreamReader(
							new FileInputStream(file), "UTF-8");// 考虑到编码格式
					BufferedReader bufferedReader = new BufferedReader(read);
					String lineTxt = null;
					while ((lineTxt = bufferedReader.readLine()) != null) {
						filecontext.append("\n" + lineTxt);
					}
					read.close();
				}

			} else if (file.isDirectory()) {
				String[] filelist = file.list();
				for (int i = 0; i < filelist.length; i++) {
					File readfile = new File(filepath + "\\" + filelist[i]);
					if (!readfile.isDirectory()) {
						if (readfile.getName().endsWith(".bas")) {
						//if (readfile.getName().indexOf("test") > -1) {
							InputStreamReader read = new InputStreamReader(
									new FileInputStream(readfile), "UTF-8");// 考虑到编码格式
							BufferedReader bufferedReader = new BufferedReader(read);
							String lineTxt = null;
							while ((lineTxt = bufferedReader.readLine()) != null) {
								filecontext.append("\n" + lineTxt);
							}
							read.close();
						}
					} else if (readfile.isDirectory()) {
						getContext(filepath + "\\" + filelist[i]);
					}
				}

			}

		} catch (FileNotFoundException e) {
			System.out.println("readfile()   Exception:" + e.getMessage());
		}
		
		return filecontext.toString();
	}
	
	/**
	 * 删除某个文件夹下的所有文件夹和文件
	 */

	/*
	 * public static boolean deletefile(String delpath) throws
	 * FileNotFoundException, IOException { try {
	 * 
	 * File file = new File(delpath); if (!file.isDirectory()) {
	 * System.out.println("1"); file.delete(); } else if (file.isDirectory()) {
	 * System.out.println("2"); String[] filelist = file.list(); for (int i = 0;
	 * i < filelist.length; i++) { File delfile = new File(delpath + "\\" +
	 * filelist[i]); if (!delfile.isDirectory()) { System.out.println("path=" +
	 * delfile.getPath()); System.out.println("absolutepath=" +
	 * delfile.getAbsolutePath()); System.out.println("name=" +
	 * delfile.getName()); delfile.delete(); System.out.println("删除文件成功"); }
	 * else if (delfile.isDirectory()) { deletefile(delpath + "\\" +
	 * filelist[i]); } } file.delete();
	 * 
	 * }
	 * 
	 * } catch (FileNotFoundException e) {
	 * System.out.println("deletefile()   Exception:" + e.getMessage()); }
	 * return true; }
	 */

	public static void WriteStringToFile(String filePath, String context) {
        try {
            File file = new File(filePath);
            PrintStream ps = new PrintStream(new FileOutputStream(file));
            ps.println(context);// 往文件里写入字符串
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

	public static int getCount() {
		return count;
	}

	public static void setCount(int count) {
		ReadFile.count = count;
	}

	public static List<String> getContexts() {
		return contexts;
	}

	public static void setContexts(List<String> contexts) {
		ReadFile.contexts = contexts;
	}
}
