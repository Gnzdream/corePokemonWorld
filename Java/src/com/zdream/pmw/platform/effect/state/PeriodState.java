package com.zdream.pmw.platform.effect.state;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.zdream.pmw.platform.attend.AttendManager;
import com.zdream.pmw.platform.attend.IStateInterceptable;
import com.zdream.pmw.platform.effect.Aperitif;
import com.zdream.pmw.platform.order.OrderManager;
import com.zdream.pmw.platform.order.event.AEvent;
import com.zdream.pmw.platform.prototype.BattlePlatform;
import com.zdream.pmw.util.json.JsonBuilder;
import com.zdream.pmw.util.json.JsonValue;

/**
 * <p>蓄力状态</p>
 * <p>蓄力和反作用等类型的技能, 需要花费多于一个回合完全释放的技能,
 * 在发动技能时产生的状态</p>
 * <p>该状态完成的动作有:
 * <li>屏蔽对该怪兽的行动请求
 * <li>自动进行该怪兽的行动提交
 * <li>指定回合结束后进行回合数计数和自毁
 * <li>如果出现有一次怪兽因为某种原因不能行动, 状态自动销毁</li></p>
 * <li>如果出现有一次怪兽因为混乱攻击自己, 状态自动销毁</li></p>
 * 
 * @since v0.2.2
 *   [2017-04-07]
 * @author Zdream
 * @version v0.2.2
 *   [2017-04-07]
 */
public class PeriodState extends AParticipantState implements IDuration {

	/* ************
	 *	  属性    *
	 ************ */
	
	byte team;
	
	JsonValue param;
	int round;
	short skillId;
	byte target;
	
	@Override
	public int getRound() {
		return round;
	}

	@Override
	public void reduceRound(int num) {
		round -= num;
		if (round < 0) {
			round = 0;
		}
	}
	
	/**
	 * 释放的技能 Id
	 * @return
	 */
	public short getSkillId() {
		return skillId;
	}
	
	/**
	 * 在上一次行动请求时, 选择的技能释放目标
	 * 当在系统可以计算出默认目标的情况下, 参数允许为 -1
	 * @return
	 */
	public byte getTarget() {
		return target;
	}

	/* ************
	 *	实现方法  *
	 ************ */

	@Override
	public String name() {
		return "period";
	}

	@Override
	public boolean canExecute(String msg) {
		switch (msg) {
		case CODE_REQUEST_MOVE:
		case CODE_REQUEST_COMMIT:
		case CODE_ROUND_END:
		case CODE_JUDGE_MOVEABLE:
		case CODE_CONFIRM_SKILL: // 用于检查混乱
			return true;
		}
		return false;
	}
	
	@Override
	public String execute(Aperitif value, IStateInterceptable interceptor, BattlePlatform pf) {
		switch (value.getHead()) {
		case CODE_REQUEST_MOVE:
			return exeRequestMove(value, interceptor, pf);
		case CODE_REQUEST_COMMIT:
			return exeRequestCommit(value, interceptor, pf);
		case CODE_ROUND_END:
			return roundEnd(value, interceptor, pf);
		case CODE_JUDGE_MOVEABLE:
			return judgeMoveable(value, interceptor, pf);
		case CODE_CONFIRM_SKILL:
			return confirmSkill(value, interceptor, pf);

		default:
			break;
		}
		
		return super.execute(value, interceptor, pf);
	}

	@Override
	public int priority(String msg) {
		if (CODE_JUDGE_MOVEABLE.equals(msg) || CODE_CONFIRM_SKILL.equals(msg)) {
			return -5;
		}
		return 0;
	}
	
	@Override
	public void set(JsonValue v, BattlePlatform pf) {
		super.set(v, pf);
		IDuration.super.set(v, pf);
		
		Map<String, JsonValue> map = v.getMap();
		if (map.containsKey("param")) {
			JsonBuilder b = new JsonBuilder();
			param = b.parseJson(map.get("param").getString());
			round = param.getArray().size();
		}
		if (map.containsKey("skillID")) {
			skillId = ((Number) map.get("skillID").getValue()).shortValue();
		}
		if (map.containsKey("target")) {
			target = (Byte.parseByte(map.get("target").getString()));
		}
		
		team = pf.getAttendManager().teamForNo(getNo());
	}

