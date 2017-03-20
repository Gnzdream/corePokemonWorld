package com.zdream.pmw.platform.attend;

/**
 * 技能的攻击范围枚举<br>
 * <br>
 * <b>v0.1.1</b><br>
 *   该类移动到 AM 模块中<br>
 * 
 * @since v0.1.1
 * @author Zdream
 * @date 2016年4月2日
 * @version v0.1.1
 */
public enum ESkillRange {
	/**
	 * 单体地方攻击
	 */
	SINGLE,
	
	/**
	 * 对方全体攻击
	 */
	TEAM,
	
	/**
	 * 周围全体攻击
	 */
	ALL,
	
	/**
	 * 自身
	 */
	SELF;
	
	public static ESkillRange parseEnum(String value) {
		for (ESkillRange s : ESkillRange.values()) {
			if (s.name().equalsIgnoreCase(value)){
				return s;
			}
		}
		return null;
	}
	
	public static ESkillRange parseEnum(int value) {
		return ESkillRange.values()[value];
	}
}
