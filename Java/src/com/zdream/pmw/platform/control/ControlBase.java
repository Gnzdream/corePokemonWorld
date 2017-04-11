package com.zdream.pmw.platform.control;

import java.util.HashMap;
import java.util.Map;

import com.zdream.pmw.platform.prototype.IMessageCallback;
import com.zdream.pmw.platform.prototype.IRequestCallback;

/**
 * 控制体的共同父类<br>
 * 利用此类可以将玩家、AI 等的行动请求写入到 <code>RequestSignal</code> 中<br>
 * <br>
 * <b>v0.2</b><br>
 *   与 <code>RequestSignal</code> 进行了合并<br>
 * <br>
 * <p><b>v0.2.2</b><br>
 * 添加了回调函数属性. 原本回调函数属性实在子类 {@code SingleModeControl} 中,
 * 现在将其移到该超类中.</p>
 * 
 * @since v0.1
 * @author Zdream
 * @date 2016年3月31日
 * @version v0.2.2
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
	 *	其它属性  *
	 ************ */
	/*
	 * v0.2.2
	 */
	protected boolean ready, loaded;
	
	/**
	 * <p>当这个控制体中放入了请求的数据时, {@code ready} 这个参数就会为 {@code true},
	 * 表示该请求已经准备好, 能够通知相应的控制方, 并能够接受指令.<br>
	 * 一般而言, 对于已经落败的队伍控制体, 或者该轮行动请求中, 指定队伍没有能够行动的,
	 * 由于不会再请求控制方行动, 因此这一轮 ready 不会为 {@code false}.
	 * 所以系统也可以靠查询有多少控制体准备好了来判断需要等待多少控制体提交控制指令.</p>
	 * <p>{@code ready} 会一直为 {@code true} 直到控制方提交了所有需要的指令.</p>
	 * <p>{@code ready} 的 Getter 方法</p>
	 * @return
	 *   这个控制体是否已经准备好发送通知、接受指令.
	 * @since v0.2.2
	 */
	public boolean isReady() {
		return ready;
	}
	
	/**
	 * <p>当这个控制体开始请求控制方时, {@code ready} 这个参数就会开始活动.<br>
	 * 它表示响应域数据是否已经提交完成.
	 * 一般而言, 对于已经落败的队伍控制体, 或者该轮行动请求中, 指定队伍没有能够行动的,
	 * 由于不会再请求控制方行动, 因此这一轮 ready 和 loaded 均不会为 {@code false}.</p>
	 * <p>{@code loaded} 在控制方提交控制指令前都为 {@code false},<br>
	 * 当完成提交时为 {@code true}.</p>
	 * <p>{@code loaded} 的 Getter 方法</p>
	 * @return
	 *   这个控制体中, 控制方是否已经提交完控制指令.
	 * @since v0.2.2
	 */
	public boolean isLoaded() {
		return loaded;
	}

	/* ************
	 *	回调函数  *
	 ************ */
	/*
	 * v0.2.1 中, 该属性只在单线程模式的子类存在.
	 * 在 v0.2.2 及之后版本, 它将在所有的控制体类中都存在.
	 */
	protected IRequestCallback callback;
	protected IMessageCallback[] msgbacks;
	
	public void setCallback(IRequestCallback callback) {
		this.callback = callback;
	}
	
	public IRequestCallback getCallback() {
		return callback;
	}
	
	public void setMessagebacks(IMessageCallback[] msgbacks) {
		this.msgbacks = msgbacks;
	}
	
	public IMessageCallback[] getMessagebacks() {
		return msgbacks;
	}
	
	/* ************
	 *	系统请求  *
	 ************ */
	/*
	 * 系统的行动部分
	 */
	
	/**
	 * <p>在请求域中放入控制方进行行动的必要数据和规则,
	 * 但是该方法不通知控制方.</p>
	 * <p>系统调用</p>
	 * @param seats
	 *   请求的怪兽 seat 列表
	 */
	public void putMoveRequest(byte[] seats) {
		if (ready) {
			throw new IllegalArgumentException("putMoveRequest: already ready");
		}
		request.put(KEY_REQ_SEAT, seats);
		request.put(KEY_REQ_CONTENT, VALUE_REQ_CONTENT_MOVE);
		
		ready = true;
	}
	
	/**
	 * <p>在请求域中放入控制方进行需要怪兽交换进场的必要数据和规则,
	 * 但是该方法不通知控制方.</p>
	 * <p>系统调用</p>
	 * @param seats
	 *   请求的怪兽 seat 列表
	 */
	public void putSwitchRequest(byte[] seats) {
		if (ready) {
			throw new IllegalArgumentException("putMoveRequest: already ready");
		}
		request.put(KEY_REQ_SEAT, seats);
		request.put(KEY_REQ_CONTENT, VALUE_REQ_CONTENT_SWITCH);
		
		ready = true;
	}
	
	/**
	 * <p>在请求域中放入最终战斗结果的消息,
	 * 但是该方法不通知控制方.</p>
	 * <p>系统调用</p>
	 * @param successCamp
	 *   获胜的阵营
	 */
	public void putEndRequest(byte successCamp) {
		request.put(KEY_REQ_CONTENT, VALUE_REQ_CONTENT_END);
		
		byte camp = cm.getRoot().getReferee().teamToCamp(team);
		if (successCamp == -1) {
			request.put(KEY_REQ_RESULT, VALUE_REQ_RESULT_TIED);
		} else if (camp == successCamp) {
			request.put(KEY_REQ_RESULT, VALUE_REQ_RESULT_SUCCESS);
		} else {
			request.put(KEY_REQ_RESULT, VALUE_REQ_RESULT_LOSE);
		}
		
		ready = true;
	}
	
	/**
	 * <p>正式通知控制方处理请求.
	 * 结果是调用 {@code IRequestCallback} 回调函数.</p>
	 * <p>如果该控制体的 {@link isReady()} 为 {@code true} 时,
	 * {@link IRequestSemaphore} 就会调用它.</p>
	 */
	public void inform() {
		callback.onRequest(cm.getRoot(), this);
	}
	
	@Override
	public void provide(String msg) {
		if (msgbacks == null) {
			return;
		}
		
		for (int i = 0; i < msgbacks.length; i++) {
			try {
				msgbacks[i].onMessage(cm.getRoot(), msg);
			} catch (Exception e) {}
		}
	}
	
	/* ************
	 * 控制方操作 *
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
	 * 了解该请求是什么<br>
	 * 可能是以下的选项之一：<br>
	 *   请求行动、请求换人名额、战斗结束
	 * @return
	 */
	public String getContent() {
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
		
		loaded = true;
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
	public void clear(){
		request.clear();
		respond.clear();
		
		ready = false;
		loaded = false;
	}
	
	/*
	 * v0.2.1
	 *   以下直接提供了选择技能、选择换怪兽上场、选择背包、选择逃跑的 API
	 */
	
	/**
	 * 获得战斗结果<br>
	 * 在收到战斗结束信号之后, 用户或 AI 可以获得结果
	 */
	public String getEndResult() {
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
