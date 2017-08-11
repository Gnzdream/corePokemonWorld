package com.zdream.pmw.platform.order.recordchecker;

import java.util.function.Predicate;

import com.zdream.pmw.platform.effect.SkillReleasePackage;

/**
 * 技能释放数据的一般查询方法<br>
 * 可以利用技能实际目标、是否造成伤害、技能的参数 (类型、属性)
 * 来搜索最近满足条件的技能释放数据<br>
 * @since v0.2.3 [2017-08-10]
 * @author Zdream
 * @version v0.2.3 [2017-08-10]
 */
public class CommonRecordSearcher implements Predicate<SkillReleasePackage> {
	
	byte target;
	
	/**
	 * 设置条件, 攻击的实际目标是哪个精灵
	 * @param no
	 */
	public void setTarget(byte no) {
		this.target = no;
	}

	@Override
	public boolean test(SkillReleasePackage t) {
		// TODO Auto-generated method stub
		return false;
	}

}
