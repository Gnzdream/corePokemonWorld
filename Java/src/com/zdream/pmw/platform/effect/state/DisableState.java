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
 * TODO 在状态生效的时间内该技能不能使用
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
				ap.get("lock"));
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
		
		byte skillNum = (byte) ap.get("skillNum");
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
		return name() + ":" + round;
	}

}
