package com.zdream.pmw.platform.effect.state;

import com.zdream.pmw.platform.attend.Participant;
import com.zdream.pmw.platform.effect.Aperitif;
import com.zdream.pmw.platform.prototype.BattlePlatform;

/**
 * <p>寄生种子附属状态</p>
 * <p>该状态绑定施加状态一方的座位, 与寄生种子状态协同工作</p>
 * 
 * @see LeechSeedState
 * @since v0.2.2 [2017-04-20]
 * @author Zdream
 * @version v0.2.2 [2017-04-20]
 */
public class LeechSeedSubState extends ASubState {

	/* ************
	 *	  属性    *
	 ************ */
	
	/**
	 * 绑定的怪兽
	 */
	public final byte seat;
	
	/**
	 * 主状态绑定的怪兽
	 */
	public final byte mainNo;

	/* ************
	 *	实现方法  *
	 ************ */

	@Override
	public String name() {
		return super.name() + "sub";
	}

	@Override
	public boolean canExecute(String msg) {
		return false;
	}

	@Override
	public int priority(String msg) {
		return 0;
	}

	/* ************
	 *	触发状态  *
	 ************ */
	
	/**
	 * 主状态直接调用, 让在该座位上的怪兽吸血
	 * @param hp
	 */
	void handleLeech(int hp, BattlePlatform pf) {
		
		Participant p = pf.getAttendManager().getParticipant(seat);
		if (p == null) {
			// 允许该座位上不存在怪兽
			return;
		}
		
		// 发送相应拦截前消息
		Aperitif ap2 = pf.getEffectManage().newAperitif(CODE_NONMOVES_DAMAGE, p.getSeat());
		ap2.append("seat", p.getSeat()).append("hp", hp * -1).append("reason", name());
		pf.readyCode(ap2);
	}

	/* ************
	 *	 初始化   *
	 ************ */

	public LeechSeedSubState(LeechSeedState mainState, byte seat) {
		super(mainState);
		this.seat = seat;
		this.mainNo = mainState.getNo();
	}
	
	@Override
	public String toString() {
		return name() + "|belong to:" + mainState.name();
	}

}
