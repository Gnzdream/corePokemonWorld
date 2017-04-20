package com.zdream.pmw.platform.effect;

import com.zdream.pmw.monster.prototype.EPokemonType;
import com.zdream.pmw.platform.attend.AttendManager;
import com.zdream.pmw.platform.attend.Participant;
import com.zdream.pmw.platform.attend.SkillRelease;
import com.zdream.pmw.platform.effect.accuracy.IAccuracyFormula;
import com.zdream.pmw.platform.effect.addition.IAdditionFormula;
import com.zdream.pmw.platform.effect.damage.IDamageFormula;
import com.zdream.pmw.platform.effect.moveable.IMoveableFormula;
import com.zdream.pmw.platform.effect.power.IPowerFormula;
import com.zdream.pmw.platform.effect.range.IRangeFormula;
import com.zdream.pmw.platform.prototype.BattlePlatform;

/**
 * 技能释放的数据包<br>
 * 里面记录了单次技能释放时的必要数据<br>
 * <br>
 * <b>v0.2</b><br>
 *   添加所需环境的属性<br>
 * <br>
 * <b>v0.2.1</b><br>
 *   该类将存储技能对应的计算公式<br>
 * 
 * @since v0.1
 * @author Zdream
 * @date 2016年4月7日
 * @version v0.2.2
 */
public class SkillReleasePackage {
	
	/* ************
	 *	所需环境  *
	 ************ */
	/*
	 * @since v0.2
	 */
	
	EffectManage em;
	AttendManager am;
	
	public EffectManage getEffects() {
		return em;
	}

	public AttendManager getAttends() {
		return am;
	}

	public void initEnvironment(BattlePlatform pf) {
		this.em = pf.getEffectManage();
		this.am = pf.getAttendManager();
	}
	
	/* ************
	 *	数据结构  *
	 ************ */
	
	/**
	 * 判定的防御方个数
	 */
	public int dfStaffLength() {
		return targets.length;
	}
	
	/**
	 * 设置防御方的个数
	 * @param length
	 */
	public void setDfStaffLength(int length) {
		dfStaffs = new Participant[length];
		atStaffAccuracy = new float[length];
		dfStaffHide = new float[length];
		hitable = new boolean[length];
		ctable = new boolean[length];
		rates = new float[length];
		correct = new float[length];
		ats = new int[length];
		dfs = new int[length];
		powers = new int[length];
		damages = new int[length];
	}
	
	int thiz;
	/**
	 * 现在判定的是第几个防御方
	 * @return
	 */
	public int getThiz() {
		return thiz;
	}
	
	public void setThiz(int thiz) {
		this.thiz = thiz;
	}
	
	/*
	 * 攻击方（Participant）
	 * 防御方列表（Participant[]）
	 * 技能释放体（SkillRelease）
	 */
	Participant atStaff;
	Participant[] dfStaffs;
	SkillRelease skill;
	byte skillNum;
	
	/**
	 * 攻击方 Participant
	 * @return
	 */
	public Participant getAtStaff() {
		return atStaff;
	}

	public void setAtStaff(Participant atStaff) {
		this.atStaff = atStaff;
	}
	
	/**
	 * 第 index 个判定的防御方 Participant
	 * @param index
	 *   从 0 开始
	 * @return
	 */
	public Participant getDfStaff(int index) {
		return dfStaffs[index];
	}
	
	/**
	 * 获取所有防御方座位号组成的数组
	 * @return
	 * @since v0.2.2
	 */
	public byte[] dfSeats() {
		byte[] bs = new byte[dfStaffs.length];
		for (int i = 0; i < bs.length; i++) {
			bs[i] = dfStaffs[i].getSeat();
		}
		return bs;
	}

	public void setDfStaff(Participant dfStaff, int index) {
		this.dfStaffs[index] = dfStaff;
	}
	
	/**
	 * 释放的技能 SkillRelease
	 * @return
	 */
	public SkillRelease getSkill() {
		return skill;
	}
	
	public void setSkill(SkillRelease skill) {
		this.skill = skill;
	}
	
