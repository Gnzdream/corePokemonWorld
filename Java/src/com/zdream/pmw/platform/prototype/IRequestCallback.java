package com.zdream.pmw.platform.prototype;

import com.zdream.pmw.platform.control.ControlBase;

/**
 * 消息返回请求的回调接口<br>
 * <br>
 * 当游戏进行到需要玩家或 AI 进行相应反馈的时候, 就会回调该类的主方法体,<br>
 * 玩家的回调函数需要自己制作.<br>
 * 
 * <p><b>v0.2.1</b><br>
 * 只在单线程模式时, 实现该接口是必须的.</p>
 * 
 * <p><b>v0.2.2</b><br>
 * 现在无论是单线程模式还是多线程模式, 都需要实现该回调接口.
 * 在多线程模式实现接口时, 线程不应该是阻塞的, 应该尽快完成,
 * 推荐的方式是在回调函数中通知另一个线程完成向控制体发送请求消息.</p>
 * 
 * @since v0.2
 * @author Zdream
 * @date 2017年2月25日
 * @version v0.2.2
 */
public interface IRequestCallback {
	
	/**
	 * 回调, 玩家和 AI 部分实现
	 * @param platform
	 *   战斗平台环境
	 * @param ctrl
	 *   控制体
	 */
	public void onRequest(BattlePlatform platform, ControlBase ctrl);
	
}
