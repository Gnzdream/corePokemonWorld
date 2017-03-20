package com.zdream.pmw.platform.effect.addition;

import com.zdream.pmw.platform.effect.IFormula;
import com.zdream.pmw.platform.effect.SkillReleasePackage;

/**
 * 附加状态 / A 类附加效果判定接口<br>
 * <br>
 * <b>v0.2.1</b>
 * <p><li>停止使用 int 类型标识, 转而使用 String 类型.
 * <li>抽出父接口 {@link IFormula}</li></p>
 * 
 * @since v0.1
 * @author Zdream
 * @date 2016年4月2日
 * @version v0.2.1
 */
public interface IAdditionFormula extends IFormula {
	
	/**
	 * 附加状态判定
	 * @param pack
	 */
	public void addition(SkillReleasePackage pack);
	
}
