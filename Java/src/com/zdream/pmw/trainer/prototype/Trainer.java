package com.zdream.pmw.trainer.prototype;

import com.zdream.pmw.monster.prototype.Pokemon;

/**
 * 训练师携带精灵、背包、信息等数据的结合模型<br>
 * 
 * @since v0.1
 * @author Zdream
 * @date 2016年3月30日
 * @version v0.1
 */
public class Trainer{
	
	/* ************
	 *	主要信息  *
	 ************ */
	
	/**
	 * 存储训练师本身的信息的数据
	 */
	private TrainerData data = new TrainerData();
	
	/**
	 * 训练师携带的精灵数据
	 */
	private Pokemon[] pokemons = new Pokemon[6];
	
	/*
	 * 背包数据、图鉴数据、箱子数据、徽章、主支线进行情况、地点触发情况数据
	 */

	public TrainerData getData() {
		return data;
	}

	public void setData(TrainerData data) {
		this.data = data;
	}

	public Pokemon[] getPokemons() {
		return pokemons;
	}

	public void setPokemons(Pokemon[] pokemons) {
		this.pokemons = pokemons;
	}
	
	/* ************
	 *	主要玩家  *
	 ************ */
	
	/**
	 * 主要面向单机游戏，因此属于玩家的训练师只有一个，放在静态变量 thizPlayer 中；<br>
	 * 而其它的训练师也可以初始化
	 */
	
	public Trainer() {
		// 允许创建多个
	}
	
	private static Trainer thizPlayer = new Trainer();
	
	/**
	 * 获得唯一的玩家对应的训练师实例
	 * @return
	 */
	public static Trainer getThizPlayer() {
		return thizPlayer;
	}
	
}
