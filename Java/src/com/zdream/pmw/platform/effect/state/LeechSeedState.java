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

/**
 * <p>寄生种子状态</p>
 * 
 * @see LeechSeedSubState
 * @since v0.2.2 [2017-04-20]
 * @author Zdream
 * @version v0.2.2 [2017-04-20]
 */
public class LeechSeedState extends AParticipantState
		implements IStateMessageFormater, ISubStateCreater {

	/* ************
	 *	  属性    *
	 ************ */
	
	/**
	 * 绑定原状态的施加方怪兽的座位
	 */
	byte atseat;
	
	/**
	 * @return
	 *  原状态的施加方怪兽的座位
	 */
	public byte getAtseat() {
		return atseat;
	}
	/**
	 * @param atseat
	 *  原状态的施加方怪兽的座位
	 */
	public void setAtseat(byte atseat) {
		this.atseat = atseat;
	}
	
	/**
	 * 附属状态
	 */
	private LeechSeedSubState sub;

	/* ************
	 *	实现方法  *
	 ************ */

	@Override
	public String name() {
		return "leech_seed";
	}

	@Override
	public boolean canExecute(String msg) {
		switch (msg) {
		case CODE_ROUND_END:
		case CODE_EXEUNT_EXCHANGE:
		case CODE_EXEUNT_FAINT:
			return true;
		}
		return false;
	}
	
	@Override
	public String execute(Aperitif ap, IStateInterceptable interceptor, BattlePlatform pf) {
		String head = ap.getHead();
		switch (head) {
		case CODE_ROUND_END:
			return roundEnd(ap, interceptor, pf);
		case CODE_EXEUNT_EXCHANGE:
		case CODE_EXEUNT_FAINT:
			return exeunt(ap, interceptor, pf);
		default:
			break;
		}
		return super.execute(ap, interceptor, pf);
	}
	
	@Override
	public int priority(String msg) {
		return 0;
	}

	@Override
	public ASubState[] getSubStates() {
		if (sub == null) {
			sub = new LeechSeedSubState(this, atseat);
		}
		return new ASubState[]{sub};
	}

	@Override
	public void handleCreateSubStates(BattlePlatform pf) {
		AttendManager am = pf.getAttendManager();
		ASubState[] states = getSubStates();
		
		for (int i = 0; i < states.length; i++) {
			ASubState state = states[i];
			am.forceStateForSeat(atseat, state);
		}
	}

	@Override
	public void handleDestroySubStates(BattlePlatform pf) {
		AttendManager am = pf.getAttendManager();
		ASubState[] states = getSubStates();
		
		for (int i = 0; i < states.length; i++) {
			ASubState state = states[i];
			am.removeStateFromSeat(atseat, state);
		}
	}
	
	@Override
	public String forceCommandLine(Aperitif ap, BattlePlatform pf) {
		// 施加命令:
		// force-state %s -no %d -source %s -skillID %d -atseat %d
		String statestr = ap.get("state").toString();
		
		byte atseat = (byte) ap.get("atseat");
		byte dfseat = (byte) ap.get("dfseat");
		byte dfno = pf.getAttendManager().noForSeat(dfseat);
		
		String cmd = String.format("force-state %s -no %d -source %s -skillID %d -atseat %d",
				statestr, dfno, ap.get("source"),
				ap.get("skillID"), atseat);
		return cmd;
	}
	
	@Override
	public void forceRealize(String[] codes, JsonObject argv, BattlePlatform pf) {
		argv.add("no", JsonValue.createJson(Byte.valueOf(codes[3])));
		argv.add("source", JsonValue.createJson(codes[5]));
		argv.add("skillID", JsonValue.createJson(Short.valueOf(codes[7])));
		argv.add("atseat", JsonValue.createJson(Byte.valueOf(codes[9])));
	}
	
	@Override
	public void set(JsonObject v, BattlePlatform pf) {
		Map<String, JsonValue> map = v.asMap();
		
		if (map.containsKey("atseat")) {
			atseat = (byte) map.get("atseat").getValue();
		}
		
		super.set(v, pf);
	}

	/* ************
	 *	触发状态  *
	 ************ */

	/**
	 * 处于寄生种子状态的怪兽每回合结束时损失 1/8 的最大 HP,
	 * 同时该状态的施放者恢复等量的 HP
	 * @return
	 */
	private String roundEnd(Aperitif ap, IStateInterceptable interceptor, BattlePlatform pf) {
		Participant p = participant(pf);
		
		// 计算出扣血的数值
		int hp = p.getAttendant().getStat()[Participant.HP] / 8;
		if (hp < 1) {
			hp = 1;
		}
		
		// 发送相应拦截前消息
		Aperitif ap2 = pf.getEffectManage().newAperitif(CODE_NONMOVES_DAMAGE, p.getSeat());
		ap2.append("seat", p.getSeat()).append("hp", hp).append("reason", name());
		pf.readyCode(ap2);
		
		// 让附属状态处理攻击方吸血
		sub.handleLeech(hp, pf);
		
		return interceptor.nextState();
	}
	
	/**
	 * @param ap
	 * @param interceptor
	 * @param pf
	 * @return
	 */
	private String exeunt(Aperitif ap, IStateInterceptable interceptor, BattlePlatform pf) {
		handleDestroySubStates(pf);
		return interceptor.nextState();
	}

	/* ************
	 *	 初始化   *
	 ************ */
	
	public LeechSeedState() {}
	
	public LeechSeedState(byte no) {
		super(no);
	}
	
	@Override
	public String toString() {
		return name();
	}

}
