package com.zdream.pmw.platform.effect;

import com.zdream.pmw.monster.prototype.EPokemonType;
import com.zdream.pmw.monster.prototype.IPokemonDataType;
import com.zdream.pmw.platform.attend.AttendManager;
import com.zdream.pmw.platform.attend.Participant;
import com.zdream.pmw.platform.attend.SkillRelease;
import com.zdream.pmw.platform.control.IMessageCode;
import com.zdream.pmw.platform.control.IPrintLevel;
import com.zdream.pmw.platform.effect.damage.IDamageFormula;
import com.zdream.pmw.util.common.ArraysUtils;
import com.zdream.pmw.util.json.JsonValue;

/**
 * 技能发动的释放计算平台<br>
 * <br>
 * <b>v0.2</b><br>
 *   添加计算数值板块<br>
 * <br>
 * <b>v0.2.1</b>
 * <p>修改了计算命中和躲避的方法, 添加<b>必中</b>/<b>必不中</b>这类特殊返回值</p>
 * 
 * @since v0.1
 * @author Zdream
 * @date 2016年4月7日
 * @version v0.2.1
 */
public class SkillReleaseBox implements IPokemonDataType {
	
	/* ************
	 *	判定流程  *
	 ************ */
	/*
	 * 1. 能够行动（判定成功后扣 PP）
	 * 2. 范围判断
	 * 3. 命中判定
	 * 4. 伤害计算
	 * 5. 伤害反馈
	 * 6. 附加效果
	 * 7. 反作用效果
	 */
	
	/**
	 * 技能行动释放
	 * @param no
	 *   攻击方的 no
	 * @param skillNum
	 *   选择的技能号码（索引）
	 * @param originTarget
	 *   原始目标数据<br>
	 * 例如训练师下达命令中，选择的对象，可能为 -1
	 */
	public void moveAct(byte no, byte skillNum, byte originTarget) {
		moveAct(no, skillNum, originTarget, null);
	}
	
	/**
	 * <p>技能行动释放, 并加入选项参数, 来修改该技能的计算方式</p>
	 * <p>举个例子, 蓄力技释放时的第二回合, 技能的参数与第一回合不同, 也不需要扣 PP 值,
	 * 因此需要进行修正.</p>
	 * <p>当不需要选项参数时, 请使用 {@link #moveAct(byte, byte, byte)}</p>
	 * @param no
	 *   攻击方的 no
	 * @param skillNum
	 *   选择的技能号码（索引）
	 * @param originTarget
	 *   原始目标数据<br>
	 *   例如训练师下达命令中，选择的对象，可能为 -1
	 * @param param
	 *   选项, 用于修改该技能的计算方式. 默认为空.<br>
	 *   格式:
	 */
	public void moveAct(byte no, byte skillNum, byte originTarget, JsonValue param) {
		initMoveAct(no, skillNum, originTarget);
		
		// 确定释放技能
		releaseSkill();
		
		confirmFormula(param);
		
		// v0.2.1 后, 将判断能否行动移到确定释放技能之后
		if (!canMove()) {
			return;
		}
		
		// 扣 PP（这里不考虑压力特性）
		ppSubForRelease(pack, param);
		
		// 写入范围
		writeRange();
		
		// 发布将要释放技能的预告
		sendMessageForRelease();
		
		// 伤害判定
		writeDamage();
		
		// v0.2.2
		em.getRoot().getOrderManager().storeReleasePackage(pack);
	}

	/**
	 * 技能发动前的初始化<br>
	 * 基本数据的存放
	 * @param no
	 * @param skillNum
	 * @param originTarget
	 */
	private void initMoveAct(byte no, byte skillNum, byte originTarget) {
		AttendManager am = em.getAttends();
		pack = new SkillReleasePackage();
		pack.initEnvironment(em.getRoot());
		
		Participant atStaff = am.getParticipant(am.seatForNo(no));
		pack.setAtStaff(atStaff);
		
		short skillID = atStaff.getAttendant().getSkill()[skillNum];
		pack.setSkill(am.getSkillRelease(skillID));
		pack.setSkillNum(skillNum);
		
		pack.setOriginTarget(originTarget);
	}
	
	/**
	 * 确定技能之后, 确定技能释放时所使用的公式<br>
	 * @param param
	 *   选项参数
	 */
	private void confirmFormula(JsonValue param) {
		SkillRelease skill = pack.getSkill();
		if (param == null) {
			em.loadFormula(pack, skill);
			return;
		}
		JsonValue v = param.getMap().get("formula");
		em.loadFormula(pack, skill, v);
	}
	
