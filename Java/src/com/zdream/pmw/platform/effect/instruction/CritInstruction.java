package com.zdream.pmw.platform.effect.instruction;

import com.zdream.pmw.platform.control.IPrintLevel;
import com.zdream.pmw.platform.effect.AInstruction;
import com.zdream.pmw.platform.effect.Aperitif;
import com.zdream.pmw.platform.effect.SkillReleasePackage;
import com.zdream.pmw.util.random.RanValue;

/**
 * <p>判断技能发动是否命中要害的指示
 * </p>
 * 
 * @since v0.2.3 [2017-04-27]
 * @author Zdream
 * @version v0.2.3 [2017-04-27]
 */
public class CritInstruction extends AInstruction {

	@Override
	public String name() {
		return CODE_CALC_CT;
	}

	@Override
	protected void execute() {
		calcCrit();
	}
	
	protected void calcCrit() {
		byte[] targets = (byte[]) getData(SkillReleasePackage.DATA_KEY_TARGETS);
		final int len = targets.length;
		byte dfseat;
		byte atseat = pack.getAtStaff().getSeat();
		byte[] ctLevels = new byte[len];
		boolean[] cts = new boolean[len];
		
		for (int i = 0; i < len; i++) {
			dfseat = targets[i];
			
			Aperitif ap = em.newAperitif(Aperitif.CODE_CALC_CT, atseat, dfseat);
			ap.append("atseat", atseat).append("dfseat", dfseat)
					.append("result", pack.getSkill().getCritLevel()).append("abs", 0);
			em.startCode(ap);
			
			int abs = (int) ap.get("abs");
			switch (abs) {
			case 1:
				ctLevels[i] = (byte) -1;
				cts[i] = true;
			case 2:
				ctLevels[i] = (byte) -2;
				cts[i] = false;
			default: 
				{
					byte ctLevel = (byte) ap.get("result");
					if (ctLevel > 3) {
						ctLevel = 3;
					}
					ctLevels[i] = ctLevel;
					
					switch (ctLevel) {
					case 1:
						cts[i] = !RanValue.isBigger(0, 8);
						break;
					case 2:
						cts[i] = !RanValue.isBigger(0, 2);
						break;
					case 3:
						cts[i] = true;
						break;
					case 0: default:
						cts[i] = !RanValue.isBigger(0, 16);
						break;
					}
				}
			}
			
			em.logPrintf(IPrintLevel.PRINT_LEVEL_VERBOSE,
					"CritInstruction.calcCrit(1): %s(seat=%d) 对 %s(seat=%d) 的攻击%s", 
					pack.getAtStaff().getNickname(), atseat,
					am.getParticipant(dfseat).getNickname(), dfseat,
					(cts[i]) ? "命中了要害" : "没有命中要害");
		}
		putData(SkillReleasePackage.DATA_KEY_CRIT_LVS, ctLevels); // crit
		putData(SkillReleasePackage.DATA_KEY_CRITS, cts);
		
	}

}
