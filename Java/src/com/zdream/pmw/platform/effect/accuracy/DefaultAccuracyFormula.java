package com.zdream.pmw.platform.effect.accuracy;

import com.zdream.pmw.platform.effect.SkillReleasePackage;

/**
 * 默认技能命中率计算<br>
 * 计算步骤：<br>
 * 直接获取并返回技能原始命中率<br>
 * 
 * @since v0.1
 * @author Zdream
 * @date 2016年4月9日
 * @version v0.1
 */
public class DefaultAccuracyFormula implements IAccuracyFormula {

	@Override
	public String name() {
		return "default";
	}

	@Override
	public int accuracy(SkillReleasePackage package1) {
		return package1.getSkill().getSkill().getAccuracy();
	}
	
	/* ************
	 *	 构造器   *
	 ************ */

	public DefaultAccuracyFormula() {
		
	}

}
