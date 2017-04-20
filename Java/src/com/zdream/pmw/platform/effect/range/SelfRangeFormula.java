package com.zdream.pmw.platform.effect.range;

/**
 * <p>将自己作为目标的目标判定公式</p>
 * 
 * @since v0.2.2 [2017-04-18]
 * @author Zdream
 * @version v0.2.2 [2017-04-18]
 */
public class SelfRangeFormula extends SingleRangeFormula {

	@Override
	public String name() {
		return "self";
	}
	
	@Override
	protected byte[] onRange() {
		return new byte[]{pack.getAtStaff().getSeat()};
	}
	
}
