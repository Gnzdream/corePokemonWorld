package com.zdream.pmw.platform.effect.addition;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

import com.zdream.pmw.platform.attend.Participant;
import com.zdream.pmw.platform.control.IPrintLevel;
import com.zdream.pmw.platform.effect.Aperitif;
import com.zdream.pmw.platform.effect.EffectManage;
import com.zdream.pmw.platform.effect.SkillReleasePackage;
import com.zdream.pmw.util.json.JsonValue;

/**
 * 吸血或反作用力伤害 / A 类附加效果判定实现类<br>
 * <p>
 * <li>吸血攻击时会回复自己的生命（含自我再生等回复技能）
 * <li>反作用力攻击时, 自己会受到伤害</li></p>
 * 
 * @since v0.2.1
 * @author Zdream
 * @date 2017年3月18日
 * @version v0.2.1
 */
public class AbsorbAdditionFormula implements IAdditionFormula {
	
	/* ************
	 *	数据结构  *
	 ************ */
	
	/**
	 * 向哪方进行操作<br>
	 * <li>{@link #SIDE_ATSTAFF} 为攻击方（默认）
	 * <li>{@link #SIDE_DFSTAFF} 为防御方</li>
	 */
	private int side;
	
	/**
	 * 向攻击方操作
	 */
	public static final int SIDE_ATSTAFF = 1;
	
	/**
	 * 向防御方操作
	 */
	public static final int SIDE_DFSTAFF = 0;
	
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

	public int getSide() {
		return side;
	}
	public void setSide(int side) {
		this.side = side;
	}
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
	
	/* ************
	 *	实现方法  *
	 ************ */

	@Override
	public String name() {
		return "absorb";
	}

	@Override
	public void addition(SkillReleasePackage pack) {
		this.pack = pack;
		
		int refer = calcRefer();
		float value = refer * rate;
		em.logPrintf(EffectManage.PRINT_LEVEL_VERBOSE, 
				"AbsorbAdditionF.addition(): %s(seat=%d) 发动 %s, 数值 %f",
				pack.getAtStaff().getNickname(), pack.getAtStaff().getSeat(),
				(refer > 0) ? "吸血":"反作用", value);
		
		byte atseat = pack.getAtStaff().getSeat();
		byte dfseat = pack.getDfStaff(pack.getThiz()).getSeat();
		
		Aperitif ap = em.newAperitif(Aperitif.CODE_ADDITION_SETTLE, atseat, dfseat);
		ap.append("atseat", atseat).append("dfseat", dfseat).append("side", side)
			.append("category", (refer > 0) ? "absorb" : "reaction")
			.append("reference", reference).append("refer", refer)
			.append("rate", rate).append("value", value);
		em.startCode(ap);
		
		this.pack = null;
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
				} break;
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
		side = SIDE_ATSTAFF;
		reference = REFERENCE_DAMAGE;
		rate = 0;
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
	
	private SkillReleasePackage pack;
	private EffectManage em;
	
	public AbsorbAdditionFormula(EffectManage em) {
		this.em = em;
	}
}
