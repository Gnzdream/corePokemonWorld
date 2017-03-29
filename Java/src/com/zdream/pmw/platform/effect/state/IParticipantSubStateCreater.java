package com.zdream.pmw.platform.effect.state;

import com.zdream.pmw.platform.attend.AttendManager;
import com.zdream.pmw.platform.prototype.BattlePlatform;

/**
 * 能产生附属至指定怪兽上的状态的接口<br>
 * 
 * @since v0.2.1
 * @author Zdream
 * @date 2017年3月21日
 * @version v0.2.1
 */
public interface IParticipantSubStateCreater extends ISubStateCreater {
	
	/**
	 * 说明这个状态将施加给谁
	 * @param state
	 * @return
	 *   怪兽的 no 值
	 */
	public byte toWhom(ASubState state);
	
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
			am.removeStateFromParticipant(toWhom(state), state.name());
		}
	}

}
