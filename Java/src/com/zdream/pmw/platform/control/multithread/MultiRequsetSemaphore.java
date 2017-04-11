package com.zdream.pmw.platform.control.multithread;

import com.zdream.pmw.platform.control.ControlBase;
import com.zdream.pmw.platform.control.ControlManager;
import com.zdream.pmw.platform.control.IPrintLevel;
import com.zdream.pmw.platform.control.IRequestSemaphore;

/**
 * 多线程模式请求行动红绿灯<br>
 * <br>
 * <b>v0.2</b><br>
 *   抽出接口, 使单线程和多线程两个模式有不同的实现<br>
 *   @see com.zdream.pmw.platform.control.singlethread.SingleRequestSemaphore<br>
 * 
 * @since v0.1
 * @author Zdream
 * @date 2016年4月4日
 * @version v0.2.2
 */
public class MultiRequsetSemaphore implements IRequestSemaphore {
	
	@Override
	public void inform() {
		final int length = cm.teamLength();
		for (byte team = 0; team < length; team++) {
			ControlBase base = cm.getCtrl(team);
			if (base.isReady()) {
				base.inform();
				waitCount();
			}
		}
		
		if (count > 0) {
			synchronized (this) {
				sleep = true;
				try {
					wait();
				} catch (InterruptedException e) {
					cm.logPrintf(IPrintLevel.PRINT_LEVEL_WARN, e);
				}
			}
		}
	}
	
	/* ************
	 *	 计数器   *
	 ************ */
	/**
	 * 正在等待回应的 Control 个数
	 */
	private int count = 0;
	
	/**
	 * 是否处于等待 Control 回应状态
	 */
	private boolean sleep = false;
	
	private synchronized void waitCount() {
		count++;
	}
	
	/**
	 * notifyCounts
	 */
	@Override
	public synchronized void onCommitResponse() {
		count--;
		if (count == 0 && sleep) {
			notify();
			cm.logPrintf(IPrintLevel.PRINT_LEVEL_DEBUG, "所有请求全部回应，系统被唤醒");
		}
	}
	
	/* ************
	 *	 初始化   *
	 ************ */
	
	ControlManager cm;

	public MultiRequsetSemaphore(ControlManager cm) {
		this.cm = cm;
	}

}
