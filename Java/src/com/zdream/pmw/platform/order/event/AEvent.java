package com.zdream.pmw.platform.order.event;

import com.zdream.pmw.platform.prototype.BattlePlatform;

/**
 * 行动的事件<br>
 * <br>
 * <b>v0.1</b><br>
 *   将行动事件的类名进行正规化<br>
 * 
 * @since v0.1
 * @author Zdream
 * @date 2016年4月11日
 * @version v0.1
 */
public abstract class AEvent {
	
	public enum EventType {
		PREVIOUS, MOVE, LAST;
	}
	
	/**
	 * 事件类型
	 */
	private EventType type;

	public EventType getType() {
		return type;
	}

	public void setType(EventType type) {
		this.type = type;
	}
	
	protected AEvent(EventType type) {
		this.type = type;
	}
	
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
