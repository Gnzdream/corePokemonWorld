package com.zdream.pmw.monster.prototype;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.ListIterator;

import com.zdream.pmw.monster.data.IPokemonDataContainer;
import com.zdream.pmw.monster.data.PokemonBaseData;
import com.zdream.pmw.monster.data.PokemonDataBuffer;
import com.zdream.pmw.trainer.prototype.TrainerData;
import com.zdream.pmw.util.json.JsonBuilder;
import com.zdream.pmw.util.json.JsonValue;

/**
 * 精灵的基本查询、计算服务<br>
 * <p>Basic query and calculation server</p>
 * 
 * @since v0.2.1
 * @author Zdream
 * @date 2017年3月20日
 * @version v0.2.1
 */
public class PokemonHandler {
	
	/* ************
	 *	单例模式  *
	 ************ */
	/*
	 * singleton pattern
	 */
	private static PokemonHandler instance = new PokemonHandler();
	
	public static PokemonHandler getInstance() {
		return instance;
	}
	
	private PokemonHandler() {
		super();
		initResource();
	}
	
	/* ************
	 *	  属性    *
	 ************ */
	
	private String[] natures, types;
	
	/*
	 * 以下的属性从原 com.zdream.pmw.monster.service.PokemonServiceImpl 合并过来.
	 * These are the fields come from class: com.zdream.pmw.monster.service.PokemonServiceImpl
	 */
	/**
	 * 精灵数据的缓存池
	 */
	IPokemonDataContainer buffer = PokemonDataBuffer.getInstance();
	
	/* ************
	 *	实现方法  *
	 ************ */
	
	private void initResource() {
		File file = new File("assets/data/static/monster.json");
		try {
			InputStreamReader reader = new InputStreamReader(
					new FileInputStream(file), "UTF-8");
			char[] chs = new char[400];
			int len;
			StringBuilder strBuilder = new StringBuilder(400);
			while ((len = reader.read(chs)) != -1) {
				strBuilder.append(chs, 0, len);
			}
			
			JsonBuilder builder = new JsonBuilder();
			JsonValue value = builder.parseJson(strBuilder.toString());
			reader.close();
			
			List<JsonValue> list;
			
			// natures
			list = value.getMap().get("nature").getArray();
			natures = new String[list.size()];
			for (ListIterator<JsonValue> it = list.listIterator(); it.hasNext();) {
				natures[it.nextIndex()] = it.next().getString();
			}
			
			// types
			list = value.getMap().get("type").getArray();
			types = new String[list.size()];
			for (ListIterator<JsonValue> it = list.listIterator(); it.hasNext();) {
				types[it.nextIndex()] = it.next().getString();
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 返回性格名称<br>
	 * <p>returns the name of nature.</p>
	 * @param nature
	 *   the nature
	 * @return
	 */
	public String natureName(EPokemonNature nature) {
		return natures[nature.ordinal()];
	}
	
	/**
	 * 由于性格提升的能力项<br>
	 * <p>returns the ability of ascension due to the specific nature.</p>
	 * @param nature
	 *   the nature
	 * @return
	 *   if return -1, it means the nature has no negative influence to status.
	 * @see com.zdream.pmw.monster.prototype.IPokemonDataType
	 */
	public int natureUpItem(EPokemonNature nature) {
		int ordinal = nature.ordinal();
		if (ordinal % 6 == 0) {
			return -1;
		}
		return ordinal / 5 + 1;
	}
	
	/**
	 * 由于性格下降的能力项<br>
	 * <p>returns the declining ability item due to the specific nature.</p>
	 * @param nature
	 *   the nature
	 * @return
	 *   if return -1, it means the nature has no positive influence to status.
	 * @see com.zdream.pmw.monster.prototype.IPokemonDataType
	 */
	public int natureDownItem(EPokemonNature nature) {
		int ordinal = nature.ordinal();
		if (ordinal % 6 == 0) {
			return -1;
		}
		return ordinal % 5 + 1;
	}
	
	/**
	 * 返回属性名称<br>
	 * <p>returns the name of type.</p>
	 * @param type
	 *   the type
	 * @return
	 */
	public String typeName(EPokemonType type) {
		return types[type.ordinal()];
	}
	/**
	 * <p>重新计算怪兽的能力值</p>
	 * <p>Recalculate the status point of the specific pokemon.</p>
	 * <p>能力值计算公式：（种族值不为 1 时）
	 * HP = (个体值 + 2x种族值 + 努力值/4)x等级/100 + 10 + 等级
	 * 其它能力值 = ((个体值 + 2x种族值 + 努力值/4)x等级/100 + 5) x 性格修正</p>
	 * @param pm
	 *   等待能力值结算的精灵数据模型
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
		EPokemonNature nature = pm.getNature();
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
			
			if (natureUpItem(nature) == i) {
				value = value * 11 / 10;
			} else if (natureDownItem(nature) == i) {
				value = value * 9 / 10;
			}
			
			stat[i] = (short) value;
		}
	}

	/**
	 * <p>将精灵恢复健康<br>
	 * 将精灵的 HP 补充到满，并移除异常状态</p>
	 * <p>Recover the pokemon, and remove its abnormal state.</p>
	 * @param pm
	 *   精灵数据模型
	 */
	public void recoverPokemon(Pokemon pm) {
		pm.setHpi(pm.getStat()[0]);
		pm.setAbnormal((byte) 0);
	}

	/**
	 * <p>查询该精灵的训练师和指定的训练师是否是同一个</p>
	 * <p>Check whether the trainer of the pokemon and the trainer is specified
	 * are the same.</p>
	 * <p>该判定将检查 Pokemon 中对应的：
	 * <li>训练师名  trainer name
	 * <li>训练师性别  trainer gender
	 * <li>训练师 ID  trainer ID
	 * <li>训练师隐式 ID  trainer SID</li>
	 * 是否相等。以上 4 项完全相等则返回 true，否则 false</p>
	 * @param pm
	 *   需要判断的精灵
	 * @param data
	 *   待判定的训练师
	 * @return
	 */
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
