package com.zdream.pmw.platform.effect.instruction;

import com.zdream.pmw.core.tools.TypeRestraint;
import com.zdream.pmw.monster.prototype.EPokemonType;
import com.zdream.pmw.platform.control.IPrintLevel;
import com.zdream.pmw.platform.effect.AInstruction;
import com.zdream.pmw.platform.effect.Aperitif;
import com.zdream.pmw.platform.effect.SkillReleasePackage;
import com.zdream.pmw.util.json.JsonObject;
import com.zdream.pmw.util.json.JsonValue;
import com.zdream.pmw.util.random.RanValue;

/**
 * <p>计算技能最后修正的指示
 * <p>该修正包括:
 * <li>属性克制修正
 * <li>乱数修正
 * <li>会心攻击修正</li>
 * </p>
 * 
 * @since v0.2.3 [2017-04-27]
 * @author Zdream
 * @version v0.2.3 [2017-04-27]
 */
public class CorrectInstruction extends AInstruction {
	
	/**
	 * 是否计算属性克制, 默认 true
	 */
	boolean calcTypeRate = true;

	@Override
	public String name() {
		return CODE_CALC_CORRENT;
	}
	
	@Override
	public void set(JsonObject args) {
		JsonValue v;
		if ((v = args.getJsonValue("calcTypeRate")) != null) {
			calcTypeRate = (boolean) v.getValue();
		}
		
		super.set(args);
	}
	
	@Override
	public void restore() {
		calcTypeRate = true;
	}

	@Override
	protected void execute() {
		if (calcTypeRate)
			typeRate();
		calcCorrect();
	}
	
	private void typeRate() {
		byte atseat = pack.getAtStaff().getSeat(); // 攻击方的 seat
		byte dfseat;
		EPokemonType atType = pack.getType();
		EPokemonType[] dfTypes;
		
		byte[] targets = (byte[]) getData(SkillReleasePackage.DATA_KEY_TARGETS);
		float[] rates = new float[targets.length];
		
		for (int i = 0; i < targets.length; i++) {
			dfseat = targets[i];
			dfTypes = am.getParticipant(dfseat).getTypes();
			float result = 1.0f;
			float rate; // 缓存倍率
			
			for (int j = 0; j < dfTypes.length; j++) {
				EPokemonType dfType = dfTypes[j];
				rate = TypeRestraint.getRestraintRate(atType, dfType);

				Aperitif ap = em.newAperitif(Aperitif.CODE_CALC_TYPE_RATE, atseat, dfseat);
				ap.append("atseat", atseat).append("dfseat", dfseat).append("atType", atType.ordinal())
						.append("dfType", dfType.ordinal()).append("result", rate);
				em.startCode(ap);

				rate = (float) ap.get("result");
				result *= rate;
			}
			em.logPrintf(IPrintLevel.PRINT_LEVEL_VERBOSE,
					"CorrectInstruction.typeRate(): seat=%d 对 seat=%d 的技能释放属性倍率为 %f",
					atseat, dfseat, result);
			rates[i] = result;
		}
		
		putData(SkillReleasePackage.DATA_KEY_TYPE_RATES, rates);
	}
	
	/**
	 * 其它修正不包括属性克制
	 */
	private void calcCorrect() {
		byte[] targets = (byte[]) getData(SkillReleasePackage.DATA_KEY_TARGETS);
		float[] rates = new float[targets.length];
		byte atseat = pack.getAtStaff().getSeat();
		
		for (int i = 0; i < targets.length; i++) {
			float rate = (RanValue.random(0, 16) + 85) / 100.0f;
			if (pack.isCtable(i)) {
				rate *= 1.5f;
			}
			
			Aperitif ap = em.newAperitif(Aperitif.CODE_CALC_CORRENT,
					atseat, targets[i]);
			ap.append("atseat", atseat).append("dfseat", targets[i])
					.append("skillId", pack.getSkill().getId()).append("result", rate);
			em.startCode(ap);
			
			rates[i] = (float) ap.get("result");
		}
		
		putData(SkillReleasePackage.DATA_KEY_CORRECTS, rates);
	}

}
