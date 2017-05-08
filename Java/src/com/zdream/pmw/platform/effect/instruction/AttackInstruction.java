package com.zdream.pmw.platform.effect.instruction;

import com.zdream.pmw.monster.prototype.IPokemonDataType;
import com.zdream.pmw.monster.skill.ESkillCategory;
import com.zdream.pmw.platform.control.IPrintLevel;
import com.zdream.pmw.platform.effect.AInstruction;
import com.zdream.pmw.platform.effect.Aperitif;
import com.zdream.pmw.platform.effect.SkillReleasePackage;
import com.zdream.pmw.platform.effect.power.IPowerFormula;
import com.zdream.pmw.util.json.JsonObject;

/**
 * <p>计算技能攻击和技能威力的指示
 * </p>
 * 
 * @since v0.2.3 [2017-04-27]
 * @author Zdream
 * @version v0.2.3 [2017-04-27]
 */
public class AttackInstruction extends AInstruction {
	
	IPowerFormula powerf;
	JsonObject param;

	@Override
	public String name() {
		return CODE_CALC_AT;
	}

	@Override
	protected void execute() {
		calcPower();
		calcAt();
	}
	
	@Override
	public void set(JsonObject args) {
		this.param = args;
	}
	
	@Override
	public void restore() {
		this.param = null;
	}

	private void calcPower() {
		if (param == null) {
			powerf = defaultPowerFormula();
		} else {
			powerf = (IPowerFormula) loadFormula((JsonObject) param);
		}
		
		putInfo("Power formula: " + powerf.toString());
		
		int[] opowers = powerf.power(pack, em);
		float[] powers = new float[opowers.length];
		byte atseat = pack.getAtStaff().getSeat();
		
		for (int i = 0; i < opowers.length; i++) {
			Aperitif ap = em.newAperitif(Aperitif.CODE_CALC_POWER, atseat);
			ap.append("skillID", pack.getSkill().getId()).append("atseat", atseat)
					.append("dfseat", pack.getDfStaff(i).getSeat())
					.append("result", (float) opowers[i]);
			em.startCode(ap);
			
			powers[i] = (float) ap.get("result");
			em.logPrintf(IPrintLevel.PRINT_LEVEL_VERBOSE,
					"AttackInstruction.calcPower(1): seat=%d 的技能威力为 %f",
					pack.getAtStaff().getSeat(), powers[i]);
		}
		
		putData(SkillReleasePackage.DATA_KEY_POWERS, powers);
	}

	private void calcAt() {
		ESkillCategory category = pack.getSkill().getSkill().getCategory();
		int item;
		
		if (category.equals(ESkillCategory.PHYSICS)) {
			em.logPrintf(IPrintLevel.PRINT_LEVEL_VERBOSE,
					"AttackInstruction.calcAt(): 攻击选择物攻类型");
			item = IPokemonDataType.AT;
		} else if (category.equals(ESkillCategory.SPECIAL)) {
			em.logPrintf(IPrintLevel.PRINT_LEVEL_VERBOSE,
					"AttackInstruction.calcAt(): 攻击选择特攻类型");
			item = IPokemonDataType.SA;
		} else {
			em.logPrintf(IPrintLevel.PRINT_LEVEL_WARN,
					"AttackInstruction.calcAt(): 攻击类型不是物攻和特攻，无法选择");
			throw new IllegalArgumentException("category = " + category + 
					" 数据错误, 无法选择攻击类型");
		}
		
		byte[] targets = (byte[]) getData(SkillReleasePackage.DATA_KEY_TARGETS);
		byte atseat = pack.getAtStaff().getSeat();
		float[] ats = new float[targets.length];
		
		for (int i = 0; i < targets.length; i++) {
			// byte dfseat = targets[i];
			boolean ct = pack.isCtable(i);
			ats[i] = em.calcAbility(atseat, item, (ct) ? 2 : 0);
			
			em.logPrintf(IPrintLevel.PRINT_LEVEL_VERBOSE,
					"AttackInstruction.calcAt(): seat=%d 的攻击力 %f",
					atseat, ats[i]);
		}
		putData(SkillReleasePackage.DATA_KEY_ATS, ats);
	}
	
}
