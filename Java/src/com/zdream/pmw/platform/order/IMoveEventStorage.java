package com.zdream.pmw.platform.order;

import com.zdream.pmw.platform.order.event.IEvent;

/**
 * <p>提供行动事件接口</p>
 * 
 * @since v0.2.3 [2017-04-21]
 * @author Zdream
 * @version v0.2.3 [2017-04-21]
 */
public interface IMoveEventStorage {
	
	public void store(IEvent... events);

}
