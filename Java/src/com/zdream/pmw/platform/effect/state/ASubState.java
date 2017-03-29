package com.zdream.pmw.platform.effect.state;

import com.zdream.pmw.platform.attend.IState;
import com.zdream.pmw.platform.attend.service.EStateSource;

/**
 * <p>从属状态超类</p>
 * 
 * @since v0.2.1
 * @author Zdream
 * @date 2017年3月20日
 * @version v0.2.1
 */
public abstract class ASubState implements IState {

	/* ************
	 *	  属性    *
	 ************ */
	
	protected final IState mainState;
	
	public IState getMainState() {
		return mainState;
	}
	
	/* ************
	 *	实现方法  *
	 ************ */

	@Override
	public String name() {
		return mainState.name() + "+";
	}
	
	@Override
	public final EStateSource source() {
		return EStateSource.STATE;
	}

	/* ************
	 *	 初始化   *
	 ************ */
	
	public ASubState(IState mainState) {
		if (mainState == null) {
			throw new NullPointerException("mainState can not be empty.");
		}
		this.mainState = mainState;
	}

}
