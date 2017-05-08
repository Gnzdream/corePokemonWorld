package com.zdream.pmw.platform.effect.instruction;

import com.zdream.pmw.platform.effect.AInstruction;

/**
 * <p>判断技能是否发动失败的指示
 * <p>一般只有部分变化技能才有的判断
 * </p>
 * 
 * @since v0.2.3 [2017-04-27]
 * @author Zdream
 * @version v0.2.3 [2017-04-27]
 */
public class FailInstruction extends AInstruction {

	@Override
	public String name() {
		return "fail";
	}

	@Override
	protected void execute() {
		// TODO Auto-generated method stub

	}

}
