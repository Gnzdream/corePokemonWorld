package com.zdream.pmw.platform.effect.instruction;

import com.zdream.pmw.monster.prototype.IPokemonDataType;
import com.zdream.pmw.monster.skill.ESkillCategory;
import com.zdream.pmw.platform.control.IPrintLevel;
import com.zdream.pmw.platform.effect.AInstruction;
import com.zdream.pmw.platform.effect.SkillReleasePackage;

/**
 * <p>计算防御方防御部分的指示
 * </p>
 * 
 * @since v0.2.3 [2017-04-27]
 * @author Zdream
 * @version v0.2.3 [2017-04-27]
 */
public class DefenseInstruction extends AInstruction {

	@Override
	public String name() {
		return CODE_CALC_DF;
	}

	@Override
	protected void execute() {
		calcDf();
	}
	
	private void calcDf() {
		ESkillCategory category = pack.getSkill().getCategory();
		int item;
		
		if (category.equals(ESkillCategory.PHYSICS)) {
			em.logPrintf(IPrintLevel.PRINT_LEVEL_VERBOSE,
					"AttackInstruction.calcAt(): 防御选择物防类型");
			item = IPokemonDataType.DF;
		} else if (category.equals(ESkillCategory.SPECIAL)) {
			em.logPrintf(IPrintLevel.PRINT_LEVEL_VERBOSE,
					"AttackInstruction.calcAt(): 防御选择特防类型");
			item = IPokemonDataType.SD;
		} else {
			em.logPrintf(IPrintLevel.PRINT_LEVEL_WARN,
					"AttackInstruction.calcAt(): 防御类型不是物防和特防，无法选择");
			throw new IllegalArgumentException("category = " + category + 
					" 数据错误, 无法选择防御类型");
		}
		
		byte[] targets = (byte[]) getData(SkillReleasePackage.DATA_KEY_TARGETS);
		float[] dfs = new float[targets.length];
		
		for (int i = 0; i < targets.length; i++) {
			boolean ct = pack.isCtable(i);
			byte dfseat = targets[i];
			
			dfs[i] = em.calcAbility(dfseat, item, (ct) ? 1 : 0);
			
			em.logPrintf(IPrintLevel.PRINT_LEVEL_VERBOSE,
					"DefenseInstruction.calcDf(): %s(seat=%d) 的防御为 %f",
					am.getParticipant(dfseat).getNickname(), dfseat, dfs[i]);
		}
		putData(SkillReleasePackage.DATA_KEY_DFS, dfs);
	}

}
