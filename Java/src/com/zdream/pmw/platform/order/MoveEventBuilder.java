package com.zdream.pmw.platform.order;

import java.util.ArrayList;
import java.util.List;

import com.zdream.pmw.platform.attend.AttendManager;
import com.zdream.pmw.platform.control.ControlBase;
import com.zdream.pmw.platform.control.ControlManager;
import com.zdream.pmw.platform.control.IPrintLevel;
import com.zdream.pmw.platform.order.event.AEvent;
import com.zdream.pmw.platform.order.event.MoveEvent;
import com.zdream.pmw.platform.order.event.ReplaceEvent;

/**
 * 精灵发动技能事件的产生类<br>
 * <br>
 * <b>v0.2.1</b>
 * <p>添加了能够产生怪兽交换的功能</p>
 * 
 * @since v0.2.1
 * @author Zdream
 * @date 2016年4月6日
 * @version v0.1
 */
public class MoveEventBuilder {
	
	/* ************
	 *	生产方法  *
	 ************ */
	
	/**
	 * 外部调用方法<br>
	 * 将用户端的消息转化成事件并返回<br>
	 * @return
	 */
	public List<AEvent> build() {
		List<AEvent> events = new ArrayList<AEvent>();
		AttendManager am = om.getRoot().getAttendManager();
		ControlManager cm = om.getRoot().getControlManager();
		
		for (byte team = 0; team < cm.teamLength(); team++) {
			if (!am.isExistForTeam(team)) {
				continue;
			}
			buildForEveryTeam(cm.getCtrl(team), events);
		}
		
		return events;
	}
	
	/**
	 * 外部调用方法<br>
	 * <p>将指定的队伍的用户端的消息转化成事件并返回</p>
	 * <p>一般在怪兽濒死后, 请求队伍获得上场的怪兽时调用的</p>
	 * @param team
	 * @return
	 */
	public List<AEvent> buildForTeam(byte team) {
		List<AEvent> events = new ArrayList<AEvent>();
		ControlManager cm = om.getRoot().getControlManager();
		
		buildForEveryTeam(cm.getCtrl(team), events);
		
		return events;
	}
	
	/**
	 * 解析 ControlBase 并添加行动事件
	 * @param base
	 * @param events
	 *   event 列表，将产生的事件添加至此
	 */
	private void buildForEveryTeam(ControlBase base, List<AEvent> events) {
		AttendManager am = om.getRoot().getAttendManager();
		
		// no, skill, target
		byte[] seats = base.getSeats();
		
		for (int i = 0; i < seats.length; i++) {
			byte seat = seats[i];
			
			String cmd = base.getCommand(seat);
			switch (cmd) {
			case ControlBase.COMMAND_MOVES: { // 选择释放技能
				String skillNumStr = base.getParam(seat);
				String targetStr = base.getTarget(seat);
				
				events.add(buildMoveEvent(am.noForSeat(seat), 
						Byte.parseByte(skillNumStr), 
						(targetStr == null) ? (byte) -1 : Byte.parseByte(targetStr)));
			} break;
			
			case ControlBase.COMMAND_REPLACE: { // 选择换怪兽上场
				String replaceStr = base.getParam(seat);
				
				events.add(buildReplaceEvent(am.noForSeat(seat),
						Byte.parseByte(replaceStr), seat));
			} break;
			
			case ControlBase.COMMAND_BAG: { // 选择使用背包道具
				// TODO 添加行动事件: 选择使用背包道具的代码待添加
			} break;
			
			case ControlBase.COMMAND_ESCAPE: { // 选择逃跑
				// TODO 添加行动事件: 选择逃跑的代码待添加
			} break;

			default:
				break;
			}
		}
	}
	
	/**
	 * 创建并返回释放技能的行动事件
	 * @param no
	 * @param skillNum
	 * @param target
	 * @return
	 */
	private MoveEvent buildMoveEvent(byte no, byte skillNum, byte target) {
		MoveEvent event = new MoveEvent();
		
		event.setNo(no);
		event.setSkillNum(skillNum);
		event.setTarget(target);

		// addition TODO （例 先制之爪的发动）
		om.logPrintf(IPrintLevel.PRINT_LEVEL_WARN, 
				"位置：MoveEventBuilder.buildAndPush(3) addition 没有添加到 MoveEvent （例 先制之爪的发动）");
		
		return event;
	}
	
	/**
	 * 创建并返回交换的行动事件
	 * @param no
	 * @param replaceNo
	 * @param seat 
	 * @return
	 */
	private ReplaceEvent buildReplaceEvent(byte no, byte replaceNo, byte seat) {
		ReplaceEvent event = new ReplaceEvent();
		
		event.setNo(no);
		event.setReplaceNo(replaceNo);
		event.setSeat(seat);
		
		return event;
	}
	
	/* ************
	 *	 初始化   *
	 ************ */
	
	OrderManager om;

	public MoveEventBuilder(OrderManager om) {
		this.om = om;
	}

}
