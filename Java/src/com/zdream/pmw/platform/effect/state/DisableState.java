package com.zdream.pmw.platform.effect.state;

import java.util.Map;

import com.zdream.pmw.platform.attend.IStateInterceptable;
import com.zdream.pmw.platform.effect.Aperitif;
import com.zdream.pmw.platform.effect.IStateMessageFormater;
import com.zdream.pmw.platform.prototype.BattlePlatform;
import com.zdream.pmw.util.json.JsonObject;
import com.zdream.pmw.util.json.JsonValue;

/**
 * <p>定身状态</p>
 * <p>在此期间目标不能使用陷入定身法状态前最后使用的技能,
 * 一般持续 4 回合</p>
 * <p><b>状态效果: </b>
 * <li>锁定进入定身法状态前最后使用的技能,
 * <li>在状态生效的时间内该技能不能使用,
 * <li>如果该怪兽选择了锁住的技能, 将发动失败.
 * <li>每回合结束时回合计数 1</li></p>
 * 
 * <p>该状态将在现在的环境中 (v0.2.2) 比较难以实现,
 * 因此将推迟到 v0.2.3 版本中实现.</p>
 * 
 * @since v0.2.2 [2017-04-21]
 * @author Zdream
 * @version v0.2.2 [2017-04-21]
 */
