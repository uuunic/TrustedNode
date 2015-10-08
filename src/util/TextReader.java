package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class TextReader {
	public static String read(String fileName) {
		StringBuilder sb = new StringBuilder();
		try {
			BufferedReader in = new BufferedReader(new FileReader(new File(fileName).getAbsolutePath()));
			try {
				String s;
				while((s = in.readLine()) != null) {
					sb.append(s);
					sb.append("\n");
				}
			} finally {
				in.close();
			}
		} catch(IOException e) {
			e.printStackTrace();
		}
		return sb.toString();
	}
	/**
	 * 读取配置文件
	 * @param String filename配置文件的名字
	 * @return String[][] info2
	 * 以空格分割，格式为: IP Name;
	 * 
	 * **/
	public static String[][] readConfig(String fileName) {
		StringBuilder sb = new StringBuilder();
		try {
			BufferedReader in = new BufferedReader(new FileReader(new File(fileName).getAbsolutePath()));
			try {
				String s;
				while((s = in.readLine()) != null) {
					sb.append(s);
				}
			} finally {
				in.close();
			}
		} catch(IOException e) {
			e.printStackTrace();
		}
		String[] info = sb.toString().split(";");
		String[][] info2 = new String[info.length][] ;
		for(int i=0; i<info.length; i++){
			info2[i] = info[i].split(" "); 
		}
		return info2;
	}
	public static void main(String[] args) {
		String[][] config = TextReader.readConfig("config.txt");
		for(int i=0; i< config.length; i++){
			for(int j=0; j<config[0].length; j++){
				System.out.print(config[i][j] + " ");
			}
			System.out.println();
		}
	}
}
