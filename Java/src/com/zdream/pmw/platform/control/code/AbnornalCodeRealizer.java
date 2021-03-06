package com.zdream.pmw.platform.control.code;

import com.zdream.pmw.core.tools.AbnormalMethods;
import com.zdream.pmw.monster.prototype.EPokemonAbnormal;
import com.zdream.pmw.platform.control.ICodeRealizer;
import com.zdream.pmw.platform.effect.Aperitif;
import com.zdream.pmw.platform.prototype.BattlePlatform;

/**
 * 异常状态消息行动实现<br>
 * 
 * @since v0.1
 * @author Zdream
 * @date 2016年4月24日
 * @version v0.2
 */
public class AbnornalCodeRealizer implements ICodeRealizer {
	
	private BattlePlatform pf;
	
	/* ************
	 *	实现方法  *
	 ************ */

	@Override
	public String[] codes() {
		return new String[]{CODE_FORCE_ABNORMAL};
	}

	@Override
	public String commandLine(Aperitif ap, BattlePlatform pf) {
		this.pf = pf;
		String head = ap.getHead();
		if (CODE_FORCE_ABNORMAL.equals(head)) {
			return forceAbnormalCommandLine(ap);
		}
		return null;
	}

	@Override
	public void realize(String[] codes, BattlePlatform pf) {
		this.pf = pf;
		String head = codes[0];
		if (CODE_FORCE_ABNORMAL.equals(head)) {
			forceAbnormal(codes);
		}
	}
	
	/* ************
	 *	私有方法  *
	 ************ */
	/**
	 * 施加异常状态的消息命令行
	 * @param value
	 * @return
	 */
	private String forceAbnormalCommandLine(Aperitif ap) {
		StringBuilder builder = new StringBuilder(25);
		builder.append("force-abnormal ").append(ap.get("dfseat")).append(' ')
				.append(ap.get("abnormal"));
		
		Object paramv = ap.get("param");
		if (paramv != null) {
			builder.append(' ').append(paramv);
		}
		
		return builder.toString();
	}
	
	/**
	 * 施加异常状态的最终实现
	 * @param codes
	 * @return
	 */
	private String forceAbnormal(String[] codes) {
		EPokemonAbnormal abnormal = AbnormalMethods.toAbnormal(Byte.parseByte(codes[2]));
		pf.getAttendManager().forceAbnormal(Byte.parseByte(codes[1]), Byte.parseByte(codes[2]));
		return String.format("中了 %s 异常状态", abnormal.name());
	}

}
