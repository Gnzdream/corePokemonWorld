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
 * 烧伤 / 灼伤异常状态<br>
 * 烧伤是一种异常状态。关于异常状态，您可以查看：<code>EPokemonAbnormal</code><br>
 * 
 * @see
 *   EPokemonAbnormal
 * @since v0.2
 * @author Zdream
 * @date 2017年2月27日
 * @version v0.2
 */
public class BurnState extends AAbnormalState {

	@Override
	public EStateSource source() {
		/*
		 * 该状态的发动与特定的施加源头没有关联
		 */
		return EStateSource.OTHER;
	}

	@Override
	public boolean canExecute(String msg) {
		switch (msg) {
		case CODE_ROUND_END:
		case CODE_CALC_AT:
			return true;
		}
		return false;
	}

	/**
	 * 可拦截的消息：<br>
	 * 1. 每回合结束时扣血<br>
	 * 3. 释放物理攻击时<br>
	 * 4. 等 TODO <br>
	 */
	public String execute(Aperitif value, IStateInterceptable interceptor, BattlePlatform pf) {
		this.pf = pf;
		String head = value.getHead();
		switch (head) {
		case CODE_ROUND_END:
			return roundEnd(value, interceptor);
		case CODE_CALC_AT:
			return calcAT(value, interceptor);
		default:
			break;
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
	 * 一般的精灵每个回合结束前需要扣 1 / 8 对应值的 HP<br>
	 * @param value
	 * @param interceptor
	 * @return
	 */
	private String roundEnd(Aperitif value, IStateInterceptable interceptor) {
		AttendManager am = pf.getAttendManager();
		
		// 步骤一：找出哪只精灵中了烧伤
		byte seat = am.seatForNo(getNo());
		
		// 步骤二：计算出扣血的数值
		// 为最大生命值的 (count)/8，再加扣 1
		Participant p = am.getParticipant(seat);
		int hpMax = p.getAttendant().getStat()[IPokemonDataType.HP];
		int hp = (hpMax / 8);
		
		// 步骤三：发送相应拦截前消息
		Aperitif v = pf.getEffectManage().newAperitif(CODE_NONMOVES_DAMAGE, seat);
		v.append("seat", seat).append("hp", hp).append("reason", name());
		pf.readyCode(v);
		
		return interceptor.nextState();
	}

	/**
	 * 计算技能修正的时候需要判断的情况<br>
	 * 使用的技能满足: 物理类型, 威力 /= 2<br>
	 * @param value
	 * @param interceptor
	 * @return
	 */
	private String calcAT(Aperitif value, IStateInterceptable interceptor) {
		float rate = (float) value.get("rate");
		rate *= 0.5f;
		value.replace("rate", rate);
		
		return interceptor.nextState();
	}

	/* ************
	 *	 初始化   *
	 ************ */
	
	BattlePlatform pf;

	public BurnState() {
		super(EPokemonAbnormal.BURN);
	}
	
	@Override
	public String toString() {
		return name();
	}

}
