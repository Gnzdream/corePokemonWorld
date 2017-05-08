package com.zdream.pmw.platform.effect.instruction;

import com.zdream.pmw.platform.effect.AInstruction;
import com.zdream.pmw.platform.effect.Aperitif;
import com.zdream.pmw.platform.effect.SkillReleasePackage;
import com.zdream.pmw.platform.effect.damage.IDamageFormula;
import com.zdream.pmw.util.json.JsonObject;

/**
 * <p>用于计算释放技能的威力的指示
 * </p>
 * 
 * @since v0.2.3 [2017-05-02]
 * @author Zdream
 * @version v0.2.3 [2017-05-04]
 */
public class DamageInstruction extends AInstruction {
	
	JsonObject param;
	IDamageFormula damagef;

	@Override
	public String name() {
		return "damage";
	}
	
	@Override
	public void set(JsonObject args) {
		param = args;
	}
	
	@Override
	public void restore() {
		param = null;
	}

	@Override
	protected void execute() {
		if (param == null) {
			damagef = defaultDamageFormula();
		} else {
			damagef = (IDamageFormula) loadFormula(param);
		}
		
		putInfo("Damage formula: " + damagef.toString());
		
		// 计算
		int[] damages = damagef.damage(pack, em);
		
		// 修正
		calcDamages(damages);
		
		putData(SkillReleasePackage.DATA_KEY_DAMAGES, damages);
		
		// 实现
		executeDamages(damages);
	}
	
	/**
	 * @param damages
	 */
	private void calcDamages(int[] damages) {
		byte[] targets = pack.getTargets();
		byte atseat = pack.getAtStaff().getSeat();
		short skillId = pack.getSkill().getId();
		
		for (int i = 0; i < targets.length; i++) {
			byte dfseat = targets[i];
			
			Aperitif ap = em.newAperitif(Aperitif.CODE_CALC_DAMAGE, dfseat);
			ap.append("atseat", atseat).append("dfseat", dfseat).append("skillID", skillId)
					.append("result", damages[i]);
			em.startCode(ap);
			
			damages[i] = (int) ap.get("result");
		}
	}

	protected void executeDamages(int[] damages) {
		byte[] targets = pack.getTargets();
		
		for (int i = 0; i < targets.length; i++) {
			byte dfseat = targets[i];
			int effect = pack.typeRateEffect(i);
			
			// 伤害刷新: 最高到防御方的 hpi v0.2.2
			int damage = damages[i];
			int hpi = pack.getDfStaff(i).getHpi();
			if (damage > hpi) {
				damage = hpi;
				damages[i] = hpi;
			}
			
			Aperitif ap = em.newAperitif(Aperitif.CODE_SKILL_DAMAGE, dfseat);
			ap.append("seat", dfseat).append("value", damage).append("ctable", pack.isCtable(i))
					.append("effect", effect);
			em.startCode(ap);

			damages[i] = (int) ap.get("value");
		}
	}

}
