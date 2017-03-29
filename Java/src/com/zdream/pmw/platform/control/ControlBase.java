package com.zdream.pmw.platform.control;

import java.util.HashMap;
import java.util.Map;

/**
 * 控制体的共同父类<br>
 * 利用此类可以将玩家、AI 等的行动请求写入到 <code>RequestSignal</code> 中<br>
 * <br>
 * <b>v0.2</b><br>
 *   与 <code>RequestSignal</code> 进行了合并<br>
 * 
 * @since v0.1
 * @author Zdream
 * @date 2016年3月31日
 * @version v0.2
 */
public abstract class ControlBase implements IRequestKey, IRespondKey, IMessageProvider {
	
	/* ************
	 *	所属队伍  *
	 ************ */
	byte team;
	public byte getTeam() {
		return team;
	}
	public void setTeam(byte team) {
		this.team = team;
	}
	
	/* ************
	 *	  类型    *
	 ************ */
	/*
	 * 这个 Control 的类型是什么，决定了它是由 玩家控制，还是 自动控制、网络控制
	 */
	public static final int TYPE_PLAYER = 2;
	public static final int TYPE_AUTO = 4;
	public static final int TYPE_NET = 8;
	public static final int TYPE_OTHER = 16;
	int type;
	public int getType(){
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	
	/* ************
	 *	用户等待  *
	 ************ */
	/*
	 * 玩家、AI 等调用一个阻塞方法，等待直到下一个请求的到来。
	 * 该区域的方法都将由玩家、AI 等调用
	 * 
	 * 请求有：请求行动、请求换人名额、战斗结束
	 * 
	 * @version v0.2
	 *   在这里分出了单线程模式和多线程模式, 在 AI 与玩家游戏时采用默认的单线程模式
	 */
	
	/**
	 * 等待下一个请求<br>
	 * 阻塞方法，由玩家、AI 等调用，当出现请求时返回
	 * @return
	 *   了解该请求是什么<br>
	 *   请求行动、请求换人名额、战斗结束
	 * @see ControlBase#requestContent()
	 */
	public abstract String nextRequest();
	
	/**
	 * 了解该请求是什么<br>
	 * 可能是以下的选项之一：<br>
	 *   请求行动、请求换人名额、战斗结束
	 * @return
	 */
	public String requestContent() {
		return request.get(KEY_REQ_CONTENT).toString();
	}
	
	/**
	 * 返回需要请求行动的精灵 seat 数组
	 * @return
	 */
	public byte[] getSeats() {
		return (byte[]) request.get(KEY_REQ_SEAT);
	}
	
	/**
	 * 设置行动指令完成
	 */
	public void commit() {
		cm.logPrintf(IPrintLevel.PRINT_LEVEL_WARN, "行动指令没有验证检测");
		cm.logPrintf(IPrintLevel.PRINT_LEVEL_DEBUG, "队伍 %d 已经提交行动", team);
		cm.getRoot().getControlManager().onCommitResponse();
	}
	
	/* ************
	 *	请求数据  *
	 ************ */
	/**
	 * 当应该是当前控制体 选择怪兽的下一步操作时，就应该调用此方法，来控制怪兽的行动<br>
	 *     是攻击、换人，还是其它操作<br>
	 * <br>
	 * 输入参数：当前请求行动的怪兽的座位号
	 */
	
	/**
	 * 请求部分 (系统告知用户)
	 */
	Map<String, Object> request;
	
	/**
	 * 响应部分 (用户反馈系统)
	 */
	Map<String, Object> respond;
	
	/**
	 * 行动指令命令 key
	 */
	private static final String KEY_COMMAND = "Command";
	
	public String getCommand(byte seat) {
		return respond.get(KEY_COMMAND + seat).toString();
	}
	
	/**
	 * 设置行动指令
	 * @param command
	 *   可以在 COMMAND_? 中选择合适的选项
	 */
	public void setCommand(byte seat, String command) {
		respond.put(KEY_COMMAND + seat, command);
	}
	
	/**
	 * 其它指令命令 key
	 */
	private static final String 
			KEY_PARAM = "Param-",
			KEY_TARGET = "Target-";
	
	public String getParam(byte seat) {
		String key = KEY_PARAM + seat;
		Object v = respond.get(key);
		if (v != null) {
			return (String) v;
		} else {
			return null;
		}
	}
	public void setParam(byte seat, String param) {
		respond.put(KEY_PARAM + seat, param);
	}
	public String getTarget(byte seat) {
		String key = KEY_TARGET + seat;
		Object v = respond.get(key);
		if (v != null) {
			return (String) v;
		} else {
			return null;
		}
	}
	public void setTarget(byte seat, String target) {
		respond.put(KEY_TARGET + seat, target);
	}
	
	/**
	 * 数据全部清空
	 */
	public void domainClean(){
		request.clear();
		respond.clear();
	}
	
	/*
	 * v0.2.1
	 *   以下直接提供了选择技能、选择换怪兽上场、选择背包、选择逃跑的 API
	 */
	
	/**
	 * 获得战斗结果<br>
	 * 在收到战斗结束信号之后, 用户或 AI 可以获得结果
	 */
	public String getResult() {
		return (String) request.get(KEY_REQ_RESULT);
	}
	
	/**
	 * 选择释放的技能, 默认的目标
	 * @param seat
	 *   玩家指示哪个怪兽的行动
	 * @param skillNum
	 *   该怪兽的第几个技能
	 * @return
	 *   该行动是否有效
	 * @since v0.2.1
	 */
	public boolean chooseMove(byte seat, int skillNum) {
		return chooseMove(seat, skillNum, (byte) -1);
	}
	
	/**
	 * 选择释放的技能, 使用特定的目标
	 * @param seat
	 *   玩家指示哪个怪兽的行动
	 * @param skillNum
	 *   该怪兽的第几个技能
	 * @param target
	 *   目标的座位<br>
	 *   如果为 -1 时, 系统会尝试自动选择目标, 一旦失败, 就会判定失误
	 * @return
	 *   该行动是否有效
	 * @since v0.2.1
	 */
	public boolean chooseMove(byte seat, int skillNum, byte target) {
		// TODO 对玩家输入的指令, 系统判断该行动是合法的, 返回 true, 否则 false
		
		setCommand(seat, COMMAND_MOVES);
		setParam(seat, Integer.toString(skillNum));
		if (target >= 0) {
			setTarget(seat, Byte.toString(target));
		}
		
		return true;
	}
	
	/**
	 * 选择交换怪兽上场的怪兽
	 * @param seat
	 *   玩家指示哪个怪兽的行动
	 * @param replaceNo
	 *   准备交换上场的怪兽的 no 号码
	 * @return
	 *   该行动是否有效
	 * @since v0.2.1
	 */
	public boolean chooseReplace(byte seat, byte replaceNo) {
		// TODO 对玩家输入的指令, 系统判断该行动是合法的, 返回 true, 否则 false

		setCommand(seat, COMMAND_REPLACE);
		setParam(seat, Byte.toString(replaceNo));
		
		return true;
	}
	
	/**
	 * 向用户端（玩家或 AI）提出行动请求<br>
	 * 战斗系统调用的方法
	 * @param seats
	 *   请求的精灵 seat 列表
	 */
	public void nextMoveRequest(byte[] seats) {
		request.put(KEY_REQ_SEAT, seats);
		request.put(KEY_REQ_CONTENT, VALUE_REQ_CONTENT_MOVE);
		
		nextActionRequest();
	}
	
	/**
	 * 向用户端（玩家或 AI）提出行动请求<br>
	 * 战斗系统调用的方法
	 * @param seats
	 *   请求的精灵 seat 列表
	 */
	public void nextSwitchRequest(byte[] seats) {
		request.put(KEY_REQ_SEAT, seats);
		request.put(KEY_REQ_CONTENT, VALUE_REQ_CONTENT_SWITCH);
		
		nextActionRequest();
	}
	
	/**
	 * 向用户端（玩家或 AI）通知战斗结束的消息<br>
	 * 战斗系统调用的方法<br>
	 * @param isSuccess
	 *   该队伍战斗是否胜利
	 *   1-胜利 2-失败 0-平局
	 */
	public void endRequest(int isSuccess) {
		request.put(KEY_REQ_CONTENT, VALUE_REQ_CONTENT_END);
		request.put(KEY_REQ_RESULT,
				(isSuccess == 1) ? VALUE_REQ_RESULT_SUCCESS : 
					(isSuccess == 2) ? VALUE_REQ_RESULT_LOSE : 
						VALUE_REQ_RESULT_TIED);
		
		nextActionRequest();
	}
	
	/**
	 * 提醒用户端（玩家或 AI）处理请求
	 */
	public abstract void nextActionRequest();
	
	/* ************
	 *	 初始化   *
	 ************ */
	
	/**
	 * 控制中心
	 */
	protected ControlManager cm;
	
	public ControlBase(ControlManager manager) {
		super();
		this.cm = manager;
		
		request = new HashMap<>();
		respond = new HashMap<>();
	}
	
}
