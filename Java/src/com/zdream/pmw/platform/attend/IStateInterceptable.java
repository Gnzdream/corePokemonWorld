package com.zdream.pmw.platform.attend;

import com.zdream.pmw.platform.effect.Aperitif;

/**
 * 可执行的状态拦截器<br>
 * 为了解耦，从 <code>StateInterceptor</code> 中抽出的接口<br>
 * <br>
 * <b>v0.2.1</b>
 * <p>利用 {@link Aperitif} 的新特征, 允许拦截器跳过某些状态的触发</p>
 * 
 * @since v0.1
 * @author Zdream
 * @date 2016年4月12日
 * @version v0.2.1
 */
public interface IStateInterceptable {

	/**
	 * 下一个 state 的拦截<br>
	 * 如果后面没有 state，则运行 realizer
	 * @return
	 *   返回拦截器下层执行返回的信息
	 */
	String nextState();
	
	/**
	 * 忽略后面所有的状态, 直接得到当前的执行命令行语句<br>
	 * <p>可能当前状态过滤时, 已经得到处理的结果, 因此跳过后面所有的状态,
	 * 直接交给 realizer 进行处理</p>
	 * @return
	 *   直接返回执行返回的信息
	 * @since
	 *   v0.2.1
	 */
	String getCommand();

}