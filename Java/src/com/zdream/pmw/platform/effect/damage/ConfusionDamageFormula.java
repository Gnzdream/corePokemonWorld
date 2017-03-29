package com.zdream.pmw.platform.effect.damage;

import com.zdream.pmw.monster.prototype.EPokemonType;
import com.zdream.pmw.monster.prototype.IPokemonDataType;
import com.zdream.pmw.platform.control.IPrintLevel;
import com.zdream.pmw.platform.effect.Aperitif;
import com.zdream.pmw.platform.effect.SkillReleasePackage;
import com.zdream.pmw.util.random.RanValue;

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
 * @since v0.2.1
 * @author Zdream
 * @date 2017年3月2日
 * @version v0.2.1
 */
public class ConfusionDamageFormula implements IDamageFormula {
	
	/* ************
	 *	  属性    *
	 ************ */
	
	SkillReleasePackage pack;
	
	/* ************
	 *	实现方法  *
	 ************ */
	
	@Override
	public String name() {
		return "confusion";
	}

	@Override
	public void damage(SkillReleasePackage pack) {
		this.pack = pack;
		pack.setType(EPokemonType.None);
		
		pack.setThiz(0);
		pack.setHitable(true, 0);
		pack.setRate(1.0f, 0);
		pack.setCtable(false, 0);
		
		writeAt();
		pack.setPower(pack.getSkill().getSkill().getPower(), 0);
		writeDf();
		writeCorrect();
		
		writeDamage();
		executeDamage();
	}
	
	/* ************
	 *	私有方法  *
	 ************ */

	/**
	 * 计算物理攻击力并写入释放数据包<br>
	 */
	protected void writeAt() {
		byte atseat = pack.getAtStaff().getSeat();
		
		int at = pack.getEffects().calcAbility(atseat, IPokemonDataType.AT, 0, false);
		pack.setAt(at, 0);
		pack.getEffects().logPrintf(IPrintLevel.PRINT_LEVEL_VERBOSE,
				"ConfusionDamageFormula.writeAt(): seat=%d 的攻击力 %d",
				atseat, at);
	}
	
	/**
	 * 计算防御力（可能是物防或特防）并写入释放数据包<br>
	 */
	protected void writeDf() {
		byte dfseat = pack.getDfStaff(0).getSeat();
		
		int df = pack.getEffects().calcAbility(dfseat, IPokemonDataType.DF, 0, false);
		pack.setDf(df, 0);
		pack.getEffects().logPrintf(IPrintLevel.PRINT_LEVEL_VERBOSE,
				"ConfusionDamageFormula.writeAt(): seat=%d 的防御力 %d",
				dfseat, df);
	}
	
	/**
	 * 计算最后的伤害修正并写入释放数据包<br>
	 */
	protected void writeCorrect() {
		float correct = (RanValue.random(0, 16) + 85) / 100.0f;
		pack.setCorrect(correct, 0);
	}
	
	/**
	 * 计算最后伤害并写入释放数据包<br>
	 * 该方法的结果是释放数据包中的伤害被写入<br>
	 */
	protected void writeDamage() {
		byte level = pack.getAtStaff().getAttendant().getLevel();
		int damage = (int) (((2.0f * level + 10) / 250 * pack.getAt(0) * pack.getPower(0)
				/ pack.getDf(0) + 2) * pack.getCorrect(0) * pack.getRate(0));

		// 先放入 package, 让 box 进行再加工. 这一步不能缺
		pack.setDamage(damage, 0);
		damage = pack.getEffects().calcDamage(pack);
		
		pack.getEffects().logPrintf(IPrintLevel.PRINT_LEVEL_VERBOSE,
				"DefaultDamageFormula.writeAt(1): seat=%d 的技能伤害为 %d",
				pack.getAtStaff().getSeat(), damage);
		pack.setDamage(damage, 0);
	}
	
	/**
	 * 最终的伤害实现
	 */
	protected void executeDamage() {
		byte seat = pack.getDfStaff(0).getSeat(); // 防御方
		
		Aperitif value = pack.getEffects().newAperitif(Aperitif.CODE_SKILL_DAMAGE, seat);
		value.append("seat", seat).append("value", pack.getDamage(0))
				.append("ctable", false).append("effect", 0).append("reason", "confusion");
		pack.getEffects().startCode(value);
	}
	
	/* ************
	 *	 构造器   *
	 ************ */

	public ConfusionDamageFormula() {}

}