	/**
	 * 行动判定<br>
	 * @return
	 */
	private boolean canMove() {
		boolean moveable = pack.getMovableFormula().canMove(pack);
		pack.setMoveable(moveable);
		return moveable;
	}
	
	/**
	 * 确定释放技能
	 */
	private void releaseSkill() {
		byte seat = pack.getAtStaff().getSeat();
		Aperitif value = em.newAperitif(Aperitif.CODE_CONFIRM_SKILL, seat);
		
		short skillID = em.getAttends().getParticipant(seat).getAttendant()
				.getSkill()[pack.getSkillNum()];
		value.append("seat", seat);
		value.append("skillID", skillID);
		em.getRoot().readyCode(value);
		
		// 补充 v0.2.1
		// 像混乱触发、变身等状态触发时, 能够修改释放的技能
		short newSkillID = (Short) value.get("skillID");
		if (newSkillID != skillID) {
			pack.setSkill(em.getAttends().getSkillRelease(newSkillID));
		}
	}
	
	/**
	 * 由于释放技能而扣 PP
	 * @param param
	 */
	private void ppSubForRelease(SkillReleasePackage pack, JsonValue param) {
		int value = 1;
		if (param != null) {
			JsonValue v = param.getMap().get("ppSub");
			if (v != null) {
				value = (int) v.getValue();
				if (value == 0) {
					return;
				}
			}
		}
		
		byte seat = pack.getAtStaff().getSeat();
		Aperitif ap = em.newAperitif(Aperitif.CODE_PP_SUB, seat);
		
		ap.append("seat", seat);
		ap.append("skillNum", pack.getSkillNum());
		ap.append("skillID", pack.getSkill().getId());
		ap.append("value", value);
		em.getRoot().readyCode(ap);
	}

	/**
	 * 计算范围并写入释放数据包<br>
	 * 该方法的结果是释放数据包中的目标列表被写入
	 */
	private void writeRange() {
		/*AttendManager am = em.getAttends();
		
		byte[] defaultSeats = em.defaultSkillRange(
				pack.getAtStaff().getSeat(), 
				pack.getSkill().getSkill().getId());
		byte[] seats = new byte[defaultSeats.length];
		int count = 0;
		
		for (int i = 0; i < defaultSeats.length; i++) {
			if (am.noForSeat(defaultSeats[i]) != -1) {
				count++;
			}
		}
		
		if (count != 0) {
			seats = new byte[count];
			int index = 0; // 这是 seats 的索引
			for (int i = 0; i < defaultSeats.length; i++) {
				byte seat = defaultSeats[i];
				if (am.noForSeat(seat) != -1) {
					seats[index ++] = seat;
				}
			}
		} else {
			seats = defaultSeats;
		}*/
		
		byte[] seats = pack.getRangeFormula().range(pack);

		Aperitif value = em.newAperitif(Aperitif.CODE_JUDGE_RANGE);
		value.append("seat", pack.getAtStaff().getSeat());
		value.append("skill", pack.getSkill().getId());
		value.append("result", ArraysUtils.bytesToString(seats));
		em.getRoot().readyCode(value);
		
		seats = ArraysUtils.StringToBytes(
				value.getMap().get("result").getString());
		pack.setTargets(seats);
		
		for (int i = 0; i < seats.length; i++) {
			pack.setDfStaff(em.getAttends().getParticipant(seats[i]), i);
		}
	}

	/**
	 * 发布将要释放技能的开胃酒消息
	 */
	private void sendMessageForRelease() {
		byte seat = pack.getAtStaff().getSeat();
		Aperitif value = em.newAperitif(Aperitif.CODE_RELEASE_SKILL, seat);
		
		short skillID = em.getAttends().getParticipant(seat).getAttendant()
				.getSkill()[pack.getSkillNum()];
		value.append("seat", seat);
		value.append("skillID", skillID);
		em.getRoot().readyCode(value);
	}
	
	/**
	 * 判定伤害并写入释放数据包<br>
	 * 该方法的结果是释放数据包中的
	 * 是否命中、会心、攻击力、防御力、技能威力等均被写入<br>
	 */
	private void writeDamage() {
		IDamageFormula formula = pack.getDamageFormula();
		formula.damage(pack);
	}
	
	/**
	 * 返回释放技能的 ID
	 * @return
	 */
	private short getSkillID() {
		return pack.getSkill().getSkill().getId();
	}
	
	/**
	 * 返回释放技能的 ID
	 * @return
	 */
	private short getSkillID(SkillReleasePackage pack) {
		return pack.getSkill().getSkill().getId();
	}
	
