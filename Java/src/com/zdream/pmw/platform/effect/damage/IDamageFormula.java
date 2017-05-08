package com.zdream.pmw.platform.effect.damage;

import com.zdream.pmw.platform.effect.EffectManage;
import com.zdream.pmw.platform.effect.IFormula;
import com.zdream.pmw.platform.effect.SkillReleasePackage;

/**
 * 伤害公式接口<br>
 * v0.1.1 时修改了计算伤害方法的输入参数和返回参数<br>
 * <br>
 * <b>v0.2.1</b>
 * <p>继承自 IFormula 接口</p>
 * 
 * <p><b>v0.2.3</b><br>
 * 伤害公式不再控制计算的流程, 而是对已有数据进行计算, 返回伤害数据
 * </p>
 * 
 * @since v0.1.1 [2016-04-02]
 * @author Zdream
 * @version v0.2.3 [2017-04-28]
 */
public interface IDamageFormula extends IFormula {
	
	/**
	 * 计算伤害
	 * @param pack
	 * @param em
	 *   环境
	 * @return
	 *   伤害值数组, 数组每一项跟随 {@code pack.getTargets()} 所指的目标
	 */
	public int[] damage(SkillReleasePackage pack, EffectManage em);

}
