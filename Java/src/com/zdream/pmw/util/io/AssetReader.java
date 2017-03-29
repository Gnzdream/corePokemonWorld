package com.zdream.pmw.util.io;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * 读取 Asset 中的文件<br>
 * 
 * @since v0.2.1
 * @author Zdream
 * @date 2017年3月22日
 * @version v0.2.1
 */
public class AssetReader {

	/**
	 * 读取 assets/ 下的文件<br>
	 * @param assetPath
	 *   "assets/" 下的目录, 开头没有斜杠
	 * @param capacity
	 *   估计文字量的大小. 仅仅会对性能有影响, 但对结果没有影响
	 * @return
	 */
	public static StringBuilder read(String assetPath, int capacity) {
		try {
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(new FileInputStream("assets/" + assetPath), "UTF-8"));
			char[] chs = new char[1000];
			StringBuilder builder = new StringBuilder(capacity);
			
			int len;
			while ((len = reader.read(chs)) != -1) {
				builder.append(chs, 0, len);
			}
			reader.close();
			
			return builder;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static void main(String[] args) {
		StringBuilder b = read("data/static/translate-meaning.log", 2000);
		System.out.println("length: " + b.length());
		System.out.println();
		System.out.println(b.toString());
	}
	
}
