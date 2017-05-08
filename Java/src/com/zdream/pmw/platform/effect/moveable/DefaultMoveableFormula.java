package com.zdream.pmw.platform.effect.moveable;

import com.zdream.pmw.platform.control.IPrintLevel;
import com.zdream.pmw.platform.effect.Aperitif;
import com.zdream.pmw.platform.effect.EffectManage;
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
 * <p><b>v0.2.3</b><br>
 * 添加查询无法行动原因的方法
 * </p>
 * 
 * @since v0.1 [2016-04-08]
 * @author Zdream
 * @version v0.2.3 [2017-05-04]
 */
public class DefaultMoveableFormula implements IMoveableFormula {
	
	/**
	 * 当判断无法行动时, 该属性将缓存原因.
	 */
	protected String reason;
	
	@Override
	public String name() {
		return "default";
	}

	@Override
	public boolean canMove(SkillReleasePackage p, EffectManage em) {
		this.reason = null;
		
		// 1. PP 值是否剩余
		byte pp = p.getAtStaff().getAttendant().getSkillPP()[p.getSkillNum()];
		if (pp == 0) {
			em.logPrintf(
					IPrintLevel.PRINT_LEVEL_WARN, "精灵 %s 由于技能 %s 没有 PP 而无法发动", 
					p.getAtStaff().getAttendant().getNickname(),
					p.getSkill().getSkill().getTitle());
			return false;
		} else {
			em.logPrintf(
					IPrintLevel.PRINT_LEVEL_VERBOSE, "精灵 %s 的技能 %s PP 为 %d", 
					p.getAtStaff().getAttendant().getNickname(),
					p.getSkill().getSkill().getTitle(), pp);
		}
		
		// 2. 
		Aperitif ap = em.newAperitif(
				Aperitif.CODE_JUDGE_MOVEABLE, p.getAtStaff().getSeat());
		ap.append("seat", p.getAtStaff().getSeat());
		ap.append("skillNum", p.getSkillNum());
		ap.append("result", true);
		em.startCode(ap);
		
		boolean result = (boolean) ap.get("result");
		if (!result) {
			reason = ap.get("fail").toString();
		}
		return result;
	}
	
	@Override
	public String reasonOf() {
		return reason;
	}
	
	@Override
	public void restore() {
		this.reason = null;
	}
	
	/* ************
	 *	数据结构  *
	 ************ */
	
	/* ************
	 *	 构造器   *
	 ************ */

	public DefaultMoveableFormula() {
		
	}
	
	@Override
	public String toString() {
		return name();
	}

}