	/* ************
	 *	计算数值  *
	 ************ */
	/*
	 * @version 0.2
	 */
	/**
	 * 判定行动 (未调用) TODO
	 */
	public boolean judgeMoveable(SkillReleasePackage pack) {
		return true;
	}
	
	/**
	 * 计算攻击方实时命中率（忽略能力变化）
	 * @return v0.2.1
	 *   当返回数值为负数 (-1) 则为必中
	 */
	public float calcHitrate(SkillReleasePackage pack) {
		byte atseat = pack.getAtStaff().getSeat();
		
		Aperitif value = em.newAperitif(Aperitif.CODE_CALC_HITRATE);
		value.append("seat", atseat);
		value.append("result", 1.0f);
		value.append("abs", false);
		em.startCode(value);
		
		boolean abs = (boolean) value.get("abs");
		if (abs == true) {
			return -1.0f;
		}
		
		return (float) value.get("result");
	}
	
	/**
	 * 计算防御方实时躲避率（忽略能力变化）
	 * @return v0.2.1
	 *   当返回数值为负数 (-1) 则为必不中
	 */
	public float calcHide(SkillReleasePackage pack) {
		byte dfseat = pack.getDfStaff(pack.getThiz()).getSeat();
		
		Aperitif ap = em.newAperitif(Aperitif.CODE_CALC_HIDE, dfseat);
		ap.append("seat", dfseat);
		ap.append("result", 1.0f);
		ap.append("abs", false);
		em.startCode(ap);
		
		boolean abs = (boolean) ap.get("abs");
		if (abs == true) {
			return -1.0f;
		}
		
		return (float) ap.get("result");
	}
	
	/**
	 * 计算技能实时命中率
	 * @return
	 */
	public float calcAccuracy(SkillReleasePackage pack) {
		short skillID = getSkillID(pack);
		byte atseat = pack.getAtStaff().getSeat();
		byte dfseat = pack.getDfStaff(pack.getThiz()).getSeat();
		float base = pack.getAccuracyFormula().accuracy(pack);
		
		Aperitif value = em.newAperitif(Aperitif.CODE_CALC_ACCURACY, atseat, dfseat);
		value.append("atseat", atseat);
		value.append("dfseat", dfseat);
		value.append("skill", skillID);
		value.append("result", base);
		value.append("abs", 0);
		em.startCode(value);
		
		return (float) value.get("result");
	}
	
	/**
	 * 判定释放技能的属性<br>
	 * @return
	 */
	public EPokemonType calcSkillType(SkillReleasePackage pack) {
		String type = pack.getSkill().getSkill().getType().name();
		byte seat = pack.getAtStaff().getSeat();

		Aperitif value = em.newAperitif(Aperitif.CODE_CALC_TYPE, seat);
		value.append("seat", seat);
		value.append("skill", getSkillID());
		value.append("result", type);
		em.startCode(value);

		type = (String) value.get("result");
		return EPokemonType.parseEnum(type);
	}
	
	/**
	 * 判定克制倍率, 返回对于敌方每个属性, 我方的技能属性的克制倍率
	 * @param pack
	 * @return
	 *  数组的大小 = 对应敌方怪兽的属性多少
	 */
	public float[] calcTypeRate(SkillReleasePackage pack) {
		final EPokemonType[] dfTypes = pack.getDfStaff(pack.getThiz()).getTypes();
		float[] results = new float[dfTypes.length];
		EPokemonType atType = pack.getType(); // 攻击方的属性
		EPokemonType dfType;
		float rate; // 缓存倍率
		
		for (int j = 0; j < dfTypes.length; j++) {
			dfType = dfTypes[j];

			rate = pack.getEffects().getRestraintRate(atType, dfType);
			byte atseat = pack.getAtStaff().getSeat(),
					dfseat = pack.getDfStaff(pack.getThiz()).getSeat();

			Aperitif value = em.newAperitif(Aperitif.CODE_CALC_TYPE_RATE, atseat, dfseat);
			value.append("atseat", atseat);
			value.append("dfseat", dfseat);
			value.append("atType", atType.name());
			value.append("dfType", dfType.name());
			value.append("result", rate);
			em.startCode(value);

			results[j] = (float) value.get("result");
		}
		return results;
	}
	
