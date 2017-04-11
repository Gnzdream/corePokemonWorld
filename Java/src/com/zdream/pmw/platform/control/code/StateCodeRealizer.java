package com.zdream.pmw.platform.control.code;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.zdream.pmw.platform.control.ICodeRealizer;
import com.zdream.pmw.platform.effect.Aperitif;
import com.zdream.pmw.platform.prototype.BattlePlatform;
import com.zdream.pmw.util.json.JsonValue;
import com.zdream.pmw.util.json.JsonValue.JsonType;

/**
 * 状态相关的消息行动实现<br>
 * <p>处理状态施加、销毁等状态改变时的事件</p>
 * 
 * @since v0.2.1
 * @author Zdream
 * @date 2017年3月3日
 * @version v0.2.1
 */
public class StateCodeRealizer implements ICodeRealizer {

	@Override
	public String[] codes() {
		return new String[]{CODE_FORCE_STATE, CODE_REMOVE_STATE, CODE_STATE_SET};
	}

	@Override
	public String commandLine(Aperitif value, BattlePlatform pf) {
		String head = value.getHead();
		
		switch (head) {
		case CODE_FORCE_STATE:
			return pf.getEffectManage().forceStateCommandLine(value);
		case CODE_REMOVE_STATE:
			return removeStateCommandLine(value, pf);
		case CODE_STATE_SET:
			return stateSetCommandLine(value, pf);

		default:
			break;
		}
		return null;
	}

	@Override
	public void realize(String[] codes, BattlePlatform pf) {
		String head = codes[0];
		
		switch (head) {
		case CODE_FORCE_STATE:
			pf.getAttendManager().forceState(codes);
			break;
		case CODE_REMOVE_STATE:
			removeStateRealize(codes, pf);
			break;
		case CODE_STATE_SET:
			stateSetRealize(codes, pf);
			break;

		default:
			break;
		}
	}
	
	/* ************
	 *	消息分配  *
	 ************ */

	private String removeStateCommandLine(Aperitif value, BattlePlatform pf) {
		Map<String, JsonValue> map = value.getMap();
		
		if (map.containsKey("seat")) {
			// 是怪兽的状态
			byte seat = (Byte) map.get("seat").getValue();
			String cmd = String.format("%s %s -seat %d",
					CODE_REMOVE_STATE, map.get("state").getValue(), seat);
			return cmd;
		} else {
			
		}
		
		return "";
	}
	
	private String removeStateRealize(String[] codes, BattlePlatform pf) {
		String stateName = codes[1];
		
		if ("-seat".equals(codes[2])) {
			// 是怪兽的状态
			pf.getAttendManager().removeStateFromParticipant(Byte.parseByte(codes[3]), stateName);
		}
		
		return null;
	}

	private String stateSetCommandLine(Aperitif value, BattlePlatform pf) {
		Map<String, JsonValue> map = value.getMap();
		Set<String> sets = new HashSet<>(map.keySet());
		
		StringBuilder builder = new StringBuilder(40);
		builder.append(CODE_STATE_SET).append(' ').append(map.get("state").getString());
		
		if (sets.contains("-seat")) {
			builder.append(' ').append("-seat").append(' ').append(map.get("-seat").getValue());
			sets.remove("-seat");
		} else {
			// TODO 不是作为 ParticipantSeat
		}
		
		for (Iterator<String> it = sets.iterator(); it.hasNext();) {
			String str = it.next();
			if (str.charAt(0) == '-') {
				builder.append(' ').append(str)
				.append(' ').append(map.get(str).getValue());
			}
		}
		
		return builder.toString();
	}
	
	/**
	 * @param codes
	 * @param pf
	 */
	private void stateSetRealize(String[] codes, BattlePlatform pf) {
		String stateName = codes[1];
		if ("-seat".equals(codes[2])) {
			byte seat = Byte.parseByte(codes[3]);
			JsonValue v = stateSetPutParam(codes);
			pf.getAttendManager().setStateFromParticipant(seat, stateName, v);
		} else {
			// TODO 不是作为 ParticipantSeat
		}
	}
	
	private JsonValue stateSetPutParam(String[] codes) {
		JsonValue v = new JsonValue(JsonType.ObjectType);
		for (int i = 4; i < codes.length; i += 2) {
			v.add(codes[i], new JsonValue(codes[i + 1]));
		}
		return v;
	}
	
	/* ************
	 *	 初始化   *
	 ************ */
}
