package com.zdream.pmwdb.mysql.model;

/**
 * 精灵升级学习技能表的静态数据库模型<br>
 * 
 * @since v0.1
 * @author Zdream
 * @date 2016年3月28日
 * @version v0.2
 */
public class LevelSkillTableModel {
	
	/**
	 * 纯索引主键
	 */
	private int levelSkillTableId;
	
	/**
	 * 精灵的种族 ID
	 */
	private short speciesId;
	
	/**
	 * 等级
	 */
	private byte level;
	
	/**
	 * 技能 ID
	 */
	private short skillId;

	public LevelSkillTableModel() {}

	@Override
	public String toString() {
		return "LevelSkillTableModel [levelSkillTableId=" + levelSkillTableId + ", speciesId=" + speciesId + ", level="
				+ level + ", skillId=" + skillId + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + levelSkillTableId;
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
		LevelSkillTableModel other = (LevelSkillTableModel) obj;
		if (levelSkillTableId != other.levelSkillTableId)
			return false;
		return true;
	}

	public int getLevelSkillTableId() {
		return levelSkillTableId;
	}

	public void setLevelSkillTableId(int levelSkillTableId) {
		this.levelSkillTableId = levelSkillTableId;
	}

	public short getSpeciesId() {
		return speciesId;
	}

	public void setSpeciesId(short speciesId) {
		this.speciesId = speciesId;
	}

	public byte getLevel() {
		return level;
	}

	public void setLevel(byte level) {
		this.level = level;
	}

	public short getSkillId() {
		return skillId;
	}

	public void setSkillId(short skillId) {
		this.skillId = skillId;
	}

}
