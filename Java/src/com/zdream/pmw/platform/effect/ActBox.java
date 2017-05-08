package com.zdream.pmw.platform.effect;

import com.zdream.pmw.monster.prototype.EPokemonAbnormal;

/**
 * 除技能发动外的一切行动的操作层处理平台<br>
 * <p>可以处理技能释放外的操作，如:</p>
 * <li>交换退场</li>
 * 
 * @since v0.2.1
 * @author Zdream
 * @date 2017年3月14日
 * @version v0.2.1
 */
public class ActBox {
	
	/* ************
	 *	怪兽交换  *
	 ************ */
	
	/**
	 * 怪兽交换
	 * @param noOut
	 *   下场的怪兽, 必须是有效的参数
	 * @param noIn
	 *   上场的怪兽<br>
	 *   满足以下情况时, 该参数可以为 -1:<br>
	 *   <li>不确定上场怪兽, 需要让 <b>玩家或 AI</b> 进行判断由哪只怪兽上场时</li>
	 *   因此如果类似吼叫技能等，让怪兽上场不是玩家决定的, 在这里必须确定上场怪兽<br>
	 */
	public void exchangeAct(byte noOut, byte noIn) {
		byte seat = em.getAttends().seatForNo(noOut);
		byte team = em.getAttends().teamForNo(noOut);
		
		// 下场部分
		// 这里需要特殊考虑的情况是技能 接力棒
		Aperitif value = em.newAperitif(Aperitif.CODE_EXEUNT_EXCHANGE, seat);
		value.put("no", noOut);
		value.put("seat", seat);
		value.put("team", team);
		if (noIn != -1) {
			value.put("exchangeNo", noIn);
		}
		em.startCode(value);
		
		// 上场部分 TODO
		if (noIn == -1) {
			// TODO 让玩家选择上场的怪兽, noIn 被覆盖
		}
		
		enteranceAct(seat, noIn, team);
	}

	/**
	 * 怪兽上场
	 * @param seat
	 *   座位号
	 * @param noIn
	 *   上场的怪兽, 必须是有效的参数
	 * @since v0.2.1
	 */
	public void enteranceAct(byte seat, byte noIn) {
		byte team = em.getAttends().teamForNo(noIn);
		enteranceAct(seat, noIn, team);
	}
	
	private void enteranceAct(byte seat, byte noIn, byte team) {
		// 入场（前）
		Aperitif value = em.newAperitif(Aperitif.CODE_ENTRANCE, seat);
		value.put("team", team);
		value.put("no", noIn);
		value.put("seat", seat);
		em.startCode(value);
		
		// 入场（后）
		value = em.newAperitif(Aperitif.CODE_AFTER_ENTRANCE, seat);
		value.put("team", team);
		value.put("no", noIn);
		value.put("seat", seat);
		em.startCode(value);
	}
	
	/* ************
	 *	效果施加  *
	 ************ */
	
	/**
	 * 施加状态 (技能施加)
	 * @param atseat
	 *   攻击方
	 * @param dfseat
	 *   防御方, 就是确定要施加状态的一方
	 * @param abnormal
	 *   {@link com.zdream.pmw.core.tools.AbnormalMethods#toBytes(EPokemonAbnormal, int)}
	 */
	public void forceAbnormal(byte atseat, byte dfseat, byte abnormal) {
		Aperitif ap = em.newAperitif("force-abnormal", atseat, dfseat);
		
		ap.append("dfseat", dfseat)
				.append("atseat", atseat)
				.append("abnormal", abnormal)
				.append("result", 0);
		em.startCode(ap);
	}
	
	/**
	 * 施加状态 (系统施加)
	 * @param pack
	 * @param abnormal
	 *   {@link com.zdream.pmw.core.tools.AbnormalMethods#toBytes(EPokemonAbnormal, int)}
	 */
	public void forceAbnormal(byte seat, byte abnormal) {
		Aperitif value = em.newAperitif(Aperitif.CODE_FORCE_ABNORMAL, seat);
		value.put("dfseat", seat);
		value.put("atseat", (byte) -1);
		value.put("abnormal", abnormal);
		value.put("result", 0);
		em.startCode(value);
	}
	
	/* ************
	 *	构造函数  *
	 ************ */
	
	EffectManage em;
	
	public ActBox(EffectManage em) {
		this.em = em;
	}

}
