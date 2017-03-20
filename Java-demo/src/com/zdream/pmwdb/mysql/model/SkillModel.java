package com.zdream.pmwdb.mysql.model;

/**
 * 技能 bean 类<br>
 * <br>
 * <b>v0.2</b><br>
 *   修改了所在包<br>
 * 
 * @since v0.1
 * @author Zdream
 * @date 2016年3月22日
 * @version v0.2
 */
public class SkillModel {
	
	private short id;
	
	private String title;
	
	private byte type;
	
	private short power;
	
	private byte accuracy;
	
	private byte category;
	
	private byte ppMax;
	
	private String description;
	
	private String releaseEffect;

	@Override
	public String toString() {
		return "SkillModel [id=" + id + ", title=" + title + ", type=" + type + ", power=" + power + ", accuracy=" + accuracy
				+ ", category=" + category + ", ppMax=" + ppMax + ", description=" + description + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + accuracy;
		result = prime * result + category;
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + id;
		result = prime * result + power;
		result = prime * result + ppMax;
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		result = prime * result + type;
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
		SkillModel other = (SkillModel) obj;
		if (accuracy != other.accuracy)
			return false;
		if (category != other.category)
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (id != other.id)
			return false;
		if (power != other.power)
			return false;
		if (ppMax != other.ppMax)
			return false;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;
		if (type != other.type)
			return false;
		return true;
	}

	public SkillModel() {
		super();
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

	public byte getType() {
		return type;
	}

	public void setType(byte type) {
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

	public byte getCategory() {
		return category;
	}

	public void setCategory(byte category) {
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

	public String getReleaseEffect() {
		return releaseEffect;
	}

	public void setReleaseEffect(String releaseEffect) {
		this.releaseEffect = releaseEffect;
	}
	
}
