package com.zdream.pmw.monster.service;

import com.zdream.pmw.monster.data.IPokemonDataContainer;
import com.zdream.pmw.monster.data.PokemonBaseData;
import com.zdream.pmw.monster.data.PokemonDataBuffer;
import com.zdream.pmw.monster.prototype.EPokemonNature;
import com.zdream.pmw.monster.prototype.IPokemonService;
import com.zdream.pmw.monster.prototype.Pokemon;
import com.zdream.pmw.trainer.prototype.TrainerData;

/**
 * 关于精灵的动态数据、静态数据的相关服务默认实现类<br>
 * 
 * @since v0.1
 * @author Zdream
 * @date 2016年3月29日
 * @version v0.2
 */
public class PokemonServiceImpl implements IPokemonService {
	
	/* ************
	 *	单例模式  *
	 ************ */
	private static PokemonServiceImpl instance = new PokemonServiceImpl();
	
	public static PokemonServiceImpl getInstance() {
		return instance;
	}
	
	/**
	 * 启用单例模式
	 */
	private PokemonServiceImpl() {
		super();
		// private
	}
	
	/* ************
	 *	 缓存池   *
	 ************ */
	/**
	 * 精灵数据的缓存池
	 */
	IPokemonDataContainer buffer = PokemonDataBuffer.getInstance();
	
	/* ************
	 *	调用接口  *
	 ************ */
	/**
	 * 能力值计算公式：（种族值不为 1 时）
	 * HP = (个体值 + 2x种族值 + 努力值/4)x等级/100 + 10 + 等级
	 * 其它能力值 = ((个体值 + 2x种族值 + 努力值/4)x等级/100 + 5) x 性格修正
	 */
	public void countStatValue(Pokemon pm) {
		PokemonBaseData data = buffer.getBaseData(pm.getSpeciesID(), pm.getForm());
		// 种族值
		short[] speciesValues = data.getSpeciesValue();
		// 个体值
		byte[] statIV = pm.getStatIV();
		// 努力值
		byte[] statEV = pm.getStatEV();
		// 能力值
		short[] stat = pm.getStat();
		// 等级
		short level = pm.getLevel();
		// 性格
		EPokemonNature nature = pm.getNaturn();
		int value = 0;
		
		// 计算 HP
		if (speciesValues[0] == 1) {
			stat[0] = (short) 1;
		} else {
			stat[0] = (short) ((statIV[0] + 2 * speciesValues[0] + statEV[0] / 4) * level / 100 + 10 + level);
		}
		
		// 计算其它五项
		for (int i = 1; i < stat.length; i++) {
			value = (statIV[i] + 2 * speciesValues[i] + statEV[i] / 4) * level / 100 + 5;
			
			if (nature.getUpItem() == i && nature.getUpItem() != i) {
				value = value * 11 / 10;
			} else if (nature.getUpItem() != i && nature.getUpItem() == i) {
				value = value * 9 / 10;
			}
			
			stat[i] = (short) value;
		}
	}
	
	@Override
	public void recoverPokemon(Pokemon pm) {
		pm.setHpi(pm.getStat()[0]);
		pm.setAbnormal((byte) 0);
	}

	@Override
	public boolean isRealTrainer(Pokemon pm, TrainerData data) {
		if (data == null) {
			return false;
		}
		return (pm.getTrainerID() == data.getTrainerID() &&
				pm.getTrainerSID() == data.getTrainerSID() &&
				pm.getTrainerName().equals(data.getTrainerName()) &&
				pm.getTrainerGender() == data.getTrainerGender()
				);
	}
}
