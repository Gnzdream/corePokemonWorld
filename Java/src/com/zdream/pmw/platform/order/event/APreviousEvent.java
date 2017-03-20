package com.zdream.pmw.platform.order.event;

/**
 * 默认的提前触发事件<br>
 * 
 * @since v0.1
 * @author Zdream
 * @date 2016年4月12日
 * @version v0.1
 */
public abstract class APreviousEvent extends AEvent implements Comparable<APreviousEvent> {

	public APreviousEvent() {
		super(EventType.PREVIOUS);
	}
	
	/**
	 * 返回优先度<br>
	 * 提前触发事件的触发顺序由优先度决定，优先度越高的该类事件越早执行
	 * @return
	 */
	public abstract int priority();
	
	@Override
	public final int compareTo(APreviousEvent o) {
		/*
		 * 想正序排列时，优先度是降序（优先度高的事件先触发）
		 */
		return this.priority() - o.priority();
	}

}
