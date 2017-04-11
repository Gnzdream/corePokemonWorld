package com.zdream.pmw.platform.test;

import java.time.Duration;
import java.time.Instant;
import java.util.Scanner;

import com.zdream.pmw.monster.data.SkillDataBuffer;
import com.zdream.pmw.monster.skill.Skill;
import com.zdream.pmw.platform.attend.AttendManager;
import com.zdream.pmw.platform.attend.Attendant;
import com.zdream.pmw.platform.attend.Participant;
import com.zdream.pmw.platform.control.ControlBase;
import com.zdream.pmw.platform.control.IRequestKey;
import com.zdream.pmw.platform.prototype.BattlePlatform;
import com.zdream.pmw.platform.prototype.IRequestCallback;

/**
 * 简单处理战场消息的类
 * 
 * @since v0.2.2
 * @author Zdream
 * @version v0.2.2
 */
public class SimplePlatformHandler {
	
	protected ControlBase base;
	protected BattlePlatform bp;
	protected Scanner scanner;
	
	// 用来测时间的
	Instant t = Instant.now();
	
	protected IRequestCallback callback = new IRequestCallback() {
		
		@Override
		public void onRequest(BattlePlatform platform, ControlBase ctrl) {
			base = ctrl;
			
			String content = base.getContent();
			System.out.println();
			System.out.println("content: " + content);
			if (IRequestKey.VALUE_REQ_CONTENT_MOVE.equals(content)) {
				screenInfo(platform, ctrl);
				chooseMove(platform, ctrl);
			} else if (IRequestKey.VALUE_REQ_CONTENT_END.equals(content)) {
				System.out.println("战斗结束，结果：" + base.getEndResult());
			} else if (IRequestKey.VALUE_REQ_CONTENT_SWITCH.equals(content)) {
				chooseMonsterManually(platform, ctrl);
			}
		}
	};

	private void chooseMove(BattlePlatform platform, ControlBase ctrl) {
		byte[] seats = base.getSeats();
		System.out.println("用时: " + Duration.between(t, Instant.now()).toMillis() + " ms");
		
		for (int i = 0; i < seats.length;) {
			byte seat = seats[i];
			
			screenAction(platform, seat, i);
			String s = scanner.nextLine();
			i += writeCommand(platform, ctrl, seat, s, i);
		}
		t = Instant.now();
		base.commit();
	}
	
	private void screenAction(BattlePlatform platform, byte seat, int curIndex) {
		SkillDataBuffer buffer = SkillDataBuffer.getInstance();
		
		Participant participant = platform.getAttendManager()
				.getParticipant(seat);
		
		System.out.println(String.format("%s 想要干什么", 
				participant.getAttendant().getNickname()));
		
		// 这个怪兽有多少技能？
		short[] skills = participant.getAttendant().getSkill();
		for (int j = 0; j < skills.length; j++) {
			if (skills[j] == 0) {
				break;
			}
			
			Skill skill = buffer.getBaseData(skills[j]);
			System.out.println(String.format("%d - 技能 %s", j, skill.getTitle()));
		}
		
		// 其它行动
		System.out.println("6 - 交换怪兽");
		System.out.println("7 - 使用道具 [未实现]");
		System.out.println("8 - 逃跑 [未实现]");
		if (curIndex > 0) {
			System.out.println("9 - 重新考虑上一个怪兽的行动");
		}
	}
	
	private int writeCommand(BattlePlatform platform, 
			ControlBase ctrl, 
			byte seat, 
			String str, 
			int curIndex) {
		try {
			int act = Integer.valueOf(str);
			
			switch (act) {
			case 0: case 1: case 2: case 3:
				ctrl.chooseMove(seat, act);
				return 1; // 该轮行动重判断结束, 下一个
				
			case 6:
				return actReplace(platform, ctrl, seat);

			default:
				break;
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0; // 该轮行动重新判断
	}
	
	private int actReplace(BattlePlatform platform, ControlBase ctrl, byte seat) {
		screenCanReplace(platform, ctrl, seat);
		String str = scanner.nextLine();
		if (str.trim().equals("-1")) {
			return 0;
		}
		int num = Integer.valueOf(str.trim());
		ctrl.chooseReplace(seat, (byte) num);
		return 1;
	}
	
	private void screenInfo(BattlePlatform platform, ControlBase ctrl) {
		System.out.println("============");
		System.out.println("场上的情况：");
		int seatLength = bp.getAttendManager().seatLength();
		
		for (byte seat = 0; seat < seatLength; seat++) {
			System.out.println(String.format("[%d]: %s", seat, bp.getAttendManager().getParticipant(seat)));
		}
		System.out.println("============");
	}
	
	private void screenCanReplace(BattlePlatform platform, ControlBase ctrl, byte seat) {
		AttendManager am = platform.getAttendManager();
		byte curNo = am.noForSeat(seat); // 可能为 -1
		
		int length = am.attendantLength();
		for (byte no = 0; no < length; no++) {
			if (ctrl.getTeam() != am.teamForNo(no)) {
				continue;
			}
			
			Attendant att = am.getAttendant(no);
			System.out.println(String.format("%d - %s %s", no, att, 
					(no == curNo) ? "[自己]" : ""));
		}
		System.out.println("-1 - [返回]");
	}
	
	/**
	 * 选择怪兽
	 */
	@SuppressWarnings("unused")
	private void chooseMonsterAuto(BattlePlatform platform, ControlBase ctrl) {
		byte[] seats = ctrl.getSeats();
		byte team = ctrl.getTeam();
		
		// 下面寻找能上场的怪兽
		AttendManager am = platform.getAttendManager();
		int length = am.attendantLength();
		
		byte no = 0;
		for (int i = 0; i < seats.length; i++) { // i 指向 seats
			byte seat = seats[i];
			
			for (; no < length; no++) {
				Attendant at = am.getAttendant(no);
				if (am.teamForNo(no) == team && am.seatForNo(no) == -1 && at.getHpi() > 0) {
					ctrl.chooseReplace(seat, no);
					break;
				}
			}
		}
		ctrl.commit();
	}
	
	/**
	 * 选择怪兽
	 */
	private void chooseMonsterManually(BattlePlatform platform, ControlBase ctrl) {
		byte[] seats = ctrl.getSeats();
		byte team = ctrl.getTeam();
		
		for (int i = 0; i < seats.length; i++) {
			byte seat = seats[i]; // 暂不支持连续上场多个怪兽
			System.out.println("选择怪兽上场至位置: " + seat);
			screenCanReplace(platform, ctrl, seat);
			
			// 下面寻找能上场的怪兽
			AttendManager am = platform.getAttendManager();
			String input = scanner.nextLine();
			
			try {
				byte inNo = Byte.parseByte(input);
				
				Attendant att = am.getAttendant(inNo);
				if (am.teamForNo(inNo) == team && am.seatForNo(inNo) == -1 && att.getHpi() > 0) {
					ctrl.chooseReplace(seat, inNo);
					continue;
				}
				
			} catch (RuntimeException e) {
				System.err.println("错误的输入\n");
				i--;
			}
		}

		ctrl.commit();
	}
	
}
