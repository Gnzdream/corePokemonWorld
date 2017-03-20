package com.zdream.pmw.platform.prototype;

import com.zdream.pmw.platform.control.ControlBase;

/**
 * 消息返回请求的回调接口<br>
 * <br>
 * 在使用单线程模式时, 当游戏进行到需要玩家或 AI 进行相应反馈的时候, 就会回调该类的主方法体,<br>
 * 玩家的回调函数需要自己制作.<br>
 * 
 * @since v0.2
 * @author Zdream
 * @date 2017年2月25日
 * @version v0.2
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
