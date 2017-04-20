package com.zdream.pmw.platform.attend;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.zdream.pmw.platform.effect.state.ISubStateCreater;
import com.zdream.pmw.platform.prototype.BattlePlatform;
import com.zdream.pmw.util.json.JsonValue;

/**
 * 状态容器<br>
 * 座位、阵营、全场需要的状态容器，来存放状态<br>
 * <br>
 * <b>v0.2.1</b>
 * <p><li>添加在添加和删除状态时, 触发状态的生命周期相关方法
 * <li>补充附属状态的产生方法
 * </li></p>
 * 
 * @since v0.1
 * @author Zdream
 * @date 2016年4月12日
 * @version v0.1
 */
public class StateContainer implements IStateContainer {
	
	/* ************
	 *	数据结构  *
	 ************ */
	
	protected final List<IState> states;
	
	/* ************
	 *	实现方法  *
	 ************ */

	@Override
	public List<IState> getStates() {
		return states;
	}
	
	@Override
	public void pushState(IState state, BattlePlatform pf) {
		states.add(state);
		state.onCreate();
		if (state instanceof ISubStateCreater) {
			((ISubStateCreater) state).handleCreateSubStates(pf);
		}
	}
	
	@Override
	public void removeState(IState state, BattlePlatform pf) {
		if (state instanceof ISubStateCreater) {
			((ISubStateCreater) state).handleDestroySubStates(pf);
		}
		state.onDestroy();
		states.remove(state);
	}
	
	@Override
	public void removeState(String stateName, BattlePlatform pf) {
		Set<IState> rms = new HashSet<>();
		for (Iterator<IState> it = states.iterator(); it.hasNext();) {
			IState state = it.next();
			if (state.name().equals(stateName)) {
				rms.add(state);
			}
		}
		
		if (rms.isEmpty()) {
			return;
		}
		
		for (Iterator<IState> it = rms.iterator(); it.hasNext();) {
			IState state = it.next();
			removeState(state, pf);
		}
	}
	
	@Override
	public IState getState(String stateName) {
		for (Iterator<IState> it = states.iterator(); it.hasNext();) {
			IState state = it.next();
			if (state.name().equals(stateName)) {
				return state;
			}
		}
		return null;
	}
	
	@Override
	public void setState(String stateName, JsonValue value, BattlePlatform pf) {
		for (Iterator<IState> it = states.iterator(); it.hasNext();) {
			IState state = it.next();
			if (state.name().equals(stateName)) {
				state.set(value, pf);
			}
		}
	}
	
	@Override
	public boolean hasCategoryState(String stateCategory) {
		for (Iterator<IState> it = states.iterator(); it.hasNext();) {
			IState state = it.next();
			if (state.ofCategory().equals(stateCategory)) {
				return true;
			}
		}
		return false;
	}
	
	/* ************
	 *	 初始化   *
	 ************ */

	public StateContainer() {
		states = new ArrayList<IState>();
	}

}
