package com.zdream.pmw.platform.control.code;

import java.util.Iterator;
import java.util.List;

import com.zdream.pmw.platform.control.ICodeRealizer;
import com.zdream.pmw.platform.effect.Aperitif;
import com.zdream.pmw.platform.prototype.BattlePlatform;
import com.zdream.pmw.util.common.ArraysUtils;
import com.zdream.pmw.util.json.JsonValue;

/**
 * 向各个控制体请求怪兽下一步操作、选择怪兽等等操作<br>
 * 
 * @since v0.2.1
 * @author Zdream
 * @date 2017年3月18日
 * @version v0.2.1
 */
public class RequestCodeRealizer implements ICodeRealizer {
	
	/* ************
	 *	实现方法  *
	 ************ */

	@Override
	public String[] codes() {
		return new String[] {CODE_REQUEST_MOVE, CODE_REQUEST_PM};
	}

	@Override
	public String commandLine(Aperitif value, BattlePlatform pf) {
		this.pf = pf;
		String code = value.getHead();
		if (CODE_REQUEST_MOVE.equals(code)) {
			return requestMoveCommandLine(value);
		} else if (CODE_REQUEST_PM.equals(code)) {
			return requestPmCommandLine(value);
		}
		return null;
	}

	@Override
	public void realize(String[] codes, BattlePlatform pf) {
		this.pf = pf;
		String code = codes[0];
		if (CODE_REQUEST_MOVE.equals(code)) {
			requestMove(codes);
		} else if (CODE_REQUEST_PM.equals(code)) {
			requestPm(codes);
		}
	}
	
	/* ************
	 *	私有方法  *
	 ************ */

	/**
	 * 请求行动指令
	 * @param value
	 * @return
	 */
	private String requestMoveCommandLine(Aperitif value) {
		return String.format("%s %d %s", CODE_REQUEST_MOVE, 
				value.get("team"),
				listToString(value.getMap().get("seats").getArray()));
	}

	/**
	 * 请求选择精灵上场的指令
	 * @param value
	 * @return
	 */
	private String requestPmCommandLine(Aperitif value) {
		StringBuilder b = new StringBuilder(40);
		b.append(CODE_REQUEST_PM).append(' ').append(value.get("team")).append(' ');
		b.append(ArraysUtils.bytesToString((byte[]) value.get("seats")));
		return b.toString();
	}
	
	/**
	 * 请求选择精灵上场的实现
	 * @param codes
	 */
	private void requestPm(String[] codes) {
		byte team = Byte.valueOf(codes[1]);
		byte[] seats = ArraysUtils.StringToBytes(codes[2]);
		pf.getControlManager().requestSwitch(team, seats);
	}

	/**
	 * 请求行动实现
	 */
	private void requestMove(String[] codes) {
		byte team = Byte.parseByte(codes[1]);
		byte[] seats = ArraysUtils.StringToBytes(codes[2]);
		pf.getControlManager().requestMove(team, seats);
	}
	
	/* ************
	 *	工具方法  *
	 ************ */
	
	/**
	 * list 转成字符串
	 * @param list
	 * @return
	 */
	private String listToString(List<JsonValue> list) {
		StringBuilder builder = new StringBuilder();
		builder.append('[');
		Iterator<JsonValue> it = list.iterator();
		if (it.hasNext()) {
			builder.append(it.next().getValue());
		}
		while (it.hasNext()) {
			builder.append(',');
			builder.append(it.next().getValue());
		}
		builder.append(']');
		return builder.toString();
	}
	
	/* ************
	 *	 初始化   *
	 ************ */

	/**
	 * 
	 */
	public RequestCodeRealizer() {}
	
	private BattlePlatform pf;

}
