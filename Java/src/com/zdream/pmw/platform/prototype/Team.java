package com.zdream.pmw.platform.prototype;

import java.util.Arrays;

import com.zdream.pmw.monster.prototype.Pokemon;
import com.zdream.pmw.trainer.prototype.Trainer;

/**
 * 队伍的数据包<br>
 * 包括训练师实体和所携带的、可以进入战场的精灵<br>
 * <br>
 * <b>v0.1.1</b><br>
 *   修改了 toString() 方法<br>
 * 
 * @since v0.1.1
 * @author Zdream
 * @date 2016年3月31日
 * @version v0.1.1
 */
public class Team {

	/**
	 * 训练师实体类
	 */
	private Trainer trainer;
	
	/**
	 * 该训练师携带的精灵
	 */
	private Pokemon[] pms;
	
	public Trainer getTrainer() {
		return trainer;
	}

	public void setTrainer(Trainer trainer) {
		this.trainer = trainer;
	}

	public Pokemon[] getPms() {
		return pms;
	}

	public void setPms(Pokemon[] pms) {
		this.pms = pms;
	}

	public Team(Trainer trainer, Pokemon[] pms) {
		super();
		this.trainer = trainer;
		this.pms = pms;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder(50);
		builder.append("Team: ").append(trainer.getData().toString()).append(", pm=").append(Arrays.toString(pms));
		return builder.toString();
	}
}