package com.zdream.pmw.platform.control.code;

import com.zdream.pmw.platform.control.ICodeRealizer;
import com.zdream.pmw.platform.effect.Aperitif;
import com.zdream.pmw.platform.prototype.BattlePlatform;

/**
 * 除状态变化、能力等级变化外的所有技能附加效果判断和结算实现<br>
 * <p>现在支持:
 * <li>吸血、反伤类技能(含回复技)
 * </li></p>
 * @since v0.2.1
 * @author Zdream
 * @date 2017年3月19日
 * @version v0.2.1
 */
public class AdditionCodeRealizer implements ICodeRealizer {
	
	/* ************
	 *	实现方法  *
	 ************ */

	@Override
	public String[] codes() {
		return new String[]{CODE_ADDITION_SETTLE};
	}

	@Override
	public String commandLine(Aperitif value, BattlePlatform pf) {
		this.pf = pf;
		String code = value.getHead();
		if (CODE_ADDITION_SETTLE.equals(code)) {
			String category = value.get("category").toString();
			switch (category) {
			case "absorb": // 吸血和回复技
			case "reaction":
				return absorbCommandLine(value);

			default:
				break;
			}
		}
		return null;
	}

	@Override
	public void realize(String[] codes, BattlePlatform pf) {
		this.pf = pf;

	}
	
	/* ************
	 * 转成命令行 *
	 ************ */

	private String absorbCommandLine(Aperitif ap) {
		Object o = ap.get("abs");
		if (o != null) {
			int abs = (int) o;
			if (abs == 2) { // 绝对失败
				// TODO 加血或吸血、反伤失败的理由, 转成命令行 return
				return null;
			}
		}
		
		float value = (float) ap.get("value");
		if (value == 0) {
			return null;
		}
		
		byte seat = (((int) ap.get("side")) == 1) ?
				(byte) ap.get("atseat") : (byte) ap.get("dfseat");
		boolean actualRecover = (value > 0);
		value = Math.abs(value); // value is positive now
		if (value < 1) {
			value = 1;
		}
		boolean expectedAbsorb = "absorb".equals(ap.get("category"));
		
		if (expectedAbsorb) {
			if (actualRecover) {
				return String.format("%s %d %d %s", CODE_RECOVER, seat, (int)value, "absorb");
			} else {
				String reason = ap.get("reason").toString();
				return String.format("%s %d %d %s&%s",
						CODE_NONMOVES_DAMAGE, seat, (int)value, "absorb", reason);
			}
		} else {
			if (!actualRecover) {
				return String.format("%s %d %d %s", CODE_NONMOVES_DAMAGE, seat, (int)value, "reaction");
			} else {
				String reason = ap.get("reason").toString();
				return String.format("%s %d %d %s&%s",
						CODE_NONMOVES_DAMAGE, seat, (int)value, "reaction", reason);
			}
		}
	}
	
	/* ************
	 *	 初始化   *
	 ************ */
	
	@SuppressWarnings("unused")
	private BattlePlatform pf;

	public AdditionCodeRealizer() {}

}
