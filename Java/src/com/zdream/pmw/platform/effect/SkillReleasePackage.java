package com.zdream.pmw.platform.effect;

import java.util.HashMap;
import java.util.Map;

import com.zdream.pmw.monster.prototype.EPokemonType;
import com.zdream.pmw.platform.attend.Participant;
import com.zdream.pmw.platform.attend.SkillRelease;
import com.zdream.pmw.platform.effect.instruction.ConfirmSkillInstruction;
import com.zdream.pmw.platform.order.event.IEvent;
import com.zdream.pmw.util.json.JsonObject;

/**
 * <p>技能释放的数据包</p>
 * <p>里面存放了单次技能释放时包括原始输入、行动、攻击、附加效果判定
 * 的所有必要数据.</p>
 * 
 * <p><b>v0.1</b><br>
 * 里面用 field 的形式记录了单次技能释放时的必要数据.
 * 这个形式保留到版本 v0.2.2</p>
 * 
 * <p><b>v0.2</b><br>
 * 添加所需环境的属性</p>
 * 
 * <p><b>v0.2.1</b><br>
 * 该类将存储技能对应的计算公式</p>
 * 
 * <p><b>v0.2.2</b><br>
 * 系统补充了单回合多段攻击的技能, 因此如果出现这类攻击,
 * 对于每一轮攻击, 系统都将生成一个 {@code SkillReleasePackage}.<br>
 * 这个规定仅在 v0.2.2 中生效.</p>
 * 
 * <p><b>v0.2.3</b><br>
 * 由于系统的 effect 部分的运行流程做了非常大幅度的修改,
 * 因此该类也与 v0.2.2 版本时有了巨大的不同:
 * <li>放弃使用 field 的形式记录各类必要数据, 而是使用 {@link ReleaseReport} 类
 * 储存该类数据;
 * <li>删除了本类中的大部分 set 方法, 保留 get 方法,
 * get 方法将试图获取 {@link ReleaseReport} 中保留的数据;
 * <li>删除了本类中的环境引用, 该类也不再存储各种公式
 * ({@link com.zdream.pmw.platform.effect.IFormula}) 类.
 * 这些数据将存储在各类 {@link AInstruction} 中;
 * <li>单回合多段攻击的技能不再生成多个 {@code SkillReleasePackage} 实例.
 * 这是由于让 {@link ReleaseReport} 类存储数据后, {@code SkillReleasePackage} 支持
 * 存储更多非官方定义的数据, 因此多轮攻击允许写在
 * 一个 {@code SkillReleasePackage} 实例中;
 * </li></p>
 * 
 * @since v0.1 [2016-04-07]
 * @author Zdream
 * @version v0.2.3 [2017-05-08]
 */
public class SkillReleasePackage {
	
	/* ************
	 *	所需环境  *
	 ************ */
	/**
	 * 存放在技能判断、计算的执行过程中,
	 * 所有 {@link IEvent} 数据的地方.
	 * @since v0.2.3
	 */
	Map<String, IEvent> eves = new HashMap<>();
	
	/**
	 * 将名称与该事件关联起来
	 * @param key
	 *   名称
	 * @param eve
	 *   事件. 这些事件中存在的缓存信息将被告知到其它的事件中,
	 *   做到信息共享.
	 * @since v0.2.3
	 */
	void relate(String key, IEvent eve) {
		eves.put(key, eve);
		report.putHead(key);
	}
	
	/**
	 * 关联一个指挥
	 * @param inst
	 *   指挥. 这些指挥中存在的缓存信息将被告知到其它的事件中,
	 *   做到信息共享.
	 * @since v0.2.3
	 */
	void relate(AInstruction inst) {
		String key = inst.canHandle();
		relate(key, inst);
		
		// 注入配置参数
		Object o;
		if ((o = getData(DATA_KEY_PARAM)) != null) {
			JsonObject jo = (JsonObject) o;
			String[] ss = inst.em.getConfig("inj-param.inst." + key, key).split(",");
			for (int i = 0; i < ss.length; i++) {
				if ((o = jo.get(ss[i])) != null) {
					inst.set((JsonObject) o);
				}
			}
		}
	}
	
	/**
	 * 记录与报告生成器
	 */
	ReleaseReport report;
	
	public ReleaseReport getReport() {
		return report;
	}
	
	void putData(String key, Object value) {
		report.putData(key, value);
		
		switch (key) {
		case DATA_KEY_RANGE: {
			byte[] seats = (byte[]) value;
			// dfStaffs = new Participant[seats.length];
			report.putData(DATA_KEY_RANGE_LENGTH, seats.length);
			
			// not good
			setDfStaffLength(seats.length);
		} break;
		default:
			break;
		}
	}

	void putInfo(Object value) {
		report.putInfo(value);
	}
	
