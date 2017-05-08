package com.zdream.pmw.platform.order;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

import com.zdream.pmw.platform.attend.AttendManager;
import com.zdream.pmw.platform.attend.service.ParticipantAbilityHandler;
import com.zdream.pmw.platform.control.IPrintLevel;
import com.zdream.pmw.platform.effect.AInstruction;
import com.zdream.pmw.platform.order.event.AMoveEvent;
import com.zdream.pmw.platform.order.event.DefaultEndEvent;
import com.zdream.pmw.platform.order.event.DefaultEnteranceEvent;
import com.zdream.pmw.platform.order.event.DefaultRequestEvent;
import com.zdream.pmw.platform.order.event.IEvent;
import com.zdream.pmw.platform.order.event.MoveEvent;
import com.zdream.pmw.platform.order.event.ReplaceEvent;
import com.zdream.pmw.platform.prototype.BattlePlatform;

/**
 * 触发列表管理器<br>
 * 
 * <p>从版本 v0.2.3 开始起, 触发列表存放了二类能够触发的事件：<br>
 * <b>提前触发事件</b>和<b>行动事件</b>.
 * 在版本 v0.2.2 及以前的三类事件中大部分都将重新归类至行动事件中.</p>
 * 
 * <p><b>提前触发事件</b>有：静电等特性触发的事件、连续攻击的事件、死亡强制退场事件等<br>
 *   实际上, 在版本 v0.2.3 开始起,
 *   所有在怪兽行动中, 所有的判定、计算和结果结算都将由该部分的所有事件协同完成.<br>
 *   这些事件的共同特点是, 它可以在一般的怪兽释放技能的事件运行前和运行时触发<br>
 * 举个例子，精灵 A 用撞击攻击 B，在 B 的技能释放前，B 的静电特性触发了<br>
 *   就说明静电特性的事件可以插在 B 的技能释放前，优先执行<br>
 *   因此可以看出静电特性事件属于提前触发事件<br>
 * 还有部分技能在一定的情况之下属于提前触发事件而非行动事件<br>
 *   比如在某方准备换精灵的时候，对方的技能追击优先触发，这时的追击技能就属于<b>提前触发事件</b></p>
 * 
 * <p><b>行动事件</b>包括:
 * <li>就可以看成训练师或 AI 对精灵做出的指令, 包括技能释放、嗑药、更换精灵等<br>
 * <li>系统结尾时发生的事件, 包括:<br>
 *   座位为空，强制换精灵上场的事件 <code>DefaultEnteranceEvent</code><br>
 *   默认结尾处理事件（比如中毒扣血） <code>DefaultEndEvent</code><br>
 *   为训练师或 AI 发送行动请求的事件 <code>DefaultRequestEvent</code><br>
 *   等</li></p>
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
 * <p><b>v0.2.3</b><br>
 * 事件类型做了重新归类</p>
 * 
 * @since v0.1.1 [2016-04-19]
 * @author Zdream
 * @version v0.2.3 [2017-04-21]
 */
public class EventList {
	
	OrderManager om;
	
	/* ************
	 *	数据结构  *
	 ************ */
	/**
	 * <p>提前触发事件列表</p>
	 * <p>所有在怪兽行动中, 所有的判定、计算和结果结算, 包括怪兽濒死退场,
	 * 都将由该部分的所有事件协同完成.</p>
	 * <p>该列表内部的事件将不会自动排序, 在事件全部运行完之前, 所有元素不会自动删除.</p>
	 */
	LinkedList<IEvent> previousEvent = new LinkedList<>();
	ListIterator<IEvent> lit;
	
	/**
	 * <p>行动事件列表</p>
	 * <p>行动事件是系统自动创建的, 里面运行的基本为系统必须的事件.
	 * 因此它将最低限度向外开放.</p>
	 * <p>排序顺序 (即触发顺序)
	 * <li>怪兽交换
	 * <li>使用道具
	 * <li>使用技能
	 * <li>收尾事件
	 * </li></p>
	 */
	LinkedList<IEvent> movesEvent = new LinkedList<>(); 
	
	/**
	 * 返回下一个 <code>AEvent</code><br>
	 * 首先看提前触发事件列表, 如果列表不为空, 返回处在列表开头位置的事件<br>
	 * 如果提前触发事件列表为空, 查看行动事件列表.
	 * 这里存放系统相关的事件, 所以一般而言, 这里事件不会为空.<br>
	 * @return
	 */
	public IEvent nextEvent() {
		if (!previousEvent.isEmpty()) {
			IEvent event = nextPreviousEvent();
			if (event != null) {
				return event;
			}
		}
		
		if (!movesEvent.isEmpty()) {
			IEvent event = nextMovesEvent();
			movesEvent.remove(event);
			return event;
		} else {
			om.logPrintf(IPrintLevel.PRINT_LEVEL_ERROR, 
					"EventList.nextEvent() 位置：%s 没有任何能够进行的事件", this.getClass().getName());
			throw new NoSuchElementException("没有任何能够进行的事件");
		}
	}
	
