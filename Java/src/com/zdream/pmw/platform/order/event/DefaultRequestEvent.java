package com.zdream.pmw.platform.order.event;

import com.zdream.pmw.platform.attend.AttendManager;
import com.zdream.pmw.platform.control.IPrintLevel;
import com.zdream.pmw.platform.effect.Aperitif;
import com.zdream.pmw.platform.order.OrderManager;
import com.zdream.pmw.platform.prototype.BattlePlatform;

/**
 * 默认启动请求各队伍做出行动指令事件<br>
 * 该事件在每回合结尾时段触发<br>
 * 
 * @since v0.1
 * @author Zdream
 * @date 2016年4月2日
 * @version v0.1
 */
public class DefaultRequestEvent extends AAttendantEvent {

	public DefaultRequestEvent() {
		super(EventType.LAST);
	}

	/* ************
	 *	事件发动  *
	 ************ */

	AttendManager am;
	
	@Override
	public void action(BattlePlatform pf) {
		this.pf = pf;
		am = pf.getAttendManager();
		/*
		 * 没有输的队伍给行动请求说明，并等待它们的行动指令的回答
		 * 遍历每一个队伍：team
		 * 1.  获得属于 team 队伍的、在场的精灵的 seat 数组
		 * 2.  在队伍的 RequestSignal 中写入说明数据
		 * 3.  等待队伍指令写入，计数器加一（不阻塞）
		 */
		
		byte[] seats = seatCondition(false);
		requestForEachTeam(seats);
		
		buildMoveEvent();
		
		// 添加默认事件
		pf.getOrderManager().addDefaultEndedEvent();
		
		this.pf = null;
	}
	
	/**
	 * 对于每个队伍，发送请求
	 * @param seats
	 */
	private void requestForEachTeam(byte[] seats) {
		int teamLength = teamLength();
		
		for (byte team = 0; team < teamLength; team++) {
			if (!am.isExistForTeam(team)) {
				continue;
			}
			// 属于 team 队伍的有精灵的座位
			byte[] seatsForTeam = seatInArrayForTeam(seats, team);
			if (seatsForTeam == null) {
				pf.logPrintf(IPrintLevel.PRINT_LEVEL_WARN, 
						"在想发送行动请求时发现 %d 队伍有精灵但是没有入场，因此跳过行动请求", team);
				continue; // 没有属于该队伍的有精灵的座位
			}
			// 发送消息
			pf.logPrintf(IPrintLevel.PRINT_LEVEL_DEBUG, 
					"准备向 %d 队伍发送行动请求", team);
			startRequestMoveCode(team, seatsForTeam);
		}
		
		sleepForRespond();
	}
	
	/**
	 * 发送请求
	 * @param team
	 * @param seats
	 */
	private void startRequestMoveCode(byte team, byte[] seats) {
		Aperitif value = pf.getEffectManage().newAperitif(
				Aperitif.CODE_REQUEST_MOVE, seats); // TODO 缺少 scan-seat
		value.append("team", team).append("seats", seats);
		pf.readyCode(value);
	}
	
	/**
	 * 产生行动事件
	 */
	private void buildMoveEvent() {
		OrderManager om = pf.getOrderManager();
		
		// 添加 MoveEvent
		om.pushEvents(om.buildMoveEvent());
		
		byte[] seats = pf.getAttendManager().existsSeat();
		
		// v0.2.2 发送信息
		Aperitif ap = pf.getEffectManage().newAperitif(Aperitif.CODE_REQUEST_COMMIT, seats);
		pf.getEffectManage().startCode(ap);
	}
	
	/**
	 * 等待所有队伍的回应
	 */
	private void sleepForRespond() {
		pf.getControlManager().inform();
	}

}
