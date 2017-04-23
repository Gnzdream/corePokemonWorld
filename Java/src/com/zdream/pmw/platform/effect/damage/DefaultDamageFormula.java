package com.zdream.pmw.platform.effect.damage;

import com.zdream.pmw.monster.prototype.EPokemonType;
import com.zdream.pmw.monster.prototype.IPokemonDataType;
import com.zdream.pmw.monster.skill.ESkillCategory;
import com.zdream.pmw.platform.attend.AttendManager;
import com.zdream.pmw.platform.control.IPrintLevel;
import com.zdream.pmw.platform.effect.Aperitif;
import com.zdream.pmw.platform.effect.EffectManage;
import com.zdream.pmw.platform.effect.SkillReleasePackage;
import com.zdream.pmw.platform.effect.addition.IAdditionFormula;
import com.zdream.pmw.util.json.JsonValue;
import com.zdream.pmw.util.json.JsonValue.JsonType;
import com.zdream.pmw.util.random.RanValue;

/**
 * 默认伤害计算公式<br>
 * 计算步骤：<br>
 * 1. 判断是否命中<br>
 * <br>
 * <b>v0.2</b><br>
 *   取消了对 pf.instance 的直接引用<br>
 * 
 * @since v0.1
 *   [2016-04-09]
 * @author Zdream
 * @version v0.2.2
 *   [2017-04-13]
 */
public class DefaultDamageFormula implements IDamageFormula {

	@Override
	public String name() {
		return "default";
	}

	@Override
	public final void damage(SkillReleasePackage package1) {
		this.pack = package1;
		
		// 属性判定
		writeType();
		
		// v0.2.2 技能判定: 是否发动失败
		if (!judgeValid()) {
			sendInvalid();
			return;
		}
		
		int length = package1.dfStaffLength();
		
		// 命中判定
		// v0.2 中将这部分从 onDamage 方法中移到了这里
		for (int index = 0; index < length; index++) {
			pack.setThiz(index);
			if (!writeHitable(index)) {
				sendMiss(index);
			}
		}
		
		eachRound();
	}
	
	/**
	 * <p>每一轮进攻的实施</p>
	 * <p>每次进行攻击时, 都会计算伤害和触发效果<br>
	 * 就算攻击没有命中, 也将进入该方法</p>
	 * @since v0.2.2
	 */
	protected void eachRound() {
		int length = pack.dfStaffLength();
		
		for (int index = 0; index < length; index++) {
			if (!pack.isHitable(index)) {
				continue;
			}
			pack.setThiz(index);
			onDamage(index);
		}
		
		// 附加状态 / 附加效果
		// v0.2.2 版本后, addition 的判断与计算不再受制于 onDamage 方法
		executeAddition();
	}
	
	/**
	 * 子类如果需要重写，请覆盖此函数
	 * @param index
	 * @return
	 */
	protected void onDamage(int index) {
		
		// 属性克制倍率
		if (writeTypeRate(index)) {
			// 没有效果
			sendImmune(index);
			return;
		}
		
		// 会心一击判定
		writeCt(index);
		
		// 攻击力计算
		writeAt(index);
		
		// 技能威力计算
		writePower(index);
		
		// 防御力计算
		writeDf(index);
		
		// 修正计算
		writeCorrect(index);
		
		// 伤害计算
		writeDamage(index);
		
		// 实现伤害
		executeDamage(index);
		
	}

