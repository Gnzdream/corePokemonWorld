package com.zdream.pmw.platform.control.multithread;

import com.zdream.pmw.platform.control.ControlBase;
import com.zdream.pmw.platform.control.ControlManager;

/**
 * 多线程模式控制体<br>
 * <b>v0.2</b><br>
 *   控制方式拆分成单线程模式和多线程模式<br>
 * 
 * @since v0.2
 * @author Zdream
 * @date 2017年2月25日
 * @version v0.2.2
 */
public abstract class MultiModeControl extends ControlBase {

	public MultiModeControl(ControlManager cm) {
		super(cm);
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

}
