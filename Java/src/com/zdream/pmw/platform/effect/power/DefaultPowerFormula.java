package com.zdream.pmw.platform.effect.power;

import java.util.Arrays;

import com.zdream.pmw.platform.effect.EffectManage;
import com.zdream.pmw.platform.effect.SkillReleasePackage;

/**
 * 默认技能威力计算<br>
 * 计算步骤：<br>
 * 直接获取并返回技能原始威力<br>
 * 
 * @since v0.1 [2016-04-10]
 * @author Zdream
 * @version v0.2.3 [2017-05-04]
 */
public class DefaultPowerFormula implements IPowerFormula {

	@Override
	public String name() {
		return "default";
	}

	@Override
	public int[] power(SkillReleasePackage pack, EffectManage em) {
		final int len = pack.targetsLength();
		int[] result = new int[len];
		Arrays.fill(result, pack.getSkill().getSkill().getPower());
		return result;
	}
	
	@Override
	public String toString() {
		return name();
	}
	
}
