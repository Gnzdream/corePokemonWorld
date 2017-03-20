package com.zdream.pmw.platform.control.multithread;

import com.zdream.pmw.platform.control.ControlManager;
import com.zdream.pmw.platform.control.IPrintLevel;

/**
 * 玩家控制类 (多线程模式)<br>
 * <br>
 * 这个 ControlBase 由游戏玩家来控制<br>
 *   这个控制类会将请求发送到 主线程来请求下一步的操作<br>
 *   同时这个控制类也将被阻塞，等待主线程的唤醒<br>
 *   其包含的 RequestSignal 数据将交给其他的线程来处理<br>
 * <br>
 *   正常情况，每一次战斗的时候，最多有一个 PlayerControl 在运行<br>
 * <br>
 * <b>v0.2</b><br>
 *   将单线程模式和多线程模式进行分拆之后, 该类从
 *   <code>com.zdream.pmw.platform.control</code>包中移到了
 *   <code>com.zdream.pmw.platform.control.multithread</code>，作为多线程模式的一部分<br>
 *   单线程模式为<code>com.zdream.pmw.platform.control.singlethread.PlayerControl</code>
 *   @see com.zdream.pmw.platform.control.singlethread.PlayerControl<br>
 * 
 * @since v0.1
 * @author Zdream
 * @date 2016年4月1日
 * @version v0.2
 */
public class MultiPlayerControl extends MultiModeControl {

	@Override
	public void nextActionRequest() {
		synchronized (this) {
			cm.logPrintf(IPrintLevel.PRINT_LEVEL_DEBUG,
					"MultiPlayerControl.nextActionRequest() wait");
			notify();
			cm.logPrintf(IPrintLevel.PRINT_LEVEL_DEBUG,
					"MultiPlayerControl.nextActionRequest() be notified");
		}
	}

	public MultiPlayerControl(ControlManager cm) {
		super(cm);
		this.setType(TYPE_PLAYER);
	}

}
