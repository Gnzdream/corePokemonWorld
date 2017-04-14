package com.zdream.pmw.platform.test;

import java.util.Random;
import java.util.Scanner;

import com.zdream.pmw.monster.data.PokemonBaseData;
import com.zdream.pmw.monster.prototype.EPokemonGender;
import com.zdream.pmw.monster.prototype.EPokemonNature;
import com.zdream.pmw.monster.prototype.Pokemon;
import com.zdream.pmw.monster.prototype.PokemonHandler;
import com.zdream.pmw.monster.skill.Skill;
import com.zdream.pmw.platform.prototype.Fuse;
import com.zdream.pmw.platform.prototype.IBattleRule;
import com.zdream.pmw.platform.prototype.IMessageCallback;
import com.zdream.pmw.platform.translate.MessageTranslator;
import com.zdream.pmw.trainer.prototype.Trainer;
import com.zdream.pmw.trainer.prototype.TrainerData;
import com.zdream.pmwdb.asset.dao.PokemonDataDaoImpl;
import com.zdream.pmwdb.asset.dao.SkillDaoImpl;

public class TestPlatform extends SimplePlatformHandler {

	public void test() {
		Pokemon pm1 = new Pokemon(); // 我方
		pm1.setSpeciesID((short) 4);
		pm1.setForm((byte) 0);
		pm1.setGender(EPokemonGender.M);
		pm1.setNature(EPokemonNature.JOLLY);
		pm1.setNickname("我的小火龙");
		pm1.setLevel((byte) 15);
		pm1.setStatIV(15, 15, 15, 15, 15, 15);
		pm1.getSkill()[0] = (short) 19; // 7
		pm1.getSkill()[1] = (short) 141; // 35 舞剑14 吸血141 巴掌3 龙之怒82
		pm1.getSkill()[2] = (short) 49;
		pm1.getSkill()[3] = (short) 54;
		pm1.getSkillPP()[0] = (byte) 35;
		pm1.getSkillPP()[1] = (byte) 15;
		pm1.getSkillPP()[2] = (byte) 35;
		pm1.getSkillPP()[3] = (byte) 35;
		pm1.setTrainerName("超级赛亚人");
		pm1.setTrainerID(66666);
		pm1.setTrainerSID(23333);
		pm1.setTrainerGender(EPokemonGender.M);
		
		Pokemon pm3 = new Pokemon(); // 我方
		pm3.setSpeciesID((short) 2);
		pm3.setForm((byte) 0);
		pm3.setGender(EPokemonGender.M);
		pm3.setNature(EPokemonNature.NAIVE);
		pm3.setNickname("我的妙蛙草");
		pm3.setLevel((byte) 15);
		pm3.setStatIV(15, 15, 15, 15, 15, 15);
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
		pm2.setNature(EPokemonNature.RASH);
		pm2.setNickname("别人皮卡丘");
		pm2.setLevel((byte) 15);
		pm2.setStatIV(21, 9, 21, 9, 21, 9);
		pm2.getSkill()[0] = (short) 10;
		pm2.getSkillPP()[0] = (byte) 35;
		pm2.getSkill()[1] = (short) 45;
		pm2.getSkillPP()[1] = (byte) 35;
		
		Pokemon pm4 = new Pokemon(); // 敌方
		pm4.setSpeciesID((short) 92);
		pm4.setForm((byte) 0);
		pm4.setGender(EPokemonGender.M);
		pm4.setNature(EPokemonNature.CALM);
		pm4.setNickname("别人鬼斯");
		pm4.setLevel((byte) 15);
		pm4.setStatIV(21, 9, 21, 9, 21, 9);
		pm4.getSkill()[0] = (short) 55;
		pm4.getSkillPP()[0] = (byte) 25;
		
		PokemonHandler service = PokemonHandler.getInstance();
		service.countStatValue(pm1);
		service.recoverPokemon(pm1);
		service.countStatValue(pm2);
		service.recoverPokemon(pm2);
		service.countStatValue(pm3);
		service.recoverPokemon(pm3);
		service.countStatValue(pm4);
		service.recoverPokemon(pm4);
		
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
		MessageTranslator translator = new MessageTranslator();
		translator.setPrint(true);
		
		fuse.putTeams(trainer, new Pokemon[]{pm1, pm3}, callback, new IMessageCallback[]{translator.getMessageCallback()});
		fuse.putTeams(null, new Pokemon[]{pm2, pm4});
		translator.setTeam((byte) 0);
		scanner = new Scanner(System.in);
		
		bp = fuse.initPlatform();
		bp.start();
		
		scanner.close();
	}
	
