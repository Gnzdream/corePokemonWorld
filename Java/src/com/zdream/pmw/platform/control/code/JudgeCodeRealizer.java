package com.zdream.pmw.platform.control.code;

import java.util.Map;

import com.zdream.pmw.platform.control.ICodeRealizer;
import com.zdream.pmw.platform.control.IPrintLevel;
import com.zdream.pmw.platform.effect.Aperitif;
import com.zdream.pmw.platform.prototype.BattlePlatform;
import com.zdream.pmw.util.json.JsonValue;

/**
 * 判定类消息行动实现<br>
 * 
 * @since v0.1
 * @author Zdream
 * @date 2016年4月8日
 * @version v0.2
 */
public class JudgeCodeRealizer implements ICodeRealizer {
	
	private BattlePlatform pf;
	
	/* ************
	 *	实现方法  *
	 ************ */

	@Override
	public String[] codes() {
		return new String[]{CODE_JUDGE_MOVEABLE, CODE_JUDGE_RANGE};
	}

	@Override
	public void realize(String[] codes, BattlePlatform pf) {
		this.pf = pf;
		
		// 判定类消息行动实现方法为空 TODO JudgeCodeRealizer
	}

	@Override
	public String commandLine(Aperitif value, BattlePlatform pf) {
		this.pf = pf;
		String code = value.getHead();
		if (CODE_JUDGE_MOVEABLE.equals(code)) {
			return moveableCommandLine(value);
		} else if (CODE_JUDGE_RANGE.equals(code)) {
			return rangeCommandLine(value);
		}
		return null;
	}

	/**
	 * 在判定怪兽能否行动时返回支持传输的指令代码<br>
	 * 一般情况下，如果能够成功行动，不会打印文字，也就是返回 null<br>
	 * 如果不能够成功行动，就会在 fault 中找到响应的指令代码，解析并且返回即可<br>
	 * <br>
	 * 解析的方法暂时没有实现<br>
	 * @param value
	 * @return
	 */
	private String moveableCommandLine(JsonValue value) {
		Map<String, JsonValue> map = value.getMap();
		if ((Boolean) map.get("result").getValue()) {
			pf.logPrintf(IPrintLevel.PRINT_LEVEL_VERBOSE, 
					"精灵 seat = %d 成功发动第 %d 个技能", 
					map.get("seat").getValue(), map.get("skillNum").getValue());
			return null;
		} else {
			return String.format("judge-moveable fault %s", map.get("fault").getString());
		}
	}
	
	/**
	 * 在判定怪兽的技能释放的范围时返回支持传输的指令代码<br>
	 * 只有在返回数据中没有包括任何范围时，即数据为"[]"时，<br>
	 * 就是行动失败，返回行动失败的响应指令代码<br>
	 * 否则返回数据为空<br>
	 * @param value
	 * @return
	 */
	private String rangeCommandLine(JsonValue value) {
		Map<String, JsonValue> map = value.getMap();
		String result = map.get("result").getString();
		if (result.length() == 2) {
			JsonValue v = map.get("fault");
			return (v == null) ? "但是它失败了" : map.get("fault").getString();
		} else {
			pf.logPrintf(IPrintLevel.PRINT_LEVEL_VERBOSE, 
					"精灵 seat = %d 技能的范围 %s", 
					map.get("seat").getValue(), result);
			return null;
		}
	}
	
	/* ************
	 *	 构造器   *
	 ************ */

	public JudgeCodeRealizer() {
		
	}

}
