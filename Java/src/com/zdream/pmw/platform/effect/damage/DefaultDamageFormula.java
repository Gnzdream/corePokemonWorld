package com.zdream.pmw.platform.effect.damage;

import com.zdream.pmw.platform.effect.EffectManage;
import com.zdream.pmw.platform.effect.SkillReleasePackage;

/**
 * 默认伤害计算公式<br>
 * 计算步骤：<br>
 * 1. 判断是否命中<br>
 * <br>
 * <b>v0.2</b><br>
 *   取消了对 pf.instance 的直接引用<br>
 * 
 * <p><b>v0.2.3</b><br>
 * 对 effect 部分的大部分类的职能进行重新划分,
 * 将所有 <code>IDamageFormula</code> 公式的计算流程大幅度简化,
 * 从此公式中将不包含流程相关的方法.
 * 它的职能只限于从原始数据中初步计算出伤害.</p>
 * 
 * @since v0.1 [2016-04-09]
 * @author Zdream
 * @version v0.2.3 [2017-05-04]
 */
public class DefaultDamageFormula implements IDamageFormula {
	
	@Override
	public String name() {
		return "default";
	}

	@Override
	public final int[] damage(SkillReleasePackage pack, EffectManage em) {
		this.pack = pack;
		this.em = em;
		
		byte[] targets = pack.getTargets();
		int[] damages = new int[targets.length];
		
		for (int i = 0; i < targets.length; i++) {
			float damagef = this.calcDamage(i);
			int damage;
			
			if (damagef == 0) {
				damage = 0;
			} else if (damagef < 1) {
				damage = 1;
			} else {
				damage = (int) damagef;
			}
			
			damages[i] = damage;
		}
		
		this.pack = null;
		this.em = null;
		
		return damages;
	}
	
	/**
	 * 基于前面计算的所有数据, 得出最后的伤害值.
	 * @return
	 * @since v0.2.3
	 */
	protected float calcDamage(int index) {
		byte level = pack.getAtStaff().getAttendant().getLevel();
		return (((2.0f * level + 10) / 250 * pack.getAt(index) * pack.getPower(index)
				/ pack.getDf(index) + 2) * pack.getCorrect(index) * pack.getRate(index));
	}
	
	/**
	 * 释放数据包
	 */
	protected SkillReleasePackage pack;
	protected EffectManage em;
	
	@Override
	public String toString() {
		return name();
	}
}
