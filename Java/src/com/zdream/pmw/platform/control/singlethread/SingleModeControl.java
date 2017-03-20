package com.zdream.pmw.platform.control.singlethread;

import com.zdream.pmw.platform.control.ControlBase;
import com.zdream.pmw.platform.control.ControlManager;
import com.zdream.pmw.platform.prototype.IMessageCallback;
import com.zdream.pmw.platform.prototype.IRequestCallback;

/**
 * 单线程模式控制体<br>
 * <b>v0.2</b><br>
 *   控制方式拆分成单线程模式和多线程模式后, 添加了回调函数的属性<br>
 * 
 * @since v0.2
 * @author Zdream
 * @date 2017年2月25日
 * @version v0.2
 */
public abstract class SingleModeControl extends ControlBase {

	public SingleModeControl(ControlManager cm) {
		super(cm);
	}

	/* ************
	 *	回调函数  *
	 ************ */
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
	 *	用户等待  *
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
	 * 等待下一个请求 (采用顺序请求)<br>
	 * 阻塞方法，由玩家、AI 等调用，当出现请求时返回
	 * @return
	 *   了解该请求是什么<br>
	 *   请求行动、请求换人名额、战斗结束
	 * @see ControlBase#requestContent()
	 */
	public String nextRequest() {
		return requestContent();
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
	
}