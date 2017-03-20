package com.zdream.pmw.platform.control.multithread;

import com.zdream.pmw.platform.control.ControlManager;
import com.zdream.pmw.platform.control.IPrintLevel;
import com.zdream.pmw.platform.control.ai.DefaultAIBrain;
import com.zdream.pmw.platform.control.ai.IAIRunnable;

/**
 * 自动控制类 (多线程模式)<br>
 * <br>
 * <b>v0.2</b><br>
 *   将单线程模式和多线程模式进行分拆之后, 该类从
 *   <code>com.zdream.pmw.platform.control</code>包中移到了
 *   <code>com.zdream.pmw.platform.control.multithread</code>，作为多线程模式的一部分<br>
 *   单线程模式为<code>com.zdream.pmw.platform.control.singlethread.AIControl</code>
 *   @see com.zdream.pmw.platform.control.singlethread.AIControl
 * 
 * @since v0.1
 * @author Zdream
 * @date 2016年4月1日
 * @version v0.2
 */
public class MultiAIControl extends MultiModeControl {
	
	/* ************
	 *	 AI 线程  *
	 ************ */
	private IAIRunnable runnable; // 它不是线程, 但能生成线程

	@Override
	public void nextActionRequest() {
		synchronized (this) {
			cm.logPrintf(IPrintLevel.PRINT_LEVEL_DEBUG,
					"MultiAIControl.nextActionRequest() wait");
			notify();
			cm.logPrintf(IPrintLevel.PRINT_LEVEL_DEBUG,
					"MultiAIControl.nextActionRequest() be notified");
		}
	}

	public MultiAIControl(ControlManager cm) {
		super(cm);
		this.setType(TYPE_AUTO);
		
		runnable = new DefaultAIBrain(cm);
		runnable.setCtrl(this);
		new Thread(runnable.createAIRunnable()).start();
	}

}
