package com.zdream.pmw.platform.effect.state;

import com.zdream.pmw.platform.attend.AttendManager;
import com.zdream.pmw.platform.attend.IState;
import com.zdream.pmw.platform.attend.IStateInterceptable;
import com.zdream.pmw.platform.effect.Aperitif;
import com.zdream.pmw.platform.prototype.BattlePlatform;

/**
 * 能产生附属至指定怪兽上的状态的接口<br>
 * 
 * @since v0.2.1
 * @author Zdream
 * @date 2017年3月21日
 * @version v0.2.1
 */
public interface IParticipantSubStateCreater extends ISubStateCreater, IState {
	
	/**
	 * 说明这个状态将施加给谁
	 * @param state
	 * @return
	 *   怪兽的 no 值
	 */
	public byte toWhom(ASubState state);
	
	@Override
	default boolean canExecute(String msg) {
		switch (msg) {
		case IState.CODE_EXEUNT_EXCHANGE:
		case IState.CODE_EXEUNT_FAINT:
			return true;
		default:
			return false;
		}
	}
	
	@Override
	default String execute(Aperitif value, IStateInterceptable interceptor, BattlePlatform pf) {
		String head = value.getHead();
		if (head.equals(IState.CODE_EXEUNT_EXCHANGE) || head.equals(IState.CODE_EXEUNT_FAINT)) {
			handleDestroySubStates(pf);
		}
		return interceptor.nextState();
	}
	
	@Override
	default void handleCreateSubStates(BattlePlatform pf) {
		AttendManager am = pf.getAttendManager();
		ASubState[] states = getSubStates();
		
		for (int i = 0; i < states.length; i++) {
			ASubState state = states[i];
			am.forceStateForParticipant(toWhom(state), state);
		}
	}

	@Override
	default void handleDestroySubStates(BattlePlatform pf) {
		AttendManager am = pf.getAttendManager();
		ASubState[] states = getSubStates();
		
		for (int i = 0; i < states.length; i++) {
			ASubState state = states[i];
			am.removeStateFromParticipant(pf.getAttendManager().seatForNo(toWhom(state)),
					state.name());
		}
	}

}
