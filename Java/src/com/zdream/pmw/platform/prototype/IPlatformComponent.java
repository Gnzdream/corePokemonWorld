package com.zdream.pmw.platform.prototype;

import com.zdream.pmw.platform.control.IPrintLevel;

/**
 * 战斗平台部件, 所有实现其方法的类需要受到 <code>BattlePlatform</code> 的管理<br>
 * 提供一些必要的基本方法和服务<br>
 * 
 * @since v0.2
 * @author Zdream
 * @date 2017年2月25日
 * @version v0.2
 */
public interface IPlatformComponent extends IPrintLevel {
	
	/* ************
	 *	获得结构  *
	 ************ */
	
	public BattlePlatform getRoot();
	
	/* ************
	 *	消息输出  *
	 ************ */
	
	/**
	 * 输出日志
	 */
	public void logPrintf(int level, String str, Object... params);

	/**
	 * 输出错误日志
	 */
	public void logPrintf(int level, Throwable throwable);

}
