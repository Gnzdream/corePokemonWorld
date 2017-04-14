package com.zdream.pmw.platform.effect.state;

import java.util.Map;

import com.zdream.pmw.platform.attend.IState;
import com.zdream.pmw.platform.attend.service.EStateSource;
import com.zdream.pmw.platform.prototype.BattlePlatform;
import com.zdream.pmw.util.json.JsonValue;

/**
 * <p>归属方为指定座位的状态</p>
 * <p>这类状态被限制归属方为座位, 它与某个怪兽所在的位置绑定.</p>
 * 
 * @since v0.2.2
 *   [2017-04-14]
 * @author Zdream
 * @version v0.2.2
 *   [2017-04-14]
 */
public abstract class ASeatState implements IState {

	/* ************
	 *	  属性    *
	 ************ */
	
	protected EStateSource source;

	protected byte seat;

	public EStateSource getSource() {
		return source;
	}
	public void setSource(EStateSource source) {
		this.source = source;
	}
	public void setSource(String source) {
		this.source = EStateSource.parseEnum(source);
	}
	public byte getSeat() {
		return seat;
	}
	public void setSeat(byte seat) {
		this.seat = seat;
	}

	/* ************
	 *	实现方法  *
	 ************ */

	@Override
	public EStateSource source() {
		return source;
	}

	@Override
	public void set(JsonValue v, BattlePlatform pf) {
		Map<String, JsonValue> map = v.getMap();
		// init: seat
		if (map.containsKey("seat")) {
			seat = (Byte) map.get("seat").getValue();
		}
		// init: source
		if (map.containsKey("source")) {
			setSource(map.get("source").getString());
		}
	}

	/* ************
	 *	 初始化   *
	 ************ */
	
	protected ASeatState(byte seat) {
		this.seat = seat;
	}
	
	protected ASeatState() {}

}
