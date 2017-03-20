package com.zdream.pmw.platform.order;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import com.zdream.pmw.platform.control.IPrintLevel;
import com.zdream.pmw.platform.order.event.AEvent;
import com.zdream.pmw.platform.order.event.MoveEvent;
import com.zdream.pmw.platform.prototype.BattlePlatform;
import com.zdream.pmw.platform.prototype.Fuse;
import com.zdream.pmw.platform.prototype.IPlatformComponent;
import com.zdream.pmw.platform.prototype.RuleConductor;

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
 * @since v0.1
 * @author Zdream
 * @date 2016年4月19日
 * @version v0.2
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
	public void setRound(int round) {
		this.round = round;
	}
	
	/* ************
	 *	触发列表  *
	 ************ */
	
	/**
	 * 按顺序触发事件
	 */
	public void actNextEvent() {
		AEvent event = list.nextEvent();
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
	private MoveEventBuilder builder = new MoveEventBuilder(this);
	
	/**
	 * 将用户端的消息转化成事件并返回<br>
	 * 一般在请求玩家行动之后调用<br>
	 * @return
	 */
	public List<AEvent> buildMoveEvent() {
		return builder.build();
	}
	
	/**
	 * 外部调用方法<br>
	 * 为某个特定的队伍, 将用户端的消息转化成事件并返回<br>
	 * @param team
	 * @return
	 */
	public List<AEvent> buildMoveEventForTeam(byte team) {
		return builder.buildForTeam(team);
	}

	/**
	 * 触发列表
	 */
	private EventList list;
	
	public void pushEvent(AEvent event) {
		list.pushEvent(event);
	}
	
	/**
	 * 添加多数 Event 列表
	 * @param events
	 */
	public void pushEvents(List<AEvent> events) {
		list.pushEvents(events);
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
		return list.removeMoveEvent(no);
	}

	/**
	 * 添加默认的回合结束事件
	 */
	public void addDefaultEndedEvent() {
		list.addDefaultEndedEvent();
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
	 * @param msg
	 *   战斗信息开始消息
	 * @param referee
	 *   裁判
	 * @param pf
	 */
	public void init(Fuse msg, RuleConductor referee, BattlePlatform pf) {
		list = new EventList(this);
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
