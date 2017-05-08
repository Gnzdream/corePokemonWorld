package com.zdream.pmw.platform.effect.range;

import com.zdream.pmw.platform.effect.EffectManage;
import com.zdream.pmw.platform.effect.IFormula;
import com.zdream.pmw.platform.effect.SkillReleasePackage;

/**
 * <p>目标判定公式接口</p>
 * <p>目标在版本 v0.2.3 分成了两个大类,
 * 一类以 {@link SingleRangeFormula} 为基类的基础选择攻击范围的目标判定公式,
 * 一类以 {@link TargetFormula} 为基类的过滤目标的判定公式.
 * <p>
 * <li>{@link SingleRangeFormula} 及子类:
 * 根据 {@link com.zdream.pmw.platform.attend.ESkillRange} 所描述的技能范围,
 * 计算出技能范围的位置; 它在一次技能释放的过程中只计算一次;
 * <li>{@link TargetFormula} 及子类:
 * 通过一定的公式, 将 {@link SingleRangeFormula} 计算的范围进行修改和过滤,
 * 得出这次技能或者效果最终追加的对象范围; 它在一次技能释放的过程中可计算多次;
 * </li></p>
 * 
 * <p><b>v0.2.3</b><br>
 * 增加了 {@link TargetFormula} 及子类的分支.
 * </p>
 * 
 * @since v0.2.2 [2017-04-18]
 * @author Zdream
 * @version v0.2.3 [2017-05-04]
 */
public interface IRangeFormula extends IFormula {
	
	/**
	 * 判定目标
	 * @param pack
	 * @param em
	 *   环境
	 * @return
	 *   <p>计算后目标座位列表</p>
	 *   <p>目标为全场时, 取攻击方座位即可;<br>
	 *   目标为某一阵营时, 取其中一位对应阵营的座位即可.</p>
	 */
	public byte[] range(SkillReleasePackage pack, EffectManage em);

}
