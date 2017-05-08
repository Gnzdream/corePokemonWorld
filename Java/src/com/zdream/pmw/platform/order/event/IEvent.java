package com.zdream.pmw.platform.order.event;

import com.zdream.pmw.platform.prototype.BattlePlatform;

/**
 * <p>行动的事件接口</p>
 * 
 * <p><b>v0.1</b><br>
 *   将行动事件的类名进行正规化</p>
 * 
 * <p><b>v0.2.3</b><br>
 *   将事件抽象类({@code AEvent})修改为接口({@code IEvent}).</p>
 * 
 * @since v0.1 [2016-04-11]
 * @author Zdream
 * @version v0.2.3 [2016-04-21]
 */
public interface IEvent {
	
	/* ************
	 *	  触发    *
	 ************ */
	/**
	 * 触发事件行动
	 * @param pf
	 *   since 0.2
	 */
	public abstract void action(BattlePlatform pf);

}
