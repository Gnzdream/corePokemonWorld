package com.zdream.pmw.platform.control;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.zdream.pmw.platform.attend.AttendManager;
import com.zdream.pmw.platform.attend.Attendant;
import com.zdream.pmw.platform.prototype.IMessageCallback;
import com.zdream.pmw.platform.prototype.IRequestCallback;

/**
 * 控制体的共同父类<br>
 * 利用此类可以将玩家、AI 等的行动请求写入到 <code>RequestSignal</code> 中<br>
 * <br>
 * <b>v0.2</b><br>
 *   与 <code>RequestSignal</code> 进行了合并<br>
 * 
 * <p><b>v0.2.2</b><br>
 * 添加了回调函数属性. 原本回调函数属性实在子类 {@code SingleModeControl} 中,
 * 现在将其移到该超类中.</p>
 * 
 * <p><b>v0.2.3</b><br>
 * 添加了输入检查.</p>
 * 
 * @since v0.1 [2016-03-31]
 * @author Zdream
 * @version v0.2.3 [2017-05-10]
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
	
	protected int valid;
	
	/**
	 * {@code valid} 参数的所有选值:
	 * <li>VALID_UNKNOWED: 参数合法情况未知. 在未调用 {@link #check()} 方法时的参数
	 * <li>VALID: 参数合法
	 * <li>INVALID: 参数不合法
	 * <li>MISSING: 参数缺失, 有需提交却还未提交的参数
	 * </li>
	 */
	public static final int VALID_UNKNOWED = 0,
			VALID = 1,
			INVALID = 2,
			MISSING = 3;
	
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
	
	/**
	 * <p>检测从玩家控制端输入的数据是否合法.
	 * <p>判断合法是由参数 {@code valid} 来决定的. 如果调用该方法时,
	 * 如果发现 {@code valid =} {@link #VALID_UNKNOWED}, 意味着输入参数
	 * 没有检查, 此时会调用 {@link #check()} 方法检查数据;
	 * 当 {@code valid} 为 {@link #VALID} 时返回 {@code true}.</p>
	 * @return
	 *   已输入的参数是否合法
	 * @since v0.2.3
	 */
	public boolean isValid() {
		if (valid == VALID_UNKNOWED) {
			check();
		}
		return valid == VALID;
	}
	
	/**
	 * <p>获得是否合法的参数. 方法内不会调用 {@code #check()} 方法.
	 * <p>valid 的 Getter 方法</p>
	 * @return
	 */
	public int getValid() {
		return valid;
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
	 * <p>在请求域中放入控制方进行行动的怪兽列表,
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
	 * <p>在请求域中放入控制方进行行动的怪兽对应的限制,
	 * 座位号为 seat 的怪兽不允许使用哪些技能.
	 * <p>传入的数据相当于黑名单, 在黑名单中的技能不允许选入;<br>
	 * 如果请求域中含白名单和黑名单, 则仅白名单有效, 黑名单将失效.
	 * <p>系统调用</p>
	 * @param seat
	 * @param skillNum
	 *   技能在怪兽的技能列表中的索引, 作为黑名单
	 * @throws IllegalStateException
	 *   如果该请求不为请求行动时
	 * @since v0.2.3
	 */
	public void putMoveLimit(byte seat, int skillNum) {
		if (!VALUE_REQ_CONTENT_MOVE.equals(request.get(KEY_REQ_CONTENT))) {
			throw new IllegalStateException(
					"content:" + request.get(KEY_REQ_CONTENT) + " != moves");
		}
		
		String key = KEY_REQ_LIMITS + seat;
		Object o = request.get(key);
		int[] is;
		if (o == null) {
			is = new int[]{skillNum};
			request.put(key, is);
		} else {
			int[] os = (int[]) o;
			boolean exist = false;
			for (int i = 0; i < os.length; i++) {
				if (skillNum == os[i]) {
					exist = true;
					break;
				}
			}
			
			if (!exist) {
				is = new int[os.length + 1];
				System.arraycopy(os, 0, is, 0, os.length);
				is[os.length] = skillNum;
				request.put(key, is);
			}
		}
	}
	
	/**
	 * <p>在请求域中放入控制方进行行动的怪兽对应的限制,
	 * 座位号为 seat 的怪兽只能允许使用哪些技能.
	 * <p>传入的数据相当于白名单, 在白名单外的技能不允许选入;
	 * 白名单只有一项, 也就意味着当已经存在白名单时试图调用此方法,
	 * 将使上一次设置的白名单失效, 白名单参数将重置成本次的值;<br>
	 * 如果请求域中含白名单和黑名单, 则仅白名单有效, 黑名单将失效.
	 * <p>系统调用</p>
	 * @param seat
	 * @param skillNum
	 *   技能在怪兽的技能列表中的索引
	 * @throws IllegalStateException
	 *   如果该请求不为请求行动时
	 * @since v0.2.3
	 */
	public void putMoveOption(byte seat, int skillNum) {
		if (!VALUE_REQ_CONTENT_MOVE.equals(request.get(KEY_REQ_CONTENT))) {
			throw new IllegalStateException(
					"content:" + request.get(KEY_REQ_CONTENT) + " != moves");
		}
		
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
	 * <p>如果该控制体的 {@link #isReady()} 为 {@code true} 时,
	 * {@link IRequestSemaphore} 就会调用它.</p>
	 */
	public void inform() {
		valid = 0;
		while (!loaded) {
			callback.onRequest(cm.getRoot(), this);
			
			// v0.2.3 当判断为游戏结束时, 自动跳出循环. 此时 loaded = false;
			if (IRequestKey.VALUE_REQ_CONTENT_END.equals(getContent())) {
				break;
			}
		}
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
	 * 询问当前请求是否为请求行动
	 * @return
	 *   当当前请求为请求行动时返回 true
	 * @since v0.2.3
	 */
	public boolean isMoveContent() {
		return VALUE_REQ_CONTENT_MOVE.equals(request.get(KEY_REQ_CONTENT));
	}
	
	/**
	 * 询问当前请求是否为交换怪兽
	 * @return
	 *   当当前请求为交换怪兽时返回 true
	 * @since v0.2.3
	 */
	public boolean isSwitchContent() {
		return VALUE_REQ_CONTENT_SWITCH.equals(request.get(KEY_REQ_CONTENT));
	}
	
	/**
	 * 询问当前请求是否为战斗结束
	 * @return
	 *   当当前请求为战斗结束时返回 true
	 * @since v0.2.3
	 */
	public boolean isSwitchEnd() {
		return VALUE_REQ_CONTENT_END.equals(request.get(KEY_REQ_CONTENT));
	}
	
	/**
	 * 返回需要请求行动的精灵 seat 数组的复制
	 * @return
	 */
	public byte[] getSeats() {
		byte[] seats = getSeats0();
		return Arrays.copyOf(seats, seats.length);
	}
	
	byte[] getSeats0() {
		return (byte[]) request.get(KEY_REQ_SEAT);
	}
	
	/**
	 * 设置行动指令完成
	 */
	public boolean commit() {
		cm.logPrintf(IPrintLevel.PRINT_LEVEL_DEBUG, "队伍 %d 尝试提交行动\r\n\t%s",
				team, Thread.currentThread().getStackTrace()[1]);
		if (!isValid()) {
			cm.logPrintf(IPrintLevel.PRINT_LEVEL_DEBUG,
					"检查到队伍 %d 尝试提交行动不合法\r\n\t%s",
					team, Thread.currentThread().getStackTrace()[1]);
			return false;
		}
		cm.logPrintf(IPrintLevel.PRINT_LEVEL_DEBUG, "队伍 %d 已经提交行动", team);
		cm.getRoot().getControlManager().onCommitResponse();
		
		loaded = true;
		return true;
	}
	
	/**
	 * <p>清除控制方提交的数据, 同时将 valid 参数置为 {@link #VALID_UNKNOWED}.
	 * <p>控制方调用
	 * @return
	 */
	public void clearResponse() {
		respond.clear();
		valid = VALID_UNKNOWED;
	}
	
	/**
	 * <p>检查从控制端输入的参数是否合法, 并将检查的结果写入到 {@code valid} 参数中.
	 * <p>该方法会检查:
	 * <li>输入数据是否缺失, 即检查是否有没有确定行动或没有确定更换的怪兽;
	 * <li>输入数据是否合法, 如果在确定行动时, 当有白名单时, 选择白名单外的技能将
	 * 视为不合法; 否则, 当有黑名单时, 选择黑名单内的技能将视为不合法; 再次, 除非
	 * 所有的技能 PP 均为 0, 否则选择 PP 为 0 的技能将视为不合法.
	 * </li>
	 * 当同时出现参数缺失与参数不合法时, valid 为 {@link #INVALID}</p>
	 */
	public void check() {
		String content = getContent();
		switch (content) {
		case VALUE_REQ_CONTENT_END:
			this.valid = VALID;
			break;
		case VALUE_REQ_CONTENT_MOVE:
			checkMoves();
			break;
		case VALUE_REQ_CONTENT_SWITCH:
			this.valid = VALID;
			break;
		}
	}
	
	protected void checkMoves() {
		int result = VALID;
		byte[] seats = getSeats0();
		byte[] replaces = new byte[seats.length];
		int replacesLen = 0;
		
		for (int i = 0; i < seats.length; i++) {
			byte seat = seats[i];
			String cmd = getCommand(seat);
			
			if (cmd == null) {
				result = MISSING;
				break;
			}
			
			switch (cmd) {
			case COMMAND_MOVES:
				result = checkMovesCommand(seat);
				break;
			case COMMAND_REPLACE: {
				byte replaceNo = (byte) getParam(seat);
				if (replaceNo == -1) {
					result = INVALID;
				}
				replaces[replacesLen++] = replaceNo;
			} break;

			default:
				// TODO 检查未完成
				result = INVALID;
				break;
			}
			
			if (result != VALID) {
				break;
			}
		}
		
		// 更换是最后检查的
		checkReplaceCommand(replaces, replacesLen);
		
		this.valid = result;
	}
	
	private int checkMovesCommand(byte seat) {
		int skillNum = this.getParam(seat);
		if (skillNum == -1) {
			return MISSING;
		}
		
		byte target = this.getTarget(seat);
		
		return (checkMoves(seat, skillNum, target)) ? VALID : INVALID;
	}
	
	private boolean checkMoves(byte seat, int skillNum, byte target) {
		//	如果在确定行动时, 当有白名单时, 选择白名单外的技能将
		//	视为不合法; 否则, 当有黑名单时, 选择黑名单内的技能将视为不合法; 再次, 除非
		//	所有的技能 PP 均为 0, 否则选择 PP 为 0 的技能将视为不合法
		
		Object o;
		
		// 白名单, 无视黑名单和 PP 检查
		if ((o = request.get(KEY_REQ_OPTION + seat)) != null) {
			int option = (int) o;
			return option == skillNum;
		}
		
		boolean[] bs = new boolean[Attendant.SKILL_LENGTH];
		Arrays.fill(bs, true);
		
		// 黑名单
		if ((o = request.get(KEY_REQ_LIMITS + seat)) != null) {
			int[] limits = (int[]) o;
			for (int i = 0; i < limits.length; i++) {
				bs[i] = false;
				if (limits[i] == skillNum) {
					return false;
				}
			}
		}
		
		// PP 检查
		AttendManager am = cm.getRoot().getAttendManager();
		byte[] pps = am.getParticipant(seat).getAttendant().getSkillPP();
		if (pps[skillNum] == 0) {
			short[] skills = am.getParticipant(seat).getAttendant().getSkill();
			for (int i = 0; i < pps.length; i++) {
				if (!bs[i] || skills[i] == 0) {
					continue;
				}
				if (pps[i] != 0) {
					return false;
				}
			}
		}
		
		return true;
	}
	
	/**
	 * @param replaceNo
	 *   选择替换的怪兽 no
	 * @return
	 */
	private int checkReplaceCommand(byte replaceNo) {
		byte[] seats = getSeats0();
		byte[] nos = new byte[seats.length];
		
		AttendManager am = cm.getRoot().getAttendManager();
		
		for (int i = 0; i < nos.length; i++) {
			nos[i] = am.noForSeat(seats[i]);
		}
		
		byte[] replaces = new byte[nos.length];
		int replacesLen = 1;
		replaces[0] = replaceNo;
		
		for (int i = 0; i < nos.length; i++) {
			byte no = nos[i];
			String cmd = getCommand(no);
			
			if (cmd == null) {
				continue;
			}
			
			if (cmd.equals(COMMAND_REPLACE)) {
				byte replaceTo0 = (byte) getParam(no);
				if (replaceTo0 == -1) {
					continue;
				}
				replaces[replacesLen++] = replaceTo0;
			}
		}
		
		return checkReplaceCommand(replaces, replacesLen);
	}
	
	/**
	 * @param nos
	 *   所有选择交换上场的怪兽的 no 列表
	 * @param len
	 *   seats 的有效长度
	 * @return
	 */
	private int checkReplaceCommand(byte[] nos, int len) {
		if (len == 0) {
			return VALID;
		}
		
		AttendManager am = cm.getRoot().getAttendManager();
		boolean[] bs = new boolean[am.attendantLength()];
		
		// 放入在场的所有怪兽
		byte[] exists = am.existSeats(team);
		for (int i = 0; i < exists.length; i++) {
			if (bs[exists[i]]) {
				return INVALID;
			} else {
				bs[exists[i]] = true;
			}
		}
		
		for (int i = 0; i < len; i++) {
			byte no = nos[i];
			if (am.getAttendant(no).getHpi() == 0) {
				// 不能选择空血的怪兽
				return INVALID;
			}
			if (bs[no]) {
				return INVALID;
			} else {
				bs[no] = true;
			}
		}
		
		return VALID;
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
		Object o;
		if ((o = respond.get(KEY_COMMAND + seat)) != null) {
			return o.toString();
		}
		return null;
	}
	
	/**
	 * 设置行动指令
	 * @param command
	 *   可以在 COMMAND_? 中选择合适的选项
	 */
	public void setCommand(byte seat, String command) {
		respond.put(KEY_COMMAND + seat, command);
		valid = VALID_UNKNOWED;
	}
	
	/**
	 * 其它指令命令 key
	 */
	private static final String 
			KEY_PARAM = "Param-",
			KEY_TARGET = "Target-";
	
	public int getParam(byte seat) {
		String key = KEY_PARAM + seat;
		Object v = respond.get(key);
		if (v != null) {
			return (int) v;
		} else {
			return -1;
		}
	}
	protected void setParam(byte seat, int param) {
		respond.put(KEY_PARAM + seat, param);
		valid = VALID_UNKNOWED;
	}
	/**
	 * 目标, 默认为 -1
	 * @param seat
	 * @return
	 */
	public byte getTarget(byte seat) {
		String key = KEY_TARGET + seat;
		Object v = respond.get(key);
		if (v != null) {
			return (byte) v;
		} else {
			return -1;
		}
	}
	protected void setTarget(byte seat, byte target) {
		respond.put(KEY_TARGET + seat, target);
		valid = VALID_UNKNOWED;
	}
	
	/**
	 * 数据全部清空
	 */
	public void clear(){
		request.clear();
		respond.clear();
		
		ready = false;
		loaded = false;
		valid = VALID_UNKNOWED;
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
	 * @throws IllegalArgumentException
	 *   当当前请求不为请求行动时
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
	 * @throws IllegalArgumentException
	 *   当当前请求不为请求行动时
	 * @since v0.2.1
	 */
	public boolean chooseMove(byte seat, int skillNum, byte target) {
		if (!isMoveContent()) {
			throw new IllegalArgumentException("content: " + getContent() + " can not permit to choose move.");
		}
		// 对玩家输入的指令, 系统判断该行动是合法的, 返回 true, 否则 false
		if (!checkMoves(seat, skillNum, target)) {
			return false;
		}
		
		setCommand(seat, COMMAND_MOVES);
		setParam(seat, skillNum);
		if (target >= 0) {
			setTarget(seat, target);
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
		// 对玩家输入的指令, 系统判断该行动是合法的, 返回 true, 否则 false
		if (checkReplaceCommand(replaceNo) != VALID) {
			return false;
		}

		setCommand(seat, COMMAND_REPLACE);
		setParam(seat, replaceNo);
		
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
