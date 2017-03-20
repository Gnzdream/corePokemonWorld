package com.zdream.pmw.monster.prototype;

/**
 * 精灵（玩家）的性别<br>
 * 
 * @since v0.1
 * @author Zdream
 * @date 2016年3月16日
 * @version v0.2
 */
public enum EPokemonGender {
	/**
	 * 男性 / 雄性
	 */
	M, 
	
	/**
	 * 女性 / 雌性
	 */
	F, 
	
	/**
	 * 无性别
	 */
	N;
	
	public EPokemonGender parseEnum(String value) {
		for (EPokemonGender s : EPokemonGender.values()) {
			if (s.name().equalsIgnoreCase(value)){
				return s;
			}
		}
		return null;
	}
	
	public EPokemonGender parseEnum(int value) {
		return EPokemonGender.values()[value];
	}
}
