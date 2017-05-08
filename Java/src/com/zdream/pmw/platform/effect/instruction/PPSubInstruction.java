package com.zdream.pmw.platform.effect.instruction;

import com.zdream.pmw.platform.effect.AInstruction;
import com.zdream.pmw.platform.effect.Aperitif;

/**
 * <p>扣除 PP 值的指示
 * </p>
 * 
 * @since v0.2.3 [2017-04-25]
 * @author Zdream
 * @version v0.2.3 [2017-04-25]
 */
public class PPSubInstruction extends AInstruction {

	@Override
	public String name() {
		return CODE_PP_SUB;
	}

	@Override
	protected void execute() {
		byte seat = pack.getAtStaff().getSeat();
		Aperitif ap = em.newAperitif(Aperitif.CODE_PP_SUB, seat);
		
		ap.put("seat", seat);
		ap.put("skillNum", pack.getSkillNum());
		ap.put("skillID", pack.getSkill().getId());
		ap.put("value", 1); // 这里默认是 1
		em.getRoot().readyCode(ap);
	}

}
