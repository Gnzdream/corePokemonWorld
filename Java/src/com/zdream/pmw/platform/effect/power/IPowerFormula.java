package com.zdream.pmw.platform.effect.power;

import com.zdream.pmw.platform.effect.EffectManage;
import com.zdream.pmw.platform.effect.IFormula;
import com.zdream.pmw.platform.effect.SkillReleasePackage;

/**
 * 威力公式接口<br>
 * v0.1.1 修改了计算命中率方法的输入参数<br>
 * <br>
 * <b>v0.2.1</b>
 * <p>继承自 IFormula 接口</p>
 * <br>
 * <p><b>v0.2.3</b>
 * power() 方法增加 em 参数</p>
 * 
 * @since v0.1.1 [2016-04-02]
 * @author Zdream
 * @version v0.2.3 [2017-05-04]
 */
public interface IPowerFormula extends IFormula {
	
	/**
	 * 计算威力
	 * @param pack
	 * @param em
	 *   环境
	 * @return
	 */
	public int[] power(SkillReleasePackage pack, EffectManage em);
	
}
