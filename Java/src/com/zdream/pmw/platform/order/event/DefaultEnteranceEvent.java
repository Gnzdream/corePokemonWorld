package com.zdream.pmw.platform.order.event;

import java.util.Arrays;

import com.zdream.pmw.platform.attend.AttendManager;
import com.zdream.pmw.platform.attend.Attendant;
import com.zdream.pmw.platform.control.IPrintLevel;
import com.zdream.pmw.platform.effect.Aperitif;
import com.zdream.pmw.platform.order.OrderManager;
import com.zdream.pmw.platform.prototype.BattlePlatform;

/**
 * 默认将精灵派上场的事件<br>
 * 该事件在每回合结尾时段触发<br>
 * 
 * @since v0.1
 * @author Zdream
 * @date 2016年4月2日
 * @version v0.1
 */
public class DefaultEnteranceEvent extends AAttendantEvent {

	public DefaultEnteranceEvent() {
		super(EventType.LAST);
	}
	
	@Override
	public String toString() {
		return "Event: DefaultEnterance";
	}

	/* ************
	 *	 优先度   *
	 ************ */

	@Override
	public void action(BattlePlatform pf) {
		this.pf = pf;
		am = pf.getAttendManager();
		
		byte[] seats = seatCondition(true);
		enterForEachTeam(seats, pf);
	}
	
	AttendManager am;
	
	/**
	 * 查询每个队伍，并按规则派上场
	 * @param seats
	 */
	private void enterForEachTeam(byte[] seats, BattlePlatform pf) {
		this.pf = pf;
		int teamLength = teamLength();
		
		for (byte team = 0; team < teamLength; team++) {
			if (!am.isExistForTeam(team)) {
				continue;
			}
			// 属于 team 队伍的空座位
			byte[] seatsForTeam = seatInArrayForTeam(seats, team);
			if (seatsForTeam == null) {
				continue; // 没有属于该队伍的空座位
			}
			
			if (zeroRound(pf)) {
				// team 队伍可以派上场的精灵的前 (seatsForTeam.length) 位
				byte[] nos = pmCanEnter(team, seatsForTeam.length);
				if (nos[0] == -1) {
					continue; // 该队伍没有能够上场的精灵
				}
				
				startEntranceCode(team, seatsForTeam, nos);
			} else {
				requestEntranceCode(team, seatsForTeam);
				// TODO 获得请求得到的 nos, 进行上场
				
				OrderManager om = pf.getOrderManager();
				// 添加 MoveEvent
				om.pushEvents(om.buildMoveEventForTeam(team));
			}
		}
		this.pf = null;
		this.am = null;
	}
	
	/**
	 * 现在是不是第零回合
	 * @return
	 */
	private boolean zeroRound(BattlePlatform pf) {
		return pf.getOrderManager().getRound() == 0;
	}
	
	/**
	 * 查询指定队伍可以入场的精灵的列表<br>
	 * 该精灵必须是：<br>
	 * 1.  不在场的<br>
	 * 2.  hpi > 0
	 * @param team
	 *   指定队伍
	 * @param amount
	 *   指定个数
	 * @return
	 *   队伍中前 amount 个能够上场的精灵的 no 号组成的数组
	 */
	private byte[] pmCanEnter(byte team, int amount) {
		int length = am.attendantLength();

		byte[] nos = new byte[amount];
		Arrays.fill(nos, (byte) -1);
		int index = 0;
		
		for (byte no = 0; no < length; no++) {
			Attendant at = am.getAttendant(no);
			if (am.teamForNo(no) == team && am.seatForNo(no) == -1 && at.getHpi() > 0) {
				nos[index ++] = no;
				if (index == amount) {
					break;
				}
			}
		}
		
		return nos;
	}
	
	/**
	 * 默认入场方法
	 * @param team
	 * @param seats
	 * @param nos
	 */
	private void startEntranceCode(byte team, byte[] seats, byte[] nos) {
		
		for (byte seat = 0; seat < seats.length; seat++) {
			if (nos[seat] == -1) {
				break;
			}
			// 入场（前）
			Aperitif value = pf.getEffectManage().newAperitif(Aperitif.CODE_ENTRANCE, seats[seat]);
			value.append("team", team);
			value.append("no", nos[seat]);
			value.append("seat", seats[seat]);
			pf.readyCode(value);
			
			// 入场（后）
			value = pf.getEffectManage().newAperitif(Aperitif.CODE_AFTER_ENTRANCE, seats[seat]);
			value.append("team", team);
			value.append("no", nos[seat]);
			value.append("seat", seats[seat]);
			pf.readyCode(value);
		}
	}
	
	/**
	 * 请求控制方, 选择怪兽入场方法
	 * @param team
	 * @param seats
	 */
	private void requestEntranceCode(byte team, byte[] seats) {
		byte[] nos = pmCanEnter(team, seats.length);
		if (nos.length < seats.length) {
			byte[] temp = seats;
			seats = new byte[nos.length];
			System.arraycopy(temp, 0, seats, 0, seats.length);
		}
		
		Aperitif value = pf.getEffectManage().newAperitif(Aperitif.CODE_REQUEST_PM);
		value.append("seats", seats);
		value.append("team", team);
		pf.readyCode(value);
		
		pf.logPrintf(IPrintLevel.PRINT_LEVEL_WARN, 
				"DefaultEnteranceEvent: 不是在 0 回合时触发入场事件还在施工");
	}

}
