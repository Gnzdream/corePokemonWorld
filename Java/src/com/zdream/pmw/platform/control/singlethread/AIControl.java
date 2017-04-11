package com.zdream.pmw.platform.control.singlethread;

import com.zdream.pmw.platform.control.ControlManager;

/**
 * 自动控制类 (单线程模式)<br>
 * <br>
 * <b>v0.2</b><br>
 *   将单线程模式和多线程模式进行分拆之后, 该类从
 *   <code>com.zdream.pmw.platform.control</code>包中移到了
 *   <code>com.zdream.pmw.platform.control.singlethread</code>，作为多线程模式的一部分<br>
 *   多线程模式为<code>com.zdream.pmw.platform.control.multithread.MultiAIControl</code>
 *   @see com.zdream.pmw.platform.control.multithread.MultiAIControl<br>
 * 
 * @since v0.2
 * @author Zdream
 * @date 2017年2月25日
 * @version v0.2.2
 */
public class AIControl extends SingleModeControl {

	public AIControl(ControlManager cm) {
		super(cm);
		this.setType(TYPE_AUTO);
	}
}
