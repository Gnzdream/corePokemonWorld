package com.zdream.pmw.platform.effect.addition;

import com.zdream.pmw.platform.effect.EffectManage;
import com.zdream.pmw.platform.effect.IFormula;
import com.zdream.pmw.platform.effect.SkillReleasePackage;

/**
 * 附加状态 / 附加效果判定接口<br>
 * <br>
 * <b>v0.2.1</b>
 * <p><li>停止使用 int 类型标识, 转而使用 String 类型.
 * <li>抽出父接口 {@link IFormula}</li></p>
 * 
 * <p><b>v0.2.3</b><br>
 * {@code addition()} 方法增加 em 参数</p>
 * 
 * @since v0.1 [2016-04-02]
 * @author Zdream
 * @version v0.2.3 [2017-05-04]
 */
@Deprecated
public interface IAdditionFormula extends IFormula {
	
	/**
	 * 附加状态判定
	 * @param pack
	 * @param em
	 *   环境
	 */
	public void addition(SkillReleasePackage pack, EffectManage em);
	
}
