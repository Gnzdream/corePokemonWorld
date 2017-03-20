package com.zdream.pmw.platform.effect.state;

import com.zdream.pmw.monster.prototype.EPokemonAbnormal;
import com.zdream.pmw.monster.prototype.IPokemonDataType;
import com.zdream.pmw.platform.attend.AttendManager;
import com.zdream.pmw.platform.attend.IStateInterceptable;
import com.zdream.pmw.platform.attend.Participant;
import com.zdream.pmw.platform.attend.service.EStateSource;
import com.zdream.pmw.platform.effect.Aperitif;
import com.zdream.pmw.platform.prototype.BattlePlatform;

/**
 * 中毒异常状态<br>
 * 这里所说的中毒不包括剧毒状态<br>
 * 中毒是一种异常状态。关于异常状态，您可以查看：<code>EPokemonAbnormal</code><br>
 * 
 * @see
 *   EPokemonAbnormal
 * @since v0.1
 * @author Zdream
 * @date 2016年4月19日
 * @version v0.2
 */
public class PoisonState extends AAbnormalState {
	
	BattlePlatform pf;

	/* ************
	 *	 初始化   *
	 ************ */

	public PoisonState() {
		super(EPokemonAbnormal.POISON);
	}

	/* ************
	 *	实现方法  *
	 ************ */

	/**
	 * 该状态的发动与特定的施加源头没有关联<br>
	 */
	public EStateSource source() {
		return EStateSource.OTHER;
	}

	/**
	 * 可拦截的消息：<br>
	 * 1. 每回合结束时<br>
	 * 2. 等 TODO <br>
	 */
	public boolean canExecute(String msg) {
		if (CODE_ROUND_END.equals(msg)) {
			return true;
		}
		return false;
	}

	@Override
	public String execute(Aperitif value, IStateInterceptable interceptor, BattlePlatform pf) {
		this.pf = pf;
		String head = value.getHead();
		if (CODE_ROUND_END.equals(head)) {
			return roundEnd(value, interceptor);
		}
		return null;
	}

	@Override
	public int priority(String msg) {
		return 0;
	}

	/* ************
	 *	私有方法  *
	 ************ */

	/**
	 * 每回合结束时需要判断的情况<br>
	 * 一般的精灵每个回合结束前需要扣 1/8 的最大 HP 对应值的 HP<br>
	 * @param value
	 * @param interceptor
	 * @return
	 */
	private String roundEnd(Aperitif value, IStateInterceptable interceptor) {
		AttendManager am = pf.getAttendManager();
		
		// 步骤一：找出哪只精灵中了剧毒
		byte seat = am.seatForNo(getNo());
		
		// 步骤二：计算出扣血的数值
		// 为最大生命值的 1/8，如果最大生命值除 8 余数大于等于 4，再加扣 1
		Participant p = am.getParticipant(seat);
		int hpMax = p.getAttendant().getStat()[IPokemonDataType.HP];
		int hp = (hpMax % 8 >= 4) ? (hpMax >> 3) + 1 : (hpMax >> 3);
		
		// 步骤三：发送相应拦截前消息
		Aperitif v = pf.getEffectManage().newAperitif(CODE_NONMOVES_DAMAGE, seat);
		v.append("seat", seat).append("hp", hp).append("reason", name());
		pf.readyCode(v);
		
		return interceptor.nextState();
	}

}
