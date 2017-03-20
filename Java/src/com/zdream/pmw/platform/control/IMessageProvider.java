package com.zdream.pmw.platform.control;

/**
 * 战场信息发生变化的时候, 向各个控制方发送战场的消息
 * 
 * @since v0.2
 * @author Zdream
 * @date 2017年2月26日
 * @version v0.2
 */
public interface IMessageProvider {
	
	/**
	 * 向各个控制方提供战场变化的消息
	 * @param msg
	 *   消息内容, 一般是类似命令行字符的数据
	 */
	public void provide(String msg);

}
