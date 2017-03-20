package com.zdream.pmw.platform.control.singlethread;

import com.zdream.pmw.platform.control.ControlBase;
import com.zdream.pmw.platform.control.ControlManager;
import com.zdream.pmw.platform.control.IRequestSemaphore;
import com.zdream.pmw.platform.prototype.RuleConductor;

/**
 * 单线程模式请求提交信号灯
 * <br>
 * <b>v0.2</b><br>
 *   抽出接口, 使单线程和多线程两个模式有不同的实现<br>
 *   @see com.zdream.pmw.platform.control.multithread.MultiRequsetSemaphore<br>
 * 
 * @since v0.2
 * @author Zdream
 * @date 2017年2月26日
 * @version v0.2
 */
public class SingleRequestSemaphore implements IRequestSemaphore {

	@Override
	public void requestMove(byte team, byte[] seats) {
		ControlBase base = cm.getCtrl(team);
		base.nextMoveRequest(seats); // 里面调用了 base, base 调用了 onCommitResponse
	}
	
	@Override
	public void requestEnterance(byte team, byte[] seats) {
		ControlBase base = cm.getCtrl(team);
		base.nextEnteranceRequest(seats); // 里面调用了 base, base 调用了 onCommitResponse
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
	public void onWaitForResponse() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onCommitResponse() {
		// TODO Auto-generated method stub
		
	}
	
	/* ************
	 *	 初始化   *
	 ************ */
	
	ControlManager cm;

	public SingleRequestSemaphore(ControlManager cm) {
		this.cm = cm;
	}

}
