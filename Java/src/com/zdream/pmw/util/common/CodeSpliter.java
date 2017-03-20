package com.zdream.pmw.util.common;

import java.util.ArrayList;
import java.util.List;

/**
 * 根据命令行的方式将字符串切开<br>
 * 
 * @since v0.1
 * @author Zdream
 * @date 2016年4月16日
 * @version v0.1
 */
public class CodeSpliter {
	
	public static String[] split(String str) {
		List<String> list = new ArrayList<String>();
		char[] chars = str.toCharArray();
		
		int index = 0; // chars 的索引
		int size = str.length(); // chars 的总长度
		
		int head = 0; // 子字符串的开头索引
		int tail = 0; // 子字符串的结尾索引
		int status = 0;
		
		for (; index < size; index++) {
			char ch = chars[index];
			switch (status) {
			case 0: // 没有开始检索
				if (Character.isSpaceChar(ch)) {
					continue;
				} else if (ch == '\"') {
					head = index;
					status = 3;
				} else {
					head = index;
					status = 1;
				}
				break;
				
			case 1: // 已经找到头（非双引号开头的）
				if (Character.isSpaceChar(ch)) {
					// 可以截断
					tail = index;
					list.add(new String(chars, head, tail - head));
					status = 0;
				} else {
					continue;
				}
				
			case 3: // 已经找到头（双引号开头的）
				if (ch == '\"') {
					if (index > 0 && chars[index - 1] != '\\') {
						// 可以截断
						tail = index;
						String ss = new String(chars, head + 1, tail - head - 1);
						ss.replace("\\\"", "\"");
						list.add(ss);
						status = 0;
					}
				} else {
					continue;
				}
			}
		}
		
		if (status == 1) {
			tail = index;
			list.add(new String(chars, head, tail - head));
		}
		
		return list.toArray(new String[list.size()]);
	}
	
	/*@Test
	public void testSplit() {
		String str = "login \"a sd\"";
		String[] ss = split(str);
		Assert.assertEquals(2, ss.length);
		if (ss.length == 2) {
			Assert.assertEquals(ss[0], "login");
			Assert.assertEquals(ss[1], "a sd");
		}
		
		str = "\"6 g好\" -b \"a \\as\\\" sd\"";
		ss = split(str);
		Assert.assertEquals(3, ss.length);
		if (ss.length == 3) {
			Assert.assertEquals(ss[0], "6 g好");
			Assert.assertEquals(ss[1], "-b");
			Assert.assertEquals(ss[2], "a \\as\\\" sd");
		}
	}*/
	
}
