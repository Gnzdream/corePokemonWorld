package com.zdream.pmw.platform.prototype;

/**
 * 返回战场信息变化的回调接口<br>
 * <br>
 * 在使用单线程模式时, 当游戏中的怪兽、平台数据产生变化的时候，就会回调该函数,<br>
 * 玩家的回调函数需要自己制作.<br>
 * 
 * @since v0.2
 * @author Zdream
 * @date 2017年2月26日
 * @version v0.2
 */
public interface IMessageCallback {
	
	/**
	 * 回调, 玩家和 AI 部分实现
	 * @param platform
	 *   战斗平台环境
	 * @param msg
	 *   消息. 战场的消息会以类似命令行字符的方式展现
	 */
	public void onMessage(BattlePlatform platform, String msg);

}
