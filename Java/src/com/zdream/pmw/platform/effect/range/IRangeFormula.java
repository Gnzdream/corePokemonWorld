package com.zdream.pmw.platform.effect.range;

import com.zdream.pmw.platform.effect.IFormula;
import com.zdream.pmw.platform.effect.SkillReleasePackage;

/**
 * <p>目标判定公式接口</p>
 * 
 * @since v0.2.2 [2017-04-18]
 * @author Zdream
 * @version v0.2.2 [2017-04-18]
 */
public interface IRangeFormula extends IFormula {
	
	/**
	 * 判定目标
	 * @param pack
	 * @return
	 *   <p>计算后目标座位列表</p>
	 *   <p>目标为全场时, 取攻击方座位即可;<br>
	 *   目标为某一阵营时, 取其中一位对应阵营的座位即可.</p>
	 */
	public byte[] range(SkillReleasePackage pack);

}
