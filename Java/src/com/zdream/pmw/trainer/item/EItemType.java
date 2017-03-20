package com.zdream.pmw.trainer.item;

/**
 * 道具的类型<br>
 * 
 * @since v0.1
 * @author Zdream
 * @date 2016年4月3日
 * @version v0.1
 */
public enum EItemType {
	/**
	 * 可用道具
	 */
	ITEMS,
	
	/**
	 * 补充类道具<br>
	 * 伤药、解毒药、能力提升剂等
	 */
	MEDICINE,
	
	/**
	 * 精灵球类型
	 */
	BALLS,
	
	/**
	 * 技能机器、秘传机器等
	 */
	TMS,
	
	/**
	 * 树果
	 */
	BERRIES,
	
	/**
	 * 信件
	 */
	MAIL,
	
	/**
	 * 战斗用品
	 */
	BATTLE_ITEMS,
	
	/**
	 * 重要道具
	 */
	KEY_ITEMS;
	
	public EItemType parseEnum(String value) {
		return valueOf(EItemType.class, value);
	}
	
	public EItemType parseEnum(int value) {
		return EItemType.values()[value];
	}
	
}
