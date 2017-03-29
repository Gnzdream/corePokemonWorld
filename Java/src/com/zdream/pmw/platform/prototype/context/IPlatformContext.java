package com.zdream.pmw.platform.prototype.context;

/**
 * <p>战场上下文接口</p>
 * 
 * @since v0.2.2
 * @author Zdream
 * @date 2017年3月28日
 * @version v0.2.2
 */
public interface IPlatformContext {
	
	/**
	 * 获得指定在场怪兽的实际情况
	 * @param seat
	 * @return
	 */
	public ParticipantContext getParticipantContext(byte seat);
	
}
