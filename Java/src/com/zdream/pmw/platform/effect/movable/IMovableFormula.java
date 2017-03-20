package com.zdream.pmw.platform.effect.movable;

import com.zdream.pmw.platform.effect.IFormula;
import com.zdream.pmw.platform.effect.SkillReleasePackage;

/**
 * 能否行动的判定接口<br>
 * <br>
 * <b>v0.2.1</b>
 * <p>继承自 IFormula 接口</p>
 * 
 * @since v0.1
 * @author Zdream
 * @date 2016年4月8日
 * @version v0.2.1
 */
public interface IMovableFormula extends IFormula {
	
	/**
	 * 能否行动判定
	 * @param p
	 *   单次技能释放时的必要数据包
	 * @return
	 */
	public boolean canMove(SkillReleasePackage p);
	
}
