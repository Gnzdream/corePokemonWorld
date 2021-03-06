package com.zdream.pmw.core.tools;

import com.zdream.pmw.monster.prototype.EPokemonType;

/**
 * <p>属性克制表
 * <p><b>v0.2.3</b><br>
 * 该类从 <code>com.zdream.pmw.platform.effect.type</code> 包中移动过来</p>
 * 
 * @since v0.2.3 [2017-04-23]
 * @author Zdream
 * @version v0.2.3 [2017-04-23]
 */
public class TypeRestraint {
	
	private static final byte[][] TABLE = new byte[][]{
		{2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2},
		{2,2,2,2,2,2,1,2,0,1,2,2,2,2,2,2,2,2,2},
		{2,3,2,1,1,2,3,1,0,3,2,2,2,2,1,3,2,3,1},
		{2,2,3,2,2,2,1,3,2,1,2,2,3,1,2,2,2,2,2},
		{2,2,2,2,1,1,1,2,1,0,2,2,3,2,2,2,2,2,3},
		{2,2,2,0,3,2,3,1,2,3,3,2,1,3,2,2,2,2,2},
		{2,2,1,3,2,1,2,3,2,1,3,2,2,2,2,3,2,2,2},
		{2,2,1,1,1,2,2,2,1,1,1,2,3,2,3,2,2,3,1},
		{2,0,2,2,2,2,2,2,3,2,2,2,2,2,3,2,2,1,2},
		{2,2,2,2,2,2,3,2,2,1,1,1,2,1,2,3,2,2,3},
		{2,2,2,2,2,2,1,3,2,3,1,1,3,2,2,3,1,2,2},
		{2,2,2,2,2,3,3,2,2,2,3,1,1,2,2,2,1,2,2},
		{2,2,2,1,1,3,3,1,2,1,1,3,1,2,2,2,1,2,2},
		{2,2,2,3,2,0,2,2,2,2,2,3,1,1,2,2,1,2,2},
		{2,2,3,2,3,2,2,2,2,1,2,2,2,2,1,2,2,0,2},
		{2,2,2,3,2,3,2,2,2,1,1,1,3,2,2,1,3,2,2},
		{2,2,2,2,2,2,2,2,2,1,2,2,2,2,2,2,3,2,0},
		{2,2,1,2,2,2,2,2,3,2,2,2,2,2,3,2,2,1,1},
		{2,2,3,2,1,2,2,2,2,1,1,2,2,2,2,2,3,3,2},
	};
	
	/**
	 * 获得属性克制倍率
	 * @param atType
	 *   攻击方属性
	 * @param dfType
	 *   防御方属性
	 * @return
	 */
	public static float getRestraintRate(EPokemonType atType, EPokemonType dfType) {
		byte value = TABLE[atType.ordinal()][dfType.ordinal()];
		
		switch (value) {
		case 0:
			return 0.0f;
		case 1:
			return 0.5f;
		case 3:
			return 2.0f;
		default:
			return 1.0f;
		}
	}
	
	/**
	 * 询问指定的两个属性攻击下是否免疫
	 * @param atType
	 *   攻击方属性
	 * @param dfType
	 *   防御方属性
	 * @return
	 *   返回防御方属性是否对攻击方属性免疫
	 */
	public static boolean isImmune(EPokemonType atType, EPokemonType dfType) {
		return TABLE[atType.ordinal()][dfType.ordinal()] == 0;
	}

}
