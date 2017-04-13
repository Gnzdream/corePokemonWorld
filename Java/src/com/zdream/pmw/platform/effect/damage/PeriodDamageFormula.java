package com.zdream.pmw.platform.effect.damage;

import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import com.zdream.pmw.platform.control.IPrintLevel;
import com.zdream.pmw.platform.effect.EffectManage;
import com.zdream.pmw.util.json.JsonValue;
import com.zdream.pmw.util.json.JsonValue.JsonType;
import com.zdream.pmw.util.random.RanValue;

/**
 * <p>蓄力、反作用类型等, 完全释放技能需要多于 1 个回合的技能</p>
 * <p>虽然说该公式的名称为蓄力伤害公式, 但是它只用于在第一回合.</p>
 * 
 * @since v0.2.2
 *   [2017-04-06]
 * @author Zdream
 * @version v0.2.2
 *   [2017-04-06]
 */
public class PeriodDamageFormula extends DefaultDamageFormula {
	
	/* ************
	 *	  属性    *
	 ************ */
	
	/**
	 * 模式
	 */
	private int mode;
	
	/**
	 * <p>像旋风刀一样一回合不做任何动作, 二回合攻击的是 charge</p>
	 * <p>默认值</p>
	 * <p>参数值 "c"</p>
	 */
	public static final int MODE_CHARGE = 0;
	
	/**
	 * <p>像大闹一番技能 (暴走) 一样, 2 至 3 回合连续攻击,
	 * 最后一轮攻击结束后自己混乱</p>
	 * <p>参数值 "t"</p>
	 */
	public static final int MODE_THRASH = 1;
	
	/**
	 * <p>像破坏光线技能一样, 第一回合攻击,
	 * 第二回合将不能行动</p>
	 * <p>参数值 "hb"</p>
	 */
	public static final int MODE_HYPER_BEAM = 2;
	
	/**
	 * 给予以后回合的公式设置参数, JsonList
	 */
	private JsonValue formulaParam;
	
	public int getMode() {
		return mode;
	}
	public void setMode(int mode) {
		this.mode = mode;
	}
	/**
	 * 请不要修改 {@code formulaPatam}
	 */
	public JsonValue getFormulaPatam() {
		return formulaParam;
	}
	public void setFormulaPatam(JsonValue formulaPatam) {
		this.formulaParam = formulaPatam;
	}
	
	/* ************
	 *	覆盖方法  *
	 ************ */
	
	@Override
	protected boolean writeHitable(int index) {
		return true;
	}
	
	@Override
	protected void eachRound() {
		switch (mode) {
		case MODE_CHARGE:
			executeAddition();
			break;
		case MODE_THRASH:
			super.eachRound();
			break;
		case MODE_HYPER_BEAM:
			super.eachRound();
			break;

		default:
			break;
		}
		
		createState();
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
					case "c":
						this.mode = MODE_CHARGE;
						break;
					case "t":
						this.mode = MODE_THRASH;
						break;
					case "hb":
						this.mode = MODE_HYPER_BEAM;
						break;

					default:
						break;
					} 
				} break;
				case "p": {
					this.formulaParam = v;
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
		mode = MODE_CHARGE;
		formulaParam = null;
	}
	
	/* ************
	 *	私有方法  *
	 ************ */
	
	private void createState() {
		JsonValue param = null;
		if (mode == MODE_THRASH) {
			int round = RanValue.random(2, 4);
			if (round == 2) {
				param = new JsonValue(JsonType.ArrayType);
				List<JsonValue> l = param.getArray();
				l.add(formulaParam.getArray().get(1));
			} else { // 3
				param = new JsonValue(JsonType.ArrayType);
				List<JsonValue> l = param.getArray();
				l.add(formulaParam.getArray().get(0));
				l.add(formulaParam.getArray().get(1));
			}
		}
		
		if (param == null) {
			param = this.formulaParam;
		}
		
		JsonValue v = new JsonValue(JsonType.ObjectType);
		v.add("-param", param);
		v.add("-target", new JsonValue(pack.getOriginTarget())); // byte
		
		byte atseat = pack.getAtStaff().getSeat();
		pack.getEffects().sendForceStateMessage("period", atseat, atseat, pack.getSkill().getId(), v);
	}
	
	/* ************
	 *	 初始化   *
	 ************ */
	
	private EffectManage em;
	
	public PeriodDamageFormula(EffectManage em) {
		this.em = em;
	}

}