	/**
	 * 判定命中并写入释放数据包<br>
	 * 该方法的结果是释放数据包中的所有防御方精灵的是否命中被写入<br>
	 * <br>
	 * 如果没有命中，还会向系统外发送没有命中的消息<br>
	 * @param index
	 * @return
	 *   是否命中
	 */
	protected boolean writeHitable(int index) {
		byte atseat = pack.getAtStaff().getSeat(); // 攻击方的 seat
		
		byte dfseat; // 防御方的 seat
		float rate; // 命中参数，攻击方的命中等级和防御方的躲避等级共同决定, 基数 1.0f
		boolean result = false;
		
		AttendManager am = pack.getAttends();

		dfseat = pack.getDfStaff(index).getSeat();
		// rate 计算了命中和躲避能力等级计算后的命中数值
		rate = am.hitableLevel(atseat, dfseat);
		// 攻击方命中 (必中负数)
		pack.setAtStaffAccuracy(countHitrate(), index);
		// 防御方躲避 (必不中负数)
		pack.setDfStaffHide(countHide(), index);
		// 技能命中 (必中0, 攻击方命中优先, 防御方躲避其次, 技能命中再次)
		pack.setSkillAccuracy((byte) countAccuracy());
		
		if (pack.getAtStaffAccuracy(index) < 0) {
			pack.setHitable(true, index);
			return true;
		}
		if (pack.getDfStaffHide(index) < 0) {
			pack.setHitable(false, index);
			return false;
		}
		if (pack.getSkillAccuracy() == 0) {
			pack.setHitable(true, index);
			return true;
		}
		
		rate *= (pack.getAtStaffAccuracy(index) / pack.getDfStaffHide(index)
				* pack.getSkillAccuracy() / 100.0f);

		result = !RanValue.isBigger((int) (rate * 1000), 1000);
		am.logPrintf(IPrintLevel.PRINT_LEVEL_VERBOSE,
				"DefaultDamageFormula.writeHitable(1): seat = %d 的命中判定 %s", dfseat, result);
		pack.setHitable(result, index);
		
		if (!result) {
			// 没有命中
			am.logPrintf(IPrintLevel.PRINT_LEVEL_INFO,
					"DefaultDamageFormula.writeHitable(1): seat=%d 的攻击没有命中", 
					pack.getDfStaff(index).getSeat());
		}

		return result;
	}
	
	/**
	 * 返回释放技能的 ID
	 * @return
	 */
	protected short getSkillID() {
		return pack.getSkill().getSkill().getId();
	}
	
	/**
	 * 计算攻击方实时命中率（忽略能力变化）
	 * @return
	 */
	protected float countHitrate() {
		return this.pack.getEffects().calcHitrate(pack);
	}
	
	/**
	 * 计算防御方实时躲避率（忽略能力变化）
	 * @return
	 */
	protected float countHide() {
		return this.pack.getEffects().calcHide(pack);
	}
	
	/**
	 * 计算技能实时命中率
	 * @return
	 */
	protected float countAccuracy() {
		return this.pack.getEffects().calcAccuracy(pack);
	}
	
	/**
	 * 判断是否会导致技能发动失败
	 * @return
	 */
	protected boolean judgeValid() {
		return true;
	}
	
	/**
	 * 判定释放技能的属性并写入释放数据包<br>
	 * 该方法的结果是释放数据包中的属性（type）被写入
	 */
	protected void writeType() {
		EPokemonType type = this.pack.getEffects().calcSkillType(pack);
		pack.getAttends().logPrintf(IPrintLevel.PRINT_LEVEL_VERBOSE,
				"DefaultDamageFormula.writeType(): seat = %d 的技能释放属性 %s", 
				pack.getAtStaff().getSeat(), type);
		pack.setType(type);
	}
	
