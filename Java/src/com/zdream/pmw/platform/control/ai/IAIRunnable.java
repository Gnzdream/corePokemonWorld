package com.zdream.pmw.platform.control.ai;

import com.zdream.pmw.platform.control.ControlBase;
import com.zdream.pmw.platform.prototype.IRequestCallback;

/**
 * AI 控制接口<br>
 * <br>
 * <b>v0.2</b>
 *   由于线程模式拆分和细化, 该接口添加了行动部分<br>
 * 
 * @since v0.1
 * @author Zdream
 * @date 2017年3月1日
 * @version v0.2
 */
public interface IAIRunnable {

	/* ************
	 *	 控制体   *
	 ************ */
	
	public ControlBase getCtrl();
	public void setCtrl(ControlBase ctrl);
	
	/* ************
	 *	  行动    *
	 ************ */
	
	public Runnable createAIRunnable();
	public IRequestCallback createAICallback();

}
