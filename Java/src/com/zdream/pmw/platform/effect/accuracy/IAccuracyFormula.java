package com.zdream.pmw.platform.effect.accuracy;

import com.zdream.pmw.platform.effect.IFormula;
import com.zdream.pmw.platform.effect.SkillReleasePackage;

/**
 * 命中公式接口<br>
 * v0.1.1 修改了计算命中率方法的输入参数<br>
 * <br>
 * <b>v0.2.1</b>
 * <p>继承自 IFormula 接口</p>
 * 
 * @since v0.1.1
 * @author Zdream
 * @date 2016年4月2日
 * @version v0.2.1
 */
public interface IAccuracyFormula extends IFormula {
	
	/**
	 * 计算命中率
	 * @param package1
	 * @return
	 */
	public int accuracy(SkillReleasePackage package1);

}
