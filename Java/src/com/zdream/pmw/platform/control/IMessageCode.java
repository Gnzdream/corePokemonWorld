package com.zdream.pmw.platform.control;

/**
 * 系统定义的能够触发的拦截前消息的消息头<br>
 * 
 * @since v0.2.1
 * @author Zdream
 * @date 2017年3月14日
 * @version v0.2.1
 */
public interface IMessageCode {
	
	/**
	 * 入场前消息头
	 */
	public static final String CODE_ENTRANCE = "entrance";
	
	/**
	 * 入场后消息头
	 */
	public static final String CODE_AFTER_ENTRANCE = "after-entrance";
	
	/**
	 * 请求行动消息头
	 */
	public static final String CODE_REQUEST_MOVE = "request-move";
	
	/**
	 * 请求行动全部结束消息头
	 */
	public static final String CODE_REQUEST_COMMIT = "request-commit";
	
	/**
	 * 由于攻击方发动技能而扣 pp 的消息头
	 */
	public static final String CODE_PP_SUB = "ppSub";
	
	/**
	 * 拦截前消息的消息头<br>
	 * 确定怪兽将要释放的技能 (可能包括使用道具等)
	 */
	public static final String CODE_CONFIRM_SKILL = "confirm-skill";
	
	/**
	 * 拦截前消息的消息头<br>
	 * 已经确定技能, 而且确认该怪兽能够行动, 此时就释放技能 (可能包括使用道具等)
	 */
	public static final String CODE_RELEASE_SKILL = "release-skill";
	
	/**
	 * <p>拦截前消息的消息头<br>
	 * 释放技能结束
	 * 
	 * <p><b>开胃酒消息格式</b>
	 * <blockquote><table>
	 * <tr><th>key</th><th>value 及说明</th></tr>
	 * <tr><td>seat</td><td>byte, 攻击方座位</td></tr>
	 * <tr><td>no</td><td>byte, 攻击方 no 编号</td></tr>
	 * <tr><td>skillId</td><td>short, 本次释放的技能 ID 号</td></tr>
	 * <tr><td>result</td><td>string, 结果, 可以为 success fail 或 miss.</td></tr>
	 * <tr><td>[reason]</td><td>string, 原因, 当发动失败时, 显示原因</td></tr>
	 * </table></blockquote>
	 * <li>当技能释放途中, 攻击方如果因为各种原因使自己濒死, seat 值必须为 -1.
	 * </li></p>
	 * 
	 * <p><b>命令行消息类型</b>
	 * <blockquote><table>
	 * <tr><th>说明</th><th>command format</th></tr>
	 * <tr><td>技能成功结束</td>
	 * <td><code>release-finish moveable &lt;no&gt;</code></td></tr>
	 * <tr><td>技能发动因为无法行动结束</td>
	 * <td><code>release-finish success &lt;no&gt; [reason]</code></td></tr>
	 * <tr><td>技能发动失败结束</td>
	 * <td><code>release-finish fail &lt;no&gt; [reason]</code></td></tr>
	 * <tr><td>技能发动以未命中结束</td>
	 * <td><code>release-finish miss &lt;no&gt; [reason]</code></td></tr>
	 * </table></blockquote>
	 * <li>飞踢等未命中会反伤的技能不会以未命中结束.
	 * 一旦出现反伤的效果将计为成功触发结束.
	 * </li></p>
	 * 
	 * @since v0.2.3
	 */
	public static final String CODE_RELEASE_FINISH = "release-finish";
	
	/**
	 * 由于攻击方发动技能而使防御方受到伤害的消息头
	 */
	public static final String CODE_SKILL_DAMAGE = "skill-damage";
	
	/**
	 * 由于各种原因，精灵处于濒死状态而退场的消息头
	 */
	public static final String CODE_EXEUNT_FAINT = "exeunt-faint";
	
	/**
	 * 请求选择精灵上场的消息头
	 */
	public static final String CODE_REQUEST_PM = "request-pokemon";
	
	/**
	 * 回合结束的消息头
	 */
	public static final String CODE_ROUND_END = "round-end";
	
	/**
	 * 由于非技能受到伤害的消息头
	 */
	public static final String CODE_NONMOVES_DAMAGE = "nonMoves-damage";
	
	/**
	 * 恢复生命值
	 */
	public static final String CODE_RECOVER = "recover";
	
