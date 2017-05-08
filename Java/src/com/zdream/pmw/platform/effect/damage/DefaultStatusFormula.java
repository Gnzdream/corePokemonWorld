package com.zdream.pmw.platform.effect.damage;

import com.zdream.pmw.platform.effect.EffectManage;
import com.zdream.pmw.platform.effect.SkillReleasePackage;

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
@Deprecated
public class DefaultStatusFormula implements IDamageFormula {

	@Override
	public String name() {
		return "status";
	}

	@Override
	public final int[] damage(SkillReleasePackage pack, EffectManage em) {
		/*this.pack = pack;
		
		// 判断是否发动失败 + 是否有效
		// 注意, 像麻痹之类的行动判定早已经完成, 这里不做这类数据的判断
		if (!canAct()) {
			return;
		}
		
		int length = pack.dfStaffLength();
		
		// 命中判定
		boolean hit = false; // 为 true 时说明至少对一名防御方，攻击命中且有效
		for (int index = 0; index < length; index++) {
			pack.setThiz(index);
			if (writeHitable()) {
				if (isEffective()) {
					hit = true;
				} else {
					sendImmune(index);
				}
			} else {
				sendMiss(index);
			}
		}
		
		if (hit == false) {
			// 对任何怪兽攻击无效: 攻击终止
			return;
		}
		
		onAddition();*/
		
		// do nothing
		return null;
	}

	/*
	 * 询问是否发动失败
	 * @return
	 *
	protected boolean canAct() {
		return true;
	}
	
	/**
	 * 询问技能是否有效
	 * @return
	 *
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
	 *
	protected boolean writeHitable() {
		int index = pack.getThiz();
		byte atseat = pack.getAtStaff().getSeat(); // 攻击方的 seat
		
		byte dfseat; // 防御方的 seat
		float rate; // 命中参数，攻击方的命中等级和防御方的躲避等级共同决定, 基数 1.0f
		boolean result = false;
		
		AttendManager am = pack.getAttends();

		dfseat = pack.getDfStaff(index).getSeat();
		// rate 计算了命中和躲避能力等级计算后的命中数值
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

		result = !RanValue.isBigger((int) (1000 * rate), 1000);
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
	 *
	protected float countHitrate() {
		return this.pack.getEffects().calcHitrate(pack);
	}
	
	/**
	 * 计算防御方实时躲避率（忽略能力变化）
	 * @return
	 *
	protected float countHide() {
		return this.pack.getEffects().calcHide(pack);
	}
	
	/**
	 * 计算技能实时命中率
	 * @return
	 *
	protected float countAccuracy() {
		return this.pack.getEffects().calcAccuracy(pack);
	}
	
	/**
	 * 由于攻击没有命中，发出开胃酒消息
	 * @param index
	 *
	protected void sendMiss(int index) {
		Aperitif ap = pack.getEffects().newAperitif(Aperitif.CODE_BROADCAST);
		ap.append("type", "miss").append("-atseat", pack.getAtStaff().getSeat())
			.append("-dfseat", pack.getDfStaff(index).getSeat());
		pack.getEffects().startCode(ap);
	}
	
	/**
	 * 由于属性免疫，发出开胃酒消息
	 * @param index
	 *
	protected void sendImmune(int index) {
		Aperitif ap = pack.getEffects().newAperitif(Aperitif.CODE_BROADCAST);
		ap.append("type", "immune").append("-atseat", pack.getAtStaff().getSeat())
			.append("-dfseat", pack.getDfStaff(index).getSeat());
		pack.getEffects().startCode(ap);
	}
	
	/**
	 * 附加状态 / 附加效果实现
	 * @param index
	 *
	private void onAddition() {
		IAdditionFormula[] formulas = pack.getAdditionFormulas();
		for (int i = 0; i < formulas.length; i++) {
			formulas[i].addition(pack);
		}
	}
	
	/* ************
	 *	数据结构  *
	 ************ *
	/**
	 * 释放数据包
	 *
	private SkillReleasePackage pack;
	
	protected SkillReleasePackage getPack() {
		return pack;
	}*/

}