	/**
	 * 判定克制倍率并写入释放数据包<br>
	 * 该方法的结果是释放数据包中的克制倍率被写入<br>
	 * <br>
	 * 如果没有效果，还会向系统外发送没有效果的消息 TODO (why, 现在没有实现)<br>
	 * @param index
	 * @return
	 *   是否能够向下进行判定<br>
	 *   当效果为无效（rate == 0.0f）时，返回 false
	 */
	protected boolean writeTypeRate(int index) {
		byte atseat = pack.getAtStaff().getSeat(); // 攻击方的 seat
		
		byte dfseat = pack.getDfStaff(index).getSeat(); // 防御方的 seat
		EPokemonType[] dfTypes = pack.getDfStaff(index).getTypes();
		float result; // 总倍率

		result = 1.0f;
		float[] rates = pack.getEffects().calcTypeRate(pack);

		for (int j = 0; j < dfTypes.length; j++) {
			result *= rates[j];
		}
		pack.getEffects().logPrintf(IPrintLevel.PRINT_LEVEL_VERBOSE,
				"DefaultDamageFormula.writeTypeRate(1): seat=%d 对 seat=%d 的技能释放属性倍率为 %f",
				atseat, dfseat, result);
		pack.setRate(result, index);
		
		if (result == 0.0f) {
			// 没有效果
			pack.getEffects().logPrintf(IPrintLevel.PRINT_LEVEL_INFO,
					"DefaultDamageFormula.writeTypeRate(1): seat=%d 的攻击好像没有效果", 
					pack.getDfStaff(index).getSeat());
		}
		
		return result == 0.0f;
		
	}
	
	/**
	 * 由于攻击没有命中，发出开胃酒消息
	 * @param index
	 */
	protected void sendMiss(int index) {
		Aperitif ap = pack.getEffects().newAperitif(Aperitif.CODE_BROADCAST);
		ap.append("type", "miss").append("-atseat", pack.getAtStaff().getSeat())
			.append("-dfseat", pack.getDfStaff(index).getSeat());
		pack.getEffects().startCode(ap);
	}
	
	/**
	 * 由于属性免疫，发出开胃酒消息
	 * @param index
	 */
	protected void sendImmune(int index) {
		Aperitif ap = pack.getEffects().newAperitif(Aperitif.CODE_BROADCAST);
		ap.append("type", "immune").append("-atseat", pack.getAtStaff().getSeat())
			.append("-dfseat", pack.getDfStaff(index).getSeat());
		pack.getEffects().startCode(ap);
	}
	
	/**
	 * 攻击失败，发出开胃酒消息
	 * @param index
	 */
	protected void sendInvalid() {
		Aperitif ap = pack.getEffects().newAperitif(Aperitif.CODE_BROADCAST);
		ap.append("type", "invalid").append("-atseat", pack.getAtStaff().getSeat())
			.append("-skill", pack.getSkill().getId());
		pack.getEffects().startCode(ap);
	}
	
	/**
	 * 判定是否发动会心一击（命中要害）并写入释放数据包<br>
	 * 该方法的结果是释放数据包中的会心一击被写入<br>
	 * @param index
	 */
	protected void writeCt(int index) {
		boolean result = false;
		
		byte ctLevel = pack.getEffects().calcCt(pack);
		if (ctLevel == -2) {
			result = false;
		} else if (ctLevel == -1) {
			result = true;
		} else {
			if (ctLevel > 3) {
				ctLevel = 3;
			}
			
			switch (ctLevel) {
			case 1:
				result = !RanValue.isBigger(0, 8);
				break;
			case 2:
				result = !RanValue.isBigger(0, 2);
				break;
			case 3:
				result = true;
				break;
			case 0: default:
				result = !RanValue.isBigger(0, 16);
				break;
			}
		}
		
		pack.setCtable(result, index);
		pack.getEffects().logPrintf(IPrintLevel.PRINT_LEVEL_VERBOSE,
				"DefaultDamageFormula.writeCt(1): seat=%d 对 seat=%d 的攻击%s", 
				pack.getAtStaff().getSeat(), pack.getDfStaff(pack.getThiz()).getSeat(),
				(result) ? "命中了要害" : "没有命中要害");
	}
	
	/**
	 * 计算攻击力（可能是物攻或特攻）并写入释放数据包<br>
	 * 该方法的结果是释放数据包中的攻击被写入<br>
	 * @param index
	 */
	protected void writeAt(int index) {
		int item = atItem();
		byte atseat = pack.getAtStaff().getSeat();
		boolean ct = pack.isCtable(index);
		
		EffectManage em = pack.getEffects();
		
		int at = em.calcAbility(atseat, item, (ct) ? 2 : 0);
		pack.setAt(at, index);
		em.logPrintf(IPrintLevel.PRINT_LEVEL_VERBOSE,
				"DefaultDamageFormula.writeAt(1): seat=%d 的攻击力 %d",
				atseat, at);
	}
	
