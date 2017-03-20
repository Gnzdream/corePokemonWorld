package com.zdream.pmw.platform.attend;

import java.util.List;

import com.zdream.pmw.platform.prototype.BattlePlatform;
import com.zdream.pmw.util.json.JsonValue;

/**
 * 状态列表处理接口<br>
 * <br>
 * v0.1.1 添加利用名称来寻找对应的状态的方法接口<br>
 * 
 * @since v0.1.1
 * @author Zdream
 * @date 2016年4月17日
 * @version v0.1.1
 */
public interface IStateHandler {

	public List<IState> getStates();
	
	/**
	 * 向列表中加入状态
	 * @param state
	 */
	public void pushState(IState state);

	/**
	 * 向列表中删除状态
	 * @param state
	 */
	public void removeState(IState state);
	
	/**
	 * 设置状态参数
	 * @param stateName
	 * @param value
	 * @param pf
	 */
	public void setState(String stateName, JsonValue value, BattlePlatform pf);
	
	/**
	 * 通过名称获得对应的状态
	 */
	public IState getState(String stateName);
	
	/**
	 * 该容器中是否存在该类型的状态<br>
	 * @param stateCategory
	 *   状态类型
	 * @see IState#ofCategory()
	 */
	public boolean hasCategoryState(String stateCategory);

}