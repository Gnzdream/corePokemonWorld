package com.zdream.pmw.platform.effect.state;

import com.zdream.pmw.platform.attend.IStateInterceptable;
import com.zdream.pmw.platform.effect.Aperitif;
import com.zdream.pmw.platform.prototype.BattlePlatform;
import com.zdream.pmw.util.json.JsonValue;
import com.zdream.pmw.util.random.RanValue;

/**
 * 混乱状态<br>
 * <p><b>状态效果: </b></p>
 * <li>判断确定释放技能时(不是能否行动), 1/3 可能攻击自己 (将技能改写)
 * <li>准备扣 PP 时, 如果检测到混乱打自己, 将不扣除 PP
 * 
 * @since v0.2.1
 * @author Zdream
 * @date 2017年3月2日
 * @version v0.2.1
 */
public class ConfusionState extends AParticipantState implements IDuration {

	/* ************
	 *	  属性    *
	 ************ */
	
	/**
	 * 混乱将持续的时间
	 */
	private int round;
	
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

	/* ************
	 *	实现方法  *
	 ************ */

	@Override
	public String name() {
		return "confusion";
	}

	@Override
	public boolean canExecute(String msg) {
		switch (msg) {
		case CODE_CONFIRM_SKILL: case CODE_PP_SUB:
			return true;
		}
		return false;
	}
	
	@Override
	public String execute(Aperitif value, IStateInterceptable interceptor, BattlePlatform pf) {
		String head = value.getHead();
		
		switch (head) {
		case CODE_CONFIRM_SKILL:
			releaseSkill(value, pf);
			break;
			
		case CODE_PP_SUB:
			ppSub(value, pf);
			break;
		}
		
		return interceptor.nextState();
	}
	
	@Override
	public void set(JsonValue v, BattlePlatform pf) {
		super.set(v, pf);
		IDuration.super.set(v, pf);
	}

	@Override
	public int priority(String msg) {
		return 0;
	}

	/* ************
	 *	状态触发  *
	 ************ */

	private void releaseSkill(Aperitif value, BattlePlatform pf) {
		if (round == 0) {
			pf.getEffectManage().sendRemoveParticipantStateMessage(name(), getNo());
			
			return;
		} else {
			// TODO 报告状态触发, 就是让显示屏上出现 "xxx 混乱了" 这句话
			// pf.startCode(...)
			
			byte seat = pf.getAttendManager().seatForNo(getNo());
			Aperitif ap = pf.getEffectManage().newAperitif(CODE_STATE_SET, seat);
			ap.append("-seat", seat).append("-reduce", 1).append("state", name());
			pf.getRoot().readyCode(ap);
		}
		
		if (RanValue.random(0, 3) == 0) {
			// 混乱攻击自己, 技能为 0

			value.add("skillID", new JsonValue((short) 0));
		}
	}
	
	private void ppSub(Aperitif value, BattlePlatform pf) {
		short skillID = (Short) value.get("skillID");
		if (skillID == 0) {
			value.append("value", 0);
		}
	}
	
	/* ************
	 *	 初始化   *
	 ************ */

	/**
	 * 默认无参数的构造方法, StateBuilder 调用<br>
	 * <p>要注意, 构造完成后, 需要调用 {@code set()} 方法设置参数</p>
	 */
	public ConfusionState() {
		super();
		this.round = RanValue.random(1, 5);
	}

	/**
	 * @param no
	 *   对应的怪兽
	 */
	public ConfusionState(byte no) {
		super(no);
		this.round = RanValue.random(1, 5);
	}
	
	@Override
	public String toString() {
		return name() + ":" + round;
	}
}
