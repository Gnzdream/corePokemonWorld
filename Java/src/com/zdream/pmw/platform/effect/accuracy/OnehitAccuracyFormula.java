package com.zdream.pmw.platform.effect.accuracy;

import com.zdream.pmw.platform.effect.SkillReleasePackage;

/**
 * <p>一击必杀技的命中计算公式</p>
 * <p><code>命中率 = [(攻击方的等级 - 防御方的等级) + 30]%</code></p>
 * 
 * @since v0.2.2
 *   [2017-04-11]
 * @author Zdream
 * @version v0.2.2
 *   [2017-04-11]
 */
public class OnehitAccuracyFormula implements IAccuracyFormula {

	@Override
	public String name() {
		return "onehit";
	}

	@Override
	public int accuracy(SkillReleasePackage pack) {
		int a = pack.getAtStaff().getAttendant().getLevel() -
				pack.getDfStaff(0).getAttendant().getLevel(); // TODO pack.getThiz() -> 0
		if (a < 0) {
			return 0;
		} else {
			return (a > 70) ? 100 : a + 30;
		}
	}
	
	@Override
	public String toString() {
		return name();
	}

}
