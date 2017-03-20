package com.zdream.pmw.platform.attend;

import com.zdream.pmw.monster.prototype.EPokemonType;
import com.zdream.pmw.monster.skill.ESkillCategory;
import com.zdream.pmw.monster.skill.Skill;
import com.zdream.pmw.util.json.JsonValue;

/**
 * 技能释放类，在战斗中用<br>
 * 里面储存了战斗中将会使用的数值，像优先度、会心率（等级）等<br>
 * <br>
 * <b>v0.2.1</b><br>
 *   特殊参数数组合并, 且将威力、伤害、命中等公式判定类全部存储到 additions 中<br>
 *   并删除冗余数据<br>
 * 
 * @since v0.1.1
 * @author Zdream
 * @date 2016年4月11日
 * @version v0.2.1
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
	private JsonValue[] additions;
	
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
	
	public void addAddition(JsonValue addition) {
		additions = appendJsonValues(additions, addition);
	}

	public JsonValue[] getAdditions() {
		return additions;
	}

	public void setAdditions(JsonValue[] additions) {
		this.additions = additions;
	}
	
	private JsonValue[] appendJsonValues(JsonValue[] src, JsonValue value) {
		if (src == null) {
			return new JsonValue[]{value};
		} else {
			JsonValue[] dest;
			dest = new JsonValue[src.length + 1];
			System.arraycopy(src, 0, dest, 0, src.length);
			dest[dest.length - 1] = value;
			return dest;
		}
	}

}
