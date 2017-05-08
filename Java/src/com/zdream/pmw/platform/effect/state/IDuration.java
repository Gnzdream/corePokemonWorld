package com.zdream.pmw.platform.effect.state;

import com.zdream.pmw.platform.attend.IState;
import com.zdream.pmw.platform.prototype.BattlePlatform;
import com.zdream.pmw.util.json.JsonObject;
import com.zdream.pmw.util.json.JsonValue;

/**
 * 含持续时间的状态<br>
 * <p>该类状态会存在一定的时间, 在一定回合后删除该状态</p>
 * 
 * @since v0.2.1
 * @author Zdream
 * @date 2017年3月18日
 * @version v0.2.1
 */
public interface IDuration extends IState {
	
	/**
	 * 该类状态一般每回合扣掉一点回合计数器<br>
	 */
	public static final String KEY_REDUCE_ROUND = "-reduce";
	
	/**
	 * @return
	 *   状态剩余回合数
	 */
	public int getRound();
	
	/**
	 * 回合数--
	 */
	public void reduceRound(int num);
	
	@Override
	default void set(JsonObject v, BattlePlatform pf) {
		JsonValue o = v.asMap().get(KEY_REDUCE_ROUND);
		if (o != null) {
			int value = Integer.valueOf(o.getValue().toString());
			reduceRound(value);
		}
	}

}
