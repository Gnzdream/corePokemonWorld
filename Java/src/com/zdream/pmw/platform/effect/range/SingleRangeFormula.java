package com.zdream.pmw.platform.effect.range;

import com.zdream.pmw.platform.attend.AttendManager;
import com.zdream.pmw.platform.effect.EffectManage;
import com.zdream.pmw.platform.effect.SkillReleasePackage;

/**
 * <p>默认目标判定公式, 攻击单体目标</p>
 * 
 * @since v0.2.2 [2017-04-18]
 * @author Zdream
 * @version v0.2.2 [2017-04-18]
 */
public class SingleRangeFormula implements IRangeFormula {
	
	protected static final byte[] EMPTY_SEATS = new byte[]{};

	@Override
	public String name() {
		return "single";
	}

	public final byte[] range(SkillReleasePackage pack, EffectManage em) {
		this.pack = pack;
		this.em = em;
		
		byte[] result = onRange();

		this.pack = null;
		this.em = null;
		return result;
	}
	
	/**
	 * <p>判定单一敌方目标</p>
	 * <p>流程: 
	 * <li>如果玩家给出的座位合法且该座位上存在怪兽, 就以其为攻击目标;
	 * <li>如果不存在, 尝试计算该攻击方能够击中的所有座位, 然后逐一删选.<br>
	 * 若仅存在一位能够攻击的对象, 则目标就指定为它; 否则无法决定攻击目标, 攻击失败.</li></p>
	 */
	protected byte[] onRange() {
		// 玩家选择的座位号, 可能为 -1
		byte oritgseat = pack.getOriginTarget();
		
		if (oritgseat != -1) {
			byte oritgno = em.getAttends().noForSeat(oritgseat);
			if (oritgno != -1) {
				return new byte[]{oritgseat};
			}
		}
		
		// 自动选择备选流程
		AttendManager am = em.getAttends();
		final int len = am.seatLength();
		byte choose = -1; // 缓存选择的座位
		byte atteam = am.teamForSeat(pack.getAtStaff().getSeat());
		
		for (byte seat = 0; seat < len; seat++) {
			if (am.getParticipant(seat) == null) {
				continue;
			}
			
			if (am.teamForSeat(seat) == atteam) {
				continue;
			}
			
			// 成为备选
			if (choose == -1) {
				choose = seat;
			} else {
				// 出现多于一个的目标
				return EMPTY_SEATS;
			}
		}
		
		if (choose != -1) {
			return new byte[]{choose};
		} else {
			return EMPTY_SEATS;
		}
	}
	
	/* ************
	 *	  其它    *
	 ************ */
	protected SkillReleasePackage pack;
	protected EffectManage em;
	
	@Override
	public String toString() {
		return name();
	}

}
