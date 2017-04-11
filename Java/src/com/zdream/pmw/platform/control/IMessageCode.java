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
	 * 计算属性
	 */
	public static final String
		CODE_CALC_TYPE = "calc-type";
	
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
	 * 计算修正率
	 */
	public static final String
		CODE_CALC_ADDITION_RATE = "calc-addition-rate";
	
	/**
	 * 计算附加状态释放几率、能否触发
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
	
}