	/**
	 * 该释放的技能是精灵的第几个技能
	 * @return
	 */
	public byte getSkillNum() {
		return skillNum;
	}
	public void setSkillNum(byte skillNum) {
		this.skillNum = skillNum;
	}
	
	
	/*
	 * 行动判定（boolean）
	 * 原始目标数据（byte）
	 * 目标列表（byte[]）
	 * 攻击方命中率列表（float[]）
	 * 防御方躲避率列表（float[]）
	 * 技能命中率（byte）
	 * 命中列表（boolean[]）
	 * 会心列表（boolean[]）
	 */
	boolean moveable;
	byte originTarget;
	byte[] targets;
	float[] atStaffAccuracy,
		dfStaffHide;
	byte skillAccuracy;
	boolean[] hitable,
		ctable;
	
	/**
	 * 攻击方能否行动
	 * @return
	 */
	public boolean isMoveable() {
		return moveable;
	}

	public void setMoveable(boolean moveable) {
		this.moveable = moveable;
	}

	
	/**
	 * 原始目标选择的数据<br>
	 * 例如训练师下达命令中，选择的对象<br>
	 * <br>
	 * 含默认值的数据为 -1<br>
	 * 比如对方全体这类技能
	 * @return
	 */
	public byte getOriginTarget() {
		return originTarget;
	}

	public void setOriginTarget(byte originTarget) {
		this.originTarget = originTarget;
	}
	
	/**
	 * 在技能目标中第 index 个防御方的 seat
	 * @param index
	 *   从 0 开始
	 * @return
	 */
	public byte getTarget(int index) {
		return targets[index];
	}
	
	public void setTarget(byte target, int index) {
		this.targets[index] = target;
	}
	
	/**
	 * 设置目标 seats，此时防御方以全部确定
	 * @param targets
	 */
	public void setTargets(byte[] targets) {
		this.targets = targets;
		setDfStaffLength(targets.length);
	}
	
	/**
	 * 在判定技能目标中第 index 个防御方时，对它释放技能时攻击方的命中率<br>
	 * @param index
	 *   从 0 开始
	 * @return
	 */
	public float getAtStaffAccuracy(int index) {
		return atStaffAccuracy[index];
	}

	public void setAtStaffAccuracy(float atStaffAccuracy, int index) {
		this.atStaffAccuracy[index] = atStaffAccuracy;
	}
	
	/**
	 * 在判定技能目标中第 index 个防御方时，对它释放技能时该防御方的躲避率<br>
	 * @param index
	 *   从 0 开始
	 * @return
	 */
	public float getDfStaffHide(int index) {
		return dfStaffHide[index];
	}

	public void setDfStaffHide(float dfStaffHide, int index) {
		this.dfStaffHide[index] = dfStaffHide;
	}
	
	/**
	 * 技能的命中率
	 * @return
	 */
	public byte getSkillAccuracy() {
		return skillAccuracy;
	}

	public void setSkillAccuracy(byte skillAccuracy) {
		this.skillAccuracy = skillAccuracy;
	}
	
	/**
	 * 是否命中了第 index 个防御方
	 * @param index
	 *   从 0 开始
	 * @return
	 */
	public boolean isHitable(int index) {
		return hitable[index];
	}

	public void setHitable(boolean hitable, int index) {
		this.hitable[index] = hitable;
	}
	
	/**
	 * 是否对第 index 个防御方命中要害
	 * @param index
	 *   从 0 开始
	 * @return
	 */
	public boolean isCtable(int index) {
		return ctable[index];
	}

	public void setCtable(boolean ctable, int index) {
		this.ctable[index] = ctable;
	}
	
	/*
	 * 发动属性（EPokemonType）
	 * 克制倍率列表（float[]）
	 * 修正列表（float[]）
	 * 攻击力列表（short[]）
	 * 防御力列表（short[]）
	 * 威力列表（short[]）
	 * 伤害列表（short[]）
	 */
	EPokemonType type;
	float[] rates,
		correct;
	int[] ats,
		dfs,
		powers,
		damages;
	
