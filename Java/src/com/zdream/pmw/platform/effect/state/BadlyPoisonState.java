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
 * 剧毒异常状态<br>
 * 剧毒是一种异常状态。关于异常状态，您可以查看：<code>EPokemonAbnormal</code><br>
 * 
 * @since v0.2
 * @author Zdream
 * @date 2017年2月27日
 * @version v0.2
 */
public class BadlyPoisonState extends AAbnormalState {
	
	/**
	 * 回合结束拦截前消息头
	 */
	private static final String PERIOD_ROUND_END = "round-end";

	@Override
	public EStateSource source() {
		/*
		 * 该状态的发动与特定的施加源头没有关联
		 */
		return EStateSource.OTHER;
	}

	/**
	 * 可拦截的消息：<br>
	 * 1. 每回合结束时<br>
	 * 2. 等 TODO <br>
	 */
	public boolean canExecute(String msg) {
		if (PERIOD_ROUND_END.equals(msg)) {
			return true;
		}
		return false;
	}

	@Override
	public String execute(Aperitif value, IStateInterceptable interceptor, BattlePlatform pf) {
		this.pf = pf;
		String head = value.getHead();
		if (PERIOD_ROUND_END.equals(head)) {
			return roundEnd(value, interceptor);
		}
		return null;
	}

	@Override
	public int priority(String msg) {
		return 0;
	}

	/* ************
	 *	 计数器   *
	 ************ */
	
	int count = 1;
	
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public void resetCount() {
		this.count = 1;
	}

	/* ************
	 *	私有方法  *
	 ************ */
	/**
	 * 每回合结束时需要判断的情况<br>
	 * 一般的精灵每个回合结束前需要扣 (count)/16，再加扣 1 对应值的 HP<br>
	 * @param value
	 * @param interceptor
	 * @return
	 */
	private String roundEnd(Aperitif value, IStateInterceptable interceptor) {
		AttendManager am = pf.getAttendManager();
		
		// 步骤一：找出哪只精灵中了剧毒
		byte seat = am.seatForNo(getNo());
		
		// 步骤二：计算出扣血的数值
		// 为最大生命值的 (count)/16，再加扣 1
		Participant p = am.getParticipant(seat);
		int hpMax = p.getAttendant().getStat()[IPokemonDataType.HP];
		int hp = (hpMax / 16) * count;
		
		// 步骤三：发送相应拦截前消息
		Aperitif v = pf.getEffectManage().newAperitif(CODE_NONMOVES_DAMAGE, seat);
		v.append("seat", seat).append("hp", hp).append("reason", name());
		pf.readyCode(v);
		
		// 步骤四：计数器再加
		if (count < 15) {
			count++;
			
			// TODO 如果是非服务器方的玩家的话, 这里睡眠状态需要放出一个消息
			// 让对方同步睡眠次数
		}
		
		return interceptor.nextState();
	}

	/* ************
	 *	 初始化   *
	 ************ */
	
	BattlePlatform pf;

	public BadlyPoisonState() {
		super(EPokemonAbnormal.BADLY_POISON);
	}
	
	@Override
	public String toString() {
		return name() + ":" + count;
	}

}