	/**
	 * 由于各种原因，精灵交换而退场的消息头
	 */
	public static final String CODE_EXEUNT_EXCHANGE = "exeunt-exchange";
	
	/**
	 * 计算各能力值
	 */
	public static final String
		CODE_CALC_AT = "calc-at",
		CODE_CALC_DF = "calc-df",
		CODE_CALC_SA = "calc-sa",
		CODE_CALC_SD = "calc-sd",
		CODE_CALC_SP = "calc-sp";
	
	/**
	 * 计算攻击方命中率
	 */
	public static final String
		CODE_CALC_HITRATE = "calc-hitrate";
	
	/**
	 * 计算技能命中率
	 */
	public static final String
		CODE_CALC_ACCURACY = "calc-accuracy";
	
	/**
	 * 计算躲藏
	 */
	public static final String
		CODE_CALC_HIDE = "calc-hide";
	
	/**
	 * 确定释放技能的真实属性
	 */
	public static final String
		CODE_CALC_TYPE = "calc-type";
	
	/**
	 * 确定防御方对技能的免疫情况
	 */
	public static final String
		CODE_JUDGE_IMMUSE = "judge-immuse";
	
	/**
	 * 计算 CT 暴击等级
	 */
	public static final String
		CODE_CALC_CT = "calc-ct";
	
	/**
	 * 计算技能威力
	 */
	public static final String
		CODE_CALC_POWER = "calc-power";
	
	/**
	 * 计算技能伤害, 最后伤害修正
	 */
	public static final String
		CODE_CALC_DAMAGE = "calc-damage";
	
	/**
	 * 计算克制倍率
	 */
	public static final String
		CODE_CALC_TYPE_RATE = "calc-typeRate";
	
	/**
	 * 计算附加状态释放几率、能否触发
	 */
	public static final String
		CODE_CALC_ADDITION_RATE = "calc-addition-rate";
	
	/**
	 * 计算修正率
	 */
	public static final String
		CODE_CALC_CORRENT = "calc-corrent";
	
	/**
	 * 施加状态消息头
	 */
	public static final String CODE_FORCE_STATE = "force-state";
	
	/**
	 * 删除状态消息头
	 */
	public static final String CODE_REMOVE_STATE = "remove-state";
	
	/**
	 * 修改状态状态消息头
	 */
	public static final String CODE_STATE_SET = "state-set";
	
	/**
	 * 消息头: 除状态变化、能力等级变化外, 所有技能附加效果的判断和结算
	 * <p>注: 吸血、反伤类技能(含回复技)</p>
	 */
	public static final String CODE_ADDITION_SETTLE = "addition-settle";
	
	/**
	 * 判定能否行动的消息头
	 */
	public static final String CODE_JUDGE_MOVEABLE = "judge-moveable";
	
	/**
	 * 判定技能范围的消息头
	 */
	public static final String CODE_JUDGE_RANGE = "judge-range";
	
	/**
	 * 施加异常状态消息头<br>
	 * 也可用作清除异常状态
	 */
	public static final String CODE_FORCE_ABNORMAL = "force-abnormal";
	
	/**
	 * 能力等级变化 / 设置消息头
	 */
	public static final String CODE_ABILITY_LEVEL = "ability-level";
	
	/**
	 * 广播
	 */
	public static final String CODE_BROADCAST = "broadcast";
	
	/* ************
	 *	附加效果  *
	 ************ */
	
	/**
	 * <p>单回合多轮攻击的攻击次数确定
	 * 
	 * <p><b>开胃酒消息格式</b>
	 * <blockquote><table>
	 * <tr><th>key</th><th>value 及说明</th></tr>
	 * <tr><th>SEATS</th><td>攻击方座位</td></tr>
	 * <tr><td>seat</td><td>byte, 攻击方座位</td></tr>
	 * <tr><td>round</td><td>int, 暂时确定的攻击次数</td></tr>
	 * <tr><td>mode</td><td>int, 见 {@code MultiStrikeInstruction} 中 mode 的说明</td></tr>
	 * </table></blockquote>
	 * <li>从 addition-settle 中分化, 仅在 2-5 次等攻击次数浮动时触发的消息.
	 * </li></p>
	 * 
	 * @since v0.2.3
	 */
	public static final String CODE_MULTI_STRIKE_COUNT = "multi-strike-count";
	
}