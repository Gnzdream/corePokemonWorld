package com.zdream.pmw.platform.effect.power;

import com.zdream.pmw.platform.effect.SkillReleasePackage;

/**
 * 默认技能威力计算<br>
 * 计算步骤：<br>
 * 直接获取并返回技能原始威力<br>
 * 
 * @since v0.1
 * @author Zdream
 * @date 2016年4月10日
 * @version v0.1
 */
public class DefaultPowerFormula implements IPowerFormula {

	@Override
	public String name() {
		return "default";
	}

	@Override
	public int power(SkillReleasePackage package1) {
		return package1.getSkill().getSkill().getPower();
	}
	
}
