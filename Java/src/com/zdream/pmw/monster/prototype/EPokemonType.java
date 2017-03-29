package com.zdream.pmw.monster.prototype;

/**
 * 精灵属性<br>
 * <p>The type</p>
 * 
 * @since v0.1
 * @author Zdream
 * @date 2016年3月16日
 * @version v0.2
 */
public enum EPokemonType {
	None,
	Normal, Fighting, Flying, Poison, Ground, Rock,
	Bug, Ghost, Steel, Fire, Water, Grass,
	Electric, Psychic, Ice, Dragon, Dark, Fairy;
	
	public static EPokemonType parseEnum(String value) {
		for (EPokemonType s : EPokemonType.values()) {
			if (s.name().equalsIgnoreCase(value)){
				return s;
			}
		}
		return None;
	}
	
	public static EPokemonType parseEnum(int value) {
		if (value < 1 || value >= EPokemonType.values().length) {
			return None;
		}
		return EPokemonType.values()[value];
	}
}
