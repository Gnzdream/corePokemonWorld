package com.zdream.pmw.platform.order.event;

/**
 * <p>提前事件的载体</p>
 * 
 * @since v0.2.3 [2017-04-21]
 * @author Zdream
 * @version v0.2.3 [2017-04-21]
 */
class PreviousEventEntry implements Comparable<PreviousEventEntry> {
	
	APreviousEvent event;
	int priority;

	@Override
	public int compareTo(PreviousEventEntry o) {
		return this.priority - o.priority;
	}
	
	public PreviousEventEntry(APreviousEvent event) {
		this.event = event;
		this.priority = event.priority();
	}

}
