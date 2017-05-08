package com.zdream.pmw.platform.order;

import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.List;

import com.zdream.pmw.platform.control.IPrintLevel;
import com.zdream.pmw.platform.effect.SkillReleasePackage;
import com.zdream.pmw.platform.order.event.AMoveEvent;
import com.zdream.pmw.platform.order.event.IEvent;
import com.zdream.pmw.platform.prototype.BattlePlatform;
import com.zdream.pmw.platform.prototype.Fuse;
import com.zdream.pmw.platform.prototype.IPlatformComponent;
import com.zdream.pmw.platform.prototype.RuleConductor;
import com.zdream.pmw.util.json.JsonObject;
import com.zdream.pmw.util.json.JsonValue;

/**
 * 战斗顺序管理者<br>
 * 用于在 BattlePlatform 中设置行动的顺序<br>
 * <br>
 * <b>v0.1</b><br>
 *   修改了 init(1) 方法<br>
 * <br>
 * <b>v0.1.1</b><br>
 *   添加了设置回合数的方法<br>
 * <br>
 * <b>v0.2</b><br>
 *   添加 BattlePlatform 的变量引用<br>
 * 
 * <p><b>v0.2.2</b>
 * <li>添加存储历史的部分,
 *   该类能够存储发送直接操作实现层的消息和战斗释放时数据;
 * <li>在运行被动模式 (客户端模式) 时, 整个系统将不会为事件驱动的,
 *   因此不会生成事件和事件管理器.</p>
 * 
 * @since v0.1
 *   [2016-04-19]
 * @author Zdream
 * @version v0.2.2
 *   [2017-04-01]
 */
public class OrderManager implements IPlatformComponent {
	
	/* ************
	 *	时间信息  *
	 ************ */
	/**
	 * 回合数
	 */
	int round = 0;
	public int getRound() {
		return this.round;
	}
	public void roundCount() {
		this.round ++;
		recorder.roundCount(round);
	}
	
	/* ************
	 *	触发列表  *
	 ************ */
	
	/**
	 * 按顺序触发事件
	 */
	public void actNextEvent() {
		IEvent event = list.nextEvent();
		Instant t = Instant.now();
		event.action(pf);
		logPrintf(IPrintLevel.PRINT_LEVEL_INFO, "事件: %s 用时 %d ms",
				event.toString(), Duration.between(t, Instant.now()).toMillis());
	}
	
	/* ************
	 *	调用结构  *
	 ************ */
	
	/**
	 * 行动事件生成器
	 */
	private MoveEventBuilder builder;
	
	/**
	 * 将用户端的消息转化成事件并返回<br>
	 * 一般在请求玩家行动之后调用<br>
	 * @return
	 */
	public void buildMoveEvent() {
		builder.build();
	}
	
	/**
	 * 外部调用方法<br>
	 * <p>将指定的队伍的用户端的消息转化成事件并返回</p>
	 * <p>一般在怪兽濒死后, 请求队伍获得上场的怪兽时调用的</p>
	 * @param team
	 * @return
	 */
	public List<IEvent> buildMoveEvent(byte team) {
		return builder.buildForTeam(team);
	}
	
	/**
	 * <p>公共方法, 创建释放技能的行动事件并添加到指定的容器中</p>
	 * @param no
	 *   行动主体怪兽的 seat
	 * @param skillNum
	 *   选择怪兽的第几个技能
	 * @param target
	 *   选择目标, 默认为 -1
	 * @since v0.2.2
	 */
	public void buildMoveEvent(byte no, byte skillNum, byte target) {
		builder.buildMoveEvent(no, skillNum, target);
	}

	/**
	 * <p>公共方法, 创建释放技能的行动事件并添加到指定的容器中</p>
	 * @see MoveEventBuilder#buildMoveEvent(byte, byte, byte, JsonValue)
	 * @since v0.2.2
	 */
	public void buildMoveEvent(byte no, byte skillNum, byte target, JsonObject param) {
		builder.buildMoveEvent(no, skillNum, target, param);
	}

	/**
	 * 触发列表
	 */
	private EventList list;

	public void pushEvent(IEvent event) {
		list.pushEvent(event);
	}
	
	/**
	 * 添加多数 Event 列表
	 * @param events
	 */
	public void pushEvents(List<IEvent> events) {
		list.pushEvents(events);
	} 

	/**
	 * @see EventList#pushEventToNext(IEvent)
	 * @since v0.2.3
	 */
	public void pushEventToNext(IEvent event) {
		list.pushEventToNext(event);
	}
	
	/**
	 * @see EventList#pushEventsToNext(IEvent)
	 * @since v0.2.3
	 */
	public void pushEventsToNext(List<IEvent> events) {
		list.pushEventsToNext(events);
	}
	
	/**
	 * @see EventList#removeInstructions(tasks)
	 * @since v0.2.3
	 */
	public void removeInstructions(Collection<String> tasks) {
		list.removeInstructions(tasks);
	}
	
	/**
	 * @see EventList#removeNexts()
	 * @since v0.2.3
	 */
	public List<IEvent> removeNexts() {
		return list.removeNexts();
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
		return list.removeMoveEvent(no);
	}
	/**
	 * 添加默认的回合结束事件
	 */
	public void addDefaultEndedEvent() {
		list.addDefaultEndedEvent();
	}
	
	/* ************
	 *	存储历史  *
	 ************ */
	/*
	 * 添加存储历史的部分,
	 *   该类能够存储发送直接操作实现层的消息和战斗释放时数据.
	 * @since v0.2.2
	 */
	
	Recorder recorder;
	
	/**
	 * 存储消息
	 * @param codes
	 */
	public void storeMessage(String[] codes) {
		recorder.storeMessage(codes);
	}
	
	/**
	 * 存储释放技能的数据
	 * @param pack
	 */
	public void storeReleasePackage(SkillReleasePackage pack) {
		recorder.storeReleasePackage(pack);
	}
	
	/**
	 * 获得某一回合中所有的消息
	 * @param round
	 *   指定的回合数
	 */
	public String[][] getMessage(int round) {
		return recorder.getMessage(round);
	}
	
	/**
	 * 获得某一回合中所有释放技能的数据
	 * @param round
	 *   指定的回合数
	 */
	public SkillReleasePackage[] getReleasePackage(int round) {
		return recorder.getReleasePackage(round);
	}
	
	/* ************
	 *	 初始化   *
	 ************ */

	/**
	 * 指向战斗平台（环境）
	 * @since 0.2
	 */
	BattlePlatform pf;
	
	/**
	 * 初始化 <code>OrderManager</code><br>
	 * 初始化 EventList 触发列表(since v1.1)<br>
	 * 
	 * <p><b>v0.2.2</b><br>
	 * 在运行被动模式 (客户端模式) 时, 不会生成事件和事件管理器.</p>
	 * 
	 * @param msg
	 *   战斗信息开始消息
	 * @param referee
	 *   裁判
	 * @param pf
	 */
	public void init(Fuse msg, RuleConductor referee, BattlePlatform pf) {
		builder = new MoveEventBuilder(this);
		if (!msg.isPassive()) {
			list = new EventList(this);
			builder.storage = list.storage;
		}
		
		recorder = new Recorder(this);
		this.pf = pf;
	}
	
	@Override
	public void logPrintf(int level, String str, Object... params) {
		pf.logPrintf(level, str, params);
	}
	
	@Override
	public void logPrintf(int level, Throwable throwable) {
		pf.logPrintf(level, throwable);
	}
	
	@Override
	public BattlePlatform getRoot() {
		return pf;
	}
	
}
