package com.zdream.pmw.platform.effect.state;

import java.util.Map;

import com.zdream.pmw.platform.attend.AttendManager;
import com.zdream.pmw.platform.attend.IStateInterceptable;
import com.zdream.pmw.platform.attend.Participant;
import com.zdream.pmw.platform.effect.Aperitif;
import com.zdream.pmw.platform.effect.IStateMessageFormater;
import com.zdream.pmw.platform.prototype.BattlePlatform;
import com.zdream.pmw.util.json.JsonObject;
import com.zdream.pmw.util.json.JsonValue;
import com.zdream.pmw.util.random.RanValue;

/**
 * 束缚状态<br>
 * <p><b>状态效果: </b></p>
 * <li>处于束缚状态的怪兽无法换下或逃走（未完成） TODO
 * <li>回合结束时, 损失 1/8 的最大 HP
 * <li>招式的施放方离场时, 效果解除（利用附属状态完成这个功能）
 * <li>不会对同一个怪兽施加多余一个的束缚状态
 * </li>
 * 
 * @since v0.2.1
 * @author Zdream
 * @date 2017年3月4日
 * @version v0.2.1
 */
public class BoundState extends AParticipantState 
		implements IStateMessageFormater, IParticipantSubStateCreater, IDuration {

	/* ************
	 *	  属性    *
	 ************ */
	
	/**
	 * 束缚将持续的时间（默认 5-6 回合）
	 */
	private int round;
	
	/**
	 * 招式的施放方怪兽的 no
	 */
	private byte atno;

	/**
	 * 附属状态
	 */
	private LeaveSubState subState;
	
	@Override
	public int getRound() {
		return round;
	}
	
	/**
	 * @return
	 *   招式的施放方怪兽的 no
	 */
	public byte getAtno() {
		return atno;
	}

	/* ************
	 *	实现方法  *
	 ************ */

	@Override
	public String name() {
		return "bound";
	}

	@Override
	public boolean canExecute(String msg) {
		switch (msg) {
		case CODE_ROUND_END:
			return true;
		}
		return IParticipantSubStateCreater.super.canExecute(msg);
	}
	
	@Override
	public String execute(Aperitif value, IStateInterceptable interceptor, BattlePlatform pf) {
		String head = value.getHead();
		
		switch (head) {
		case CODE_ROUND_END:
			roundEnd(value, pf);
			break;

		default:
			break;
		}
		return IParticipantSubStateCreater.super.execute(value, interceptor, pf);
	}

	@Override
	public int priority(String msg) {
		return 0;
	}
	
	@Override
	public String forceCommandLine(Aperitif ap, BattlePlatform pf) {
		String statestr = ap.get("state").toString();
		
		byte atseat = (byte) ap.get("atseat");
		byte atno = pf.getAttendManager().noForSeat(atseat);
		byte dfseat = (byte) ap.get("dfseat");
		byte dfno = pf.getAttendManager().noForSeat(dfseat);
		
		String cmd = String.format("force-state %s -no %d -source %s -skillID %d -atno %d",
				statestr, dfno, ap.get("source"),
				ap.get("skillID"), atno);
		return cmd;
	}
	
	@Override
	public void forceRealize(String[] codes, JsonObject argv, BattlePlatform pf) {
		argv.add("no", JsonValue.createJson(Byte.valueOf(codes[3])));
		argv.add("source", JsonValue.createJson(codes[5]));
		argv.add("skillID", JsonValue.createJson(Short.valueOf(codes[7])));
		argv.add("atno", JsonValue.createJson(Byte.valueOf(codes[9])));
	}
	
	@Override
	public void set(JsonObject v, BattlePlatform pf) {
		super.set(v, pf);
		Map<String, JsonValue> map = v.asMap();
		
		if (map.containsKey("atno")) {
			this.atno = (byte) map.get("atno").getValue();
		}
		
		IDuration.super.set(v, pf);
	}
	
	@Override
	public ASubState[] getSubStates() {
		if (subState == null) {
			subState = new LeaveSubState(this, atno);
		}
		return new ASubState[]{subState};
	}
	
	@Override
	public byte toWhom(ASubState state) {
		if (subState == state) {
			return atno;
		}
		return (byte) -1;
	}
	
	@Override
	public void reduceRound(int num) {
		round -= num;
	}
	
	/* ************
	 *	状态触发  *
	 ************ */
	
	/**
	 * 回合结束时, 损失 1/8 的最大 HP<br>
	 * 如果束缚持续时间到了, 就会删除状态<br>
	 * @param value
	 * @param pf
	 */
	protected void roundEnd(Aperitif value, BattlePlatform pf) {
		if (round == 0) {
			// 删除该状态
			pf.getEffectManage().sendRemoveParticipantStateMessage(name(), getNo());
			return;
		} else {
			round--;
		}
		
		AttendManager am = pf.getAttendManager();
		byte seat = am.seatForNo(getNo());
		
		// 步骤一：计算出扣血的数值
		// 为最大生命值的 (count)/8，至少为 1
		Participant p = am.getParticipant(seat);
		int hpMax = p.getAttendant().getStat()[Participant.HP];
		int hp = (hpMax / 8);
		if (hp < 1) {
			hp = 1;
		}
		
		// 步骤二：发送相应拦截前消息
		Aperitif ap = pf.getEffectManage().newAperitif(CODE_NONMOVES_DAMAGE, seat);
		ap.append("seat", seat).append("hp", hp).append("reason", name());
		pf.readyCode(ap);
	}
	
	/* ************
	 *	 初始化   *
	 ************ */

	/**
	 * 默认无参数的构造方法, StateBuilder 调用<br>
	 * <p>要注意, 构造完成后, 需要调用 {@code set()} 方法设置参数</p>
	 */
	public BoundState() {
		super();
		this.round = RanValue.random(5, 6);
	}

	/**
	 * @param no
	 *   对应的怪兽
	 */
	public BoundState(byte no) {
		super(no);
		this.round = RanValue.random(5, 6);
	}
	
	@Override
	public String toString() {
		return name() + ":" + round;
	}

}
