package com.zdream.pmw.platform.effect.moveable;

import com.zdream.pmw.platform.effect.EffectManage;
import com.zdream.pmw.platform.effect.IFormula;
import com.zdream.pmw.platform.effect.SkillReleasePackage;

/**
 * 能否行动的判定接口<br>
 * <br>
 * <b>v0.2.1</b>
 * <p>继承自 IFormula 接口</p>
 * 
 * <p><b>v0.2.3</b><br>
 * canMove() 方法增加 em 参数;<br>
 * 添加 reasonOf 方法, 用于查询无法行动原因</p>
 * 
 * @since v0.1 [2016-04-08]
 * @author Zdream
 * @version v0.2.3 [2017-05-04]
 */
public interface IMoveableFormula extends IFormula {
	
	/**
	 * 能否行动判定
	 * @param p
	 *   单次技能释放时的必要数据包
	 * @param em
	 *   环境
	 * @return
	 */
	public boolean canMove(SkillReleasePackage p, EffectManage em);
	
	/**
	 * 当判断无法行动时, 返回无法行动的原因
	 * @return
	 * @since v0.2.3
	 */
	default public String reasonOf() {return null;}
	
}
