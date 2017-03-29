package com.zdream.pmw.platform.effect.state;

import com.zdream.pmw.platform.prototype.BattlePlatform;

/**
 * 能产生附属状态的接口<br>
 * <p>实现该接口的状态能够产生附属状态</p>
 * <p>状态的生成将在实现层完成, 换句话说, 这类状态的产生将不需要经过 
 * {@link com.zdream.pmw.platform.effect.Aperitif}</p>
 * 
 * @since v0.2.1
 * @author Zdream
 * @date 2017年3月20日
 * @version v0.2.1
 */
public interface ISubStateCreater {
	
	/**
	 * 产生附属状态
	 * @param codes
	 * @return
	 */
	public ASubState[] getSubStates();
	
	/**
	 * 处理创建附属状态的全过程
	 * @param pf
	 */
	public void handleCreateSubStates(BattlePlatform pf);
	
	/**
	 * 处理销毁附属状态的全过程
	 * @param pf
	 */
	public void handleDestroySubStates(BattlePlatform pf);

}
