package com.zdream.pmw.monster.prototype;

import com.zdream.pmw.trainer.prototype.TrainerData;

/**
 * 关于精灵的动态数据、静态数据的相关服务接口<br>
 * 
 * @since v0.1
 * @author Zdream
 * @date 2016年3月29日
 * @version v0.2
 */
public interface IPokemonService {
	
	/**
	 * 重新计算精灵的能力值
	 * @param pm
	 *   等待能力值结算的精灵数据模型
	 */
	public void countStatValue(Pokemon pm);
	
	/**
	 * 将精灵恢复健康<br>
	 * 将精灵的 HP 补充到满，并移除异常状态
	 * @param pm
	 *   精灵数据模型
	 */
	public void recoverPokemon(Pokemon pm);
	
	/**
	 * 查询该精灵的训练师和指定的训练师是否是同一个<br>
	 * 该判定将检查 Pokemon 中对应的：<br>
	 * 1.  训练师名  trainer name<br>
	 * 2.  训练师性别  trainer gender<br>
	 * 3.  训练师 ID  trainer ID<br>
	 * 4.  训练师隐式 ID  trainer SID<br>
	 * 是否相等。以上 4 项完全相等则返回 true，否则 false
	 * @param pm
	 *   需要判断的精灵
	 * @param data
	 *   待判定的训练师
	 * @return
	 */
	public boolean isRealTrainer(Pokemon pm, TrainerData data);
	
}
