package com.zdream.pmw.platform.effect.damage;

import java.util.Iterator;
import java.util.Set;
import java.util.Map.Entry;

import com.zdream.pmw.platform.control.IPrintLevel;
import com.zdream.pmw.platform.effect.EffectManage;
import com.zdream.pmw.util.json.JsonValue;

/**
 * <p>固定伤害类招式伤害计算公式, 包括:
 * <li>固定伤害值技能;
 * <li>按攻击方等级计算的伤害技能;
 * <li>按防御方当前 HP 计算的伤害技能;
 * <li>按攻击方当前 HP 计算的伤害技能;
 * <li>按攻击方和防御方当前 HP 的差值计算的伤害技能;
 * <li>按自身上次受到的物理 / 特殊伤害的计算的伤害技能;
 * <li>按处于忍耐状态期间受到的总伤害计算的伤害技能;
 * <li>等</li></p>
 * 
 * @since v0.2.2
 *   [2017-04-04]
 * @author Zdream
 * @version v0.2.2
 *   [2017-04-04]
 */
public class FixedDamageFormula extends DefaultDamageFormula {
	
	/* ************
	 *	  属性    *
	 ************ */
	
	/**
	 * 模式
	 */
	private int mode;
	
	/**
	 * <p>固定伤害值技能模式, 默认值</p>
	 */
	public static final int MODE_FIXED_DAMAGE = 0;
	
	/**
	 * 数值
	 */
	private int param;
	
	/**
	 * 比率
	 */
	private float rate;
	
	public int getMode() {
		return mode;
	}
	public void setMode(int mode) {
		this.mode = mode;
	}
	/**
	 * <p>
	 * <li>固定伤害值技能时: 伤害值
	 * </li></p>
	 * @return
	 */
	public int getParam() {
		return param;
	}
	public void setParam(int param) {
		this.param = param;
	}
	/**
	 * 
	 * @return
	 */
	public float getRate() {
		return rate;
	}
	public void setRate(float rate) {
		this.rate = rate;
	}
	
	/* ************
	 *	覆盖方法  *
	 ************ */
	
	protected void onDamage(int index) {
		
		// 属性克制倍率
		if (writeTypeRate(index)) {
			// 没有效果
			sendImmune(index);
			return;
		}
		
		// 不判定会心一击
		// 不计算攻击力
		// 不计算技能威力
		// 不计算防御力
		// 不计算修正
		
		// 伤害计算
		writeDamage(index);
		
		// 实现伤害
		executeDamage(index);
		
	}
	
	protected void writeDamage(int index) {
		// 按照 mode 来计算伤害
		int damage = 0;
		
		switch (mode) {
		case MODE_FIXED_DAMAGE:
			damage = param;

		default:
			break;
		}

		// 先放入 package, 让 box 进行再加工. 这一步不能缺
		pack.setDamage(damage, index);
		damage = pack.getEffects().calcDamage(pack);
		
		pack.getEffects().logPrintf(IPrintLevel.PRINT_LEVEL_VERBOSE,
				"DefaultDamageFormula.writeAt(1): seat=%d 的技能伤害为 %d",
				pack.getAtStaff().getSeat(), damage);
		pack.setDamage(damage, index);
	}
	
	/* ************
	 *	实现方法  *
	 ************ */

	@Override
	public String name() {
		return "fixed";
	}
	
	@Override
	public void set(JsonValue args) {
		Set<Entry<String, JsonValue>> set = args.getMap().entrySet();

		String k;
		JsonValue v;
		
		for (Iterator<Entry<String, JsonValue>> it = set.iterator(); it.hasNext();) {
			Entry<String, JsonValue> entry = it.next();
			k = entry.getKey();
			v = entry.getValue();
			
			try {
				switch (k) {
				case "m": { // mode
					String mode = v.getString();
					switch (mode) {
					case "fixed":
						this.mode = MODE_FIXED_DAMAGE;

					default:
						break;
					}
					
				} break;
				case "p": { // param
					setParam((int) v.getValue());
				} break;
				case "r": { // rate
					setRate(((Number) v.getValue()).floatValue());
				} break;
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
		mode = MODE_FIXED_DAMAGE;
		param = 0;
		rate = 1.0f;
	}
	
	/* ************
	 *	 初始化   *
	 ************ */
	
	private EffectManage em;
	
	public FixedDamageFormula(EffectManage em) {
		this.em = em;
	}

}
