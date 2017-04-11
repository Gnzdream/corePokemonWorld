package com.zdream.pmw.monster.prototype;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import com.zdream.pmw.core.tools.AbnormalMethods;

/**
 * 精灵的数据模型<br>
 * The prototype of Pokemon.<br>
 * <p>该类中, 只会存储精灵 (怪兽) 的个性数据, 比如每个怪兽的性格、特性都会不同.<br>
 * 而 {@link com.zdream.pmw.monster.data.PokemonBaseData} 存储的是怪兽的共性数据.
 * 比如两个相同形态的怪兽, 他们的属性一定是相同的.</p>
 * <p>In this class, we only store personality data like character, ability, which
 * is the same between two Pokemon of the same species.<br>
 * And we store the common data (ex. types) in class
 * {@link com.zdream.pmw.monster.data.PokemonBaseData}.</p>
 * <br>
 * <b>v0.1.1</b><br>
 *   修改了 toString() 方法<br>
 * <br>
 * <p><b>v0.2.2</b><br>
 * <li>该模型支持自定义序列化方法;
 * <li>修正了设置怪兽的方法, 使其无法设置数组</li></p>
 * 
 * @since v0.1
 * @author Zdream
 * @date 2016年3月14日
 * @version v0.2.2
 */
public class Pokemon implements IPokemonDataType, IPokemonTrainerMarker, Externalizable {
	
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
	 * <p>According to National Pokedex</p>
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
	 * 值范围为 0 - 4_294_967_295(0xFFFF FFFF)<br>
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
	 * <p>The gender can be: M(Male) F(Female) N(Unknowed)</p>
	 */
	private EPokemonGender gender;
	
	/**
	 * 性格 nature<br>
	 * 精灵的性格（枚举）
	 */
	private EPokemonNature nature;
	
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

	public EPokemonNature getNature() {
		return nature;
	}
	
