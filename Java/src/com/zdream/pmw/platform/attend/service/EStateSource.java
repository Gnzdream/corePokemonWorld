package com.zdream.pmw.platform.attend.service;

/**
 * 表明一个状态是由于什么而产生的<br>
 * 在游戏中，一个状态可能是由技能、特性、携带或使用的道具产生的<br>
 * 而各种不同的产生源头产生的状态触发时，它的发动形式和特效可能有比较大的区别<br>
 * 
 * @since v0.1
 * @author Zdream
 * @date 2016年4月12日
 * @version v0.1
 */
public enum EStateSource {
	
	/**
	 * 由未知的源头产生的<br>
	 * 默认值，其中刚进入战斗时，如果有精灵正处于异常状态<br>
	 * 那么该状态的源头很有可能是 <code>OTHER</code> 对象<br>
	 */
	OTHER,
	/**
	 * 表明该状态由自己或其它精灵释放的技能所追加的
	 */
	SKILL,
	/**
	 * 表明该状态由自己或其它精灵的特性发动所追加的
	 */
	ABILITY,
	/**
	 * 表明该状态由自己或其它精灵的携带道具、战斗使用道具所追加的
	 */
	ITEM;
	
	public static EStateSource parseEnum(String value) {
		for (EStateSource e : values()) {
			if (e.name().equalsIgnoreCase(value)) {
				return e;
			}
		}
		return OTHER;
	}
	
	public static EStateSource parseEnum(int value) {
		return EStateSource.values()[value];
	}

}
