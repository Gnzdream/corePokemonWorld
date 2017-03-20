package com.zdream.pmw.monster.prototype;

/**
 * 精灵的数据的固定值<br>
 * 
 * @since v0.1
 * @author Zdream
 * @date 2016年3月14日
 * @version v0.2
 */
public interface IPokemonDataType {

	// Pokemon 
	public static final int HP = 0;
	public static final int AT = 1;
	public static final int DF = 2;
	public static final int SA = 3;
	public static final int SD = 4;
	public static final int SP = 5;
	
	// PokemonOnFight
	public static final int TG = 6;//命中
	public static final int HD = 7;//躲避
	public static final int LEVEL_UPPER_LIMIT = 6;
	public static final int LEVEL_LOWER_LIMIT = -6;
}
