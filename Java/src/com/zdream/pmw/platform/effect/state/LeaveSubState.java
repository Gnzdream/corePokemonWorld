package com.zdream.pmw.platform.effect.state;

import com.zdream.pmw.platform.attend.AttendManager;
import com.zdream.pmw.platform.attend.IState;
import com.zdream.pmw.platform.attend.IStateInterceptable;
import com.zdream.pmw.platform.effect.Aperitif;
import com.zdream.pmw.platform.prototype.BattlePlatform;

/**
 * <p>留场形式状态的附属状态</p>
 * <p>留场形式就是为对方施加状态, 只要攻击方在场, 该状态就会一直存在, 直到攻击方离场.</p>
 * <p>典型的这类状态有:
 * <li>束缚状态 {@link BoundState}
 * </li></p>
 * 
 * @since v0.2.1
 * @author Zdream
 * @date 2017年3月20日
 * @version v0.2.1
 */
public class LeaveSubState extends ASubState {

	/* ************
	 *	  属性    *
	 ************ */
	
	/**
	 * 绑定的怪兽
	 */
	public final byte no;
	
	/**
	 * 主状态绑定的怪兽
	 */
	public final byte mainNo;

	/* ************
	 *	实现方法  *
	 ************ */

	@Override
	public boolean canExecute(String msg) {
		if (msg.equals(CODE_EXEUNT_EXCHANGE) || msg.equals(CODE_EXEUNT_FAINT)) {
			return true;
		}
		return false;
	}
	
	@Override
	public String execute(Aperitif value, IStateInterceptable interceptor, BattlePlatform pf) {
		// 当判断为攻击方离场, 则删除主状态
		byte seat, no;
		AttendManager am = pf.getAttendManager();
		Object o = value.get("no");
		if (o != null) {
			no = (byte) o;
		} else {
			o = value.get("seat");
			seat = (byte) o;
			no = am.noForSeat(seat);
		}
		
		if (no != this.no) {
			return interceptor.nextState();
		}
		
		// 删除主状态
		byte mainSeat = am.seatForNo(mainNo);
		Aperitif ap = pf.getEffectManage().newAperitif("remove-state", mainSeat);
		ap.append("seat", mainSeat).append("state", mainState.name());
		pf.getEffectManage().startCode(ap);
		
		return interceptor.nextState();
	}

	@Override
	public int priority(String msg) {
		return 0;
	}
	
	@Override
	public String name() {
		return super.name() + "leave";
	}

	/* ************
	 *	 初始化   *
	 ************ */

	public LeaveSubState(AParticipantState mainState, byte no) {
		super(mainState);
		this.no = no;
		this.mainNo = mainState.getNo();
	}

	public LeaveSubState(IState mainState, byte no, byte mainNo) {
		super(mainState);
		this.no = no;
		this.mainNo = mainNo;
	}
	
	@Override
	public String toString() {
		return name() + "|belong to:" + mainState.name();
	}

}