	/* ************
	 *	状态触发  *
	 ************ */
	
	private String exeRequestMove(Aperitif value, IStateInterceptable interceptor, BattlePlatform pf) {
		byte curTeam = (byte) value.get("team");
		if (curTeam != team) {
			return interceptor.nextState();
		}
		
		byte[] seats = (byte[]) value.get("seats");
		byte seat = pf.getAttendManager().seatForNo(getNo());
		boolean exist = false;
		int i = 0;
		for (; i < seats.length; i++) {
			if (seats[i] == seat) {
				exist = true;
				break;
			}
		}
		
		if (!exist) {
			return interceptor.nextState();
		}
		
		byte[] modify = new byte[seats.length - 1];
		if (i > 0) {
			System.arraycopy(seats, 0, modify, 0, i);
		}
		if (i < seats.length - 1) {
			System.arraycopy(seats, i + 1, modify, i, seats.length - i - 1);
		}
		value.replace("seats", modify);
		
		return interceptor.nextState();
	}
	
	private String exeRequestCommit(Aperitif value, IStateInterceptable interceptor, BattlePlatform pf) {
		List<AEvent> events = new ArrayList<>();
		
		AttendManager am = pf.getAttendManager();
		byte skillNum = -1;
		short[] skills = am.getAttendant(getNo()).getSkill();
		for (byte i = 0; i < skills.length; i++) {
			short skillId = skills[i];
			if (skillId == this.skillId) {
				skillNum = i;
				break;
			}
		}
		
		if (skillNum == -1) {
			pf.logPrintf(BattlePlatform.PRINT_LEVEL_WARN, 
					"PeriodState.exeRequestCommit(): 在怪兽 %s(no=%d) 中没有找到"
					+ " skillId=%d 的技能, 无法创建蓄力技能释放事件",
					am.getAttendant(getNo()).getNickname(), getNo(), this.skillId);
			return interceptor.nextState();
		}
		
		OrderManager om = pf.getOrderManager();
		List<JsonValue> list = param.getArray();
		om.buildMoveEvent(getNo(), skillNum, target, list.get(list.size() - round), events);
		om.pushEvents(events);
		
		// 然后回合计数 --
		byte seat = pf.getAttendManager().seatForNo(getNo());
		Aperitif ap = pf.getEffectManage().newAperitif(CODE_STATE_SET, seat);
		ap.append("-seat", seat).append("-reduce", 1).append("state", name());
		pf.getRoot().readyCode(ap);
		
		return interceptor.nextState();
	}

	private String roundEnd(Aperitif value, IStateInterceptable interceptor, BattlePlatform pf) {
		// 如果蓄力状态结束了, 应该去掉该状态
		if (round <= 0) {
			pf.getEffectManage().sendRemoveParticipantStateMessage(name(), getNo());
		}
		return interceptor.nextState();
	}

	private String judgeMoveable(Aperitif value, IStateInterceptable interceptor, BattlePlatform pf) {
		boolean result = (boolean) value.get("result");
		if (result == false) {
			// 销毁该状态
			pf.getEffectManage().sendRemoveParticipantStateMessage(name(), getNo());
		}
		return interceptor.nextState();
	}

	private String confirmSkill(Aperitif value, IStateInterceptable interceptor, BattlePlatform pf) {
		Object o = value.get("skillID");
		if (o != null) {
			if ((short) o == 0) {
				// 说明触发了混乱攻击自己
				// 销毁该状态
				pf.getEffectManage().sendRemoveParticipantStateMessage(name(), getNo());
			}
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
	public PeriodState() {
		super();
	}

	/**
	 * @param no
	 *   对应的防御方怪兽
	 */
	public PeriodState(byte no) {
		super(no);
	}
	
	@Override
	public String toString() {
		return name() + ":" + round;
	}

}
