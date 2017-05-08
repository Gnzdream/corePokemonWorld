package com.zdream.pmw.platform.attend;

import com.zdream.pmw.monster.prototype.EPokemonType;
import com.zdream.pmw.monster.skill.ESkillCategory;
import com.zdream.pmw.monster.skill.Skill;
import com.zdream.pmw.util.json.JsonObject;

/**
 * 技能释放类，在战斗中用<br>
 * 里面储存了战斗中将会使用的数值，像优先度、会心率（等级）等<br>
 * <br>
 * <b>v0.2.1</b><br>
 *   特殊参数数组合并, 且将威力、伤害、命中等公式判定类全部存储到 additions 中<br>
 *   并删除冗余数据<br>
 * 
 * <p><b>v0.2.3</b>
 * 添加了特殊属性的 field.
 * </p>
 * 
 * @since v0.1.1 [2016-04-11]
 * @author Zdream
 * @version v0.2.3 [2017-04-24]
 */
public class SkillRelease {
	
	/* ************
	 *	原始数据  *
	 ************ */
	
	/**
	 * 简化数据位置
	 */
	private Skill skill;
	
	public Skill getSkill() {
		return skill;
	}

	/* ************
	 *	其它参数  *
	 ************ */
	
	/**
	 * 技能范围
	 */
	private ESkillRange range;
	
	/**
	 * 会心等级
	 */
	private byte critLevel;
	
	/**
	 * 优先度
	 */
	private byte priority;
	
	/**
	 * 特殊参数<br>
	 * 威力、伤害、命中等公式判定类的生成需要的数据
	 */
	private JsonObject[] additions;
	
	/**
	 * <p>技能的特殊属性.
	 * <p>该技能可能属于特殊的分类, 比如睡眠粉、麻痹粉属于粉末类技能;
	 * 叫声、大喊属于声音技能等. 该参数用于存放这类信息.
	 * <p>还有某些技能可能会附加比如不需要判断属性免疫、不需要扣除 PP
	 * 等其它
	 */
	private JsonObject property;
	
	/* ************
	 *	代理方法  *
	 ************ */

	public short getId() {
		return skill.getId();
	}

	public String getTitle() {
		return skill.getTitle();
	}

	public EPokemonType getType() {
		return skill.getType();
	}

	public short getPower() {
		return skill.getPower();
	}

	public byte getAccuracy() {
		return skill.getAccuracy();
	}

	public ESkillCategory getCategory() {
		return skill.getCategory();
	}
	
	/* ************
	 *	其它数据  *
	 ************ */

	public SkillRelease(Skill skill) {
		this.skill = skill;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder(10);
		builder.append(skill.getId()).append('-').append(skill.getTitle());
		return builder.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((skill == null) ? 0 : skill.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SkillRelease other = (SkillRelease) obj;
		if (skill == null) {
			if (other.skill != null)
				return false;
		} else if (!skill.equals(other.skill))
			return false;
		return true;
	}

	public ESkillRange getRange() {
		return range;
	}

	public void setRange(ESkillRange range) {
		this.range = range;
	}

	public byte getCritLevel() {
		return critLevel;
	}

	public void setCritLevel(byte critLevel) {
		this.critLevel = critLevel;
	}

	public byte getPriority() {
		return priority;
	}

	public void setPriority(byte priority) {
		this.priority = priority;
	}

	public JsonObject[] getAdditions() {
		return additions;
	}

	public void setAdditions(JsonObject[] additions) {
		this.additions = additions;
	}
	
	/**
	 * 返回所有的属性值
	 * @see #property
	 * @since v0.2.3
	 */
	public JsonObject getProperty() {
		if (property == null) {
			property = new JsonObject();
		}
		return property;
	}
	
	/**
	 * 询问该技能的特殊属性值
	 * @see #property
	 * @see #getProperty()
	 * @param key
	 *   属性的 key
	 * @return
	 *   属性值. 默认返回 null
	 * @since v0.2.3
	 */
	public Object getProperty(String key) {
		if (property == null) {
			return null;
		}
		return property.get(key);
	}
	
	/**
	 * <p>询问该技能是否含有一些特殊的属性
	 * <p>理论上属性这个参数能够存储除了 {@code boolean} 以外类型的值.
	 * 在这里, 如果该参数对应的值为布尔类型的, 返回该值;
	 * 如果该参数对应的值不为布尔类型的, 如果该值不为空, 则返回 {@code true}
	 * @see #property
	 * @see #getProperty(String)
	 * @param key
	 *   属性的 key
	 * @return
	 *   返回属性值. 默认 false
	 * @since v0.2.3
	 */
	public boolean isProperty(String key) {
		if (property == null) {
			return false;
		}
		Object o = property.get(key);
		if (o instanceof Boolean) {
			return (boolean) o;
		}
		return true;
	}

}