	public Object getData(String key) {
		return report.getData(key);
	}
	
	public Object[] getDataRecords(String key) {
		return report.getDataRecords(key);
	}
	
	/**
	 * 技能 ID, short
	 */
	public static final String DATA_KEY_SKILL_ID = "skillId";
	/**
	 * 能否行动, int
	 */
	public static final String DATA_KEY_MOVEABLE = "moveable";
	/**
	 * 目标的 seat 列表, byte[]
	 */
	public static final String DATA_KEY_RANGE = "range";
	/**
	 * 目标的 no 列表, byte[]<br>
	 * 与上面的 seat 列表的元素一一对应
	 */
	public static final String DATA_KEY_RANGE_NO = "range_no";
	/**
	 * 分配到每个 instruction 的参数, 含部分 formula, JsonObject
	 */
	public static final String DATA_KEY_PARAM = "_p";
	/**
	 * 目标的范围大小, int
	 */
	public static final String DATA_KEY_RANGE_LENGTH = "range_length";
	/**
	 * 技能释放的属性, int
	 */
	public static final String DATA_KEY_SKILL_TYPE = "type";
	/**
	 * 各个防御方对技能的免疫, boolean[]
	 */
	public static final String DATA_KEY_IMMUSES = "immuses";
	/**
	 * 技能命中率, 默认值为技能命中率, 0 - 100, float
	 */
	public static final String DATA_KEY_ACCURACY = "accuracy";
	/**
	 * 攻击方命中率, 默认 1.0f, float
	 */
	public static final String DATA_KEY_HITRATE = "hitrate";
	/**
	 * 防御方躲避率, 加算攻击方命中等级与防御方躲避等级, 默认 1.0f, float[]
	 */
	public static final String DATA_KEY_HIDES = "hides";
	/**
	 * 对于每个防御方, 攻击是否命中, boolean[]
	 */
	public static final String DATA_KEY_HITS = "hits";
	/**
	 * 确定命中的、有效的目标的 seat 列表, byte[]
	 */
	public static final String DATA_KEY_TARGETS = "targets";
	/**
	 * 会心等级, int[]
	 * 特殊数值: -1: 必定会心, -2: 必定不会心
	 */
	public static final String DATA_KEY_CRIT_LVS = "crit_levels";
	/**
	 * 是否会心一击, boolean[]
	 */
	public static final String DATA_KEY_CRITS = "crits";
	/**
	 * 攻击, 包括物攻或者特攻, float[]
	 */
	public static final String DATA_KEY_ATS = "ats";
	/**
	 * 威力, float[]
	 */
	public static final String DATA_KEY_POWERS = "powers";
	/**
	 * 防御, 包括物防或者特防, float[]
	 */
	public static final String DATA_KEY_DFS = "dfs";
	/**
	 * 属性克制的倍率, float[], 数组每一项跟随 targets 所指的目标
	 */
	public static final String DATA_KEY_TYPE_RATES = "type_rates";
	/**
	 * 其它修正的倍率, 不包括属性克制, float[], 数组每一项跟随 targets 所指的目标
	 */
	public static final String DATA_KEY_CORRECTS = "corrects";
	/**
	 * 伤害值, int[], 数组每一项跟随 targets 所指的目标
	 */
	public static final String DATA_KEY_DAMAGES = "damages";
	/**
	 * int, 在蓄力攻击等占用多个回合的攻击中, 该回合处于第几个回合
	 */
	public static final String DATA_KEY_PERIOD_COUNT = "period_count";
	/**
	 * int, 在单回合中预计多段攻击有几次
	 */
	public static final String DATA_KEY_MULTI_MAX = "multi_max";
	/**
	 * int, 现在是在单回合中多段攻击的第几次. 这个数据不会大于 {@link #DATA_KEY_MULTI_MAX}
	 */
	public static final String DATA_KEY_MULTI_COUNT = "multi_count";
	/**
	 * int, 单回合中多段攻击实际算数的有几次. 这个数据不会大于 {@link #DATA_KEY_MULTI_MAX}
	 */
	public static final String DATA_KEY_MULTI_ACTUAL_COUNT = "multi_actual";

	/* ************
	 *	数据结构  *
	 ************ */
	
	/*
	 * 技能发动的原始数据
	 * 攻击方 no（byte）
	 * 选择技能序号（int）
	 * 原始目标（byte）
	 * v0.2.3
	 */
	byte atno;
	int skillNum;
	byte originTarget;
	int round;
	
	/**
	 * @reurn
	 *   攻击方怪兽的 no 号
	 */
	public byte getAtno() {
		return atno;
	}
	
