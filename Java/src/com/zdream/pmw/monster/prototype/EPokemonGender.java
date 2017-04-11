package com.zdream.pmw.monster.prototype;

/**
 * 精灵（玩家）的性别<br>
 * <p>The Gender of Pokemon and Trainer.</p>
 * 
 * @since v0.1
 * @author Zdream
 * @date 2016年3月16日
 * @version v0.2
 */
public enum EPokemonGender {
	/**
	 * 男性 / 雄性, Male
	 */
	M, 
	
	/**
	 * 女性 / 雌性, Female
	 */
	F, 
	
	/**
	 * 无性别, Unknowed
	 */
	N;
	
	public static EPokemonGender parseEnum(String value) {
		for (EPokemonGender s : EPokemonGender.values()) {
			if (s.name().equalsIgnoreCase(value)){
				return s;
			}
		}
		return null;
	}
	
	public static EPokemonGender parseEnum(int value) {
		return EPokemonGender.values()[value];
	}
}