	/**
	 * 释放技能的真正属性
	 * @return
	 */
	public EPokemonType getType() {
		return type;
	}

	public void setType(EPokemonType type) {
		this.type = type;
	}

	/**
	 * 对第 index 个防御方的技能属性克制倍率
	 * @param index
	 *   从 0 开始
	 * @return
	 */
	public float getRate(int index) {
		return rates[index];
	}

	public void setRate(float rate, int index) {
		this.rates[index] = rate;
	}

	/**
	 * 对第 index 个防御方的其它伤害修正
	 * @param index
	 *   从 0 开始
	 * @return
	 */
	public float getCorrect(int index) {
		return correct[index];
	}

	public void setCorrect(float correct, int index) {
		this.correct[index] = correct;
	}

	/**
	 * 对第 index 个防御方攻击时，攻击方攻击 / 特攻的实时值
	 * @param index
	 *   从 0 开始
	 * @return
	 */
	public int getAt(int index) {
		return ats[index];
	}
	
	public void setAt(int at, int index) {
		this.ats[index] = at;
	}

	/**
	 * 对第 index 个防御方攻击时，防御方防御 / 特防的实时值
	 * @param index
	 *   从 0 开始
	 * @return
	 */
	public int getDf(int index) {
		return dfs[index];
	}

	public void setDf(int dfs, int index) {
		this.dfs[index] = dfs;
	}

	/**
	 * 对第 index 个防御方攻击时，技能威力的实时值
	 * @param index
	 *   从 0 开始
	 * @return
	 */
	public int getPower(int index) {
		return powers[index];
	}
	
	public void setPower(int power, int index) {
		this.powers[index] = power;
	}

	/**
	 * 对第 index 个防御方攻击时，造成的伤害
	 * @param index
	 *   从 0 开始
	 * @return
	 */
	public int getDamage(int index) {
		return damages[index];
	}

	public void setDamage(int damage, int index) {
		this.damages[index] = damage;
	}
	
	/* ************
	 *	计算公式  *
	 ************ */
	/*
	 * @version 0.2.1
	 */
	
	IMoveableFormula movableFormula;
	IRangeFormula rangeFormula;
	IDamageFormula damageFormula;
	IAccuracyFormula accuracyFormula;
	IPowerFormula powerFormula;
	IAdditionFormula[] additionFormulas;

	public IMoveableFormula getMovableFormula() {
		return movableFormula;
	}

	public void setMovableFormula(IMoveableFormula movableFormula) {
		this.movableFormula = movableFormula;
	}

	public IRangeFormula getRangeFormula() {
		return rangeFormula;
	}

	public void setRangeFormula(IRangeFormula rangeFormula) {
		this.rangeFormula = rangeFormula;
	}

	public IDamageFormula getDamageFormula() {
		return damageFormula;
	}

	public void setDamageFormula(IDamageFormula damageFormula) {
		this.damageFormula = damageFormula;
	}

	public IAccuracyFormula getAccuracyFormula() {
		return accuracyFormula;
	}

	public void setAccuracyFormula(IAccuracyFormula accuracyFormula) {
		this.accuracyFormula = accuracyFormula;
	}

	public IPowerFormula getPowerFormula() {
		return powerFormula;
	}

	public void setPowerFormula(IPowerFormula powerFormula) {
		this.powerFormula = powerFormula;
	}

	public IAdditionFormula[] getAdditionFormulas() {
		return additionFormulas;
	}

	public void setAdditionFormulas(IAdditionFormula[] additionFormulas) {
		this.additionFormulas = additionFormulas;
	}
	
	/**
	 * 清除所有的公式
	 * @since v0.2.2
	 */
	public void clearFormulas() {
		movableFormula = null;
		rangeFormula = null;
		damageFormula = null;
		accuracyFormula = null;
		powerFormula = null;
		additionFormulas = null;
	}
	
	
	/* ************
	 *	构造方法  *
	 ************ */

	public SkillReleasePackage() {
		
	}

}