	/**
	 * <p>在提前触发事件中选出最早触发的事件<br>
	 * 即在 {@code lit} 指向列表的事件</p>
	 * @return
	 */
	private IEvent nextPreviousEvent() {
		if (lit == null) {
			lit = previousEvent.listIterator();
		}
		
		if (lit.hasNext()) {
			return lit.next();
		} else {
			lit = null;
			previousEvent.clear();
		}
		
		return null;
	}
	
	/**
	 * 添加新的 <code>IEvent</code>,
	 * 并将其放入提前触发事件列表的结尾.
	 * @see EventList#previousEvent
	 * @see EventList#movesEvent
	 * @param event
	 */
	public void pushEvent(IEvent event) {
		om.logPrintf(IPrintLevel.PRINT_LEVEL_DEBUG, "尝试添加一个事件 %s\r\n\tat %s",
				event, Thread.currentThread().getStackTrace()[1]);
		previousEvent.add(event);
		
		if (lit != null) {
			lit = previousEvent.listIterator(lit.nextIndex());
		}
	}
	
	/**
	 * 添加多数 <code>IEvent</code>,
	 * 并且按照次序放入提前触发事件列表的结尾.
	 * @param events
	 */
	public void pushEvents(List<IEvent> events) {
		om.logPrintf(IPrintLevel.PRINT_LEVEL_DEBUG, "尝试添加多个事件 %s\r\n\tat %s",
				events, Thread.currentThread().getStackTrace()[1]);
		previousEvent.addAll(events);
		
		if (lit != null) {
			lit = previousEvent.listIterator(lit.nextIndex());
		}
	}
	
	/**
	 * 添加新的 <code>IEvent</code>,
	 * 让其下一个执行.
	 * @param event
	 * @since v0.2.3
	 */
	public void pushEventToNext(IEvent event) {
		om.logPrintf(IPrintLevel.PRINT_LEVEL_DEBUG, "尝试添加一个事件稍后执行 %s\r\n\tat %s", event,
				Thread.currentThread().getStackTrace()[1]);
		if (lit == null) {
			previousEvent.addFirst(event);
		} else {
			lit.add(event);
			lit.previous();
		}
	}
	
	/**
	 * 添加多数新的 <code>IEvent</code>,
	 * 让其在当前事件完成后按次序执行.
	 * @param event
	 * @since v0.2.3
	 */
	public void pushEventsToNext(List<IEvent> events) {
		om.logPrintf(IPrintLevel.PRINT_LEVEL_DEBUG, "尝试添加多个事件稍后执行 %s\r\n\tat %s", events,
				Thread.currentThread().getStackTrace()[1]);
		if (lit == null) {
			for (ListIterator<IEvent> it = events.listIterator(events.size()); it.hasPrevious();) {
				IEvent eve = it.previous();
				previousEvent.addFirst(eve);
			}
		} else {
			for (ListIterator<IEvent> it = events.listIterator(events.size()); it.hasPrevious();) {
				IEvent eve = it.previous();
				lit.add(eve);
				lit.previous();
			}
		}
	}
	
	/**
	 * <p>删除当前执行的事件之后的指定的指示事件.
	 * @param tasks
	 *   要删除的指示事件能够执行任务的集合.
	 * @since v0.2.3
	 */
	public void removeInstructions(Collection<String> tasks) {
		if (lit == null) {
			previousEvent.removeIf((name) -> {return tasks.contains(name);});
		} else {
			int idx = lit.nextIndex();
			for (; lit.hasNext();) {
				IEvent e = lit.next();
				if (e instanceof AInstruction) {
					AInstruction inst = (AInstruction) e;
					if (tasks.contains(inst.canHandle())) {
						lit.remove();
					}
				}
			}
			
			lit = previousEvent.listIterator(idx);
		}
	}
	
	/**
	 * <p>删除之后将执行的所有 previous 事件, 并返回.
	 * <p>执行了该方法之后, 正在执行的事件执行完之后, 就没有未完成的 previous 事件了.
	 * @return
	 *   原本在正在执行的事件之后执行的所有 previous 事件集合, 按照原顺序排列.
	 * @since v0.2.3
	 */
	public List<IEvent> removeNexts() {
		om.logPrintf(IPrintLevel.PRINT_LEVEL_DEBUG, "尝试删除剩余的行动事件\r\n\tat %s",
				Thread.currentThread().getStackTrace()[1]);
		ArrayList<IEvent> list = new ArrayList<>(previousEvent.size() - lit.nextIndex());
		
		while (lit.hasNext()) {
			list.add(lit.next());
			lit.remove();
		}
		
		return list;
	}
	
