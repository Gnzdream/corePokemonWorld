package com.zdream.pmw.platform.effect.damage;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.zdream.pmw.platform.control.IPrintLevel;
import com.zdream.pmw.platform.effect.EffectManage;
import com.zdream.pmw.util.json.JsonValue;

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
	 */
	public static final int MODE_CHARGE = 0;
	
	/**
	 * 给予以后回合的公式设置参数, JsonList
	 */
	private JsonValue formulaPatam;
	
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
		return formulaPatam;
	}
	public void setFormulaPatam(JsonValue formulaPatam) {
		this.formulaPatam = formulaPatam;
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
			// do nothing
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
					case "charge":
						this.mode = MODE_CHARGE;
						break;

					default:
						break;
					} 
				} break;
				case "p": {
					this.formulaPatam = v;
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
		formulaPatam = null;
	}
	
	/* ************
	 *	私有方法  *
	 ************ */
	
	private void createState() {
		Map<String, Object> v = new HashMap<>();
		v.put("-param", this.formulaPatam);
		v.put("-target", pack.getOriginTarget()); // byte
		
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
