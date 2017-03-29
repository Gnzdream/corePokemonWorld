package com.zdream.pmw.platform.control.ai;

import java.util.Random;

import com.zdream.pmw.platform.attend.Participant;
import com.zdream.pmw.platform.control.ControlBase;
import com.zdream.pmw.platform.control.ControlManager;
import com.zdream.pmw.platform.control.IRequestKey;
import com.zdream.pmw.platform.prototype.BattlePlatform;
import com.zdream.pmw.platform.prototype.IRequestCallback;

/**
 * 默认 AI (单线程操作)<br>
 * 
 * @since v0.2
 * @author Zdream
 * @date 2017年2月25日
 */
public class DefaultAIBrain implements IAIRunnable {
	
	ControlManager cm;
	
	/* ************
	 *	 控制体   *
	 ************ */
	private ControlBase ctrl;
	
	@Override
	public ControlBase getCtrl() {
		return ctrl;
	}
	
	@Override
	public void setCtrl(ControlBase ctrl) {
		this.ctrl = ctrl;
	}
	
	/* ************
	 *	 随机数   *
	 ************ */
	Random random = new Random();
	
	/* ************
	 *	  行动    *
	 ************ */
	
	/**
	 * 需要行动的座位号数组
	 */
	byte[] seats;
	
	/**
	 * 为多线程模式准备的
	 */
	@Override
	public Runnable createAIRunnable() {
		return new Runnable() {
			
			@Override
			public void run() {
				while (true) {
					String content = ctrl.nextRequest();
					if (IRequestKey.VALUE_REQ_CONTENT_MOVE.equals(content)) {
						chooseMove();
					} else if (IRequestKey.VALUE_REQ_CONTENT_END.equals(content)) {
						break;
					}
				}
			}
		};
	}

	@Override
	public IRequestCallback createAICallback() {
		return new IRequestCallback() {
			
			@Override
			public void onRequest(BattlePlatform platform, ControlBase ctrl) {
				setCtrl(ctrl);
				String content = ctrl.nextRequest();
				if (IRequestKey.VALUE_REQ_CONTENT_MOVE.equals(content)) {
					chooseMove();
				} else if (IRequestKey.VALUE_REQ_CONTENT_END.equals(content)) {
					return;
				}
			}
		};
	};
	
	
	/**
	 * 选择行动
	 */
	private void chooseMove() {
		seats = ctrl.getSeats();
		
		for (int i = 0; i < seats.length; i++) {
			byte seat = seats[i];
			
			// 这个怪兽有多少技能？
			Participant participant = cm.getRoot().getAttendManager()
					.getParticipant(seat);
			short[] skills = participant.getAttendant().getSkill();
			int moveLength = skills.length;
			int move;
			while (true) {
				move = random.nextInt(moveLength);
				if (skills[move] != 0) {
					ctrl.setCommand(seat, ControlBase.COMMAND_MOVES);
					ctrl.setParam(seat, Integer.toString(move));
					break;
				}
			}
		}
		ctrl.commit();
	}

	/* ************
	 *	 初始化   *
	 ************ */

	public DefaultAIBrain(ControlManager cm) {
		this.cm = cm;
	}

}