	/**
	 * 删除对应 no 精灵的行动事件，并返回<br>
	 * 可能因为有精灵已经处于濒死，那么该精灵的行动事件不会发动<br>
	 * @param no
	 *   指定精灵的 no
	 * @return
	 *   该事件的实例
	 */
	public AMoveEvent removeMoveEvent(byte no) {
		om.logPrintf(IPrintLevel.PRINT_LEVEL_DEBUG, "尝试删除 no=%d 的行动事件\r\n\tat %s", no,
				Thread.currentThread().getStackTrace()[1]);
		for (Iterator<IEvent> it = movesEvent.iterator(); it.hasNext();) {
			IEvent event = it.next();
			if (event instanceof AMoveEvent) {
				AMoveEvent me = (AMoveEvent) event;
				if (me.getNo() == no) {
					movesEvent.remove(event);
					return me;
				}
			}
		}
		return null;
	}
	
	/**
	 * 添加默认的回合结束事件到行动事件列表的末尾
	 * <br>
	 * <p><b>v0.2.1</b><br>
	 * 之前的顺序是 enterance, end, request, 现在改为 end, enterance, request.<br>
	 * end 事件会同时发动中毒、天气伤害等事件, 而这些事件在上场之前发动才更合理.</p>
	 */
	public void addDefaultEndedEvent() {
		movesEvent.add(new DefaultEndEvent());
		if (om.getRound() <= 0) {
			movesEvent.add(new DefaultEnteranceEvent(true));
		} else {
			movesEvent.add(new DefaultEnteranceEvent());
		}
		movesEvent.add(new DefaultRequestEvent());
	}
	
	class Storage implements IMoveEventStorage {

		@Override
		public void store(IEvent... events) {
			for (IEvent e : events) {
				storeAny(e);
			}
		}
		
		void storeAny(IEvent e) {
			int p = moveEventPriority(e);
			
			for (ListIterator<IEvent> it = movesEvent.listIterator(); it.hasNext();) {
				IEvent e0 = it.next();
				int np = moveEventPriority(e0);
				if (np < p) {
					movesEvent.add(it.previousIndex(), e);
					return;
				}
			}
			
			movesEvent.addLast(e);
		}
		
	}
	
	private int moveEventPriority(IEvent e) {
		if (e instanceof ReplaceEvent) {
			return 4;
		}
		// 缺少使用道具
		if (e instanceof MoveEvent) {
			return 1;
		}
		
		return 0;
	}
	
	Storage storage = new Storage();
	
	/* ************
	 *	事件先后  *
	 ************ */
	
	class MovesEntry {
		byte seat; // 发动方精灵的 seat
		short skill; // 技能号
		byte priority; // 技能优先级
		byte addition; // 物品发动结果
		float speed; // 精灵的速度
		MoveEvent event; // 事件
		@Override
		public String toString() {
			return "MovesEntry [seat=" + seat + ", skill=" + skill + ", priority=" + priority + ", addition=" + addition
					+ ", speed=" + speed + "]";
		}
	}
	
	/**
	 * <p>在行动顺序列表中寻找最早发动的技能事件，并返回</p>
	 * <p>总体的顺序是 (靠前的先触发): 怪兽交换、使用道具、使用技能、收尾事件</p>
	 * <p>其实比的是：技能优先级、物品发动结果、怪兽的速度</p>
	 * @return
	 */
	private IEvent nextMovesEvent() {
		BattlePlatform pf = om.getRoot();
		AttendManager am = pf.getAttendManager();
		
		IEvent selected = this.movesEvent.getFirst();
		
		if (selected instanceof ReplaceEvent) {
			return selected;
		} 
		// 缺少使用道具
		else if (selected instanceof MoveEvent) {
			// 使用技能
			MovesEntry[] entries = new MovesEntry[movesEvent.size()];
			int i = 0;
			for (Iterator<IEvent> it = movesEvent.iterator(); it.hasNext(); i++) {
				IEvent ie = it.next();
				if (!(ie instanceof MoveEvent)) {
					break;
				}
				MoveEvent event = (MoveEvent) ie;
				
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
			
			Arrays.sort(entries, 0, i, comparator);
			pf.logPrintf(IPrintLevel.PRINT_LEVEL_VERBOSE,
					"EventList.nextMovesEvent(): 排序结果\n\t%s", Arrays.toString(entries));
			pf.logPrintf(IPrintLevel.PRINT_LEVEL_WARN,
					"EventList.nextMovesEvent() entry.addition 数据(物品发动对行动顺序的影响) 未完成"); // TODO
			pf.logPrintf(IPrintLevel.PRINT_LEVEL_WARN,
					"EventList.nextMovesEvent(): 顺序排序中前多个精灵判定相同时的解决方法没有实现"); // TODO
			
			return entries[0].event;
		} else {
			// 收尾事件
			return selected;
		}
	}
	
	class MovesEntryComparator implements Comparator<MovesEntry> {
		@Override
		public int compare(MovesEntry o1, MovesEntry o2) {
			if (o1 == null || o2 == null) {
				if (o1 == null)
					return (o2 == null) ? 0 : 1;
				else
					return -1;
			}
			
			if (o1.priority != o2.priority) {
				return o2.priority - o1.priority;
			} else if (o1.addition != o2.addition) {
				return o2.addition - o1.addition;
			} else {
				float s = o2.speed - o1.speed;
				if (s > 0) {
					return 1;
				} else if (s < 0) {
					return -1;
				}
				return 0;
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
