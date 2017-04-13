package com.zdream.pmw.platform.effect.state;

import com.zdream.pmw.core.tools.AbnormalMethods;
import com.zdream.pmw.monster.prototype.EPokemonAbnormal;
import com.zdream.pmw.monster.prototype.EPokemonType;
import com.zdream.pmw.monster.prototype.IPokemonDataType;
import com.zdream.pmw.platform.attend.AttendManager;
import com.zdream.pmw.platform.attend.IStateInterceptable;
import com.zdream.pmw.platform.attend.Participant;
import com.zdream.pmw.platform.attend.service.EStateSource;
import com.zdream.pmw.platform.control.IMessageCode;
import com.zdream.pmw.platform.control.IPrintLevel;
import com.zdream.pmw.platform.effect.Aperitif;
import com.zdream.pmw.platform.prototype.BattlePlatform;
import com.zdream.pmw.util.json.JsonValue;

/**
 * 存在状态<br>
 * 一种最基础的状态, 归属方一定为怪兽<br>
 * 在怪兽上场后, 该状态就会默认添加、一直保留, 直到濒死退场或交换退场;<br>
 * <br>
 * 作用: 技能本系加成能力提升<br>
 * <br>
 * <b>v0.2.1</b>
 * <p>将 {@code Participant} 中关于能力等级的属性移到此处</p>
 * @see com.zdream.pmw.platform.attend.Participant
 * 
 * @since v0.2.1
 * @author Zdream
 * @date 2017年2月28日
 * @version v0.2
 */
public class ExistState extends ParticipantState implements IPokemonDataType {

	/* ************
	 *	  属性    *
	 ************ */
	
	/**
	 * 能力等级<br>
	 * <p>原来该项数据由 {@link com.zdream.pmw.platform.attend.Participant} 管理,
	 * 现在移至此</p>
	 * @since v0.2.1
	 */
	private byte[] abiliLvs = new byte[8];

	public void setAbilityLevel(int item, byte level) {
		this.abiliLvs[item] = level;
	}
	public void setAbilityLevels(byte[] abilityLevel) {
		this.abiliLvs = abilityLevel;
	}
	public byte getAbilityLevel(int item) {
		return abiliLvs[item];
	}

	/* ************
	 *	实现方法  *
	 ************ */

	@Override
	public String name() {
		return "exist";
	}

	/**
	 * 发动源: 无<br>
	 * 在怪兽上场后, 该状态就会一直保留, 直到濒死退场或交换退场
	 */
	public EStateSource source() {
		return EStateSource.OTHER;
	}

	@Override
	public boolean canExecute(String msg) {
		switch (msg) {
		case IMessageCode.CODE_CALC_POWER:
		case IMessageCode.CODE_CALC_AT:
		case IMessageCode.CODE_CALC_DF:
		case IMessageCode.CODE_CALC_SA:
		case IMessageCode.CODE_CALC_SD:
		case IMessageCode.CODE_CALC_SP:
		case IMessageCode.CODE_CALC_ADDITION_RATE:
			return true;

		default:
			break;
		}
		return false;
	}
	
	@Override
	public String execute(Aperitif value, IStateInterceptable interceptor, BattlePlatform pf) {
		this.pf = pf;
		switch (value.getHead()) {
		case IMessageCode.CODE_CALC_POWER:
			return calcPower(value, interceptor);
		case IMessageCode.CODE_CALC_AT:
			return calcAT(value, interceptor, pf);
		case IMessageCode.CODE_CALC_DF:
			return calcDF(value, interceptor, pf);
		case IMessageCode.CODE_CALC_SA:
			return calcSA(value, interceptor, pf);
		case IMessageCode.CODE_CALC_SD:
			return calcSD(value, interceptor, pf);
		case IMessageCode.CODE_CALC_SP:
			return calcSP(value, interceptor, pf);
		case IMessageCode.CODE_CALC_ADDITION_RATE:
			return calcAdditionRate(value, interceptor, pf);
		default:
			break;
		}
		return execute(value, interceptor, pf);
	}

	@Override
	public int priority(String msg) {
		return 10;
	}
	
	@Override
	public String ofCategory() {
		return "exist";
	}

	/* ************
	 *	私有方法  *
	 ************ */
	private String calcPower(Aperitif value, IStateInterceptable interceptor) {
		// 首先, 能发动 count-power 拦截前消息的一定是能够造成伤害的技能
		
		// 释放技能号
		short skillID = (short) value.get("skillID");
		
		// 技能属性
		EPokemonType type = pf.getAttendManager().getSkillRelease(skillID).getType();
		
		// 攻击方怪兽
		byte seat = (byte) value.get("atseat");
		
		if (pf.getAttendManager().hasType(seat, type)) {
			// 可以本系加成
			float power = (float) value.get("result");
			power *= 1.5f;
			pf.logPrintf(IPrintLevel.PRINT_LEVEL_VERBOSE,
					"ExistState.countPower(2): %s(seat = %d) 的攻击 %s 本系加成",
					pf.getAttendManager().getParticipant(seat).getNickname(),
					seat, pf.getAttendManager().getSkillRelease(skillID).getTitle());
			value.replace("result", power);
		}
		
		return interceptor.nextState();
	}
	
	private String calcAT(Aperitif value, IStateInterceptable interceptor, BattlePlatform pf) {
		return calcStat(AT, value, interceptor, pf);
	}
	
