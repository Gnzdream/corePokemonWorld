package com.zdream.pmw.platform.control.code;

import java.util.Map;

import com.zdream.pmw.monster.prototype.EPokemonAbnormal;
import com.zdream.pmw.platform.common.AbnormalMethods;
import com.zdream.pmw.platform.control.ICodeRealizer;
import com.zdream.pmw.platform.effect.Aperitif;
import com.zdream.pmw.platform.prototype.BattlePlatform;
import com.zdream.pmw.util.json.JsonValue;

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
	public String commandLine(Aperitif value, BattlePlatform pf) {
		this.pf = pf;
		String head = value.getHead();
		if (CODE_FORCE_ABNORMAL.equals(head)) {
			return forceAbnormalCommandLine(value);
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
	private String forceAbnormalCommandLine(JsonValue value) {
		Map<String, JsonValue> map = value.getMap();
		StringBuilder builder = new StringBuilder(25);
		builder.append("force-abnormal ").append(map.get("dfseat").getValue()).append(' ')
				.append(map.get("abnormal").getValue());
		
		JsonValue paramv = map.get("param");
		if (paramv != null) {
			builder.append(' ').append(paramv.getString());
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
