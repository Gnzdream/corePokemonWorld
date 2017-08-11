package com.zdream.pmw.platform.order.event;

import com.zdream.pmw.platform.attend.AttendManager;
import com.zdream.pmw.platform.prototype.BattlePlatform;
import com.zdream.pmw.util.common.ArraysUtils;

/**
 * 关于所有在场精灵、队伍的有关查询的抽象事件<br>
 * <br>
 * v0.1.1 抽出工具方法到 ArraysUtils.copyArray(2)<br>
 * 
 * @since v0.1.1
 * @author Zdream
 * @date 2016年4月11日
 * @version v0.2
 */
public abstract class AAttendantEvent implements IEvent {
	
	protected BattlePlatform pf;
	
	/* ************
	 *	工具方法  *
	 ************ */
	
	/**
	 * 查看战场中座位上的精灵情况<br>
	 * 返回所有空的或有精灵站着的座位
	 * @param isEmpty
	 *   当前要查询的是空座位的列表或是有精灵站着的座位列表<br>
	 *   true - 查询所有空座位的列表
	 *   false - 查询所有非空座位的列表
	 * @return 
	 */
	protected byte[] seatCondition(boolean isEmpty) {
		AttendManager am = pf.getAttendManager();
		int seatLength = am.seatLength();
		byte[] seats = new byte[seatLength];
		int index = 0;
		
		for (byte seat = 0; seat < seatLength; seat++) {
			if ((am.getParticipant(seat) == null) == isEmpty) {
				seats[index ++] = seat;
			}
		}
		
		return ArraysUtils.splitArray(seats, index);
	}
	
	/**
	 * 返回在 seats 座位列表中，属于 team 队伍的座位元素组成的列表
	 * @param seats
	 * @param team
	 * @return
	 */
	protected byte[] seatInArrayForTeam(byte[] seats, byte team) {
		AttendManager am = pf.getAttendManager();
		byte[] seatsForTeam = new byte[seats.length];
		int index = 0; // index 是对于 seatsForTeam 的索引
		
		for (int i = 0; i < seats.length; i++) { // i 是对于 seats 数组的索引
			if (am.teamForSeat(seats[i]) == team) {
				seatsForTeam[index ++] = seats[i];
			}
		}
		
		if (index == 0) {
			return null;
		} else {
			return ArraysUtils.splitArray(seatsForTeam, index);
		}
	}
	
	/**
	 * 队伍总数
	 * @return
	 */
	protected int teamLength() {
		return pf.getReferee().teamLength();
	}

}
