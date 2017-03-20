package com.zdream.pmw.monster.prototype;

/**
 * 精灵的异常状态<br>
 * 
 * @since v0.1
 * @author Zdream
 * @date: 2016年3月29日
 */
public enum EPokemonAbnormal {
	/**
	 * 无特殊状态
	 */
	NONE,
	/**
	 * 中毒
	 */
	POISON,
	/**
	 * 剧毒
	 */
	BADLY_POISON,
	/**
	 * 麻痹
	 */
	PARALYSIS,
	/**
	 * 睡眠
	 */
	SLEEP,
	/**
	 * 烧伤
	 */
	BURN,
	/**
	 * 冻伤
	 */
	FREEZE,
	/**
	 * 濒死
	 */
	FAINTING;
	
	public static EPokemonAbnormal parseEnum(String value) {
		return valueOf(EPokemonAbnormal.class, value);
	}
	
	public static EPokemonAbnormal parseEnum(int value) {
		return values()[value];
	}
}
