package com.zdream.pmw.util.random;

import java.util.Random;

/**
 * 随机数方法<br>
 * 
 * @since v0.1
 * @author Zdream
 * @date 2016年1月1日 (不确定)
 * @version v0.1
 */
public final class RanValue {

	private final static int BASE = 65536;
	
	/**
	 * 随机数源
	 */
	private static Random ran = new Random();

	/**
	 * 获得一个随机整数，该随机数的值域为 [0, BASE)
	 * @return
	 */
	public static int random() {
		return ran.nextInt(BASE);
	}
	
	/**
	 * 获得一个随机整数，该随机数的值域为 [min, max)
	 * @param min
	 * @param max
	 * @return
	 */
	public static int random(int min, int max) {
		return ran.nextInt(max - min) + min;
	}

	/**
	 * 在 [0, max) 中随机获得一个整数 a，比较该 a 与给定阈值 standard 的大小
	 * @param standard
	 * @param max
	 * @return
	 *   当随机整数 a 大于 standard 时返回 true
	 */
	public static boolean isBigger(int standard, int max) {
		return ran.nextInt(max) > standard;
	}

	/**
	 * 在 [0, 100) 中随机获得一个整数 a，比较该 a 与给定阈值 standard 的大小
	 * @param standard
	 * @param max
	 * @return
	 *   当随机整数 a 大于 standard 时返回 true
	 */
	public static boolean isBigger(int standard) {
		return isBigger(standard, 100);
	}

	/**
	 * 在 [0, max) 中随机获得一个整数 a，比较该 a 与给定阈值 standard 的大小
	 * @param standard
	 * @param max
	 * @return
	 *   当随机整数 a 小于 standard 时返回 true
	 */
	public static boolean isSmaller(int standard, int max) {
		return ran.nextInt(max) < standard;
	}

	/**
	 * 在 [0, 100) 中随机获得一个整数 a，比较该 a 与给定阈值 standard 的大小
	 * @param standard
	 * @param max
	 * @return
	 *   当随机整数 a 小于 standard 时返回 true
	 */
	public static boolean isSmaller(int standard) {
		return isSmaller(standard, 100);
	}

}
