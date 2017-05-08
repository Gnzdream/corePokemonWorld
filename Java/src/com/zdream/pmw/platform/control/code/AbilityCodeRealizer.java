package com.zdream.pmw.platform.control.code;

import com.zdream.pmw.platform.control.ICodeRealizer;
import com.zdream.pmw.platform.control.IPrintLevel;
import com.zdream.pmw.platform.effect.Aperitif;
import com.zdream.pmw.platform.effect.addition.AbilityLevelAdditionFormula;
import com.zdream.pmw.platform.prototype.BattlePlatform;

/**
 * 能力变化消息行动实现<br>
 * 
 * @since v0.1
 * @author Zdream
 * @date 2016年4月24日
 * @version v0.1
 */
public class AbilityCodeRealizer implements ICodeRealizer {

	private BattlePlatform pf;
	
	/* ************
	 *	实现方法  *
	 ************ */

	@Override
	public String[] codes() {
		return new String[]{CODE_ABILITY_LEVEL};
	}

	@Override
	public String commandLine(Aperitif ap, BattlePlatform pf) {
		this.pf = pf;
		String head = ap.getHead();
		if (CODE_ABILITY_LEVEL.equals(head)) {
			return abilityLevelCommandLine(ap);
		}
		return null;
	}

	@Override
	public void realize(String[] codes, BattlePlatform pf) {
		this.pf = pf;
		String code = codes[0];
		if (CODE_ABILITY_LEVEL.equals(code)) {
			abilityLevel(codes);
		}
	}
	
	/* ************
	 *	私有方法  *
	 ************ */

	/**
	 * 能力等级变化 / 设置指令
	 * @param value
	 * @return
	 */
	private String abilityLevelCommandLine(Aperitif ap) {
		StringBuilder builder = new StringBuilder(25);
		
		byte dfseat = (byte) ap.get("dfseat");
		
		builder.append("ability-level ").append(ap.get("param")).append(' ');
		builder.append(dfseat).append(' ');

		int[] items = (int[]) ap.get("items");
		int[] values = (int[]) ap.get("values");
		int length = items.length;
		for (int i = 0; i < length; i++) {
			builder.append(items[i]).append(' ').append(values[i]).append(' ');
		}

		return builder.toString();
	}
	
	/**
	 * 能力等级变化 / 设置实现
	 * @param codes
	 * @return
	 */
	private String abilityLevel(String[] codes) {
		boolean isChange;
		if (AbilityLevelAdditionFormula.PARAM_CHANGE.equals(codes[1])) {
			isChange = true;
		} else if (AbilityLevelAdditionFormula.PARAM_SET.equals(codes[1])) {
			isChange = false;
		} else {
			pf.logPrintf(IPrintLevel.PRINT_LEVEL_WARN, 
					"AbilityCR.abilityLevel(1) 无效的参数 \"%s\"", codes[1]);
			return null;
		}
		
		byte seat = Byte.valueOf(codes[2]);
		int length = (codes.length - 3) >> 1;
		for (int i = 0; i < length; i++) {
			int c = Integer.valueOf(codes[(i << 1) + 3]).intValue();
			if (c == 0) {
				continue;
			}
			int v = Integer.valueOf(codes[(i << 1) + 4]).intValue();
			
			if (isChange) {
				pf.getAttendManager().changeAbilityLevel(seat, c, v);
			} else {
				pf.getAttendManager().setAbilityLevel(seat, c, v);
			}
		}
		return null;
	}

}