	/**
	 * 计算会心一击等级
	 * @param pack
	 * @return
	 *   当数值大于等于零, 表示会心一击等级;<br>
	 *   当数值为 -2, 表示绝对回避会心;<br>
	 *   当数值为 -1, 表示绝对会心;<br>
	 */
	public byte calcCt(SkillReleasePackage pack) {
		byte atseat = pack.getAtStaff().getSeat(),
				dfseat = pack.getDfStaff(pack.getThiz()).getSeat();
		
		Aperitif value = em.newAperitif(Aperitif.CODE_CALC_CT, atseat, dfseat);
		value.append("atseat", atseat);
		value.append("dfseat", dfseat);
		value.append("result", pack.getSkill().getCritLevel());
		value.append("abs", 0);
		em.startCode(value);
		
		int abs = (int) value.get("abs");
		switch (abs) {
		case 1:
			return (byte) -1;
		case 2:
			return (byte) -2;
		default:
			return (byte) value.get("result");
		}
	}
	
	/**
	 * 计算技能威力
	 * @param pack
	 * @return
	 */
	public int calcPower(SkillReleasePackage pack) {
		float power = pack.getPowerFormula().power(pack);
		byte atseat = pack.getAtStaff().getSeat();
		
		Aperitif value = em.newAperitif(Aperitif.CODE_CALC_POWER, atseat);
		value.append("skillID", getSkillID(pack));
		value.append("atseat", atseat);
		value.append("dfseat", pack.getDfStaff(pack.getThiz()).getSeat());
		value.append("result", power);
		em.startCode(value);
		
		power = (float) value.get("result");
		return (int) power;
	}
	
	/**
	 * 计算最后的伤害修正 (状态等修正)<br>
	 * 这里不包括乱数修正等<br>
	 * @param pack
	 */
	public float calcCorrect(SkillReleasePackage pack) {
		Aperitif value = em.newAperitif(Aperitif.CODE_CALC_CORRENT,
				pack.getAtStaff().getSeat(), pack.getDfStaff(pack.getThiz()).getSeat());
		
		value.append("atseat", pack.getAtStaff().getSeat());
		value.append("dfseat", pack.getDfStaff(pack.getThiz()).getSeat());
		value.append("skillID", getSkillID(pack));
		value.append("result", pack.getCorrect(pack.getThiz()));
		pack.getEffects().startCode(value);
		
		return (float) value.get("result");
	}
	
	/**
	 * 计算最后伤害
	 * @param pack
	 * @return
	 */
	public int calcDamage(SkillReleasePackage pack) {
		byte dfseat = pack.getDfStaff(pack.getThiz()).getSeat();
		
		Aperitif value = em.newAperitif(Aperitif.CODE_CALC_DAMAGE, dfseat);
		value.append("atseat", pack.getAtStaff().getSeat());
		value.append("dfseat", dfseat);
		value.append("skillID", getSkillID(pack));
		value.append("result", pack.getDamage(pack.getThiz()));
		pack.getEffects().startCode(value);
		
		return (int) value.get("result");
	}
	
	/**
	 * 获得精灵的实时能力<br>
	 * 能力包括攻击、防御、特攻、特防、速度、命中、躲避 7 项<br>
	 * 其中命中、躲避 2 项不允许使用<br>
	 * @param seat
	 *   指定的精灵的 seat
	 * @param item
	 *   需要查看精灵的能力项<br>
	 *   能力项在 <code>IPokemonDataType</code> 中定义
	 * @return
	 *   实时的能力值<br>
	 */
	public int calcAbility(byte seat, int item) {
		return calcAbility(seat, item, 0);
	}

	/**
	 * 获得精灵的实时能力，无视部分能力等级的计算<br>
	 * 能力包括攻击、防御、特攻、特防、速度 5 项<br>
	 * 命中、躲避 2 项不允许使用<br>
	 * @param seat
	 *   指定的精灵的 seat
	 * @param item
	 *   需要查看精灵的能力项<br>
	 *   能力项在 <code>IPokemonDataType</code> 中定义
	 * @param ignore
	 *   忽略的能力等级<br>
	 *   当参数为 0 时，不忽略任何能力等级，其做法与 realTimeAbility(2) 相同<br>
	 *   当参数为 1 时，忽略提高的能力等级，当该精灵能力等级大于 0 时忽略能力等级计算<br>
	 *   当参数为 2 时，忽略降低的能力等级，当该精灵能力等级小于 0 时忽略能力等级计算<br>
	 * @return
	 *   实时的能力值<br>
	 */
	public int calcAbility(byte seat, int item, int ignore) {
		return calcAbility(seat, item, ignore, true);
	}
	