	/**
	 * 根据技能选择攻击力由物攻还是特攻计算<br>
	 * 变化技能请不要调用该函数
	 * @return
	 */
	protected int atItem() {
		// 技能的伤害类型
		ESkillCategory category = pack.getSkill().getSkill().getCategory();
		
		if (category.equals(ESkillCategory.PHYSICS)) {
			pack.getAttends().logPrintf(IPrintLevel.PRINT_LEVEL_VERBOSE,
					"DefaultDamageFormula.atItem(): 攻击选择物攻类型");
			return IPokemonDataType.AT;
		} else if (category.equals(ESkillCategory.SPECIAL)) {
			pack.getAttends().logPrintf(IPrintLevel.PRINT_LEVEL_VERBOSE,
					"DefaultDamageFormula.atItem(): 攻击选择特攻类型");
			return IPokemonDataType.SA;
		} else {
			pack.getAttends().logPrintf(IPrintLevel.PRINT_LEVEL_WARN,
					"DefaultDamageFormula.atItem(): 攻击类型不是物攻和特攻，无法选择");
			return -1;
		}
	}
	
	/**
	 * 计算技能威力并写入释放数据包<br>
	 * 该方法的结果是释放数据包中的技能威力被写入<br>
	 * @param index
	 */
	protected void writePower(int index) {
		EffectManage em = pack.getEffects();
		
		int power = pack.getEffects().calcPower(pack);
		
		em.logPrintf(IPrintLevel.PRINT_LEVEL_VERBOSE,
				"DefaultDamageFormula.writeAt(1): seat=%d 的技能威力为 %d",
				pack.getAtStaff().getSeat(), power);
		
		pack.setPower(power, index);
	}
	
	/**
	 * 计算防御力（可能是物防或特防）并写入释放数据包<br>
	 * 该方法的结果是释放数据包中的防御被写入<br>
	 * @param index
	 */
	protected void writeDf(int index) {
		int item = dfItem();
		byte dfseat = pack.getDfStaff(index).getSeat();
		boolean ct = pack.isCtable(index);
		
		EffectManage em = pack.getEffects();
		
		int df = em.calcAbility(dfseat, item, (ct) ? 1 : 0);
		pack.setDf(df, index);
		em.logPrintf(IPrintLevel.PRINT_LEVEL_VERBOSE,
				"DefaultDamageFormula.writeAt(1): seat=%d 的防御力 %d",
				dfseat, df);
	}
	
	/**
	 * 根据技能选择防御力由物防还是特防计算<br>
	 * 变化技能请不要调用该函数
	 * @return
	 */
	protected int dfItem() {
		// 技能的伤害类型
		ESkillCategory category = pack.getSkill().getSkill().getCategory();
		
		if (category.equals(ESkillCategory.PHYSICS)) {
			pack.getAttends().logPrintf(IPrintLevel.PRINT_LEVEL_VERBOSE,
					"DefaultDamageFormula.atItem(): 攻击选择物防类型");
			return IPokemonDataType.DF;
		} else if (category.equals(ESkillCategory.SPECIAL)) {
			pack.getAttends().logPrintf(IPrintLevel.PRINT_LEVEL_VERBOSE,
					"DefaultDamageFormula.atItem(): 攻击选择特防类型");
			return IPokemonDataType.SD;
		} else {
			pack.getAttends().logPrintf(IPrintLevel.PRINT_LEVEL_WARN,
					"DefaultDamageFormula.atItem(): 攻击类型不是物防和特防，无法选择");
			return -1;
		}
	}
	
