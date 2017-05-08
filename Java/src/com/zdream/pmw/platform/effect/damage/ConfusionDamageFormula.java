package com.zdream.pmw.platform.effect.damage;

/**
 * 混乱状态下, 攻击自己时伤害计算公式.<br>
 * <p>判定效果:
 * <li>不判断属性加成
 * <li>不判断是否命中
 * <li>不判断是否命中要害
 * <li>不通过状态计算技能威力
 * <li>不实现附加效果和反作用</li>
 * </p>
 * 
 * <p><b>v0.2.3</b><br>
 * 开始继承自 {@code DefaultDamageFormula} 类;<br>
 * 删除大部分流程</p>
 * 
 * @since v0.2.1 [2017-03-02]
 * @author Zdream
 * @version v0.2.3 [2017-05-04]
 */
public class ConfusionDamageFormula extends DefaultDamageFormula {
	
	/* ************
	 *	实现方法  *
	 ************ */
	
	@Override
	public String name() {
		return "confusion";
	}
	
	@Override
	protected float calcDamage(int index) {
		byte level = pack.getAtStaff().getAttendant().getLevel();
		return (((2.0f * level + 10) / 250 * pack.getAt(index) * pack.getPower(index)
				/ pack.getDf(index) + 2) * pack.getCorrect(index));
	}
}
