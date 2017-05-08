package com.zdream.pmw.platform.order.event;

import com.zdream.pmw.util.json.JsonObject;

/**
 * <p>行动事件</p>
 * <p>包括技能释放、嗑药、更换怪兽等</p>
 * 
 * @since v0.2.3 [2017-04-21]
 * @author Zdream
 * @version v0.2.3 [2017-04-21]
 */
public abstract class AMoveEvent implements IEvent {

	/**
	 * 该技能释放者的 no 值<br>
	 */
	protected byte no;
	
	public byte getNo() {
		return no;
	}

	public void setNo(byte no) {
		this.no = no;
	}
	
	/**
	 * 其它附加参数, 用于修改技能释放时的技能参数、触发方式、结算公式等
	 * @since v0.2.2
	 */
	protected JsonObject param;
	
	public JsonObject getParam() {
		return param;
	}
	
	public void setParam(JsonObject param) {
		this.param = param;
	}
	
}
