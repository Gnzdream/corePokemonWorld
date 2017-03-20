package com.zdream.pmw.monster.skill;

/**
 * 技能的伤害类型<br>
 * <br>
 * <b>v0.2</b><br>
 *   变化技能命名从 variety 修正为 status<br>
 * 
 * @since v0.1
 * @author Zdream
 * @date 2016年3月16日
 * @version v0.2
 */
public enum ESkillCategory {
	/**
	 * 变化技能
	 */
	STATUS, 
	
	/**
	 * 物理技能
	 */
	PHYSICS, 
	
	/**
	 * 特殊技能
	 */
	SPECIAL;
	
	public static ESkillCategory parseEnum(String value) {
		for (ESkillCategory s : ESkillCategory.values()) {
			if (s.name().equalsIgnoreCase(value)){
				return s;
			}
		}
		return null;
	}
	
	public static ESkillCategory parseEnum(int value) {
		return ESkillCategory.values()[value];
	}
	
	
}