	/**
	 * 该释放的技能是怪兽的第几个技能
	 * @return
	 */
	public int getSkillNum() {
		return skillNum;
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
	
	/**
	 * 这是在第几回合释放的
	 * @return
	 * @since v0.2.3
	 */
	public int getRound() {
		return round;
	}
	
	/**
	 * 设置原始的命令, 包括原攻击方、选择的技能序号、原始选择的目标
	 * @param no
	 *   原攻击方 no
	 * @param skillNum
	 *   原选择的技能, 在该攻击方怪兽
	 * @param originTarget
	 */
	void setOriginCommand(byte no, int skillNum, byte originTarget) {
		this.atno = no;
		this.skillNum = skillNum;
		this.originTarget = originTarget;
		report.putHead("origin");
		report.putData("atno", no);
		report.putData("skillNum", skillNum);
		report.putData("originTarget", originTarget);
	}

	/**
	 * 返回防御方个数
	 */
	public int dfStaffLength() {
		return (int) report.getData(DATA_KEY_RANGE_LENGTH);
	}

	/**
	 * 返回判定的防御方个数<br>
	 * 这个数量将排除攻击免疫与未命中的对象.
	 */
	public int targetsLength() {
		Object o = report.getData(DATA_KEY_TARGETS);
		if (o == null) {
			return 0;
		}
		
		return ((byte[]) o).length;
	}
	
	/**
	 * 设置防御方的个数
	 * @param length
	 */
	public void setDfStaffLength(int length) {
		dfStaffs = new Participant[length];
	}
	
	/*
	 * 攻击方（Participant）
	 * 防御方列表（Participant[]）
	 * 技能释放体（SkillRelease）
	 */
	Participant atStaff;
	Participant[] dfStaffs;
	SkillRelease skr;
	
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
		return ((byte[]) report.getData(DATA_KEY_RANGE));
	}

	public void setDfStaff(Participant dfStaff, int index) {
		this.dfStaffs[index] = dfStaff;
	}
	
	/**
	 * 释放的技能 SkillRelease
	 * @return
	 */
	public SkillRelease getSkill() {
		if (skr != null) {
			return skr;
		}
		IEvent eve = eves.get(AInstruction.CODE_CONFIRM_SKILL);
		if (eve == null) {
			return null;
		}
		return skr = ((ConfirmSkillInstruction) eve).getSkr();
	}
	
	/**
	 * 释放的技能 Id
	 * @return
	 * @since v0.2.3
	 */
	public short getSkillId() {
		Object o = getData(DATA_KEY_SKILL_ID);
		if (o != null) {
			return (short) o;
		}
		return -1;
	}
	
	/**
	 * 能否行动的参数
	 */
	public static final int
			MOVEABLE_UNKNOWED = 0,
			MOVEABLE_TRUE = 1,
			MOVEABLE_FALSE = 2;
	
	public int getMoveable() {
		Object o = report.getData(DATA_KEY_MOVEABLE);
		if (o == null) {
			return MOVEABLE_UNKNOWED;
		} else {
			return (int) o;
		}
	}
	
	/**
	 * 攻击方能否行动
	 * @return
	 */
	public boolean isMoveable() {
		return getMoveable() == MOVEABLE_TRUE;
	}
	
	/**
	 * 技能释放范围的 seat 集合
	 * @return
	 * @since v0.2.3
	 */
	public byte[] getRanges() {
		return ((byte[]) report.getData(DATA_KEY_RANGE));
	}
	
	/**
	 * 在技能目标中第 index 个防御方的 seat
	 * @param index
	 *   从 0 开始
	 * @return
	 */
	public byte getRange(int index) {
		return ((byte[]) report.getData(DATA_KEY_RANGE))[index];
	}
	
	/**
	 * 在技能目标中当前判定的所有 seat 集合, 可能和 range 不同
	 * @return
	 */
	public byte[] getTargets() {
		return ((byte[]) report.getData(DATA_KEY_TARGETS));
	}
	
	/**
	 * 攻击方的命中率
	 * @return
	 */
	public float getAtStaffAccuracy(int index) {
		return ((float) report.getData(DATA_KEY_HITRATE));
	}

	/**
	 * 在判定技能目标中第 index 个防御方时，对它释放技能时该防御方的躲避率<br>
	 * @param index
	 *   该索引指向 targets 数组中 dfseat 的位置,
	 *   从 0 开始
	 * @return
	 */
	public float getDfStaffHide(int index) {
		return ((float[]) report.getData(DATA_KEY_HIDES))[index];
	}
	
	/**
	 * 技能的命中率
	 * @return
	 */
	public float getSkillAccuracy() {
		return ((float) report.getData(DATA_KEY_ACCURACY));
	}

