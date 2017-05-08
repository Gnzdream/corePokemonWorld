package com.zdream.pmw.platform.control.code;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.zdream.pmw.platform.control.ICodeRealizer;
import com.zdream.pmw.platform.effect.Aperitif;
import com.zdream.pmw.platform.prototype.BattlePlatform;
import com.zdream.pmw.util.json.JsonObject;
import com.zdream.pmw.util.json.JsonValue;

/**
 * 状态相关的消息行动实现<br>
 * <p>处理状态施加、销毁等状态改变时的事件</p>
 * 
 * @since v0.2.1
 *   [2017-03-03]
 * @author Zdream
 * @version v0.2.2
 *   [2017-04-14]
 */
public class StateCodeRealizer implements ICodeRealizer {

	@Override
	public String[] codes() {
		return new String[]{CODE_FORCE_STATE, CODE_REMOVE_STATE, CODE_STATE_SET};
	}

	@Override
	public String commandLine(Aperitif ap, BattlePlatform pf) {
		String head = ap.getHead();
		
		switch (head) {
		case CODE_FORCE_STATE:
			return pf.getEffectManage().forceStateCommandLine(ap);
		case CODE_REMOVE_STATE:
			return removeStateCommandLine(ap, pf);
		case CODE_STATE_SET:
			return stateSetCommandLine(ap, pf);

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

	private String removeStateCommandLine(Aperitif ap, BattlePlatform pf) {
		if (ap.containsKey("seat")) {
			// 是怪兽或指定座位的状态
			byte seat = (byte) ap.get("seat");
			String cmd = String.format("%s %s -seat %d",
					CODE_REMOVE_STATE, ap.get("state"), seat);
			return cmd;
		} else {
			
		}
		
		return "";
	}
	
	private String removeStateRealize(String[] codes, BattlePlatform pf) {
		String stateName = codes[1];
		
		if ("-seat".equals(codes[2])) {
			byte seat = Byte.parseByte(codes[3]);
			// 是怪兽或指定座位的状态
			pf.getAttendManager().removeStateFromParticipant(seat, stateName);
			pf.getAttendManager().removeStateFromSeat(seat, stateName);
		}
		
		return null;
	}

	private String stateSetCommandLine(Aperitif ap, BattlePlatform pf) {
		Set<String> sets = new HashSet<>(ap.keySet());
		
		StringBuilder builder = new StringBuilder(40);
		builder.append(CODE_STATE_SET).append(' ').append(ap.get("state"));
		
		if (sets.contains("-seat")) {
			builder.append(' ').append("-seat").append(' ').append(ap.get("-seat"));
			sets.remove("-seat");
		} else {
			// TODO 不是作为 ParticipantSeat
		}
		
		for (Iterator<String> it = sets.iterator(); it.hasNext();) {
			String str = it.next();
			if (str.charAt(0) == '-') {
				builder.append(' ').append(str)
				.append(' ').append(ap.get(str));
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
			JsonObject v = stateSetPutParam(codes);
			pf.getAttendManager().setStateFromParticipant(seat, stateName, v);
			pf.getAttendManager().setStateFromSeat(seat, stateName, v);
		} else {
			// TODO 不是作为 ParticipantSeat
		}
	}
	
	private JsonObject stateSetPutParam(String[] codes) {
		JsonObject v = new JsonObject();
		for (int i = 4; i < codes.length; i += 2) {
			v.add(codes[i], JsonValue.createJson(codes[i + 1]));
		}
		return v;
	}
	
	/* ************
	 *	 初始化   *
	 ************ */
}
