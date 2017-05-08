package com.zdream.pmw.platform.effect.state;

import java.util.Map;

import com.zdream.pmw.platform.attend.AttendManager;
import com.zdream.pmw.platform.attend.IState;
import com.zdream.pmw.platform.attend.Participant;
import com.zdream.pmw.platform.attend.service.EStateSource;
import com.zdream.pmw.platform.prototype.BattlePlatform;
import com.zdream.pmw.platform.prototype.IPlatformComponent;
import com.zdream.pmw.util.json.JsonObject;
import com.zdream.pmw.util.json.JsonValue;

/**
 * 归属方为指定怪兽的状态<br>
 * 这类状态被限制归属方为怪兽本体, 存放于 <code>Participant</code> 中<br>
 * 
 * @see com.zdream.pmw.platform.attend.Participant
 * 
 * @since v0.2.1
 * @author Zdream
 * @date 2017年3月2日
 * @version v0.2.1
 */
public abstract class AParticipantState implements IState {

	/* ************
	 *	  属性    *
	 ************ */
	
	private EStateSource source;

	private byte no;
	
	/**
	 * @return 这个状态属于对应哪个怪兽
	 */
	public byte getNo() {
		return no;
	}
	
	/**
	 * @param no 设置这个状态属于对应哪个怪兽
	 */
	public void setNo(byte no) {
		this.no = no;
	}
	public void setSource(String source) {
		this.source = EStateSource.parseEnum(source);
	}
	public void setSource(EStateSource source) {
		this.source = source;
	}
	
	/**
	 * @since v0.2.2
	 */
	protected Participant participant(IPlatformComponent com) {
		AttendManager am = com.getRoot().getAttendManager();
		return am.getParticipant(am.seatForNo(no));
	}

	/* ************
	 *	实现方法  *
	 ************ */

	@Override
	public EStateSource source() {
		return source;
	}

	@Override
	public void set(JsonObject v, BattlePlatform pf) {
		Map<String, JsonValue> map = v.asMap();
		// init: no
		if (map.containsKey("no")) {
			no = (Byte) map.get("no").getValue();
		}
		// init: source
		if (map.containsKey("source")) {
			setSource(map.get("source").getString());
		}
	}

	/* ************
	 *	 初始化   *
	 ************ */
	
	protected AParticipantState(byte no) {
		this.no = no;
	}
	
	protected AParticipantState() {}

}
