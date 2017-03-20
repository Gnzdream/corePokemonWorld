package com.zdream.pmw.platform.attend;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.zdream.pmw.platform.prototype.BattlePlatform;
import com.zdream.pmw.util.json.JsonValue;

/**
 * 状态容器<br>
 * 座位、阵营、全场需要的状态容器，来存放状态<br>
 * 
 * @since v0.1
 * @author Zdream
 * @date 2016年4月12日
 * @version v0.1
 */
public class StateContainer implements IStateHandler {
	
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
	public void pushState(IState state) {
		states.add(state);
	}
	
	@Override
	public void removeState(IState state) {
		states.remove(state);
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
