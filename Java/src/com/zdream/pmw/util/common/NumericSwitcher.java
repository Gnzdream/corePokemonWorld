package com.zdream.pmw.util.common;

/**
 * 数据进行按位转换的工具<br>
 * 
 * @since v0.1
 * @author Zdream
 * @date 2016年3月24日
 * @version v0.1
 */
public class NumericSwitcher {
	
	/**
	 * 获取原 short 类型数字的高八位，并截成 byte 类型数据
	 * @param value
	 * @return
	 */
	public static byte fetchHighFromShort(short value) {
		return (byte) (0x00FF & (value >>> 8));
	}
	
	/**
	 * 获取原 short 类型数字的低八位，并截成 byte 类型数据
	 * @param value
	 * @return
	 */
	public static byte fetchLowFromShort(short value) {
		return (byte) (0x00FF & value);
	}
	
	/**
	 * 将两个 byte 合并成一个 short 类型的数据
	 * @param high
	 *   成为 short 结果的高八位数据
	 * @param low
	 *   成为 short 结果的低八位数据
	 * @return
	 */
	public static short joinByteToShort(byte high, byte low) {
		return (short)(((high & 0x00FF) << 8) | (0x00FF & low));
	}
	
	/**
	 * 将 byte 类型的数据转成对应的无符号 short
	 * @param x
	 * @return
	 */
	public static short toUnsignedShort(byte x) {
		return (short)(((int) x) & 0xff);
	}
	
}
