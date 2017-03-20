package com.zdream.pmw.platform.effect.damage;

import com.zdream.pmw.platform.effect.IFormula;
import com.zdream.pmw.platform.effect.SkillReleasePackage;

/**
 * 伤害公式接口<br>
 * v0.1.1 时修改了计算伤害方法的输入参数和返回参数<br>
 * <br>
 * <b>v0.2.1</b>
 * <p>继承自 IFormula 接口</p>
 * 
 * @since v0.1.1
 * @author Zdream
 * @date 2016年4月2日
 * @version v0.2.1
 */
public interface IDamageFormula extends IFormula {
	
	/**
	 * 计算伤害，并将数据写入到 package1 中
	 * @param package1
	 */
	public void damage(SkillReleasePackage package1);

}
