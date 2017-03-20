package com.zdream.pmw.trainer.prototype;

import java.io.Serializable;

import com.zdream.pmw.monster.prototype.EPokemonGender;

/**
 * 记录玩家信息的数据模型<br>
 * <br>
 * <b>v0.1.1</b><br>
 *   修改了 toString 的方法<br>
 * 
 * @since v0.1.1
 * @author Zdream
 * @date 2016年3月28日
 * @version v0.1.1
 */
public class TrainerData implements Serializable{
	
	private static final long serialVersionUID = -5506178754712854012L;

	/* ************
	 * 训练师信息 *
	 ************ */
	/*
	 * 训练师信息，包括：
	 *   训练师名  trainer name
	 *   训练师性别  trainer gender
	 *   训练师 ID  trainer ID
	 *   训练师隐式 ID  trainer SID
	 *   标签  trainer markers
	 */
	
	/**
	 * 训练师名  trainer name
	 */
	private String trainerName;
	
	/**
	 * 训练师性别  trainer gender<br>
	 * 它的可能值在 EPokemonGender 中进行了定义，可能是 M、F 中的一个；
	 */
	private EPokemonGender trainerGender;
	
	/**
	 * 训练师 ID  trainer ID
	 */
	private int trainerID;
	
	/**
	 * 训练师隐式 ID  trainer SID
	 */
	private int trainerSID;

	public TrainerData() {}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder(20);
		builder.append("Trainer :").append(trainerName).append(' ').append(trainerGender.name())
				.append(" ID:").append(trainerID);
		return builder.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((trainerGender == null) ? 0 : trainerGender.hashCode());
		result = prime * result + trainerID;
		result = prime * result + ((trainerName == null) ? 0 : trainerName.hashCode());
		result = prime * result + trainerSID;
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
		TrainerData other = (TrainerData) obj;
		if (trainerGender != other.trainerGender)
			return false;
		if (trainerID != other.trainerID)
			return false;
		if (trainerName == null) {
			if (other.trainerName != null)
				return false;
		} else if (!trainerName.equals(other.trainerName))
			return false;
		if (trainerSID != other.trainerSID)
			return false;
		return true;
	}

	public String getTrainerName() {
		return trainerName;
	}

	public void setTrainerName(String trainerName) {
		this.trainerName = trainerName;
	}

	public EPokemonGender getTrainerGender() {
		return trainerGender;
	}

	public void setTrainerGender(EPokemonGender trainerGender) {
		this.trainerGender = trainerGender;
	}

	public int getTrainerID() {
		return trainerID;
	}

	public void setTrainerID(int trainerID) {
		this.trainerID = trainerID;
	}

	public int getTrainerSID() {
		return trainerSID;
	}

	public void setTrainerSID(int trainerSID) {
		this.trainerSID = trainerSID;
	}

}