	/**
	 * 计算最后的伤害修正并写入释放数据包<br>
	 * 该方法的结果是释放数据包中的修正被写入<br>
	 * 修正包括乱数修正等, 不包括本系加成修正<br>
	 * @param index
	 */
	protected void writeCorrect(int index) {
		float correct = (RanValue.random(0, 16) + 85) / 100.0f;
		if (pack.isCtable(index)) {
			correct *= 1.5f;
		}
		// 先放入 package, 让 box 进行再加工. 这一步不能缺
		pack.setCorrect(correct, index);
		correct = pack.getEffects().calcCorrect(pack);
		pack.getAttends().logPrintf(IPrintLevel.PRINT_LEVEL_VERBOSE,
				"DefaultDamageFormula.writeAt(1): seat=%d 的技能伤害修正为 %f",
				pack.getAtStaff().getSeat(), correct);
		pack.setCorrect(correct, index);
	}
	
	/**
	 * 计算最后伤害并写入释放数据包<br>
	 * 该方法的结果是释放数据包中的伤害被写入<br>
	 * @param index
	 */
	protected void writeDamage(int index) {
		byte level = pack.getAtStaff().getAttendant().getLevel();
		int damage = (int) (((2.0f * level + 10) / 250 * pack.getAt(index) * pack.getPower(index)
				/ pack.getDf(index) + 2) * pack.getCorrect(index) * pack.getRate(index));

		// 先放入 package, 让 box 进行再加工. 这一步不能缺
		pack.setDamage(damage, index);
		damage = pack.getEffects().calcDamage(pack);
		
		pack.getEffects().logPrintf(IPrintLevel.PRINT_LEVEL_VERBOSE,
				"DefaultDamageFormula.writeAt(1): seat=%d 的技能伤害为 %d",
				pack.getAtStaff().getSeat(), damage);
		pack.setDamage(damage, index);
	}
	
	/**
	 * 最终的伤害实现
	 * @param index
	 */
	protected void executeDamage(int index) {
		byte seat = pack.getDfStaff(index).getSeat(); // 防御方
		int effect = 0;
		if (pack.getRate(index) > 1) {
			effect = 2;
		} else if (pack.getRate(index) < 1) {
			effect = 1;
		}
		
		// 伤害刷新: 最高到防御方的 hpi v0.2.2
		int damage = pack.getDamage(index);
		int hpi = pack.getDfStaff(index).getHpi();
		if (damage > hpi) {
			damage = hpi;
			pack.setDamage(hpi, index);
		}
		
		Aperitif value = pack.getEffects().newAperitif(Aperitif.CODE_SKILL_DAMAGE, seat);
		value.append("seat", seat);
		value.append("value", damage);
		value.append("ctable", pack.isCtable(index));
		value.append("effect", effect);
		pack.getEffects().startCode(value);
	}
	
	/**
	 * 附加状态 / 附加效果实现
	 */
	protected void executeAddition() {
		this.executeAddition(false);
	}
	
	/**
	 * 附加状态 / 附加效果实现<br>
	 * 判断附加状态时, 忽略是否命中的参数, 一样必须执行
	 * @param ignoreHit
	 *   在判断附加状态时是否忽略是否命中的参数
	 * @since v0.2.2
	 */
	protected void executeAddition(boolean ignoreHit) {
		IAdditionFormula[] formulas = pack.getAdditionFormulas();
		for (int i = 0; i < formulas.length; i++) {
			IAdditionFormula f = formulas[i];
			if (ignoreHit) {
				JsonValue v = new JsonValue(JsonType.ObjectType);
				v.add("ignoreHit", new JsonValue(true));
				f.set(v);
			}
			f.addition(pack);
		}
	}
	
	/* ************
	 *	数据结构  *
	 ************ */
	/**
	 * 释放数据包
	 */
	protected SkillReleasePackage pack;
	
	protected SkillReleasePackage getPack() {
		return pack;
	}
	
	/* ************
	 *	 构造器   *
	 ************ */

	public DefaultDamageFormula() {
		
	}

}
