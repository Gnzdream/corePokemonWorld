package com.zdream.pmw.platform.effect.addition;

import java.util.Map;

import com.zdream.pmw.platform.effect.Aperitif;
import com.zdream.pmw.platform.effect.EffectManage;
import com.zdream.pmw.util.json.JsonValue;
import com.zdream.pmw.util.random.RanValue;

/**
 * 添加状态判定实现类<br>
 * 向我方或敌方施加或删除某个座位状态<br>
 * 
 * <p><b>v0.2.2</b><br>
 * 该类添加了删除状态的功能</p>
 * 
 * @since v0.2.1
 *   [2017-03-02]
 * @author Zdream
 * @version v0.2.2
 *   [2017-04-13]
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
	 * 从技能原始数据 (数据库中) 传来的技能参数数据<br>
	 * 里面包含了需要生成状态的构造所需数据
	 * 或能够说明需要删除的状态的数据<br>
	 */
	private JsonValue value;
	
	/**
	 * 是否为删除状态
	 * @since v0.2.2
	 */
	private boolean remove;
	
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
	/**
	 * 询问该类是否为删除状态的附加公式
	 * @since v0.2.2
	 */
	public boolean isRemove() {
		return remove;
	}
	/**
	 * 设置该类是否为删除状态的附加公式
	 * @since v0.2.2
	 */
	public void setRemove(boolean remove) {
		this.remove = remove;
	}
	
	// v0.2.2
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
		forces = null;
		remove = false;
	}
	
	@Override
	public void set(JsonValue args) {
		super.set(args);
		/* {
		 *  "n":"a.state",
		 *  "t":"a",
		 *  "tg":"e",
		 *  "v":"confusion",
		 *  "r":100,
		 * },*/
		Map<String, JsonValue> map = args.getMap();
		// state information, is needed
		JsonValue v = map.get("v");
		if (v != null) {
			stateInfo = v.getString();
		}
		// rate
		v = map.get("r");
		if (v != null) {
			rate = (Integer) v.getValue();
		}
		// remove
		v = map.get("rm");
		if (v != null) {
			remove = (Boolean) v.getValue();
		}
		
		this.value = args;
	}
	
	/**
	 * 如果该类进行复用时，会将其中的数据重置
	 */
	public void restore() {
		super.restore();
		rate = 100;
	}
	
	/* ************
	 *	私有方法  *
	 ************ */
	
	protected boolean canTrigger() {
		// 计算附加状态真实释放几率
		if (seatLen == 0) {
			return false;
		}
		
		if (side == SIDE_ATSTAFF) {
			if (canTriggerForSelf()) {
				this.forces = new boolean[]{true};
				return true;
			}
		} else if (side == SIDE_DFSTAFF) {
			this.forces = new boolean[seatLen];
		} else {
			throw new IllegalArgumentException("side: " + side + " is not done.");
		}
		
		boolean exist = false;
		byte[] bs = pack.dfSeats();
		for (int i = 0; i < this.seatLen; i++) {
			if (canTriggerForEnemy(i, bs)) {
				exist = true;
				this.forces[i] = true;
			}
		}
		
		return exist;
	}
	
	private boolean canTriggerForSelf() {
		// 如果没有攻击到人
		byte[] dfseats = pack.dfSeats();
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
		
		if (remove) {
			value.append("remove", true);
		}
		
		pack.getEffects().startCode(value);
		
		int result = (Integer) value.get("result");
		if (result == 0) {
			int instanceRate = (int) value.get("rate");
			return RanValue.isSmaller(instanceRate);
		}
		
		return false;
	}
	
	/**
	 * 施加异常状态到防御方身上
	 * @param i
	 *   在攻击到的对象列表中的索引, 指向 <code>this.seats</code>
	 * @param dfseats
	 *   防御方座位组成的数组, 作为参数因为每次调用该方法时,
	 *   生成的 dfseats 都一样, 因此作为传入数据可以减少多次重复的方法调用
	 * @return
	 */
	private boolean canTriggerForEnemy(int i, byte[] dfseats) {
		byte target = seats[i];
		byte atseat = pack.getAtStaff().getSeat();
		
		Aperitif value = pack.getEffects().newAperitif(Aperitif.CODE_CALC_ADDITION_RATE, atseat, target);
		value.append("type", "state")
			.append("dfseats", dfseats)
			.append("target", target)
			.append("atseat", atseat)
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
		if (remove) {
			if (side == SIDE_ATSTAFF) {
				em.sendRemoveParticipantStateMessage(stateInfo, pack.getAtStaff().getNo());
			} else if (side == SIDE_DFSTAFF) {
				if (pack.dfStaffLength() == 1) {
					em.sendRemoveParticipantStateMessage(stateInfo, pack.getDfStaff(0).getNo());
				} else {
					for (int i = 0; i < forces.length; i++) {
						if (forces[i]) {
							em.sendRemoveParticipantStateMessage(stateInfo,
									pack.getAttends().noForSeat(seats[i]));
						}
					}
				}
			} else {
				for (int i = 0; i < forces.length; i++) {
					if (forces[i]) {
						em.sendRemoveParticipantStateMessage(stateInfo,
								pack.getAttends().noForSeat(seats[i]));
					}
				}
			}
		} else {
			if (side == SIDE_ATSTAFF) {
				em.sendForceStateMessage(stateInfo, pack.getAtStaff().getSeat(),
						pack.getAtStaff().getSeat(), pack.getSkill().getId(), value);
			} else if (side == SIDE_DFSTAFF) {
				if (pack.dfStaffLength() == 1) {
					em.sendForceStateMessage(stateInfo, pack.getAtStaff().getSeat(),
							pack.getDfStaff(0).getSeat(), pack.getSkill().getId(), value);
				} else {
					for (int i = 0; i < forces.length; i++) {
						if (forces[i]) {
							em.sendForceStateMessage(stateInfo, pack.getAtStaff().getSeat(),
									seats[i], pack.getSkill().getId(), value);
						}
					}
				}
			} else {
				for (int i = 0; i < forces.length; i++) {
					if (forces[i]) {
						em.sendForceStateMessage(stateInfo, pack.getAtStaff().getSeat(),
								seats[i], pack.getSkill().getId(), value);
					}
				}
			}
			
		}
	}
	
	/* ************
	 *	 初始化   *
	 ************ */
	
	private EffectManage em;
	
	public ParticipantStateAdditionFormula(EffectManage em) {
		this.em = em;
	}

}