	/**
	 * 是否命中了第 index 个防御方
	 * @param index
	 *   该索引指向 targets 数组中 dfseat 的位置,
	 *   从 0 开始
	 * @return
	 */
	public boolean isHitable(int index) {
		return ((boolean[]) report.getData(DATA_KEY_HITS))[index];
	}

	/**
	 * 是否对第 index 个防御方命中要害
	 * @param index
	 *   该索引指向 targets 数组中 dfseat 的位置,
	 *   从 0 开始
	 * @return
	 */
	public boolean isCtable(int index) {
		Object o = getData(DATA_KEY_CRITS);
		if (o == null) {
			return false;
		}
		
		return ((boolean[]) o)[index];
	}
	
	/**
	 * 释放技能的真正属性
	 * @return
	 */
	public EPokemonType getType() {
		Object o = getData(DATA_KEY_SKILL_TYPE);
		if (o == null) {
			return getSkill().getType();
		}
		
		return EPokemonType.values()[(int) o];
	}

	/**
	 * 对第 index 个防御方的技能属性克制倍率
	 * @param index
	 *   该索引指向 targets 数组中 dfseat 的位置,
	 *   从 0 开始
	 * @return
	 */
	public float getRate(int index) {
		Object o = getData(DATA_KEY_TYPE_RATES);
		if (o == null) {
			return -1;
		}
		
		return ((float[]) o)[index];
	}
	
	/**
	 * 对第 index 个防御方的技能属性克制效果
	 * @param index
	 *   该索引指向 targets 数组中 dfseat 的位置,
	 *   从 0 开始
	 * @return
	 *   <li>0: 普通的效果;
	 *   <li>1: 效果拔群;
	 *   <li>-1: 效果很小 / 一般;
	 *   <li>-2: 无效 (由于 targets 过滤掉免疫的对象, 一般不会返回这个数据)</li>
	 * @since v0.2.3
	 */
	public int typeRateEffect(int index) {
		float rate = getRate(index);
		
		if (rate == 1 || rate == -1) {
			return 0;
		} else if (rate > 1) {
			return 1;
		} else if (rate < 1 && rate > 0) {
			return -1;
		}
		return -2;
	}

	/**
	 * 对第 index 个防御方的其它伤害修正
	 * @param index
	 *   该索引指向 targets 数组中 dfseat 的位置,
	 *   从 0 开始
	 * @return
	 */
	public float getCorrect(int index) {
		Object o = getData(DATA_KEY_CORRECTS);
		if (o == null) {
			return -1;
		}
		
		return ((float[]) o)[index];
	}

	/**
	 * 对第 index 个防御方攻击时，攻击方攻击 / 特攻的实时值
	 * @param index
	 *   该索引指向 targets 数组中 dfseat 的位置,
	 *   从 0 开始
	 * @return
	 */
	public float getAt(int index) {
		Object o = getData(DATA_KEY_ATS);
		if (o == null) {
			return -1;
		}
		
		return ((float[]) o)[index];
	}
	
	/**
	 * 对第 index 个防御方攻击时，防御方防御 / 特防的实时值
	 * @param index
	 *   该索引指向 targets 数组中 dfseat 的位置,
	 *   从 0 开始
	 * @return
	 */
	public float getDf(int index) {
		Object o = getData(DATA_KEY_DFS);
		if (o == null) {
			return -1;
		}
		
		return ((float[]) o)[index];
	}

	/**
	 * 技能威力的实时值
	 * @param index
	 *   该索引指向 targets 数组中 dfseat 的位置,
	 *   从 0 开始
	 * @return
	 */
	public float getPower(int index) {
		Object o = getData(DATA_KEY_POWERS);
		if (o == null) {
			return -1;
		}
		
		return ((float[]) o)[index];
	}
	
	/**
	 * 对第 index 个防御方攻击时，造成的伤害
	 * @param index
	 *   该索引指向 targets 数组中 dfseat 的位置,
	 *   从 0 开始
	 * @return
	 */
	public int getDamage(int index) {
		Object o = getData(DATA_KEY_DAMAGES);
		if (o == null) {
			return -1;
		}
		
		return ((int[]) o)[index];
	}

	/**
	 * <p>通知这个 SkillReleasePackage 一个消息, 它将被存储到 om.recoder 中.
	 * <p>因此在这个时间点中, SkillReleasePackage 中的所有的数据将不能被修改,
	 * 所有的环境引用需要被清除, 只允许留有数据.
	 * <p>该方法由 om 模块调用.</p>
	 * @param round
	 *   当前的回合数
	 * @since v0.2.3
	 */
	public void store(int round) {
		this.round = round;
		this.skr = null;
		this.atStaff = null;
		this.dfStaffs = null;
	}
	
	
	/* ************
	 *	构造方法  *
	 ************ */

	public SkillReleasePackage() {
		report = new ReleaseReport();
	}

}
