package com.zdream.pmw.platform.effect.damage;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

import com.zdream.pmw.platform.control.IPrintLevel;
import com.zdream.pmw.platform.effect.Aperitif;
import com.zdream.pmw.platform.effect.SkillReleasePackage;
import com.zdream.pmw.util.json.JsonValue;
import com.zdream.pmw.util.random.RanValue;

/**
 * 多次连续攻击伤害计算公式<br>
 * 
 * @since v0.2.2
 *   [2017年4月1日]
 * @author Zdream
 * @version v0.2.2
 *   [2017年4月1日]
 */
public class DoubleDamageFormula extends DefaultDamageFormula {
	
	/* ************
	 *	  属性    *
	 ************ */
	
	/**
	 * 连续攻击的次数
	 */
	int round;
	public int getRound() {
		return round;
	}
	
	int mode;
	/**
	 * <p>普通 2-5 次连续攻击. (默认值)</p>
	 * </p>这类攻击时, 攻击 2 下和 3 下都有 1/3 几率触发,
	 * 攻击 4 下和 5 下都有 1/6 几率触发.</p>
	 */
	public static final int MODE_FLOAT = 0;
	
	/**
	 * <p>固定连续攻击次数</p>
	 * <p>param 设置了连续攻击的次数</p>
	 */
	public static final int MODE_FIXED = 1;
	
	/**
	 * 连续攻击每次产生的 pack 都放在这里
	 */
	SkillReleasePackage[] packs;
	
	/* ************
	 *	实现方法  *
	 ************ */
	
	@Override
	protected void eachRound() {
		// TODO 准备
		// 1. 计算攻击次数
		calcRound();
		// 2. 发布开始连续攻击的广播消息
		postBeginAperitif();
		
		int i = 0;
		for (; i < round; i++) {
			// TODO 检查
			if (!checkEveryRound(i)) {
				break;
			}
			
			replacePackage(i);
			
			super.eachRound();
			
			if (i == 0) CHECK: {
				// TODO 如果已经判定第一轮攻击免疫, 将不会再执行.
				int length = pack.dfStaffLength();
				for (int j = 0; j < length; j++) {
					if (pack.getRate(j) > 0) {
						break CHECK;
					}
				}
				
				// 攻击免疫, 终止执行
				break;
			}
		}
		
		// 结束前的处理
		postEndAperitif(i);
	}

	@Override
	public void set(JsonValue value) {
		Set<Entry<String, JsonValue>> set = value.getMap().entrySet();
		String k;
		JsonValue v;
		
		for (Iterator<Entry<String, JsonValue>> it = set.iterator(); it.hasNext();) {
			Entry<String, JsonValue> entry = it.next();
			k = entry.getKey();
			v = entry.getValue();
			
			try {
				switch (k) {
				case "m": { // mode
					String str = v.getString();
					switch (str) {
					case "float":
						mode = MODE_FLOAT;
						break;
					case "fixed":
						mode = MODE_FIXED;
					default:
						break;
					}
				} break;
				case "r": { // round
					round = (int) v.getValue();
				} break;
				default:
					break;
				}
				
			} catch (RuntimeException e) {
				pack.getEffects().logPrintf(IPrintLevel.PRINT_LEVEL_WARN, e);
			}
		}
	}
	
	@Override
	public void restore() {
		mode = MODE_FLOAT;
		round = 0;
		packs = null;
	}

	@Override
	public String name() {
		return "double";
	}
	
	/* ************
	 *	私有方法  *
	 ************ */
	
	/**
	 * 计算攻击发动的次数
	 */
	protected void calcRound() {
		switch (mode) {
		case MODE_FLOAT:
			calcFloatRound();
			break;

		case MODE_FIXED:
			if (round == 0) {
				throw new IllegalArgumentException(
					"round: " + round + " 在固定连续攻击次数时是非法的");
			} else {
				packs = new SkillReleasePackage[round];
			}
			break;
		default:
			break;
		}
	}
	
	/**
	 * 计算 float 模式下, 攻击发动的次数
	 */
	protected void calcFloatRound() {
		int r = RanValue.random(0, 6);
		switch (r) {
		case 0: case 1:
			round = 2;
			break;
		case 2: case 3:
			round = 3;
			break;
		case 4:
			round = 4;
			break;
		case 5:
			round = 5;
			break;
		}
		
		Aperitif ap = pack.getEffects().newAperitif(Aperitif.CODE_ADDITION_SETTLE,
				pack.getAtStaff().getSeat());
		ap.append("category", "double").append("mode", mode).append("round", round);
		pack.getEffects().startCode(ap);
		
		round = (int) ap.get("round");
		packs = new SkillReleasePackage[round];
	}
	
	/**
	 * 发布开始连续攻击的广播消息
	 */
	protected void postBeginAperitif() {
		Aperitif ap = pack.getEffects().newAperitif(Aperitif.CODE_BROADCAST);
		ap.append("type", "double").append("-tag", "begin");
		pack.getEffects().startCode(ap);
	}
	
	/**
	 * 发布结束连续攻击的广播消息
	 * @param round
	 *   成功攻击了几轮, 这次是正确的轮数
	 */
	protected void postEndAperitif(int round) {
		Aperitif ap = pack.getEffects().newAperitif(Aperitif.CODE_BROADCAST);
		ap.append("type", "double").append("-tag", "end").append("-round", round);
		pack.getEffects().startCode(ap);
	}
	
	protected void replacePackage(int round) {
		if (round == 0) {
			packs[0] = pack;
			return;
		}
		packs[round] = pack;
		pack = pack.getEffects().replacePackage(pack);
		pack.setType(packs[round].getType());
		
		final int length = pack.dfStaffLength();
		for (int i = 0; i < length; i++) {
			pack.setHitable(packs[round].isHitable(i), i);
		}
	}
	
	/**
	 * 每一轮的检查
	 * @param round
	 *   第几轮, 该数值从 0 开始, 所以当 {@code round = 1} 时是第二轮.
	 * @return
	 */
	protected boolean checkEveryRound(int round) {
		// 现在只检查该轮攻击方和防御方的 HP
		int athpi = pack.getAtStaff().getHpi();
		if (athpi == 0) {
			return false;
		}
		
		boolean exist = false;
		final int length = pack.dfStaffLength();
		for (int i = 0; i < length; i++) {
			int dfhpi = pack.getDfStaff(i).getHpi();
			if (dfhpi == 0) {
				pack.setHitable(false, i);
			} else {
				exist = true;
			}
		}
		
		return exist;
	}
	
}
