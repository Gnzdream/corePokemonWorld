package com.zdream.pmw.platform.control;

/**
 * 信号灯接口<br>
 * 用于指导和控制控制体传输请求、获得响应<br>
 * 
 * @since v0.2
 * @author Zdream
 * @date 2017年3月1日
 * @version v0.2
 */
public interface IRequestSemaphore {
	
	/**
	 * 行动请求
	 * @param team
	 *   队伍号
	 * @param seats
	 *   该队伍需要行动的怪兽 seat 列表
	 */
	public void requestMove(byte team, byte[] seats);
	
	/**
	 * 怪兽选择怪兽或入场请求
	 * @param team
	 *   队伍号
	 * @param seats
	 *   选择怪兽需要入场的位置
	 */
	public void requestEnterance(byte team, byte[] seats);
	
	/**
	 * 请求通知所有的队伍的 Control，战斗已经结束<br>
	 * @param successCamp
	 *   这个是获胜的阵营，该阵营中的所有队伍宣告战斗胜利
	 *   当出现平局的情况，这个值为 -1
	 */
	public void requestEnd(byte successCamp);
	
	/**
	 * 所有的请求发出后, 系统需要调用该函数 (单线程和多线程模式)<br>
	 * 该函数需要检查现在所有的请求是否已经提交, 以及请求数据的完整性, <br>
	 * 以及如果不完整时的方案<br>
	 */
	public void onWaitForResponse();
	
	/**
	 * 客户端 (玩家 / AI 等) 部分提交请求时触发<br>
	 * 该函数需要检查数据的完整性, 在多线程模式中还需要改动信号量. <br>
	 */
	public void onCommitResponse();
	
}