package com.zdream.pmw.platform.effect.range;

import com.zdream.pmw.platform.attend.AttendManager;

/**
 * <p>将全场场地作为目标的目标判定公式</p>
 * 
 * @since v0.2.2 [2017-04-21]
 * @author Zdream
 * @version v0.2.2 [2017-04-21]
 */
public class PlatformRangeFormula extends SingleRangeFormula {

	@Override
	public String name() {
		return "platform";
	}
	
	/**
	 * <p>判定全场目标</p>
	 * <p>流程:
	 * <li>忽略玩家的选择, 选定所有存在怪兽的座位</li></p>
	 */
	protected byte[] onRange() {
		AttendManager am = pack.getAttends();
		return am.existsSeat();
	}

}