	/**
	 * 获得精灵的实时能力，无视部分能力等级的计算<br>
	 * 能力包括攻击、防御、特攻、特防、速度 5 项<br>
	 * 命中、躲避 2 项不允许使用<br>
	 * @param seat
	 *   指定的精灵的 seat
	 * @param item
	 *   需要查看精灵的能力项<br>
	 *   能力项在 <code>IPokemonDataType</code> 中定义
	 * @param ignore
	 *   忽略的能力等级<br>
	 *   当参数为 0 时，不忽略任何能力等级，其做法与 realTimeAbility(2) 相同<br>
	 *   当参数为 1 时，忽略提高的能力等级，当该精灵能力等级大于 0 时忽略能力等级计算<br>
	 *   当参数为 2 时，忽略降低的能力等级，当该精灵能力等级小于 0 时忽略能力等级计算<br>
	 *   当参数为 3 时，忽略所有能力等级变化<br>
	 * @param alter
	 *   是否添加其它状态等造成的影响, 默认为 true<br>
	 *   比如在混乱状态时, 是不计算装备、其它状态对能力值的影响的<br>
	 * @return
	 *   实时的能力值<br>
	 */
	public int calcAbility(byte seat, int item, int ignore, boolean alter) {
		AttendManager am = em.getAttends();
		
		int base = am.getParticipant(seat).getStat(item);
		String head;
		
		switch (item) {
		case AT:
			head = IMessageCode.CODE_CALC_AT;
			break;
		case DF:
			head = IMessageCode.CODE_CALC_DF;
			break;
		case SA:
			head = IMessageCode.CODE_CALC_SA;
			break;
		case SD:
			head = IMessageCode.CODE_CALC_SD;
			break;
		case SP:
			head = IMessageCode.CODE_CALC_SP;
			break;
		default:
			throw new IllegalArgumentException("Illegal Item: " + item);
		}
		
		em.logPrintf(IPrintLevel.PRINT_LEVEL_VERBOSE,
				"em.SkillReleaseBox(4): %s 的原始 %d 数据为 %d",
				am.getParticipant(seat).getNickname(), item, base);
		
		Aperitif ap = em.newAperitif(head, seat);
		ap.append("value", base).append("rate", 1.0f).append("seat", seat);
		
		if (alter == false) {
			ap.append("alter", false);
		}
		em.startCode(ap);
		
		float result = (Float) ap.get("rate");
		int v = (Integer) ap.get("value");
		return (int) (v * result);
	}
	
	/* ************
	 *	数据结构  *
	 ************ */
	/**
	 * 释放数据包
	 */
	private SkillReleasePackage pack;

	/**
	 * 获得当前进行攻击判定的数据包
	 * @return
	 * @since v0.2.1
	 */
	public SkillReleasePackage getCurrentPackage() {
		return pack;
	}
	
	/**
	 * <p>替换 {@code SkillReleasePackage}.</p>
	 * <p>用新建的 package 的替换现在正在使用的 package.
	 * 会将旧有的 package 存入仓库, 然后将初始化一个新的 packge 返回.</p>
	 * <p>一般在多段攻击时, 每一轮攻击需要生成一个 package, 需要再此替换 package.</p>
	 * @param oldPack
	 *   原有的 package
	 * @return
	 *   新建的 package
	 * @since v0.2.2
	 */
	public SkillReleasePackage replacePackage(SkillReleasePackage oldPack) {
		SkillReleasePackage pack = new SkillReleasePackage();
		pack.initEnvironment(em.getRoot());
		
		pack.setAtStaff(oldPack.getAtStaff());
		pack.setSkill(oldPack.getSkill());
		pack.setSkillNum(oldPack.getSkillNum());
		pack.setOriginTarget(oldPack.getOriginTarget());
		
		pack.setAccuracyFormula(oldPack.getAccuracyFormula());
		pack.setAdditionFormulas(oldPack.getAdditionFormulas());
		pack.setDamageFormula(oldPack.getDamageFormula());
		pack.setPowerFormula(oldPack.getPowerFormula());
		pack.setMovableFormula(oldPack.getMovableFormula());
		
		pack.setMoveable(oldPack.isMoveable());
		
		final int length = oldPack.dfStaffLength();
		
		byte[] targets = new byte[length];
		for (int i = 0; i < length; i++) {
			targets[i] = oldPack.getTarget(i);
		}
		pack.setTargets(targets);
		for (int i = 0; i < length; i++) {
			pack.setDfStaff(oldPack.getDfStaff(i), i);
		}
		
		em.getRoot().getOrderManager().storeReleasePackage(oldPack);
		this.pack = pack;
		
		return pack;
	}
	
	/* ************
	 *	构造函数  *
	 ************ */
	
	EffectManage em;

	public SkillReleaseBox(EffectManage em) {
		this.em = em;
	}

}
