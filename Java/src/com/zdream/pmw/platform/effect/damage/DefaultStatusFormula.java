package com.zdream.pmw.platform.effect.damage;

import com.zdream.pmw.platform.attend.AttendManager;
import com.zdream.pmw.platform.attend.service.ParticipantAbilityHandler;
import com.zdream.pmw.platform.control.IPrintLevel;
import com.zdream.pmw.platform.effect.SkillReleasePackage;
import com.zdream.pmw.platform.effect.addition.IAdditionFormula;
import com.zdream.pmw.util.random.RanValue;

/**
 * 默认变化类技能的 (伤害) 计算公式<br>
 * <p><b>计算特点</b></p>
 * <li>判断是否命中
 * <li>判断是否发动失败 + 是否有效
 * <li>不判断伤害相关
 * 
 * @since v0.2.1
 * @author Zdream
 * @date 2017年3月4日
 * @version v0.2.1
 */
public class DefaultStatusFormula implements IDamageFormula {

	@Override
	public String name() {
		return "status";
	}

	@Override
	public final void damage(SkillReleasePackage pack) {
		this.pack = pack;
		
		// 判断是否发动失败 + 是否有效
		if (!canAct()) {
			// TODO
			return;
		}
		if (!isEffective()) {
			// TODO
			return;
		}
		
		int length = pack.dfStaffLength();
		
		// 命中判定
		boolean hit = false;
		for (int index = 0; index < length; index++) {
			pack.setThiz(index);
			if (writeHitable()) {
				hit = true;
			}
		}
		
		if (hit == false) {
			// 没有命中任何怪兽 TODO
		}
		
		for (int index = 0; index < length; index++) {
			if (!pack.isHitable(index)) {
				continue;
			}
			pack.setThiz(index);
			onAddition();
		}
		
	}

	/**
	 * 询问是否发动失败
	 * @return
	 */
	protected boolean canAct() {
		return true;
	}
	
	/**
	 * 询问技能是否有效
	 * @return
	 */
	protected boolean isEffective() {
		return true;
	}
	
	/**
	 * 判定命中并写入释放数据包<br>
	 * 该方法的结果是释放数据包中的所有防御方精灵的是否命中被写入<br>
	 * <br>
	 * 如果没有命中，还会向系统外发送没有命中的消息<br>
	 * @return
	 *   是否命中
	 */
	protected boolean writeHitable() {
		int index = pack.getThiz();
		byte atseat = pack.getAtStaff().getSeat(); // 攻击方的 seat
		
		byte dfseat; // 防御方的 seat
		int rate; // 命中参数，攻击方的命中等级和防御方的躲避等级共同决定
		boolean result = false;
		
		AttendManager am = pack.getAttends();

		dfseat = pack.getDfStaff(index).getSeat();
		// rate 计算了命中和躲避能力等级计算后的命中数值, 基点 150
		rate = am.hitableLevel(atseat, dfseat);
		// 攻击方命中 (必中负数)
		pack.setAtStaffAccuracy(countHitrate(), index);
		// 防御方躲避 (必不中负数)
		pack.setDfStaffHide(countHide(), index);
		// 技能命中 (必中0, 攻击方命中优先, 防御方躲避其次, 技能命中再次)
		pack.setSkillAccuracy((byte) countAccuracy());
		
		if (pack.getAtStaffAccuracy(index) < 0) {
			pack.setHitable(true, index);
			return true;
		}
		if (pack.getDfStaffHide(index) < 0) {
			pack.setHitable(false, index);
			return false;
		}
		if (pack.getSkillAccuracy() == 0) {
			pack.setHitable(true, index);
			return true;
		}
		
		rate *= (pack.getAtStaffAccuracy(index) / pack.getDfStaffHide(index)
				* pack.getSkillAccuracy() / 100.0f);

		result = !RanValue.isBigger(rate, ParticipantAbilityHandler.TG_BASE);
		am.logPrintf(IPrintLevel.PRINT_LEVEL_VERBOSE,
				"DefaultDamageFormula.writeHitable(1): seat = %d 的命中判定 %s", dfseat, result);
		pack.setHitable(result, index);
		
		if (!result) {
			// 没有命中
			am.logPrintf(IPrintLevel.PRINT_LEVEL_INFO,
					"DefaultDamageFormula.writeHitable(1): seat=%d 的攻击没有命中", 
					pack.getDfStaff(index).getSeat());
			am.logPrintf(IPrintLevel.PRINT_LEVEL_WARN,
					"DefaultDamageFormula.writeHitable(1): 没有命中，没有向外部触发信息");
		}

		return result;
	}
	
	/**
	 * 计算攻击方实时命中率（忽略能力变化）
	 * @return
	 */
	protected float countHitrate() {
		return this.pack.getEffects().calcHitrate(pack);
	}
	
	/**
	 * 计算防御方实时躲避率（忽略能力变化）
	 * @return
	 */
	protected float countHide() {
		return this.pack.getEffects().calcHide(pack);
	}
	
	/**
	 * 计算技能实时命中率
	 * @return
	 */
	protected float countAccuracy() {
		return this.pack.getEffects().calcAccuracy(pack);
	}
	
	/**
	 * 附加状态 / A 类附加效果实现
	 * @param index
	 */
	private void onAddition() {
		IAdditionFormula[] formulas = pack.getAdditionFormulas();
		for (int i = 0; i < formulas.length; i++) {
			formulas[i].addition(pack);
		}
	}
	
	/* ************
	 *	数据结构  *
	 ************ */
	/**
	 * 释放数据包
	 */
	private SkillReleasePackage pack;
	
	protected SkillReleasePackage getPack() {
		return pack;
	}

}