public class DisableState extends AParticipantState
		implements IDuration, IStateMessageFormater {

	/* ************
	 *	  属性    *
	 ************ */
	
	/**
	 * 定身将持续的时间（默认开始是 4 回合）
	 */
	private int round;
	
	/**
	 * 锁定怪兽的第几项技能, 有效值一般为 0 - 3
	 */
	private int lock;
	
	@Override
	public int getRound() {
		return round;
	}
	
	/**
	 * @see #lock
	 */
	public int getLock() {
		return lock;
	}

	/* ************
	 *	实现方法  *
	 ************ */

	@Override
	public String name() {
		return "disable";
	}

	@Override
	public boolean canExecute(String msg) {
		switch (msg) {
		case CODE_JUDGE_MOVEABLE:
		case CODE_ROUND_END:
		case CODE_REQUEST_MOVE:
		case CODE_CONFIRM_SKILL:
			return true;
		default:
			return false;
		}
	}
	
	@Override
	public String execute(Aperitif ap, IStateInterceptable interceptor, BattlePlatform pf) {
		String head = ap.getHead();
		switch (head) {
		case CODE_JUDGE_MOVEABLE:
			return judgeMoveable(ap, interceptor, pf);
		case CODE_ROUND_END:
			return roundEnd(ap, interceptor, pf);
		case CODE_REQUEST_MOVE:
			return requestMove(ap, interceptor, pf);
		case CODE_CONFIRM_SKILL:
			return confirmSkill(ap, interceptor, pf);

		default:
			break;
		}
		return super.execute(ap, interceptor, pf);
	}
	
	@Override
	public void set(JsonObject v, BattlePlatform pf) {
		Map<String, JsonValue> map = v.asMap();
		if (map.containsKey("lock")) {
			lock = (int) map.get("lock").getValue();
		}
		super.set(v, pf);
		IDuration.super.set(v, pf);
	}

	@Override
	public int priority(String msg) {
		if (CODE_CONFIRM_SKILL.equals(msg)) {
			return -5;
		}
		return 99;
	}

	@Override
	public void reduceRound(int num) {
		round -= num;
	}
	
	@Override
	public String forceCommandLine(Aperitif ap, BattlePlatform pf) {
		// force-state %s -no %d -source %s -lock %d
		String statestr = ap.get("state").toString();
		
		byte dfseat = (byte) ap.get("dfseat");
		byte dfno = pf.getAttendManager().noForSeat(dfseat);
		
		String cmd = String.format("force-state %s -no %d -source %s -lock %d",
				statestr, dfno, ap.get("source"),
				ap.get("-lock"));
		return cmd;
	}
	
	@Override
	public void forceRealize(String[] codes, JsonObject argv, BattlePlatform pf) {
		argv.add("no", JsonValue.createJson(Byte.valueOf(codes[3])));
		argv.add("source", JsonValue.createJson(codes[5]));
		argv.add("lock", JsonValue.createJson(Integer.valueOf(codes[7])));
	}
	
	/* ************
	 *	状态触发  *
	 ************ */
	
	private String judgeMoveable(Aperitif ap, IStateInterceptable interceptor, BattlePlatform pf) {
		
		int skillNum = (int) ap.get("skillNum");
		if (skillNum == lock) {
			ap.replace("result", false);
			ap.append("fail", name());
			return null;
		}
		
		return interceptor.nextState();
	}
	
	/**
	 * 回合结束时, 持续时间会减 1<br>
	 * 如果束缚持续时间到了, 就会删除状态<br>
	 */
	protected String roundEnd(Aperitif ap, IStateInterceptable interceptor, BattlePlatform pf) {
		if (round == 0) {
			// 删除该状态
			pf.getEffectManage().sendRemoveParticipantStateMessage(name(), getNo());
		} else {
			byte seat = pf.getAttendManager().seatForNo(getNo());
			Aperitif ap0 = pf.getEffectManage().newAperitif(CODE_STATE_SET, seat);
			ap0.append("-seat", seat).append("-reduce", 1).append("state", name());
			pf.getRoot().readyCode(ap0);
		}
		
		return interceptor.nextState();
	}
	
	protected String requestMove(Aperitif ap, IStateInterceptable interceptor, BattlePlatform pf) {
		// 检查是否是拥有该状态的怪兽请求行动
		boolean b = false;
		byte[] seats = (byte[]) ap.get("seats");
		byte thizSeat = pf.getAttendManager().seatForNo(getNo());
		for (int i = 0; i < seats.length; i++) {
			if (seats[i] == thizSeat) {
				b = true;
				break;
			}
		}
		if (!b) {
			return interceptor.nextState();
		}
		
		// 添加限制
		Object o;
		if ((o = ap.get("limits")) == null) {
			ap.put("limits", new byte[]{thizSeat});
			int[][] iss = new int[][]{new int[]{lock}};
			ap.put("limit_skills", iss);
			return interceptor.nextState();
		}
		
		// o != null
		byte[] ls = (byte[]) o;
		int[][] iss = (int[][]) ap.get("limit_skills");
		for (int i = 0; i < ls.length; i++) {
			if (ls[i] == thizSeat) {
				// ls 中有 thizSeat 这个元素
				int[] is = iss[i];
				
				for (int j = 0; j < is.length; j++) {
					if (is[j] == lock) {
						// 该数据已经在 is 中
						return interceptor.nextState();
					}
				}
				
				// lock 不再 is 中
				int[] limits = new int[is.length + 1];
				System.arraycopy(is, 0, limits, 0, is.length);
				limits[is.length] = lock;
				iss[i] = limits;

				return interceptor.nextState();
			}
		}
		
		// ls 中没有 thizSeat 这个元素
		byte[] limitSeats = new byte[ls.length + 1];
		System.arraycopy(ls, 0, limitSeats, 0, ls.length);
		limitSeats[ls.length] = thizSeat;
		
		int[][] niss = new int[iss.length + 1][];
		System.arraycopy(iss, 0, niss, 0, iss.length);
		niss[iss.length] = new int[]{lock};
		
		ap.put("limits", limitSeats);
		ap.put("limit_skills", niss);
		
		return interceptor.nextState();
	}
	
	/**
	 * 主要完成在状态生效的时间内被锁定的技能不能使用<br>
	 * 同一回合内, 当定身术比对应怪兽的对应技能先发动时,
	 * 或者定身术发动之后在使用蓄力技的第二回合时, 将会触发
	 */
	protected String confirmSkill(Aperitif ap, IStateInterceptable interceptor, BattlePlatform pf) {
		short oriSkillID = (short) ap.get("oriSkillID");
		byte seat = (byte) ap.get("seat");
		if (oriSkillID == pf.getAttendManager().getParticipant(seat).getSkill(lock)) {
			// 需要被锁定
			ap.replace("skillID", (short) -1);
			ap.append("reason", "disable");
		}
		
		return interceptor.nextState();
	}

	/* ************
	 *	 初始化   *
	 ************ */

	/**
	 * 默认无参数的构造方法, StateBuilder 调用<br>
	 * <p>要注意, 构造完成后, 需要调用 {@code set()} 方法设置参数</p>
	 */
	public DisableState() {
		super();
		this.round = 4;
	}

	/**
	 * @param no
	 *   对应的怪兽
	 */
	public DisableState(byte no) {
		super(no);
		this.round = 4;
	}
	
	@Override
	public String toString() {
		return name() + ":" + round + "," + lock;
	}

}
