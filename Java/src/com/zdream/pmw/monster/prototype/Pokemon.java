package com.zdream.pmw.monster.prototype;

import java.io.Serializable;
import java.util.Calendar;

/**
 * 精灵的数据模型<br>
 * <br>
 * <b>v0.1.1</b><br>
 *   修改了 toString() 方法<br>
 * 
 * @since v0.1
 * @author Zdream
 * @date 2016年3月14日
 * @version v0.2
 */
public class Pokemon implements IPokemonDataType, IPokemonTrainerMarker, Serializable {
	
	private static final long serialVersionUID = -8640016272700826226L;

	/* ************
	 *	主要信息  *
	 ************ */
	/*
	 * 主要信息，包括：
	 *   编号 identifier
	 *   精灵编号 speciesID
	 *   形态 form
	 *   性格值 pid
	 *   特性 ability
	 *   性别 gender
	 *   性格 nature
	 *   蛋 egg
	 *   亲密度 / 孵化步数 happiness
	 *   昵称 nickname
	 *   等级 level
	 *   经验 experience
	 *   携带道具 heldItem
	 */
	
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
	 * 性格值 PID<br>
	 * <strong>保留项</strong><br>
	 * 值范围为 0 - 4'294'967'295(0xFFFF FFFF)<br>
	 * 读取时 int 转 long 值：long l = ((long) i) & 0xffffffffL;<br>
	 * 写入时 long 转 int 值：int i = (int)l;
	 */
	private int pid;
	
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
	 * 蛋 egg<br>
	 * 精灵是否处在未孵化的状态<br>
	 * true 表示尚未孵化
	 */
	private boolean egg;
	
	/**
	 * 亲密度 / 孵化步数 happiness<br>
	 * 当精灵未孵化时（即 egg = true），该值表示剩余的孵化步数（值域：0-32767）；<br>
	 * 当精灵孵化时（即 egg = false），该值表示亲密度（值域：0-255）；<br>
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

	public long getPid() {
		return ((long) pid) & 0xffffffffL;
	}

	public void setPid(long pid) {
		this.pid = (int)pid;
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

	public boolean isEgg() {
		return egg;
	}

	public void setEgg(boolean egg) {
		this.egg = egg;
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
	
	/* ************
	 *	相遇信息  *
	 ************ */
	/*
	 * 相遇信息，包括：
	 *   相遇位置 met location
	 *   捕捉球类型 met ball
	 *   相遇时等级 met level
	 *   相遇时间 met date
	 *   相遇类型 encounter
	 *   获得蛋的位置 met egg location
	 *   获得蛋的时间 met egg date
	 */
	
	/**
	 * 相遇位置 met location<br>
	 * 第一次精灵与玩家相遇，并且精灵开始跟随玩家时的位置编码（存放在数据库中）
	 */
	private short metLocation;
	
	/**
	 * 捕捉球类型 met ball<br>
	 * 玩家将精灵捕捉时使用的球的类型，默认为<strong>怪兽球</strong><br>
	 * 使用道具编号
	 */
	private short metBall;
	
	/**
	 * 相遇时等级 met level<br>
	 * 第一次精灵与玩家相遇时，精灵的等级
	 */
	private byte metLevel;
	
	/**
	 * 相遇时间 met date<br>
	 * 第一次精灵与玩家相遇时的时间（精确到日）
	 */
	private Calendar metDate;
	
	/**
	 * 相遇类型 encounter<br>
	 * 这个指明了第一次精灵与玩家相遇时，相遇时的情景<br>
	 * 比如在草丛、洞窟捕获，或是从蛋中孵化
	 */
	private byte encounter;
	
	/**
	 * 获得蛋的位置 met egg location<br>
	 * 这个属性只在相遇类型为<strong>从蛋中孵化</strong>时才有效<br>
	 * 它指明了蛋孵化时玩家的位置编码
	 */
	private short metEggLocation;
	
	/**
	 * 获得蛋的时间 met egg date<br>
	 * 这个属性只在相遇类型为<strong>从蛋中孵化</strong>时才有效<br>
	 * 它指明了玩家获得蛋的时间（精确到日）
	 */
	private Calendar metEggDate;

	public short getMetLocation() {
		return metLocation;
	}

	public void setMetLocation(short metLocation) {
		this.metLocation = metLocation;
	}

	public short getMetBall() {
		return metBall;
	}

	public void setMetBall(short metBall) {
		this.metBall = metBall;
	}

	public byte getMetLevel() {
		return metLevel;
	}

