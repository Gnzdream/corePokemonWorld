package com.zdream.pmw.platform.effect.addition;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

import com.zdream.pmw.core.tools.AbnormalMethods;
import com.zdream.pmw.monster.prototype.EPokemonAbnormal;
import com.zdream.pmw.platform.control.IPrintLevel;
import com.zdream.pmw.platform.effect.Aperitif;
import com.zdream.pmw.platform.effect.EffectManage;
import com.zdream.pmw.util.json.JsonObject;
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
 * @version v0.2.2
 */
public class AbnormalAdditionFormula extends AAdditionFormula {
	
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

	// v0.2.2
	/**
	 * 对应上面的 seats, 每个目标是否触发
	 */
	boolean[] forces;
	
	/* ************
	 *	实现方法  *
	 ************ */

	@Override
	public String name() {
		return "abn";
	}
	
	@Override
	protected void postHandle() {
		forces = null;
	}

	/**
	 * 参数的格式：<br>
	 * n: Abnormal 固定 a.abn<br>
	 * i: 异常状态<br>
	 * r: 百分比施加可能性<br>
	 * tg: 施加对象<br>
	 */
	public void set(JsonObject value) { // 暂时没定格式
		super.set(value);
		Set<Entry<String, JsonValue>> set = value.asMap().entrySet();

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
		super.restore();
	}
	
	/* ************
	 *	私有方法  *
	 ************ */
	
	/**
	 * 按随机数，计算异常状态是否能够触发
	 * @return
	 *   如果所有目标都不能发动, 为 false
	 */
	protected boolean canTrigger() {
		// 需要计算附加状态真实释放几率
		
		final int len = targets.length;
		if (len == 0) {
			return false;
		}
		
		this.forces = new boolean[targets.length];
		final byte atseat = pack.getAtStaff().getSeat();
		boolean exist = false;
		
		for (int i = 0; i < len; i++) {
			byte target = targets[i];
			
			if (target == atseat) {
				exist |= forces[i] = canTriggerForSelf();
			} else {
				exist |= forces[i] = canTriggerForEnemy(target, targets);
			}
		}
		
		return exist;
	}
	/**
	 * 施加异常状态到自己身上
	 * @return
	 */
	private boolean canTriggerForSelf() {
		final byte[] dfseats = pack.dfSeats();
		final byte atseat = pack.getAtStaff().getSeat();
		
		Aperitif ap = em.newAperitif(Aperitif.CODE_CALC_ADDITION_RATE, dfseats);
		ap.putScanSeats(atseat);
		
		ap.append("type", "abnormal")
			.append("dfseats", dfseats)
			.append("atseat", atseat)
			.append("target", atseat)
			.append("abnormal", AbnormalMethods.toBytes(abnormal)) // 不含参数
			.append("rate", rate)
			.append("result", 0)
			.append("state-category", "abnormal");
		em.startCode(ap);
		
		int result = (Integer) ap.get("result");
		if (result == 0) {
			int instanceRate = (int) ap.get("rate");
			return RanValue.isSmaller(instanceRate);
		}
		
		return false;
	}
	
	/**
	 * 施加异常状态到防御方身上
	 * @param target
	 *   在攻击到的对象 seat
	 * @param dfseats
	 *   防御方座位组成的数组, 作为参数因为每次调用该方法时,
	 *   生成的 dfseats 都一样, 因此作为传入数据可以减少多次重复的方法调用
	 * @return
	 */
	private boolean canTriggerForEnemy(byte target, byte[] dfseats) {
		byte atseat = pack.getAtStaff().getSeat();
		
		Aperitif ap = em.newAperitif(Aperitif.CODE_CALC_ADDITION_RATE, atseat, target);
		ap.append("type", "abnormal")
			.append("dfseats", dfseats)
			.append("atseat", atseat)
			.append("target", target)
			.append("abnormal", AbnormalMethods.toBytes(abnormal)) // 不含参数
			.append("rate", rate)
			.append("result", 0)
			.append("state-category", "abnormal");
		em.startCode(ap);
		
		int result = (Integer) ap.get("result");
		if (result == 0) {
			int instanceRate = (int) ap.get("rate");
			return RanValue.isSmaller(instanceRate);
		}
		
		return false;
	}
	
	/**
	 * 施加状态
	 */
	protected void force() {
		byte atseat = pack.getAtStaff().getSeat();
		for (int i = 0; i < targets.length; i++) {
			if (forces[i]) {
				em.forceAbnormal(atseat, targets[i],
						AbnormalMethods.toBytes(abnormal));
			}
		}
	}
	
	/* ************
	 *	 初始化   *
	 ************ */
	
	private EffectManage em;
	
	public AbnormalAdditionFormula(EffectManage em) {
		this.em = em;
	}
}
