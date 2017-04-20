package com.zdream.pmw.platform.effect.moveable;

import com.zdream.pmw.platform.effect.Aperitif;
import com.zdream.pmw.platform.effect.SkillReleasePackage;

/**
 * <p>硬直</p>
 * <p>使用了下一回合自己将无法动弹的技能之后, 第二回合就会产生硬直</p>
 * <p>特点
 * <li>默认为不能行动</li></p>
 * 
 * @since v0.2.2 [2017-04-17]
 * @author Zdream
 * @version v0.2.2 [2017-04-17]
 */
public class HesitantMoveableFormula implements IMoveableFormula {

	@Override
	public String name() {
		return "hesitant";
	}

	@Override
	public boolean canMove(SkillReleasePackage p) {
		Aperitif ap = p.getEffects().newAperitif(
				Aperitif.CODE_JUDGE_MOVEABLE, p.getAtStaff().getSeat());
		ap.append("seat", p.getAtStaff().getSeat());
		ap.append("skillNum", p.getSkillNum());
		ap.append("fail", name());
		ap.append("result", false);
		p.getEffects().startCode(ap);
		
		boolean result = (boolean) ap.getMap().get("result").getValue();
		
		if (!result) {
			ap = p.getEffects().newAperitif(Aperitif.CODE_BROADCAST);
			ap.append("type", name());
			p.getEffects().startCode(ap);
		}
		
		return result;
	}

}
