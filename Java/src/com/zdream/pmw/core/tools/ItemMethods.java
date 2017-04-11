package com.zdream.pmw.core.tools;

import com.zdream.pmw.monster.skill.ESkillCategory;

/**
 * 能力项工具<br>
 * 能力项是例如 AT 攻击等术语的集合,<br>
 * 是 <code>Pokemon.stat</code> 的元数据<br>
 * <br>
 * <b>v0.2</b><br>
 *   添加技能类型相关的方法<br>
 * 
 * @since v0.1
 * @author Zdream
 * @date 2016年9月6日
 * @version v0.2
 */
public class ItemMethods {

	/**
	 * 将参数的名称转化成对应的代号<br>
	 * 是 {@link #parseItemName(int)} 的反函数<br>
	 * @param item
	 * @return
	 */
	public static int parseItemCode(String item) {
		
		item = item.toUpperCase();
		
		switch (item) {
		case "HP":
			return 0;
		case "AT":
			return 1;
		case "DF":
			return 2;
		case "SA":
			return 3;
		case "SD":
			return 4;
		case "SP":
			return 5;
		case "TG":
			return 6;
		case "HD":
			return 7;

		default:
			return -1;
		}
	}

	/**
	 * 将能力代号转化成对应的能力名称<br>
	 * 是 {@link #parseItemCode(String)} 的反函数<br>
	 * @param code
	 *   能力代号
	 * @return
	 *   返回诸如 {@code "AT" "DF"} 这类表示能力的字符串<br>
	 *   错误的 {@code code} 返回 {@code null}<br>
	 * @since v0.2.1
	 */
	public static String parseItemName(int code) {
		
		switch (code) {
		case 0:
			return "HP";
		case 1:
			return "AT";
		case 2:
			return "DF";
		case 3:
			return "SA";
		case 4:
			return "SD";
		case 5:
			return "SP";
		case 6:
			return "TG";
		case 7:
			return "HD";

		default:
			return null;
		}
	}
	
	/**
	 * 将技能类型的名称转化成枚举类型
	 * @param category
	 * @return
	 */
	public static ESkillCategory parseCategory(String category) {
		category = category.toLowerCase();
		
		switch (category) {
		case "physical":
			return ESkillCategory.PHYSICS;
		case "special":
			return ESkillCategory.SPECIAL;
		case "status":
			return ESkillCategory.STATUS;

		default:
			return null;
		}
	}
	
	/**
	 * 类型的名称
	 * @param category
	 * @return
	 */
	public static String categoryName(ESkillCategory category) {
		switch (category) {
		case PHYSICS:
			return "physical";
		case SPECIAL:
			return "special";
		case STATUS:
			return "status";

		default:
			return null;
		}
	}

}
