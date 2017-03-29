package com.zdream.pmw.platform.control.code;

import com.zdream.pmw.platform.control.ICodeRealizer;
import com.zdream.pmw.platform.control.IPrintLevel;
import com.zdream.pmw.platform.effect.Aperitif;
import com.zdream.pmw.platform.prototype.BattlePlatform;

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
		
		// TODO 判定类消息行动实现方法为空 JudgeCodeRealizer
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
	private String moveableCommandLine(Aperitif ap) {
		if ((Boolean) ap.get("result")) {
			pf.logPrintf(IPrintLevel.PRINT_LEVEL_VERBOSE, 
					"精灵 seat = %d 成功发动第 %d 个技能", 
					ap.get("seat"), ap.get("skillNum"));
			return null;
		} else {
			return String.format("judge-moveable fail %d %s", ap.get("seat"), ap.get("fail"));
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
	private String rangeCommandLine(Aperitif value) {
		String result = value.get("result").toString();
		if (result.length() == 2) {
			return String.format("%s fail %s", CODE_JUDGE_RANGE, value.get("fail"));
		} else {
			pf.logPrintf(IPrintLevel.PRINT_LEVEL_VERBOSE, 
					"精灵 seat = %d 技能的范围 %s", 
					value.get("seat"), result);
			return null;
		}
	}
	
	/* ************
	 *	 构造器   *
	 ************ */

	public JudgeCodeRealizer() {
		
	}

}
