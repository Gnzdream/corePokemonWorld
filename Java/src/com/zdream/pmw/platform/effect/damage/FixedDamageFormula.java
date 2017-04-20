package com.zdream.pmw.platform.effect.damage;

import java.util.Iterator;
import java.util.Set;
import java.util.Map.Entry;

import com.zdream.pmw.platform.attend.AttendManager;
import com.zdream.pmw.platform.attend.Participant;
import com.zdream.pmw.platform.control.IPrintLevel;
import com.zdream.pmw.platform.effect.EffectManage;
import com.zdream.pmw.platform.effect.SkillReleasePackage;
import com.zdream.pmw.platform.order.OrderManager;
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
	 * <p>一击必杀技模式</p>
	 */
	public static final int MODE_ONE_HIT = 1;
	
	/**
	 * <p>双倍奉还模式, 包括技能双倍奉还、镜面反射、金属爆炸</p>
	 * <p>造成（本回合内）使用者上一次受到的 <code>物理伤害 * rate</code> 的固定伤害, 属性免疫对该招式有效.<br>
	 * 招式目标固定为上一次使用物理(双倍奉还)或特殊(镜面反射)或全部(金属爆炸)招式攻击自身的对方. 
	 * 如果没有合适的目标，招式使用失败。
	 * 双倍奉还不能被双倍奉还, 镜面反射不能被镜面反射</p>
	 * <p><code>param</code> 用来记录查询的技能类别.<br>
	 * 1 为物理, 2 为特殊, 3 为物理与特殊</p>
	 */
	public static final int MODE_COUNTER = 2;
	
	/**
	 * 造成的伤害值等于攻击方等级
	 */
	public static final int MODE_AT_LEVEL = 3;
	
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
	
	@Override
	protected boolean judgeValid() {
		switch (mode) {
		case MODE_ONE_HIT:
			return pack.getAtStaff().getAttendant().getLevel() >=
				pack.getDfStaff(pack.getThiz()).getAttendant().getLevel();
		default:
			return true;
		}
	}
	
	protected void writeDamage(int index) {
		// 按照 mode 来计算伤害
		int damage = 0;
		
		switch (mode) {
		case MODE_FIXED_DAMAGE:
			damage = param;
			break;
		case MODE_ONE_HIT:
			damage = pack.getDfStaff(pack.getThiz()).getStat(Participant.HP);
			break;
		case MODE_COUNTER:
			damage = this.searchCounterDamage();
			break;
		case MODE_AT_LEVEL:
			damage = pack.getAtStaff().getAttendant().getLevel();
			break;

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
	
	/**
	 * 查找上一次收到的攻击伤害
	 * @return
	 *   -1 为没找到
	 */
	private int searchCounterDamage() {
		AttendManager am = pack.getAttends();
		OrderManager om = am.getRoot().getOrderManager();
		int curRound = om.getRound();
		SkillReleasePackage[] ps = om.getReleasePackage(curRound);
		
		// 当前攻击方
		byte curno = pack.getAtStaff().getNo();
		byte curteam = am.teamForNo(curno);
		// 注意, 到现在为止, 防御方还没有确定
		
		for (int i = 0; i < ps.length; i++) {
			SkillReleasePackage p = ps[i];
			
			int thiz = -1;
			int len = p.dfStaffLength();
			
			// 当时防御方是否为当前攻击方
			for (int j = 0; j < len; j++) {
				if (p.getDfStaff(j).getNo() == curno) {
					if (p.isHitable(j)) {
						thiz = j;
					}
					break;
				}
			}
			if (thiz == -1) {
				continue;
			}
			
			// 当时攻击方是否为当前攻击方队友或自己
			if (am.teamForNo(p.getAtStaff().getNo()) == curteam) {
				continue;
			}
			
			if (p.getSkill().getId() != pack.getSkill().getId()) {
				int damage = p.getDamage(thiz);
				if (damage > 0) {
					// 确定攻击目标
					// pack.set
					
					return damage;
				}
			}
		}
		
		return 0;
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
						break;
					case "onehit":
						this.mode = MODE_ONE_HIT;
						break;
					case "atlv":
						this.mode = MODE_AT_LEVEL;
						break;

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
