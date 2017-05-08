package com.zdream.pmw.platform.effect.instruction;

import com.zdream.pmw.platform.control.IPrintLevel;
import com.zdream.pmw.platform.effect.AInstruction;
import com.zdream.pmw.platform.effect.Aperitif;
import com.zdream.pmw.platform.effect.SkillReleasePackage;
import com.zdream.pmw.platform.effect.accuracy.IAccuracyFormula;
import com.zdream.pmw.util.json.JsonObject;
import com.zdream.pmw.util.random.RanValue;

/**
 * <p>判断技能是否命中的指示
 * <p>该类处理的事务有:
 * <li>确定技能的命中率;
 * <li>确定攻击方的命中率;
 * <li>确定防御方的躲避率;
 * </li></p>
 * 
 * @since v0.2.3 [2017-04-27]
 * @author Zdream
 * @version v0.2.3 [2017-04-27]
 */
public class HitInstruction extends AInstruction {
	
	JsonObject param;
	IAccuracyFormula accf;
	
	public IAccuracyFormula getAccuracy() {
		return accf;
	}

	@Override
	public String name() {
		return CODE_CALC_HITRATE;
	}
	
	@Override
	public void set(JsonObject args) {
		this.param = args;
	}
	
	@Override
	public void restore() {
		super.restore();
		this.param = null;
	}

	@Override
	protected void execute() {
		if (param != null)
			accf = (IAccuracyFormula) loadFormula(param);
		else
			accf = defaultAccuracyFormula();
		
		putInfo("Accuracy formula: " + accf.toString());
		
		byte atseat = pack.getAtStaff().getSeat();
		
		// float acc = calcAccuracy();
		float hitrate = calcHitrate(atseat);
		
		final int len = pack.dfStaffLength();
		float[] accs = new float[len];
		float[] hides = new float[len];
		boolean[] hits = new boolean[len];
		
		for (int i = 0; i < len; i++) {
			byte dfseat = pack.getDfStaff(i).getSeat();
			accs[i] = calcAccuracy(atseat, dfseat);
			hides[i] = calcHide(atseat, dfseat);
			
			boolean result = judgeHit(hitrate, hides[i], accs[i]);
			hits[i] = result;
			
			em.logPrintf(IPrintLevel.PRINT_LEVEL_VERBOSE,
					"HitInstruction.judgeHit(3): %s(seat=%d) 对 %s(seat=%d) 的命中判定 %s",
					am.getParticipant(atseat), atseat, am.getParticipant(dfseat), dfseat,
					(result) ? "成功命中" : "未命中");
			if (!result) {
				sendMiss(atseat, dfseat);
			}
		}
		
		putData(SkillReleasePackage.DATA_KEY_HITRATE, hitrate);
		putData(SkillReleasePackage.DATA_KEY_ACCURACY, accs);
		putData(SkillReleasePackage.DATA_KEY_HIDES, hides);
		putData(SkillReleasePackage.DATA_KEY_HITS, hits);
	}
	
	/**
	 * 发送未命中的消息
	 * @param atseat
	 * @param dfseat
	 */
	private void sendMiss(byte atseat, byte dfseat) {
		JsonObject jo = new JsonObject();
		jo.put("type", "miss");
		jo.put("-atseat", atseat);
		jo.put("-dfseat", dfseat);
		
		AInstruction ins = (AInstruction) loadFormula("i.broadcast");
		ins.setPackage(pack);
		ins.set(jo);
		putNextEvent(ins);
	}

	/**
	 * 计算攻击方实时命中率（忽略能力变化）
	 * @return v0.2.1
	 *   当返回数值为 0 则为必不中, -1 则为必中, 默认 1
	 */
	protected float calcHitrate(byte atseat) {
		Aperitif ap = em.newAperitif(Aperitif.CODE_CALC_HITRATE, atseat);
		ap.put("seat", atseat);
		ap.put("result", 1.0f);
		ap.put("abs", false);
		em.startCode(ap);
		
		boolean abs = (boolean) ap.get("abs");
		if (abs == true) {
			return -1.0f;
		}
		
		return (float) ap.get("result");
	}
	
	/**
	 * 计算防御方实时躲避率（忽略能力变化）
	 * @return v0.2.1
	 *   当返回数值为 0 则为必中, -1 则为必不中, 默认 1,
	 *   计算时计算攻击方命中等级和防御方躲避等级
	 */
	protected float calcHide(byte atseat, byte dfseat) {
		Aperitif ap = em.newAperitif(Aperitif.CODE_CALC_HIDE, dfseat);
		ap.append("seat", dfseat).append("result", 1.0f / am.hitableLevel(atseat, dfseat))
				.append("abs", false);
		em.startCode(ap);
		
		boolean abs = (boolean) ap.get("abs");
		if (abs == true) {
			return -1.0f;
		}
		
		return (float) ap.get("result");
	}
	
	/**
	 * 计算技能实时命中率
	 * @return
	 *   当返回数值为 0 则为必中, 默认值为 0-100, 视技能命中率而定.
	 *   返回值不会为负数.
	 */
	protected float calcAccuracy(byte atseat, byte dfseat) {
		short skillId = pack.getSkill().getId();
		float base = accf.accuracy(pack);
		
		Aperitif ap = em.newAperitif(Aperitif.CODE_CALC_ACCURACY, atseat, dfseat);
		ap.append("atseat", atseat).append("dfseat", dfseat).append("skill", skillId)
				.append("result", base).append("abs", 0);
		em.startCode(ap);
		
		return (float) ap.get("result");
	}
	
	/**
	 * 计算是否命中
	 */
	protected boolean judgeHit(float hitrate, float hide, float acc) {
		// 绝对防御 > 绝对命中 > 技能绝对命中
		if (hide == -1) {
			// 必不中
			return false;
		} else if (hide == 0) {
			// 必中
			return true;
		}
		
		if (hitrate == -1) {
			// 必中
			return true;
		} else if (hitrate == 0) {
			// 必不中
			return false;
		}
		
		if (acc == 0) {
			return true;
		}
		
		float rate = (acc * hitrate / hide);

		boolean result = !RanValue.isBigger((int) (rate * 1000), 1000);
		return result;
	}

}
