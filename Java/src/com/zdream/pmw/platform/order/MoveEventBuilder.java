package com.zdream.pmw.platform.order;

import java.util.ArrayList;
import java.util.List;

import com.zdream.pmw.platform.attend.AttendManager;
import com.zdream.pmw.platform.control.ControlBase;
import com.zdream.pmw.platform.control.ControlManager;
import com.zdream.pmw.platform.control.IPrintLevel;
import com.zdream.pmw.platform.order.event.IEvent;
import com.zdream.pmw.platform.order.event.MoveEvent;
import com.zdream.pmw.platform.order.event.ReplaceEvent;
import com.zdream.pmw.util.json.JsonObject;

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
	 * 将用户端的消息转化成事件并添加到系统的行动列表中<br>
	 * 
	 * <p><b>v0.2.2</b><br>
	 * 它行动方式进行了改动, 为有消息的控制体产生行动事件, 而不是所有控制体.</p>
	 */
	public void build() {
		List<IEvent> events = new ArrayList<IEvent>();
		AttendManager am = om.getRoot().getAttendManager();
		ControlManager cm = om.getRoot().getControlManager();
		
		for (byte team = 0; team < cm.teamLength(); team++) {
			if (!am.isExistForTeam(team)) {
				continue;
			}
			ControlBase ctrl = cm.getCtrl(team);
			if (ctrl.isLoaded()) {
				buildForEveryTeam(ctrl, events);
				ctrl.clear();
			}
		}
		
		IEvent[] es = new IEvent[events.size()];
		storage.store(events.toArray(es));
	}
	
	/**
	 * 外部调用方法<br>
	 * <p>将指定的队伍的用户端的消息转化成事件并返回</p>
	 * <p>一般在怪兽濒死后, 请求队伍获得上场的怪兽时调用的</p>
	 * @param team
	 * @return
	 */
	public List<IEvent> buildForTeam(byte team) {
		List<IEvent> events = new ArrayList<IEvent>();
		ControlBase ctrl = om.getRoot().getControlManager().getCtrl(team);

		if (ctrl.isLoaded()) {
			buildForEveryTeam(ctrl, events);
			ctrl.clear();
		}
		
		return events;
	}
	
	/**
	 * <p>公共方法, 创建释放技能的行动事件并添加到指定的容器中</p>
	 * @param no
	 *   行动主体怪兽的 seat
	 * @param skillNum
	 *   选择怪兽的第几个技能
	 * @param target
	 *   选择目标, 默认为 -1
	 * @param events
	 *   盛放事件的列表, 生成的事件将加入到这里去
	 * @since v0.2.2
	 */
	public void buildMoveEvent(byte no,
			byte skillNum,
			byte target) {
		this.storage.store(this.buildMoveEvent0(no, skillNum, target));
	}
	
	/**
	 * <p>公共方法, 创建释放技能的行动事件并添加到指定的容器中</p>
	 * @param no
	 *   行动主体怪兽的 seat
	 * @param skillNum
	 *   选择怪兽的第几个技能
	 * @param target
	 *   选择目标, 默认为 -1
	 * @param events
	 *   盛放事件的列表, 生成的事件将加入到这里去
	 * @param param
	 *   其它附加参数, 用于修改技能释放时的技能参数、触发方式、结算公式等
	 * @since v0.2.2
	 */
	public void buildMoveEvent(byte no,
			byte skillNum,
			byte target,
			JsonObject param) {
		this.storage.store(this.buildMoveEvent0(no, skillNum, target, param));
	}
	
	/**
	 * 解析 ControlBase 并添加行动事件
	 * @param base
	 * @param events
	 *   event 列表，将产生的事件添加至此
	 */
	private void buildForEveryTeam(ControlBase base, List<IEvent> events) {
		AttendManager am = om.getRoot().getAttendManager();
		
		// no, skill, target
		byte[] seats = base.getSeats();
		
		for (int i = 0; i < seats.length; i++) {
			byte seat = seats[i];
			
			String cmd = base.getCommand(seat);
			switch (cmd) {
			case ControlBase.COMMAND_MOVES: { // 选择释放技能
				int skillNum = base.getParam(seat);
				byte target = base.getTarget(seat);
				
				events.add(buildMoveEvent0(am.noForSeat(seat), 
						skillNum, target));
			} break;
			
			case ControlBase.COMMAND_REPLACE: { // 选择换怪兽上场
				byte replace = (byte) base.getParam(seat);
				
				events.add(buildReplaceEvent(am.noForSeat(seat),
						replace, seat));
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
	private MoveEvent buildMoveEvent0(byte no, int skillNum, byte target) {
		MoveEvent event = new MoveEvent();
		
		event.setNo(no);
		event.setSkillNum(skillNum);
		event.setTarget(target);

		// addition TODO （例 先制之爪的发动）
		om.logPrintf(IPrintLevel.PRINT_LEVEL_WARN, 
				"位置：MoveEventBuilder.buildMoveEvent0(3) addition 没有添加到 MoveEvent （例 先制之爪的发动）");
		
		return event;
	}
	
	/**
	 * 创建并返回释放技能的行动事件
	 * @param no
	 * @param skillNum
	 * @param target
	 * @param param
	 *   附加参数
	 * @return
	 */
	private MoveEvent buildMoveEvent0(byte no, byte skillNum, byte target, JsonObject param) {
		MoveEvent event = new MoveEvent();
		
		event.setNo(no);
		event.setSkillNum(skillNum);
		event.setTarget(target);
		event.setParam(param);

		// addition TODO （例 先制之爪的发动）
		om.logPrintf(IPrintLevel.PRINT_LEVEL_WARN, 
				"位置：MoveEventBuilder.buildMoveEvent0(4) addition 没有添加到 MoveEvent （例 先制之爪的发动）");
		
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
	IMoveEventStorage storage;

	public MoveEventBuilder(OrderManager om) {
		this.om = om;
	}

}
