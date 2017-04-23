package com.zdream.pmw.platform.effect.state;

import com.zdream.pmw.monster.prototype.IPokemonDataType;
import com.zdream.pmw.platform.attend.IStateInterceptable;
import com.zdream.pmw.platform.effect.Aperitif;
import com.zdream.pmw.platform.prototype.BattlePlatform;

/**
 * <p>愤怒状态</p>
 * <p>处于愤怒状态的宝可梦受到招式伤害时攻击力上升 1 等级,<br>
 * 替身挡下伤害不算作受到招式伤害.</p>
 * 
 * @since v0.2.2 [2017-04-20]
 * @author Zdream
 * @version v0.2.2 [2017-04-20]
 */
public class RageState extends AParticipantState {

	/* ************
	 *	实现方法  *
	 ************ */

	@Override
	public String name() {
		return "rage";
	}

	@Override
	public boolean canExecute(String msg) {
		switch (msg) {
		case CODE_SKILL_DAMAGE:
		case CODE_CONFIRM_SKILL:
			return true;
		default:
			break;
		}
		return false;
	}
	
	@Override
	public String execute(Aperitif ap, IStateInterceptable interceptor, BattlePlatform pf) {
		String head = ap.getHead();
		if (CODE_SKILL_DAMAGE.equals(head)) {
			return skillDamage(ap, interceptor, pf);
		} else if (CODE_CONFIRM_SKILL.equals(head)) {
			return confirmSkill(ap, interceptor, pf);
		}
		
		return super.execute(ap, interceptor, pf);
	}

	@Override
	public int priority(String msg) {
		return 0;
	}

	/* ************
	 *	状态触发  *
	 ************ */
	
	private String skillDamage(Aperitif ap, IStateInterceptable interceptor, BattlePlatform pf) {
		String cmd = interceptor.nextState();
		
		// 后拦截
		byte seat = pf.getAttendManager().seatForNo(getNo());
		if (pf.getAttendManager().getParticipant(seat).getHpi() <= 0) {
			return cmd;
		}
		
		Aperitif ap0 = pf.getEffectManage().newAperitif(Aperitif.CODE_CALC_ADDITION_RATE, seat);
		ap0.append("dfseats", new byte[]{seat}).append("target", seat).append("atseat", seat)
				.append("type", "abilityLevel").append("param", "-c")
				.append("items", new int[]{IPokemonDataType.AT})
				.append("values", new int[]{1}).append("rate", 100)
				.append("source", "state:" + name()).append("result", 0);
		pf.getEffectManage().startCode(ap0);
		
		int result = (int) ap0.get("result");
		
		if (result != 0 && result != 2) {
			// TODO 愤怒效果触发失败
		}
		
		Aperitif ap1 = pf.getEffectManage().newAperitif(Aperitif.CODE_ABILITY_LEVEL, seat);
		ap1.append("dfseat", seat).append("param", "-c")
				.append("items", ap0.get("items"))
				.append("values", ap0.get("values"));
		pf.getEffectManage().startCode(ap1);
		
		return cmd;
	}

	/**
	 * 删除自己的技能
	 */
	private String confirmSkill(Aperitif ap, IStateInterceptable interceptor, BattlePlatform pf) {
		String cmd = interceptor.nextState();
		
		// 后拦截
		pf.getEffectManage().sendRemoveParticipantStateMessage(name(), getNo());
		
		return cmd;
	}

	/* ************
	 *	 初始化   *
	 ************ */

	/**
	 * 默认无参数的构造方法, StateBuilder 调用<br>
	 * <p>要注意, 构造完成后, 需要调用 {@code set()} 方法设置参数</p>
	 */
	public RageState() {
		super();
	}

	/**
	 * @param no
	 *   对应的怪兽
	 */
	public RageState(byte no) {
		super(no);
	}
	
	@Override
	public String toString() {
		return name();
	}

}
