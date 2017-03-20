package com.zdream.pmw.platform.effect;

import com.zdream.pmw.platform.attend.AttendManager;
import com.zdream.pmw.platform.attend.ESkillRange;
import com.zdream.pmw.platform.attend.SkillRelease;
import com.zdream.pmw.platform.prototype.RuleConductor;

/**
 * 默认目标的计算者<br>
 * 部分技能的目标是非指定性的，比如一些群攻技能，目标是“对方全体”等<br>
 * 需要计算出实际会攻击到的目标 seat<br>
 * 
 * @since v0.1
 * @author Zdream
 * @date 2016年4月5日
 * @version v0.1
 */
public class TargetCounter {

	/* ************
	 *	外部调用  *
	 ************ */
	
	/**
	 * 获得默认的攻击目标<br>
	 * 得到的默认目标指技能能够波及的范围，无论该座位上有没有精灵<br>
	 * @param seat
	 *   技能发动精灵的 seat
	 * @param skillID
	 * @return
	 */
	public byte[] defaultTargets(byte seat, SkillRelease skill) {
		ESkillRange range = skill.getRange();
		switch (range) {
		case SINGLE:
			return defaultTargetForSingle(seat);
			
		case SELF:
			return new byte[]{seat};
			
		case TEAM:
			return defaultTargetForTeam(seat);
			
		case ALL:
			return defaultTargetForTeam(seat);
			
			// TODO 更多的攻击目标

		default:
			break;
		}
		return null;
	}
	
	/**
	 * 单体技能默认的攻击目标<br>
	 * 只能计算出在 1 on 1 模式下的攻击目标
	 * @param seat
	 * @return
	 *   返回的数组长度为 1
	 */
	private byte[] defaultTargetForSingle(byte seat) {
		RuleConductor referee = em.getRoot().getReferee();
		if (referee.seatLength() == 2) { // TODO referee [20160406001]

			// 攻击方的队伍号
			AttendManager am = em.getAttends();
			byte team = am.teamForSeat(seat);
			int seatLength = am.seatLength();
			
			for (byte s = 0; s < seatLength; s++) {
				if (s != seat && am.teamForSeat(s) != team) {
					if (am.getParticipant(s) == null) {
						return new byte[0];
					} else {
						return new byte[]{s};
					}
				}
			}
		}
		throw new IllegalArgumentException("缺失参数：不是 1 on 1 的战斗中没有指定释放技能的目标");
	}
	
	/**
	 * 敌方全体技能默认的攻击目标<br>
	 * @param seat
	 * @return
	 */
	private byte[] defaultTargetForTeam(byte seat) {
		return defaultTargetForSingle(seat);
	}
	
	/* ************
	 *	 初始化   *
	 ************ */
	
	private EffectManage em;

	public TargetCounter(EffectManage em) {
		this.em = em;
	}

}
