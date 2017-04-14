package com.zdream.pmw.platform.order;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.zdream.pmw.platform.attend.AttendManager;
import com.zdream.pmw.platform.attend.service.ParticipantAbilityHandler;
import com.zdream.pmw.platform.control.IPrintLevel;
import com.zdream.pmw.platform.order.event.AEvent;
import com.zdream.pmw.platform.order.event.APreviousEvent;
import com.zdream.pmw.platform.order.event.DefaultEndEvent;
import com.zdream.pmw.platform.order.event.DefaultEnteranceEvent;
import com.zdream.pmw.platform.order.event.DefaultRequestEvent;
import com.zdream.pmw.platform.order.event.MoveEvent;
import com.zdream.pmw.platform.prototype.BattlePlatform;

/**
 * 触发列表管理器<br>
 * <br>
 * <b>v0.1.1</b><br>
 *   添加注释;<br>
 *   将回合结束事件加入到默认的系统事件内<br>
 * <br>
 * <b>v0.2</b><br>
 *   取消了对 pf.instance 的直接引用<br>
 * <br>
 * <b>v0.2.1</b>
 * <p>修改了默认的三个回合结束事件触发的顺序.<br>
 * 见 {@link #addDefaultEndedEvent()}</p>
 * 
 * @since v0.1.1
 * @author Zdream
 * @date 2016年4月19日
 * @version v0.2
 */
public class EventList {
	
	OrderManager om;
	
	/* ************
	 *	数据结构  *
	 ************ */
	/**
	 * 触发列表存放了三种能够触发的事件：<br>
	 * <b>提前触发事件</b>、<b>行动事件</b>、<b>收尾事件</b><br>
	 * <br>
	 * <b>提前触发事件</b>有：静电等特性触发的事件、连续攻击的事件、死亡强制退场事件等<br>
	 *   这些事件的共同特点是，它可以在一般的精灵释放技能的事件运行前触发<br>
	 * 举个例子，精灵 A 用撞击攻击 B，在 B 的技能释放前，B 的静电特性触发了<br>
	 *   就说明静电特性的事件可以插在 B 的技能释放前，优先执行<br>
	 *   因此可以看出静电特性事件属于提前触发事件<br>
	 * 还有部分技能在一定的情况之下属于提前触发事件而非行动事件<br>
	 *   比如在某方准备换精灵的时候，对方的技能追击优先触发，这时的追击技能就属于<b>提前触发事件</b><br>
	 * <br>
	 * <b>行动事件</b>就可以看成训练师或 AI 对精灵做出的指令<br>
	 * 包括技能释放、嗑药、更换精灵等<br>
	 * <br>
	 * <b>提前触发事件</b>在<b>行动事件</b>全部运行完之后发动<br>
	 * 包含的系统事件有：<br>
	 *   座位为空，强制换精灵上场的事件 <code>DefaultEnteranceEvent</code><br>
	 *   默认结尾处理事件（比如中毒扣血） <code>DefaultEndEvent</code><br>
	 *   为训练师或 AI 发送行动请求的事件 <code>DefaultRequestEvent</code><br>
	 *   等
	 */
	List<AEvent> previousEvent = new LinkedList<AEvent>(),
			movesEvent = new LinkedList<AEvent>(), 
			lastEvent = new LinkedList<AEvent>();
	
	/**
	 * 返回下一个 <code>AEvent</code><br>
	 * 首先看提前触发事件列表，如果列表不为空，返回优先度最高的事件<br>
	 * 如果提前触发事件列表为空，查看行动事件列表，如果不为空，返回优先度最高的事件<br>
	 * 如果行动事件列表为空，查看收尾事件，返回列表第一个事件<br>
	 * <br>
	 * 一般收尾事件是不会为空的，因为收尾事件中存放系统相关的事件<br>
	 * @return
	 */
	public AEvent nextEvent() {
		if (!previousEvent.isEmpty()) {
			AEvent event = nextPreviousEvent();
			previousEvent.remove(event);
			return event;
		} else if (!movesEvent.isEmpty()) {
			AEvent event = nextMovesEvent();
			movesEvent.remove(event);
			return event;
		} else if (!lastEvent.isEmpty()) {
			AEvent event = lastEvent.get(0);
			lastEvent.remove(event);
			return event;
		} else {
			om.logPrintf(IPrintLevel.PRINT_LEVEL_ERROR, 
					"EventList.nextEvent() 位置：%s 没有任何能够进行的事件", this.getClass().getName());
			throw new NullPointerException("没有任何能够进行的事件");
		}
	}
	
	/**
	 * 在提前触发事件中选出最早触发的事件<br>
	 * 由于提前触发的事件都继承抽象事件类，可以利用优先度来排序，选出优先度最高的事件<br>
	 * 当优先度相同时，返回相对开头的事件（在列表中索引小的事件）<br>
	 * @return
	 */
	private AEvent nextPreviousEvent() {
		Iterator<AEvent> it = previousEvent.iterator();
		
		APreviousEvent selected = (APreviousEvent) it.next();
		
		for (; it.hasNext();) {
			APreviousEvent event = (APreviousEvent) it.next();
			if (event.compareTo(selected) > 0) {
				selected = event;
			}
		}
		
		return selected;
	}
	
