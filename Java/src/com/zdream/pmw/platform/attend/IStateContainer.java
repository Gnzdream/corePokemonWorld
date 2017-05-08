package com.zdream.pmw.platform.attend;

import java.util.List;

import com.zdream.pmw.platform.prototype.BattlePlatform;
import com.zdream.pmw.util.json.JsonObject;

/**
 * 状态列表处理接口<br>
 * <br>
 * <p><b>v0.1.1</b><br>
 * 添加利用名称来寻找对应的状态的方法接口<br></p>
 * 
 * <p><b>v0.2.2</b><br>
 * 添加更加便捷的删除状态的方法<br></p>
 * 
 * @since v0.1.1
 *   [2016-04-17]
 * @author Zdream
 * @version v0.2.2
 *   [2017-04-14]
 */
public interface IStateContainer {

	public List<IState> getStates();
	
	/**
	 * 向列表中加入状态
	 * @param state
	 * @param pf
	 */
	public void pushState(IState state, BattlePlatform pf);

	/**
	 * 向列表中删除状态
	 * @param state
	 * @param pf
	 */
	public void removeState(IState state, BattlePlatform pf);
	
	/**
	 * 列表中删除所有指定名称的状态
	 * @param stateName
	 *   指定的状态名称, {@link IState#name()}
	 * @param pf
	 * @since v0.2.2
	 */
	public void removeState(String stateName, BattlePlatform pf);
	
	/**
	 * 设置状态参数
	 * @param stateName
	 * @param value
	 * @param pf
	 */
	public void setState(String stateName, JsonObject value, BattlePlatform pf);
	
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