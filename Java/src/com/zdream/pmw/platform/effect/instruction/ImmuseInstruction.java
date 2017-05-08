package com.zdream.pmw.platform.effect.instruction;

import com.zdream.pmw.core.tools.TypeRestraint;
import com.zdream.pmw.monster.prototype.EPokemonType;
import com.zdream.pmw.platform.effect.AInstruction;
import com.zdream.pmw.platform.effect.Aperitif;
import com.zdream.pmw.platform.effect.SkillReleasePackage;
import com.zdream.pmw.util.json.JsonObject;

/**
 * <p>判断技能是否属性等其它部分免疫的指示
 * <p>该类处理的事务有:
 * <li>确定技能释放时的判定属性;
 * <li>确定各个怪兽对技能的免疫情况
 * </li></p>
 * 
 * @since v0.2.3 [2017-04-27]
 * @author Zdream
 * @version v0.2.3 [2017-04-27]
 */
public class ImmuseInstruction extends AInstruction {

	@Override
	public String name() {
		return "immuse";
	}

	@Override
	protected void execute() {
		EPokemonType atType = skillType();
		
		if (calcImmuse(atType)) {
			/*
			AInstruction ins;
		
			ins = (AInstruction) loadFormula("i.finish");
			ins.setPackage(pack);
			// set
			list.add(ins);
			 */
		} else {
			// 直接中断
			// 注意, 这里是当所有防御方都免疫时, 进入这里
			immuseAll();
		}
	}
	
	/**
	 * 判断该释放的技能的真正属性
	 * @return
	 */
	private EPokemonType skillType() {
		byte atseat = pack.getAtStaff().getSeat();
		Aperitif ap = em.newAperitif(CODE_CALC_TYPE, atseat);
		ap.append("seat", atseat).append("skill", pack.getSkill().getId())
				.append("result", pack.getSkill().getType().ordinal());
		em.startCode(ap);
		
		int type = (int) ap.get("result");
		putData(SkillReleasePackage.DATA_KEY_SKILL_TYPE, type);
		return EPokemonType.parseEnum(type);
	}
	
	/**
	 * 对每个防御方判断属性免疫和特殊技能免疫.
	 * @return
	 *  如果存在一个防御方不免疫, 返回 true
	 */
	private boolean calcImmuse(EPokemonType atType) {
		byte[] targets = (byte[]) getData(SkillReleasePackage.DATA_KEY_RANGE);
		boolean[] immuses = new boolean[targets.length];
		boolean result = false;
		byte atseat = pack.getAtStaff().getSeat();
		
		EPokemonType dfType;
		for (int i = 0; i < targets.length; i++) {
			final EPokemonType[] dfTypes = pack.getDfStaff(i).getTypes();
			boolean[] typeImmuses = new boolean[dfTypes.length];
			
			for (int j = 0; j < dfTypes.length; j++) {
				dfType = dfTypes[j];
				typeImmuses[j] = TypeRestraint.isImmune(atType, dfType);
			}
			
			byte dfseat = targets[i];
			Aperitif ap = em.newAperitif(Aperitif.CODE_JUDGE_IMMUSE, atseat, dfseat);
			ap.append("atseat", atseat).append("dfseat", dfseat)
				.append("atType", atType.name()).append("typeImmuses", typeImmuses)
				.append("skillId", pack.getSkill().getId());
			em.startCode(ap);

			// 最后判断
			typeImmuses = (boolean[]) ap.get("typeImmuses");
			for (int j = 0; j < typeImmuses.length; j++) {
				if (typeImmuses[j]) {
					immuses[i] = true;
					break;
				}
			}
			
			if (!immuses[i]) {
				result = true;
			} else {
				sendImmuse(atseat, dfseat);
			}
		}
		
		putData(SkillReleasePackage.DATA_KEY_IMMUSES, immuses);
		return result;
	}
	
	/**
	 * 如果所有的防御方都免疫了本次攻击, 将不进入判断命中的环节,
	 * 直接进入伤害与附加效果的判断环节
	 */
	private void immuseAll() {
		putInfo("immuse all");
		removeInstructions(CODE_CALC_HITRATE);
	}
	
	/**
	 * 发送未命中的消息
	 * @param atseat
	 * @param dfseat
	 */
	private void sendImmuse(byte atseat, byte dfseat) {
		JsonObject jo = new JsonObject();
		jo.put("type", "immuse");
		jo.put("-atseat", atseat);
		jo.put("-dfseat", dfseat);
		
		AInstruction ins = (AInstruction) loadFormula("i.broadcast");
		ins.setPackage(pack);
		ins.set(jo);
		putNextEvent(ins);
	}

}
