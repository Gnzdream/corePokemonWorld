package com.zdream.pmw.platform.effect.addition;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

import com.zdream.pmw.monster.prototype.EPokemonAbnormal;
import com.zdream.pmw.platform.common.AbnormalMethods;
import com.zdream.pmw.platform.control.IPrintLevel;
import com.zdream.pmw.platform.effect.Aperitif;
import com.zdream.pmw.platform.effect.EffectManage;
import com.zdream.pmw.platform.effect.SkillReleasePackage;
import com.zdream.pmw.util.json.JsonValue;
import com.zdream.pmw.util.random.RanValue;

/**
 * 异常状态附加状态 / A 类附加效果判定实现类<br>
 * 向我方或敌方施加单个异常状态<br>
 * <br>
 * <b>v0.2</b><br>
 *   取消对 pf.instance 的引用<br>
 * 
 * @since v0.1
 * @author Zdream
 * @date 2016年4月16日
 * @version v0.2
 */
public class AbnormalAdditionFormula implements IAdditionFormula {
	
	/* ************
	 *	数据结构  *
	 ************ */
	
	/**
	 * 附加状态施加的异常状态种类（单个种类）<br>
	 */
	private EPokemonAbnormal abnormal;
	
	/**
	 * 附加状态施加的可能性（百分比制）<br>
	 * 参数 100 为百分之百施加<br>
	 */
	private int rate;
	
	/**
	 * 向哪方进行状态的施加<br>
	 * 是攻击方还是防御方<br>
	 * 参数 1 为攻击方，0 为防御方（默认）<br>
	 */
	private int side;
	
	/**
	 * 向攻击方施加状态
	 */
	public static final int SIDE_ATSTAFF = 1;
	
	/**
	 * 向防御方施加状态
	 */
	public static final int SIDE_DFSTAFF = 0;
	
	public EPokemonAbnormal getAbnormal() {
		return abnormal;
	}
	public void setAbnormal(EPokemonAbnormal abnormal) {
		this.abnormal = abnormal;
	}
	public int getRate() {
		return rate;
	}
	public void setRate(int rate) {
		this.rate = rate;
	}
	public int getSide() {
		return side;
	}
	public void setSide(int side) {
		this.side = side;
	}
	
	/* ************
	 *	实现方法  *
	 ************ */

	@Override
	public String name() {
		return "abnormal";
	}

	@Override
	public void addition(SkillReleasePackage pack) {
		this.pack = pack;
		if (canTrigger()) {
			force();
		}
		this.pack = null;
	}

	/**
	 * 参数的格式：<br>
	 * n: Abnormal 固定 a.abn<br>
	 * i: 异常状态<br>
	 * r: 百分比施加可能性<br>
	 * tg: 施加对象<br>
	 */
	public void set(JsonValue value) { // 暂时没定格式
		Set<Entry<String, JsonValue>> set = value.getMap().entrySet();

		String k;
		JsonValue v;
		
		for (Iterator<Entry<String, JsonValue>> it = set.iterator(); it.hasNext();) {
			Entry<String, JsonValue> entry = it.next();
			
			k = entry.getKey();
			v = entry.getValue();
			
			try {

				switch (k) {
				case "i": // items
					setAbnormal(EPokemonAbnormal.parseEnum(v.getString()));
					break;
				case "tg": { // target
					String param = v.getString();
					if ("e".equals(param)) {
						setSide(SIDE_DFSTAFF);
					} else if ("s".equals(param)) {
						setSide(SIDE_ATSTAFF);
					} else {
						em.logPrintf(IPrintLevel.PRINT_LEVEL_WARN, 
								"AbnormalAdditionF.set(1): 施加对象 %s 不符合要求", param);
					}
				}break;
				case "r": // rate
					setRate((Integer) v.getValue());
					break;

				default:
					break;
				}
				
			} catch (RuntimeException e) {
				em.logPrintf(IPrintLevel.PRINT_LEVEL_WARN, e);
			}
		}
		
	}
	
	@Override
	public void restore() {
		rate = 100;
		side = SIDE_DFSTAFF;
	}
	
	/* ************
	 *	私有方法  *
	 ************ */
	
	/**
	 * 按随机数，计算异常状态是否能够触发
	 * @return
	 */
	private boolean canTrigger() {
		// 计算附加状态真实释放几率
		byte atseat = pack.getAtStaff().getSeat();
		byte dfseat = (side == 0) ? pack.getDfStaff(pack.getThiz()).getSeat() : atseat;
		
		Aperitif value = pack.getEffects().newAperitif(Aperitif.CODE_CALC_ADDITION_RATE, atseat, dfseat);
		value.append("type", "abnormal");
		value.append("dfseat", dfseat);
		value.append("atseat", atseat);
		value.append("abnormal", AbnormalMethods.toBytes(abnormal)); // 不含参数
		value.append("rate", rate);
		value.append("result", 0);
		value.append("state-category", "abnormal");
		pack.getEffects().startCode(value);
		
		// 计算
		int result = (Integer) value.get("result");
		if (result == 0) {
			int instanceRate = (int) value.get("rate");
			return RanValue.isSmaller(instanceRate);
		}
		
		return false;
	}
	
	/**
	 * 施加状态
	 */
	private void force() {
		pack.getEffects().forceAbnormal(pack, AbnormalMethods.toBytes(abnormal));
	}
	
	/* ************
	 *	 初始化   *
	 ************ */
	
	private SkillReleasePackage pack;
	private EffectManage em;
	
	public AbnormalAdditionFormula(EffectManage em) {
		this.em = em;
	}
}