	public void setNature(EPokemonNature nature) {
		this.nature = nature;
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
	private java.time.Instant metDate;
	
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
	private java.time.Instant metEggDate;

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

	public java.time.Instant getMetDate() {
		return metDate;
	}

	public void setMetDate(java.time.Instant metDate) {
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

	public java.time.Instant getMetEggDate() {
		return metEggDate;
	}

	public void setMetEggDate(java.time.Instant metEggDate) {
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
	 * 异常状态 abnormal code<br>
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
	
	/**
	 * <p>设置个体值</p>
	 * <p>当输入参数为 -1 时, 为不改变.</p>
	 * @param statIV0
	 *   HP 个体值
	 * @param statIV1
	 *   攻击个体值
	 * @param statIV2
	 *   防御个体值
	 * @param statIV3
	 *   特攻个体值
	 * @param statIV4
	 *   特防个体值
	 * @param statIV5
	 *   速度个体值
	 * @since v0.2.2
	 */
	public void setStatIV(int statIV0, int statIV1, int statIV2,
			int statIV3, int statIV4, int statIV5) {
		if (statIV0 != -1) {
			statIV[0] = (byte) statIV0;
		}
		if (statIV1 != -1) {
			statIV[1] = (byte) statIV1;
		}
		if (statIV2 != -1) {
			statIV[2] = (byte) statIV2;
		}
		if (statIV3 != -1) {
			statIV[3] = (byte) statIV3;
		}
		if (statIV4 != -1) {
			statIV[4] = (byte) statIV4;
		}
		if (statIV5 != -1) {
			statIV[5] = (byte) statIV5;
		}
	}
	
	/**
	 * <p>设置单项个体值</p>
	 * @param item
	 *   具体哪项数据
	 *   在 {@link IPokemonDataType} 中, 设置了具体的参数,
	 *   可以为 HP, AT, DF, SA, SD, SP.
	 * @param statIV
	 *   设置的个体值
	 * @since v0.2.2
	 */
	public void setStatIV(int item, int statIV) {
		this.statIV[item] = (byte) statIV;
	}

	public byte[] getStatEV() {
		return statEV;
	}
	
	/**
	 * <p>设置努力值</p>
	 * <p>当输入参数为 -1 时, 为不改变.</p>
	 * @param statEV0
	 *   HP 努力值
	 * @param statEV1
	 *   攻击努力值
	 * @param statEV2
	 *   防御努力值
	 * @param statEV3
	 *   特攻努力值
	 * @param statEV4
	 *   特防努力值
	 * @param statEV5
	 *   速度努力值
	 * @since v0.2.2
	 */
	public void setStatEV(int statEV0, int statEV1, int statEV2,
			int statEV3, int statEV4, int statEV5) {
		if (statEV0 != -1) {
			statEV[0] = (byte) statEV0;
		}
		if (statEV1 != -1) {
			statEV[1] = (byte) statEV1;
		}
		if (statEV2 != -1) {
			statEV[2] = (byte) statEV2;
		}
		if (statEV3 != -1) {
			statEV[3] = (byte) statEV3;
		}
		if (statEV4 != -1) {
			statEV[4] = (byte) statEV4;
		}
		if (statEV5 != -1) {
			statEV[5] = (byte) statEV5;
		}
	}
	
	/**
	 * <p>设置单项努力值</p>
	 * @param item
	 *   具体哪项数据
	 *   在 {@link IPokemonDataType} 中, 设置了具体的参数,
	 *   可以为 HP, AT, DF, SA, SD, SP.
	 * @param statIV
	 *   设置的努力值
	 * @since v0.2.2
	 */
	public void setStatEV(int item, int statEV) {
		this.statEV[item] = (byte) statEV;
	}

	public short[] getStat() {
		return stat;
	}
	
	/**
	 * <p>设置能力值</p>
	 * <p>当输入参数为 -1 时, 为不改变.</p>
	 * @param stat0
	 *   HP 能力值
	 * @param stat1
	 *   攻击能力值
	 * @param stat2
	 *   防御能力值
	 * @param stat3
	 *   特攻能力值
	 * @param stat4
	 *   特防能力值
	 * @param stat5
	 *   速度能力值
	 * @since v0.2.2
	 */
	public void setStat(int stat0, int stat1, int stat2,
			int stat3, int stat4, int stat5) {
		if (stat0 != -1) {
			stat[0] = (short) stat0;
		}
		if (stat1 != -1) {
			stat[1] = (short) stat1;
		}
		if (stat2 != -1) {
			stat[2] = (short) stat2;
		}
		if (stat3 != -1) {
			stat[3] = (short) stat3;
		}
		if (stat4 != -1) {
			stat[4] = (short) stat4;
		}
		if (stat5 != -1) {
			stat[5] = (short) stat5;
		}
	}
	
	/**
	 * <p>设置单项能力值</p>
	 * @param item
	 *   具体哪项数据
	 *   在 {@link IPokemonDataType} 中, 设置了具体的参数,
	 *   可以为 HP, AT, DF, SA, SD, SP.
	 * @param statIV
	 *   设置的能力值
	 * @since v0.2.2
	 */
	public void setStat(int item, int stat) {
		this.stat[item] = (short) stat;
	}

	public byte[] getStatContest() {
		return statContest;
	}

	public void setStatContest(byte[] statContest) {
		this.statContest = statContest;
	}
	
	/**
	 * <p>设置表演参数</p>
	 * <p>当输入参数为 -1 时, 为不改变.</p>
	 * @param c0
	 * @param c1
	 * @param c2
	 * @param c3
	 * @param c4
	 * @param c5
	 * @since v0.2.2
	 */
	public void setStatContest(int c0, int c1, int c2, int c3, int c4, int c5) {
		if (c0 != -1) {
			statContest[5] = (byte) c0;
		}
		if (c1 != -1) {
			statContest[0] = (byte) c1;
		}
		if (c2 != -1) {
			statContest[1] = (byte) c2;
		}
		if (c3 != -1) {
			statContest[2] = (byte) c3;
		}
		if (c4 != -1) {
			statContest[3] = (byte) c4;
		}
		if (c5 != -1) {
			statContest[4] = (byte) c5;
		}
	}

	public byte getAbnormal() {
		return abnormal;
	}

	/**
	 * <p>设置异常状态码</p>
	 * @see com.zdream.pmw.core.tools.AbnormalMethods
	 * @param abnormal
	 */
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
	 * 技能 PP 上限提升次数  skill PP ups
	 * 值域 0-3，非负
	 */
	private byte[] skillPPUps = new byte[SKILL_LENGTH];
	
	public short[] getSkill() {
		return skill;
	}
	
	/**
	 * 设置技能
	 * @param index
	 *   怪兽技能格中的位置, 从 0 开始
	 * @param skill
	 *   技能号码
	 * @since v0.2.2
	 */
	public void setSkill(int index, int skill) {
		this.skill[index] = (short) skill;
	}
	
	public byte[] getSkillPP() {
		return skillPP;
	}
	
	/**
	 * 设置技能 PP
	 * @param index
	 *   怪兽技能格中的位置, 从 0 开始
	 * @param skillPP
	 *   技能 PP, 值域 0-64，非负
	 * @since v0.2.2
	 */
	public void setSkillPP(int index, int skillPP) {
		this.skillPP[index] = (byte) skillPP;
	}

	public byte[] getSkillPPUps() {
		return skillPPUps;
	}
	
	/**
	 * 设置技能 PP 上限提升次数
	 * @param index
	 *   怪兽技能格中的位置, 从 0 开始
	 * @param skillPPUps
	 *   技能 PP 上限提升次数, 值域 0-3，非负
	 * @since v0.2.2
	 */
	public void setSkillPPUps(int index, int skillPPUps) {
		this.skillPPUps[index] = (byte) skillPPUps;
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

	public void setTrainerMarkers(int index, boolean m) {
		trainerMarkers[index] = m;
	}

	/* ************
	 *	简单方法  *
	 ************ */
	/*
	 * v0.2.1
	 */
	
	/**
	 * 获得异常状态的类型<br>
	 * <p>如果想得到异常状态的编码, 请使用 {@code getAbnormal()}.</p>
	 * @return
	 *   异常状态的枚举类
	 * @since v0.2.1
	 */
	public EPokemonAbnormal getAbnormalType() {
		return AbnormalMethods.parseAbnormal(abnormal);
	}
	
	/**
	 * 确认该怪兽是否濒死
	 * @return
	 * @since v0.2.1
	 */
	public boolean isFainted() {
		return (hpi <= 0);
	}

	/* ************
	 *	  其它    *
	 ************ */

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

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		// 版本
		out.writeByte(1);
		
		out.writeShort(identifier);
		out.writeShort(speciesID);
		out.writeByte(form);
		out.writeInt(pid);
		out.writeShort(ability);
		
		byte b = (byte) nature.ordinal();
		b += (gender.ordinal() << 5);
		if (egg) {
			b |= 0x80;
		}
		out.writeByte(b);
		
		if (egg) {
			out.writeShort(happiness);
		} else {
			out.writeByte(happiness);
		}
		
		int i = experience;
		i += (level << 24);
		out.writeInt(i);
		out.writeShort(heldItem);
		
		byte[] bs = nickname.getBytes("UTF-8");
		out.writeByte(bs.length);
		out.write(bs);
		
		// TODO met 部分不写
		out.writeByte(0);
		
		// skill 部分
		for (; b < skill.length; b++) {
			if (skill[b] == 0) {
				break;
			}
		}
		out.writeByte(b);
		
		for (i = 0; i < b; i++) {
			out.writeShort(skill[i]);
		}
		for (i = 0; i < b; i++) {
			int j = skillPP[i] + (skillPPUps[i] << 6);
			out.writeByte(j);
		}
		
		// trainer 部分
		if (trainerName == null || trainerName.trim().length() == 0) {
			out.writeByte(0);
		} else {
			bs = trainerName.getBytes("UTF-8");
			out.writeByte(bs.length);
			out.write(bs);
			
			out.writeInt(trainerID);
			out.writeInt(trainerSID);
			b = 0;
			for (i = 0; i < trainerMarkers.length; i++) {
				if (trainerMarkers[i]) {
					b += (1 << i);
				}
			}
			b += (trainerGender.ordinal() << 6);
			out.writeByte(b);
		}
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		// 检查版本
		byte version = in.readByte();
		if (version != 1) {
			throw new ClassNotFoundException("the version of the pokemon data is not accept.");
		}
		
		identifier = in.readShort();
		speciesID = in.readShort();
		form = in.readByte();
		pid = in.readInt();
		ability = in.readShort();
		
		int b = in.readUnsignedByte();
		nature = EPokemonNature.values()[b & 0x1F];
		gender = EPokemonGender.values()[(b >> 5) & 3];
		if ((b >> 7) > 0) {
			egg = true;
		} else {
			egg = false;
		}
		
		if (egg) {
			happiness = in.readShort();
		} else {
			happiness = (short) in.readUnsignedByte();
		}
		
		int i = in.readInt();
		level = (byte) (i >> 24);
		experience = (i & 0xFFFFFF);
		heldItem = in.readShort();
		
		i = in.readUnsignedByte();
		byte[] bs = new byte[i];
		in.read(bs);
		nickname = new String(bs, "UTF-8");
		
		// TODO met 部分不读
		in.readByte();

		// skill 部分
		b = in.readByte();
		for (i = 0; i < b; i++) {
			skill[i] = in.readShort();
		}
		for (i = 0; i < b; i++) {
			int j = in.readUnsignedByte();
			skillPP[i] = (byte) (j & 0x3F);
			skillPPUps[i] = (byte) (j >> 6);
		}
		
		// trainer 部分
		b = in.readUnsignedByte();
		if (b != 0) {
			bs = new byte[b];
			in.read(bs);
			trainerName = new String(bs, "UTF-8");
			
			trainerID = in.readInt();
			trainerSID = in.readInt();
			b = in.readUnsignedByte();
			for (i = 0; i < trainerMarkers.length; i++) {
				int j = i % 2;
				b >>= 1;
				if (j > 0) {
					trainerMarkers[i] = true;
				} else {
					trainerMarkers[i] = false;
				}
			}
			trainerGender = EPokemonGender.values()[b];
		}
	}
}
