package com.zdream.pmw.platform.control;

/**
 * 请求指令参数接口<br>
 * 将常用的请求指令在此写明<br>
 * 写明系统向玩家或 AI 发送的信息的种类<br>
 * 
 * <p><b>v0.2.3</b><br>
 * 补充了黑白名单 key.</p>
 * 
 * @since v0.1.1 [2016-04-05]
 * @author Zdream
 * @version v0.2.3 [2017-05-09]
 */
public interface IRequestKey {
	
	/**
	 * 请求行动的怪兽位置<br>
	 * value 值的格式如下：<br>
	 * [0,1] 表示 seat 为 0 和 1 的精灵在请求行动指令
	 */
	public static final String KEY_REQ_SEAT = "req_seat";
	
	/**
	 * 是否需要玩家、AI 等指明行动的目标位置<br>
	 * value 值的格式如下：<br>
	 *   true: 表示需要存入行动数据时，设置技能释放的目标；一般出现在双打等形式中
	 *   false: 表示需要存入行动数据时，不需要设置技能释放的目标；一般出现在单打等形式中
	 */
	public static final String KEY_REQ_SEATNEEDED = "req_seat_needed";
	
	/**
	 * 请求内容，即本次请求，系统希望用户端（玩家或 AI）做什么<br>
	 * 可能是以下的选项之一：<br>
	 *   请求行动、请求换人名额、战斗结束
	 * value 值的格式（字符串）为：<br>
	 *   VALUE_REQ_CONTENT_MOVE、VALUE_REQ_CONTENT_SWITCH、VALUE_REQ_CONTENT_END
	 */
	public static final String KEY_REQ_CONTENT = "req_seat_content";
	
	/**
	 * 以下是 KEY_REQ_CONTENT 的键值的可选选项<br>
	 *   VALUE_REQ_CONTENT_MOVE: 表示行动回合到，系统请求用户端回答下一步精灵的行动<br>
	 *   VALUE_REQ_CONTENT_SWITCH: 表示换精灵时，系统请求用户端回答换上的精灵<br>
	 *   VALUE_REQ_CONTENT_END: 表示系统告知用户战斗结束
	 */
	public static final String 
			VALUE_REQ_CONTENT_MOVE = "moves",
			VALUE_REQ_CONTENT_SWITCH = "switch",
			VALUE_REQ_CONTENT_END = "end";
	
	/**
	 * 战斗结束时战况的通告<br>
	 * 在本次战斗中，该用户端（玩家或 AI）最后的结果是胜利、平局还是失败
	 * value 值的格式（字符串）为：<br>
	 *   VALUE_REQ_RESULT_SUCCESS、VALUE_REQ_RESULT_TIED、VALUE_REQ_RESULT_LOSE
	 */
	public static final String KEY_REQ_RESULT = "req_result";
	
	/**
	 * 以下是 KEY_REQ_RESULT 的键值的可选选项<br>
	 */
	public static final String 
		VALUE_REQ_RESULT_SUCCESS = "success",
		VALUE_REQ_RESULT_TIED = "tied",
		VALUE_REQ_RESULT_LOSE = "lose";
	
	/**
	 * <p>对应的怪兽不允许使用哪些技能
	 * <p>{@code KEY_REQ_LIMITS + <seat>} 作为座位号 seat 的怪兽不允许使用技能的 key,
	 * 从请求域中获得的数据类型为 int[], 为技能选择的黑名单, 在数组中的所有元素代表着
	 * 对应怪兽的技能列表的索引. 黑名单与白名单同时存在存在时, 黑名单将失效.
	 * <p>该类数据可选</p>
	 * @since v0.2.3
	 */
	public static final String KEY_REQ_LIMITS = "req_limits";
	
	/**
	 * <p>对应的怪兽只允许使用哪些技能
	 * <p>{@code KEY_REQ_OPTIONS + <seat>} 作为座位号 seat 的怪兽只能选择的技能的 key,
	 * 从请求域中获得的数据类型为 int (不是数组), 这也意味着白名单只有一项,
	 * 该值对应怪兽的技能列表的索引. 黑名单与白名单同时存在存在时, 黑名单将失效.
	 * <p>该类数据可选</p>
	 * @since v0.2.3
	 */
	public static final String KEY_REQ_OPTION = "req_options";
	
}
