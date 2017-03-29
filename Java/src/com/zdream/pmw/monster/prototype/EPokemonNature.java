package com.zdream.pmw.monster.prototype;

/**
 * 精灵性格<br>
 * <p>the nature of Pokemon</p>
 * 
 * @since v0.1
 * @author Zdream
 * @date 2016年3月14日
 * @version v0.2
 */
public enum EPokemonNature implements IPokemonDataType {
	HARDY,
	LONELY,
	BRAVE,
	ADAMANT,
	NAUGHTY,
	BOLD,
	DOCILE,
	RELAXED,
	IMPISH,
	LAX,
	TIMID,
	HASTY,
	SERIOUS,
	JOLLY,
	NAIVE,
	MODEST,
	MILD,
	QUIET,
	BASHFUL,
	RASH,
	CALM,
	GENTLE,
	SASSY,
	CAREFUL,
	QUIRKY;
	
	public static EPokemonNature parseEnum(String value) {
		for (EPokemonNature s : EPokemonNature.values()) {
			if (s.name().equalsIgnoreCase(value)){
				return s;
			}
		}
		return null;
	}
}
