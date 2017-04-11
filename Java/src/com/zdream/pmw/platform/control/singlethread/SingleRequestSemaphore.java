package com.zdream.pmw.platform.control.singlethread;

import com.zdream.pmw.platform.control.ControlBase;
import com.zdream.pmw.platform.control.ControlManager;
import com.zdream.pmw.platform.control.IRequestSemaphore;

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
 * @version v0.2.2
 */
public class SingleRequestSemaphore implements IRequestSemaphore {

	@Override
	public void inform() {
		final int length = cm.teamLength();
		for (byte team = 0; team < length; team++) {
			ControlBase base = cm.getCtrl(team);
			if (base.isReady()) {
				base.inform();
			}
		}
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
