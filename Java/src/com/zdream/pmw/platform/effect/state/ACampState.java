package com.zdream.pmw.platform.effect.state;

import java.util.Map;

import com.zdream.pmw.platform.attend.IState;
import com.zdream.pmw.platform.attend.service.EStateSource;
import com.zdream.pmw.platform.prototype.BattlePlatform;
import com.zdream.pmw.util.json.JsonValue;

/**
 * <p>归属方为指定阵营的状态</p>
 * <p>这类状态被限制归属方为阵营, 它与某个阵营进行了绑定. 该类状态多半为场地状态</p>
 * 
 * @since v0.2.2 [2017-04-17]
 * @author Zdream
 * @version v0.2.2 [2017-04-17]
 */
public abstract class ACampState implements IState {

	/* ************
	 *	  属性    *
	 ************ */
	
	protected EStateSource source;

	protected byte camp;
	
	public void setSource(EStateSource source) {
		this.source = source;
	}
	public void setSource(String source) {
		this.source = EStateSource.parseEnum(source);
	}
	public byte getCamp() {
		return camp;
	}
	public void setCamp(byte camp) {
		this.camp = camp;
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
		// init: camp
		if (map.containsKey("camp")) {
			camp = (Byte) map.get("camp").getValue();
		}
		// init: source
		if (map.containsKey("source")) {
			setSource(map.get("source").getString());
		}
	}

	/* ************
	 *	 初始化   *
	 ************ */
	
	protected ACampState(byte camp) {
		this.camp = camp;
	}
	
	protected ACampState() {}

}
