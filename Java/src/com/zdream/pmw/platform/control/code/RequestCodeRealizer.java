package com.zdream.pmw.platform.control.code;

import com.zdream.pmw.platform.control.ICodeRealizer;
import com.zdream.pmw.platform.effect.Aperitif;
import com.zdream.pmw.platform.prototype.BattlePlatform;
import com.zdream.pmw.util.common.ArraysUtils;

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
		return new String[] {CODE_REQUEST_MOVE, CODE_REQUEST_PM, CODE_REQUEST_COMMIT};
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
		// CODE_REQUEST_COMMIT: nothing.
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
	 * @param ap
	 * @return
	 */
	private String requestMoveCommandLine(Aperitif ap) {
		StringBuilder b = new StringBuilder(20);
		
		b.append(CODE_REQUEST_MOVE).append(' ').append(ap.get("team"))
				.append(' ').append(ArraysUtils.bytesToString(
						(byte[]) ap.get("seats")));
		
		Object o;
		boolean bo = false;
		// v0.2.3 白名单
		if ((o = ap.get("option")) != null) {
			byte[] seats = (byte[]) o;
			if (seats.length > 0) {
				bo = true;
				int[] options = (int[]) ap.get("option_skill");
				
				b.append(' ').append("-option");
				for (int i = 0; i < seats.length; i++) {
					b.append(' ').append(seats[i]).append(' ').append(options[i]);
				}
			}
		}

		// v0.2.3 黑名单
		if (!bo && (o = ap.get("limits")) != null) {
			byte[] seats = (byte[]) o;
			if (seats.length > 0) {
				bo = true;
				int[][] limits = (int[][]) ap.get("limit_skills");
				
				b.append(' ').append("-limit");
				for (int i = 0; i < seats.length; i++) {
					b.append(' ').append(seats[i]).append(' ');
					int[] ls = limits[i];
					
					if (ls.length == 1) {
						b.append(ls[0]);
					} else {
						b.append(ArraysUtils.intsToString(ls));
					}
				}
			}
		}
		
		return b.toString();
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
		pf.getControlManager().putSwitchRequest(team, seats);
	}

	/**
	 * 请求行动实现
	 */
	private void requestMove(String[] codes) {
		byte team = Byte.parseByte(codes[1]);
		byte[] seats = ArraysUtils.StringToBytes(codes[2]);
		pf.getControlManager().putMoveRequest(team, seats);
		
		// v0.2.3 黑、白名单
		if (codes.length > 3) {
			int idx = 4;
			final int len = codes.length;
			// 因为黑名单与白名单不能同时存在
			final boolean isLimit = "-limit".equals(codes[3]);
			
			for (; idx < len; idx += 2) {
				byte seat = Byte.parseByte(codes[idx]);
				if (codes[idx + 1].startsWith("[")) {
					// 数组
					int[] is = ArraysUtils.StringToInts(codes[idx + 1]);
					for (int i = 0; i < is.length; i++) {
						if (isLimit) {
							pf.getControlManager().putMoveLimit(team, seat, is[i]);
						} else {
							pf.getControlManager().putMoveOption(team, seat, is[i]);
						}
					}
				} else {
					// 数值
					int skillNum = Integer.parseInt(codes[idx + 1]);
					if (isLimit) {
						pf.getControlManager().putMoveLimit(team, seat, skillNum);
					} else {
						pf.getControlManager().putMoveOption(team, seat, skillNum);
					}
				}
			}
		}
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
