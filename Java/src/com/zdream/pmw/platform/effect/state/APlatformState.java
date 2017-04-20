package com.zdream.pmw.platform.effect.state;

import java.util.Map;

import com.zdream.pmw.platform.attend.IState;
import com.zdream.pmw.platform.attend.service.EStateSource;
import com.zdream.pmw.platform.prototype.BattlePlatform;
import com.zdream.pmw.util.json.JsonValue;

/**
 * <p>全场场地状态</p>
 * 
 * @since v0.2.2 [2017-04-17]
 * @author Zdream
 * @version v0.2.2 [2017-04-17]
 */
public abstract class APlatformState implements IState {

	/* ************
	 *	  属性    *
	 ************ */
	
	protected EStateSource source;
	
	public void setSource(EStateSource source) {
		this.source = source;
	}
	public void setSource(String source) {
		this.source = EStateSource.parseEnum(source);
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
		// init: source
		if (map.containsKey("source")) {
			setSource(map.get("source").getString());
		}
	}

	/* ************
	 *	 初始化   *
	 ************ */
	
	protected APlatformState() {}

}