	private String calcDF(Aperitif value, IStateInterceptable interceptor, BattlePlatform pf) {
		return calcStat(DF, value, interceptor, pf);
	}
	
	private String calcSA(Aperitif value, IStateInterceptable interceptor, BattlePlatform pf) {
		return calcStat(SA, value, interceptor, pf);
	}
	
	private String calcSD(Aperitif value, IStateInterceptable interceptor, BattlePlatform pf) {
		return calcStat(SD, value, interceptor, pf);
	}
	
	private String calcSP(Aperitif value, IStateInterceptable interceptor, BattlePlatform pf) {
		return calcStat(SP, value, interceptor, pf);
	}
	
	private String calcStat(int item, Aperitif value, IStateInterceptable interceptor, BattlePlatform pf) {
		byte seat = (byte) value.getMap().get("seat").getValue();
		
		JsonValue v = value.getMap().get("ignore");
		int ignore;
		if (v == null) {
			ignore = 0;
		} else {
			ignore = (int) v.getValue();
		}
		
		int level = pf.getAttendManager().abilityLevel(seat, item, ignore);
		float rate = pf.getAttendManager().abilityLevelRate(item, level);
		
		v = value.getMap().get("rate");
		v.setValue(rate * (float) v.getValue());
		value.getMap().put("level", new JsonValue(level));
		
		return interceptor.nextState();
	}
	
	/**
	 * <p>1. 如果判断异常状态的话:
	 * <li>中毒和剧毒无法施加给毒属性、钢属性怪兽
	 * <li>麻痹无法施加给电气属性怪兽
	 * <li>烧伤无法施加给火属性怪兽
	 * <li>冻伤无法施加给冰属性怪兽</li>
	 * 如果想忽略此类状态, 可以在 {@link Aperitif} 中添加这个声明:
	 * <blockquote><pre>
     *     "type"
     * </pre></blockquote>
	 * </p>
	 * 
	 * <p>2. 所有状态 (包括异常状态):
	 * <li>不能为同一个对象添加相同的状态</li>
	 * 如果想忽略此类状态, 可以在 {@link Aperitif} 中添加这个声明，
     * 表示你想替换原有状态或为该对象添加多个这类状态:
	 * <blockquote><pre>
     *     "repeat"
     * </pre></blockquote>
	 * </p>
	 * @param value
	 * @param interceptor
	 * @param pf
	 * @return
	 */
	private String calcAdditionRate(Aperitif value, IStateInterceptable interceptor, BattlePlatform pf) {
		// 防御方限定
		byte seat = (byte) value.get("target");
		if (seat != pf.getAttendManager().seatForNo(getNo())) {
			return interceptor.nextState();
		}
		
		// 只处理状态
		if (!"state".equals(value.get("type")) && !"abnormal".equals(value.get("type"))) {
			return interceptor.nextState();
		}
		
		// 如果是删除状态, 则不处理 v0.2.2
		Object rm = value.get("remove");
		if (rm != null) {
			if ((boolean) rm) {
				return interceptor.nextState();
			}
		}
		
		// 1 类
		if ("abnormal".equals(value.get("state-category"))) {
			if (!value.hasFilter("type") && handleAbnormal(value, pf)) {
				// 不能施加状态
				value.append("reason", "type").replace("result", 1);
				return null;
			}
		}
		
		// 2 类
		if (!value.hasFilter("repeat") && handleRepeat(value, pf)) {
			// 不能施加状态
			value.append("reason", "repeat").replace("result", 1);
			pf.logPrintf(IPrintLevel.PRINT_LEVEL_INFO,
					"ExistState.calcAdditionRate(3) 由于怪兽 %s(seat=%d) 已经有 %s 状态, 不能再施加",
					pf.getAttendManager().getParticipant(seat).getNickname(), seat, name());
			return null;
		}
		
		return interceptor.nextState();
	}
	
	private boolean handleAbnormal(Aperitif value, BattlePlatform pf) {
		EPokemonAbnormal abnormal = AbnormalMethods.parseAbnormal((byte) value.get("abnormal"));
		
		AttendManager am = pf.getAttendManager();
		byte seat = am.seatForNo(getNo());

		switch (abnormal) {
		case POISON: case BADLY_POISON: {
			if (am.hasType(seat, EPokemonType.Poison) ||
					am.hasType(seat, EPokemonType.Steel)) {
				return true;
			}
		} break;
		
		case PARALYSIS: {
			if (am.hasType(seat, EPokemonType.Electric)) {
				return true;
			}
		}

		case BURN: {
			if (am.hasType(seat, EPokemonType.Fire)) {
				return true;
			}
		} break;

		case FREEZE: {
			if (am.hasType(seat, EPokemonType.Ice)) {
				return true;
			}
		} break;
		
		default:
			break;
		}
		
		return false;
	}
	
	private boolean handleRepeat(Aperitif value, BattlePlatform pf) {
		// 获取状态的分类
		Object o = value.get("state-category");
		if (o == null) {
			o = value.get("state");
		}
		String stateCategory = o.toString();
		
		Participant p = pf.getAttendManager().getParticipant((byte) value.get("target"));
		if (p.hasCategoryState(stateCategory)) {
			return true;
		}
		
		return false;
	}

	/* ************
	 *	 初始化   *
	 ************ */
	
	BattlePlatform pf;

	public ExistState(byte no) {
		super(no);
	}
	
	@Override
	public String toString() {
		return name() + "-no: " + getNo();
	}

}
