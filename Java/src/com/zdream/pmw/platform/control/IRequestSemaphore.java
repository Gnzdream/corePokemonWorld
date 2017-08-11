package com.zdream.pmw.platform.control;

/**
 * 信号灯接口<br>
 * 用于指导和控制控制体传输请求、获得响应<br>
 * <br>
 * <b>v0.2.1</b>
 * <p>添加选择怪兽上场的方法</p>
 * <br>
 * <b>v0.2.2</b>
 * <p>为了将单线程模式和多线程模式进行进一步的行动统一,
 * 该接口进行了简化.</p>
 * 
 * @since v0.2
 * @author Zdream
 * @date 2017年3月1日
 * @version v0.2.2
 */
public interface IRequestSemaphore {
	
	/**
	 * <p>发送请求的消息</p>
	 * <p>该方法在多线程模式下是阻塞的.</p>
	 */
	public void inform();
	
	/**
	 * 客户端 (玩家 / AI 等) 部分提交请求时触发<br>
	 * 该函数需要检查数据的完整性, 尽管在 ControlBase 中已经进行了一次初步的检查.<br>
	 * 在多线程模式中还需要改动信号量.<br>
	 */
	public void onCommitResponse();
	
}