	void testRandom() {
		Random r = new Random();
		Trainer trainer = Trainer.getThizPlayer();
		TrainerData trainerdata = trainer.getData();
		trainerdata.setTrainerName("超级赛亚人");
		trainerdata.setTrainerID(66666);
		trainerdata.setTrainerSID(23333);
		trainerdata.setTrainerGender(EPokemonGender.M);
		
		PokemonDataDaoImpl dao = new PokemonDataDaoImpl();
		SkillDaoImpl sdao = new SkillDaoImpl();
		PokemonHandler service = PokemonHandler.getInstance();
		
		Pokemon[] pms0 = new Pokemon[3], pms1 = new Pokemon[3];
		
		// 0-我方 1-敌方
		for (int camp = 0; camp < 2; camp++) {
			Pokemon[] pms;
			if (camp == 0) {
				pms = pms0;
			} else {
				pms = pms1;
			}
			
			for (int i = 0; i < pms.length; i++) {
				Pokemon pm = new Pokemon();
				short id;
				PokemonBaseData data;
				
				while (true) {
					id = (short) r.nextInt(151);
					try {
						if ((data = dao.getData(id)) != null)
							break;
					} catch (Exception e) {}
					System.err.println("[怪兽 #" + id + " 不存在]");
				}
				pm.setSpeciesID(id);
				pm.setForm((byte) 0);
				pm.setGender((r.nextInt(2) == 0) ? EPokemonGender.M : EPokemonGender.F);
				pm.setNature(EPokemonNature.values()[r.nextInt(25)]);
				pm.setNickname(((camp == 0) ? "我的" : "敌方") + data.getSpeciesName());
				pm.setLevel((byte) 15);
				pm.setStatIV(r.nextInt(32), r.nextInt(32), r.nextInt(32),
						r.nextInt(32), r.nextInt(32), r.nextInt(32));
				
				for (int j = 0; j < 4; j++) {
					Skill sk;
					short sid;
					
					while (true) {
						sid = (short) r.nextInt(100);
						try {
							if ((sk = sdao.getSkill(sid)) != null)
								break;
						} catch (Exception e) {}
						System.err.println("[技能 #" + sid + " 不存在]");
					}
					pm.getSkill()[j] = sid;
					pm.getSkillPP()[j] = sk.getPpMax();
				}
				
				if (camp == 0) {
					pm.setTrainerName(trainerdata.getTrainerName());
					pm.setTrainerID(trainerdata.getTrainerID());
					pm.setTrainerSID(trainerdata.getTrainerSID());
					pm.setTrainerGender(EPokemonGender.M);
				}
				service.countStatValue(pm);
				service.recoverPokemon(pm);
				
				pms[i] = pm;
				trainer.getPokemons()[i] = pm;
			}
		}
		
		// 准备战斗
		Fuse fuse = new Fuse(IBattleRule.RULE_DEBUG);
		MessageTranslator translator = new MessageTranslator();
		translator.setPrint(true);
		
		fuse.putTeams(trainer, pms0, callback, new IMessageCallback[]{translator.getMessageCallback()});
		fuse.putTeams(null, pms1);
		translator.setTeam((byte) 0);
		scanner = new Scanner(System.in);
		
		bp = fuse.initPlatform();
		bp.start();
		
		scanner.close();
	}
	
	public static void main(String[] args) {
		// new TestPlatform().testRandom();
		new TestPlatform().test();
	}

}
