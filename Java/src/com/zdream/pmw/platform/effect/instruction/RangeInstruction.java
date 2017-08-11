package com.zdream.pmw.platform.effect.instruction;

import com.zdream.pmw.platform.effect.AInstruction;
import com.zdream.pmw.platform.effect.Aperitif;
import com.zdream.pmw.platform.effect.SkillReleasePackage;
import com.zdream.pmw.platform.effect.range.IRangeFormula;
import com.zdream.pmw.util.json.JsonObject;

/**
 * <p>判断技能的释放范围
 * </p>
 * 
 * @since v0.2.3 [2017-04-26]
 * @author Zdream
 * @version v0.2.3 [2017-04-26]
 */
public class RangeInstruction extends AInstruction {
	
	IRangeFormula rangeFormula;
	JsonObject param;
	
	public IRangeFormula getRangeFormula() {
		return rangeFormula;
	}

	@Override
	public String name() {
		return CODE_JUDGE_RANGE;
	}
	
	@Override
	public void set(JsonObject args) {
		param = args;
	}
	
	@Override
	public void restore() {
		this.param = null;
		super.restore();
	}

	@Override
	protected void execute() {
		if (param != null) {
			if (param.get("v") != null) {
				rangeFormula = (IRangeFormula) loadFormula("r." + param.get("v").toString());
				rangeFormula.set(param);
			}
		}
		if (rangeFormula == null)
			rangeFormula = skillRangeFormula(pack.getSkill().getRange());
		
		putInfo("Range formula: " + rangeFormula.toString());
		
		byte[] seats = rangeFormula.range(pack, em);

		Aperitif ap = em.newAperitif(Aperitif.CODE_JUDGE_RANGE);
		ap.put("seat", pack.getAtStaff().getSeat());
		ap.put("skill", getData(SkillReleasePackage.DATA_KEY_SKILL_ID));
		ap.put("result", seats);
		em.getRoot().readyCode(ap);
		
		seats = (byte[]) ap.get("result");
		putData(SkillReleasePackage.DATA_KEY_RANGE, seats);
		byte[] nos = new byte[seats.length];
		
		for (int i = 0; i < seats.length; i++) {
			byte seat = seats[i];
			nos[i] = am.noForSeat(seat);
			pack.setDfStaff(em.getAttends().getParticipant(seat), i);
		}
		putData(SkillReleasePackage.DATA_KEY_RANGE_NO, nos);
	}

}
