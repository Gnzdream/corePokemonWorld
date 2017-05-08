package com.zdream.pmw.platform.effect.range;

import com.zdream.pmw.platform.attend.AttendManager;

/**
 * <p>将除自己外所有能击中的怪兽作为目标的目标判定公式</p>
 * <p>TODO 由于在 3 on 3 模式的战斗中, 处在对角线上的敌人能不能相互打中,
 * 应该作为变量进行设置. 默认不能打中.<br>
 * 该变量将在以后进行补充</p>
 * 
 * @since v0.2.2 [2017-04-18]
 * @author Zdream
 * @version v0.2.2 [2017-04-18]
 */
public class AllRangeFormula extends SingleRangeFormula {
	
	@Override
	public String name() {
		return "all";
	}
	
	/**
	 * <p>判定全队目标</p>
	 * <p>流程:
	 * <li>忽略玩家的选择, 选定所有攻击方能够击中的、非本阵营的所有座位</li></p>
	 */
	protected byte[] onRange() {
		AttendManager am = em.getAttends();
		final int len = am.seatLength();
		byte[] results = new byte[len - 1]; // 缓存选择的座位
		int idx = 0;
		byte atseat = pack.getAtStaff().getSeat();
		
		for (byte seat = 0; seat < len; seat++) {
			if (am.getParticipant(seat) == null) {
				continue;
			}
			
			if (am.teamForSeat(seat) == atseat) {
				continue;
			}
			
			// TODO 自己能否击中该位置尚不明了
			
			results[idx++] = seat;
		}
		
		if (idx != len - 1) {
			byte[] seats = new byte[idx];
			System.arraycopy(results, 0, seats, 0, idx);
			return seats;
		} else {
			return results;
		}
	}

}
