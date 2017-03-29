package com.zdream.pmw.platform.control.multithread;

import com.zdream.pmw.platform.control.ControlBase;
import com.zdream.pmw.platform.control.ControlManager;
import com.zdream.pmw.platform.control.IPrintLevel;
import com.zdream.pmw.platform.control.IRequestSemaphore;
import com.zdream.pmw.platform.prototype.RuleConductor;

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
 * @version v0.2.1
 */
public class MultiRequsetSemaphore implements IRequestSemaphore {
	
	@Override
	public void requestMove(byte team, byte[] seats) {
		ControlBase base = cm.getCtrl(team);
		base.nextMoveRequest(seats);
		waitCount();
	}
	
	@Override
	public void requestSwitch(byte team, byte[] seats) {
		ControlBase base = cm.getCtrl(team);
		base.nextSwitchRequest(seats);
		waitCount();
	}
	
	@Override
	public void requestEnd(byte successCamp) {
		RuleConductor referee = cm.getRoot().getReferee();
		int teamLength = cm.teamLength();
		ControlBase base;
		
		for (byte team = 0; team < teamLength; team++) {
			base = cm.getCtrl(team);
			if (referee.teamToCamp(team) == successCamp) {
				base.endRequest(1);
			} else if (successCamp == -1) {
				base.endRequest(0);
			} else {
				base.endRequest(2);
			}
		}
	}
	
	@Override
	public synchronized void onWaitForResponse() {
		if (count > 0) {
			sleep = true;
			try {
				wait();
			} catch (InterruptedException e) {
				cm.logPrintf(IPrintLevel.PRINT_LEVEL_WARN, e);
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
