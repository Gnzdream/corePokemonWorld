package com.zdream.pmw.platform.effect.moveable;

import com.zdream.pmw.platform.control.IPrintLevel;
import com.zdream.pmw.platform.effect.Aperitif;
import com.zdream.pmw.platform.effect.SkillReleasePackage;

/**
 * 默认行动判定<br>
 * 判定方法：<br>
 * 1. PP 值是否剩余<br>
 * 2. 状态的影响
 * <br>
 * <b>v0.2</b><br>
 *   取消对 pf.instance 的引用<br>
 * 
 * @since v0.1
 * @author Zdream
 * @date 2016年4月8日
 * @version v0.2
 */
public class DefaultMoveableFormula implements IMoveableFormula {
	
	@Override
	public String name() {
		return "default";
	}

	@Override
	public boolean canMove(SkillReleasePackage p) {
		// 1. PP 值是否剩余
		byte pp = p.getAtStaff().getAttendant().getSkillPP()[p.getSkillNum()];
		if (pp == 0) {
			p.getEffects().logPrintf(
					IPrintLevel.PRINT_LEVEL_WARN, "精灵 %s 由于技能 %s 没有 PP 而无法发动", 
					p.getAtStaff().getAttendant().getNickname(),
					p.getSkill().getSkill().getTitle());
			return false;
		} else {
			p.getEffects().logPrintf(
					IPrintLevel.PRINT_LEVEL_VERBOSE, "精灵 %s 的技能 %s PP 为 %d", 
					p.getAtStaff().getAttendant().getNickname(),
					p.getSkill().getSkill().getTitle(), pp);
		}
		
		// 2. 
		Aperitif value = p.getEffects().newAperitif(
				Aperitif.CODE_JUDGE_MOVEABLE, p.getAtStaff().getSeat());
		value.append("seat", p.getAtStaff().getSeat());
		value.append("skillNum", p.getSkillNum());
		value.append("result", true);
		p.getEffects().startCode(value);
		
		return (Boolean) value.getMap().get("result").getValue();
	}
	
	/* ************
	 *	数据结构  *
	 ************ */
	
	/* ************
	 *	 构造器   *
	 ************ */

	public DefaultMoveableFormula() {
		
	}

}
