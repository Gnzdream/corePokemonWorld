package com.zdream.pmw.platform.order.event;

import java.util.Arrays;

import com.zdream.pmw.platform.attend.AttendManager;
import com.zdream.pmw.platform.attend.Attendant;
import com.zdream.pmw.platform.effect.Aperitif;
import com.zdream.pmw.platform.prototype.BattlePlatform;

/**
 * 默认将精灵派上场的事件<br>
 * 该事件在每回合结尾时段触发<br>
 * <br>
 * <p><b>v0.2.1</b><br>
 * 增加参数是否为零回合的记号</p>
 * 
 * @since v0.1
 * @author Zdream
 * @date 2016年4月2日
 * @version v0.2.1
 */
public class DefaultEnteranceEvent extends AAttendantEvent {

	/* ************
	 *	  属性    *
	 ************ */
	
	/**
	 * 判断现在是不是第零回合的记号
	 */
	boolean zeroRound;

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
		
		boolean exist = false; // 在非第零回合中, 如果需要有怪兽入场, 该参数为 trues
		for (byte team = 0; team < teamLength; team++) {
			if (!am.isExistForTeam(team)) {
				continue;
			}
			// 属于 team 队伍的空座位
			byte[] seatsForTeam = seatInArrayForTeam(seats, team);
			if (seatsForTeam == null) {
				continue; // 没有属于该队伍的空座位
			}
			// team 队伍可以派上场的精灵的前 (seatsForTeam.length) 位
			byte[] nos = pmCanEnter(team, seatsForTeam.length);
			if (nos[0] == -1) {
				continue; // 该队伍没有能够上场的精灵
			}
			
			if (zeroRound) {
				startEntranceCode(team, seatsForTeam, nos);
			} else {
				sendRequestPokemonMsg(team, seatsForTeam);
				exist = true;
			}
		}
		
		if (exist) {
			pf.getControlManager().inform();
			pf.getOrderManager().pushEvents(pf.getOrderManager().buildMoveEvent());
		}
		
		this.pf = null;
		this.am = null;
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
			
			pf.getEffectManage().enteranceAct(seats[seat], nos[seat]);
		}
	}
	
	/**
	 * 向系统发送请求选择精灵上场的拦截前消息
	 * @param team
	 *   该濒死精灵所属的队伍
	 * @param seats
	 *   该队伍中的空位
	 */
	private void sendRequestPokemonMsg(byte team, byte[] seats) {
		Aperitif value = pf.getEffectManage().newAperitif(Aperitif.CODE_REQUEST_PM);
		value.append("seats", seats);
		value.append("team", team);
		pf.readyCode(value);
	}

	/* ************
	 *	 初始化   *
	 ************ */

	public DefaultEnteranceEvent() {
		this(false);
	}

	public DefaultEnteranceEvent(boolean zeroRound) {
		super(EventType.LAST);
		this.zeroRound = zeroRound;
	}

}
