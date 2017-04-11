package com.zdream.pmw.platform.effect.addition;

import java.util.Map;

import com.zdream.pmw.platform.effect.Aperitif;
import com.zdream.pmw.platform.effect.EffectManage;
import com.zdream.pmw.util.json.JsonValue;
import com.zdream.pmw.util.random.RanValue;

/**
 * 添加状态判定实现类<br>
 * 向我方或敌方施加某个座位状态<br>
 * 
 * @since v0.2.1
 * @author Zdream
 * @date 2017年3月2日
 * @version v0.2.1
 */
public class ParticipantStateAdditionFormula extends AAdditionFormula {
	
	/* ************
	 *	数据结构  *
	 ************ */
	
	/**
	 * 附加状态施加的可能性（百分比制）<br>
	 * 参数 100 为百分之百施加<br>
	 */
	private int rate;
	
	/**
	 * 状态名
	 */
	private String stateInfo;
	
	/**
	 * 向哪方进行状态的施加<br>
	 * 是攻击方还是防御方<br>
	 * 参数 1 为攻击方，0 为防御方（默认）<br>
	 */
	private int side;
	
	/**
	 * 从技能原始数据 (数据库中) 传来的技能参数数据<br>
	 * 里面包含了需要生成状态的构造所需数据<br>
	 */
	private JsonValue value;
	
	/**
	 * 向攻击方施加状态
	 */
	public static final int SIDE_ATSTAFF = 1;
	
	/**
	 * 向防御方施加状态
	 */
	public static final int SIDE_DFSTAFF = 0;
	
	public int getRate() {
		return rate;
	}
	public void setRate(int rate) {
		this.rate = rate;
	}
	public String getStateInfo() {
		return stateInfo;
	}
	public void setStateInfo(String stateInfo) {
		this.stateInfo = stateInfo;
	}
	public void setSide(int side) {
		this.side = side;
	}
	public int getSide() {
		return side;
	}
	
	// v0.2.2
	/**
	 * 施加效果的目标<br>
	 * 缓存<br>
	 */
	byte[] seats;
	/**
	 * 对应上面的 seats, 每个目标是否触发
	 */
	boolean[] forces;
	
	/* ************
	 *	实现方法  *
	 ************ */

	@Override
	public String name() {
		return "participant-state";
	}
	
	@Override
	protected void onFinish() {
		seats = null;
		forces = null;
	}
	
	@Override
	public void set(JsonValue args) {
		/* {
		 *  "n":"a.state",
		 *  "t":"a",
		 *  "tg":"e",
		 *  "v":"confusion",
		 *  "r":100,
		 * },*/
		Map<String, JsonValue> map = args.getMap();
		// target
		JsonValue v = map.get("tg");
		if (v != null) {
			String str = v.getString();
			if ("s".equals(str)) {
				side = SIDE_ATSTAFF;
			}
		}
		// state information, is needed
		v = map.get("v");
		if (v != null) {
			stateInfo = v.getString();
		}
		// rate
		v = map.get("r");
		if (v != null) {
			rate = (Integer) v.getValue();
		}
		
		this.value = args;
	}
	
	/**
	 * 如果该类进行复用时，会将其中的数据重置
	 */
	public void restore() {
		rate = 100;
		side = SIDE_DFSTAFF;
	}
	
	/* ************
	 *	私有方法  *
	 ************ */
	
	protected boolean canTrigger() {
		// 计算附加状态真实释放几率
		if (side == SIDE_ATSTAFF) {
			if (canTriggerForSelf()) {
				this.seats = new byte[]{pack.getAtStaff().getSeat()};
				this.forces = new boolean[]{true};
				return true;
			}
		} else if (side == SIDE_DFSTAFF) {
			this.seats = targetSeats();
			this.forces = new boolean[seats.length];
		} else {
			throw new IllegalArgumentException("side: " + side + " is illegal.");
		}
		
		boolean exist = false;
		for (int i = 0; i < this.seats.length; i++) {
			if (canTriggerForEnemy(i)) {
				exist = true;
				this.forces[i] = true;
			}
		}
		
		return exist;
	}
	
	private boolean canTriggerForSelf() {
		// 如果没有攻击到人
		byte[] dfseats = targetSeats();
		if (dfseats.length == 0) {
			return false;
		}
		
		final byte atseat = pack.getAtStaff().getSeat();
		byte[] scans = new byte[dfseats.length + 1];
		scans[0] = atseat;
		System.arraycopy(dfseats, 0, scans, 1, dfseats.length);
		
		Aperitif value = pack.getEffects().newAperitif(Aperitif.CODE_CALC_ADDITION_RATE, scans);
		value.append("type", "state")
			.append("dfseats", dfseats)
			.append("atseat", atseat)
			.append("target", atseat)
			.append("rate", rate)
			.append("state", stateInfo)
			.append("result", 0);
		pack.getEffects().startCode(value);
		
		int result = (Integer) value.get("result");
		if (result == 0) {
			int instanceRate = (int) value.get("rate");
			return RanValue.isSmaller(instanceRate);
		}
		
		return false;
	}
	
	private boolean canTriggerForEnemy(int i) {
		byte target = seats[i];
		byte atseat = pack.getAtStaff().getSeat();
		
		Aperitif value = pack.getEffects().newAperitif(Aperitif.CODE_CALC_ADDITION_RATE, atseat, target);
		value.append("type", "state")
			.append("dfseats", seats)
			.append("target", target)
			.append("atseat", atseat)
			.append("target", atseat)
			.append("rate", rate)
			.append("state", stateInfo)
			.append("result", 0);
		pack.getEffects().startCode(value);
		
		int result = (Integer) value.get("result");
		if (result == 0) {
			int instanceRate = (int) value.get("rate");
			return RanValue.isSmaller(instanceRate);
		}
		
		return false;
	}

	/**
	 * @param pack
	 */
	protected void force() {
		em.sendForceStateMessage(this.value, pack);
	}
	
	/* ************
	 *	 初始化   *
	 ************ */
	
	private EffectManage em;
	
	public ParticipantStateAdditionFormula(EffectManage em) {
		this.em = em;
	}

}
