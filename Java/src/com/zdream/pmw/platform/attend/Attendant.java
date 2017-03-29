package com.zdream.pmw.platform.attend;

import java.io.Serializable;

import com.zdream.pmw.monster.prototype.EPokemonGender;
import com.zdream.pmw.monster.prototype.EPokemonNature;

/**
 * 战斗精灵的数据模型
 * 
 * @since v0.1
 * @author Zdream
 * @date 2016年3月29日
 * @version v0.1
 */
public class Attendant implements Serializable{
	private static final long serialVersionUID = -3343258489411940816L;

	private byte no;
	/**
	 * 获得精灵对应的准入号 no
	 * @return
	 */
	public byte getNo() {
		return no;
	}
	
	/**
	 * 编号 identifier<br>
	 * 对于每一个训练师，他的精灵的编号都是不同的
	 */
	private int identifier;
	
	/**
	 * 精灵编号 species ID<br>
	 * 标定了每个精灵的类型，以官方的全国图鉴为准。
	 */
	private short speciesID;
	
	/**
	 * 精灵形态 form<br>
	 * 精灵的形态可以不同，指除了性别以外的形态。有些精灵是没有形态的
	 */
	private byte form;
	
	/**
	 * 特性 ability<br>
	 * 精灵的特性（编号）
	 */
	private short ability;
	
	/**
	 * 性别 gender<br>
	 * 精灵的性别；<br>
	 * 它的可能值在 EPokemonGender 中进行了定义，可能是 M、F、N 中的一个；<br>
	 * 对于一个固定的精灵种类，它的 EPokemonGender 可能是固定的，也可能是去掉以上三种中的某种选项。
	 */
	private EPokemonGender gender;
	
	/**
	 * 性格 nature<br>
	 * 精灵的性格（枚举）
	 */
	private EPokemonNature naturn;
	
	/**
	 * 亲密度 happiness<br>
	 * 该值值域：0-255<br>
	 */
	private short happiness;
	
	/**
	 * 昵称 nickname
	 */
	private String nickname;
	
	/**
	 * 等级 level<br>
	 * 值域 1-100
	 */
	private byte level;
	
	/**
	 * 经验 experience
	 */
	private int experience;
	
	/**
	 * 携带道具 held item<br>
	 * 仅存储道具的编号
	 */
	private short heldItem;

	public int getIdentifier() {
		return identifier;
	}

	public void setIdentifier(int identifier) {
		this.identifier = identifier;
	}

	public short getSpeciesID() {
		return speciesID;
	}

	public void setSpeciesID(short speciesID) {
		this.speciesID = speciesID;
	}

	public byte getForm() {
		return form;
	}

	public void setForm(byte form) {
		this.form = form;
	}
	
	public short getAbility() {
		return ability;
	}
	
	public void setAbility(short ability) {
		this.ability = ability;
	}
	
	public EPokemonGender getGender() {
		return gender;
	}

	public void setGender(EPokemonGender gender) {
		this.gender = gender;
	}

	public EPokemonNature getNaturn() {
		return naturn;
	}
	
	public void setNaturn(EPokemonNature naturn) {
		this.naturn = naturn;
	}

	public short getHappiness() {
		return happiness;
	}

	public void setHappiness(short happiness) {
		this.happiness = happiness;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public byte getLevel() {
		return level;
	}

	public void setLevel(byte level) {
		this.level = level;
	}

	public int getExperience() {
		return experience;
	}

	public void setExperience(int experience) {
		this.experience = experience;
	}

	public short getHeldItem() {
		return heldItem;
	}

	public void setHeldItem(short heldItem) {
		this.heldItem = heldItem;
	}
	

	/**
	 * （能力值）参数长度，包括战斗和表演的能力值
	 */
	private static final int STAT_LENGTH = 6;
	
	/**
	 * 种族值 speciesValue<br>
	 */
	private short[] speciesValue = new short[STAT_LENGTH];
	
	/**
	 * 个体值 stat-IV<br>
	 * 值域 0-31
	 */
	private byte[] statIV = new byte[STAT_LENGTH];
	
	/**
	 * 努力值 stat-EV<br>
	 * 值域 0-255，非负
	 */
	private byte[] statEV = new byte[STAT_LENGTH];
	
	/**
	 * 参数 stat<br>
	 * 值域：大于等于 0
	 */
	private short[] stat = new short[STAT_LENGTH];
	
	/**
	 * 异常状态 abnormal<br>
	 * 精灵处在的异常状态 code，该值标明了其编码
	 */
	private byte abnormal;
	
	/**
	 * 现有 HP 值
	 */
	private short hpi;

	public byte[] getStatIV() {
		return statIV;
	}

	public void setStatIV(byte[] statIV) {
		this.statIV = statIV;
	}

	public short[] getSpeciesValue() {
		return speciesValue;
	}

	public void setSpeciesValue(short[] speciesValue) {
		this.speciesValue = speciesValue;
	}

	public byte[] getStatEV() {
		return statEV;
	}

	public void setStatEV(byte[] statEV) {
		this.statEV = statEV;
	}

	public short[] getStat() {
		return stat;
	}

	public void setStat(short[] stat) {
		this.stat = stat;
	}

	public byte getAbnormal() {
		return abnormal;
	}

	public void setAbnormal(byte abnormal) {
		this.abnormal = abnormal;
	}
	
	public short getHpi() {
		return hpi;
	}

	public void setHpi(short hpi) {
		this.hpi = hpi;
	}

	/**
	 * （能力值）参数长度，包括战斗和表演的能力值
	 */
	private static final int SKILL_LENGTH = 4;
	
	/**
	 * 技能列表 skill<br>
	 * 仅存储技能的编号
	 */
	private short[] skill = new short[SKILL_LENGTH];
	
	/**
	 * 技能 PP  skill PP
	 * 值域 0-64，非负
	 */
	private byte[] skillPP = new byte[SKILL_LENGTH];
	
	/**
	 * 技能 PP 上限  skill PP ups
	 * 值域 0-3，非负
	 */
	private byte[] skillPPUps = new byte[SKILL_LENGTH];
	
	public short[] getSkill() {
		return skill;
	}
	
	public void setSkill(short[] skill) {
		this.skill = skill;
	}
	
	public byte[] getSkillPP() {
		return skillPP;
	}

	public void setSkillPP(byte[] skillPP) {
		this.skillPP = skillPP;
	}

	public byte[] getSkillPPUps() {
		return skillPPUps;
	}

	public void setSkillPPUps(byte[] skillPPUps) {
		this.skillPPUps = skillPPUps;
	}
	
	/**
	 * 标识该精灵现在的主人是不是原来的主人<br>
	 * 这个参数主要影响对战之后经验值的获得数量
	 */
	private boolean realTrainer;
	
	public boolean isRealTrainer() {
		return realTrainer;
	}

	public void setRealTrainer(boolean realTrainer) {
		this.realTrainer = realTrainer;
	}

	/**
	 * 精灵的准入号
	 * @param no
	 */
	public Attendant(byte no) {
		this.no = no;
	}
	
	@Override
	public String toString() {
		return String.format("Attendant:%d %s Lv.%d HP:%d/%d",
				no, nickname, level, hpi, stat[0]);
	}

}
