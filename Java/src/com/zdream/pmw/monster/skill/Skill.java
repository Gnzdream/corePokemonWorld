package com.zdream.pmw.monster.skill;

import java.io.Serializable;

import com.zdream.pmw.monster.prototype.EPokemonType;
import com.zdream.pmw.util.json.JsonValue;

/**
 * 精灵技能的数据模型<br>
 * <br>
 * <b>v0.2</b><br>
 *   添加释放数据属性<br>
 * 
 * @since v0.1
 * @author Zdream
 * @date 2016年3月15日
 * @version v0.2
 */
public class Skill implements Serializable{
	
	private static final long serialVersionUID = -5821245942421547302L;

	/**
	 * 技能编号 skill id<br>
	 * 作为该数据模型的主键
	 */
	private short id;
	
	/**
	 * 名称 Title<br>
	 * 技能的名字
	 */
	private String title;
	
	/**
	 * 属性 Type<br>
	 * 技能所带的属性
	 */
	private EPokemonType type;
	
	/**
	 * 威力 Power<br>
	 * 技能的威力
	 */
	private short power;
	
	/**
	 * 无威力默认值
	 */
	public static final short POWER_NONE_POWER = 0;
	
	/**
	 * 变威力默认值
	 */
	public static final short POWER_FLOAT_POWER = 1;
	
	/**
	 * 命中率 Accuracy
	 */
	private byte accuracy;
	
	/**
	 * 无命中率默认值
	 */
	public static final short ACCURACY_OTHER = 0;
	
	/**
	 * 技能的伤害类型
	 */
	private ESkillCategory category;
	
	/**
	 * PP Max
	 */
	private byte ppMax;
	
	/**
	 * 描述 Description
	 */
	private String description;
	
	/**
	 * 技能在释放时的特殊性<br>
	 * 比如技能会提升自己能力、使对方陷入异常状态等<br>
	 * @since v0.2
	 */
	private JsonValue release;

	public Skill() {
		super();
	}

	@Override
	public String toString() {
		return "Skill [id=" + id + ", title=" + title + ", type=" + type + ", power=" + power + ", accuracy=" + accuracy
				+ ", category=" + category + ", ppMax=" + ppMax + ", description=" + description + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
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
		Skill other = (Skill) obj;
		if (id != other.id)
			return false;
		return true;
	}

	public short getId() {
		return id;
	}

	public void setId(short id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public EPokemonType getType() {
		return type;
	}

	public void setType(EPokemonType type) {
		this.type = type;
	}

	public short getPower() {
		return power;
	}

	public void setPower(short power) {
		this.power = power;
	}

	public byte getAccuracy() {
		return accuracy;
	}

	public void setAccuracy(byte accuracy) {
		this.accuracy = accuracy;
	}

	public ESkillCategory getCategory() {
		return category;
	}

	public void setCategory(ESkillCategory category) {
		this.category = category;
	}

	public byte getPpMax() {
		return ppMax;
	}

	public void setPpMax(byte ppMax) {
		this.ppMax = ppMax;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @since v0.2
	 */
	public JsonValue getRelease() {
		return release;
	}

	/**
	 * @since v0.2
	 */
	public void setRelease(JsonValue release) {
		this.release = release;
	}
}
