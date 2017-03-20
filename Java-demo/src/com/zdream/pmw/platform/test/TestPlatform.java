package com.zdream.pmw.platform.test;

import java.time.Duration;
import java.time.Instant;
import java.util.Scanner;

import org.junit.Test;

import com.zdream.pmw.monster.data.SkillDataBuffer;
import com.zdream.pmw.monster.prototype.EPokemonGender;
import com.zdream.pmw.monster.prototype.EPokemonNature;
import com.zdream.pmw.monster.prototype.IPokemonService;
import com.zdream.pmw.monster.prototype.Pokemon;
import com.zdream.pmw.monster.service.PokemonServiceImpl;
import com.zdream.pmw.monster.skill.Skill;
import com.zdream.pmw.platform.attend.AttendManager;
import com.zdream.pmw.platform.attend.Attendant;
import com.zdream.pmw.platform.attend.Participant;
import com.zdream.pmw.platform.control.ControlBase;
import com.zdream.pmw.platform.control.IRequestKey;
import com.zdream.pmw.platform.prototype.BattlePlatform;
import com.zdream.pmw.platform.prototype.Fuse;
import com.zdream.pmw.platform.prototype.IBattleRule;
import com.zdream.pmw.platform.prototype.IMessageCallback;
import com.zdream.pmw.platform.prototype.IRequestCallback;
import com.zdream.pmw.platform.translate.MessageTranslator;
import com.zdream.pmw.trainer.prototype.Trainer;
import com.zdream.pmw.trainer.prototype.TrainerData;

public class TestPlatform {
	
	ControlBase base;
	
	BattlePlatform bp;
	
	Scanner scanner;
	
	// 用来测时间的
	Instant t = Instant.now();

	@Test
	public void test() {
		Pokemon pm1 = new Pokemon(); // 我方
		pm1.setSpeciesID((short) 4);
		pm1.setForm((byte) 0);
		pm1.setGender(EPokemonGender.M);
		pm1.setNaturn(EPokemonNature.JOLLY);
		pm1.setNickname("我的小火龙");
		pm1.setLevel((byte) 15);
		pm1.setStatIV(new byte[]{15, 15, 15, 15, 15, 15});
		pm1.getSkill()[0] = (short) 7;
		pm1.getSkill()[1] = (short) 16;
		pm1.getSkill()[2] = (short) 47;
		pm1.getSkill()[3] = (short) 261;
		pm1.getSkillPP()[0] = (byte) 35;
		pm1.getSkillPP()[1] = (byte) 15;
		pm1.getSkillPP()[2] = (byte) 35;
		pm1.getSkillPP()[3] = (byte) 20;
		pm1.setTrainerName("超级赛亚人");
		pm1.setTrainerID(66666);
		pm1.setTrainerSID(23333);
		pm1.setTrainerGender(EPokemonGender.M);
		
		Pokemon pm3 = new Pokemon(); // 我方
		pm3.setSpeciesID((short) 2);
		pm3.setForm((byte) 0);
		pm3.setGender(EPokemonGender.M);
		pm3.setNaturn(EPokemonNature.NAIVE);
		pm3.setNickname("我的妙蛙草");
		pm3.setLevel((byte) 15);
		pm3.setStatIV(new byte[]{15, 15, 15, 15, 15, 15});
		pm3.getSkill()[0] = (short) 7;
		pm3.getSkill()[1] = (short) 157;
		pm3.getSkill()[2] = (short) 74;
		pm3.getSkill()[3] = (short) 39;
		pm3.getSkillPP()[0] = (byte) 35;
		pm3.getSkillPP()[1] = (byte) 15;
		pm3.getSkillPP()[2] = (byte) 35;
		pm3.getSkillPP()[3] = (byte) 20;
		pm3.setTrainerName("超级赛亚人");
		pm3.setTrainerID(66666);
		pm3.setTrainerSID(23333);
		pm3.setTrainerGender(EPokemonGender.M);
		
		Pokemon pm2 = new Pokemon(); // 敌方
		pm2.setSpeciesID((short) 25);
		pm2.setForm((byte) 0);
		pm2.setGender(EPokemonGender.F);
		pm2.setNaturn(EPokemonNature.RASH);
		pm2.setNickname("别人皮卡丘");
		pm2.setLevel((byte) 15);
		pm2.setStatIV(new byte[]{21, 9, 21, 9, 21, 9});
		pm2.getSkill()[0] = (short) 10;
		pm2.getSkillPP()[0] = (byte) 35;
		
		IPokemonService service = PokemonServiceImpl.getInstance();
		service.countStatValue(pm1);
		service.recoverPokemon(pm1);
		service.countStatValue(pm2);
		service.recoverPokemon(pm2);
		service.countStatValue(pm3);
		service.recoverPokemon(pm3);
		
		Trainer trainer = Trainer.getThizPlayer();
		trainer.getPokemons()[0] = pm1;
		trainer.getPokemons()[1] = pm3;
		{
			TrainerData trainerdata = trainer.getData();
			trainerdata.setTrainerName("超级赛亚人");
			trainerdata.setTrainerID(66666);
			trainerdata.setTrainerSID(23333);
			trainerdata.setTrainerGender(EPokemonGender.M);
		}
		
		// 准备战斗
		Fuse fuse = new Fuse(IBattleRule.RULE_DEBUG);
		fuse.putTeams(trainer, new Pokemon[]{pm1, pm3}, new IRequestCallback() {
			
			@Override
			public void onRequest(BattlePlatform platform, ControlBase ctrl) {
				base = ctrl;
				
				String content = base.nextRequest();
				System.out.println();
				System.out.println("content: " + content);
				if (IRequestKey.VALUE_REQ_CONTENT_MOVE.equals(content)) {
					screenInfo(platform, ctrl);
					chooseMove(platform, ctrl);
				} else if (IRequestKey.VALUE_REQ_CONTENT_END.equals(content)) {
					System.out.println("战斗结束，结果：" + base.getResult());
				} else if (IRequestKey.VALUE_REQ_CONTENT_SWITCH.equals(content)) {
					System.out.println("要换人！");
					chooseMonsterAuto(platform, ctrl); // TODO 以后不要用自动选择换人
				}
			}
		}, new IMessageCallback[]{new MessageTranslator().getMessageCallback()});
		fuse.putTeams(null, new Pokemon[]{pm2});
		scanner = new Scanner(System.in);
		
		bp = fuse.initPlatform();
		bp.start();
		
		scanner.close();
	}

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
		byte curNo = am.noForSeat(seat);
		
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
	
	public static void main(String[] args) {
		new TestPlatform().test();
	}

}
