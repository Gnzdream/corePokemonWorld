package com.zdream.pmw.platform.effect.addition;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

import com.zdream.pmw.platform.attend.Participant;
import com.zdream.pmw.platform.control.IPrintLevel;
import com.zdream.pmw.platform.effect.Aperitif;
import com.zdream.pmw.platform.effect.EffectManage;
import com.zdream.pmw.util.json.JsonValue;

/**
 * 吸血或反作用力伤害 / A 类附加效果判定实现类<br>
 * <p>
 * <li>吸血攻击时会回复自己的生命（含自我再生等回复技能）
 * <li>反作用力攻击时, 自己会受到伤害</li></p>
 * 
 * <p><b>v0.2.2</b><br>
 * 添加模式字段, 使该类能够完成类似飞踢、飞膝踢等在未命中时才触发的附加效果</p>
 * 
 * @since v0.2.1
 *   [2017-03-18]
 * @author Zdream
 * @version v0.2.2
 *   [2017-04-13]
 */
public class AbsorbAdditionFormula extends AAdditionFormula {
	
	/* ************
	 *	  属性    *
	 ************ */
	
	/**
	 * 参考方<br>
	 * <li>{@link #REFERENCE_DAMAGE} 为伤害值（默认）
	 * <li>{@link #REFERENCE_LIFE} 为攻击方的生命上限</li>
	 */
	private int reference;
	
	/**
	 * 以伤害值为参考方
	 */
	public static final int REFERENCE_DAMAGE = 0;
	
	/**
	 * 以攻击方的生命上限为参考方
	 */
	public static final int REFERENCE_LIFE = 1;
	
	/**
	 * <p>对敌方进行攻击时, 自己回复/受伤占参考方的倍数</p>
	 * <p>吸血为正数, 受伤为负数</p>
	 */
	private float rate;
	
	/**
	 * 模式
	 */
	private int mode;
	
	/**
	 * 默认模式  参数: "d"
	 */
	public static final int MODE_DEFAULT = 0;
	
	/**
	 * 飞踢模式 (Jump Kick)  参数: "j"
	 * <p>在该模式下, 参考方将设置为攻击方的生命上限, 目标为攻击方, 并且无视修改</p>
	 */
	public static final int MODE_JUMP_KICK = 1;
	
	public int getReference() {
		return reference;
	}
	public void setReference(int reference) {
		this.reference = reference;
	}
	public float getRate() {
		return rate;
	}
	public void setRate(float rate) {
		this.rate = rate;
	}
	public int getMode() {
		return mode;
	}
	public void setMode(int mode) {
		this.mode = mode;
	}
	
	/* ************
	 *	实现方法  *
	 ************ */
	
	@Override
	protected void onStart() {
		if (mode == MODE_JUMP_KICK) {
			reference = REFERENCE_LIFE;
			side = SIDE_ATSTAFF;
		}
	}

	@Override
	public String name() {
		return "absorb";
	}
	
	@Override
	protected boolean canTrigger() {
		if (mode == MODE_JUMP_KICK) {
			if (seatLen > 0) {
				// 飞踢的反伤效果将不会触发
				return false;
			} else {
				// 触发飞踢的反伤效果
				return true;
			}
		}
		return true;
	}
	
	@Override
	protected void force() {
		int refer = calcRefer();
		float value = refer * rate;
		if (Math.abs(value) < 1 && value != 0) {
			if (value > 0) {
				value = 1.0f;
			} else {
				value = -1.0f;
			}
		}
		
		em.logPrintf(EffectManage.PRINT_LEVEL_VERBOSE, 
				"AbsorbAdditionF.addition(): %s(seat=%d) 发动 %s, 数值 %f",
				pack.getAtStaff().getNickname(), pack.getAtStaff().getSeat(),
				(rate > 0) ? "吸血":"反作用", value);
		
		if (side == SIDE_ATSTAFF) {
			em.logPrintf(EffectManage.PRINT_LEVEL_WARN, 
					"AbsorbAdditionF.addition(): 目标为自己的技能还没有完成");
		}
		
		byte atseat = pack.getAtStaff().getSeat();
		byte[] dfseats = pack.dfSeats();
		if (dfseats.length == 0) {
			em.logPrintf(EffectManage.PRINT_LEVEL_VERBOSE, 
					"AbsorbAdditionF.addition(): 吸血、反伤技能没有击中任何对象");
			return;
		}
		
		byte[] scans = new byte[dfseats.length + 1];
		scans[0] = atseat;
		System.arraycopy(dfseats, 0, scans, 1, dfseats.length);
		
		Aperitif ap = em.newAperitif(Aperitif.CODE_ADDITION_SETTLE, scans);
		ap.append("atseat", atseat).append("dfseat", dfseats).append("side", side)
			.append("category", (rate > 0) ? "absorb" : "reaction")
			.append("reference", reference).append("refer", refer).append("target", atseat)
			.append("rate", rate).append("value", value);
		em.startCode(ap);
	}
	
	@Override
	public void set(JsonValue args) {
		super.set(args);
		Set<Entry<String, JsonValue>> set = args.getMap().entrySet();

		String k;
		JsonValue v;
		
		for (Iterator<Entry<String, JsonValue>> it = set.iterator(); it.hasNext();) {
			Entry<String, JsonValue> entry = it.next();
			k = entry.getKey();
			v = entry.getValue();
			
			try {
				switch (k) {
				case "rf": { // reference
					String param = v.getString();
					if ("d".equals(param)) {
						setReference(REFERENCE_DAMAGE);
					} else if ("l".equals(param)) {
						setReference(REFERENCE_LIFE);
					} else {
						em.logPrintf(IPrintLevel.PRINT_LEVEL_WARN, 
								"AbnormalAdditionF.set(1): 施加参考方 %s 不符合要求", param);
					}
				} break;
				case "r": { // rate
					setRate(((Number) v.getValue()).floatValue());
				} break;
				case "m": { // mode
					String param = v.getString();
					if ("j".equals(param)) {
						setMode(MODE_JUMP_KICK);
					} else {
						setMode(MODE_DEFAULT);
					}
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
		super.restore();
		side = SIDE_ATSTAFF;
		reference = REFERENCE_DAMAGE;
		rate = 0;
		mode = MODE_DEFAULT;
	}
	
	/* ************
	 *	私有方法  *
	 ************ */
	
	/**
	 * 计算参考值, 是伤害值或攻击方生命值, {@link reference} 来指定.
	 * @return
	 */
	private int calcRefer() {
		switch (reference) {
		case REFERENCE_DAMAGE: {
			int damage = 0;
			int length = pack.dfStaffLength();
			for (int i = 0; i < length; i++) {
				damage += pack.getDamage(i);
			}
			return damage;
		}
		case REFERENCE_LIFE: {
			return pack.getAtStaff().getStat(Participant.HP);
		}
		default:
			break;
		}
		return 0;
	}
	
	/* ************
	 *	 初始化   *
	 ************ */
	
	private EffectManage em;
	
	public AbsorbAdditionFormula(EffectManage em) {
		this.em = em;
	}
}
