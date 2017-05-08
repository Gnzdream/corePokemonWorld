package com.zdream.pmw.platform.effect.range;

import com.zdream.pmw.platform.effect.SkillReleasePackage;

/**
 * <p>最终目标为自己的锁定指示
 * <p>判断如果有攻击, 并且命中且对某防御方攻击有效, 或者该技能不属于攻击技能,
 * 将目标锁定为自己.
 * </p>
 * 
 * @since v0.2.3 [2017-05-05]
 * @author Zdream
 * @version v0.2.3 [2017-05-05]
 */
public class SelfTargetFormula extends TargetFormula {
	
	@Override
	protected byte[] targets() {
		byte[] ranges = rangeTargets();
		// 存在一个防御方, 对它攻击命中且有效
		boolean isHited = false;
		
		Object ohits = getData(SkillReleasePackage.DATA_KEY_HITS);
		Object oimmuses = getData(SkillReleasePackage.DATA_KEY_IMMUSES);
		boolean[] hits = null;
		boolean[] immuses = null;
		if (ohits != null) {
			hits = (boolean[]) ohits;
		}
		if (oimmuses != null) {
			immuses = (boolean[]) oimmuses;
		}
		
		for (int i = 0; i < ranges.length; i++) {
			boolean b = true;
			
			if (hits != null) {
				b &= hits[i];
			}
			if (immuses != null) {
				b &= !immuses[i];
			}
			
			if (b) {
				isHited = true;
				break;
			}
		}
		
		if (isHited) {
			return new byte[]{pack.getAtStaff().getSeat()};
		} else {
			return EMPTY_SEATS;
		}
	}
	
	@Override
	public String name() {
		return "self";
	}

}