	/**
	 * 添加新的 <code>AEvent</code><br>
	 * 根据事件的类型，它是提前触发的、行动的、还是结尾的事件<br>
	 * 将其放入对应的列表<br>
	 * @see EventList#previousEvent
	 * @see EventList#movesEvent
	 * @see EventList#lastEvent
	 * @param event
	 */
	public void pushEvent(AEvent event) {
		om.logPrintf(IPrintLevel.PRINT_LEVEL_DEBUG,
				"EventList.pushEvent() 尝试添加一个事件 %s, 类型 %s", event.getClass().getSimpleName(), event.getType());
		switch (event.getType()) {
		case PREVIOUS:
			previousEvent.add(event);
			break;
		case MOVE:
			movesEvent.add(event);
			break;
		case LAST:
			lastEvent.add(event);
			break;
		default:
			om.logPrintf(IPrintLevel.PRINT_LEVEL_WARN,
					"EventList.pushEvent() 尝试添加一个未知类型的事件被拒绝：%s", event.getClass().getName());
		}
	}
	
	/**
	 * 添加多数 Event 列表
	 * @param events
	 */
	public void pushEvents(List<AEvent> events) {
		for (Iterator<AEvent> it = events.iterator(); it.hasNext();) {
			AEvent event = it.next();
			pushEvent(event);
		}
	}
	
	/**
	 * 删除对应 no 精灵的行动事件，并返回<br>
	 * 可能因为有精灵已经处于濒死，那么该精灵的行动事件不会发动<br>
	 * @param no
	 *   指定精灵的 no
	 * @return
	 *   该事件的实例
	 */
	public MoveEvent removeMoveEvent(byte no) {
		om.logPrintf(IPrintLevel.PRINT_LEVEL_INFO,
				"EventList.removeMoveEvent(1) 尝试删除 no=%d 的行动事件", no);
		for (Iterator<AEvent> it = movesEvent.iterator(); it.hasNext();) {
			MoveEvent event = (MoveEvent) it.next();
			if (event.getNo() == no) {
				movesEvent.remove(event);
				return event;
			}
		}
		return null;
	}
	
	/**
	 * 添加默认的回合结束事件
	 * <br>
	 * <p><b>v0.2.1</b><br>
	 * 之前的顺序是 enterance, end, request, 现在改为 end, enterance, request.<br>
	 * end 事件会同时发动中毒、天气伤害等事件, 而这些事件在上场之前发动才更合理.</p>
	 */
	public void addDefaultEndedEvent() {
		lastEvent.add(new DefaultEndEvent());
		if (om.getRound() <= 0) {
			lastEvent.add(new DefaultEnteranceEvent(true));
		} else {
			lastEvent.add(new DefaultEnteranceEvent());
		}
		lastEvent.add(new DefaultRequestEvent());
	}
	
	/* ************
	 *	事件先后  *
	 ************ */
	
	class MovesEntry {
		byte seat; // 发动方精灵的 seat
		short skill; // 技能号
		byte priority; // 技能优先级
		byte addition; // 物品发动结果
		int speed; // 精灵的速度
		MoveEvent event; // 事件
		@Override
		public String toString() {
			return "MovesEntry [seat=" + seat + ", skill=" + skill + ", priority=" + priority + ", addition=" + addition
					+ ", speed=" + speed + "]";
		}
	}
	
	/**
	 * movesEvent 中寻找最早发动的技能事件，并返回<br>
	 * 其实比的是：技能优先级、物品发动结果、精灵的速度
	 * @return
	 */
	private AEvent nextMovesEvent() {
		MovesEntry[] entries = new MovesEntry[movesEvent.size()];
		
		BattlePlatform pf = om.getRoot();
		AttendManager am = pf.getAttendManager();
		
		int i = 0;
		for (Iterator<AEvent> it = movesEvent.iterator(); it.hasNext(); i++) {
			MoveEvent event = (MoveEvent) it.next();
			
			byte no = event.getNo();
			short skillID = am.getAttendant(no).getSkill()[event.getSkillNum()];
			
			entries[i] = new MovesEntry();
			entries[i].event = event;
			entries[i].seat = am.seatForNo(event.getNo());
			entries[i].speed = pf.getEffectManage().calcAbility(
					entries[i].seat, ParticipantAbilityHandler.SP);
			entries[i].priority = am.getSkillRelease(skillID).getPriority();
			pf.logPrintf(IPrintLevel.PRINT_LEVEL_VERBOSE,
					"EventList.nextMovesEvent() 精灵数据 %s", entries[i]);
		}
		
		Arrays.sort(entries, comparator);
		pf.logPrintf(IPrintLevel.PRINT_LEVEL_VERBOSE,
				"EventList.nextMovesEvent(): 排序结果\n\t%s", Arrays.toString(entries));
		pf.logPrintf(IPrintLevel.PRINT_LEVEL_WARN,
				"EventList.nextMovesEvent() entry.addition 数据(物品发动对行动顺序的影响) 未完成"); // TODO
		pf.logPrintf(IPrintLevel.PRINT_LEVEL_WARN,
				"EventList.nextMovesEvent(): 顺序排序中前多个精灵判定相同时的解决方法没有实现"); // TODO
		
		return entries[0].event;
	}
	
	class MovesEntryComparator implements Comparator<MovesEntry> {
		@Override
		public int compare(MovesEntry o1, MovesEntry o2) {
			if (o1.priority != o2.priority) {
				return o2.priority - o1.priority;
			} else if (o1.addition != o2.addition) {
				return o2.addition - o1.addition;
			} else {
				return o2.speed - o1.speed;
			}
		}
	}
	private MovesEntryComparator comparator = new MovesEntryComparator();
	
	/* ************
	 *	 初始化   *
	 ************ */

	public EventList(OrderManager om) {
		this.om = om;
		addDefaultEndedEvent();
	}

}
