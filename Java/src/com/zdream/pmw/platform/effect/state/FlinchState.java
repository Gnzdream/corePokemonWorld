package com.zdream.pmw.platform.effect.state;

import com.zdream.pmw.platform.attend.IStateInterceptable;
import com.zdream.pmw.platform.effect.Aperitif;
import com.zdream.pmw.platform.prototype.BattlePlatform;
import com.zdream.pmw.util.json.JsonValue;

/**
 * 畏缩状态<br>
 * <p><b>状态效果: </b></p>
 * <li>判断能否行动时, 为否
 * <li>回合结束时, 删除状态
 * 
 * @since v0.2.1
 * @author Zdream
 * @date 2017年3月2日
 * @version v0.2.1
 */
public class FlinchState extends ParticipantState {

	/* ************
	 *	实现方法  *
	 ************ */

	@Override
	public String name() {
		return "flinch";
	}

	@Override
	public boolean canExecute(String msg) {
		switch (msg) {
		case CODE_JUDGE_MOVEABLE: case CODE_ROUND_END:
			return true;
		}
		return false;
	}
	
	@Override
	public String execute(Aperitif value, IStateInterceptable interceptor, BattlePlatform pf) {
		switch (value.getHead()) {
		case CODE_JUDGE_MOVEABLE: 
			return judgeMoveable(value, interceptor, pf);
			
		case CODE_ROUND_END:
			roundEnd(value, pf);
			break;
		}
		
		return interceptor.nextState();
	}

	@Override
	public int priority(String msg) {
		return 0;
	}

	/* ************
	 *	状态触发  *
	 ************ */

	private String judgeMoveable(Aperitif value, IStateInterceptable interceptor, BattlePlatform pf) {
		// 设置为不能行动
		boolean result = (boolean) value.get("result");
		if (result) {
			value.replace("result", false);
			value.append("fail", name());
			return interceptor.getCommand();
		}
		return interceptor.nextState();
	}

	private void roundEnd(JsonValue value, BattlePlatform pf) {
		pf.getEffectManage().sendRemoveParticipantStateMessage(name(), getNo());
	}

	/* ************
	 *	 初始化   *
	 ************ */

	/**
	 * 默认无参数的构造方法, StateBuilder 调用<br>
	 * <p>要注意, 构造完成后, 需要调用 {@code set()} 方法设置参数</p>
	 */
	public FlinchState() {
		super();
	}

	/**
	 * @param no
	 *   对应的怪兽
	 */
	public FlinchState(byte no) {
		super(no);
	}
	
	@Override
	public String toString() {
		return name();
	}
}
