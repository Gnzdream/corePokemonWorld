package com.zdream.pmw.platform.control;

/**
 * 回应指令参数接口<br>
 * 将常用的回应指令在此写明<br>
 * 写明玩家或 AI 向系统发送的信息的种类<br>
 * 
 * @since v0.1
 * @author Zdream
 * @date 2016年4月5日
 * @version v0.1
 */
public interface IRespondKey {
	
	/**
	 * 选择下一回合的行动时使用的指令 key
	 */
	public static final String
			COMMAND_MOVES = "MOVES", // 技能指令
			COMMAND_BAG = "BAG", // 背包指令
			COMMAND_REPLACE = "REPLACE", // 调换指令
			COMMAND_ESCAPE = "ESCAPE"; // 逃跑指令

}