	public void setMetLevel(byte metLevel) {
		this.metLevel = metLevel;
	}

	public Calendar getMetDate() {
		return metDate;
	}

	public void setMetDate(Calendar metDate) {
		this.metDate = metDate;
	}

	public byte getEncounter() {
		return encounter;
	}

	public void setEncounter(byte encounter) {
		this.encounter = encounter;
	}

	public short getMetEggLocation() {
		return metEggLocation;
	}

	public void setMetEggLocation(short metEggLocation) {
		this.metEggLocation = metEggLocation;
	}

	public Calendar getMetEggDate() {
		return metEggDate;
	}

	public void setMetEggDate(Calendar metEggDate) {
		this.metEggDate = metEggDate;
	}
	
	/* ************
	 *	参数信息  *
	 ************ */
	/*
	 * 参数信息，包括：
	 *   个体值 stat-IV
	 *   努力值 stat-EV
	 *   参数 stat
	 *   表演参数 stat-contest
	 *   异常状态 abnormal
	 *   生命值 hpi
	 */
	
	/**
	 * （能力值）参数长度，包括战斗和表演的能力值
	 */
	private static final int STAT_LENGTH = 6;
	
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
	 * 值域：大于 0
	 */
	private short[] stat = new short[STAT_LENGTH];
	
	/**
	 * 表演参数 stat-contest<br>
	 * 值域 0-255，非负
	 */
	private byte[] statContest = new byte[STAT_LENGTH];
	
	/**
	 * 异常状态 abnormal<br>
	 * 精灵处在的异常状态，该值标明了其编码
	 */
	private byte abnormal;
	
	/**
	 * 生命值 hpi
	 */
	private short hpi;

	public byte[] getStatIV() {
		return statIV;
	}

	public void setStatIV(byte[] statIV) {
		this.statIV = statIV;
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

	public byte[] getStatContest() {
		return statContest;
	}

	public void setStatContest(byte[] statContest) {
		this.statContest = statContest;
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
	
	/* ************
	 *	行动信息  *
	 ************ */
	/*
	 * 行动信息，包括：
	 *   技能列表  skill
	 *   技能 PP  skill PP
	 *   技能 PP 上限  skill PP ups
	 */

	/**
	 * （能力值）参数长度，包括战斗和表演的能力值
	 */
	private static final int SKILL_LENGTH = 6;
	
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
	
	/* ************
	 * 训练师信息 *
	 ************ */
	/*
	 * 训练师信息，包括：
	 *   训练师名  trainer name
	 *   训练师性别  trainer gender
	 *   训练师 ID  trainer ID
	 *   训练师隐式 ID  trainer SID
	 *   标签  trainer markers
	 */

	/**
	 * 训练师名  trainer name
	 */
	private String trainerName;
	
	/**
	 * 训练师性别  trainer gender<br>
	 * 它的可能值在 EPokemonGender 中进行了定义，可能是 M、F 中的一个；
	 */
	private EPokemonGender trainerGender;
	
	/**
	 * 训练师 ID  trainer ID
	 */
	private int trainerID;
	
	/**
	 * 训练师隐式 ID  trainer SID
	 */
	private int trainerSID;
	
	/**
	 * 标签  trainer markers<br>
	 * 0-5: {circle, triangle, square, heart, star, diamond}
	 */
	private boolean[] trainerMarkers = new boolean[MARKER_LENGTH];

	public String getTrainerName() {
		return trainerName;
	}

	public void setTrainerName(String trainerName) {
		this.trainerName = trainerName;
	}

	public EPokemonGender getTrainerGender() {
		return trainerGender;
	}

	public void setTrainerGender(EPokemonGender trainerGender) {
		this.trainerGender = trainerGender;
	}

	public int getTrainerID() {
		return trainerID;
	}

	public void setTrainerID(int trainerID) {
		this.trainerID = trainerID;
	}

	public int getTrainerSID() {
		return trainerSID;
	}

	public void setTrainerSID(int trainerSID) {
		this.trainerSID = trainerSID;
	}

	public boolean[] getTrainerMarkers() {
		return trainerMarkers;
	}

	public void setTrainerMarkers(boolean[] trainerMarkers) {
		this.trainerMarkers = trainerMarkers;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder(30);
		builder.append(speciesID);
		if (form != 0) {
			builder.append('-').append(form);
		}
		builder.append(' ').append(nickname).append(" Lv.").append(' ')
				.append(hpi).append('/').append(stat[0]);
		
		return builder.toString();
	}
}
