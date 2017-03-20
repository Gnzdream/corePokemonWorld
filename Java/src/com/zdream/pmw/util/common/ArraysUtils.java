package com.zdream.pmw.util.common;

/**
 * 数组的通用工具<br>
 * <br>
 * <b>v0.1.1</b><br>
 *   添加 copyArray 方法<br>
 * 
 * @since v0.1.1
 * @author Zdream
 * @date 2016年4月7日
 * @version v0.1.1
 */
public class ArraysUtils {
	
	/**
	 * 将 byte 数组转化成字符串
	 * @param arrays
	 * @return
	 */
	public static String bytesToString(byte[] arrays) {
		if (arrays.length == 0) {
			return "[]";
		}
		
		StringBuilder builder = new StringBuilder(arrays.length * 3 + 2);
		builder.append('[');
		int v = arrays.length - 1;
		for (int i = 0; i < v; i++) {
			builder.append(arrays[i]).append(',');
		}
		builder.append(arrays[v]).append(']');
		return builder.toString();
	}
	
	/**
	 * 将 [1,2,3,4,5] 类似的字符串转成相应的 byte 数组
	 * @param str
	 * @return
	 */
	public static byte[] StringToBytes(String str) {
		String[] ss = str.substring(1, str.length() - 1).split(",");
		if (ss.length == 1 && ss[0].equals("")) {
			return new byte[0];
		}
		
		byte[] bytes = new byte[ss.length];
		for (int i = 0; i < ss.length; i++) {
			bytes[i] = Byte.parseByte(ss[i]);
		}
		return bytes;
	}
	
	/**
	 * byte 数组拷贝到一定长度
	 * @param src
	 * @param length
	 * @return
	 */
	public static byte[] copyArray(byte[] src, int length) {
		if (src.length == length) {
			return src;
		} else {
			byte[] dest = new byte[length];
			System.arraycopy(src, 0, dest, 0, length);
			return dest;
		}
	}

}