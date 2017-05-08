package com.zdream.pmw.platform.effect.state;

import com.zdream.pmw.core.tools.AbnormalMethods;
import com.zdream.pmw.core.tools.ItemMethods;
import com.zdream.pmw.monster.prototype.EPokemonAbnormal;
import com.zdream.pmw.monster.prototype.EPokemonType;
import com.zdream.pmw.monster.prototype.IPokemonDataType;
import com.zdream.pmw.platform.attend.AttendManager;
import com.zdream.pmw.platform.attend.IStateContainer;
import com.zdream.pmw.platform.attend.IStateInterceptable;
import com.zdream.pmw.platform.control.IMessageCode;
import com.zdream.pmw.platform.control.IPrintLevel;
import com.zdream.pmw.platform.effect.Aperitif;
import com.zdream.pmw.platform.prototype.BattlePlatform;

/**
 * 存在状态<br>
 * 一种最基础的状态, 归属方一定为怪兽<br>
 * 在怪兽上场后, 该状态就会默认添加、一直保留, 直到濒死退场或交换退场;<br>
 * <br>
 * 作用
 * <li>技能本系加成能力提升
 * <li>检查状态, 去除重复的状态</li>
 * <br>
 * <b>v0.2.1</b>
 * <p>将 {@code Participant} 中关于能力等级的属性移到此处</p>
 * 
 * <p><b>v0.2.2</b><br>
 * 由于座位、阵营、全场状态的出现, 每个状态容器都将保留一个初始状态,
 * 因此该类被重新归类到初始状态中( {@link ABaseState} ).</p>
 * 
 * @see com.zdream.pmw.platform.attend.Participant
 * 
 * @since v0.2.2 [2017-04-17]
 * @author Zdream
 * @version v0.2 [2017-02-28]
 */
public final class ExistState extends ABaseState implements IPokemonDataType {

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

	private byte no;
	
	/**
	 * @return 这个状态属于对应哪个怪兽
	 */
	public byte getNo() {
		return no;
	}

	/* ************
	 *	实现方法  *
	 ************ */

	@Override
	public String name() {
		return "exist";
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
	
	private String calcStat(int item, Aperitif ap, IStateInterceptable interceptor, BattlePlatform pf) {
		byte seat = (byte) ap.get("seat");
		
		Object obj = ap.get("ignore");
		int ignore;
		if (obj == null) {
			ignore = 0;
		} else {
			ignore = (int) obj;
		}
		
		int level = pf.getAttendManager().abilityLevel(seat, item, ignore);
		float rate = pf.getAttendManager().abilityLevelRate(item, level);
		
		obj = ap.get("rate");
		ap.put("rate", (float)(rate * (float) obj));
		ap.put("level", level);
		
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
	 * @param ap
	 * @param interceptor
	 * @param pf
	 * @return
	 */
	protected String calcAdditionRate(Aperitif ap, IStateInterceptable interceptor, BattlePlatform pf) {
		if ("state".equals(ap.get("type"))) {
			// 2 类
			super.calcAdditionRate(ap, interceptor, pf);
		} else if ("abnormal".equals(ap.get("type"))) {
			// 1 类
			if (!confirmTarget(ap, pf)) {
				return interceptor.nextState();
			}

			byte code = (byte) ap.get("abnormal");
			AttendManager am = pf.getAttendManager();
			byte curCode = am.getAttendant(no).getAbnormal();
			if (curCode == 0) {
				if (code != 0) {
					if (!ap.hasFilter("type") && handleAbnormal(ap, pf)) {
						// 不能施加状态
						ap.append("reason", "type").replace("result", 1);
						return null;
					} else {
						return interceptor.nextState();
					}
				} else {
					// 原来没有状态, 现在要清除状态
					// 发动失败
					ap.append("reason", "none").replace("result", 1);
					return null;
				}
			} else {
				if (code != 0) {
					// 原来有状态, 现在还要施加状态
					// 除了睡眠技能, 其它都忽略, 发动失败
					ap.append("reason", "already").replace("result", 1);
					return null;
				} else {
					// 清除状态
					return interceptor.nextState();
				}
			}
		}
		
		return interceptor.nextState();
	}
	
	private boolean handleAbnormal(Aperitif value, BattlePlatform pf) {
		EPokemonAbnormal abnormal = AbnormalMethods.parseAbnormal((byte) value.get("abnormal"));
		
		AttendManager am = pf.getAttendManager();
		byte seat = am.seatForNo(no);

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
	
	@Override
	protected IStateContainer stateContainer(BattlePlatform pf) {
		byte seat = pf.getAttendManager().seatForNo(no);
		return pf.getAttendManager().getParticipant(seat);
	}
	
	@Override
	protected boolean confirmTarget(Aperitif ap, BattlePlatform pf) {
		byte seat = (byte) ap.get("target");
		return seat == pf.getAttendManager().seatForNo(getNo());
	}

	/* ************
	 *	 初始化   *
	 ************ */
	
	BattlePlatform pf;

	public ExistState(byte no) {
		this.no = no;
	}
	
	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append(name()).append("-no:").append(getNo());
		
		boolean lvs = false;
		for (int i = 1; i < abiliLvs.length; i++) {
			if (abiliLvs[i] != 0) {
				if (!lvs) {
					b.append("|");
					lvs = true;
				} else {
					b.append("&");
				}
				b.append(ItemMethods.parseItemName(i)).append(":").append(abiliLvs[i]);
			}
		}
		
		return b.toString();
	}

}
