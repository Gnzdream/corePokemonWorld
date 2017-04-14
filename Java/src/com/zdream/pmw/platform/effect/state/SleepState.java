package com.zdream.pmw.platform.effect.state;

import com.zdream.pmw.core.tools.AbnormalMethods;
import com.zdream.pmw.monster.prototype.EPokemonAbnormal;
import com.zdream.pmw.platform.attend.IStateInterceptable;
import com.zdream.pmw.platform.attend.service.EStateSource;
import com.zdream.pmw.platform.effect.Aperitif;
import com.zdream.pmw.platform.prototype.BattlePlatform;
import com.zdream.pmw.util.json.JsonValue;
import com.zdream.pmw.util.random.RanValue;

/**
 * 睡眠异常状态<br>
 * 
 * @since v0.1
 * @author Zdream
 * @date 2016年4月22日
 * @version v0.2
 */
public class SleepState extends AAbnormalState implements IDuration {

	/* ************
	 *	  属性    *
	 ************ */
	
	/**
	 * 睡眠将持续的时间
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
	 *	 初始化   *
	 ************ */
	
	BattlePlatform pf;

	public SleepState() {
		super(EPokemonAbnormal.SLEEP);
		this.round = RanValue.random(2, 4);
	}
	
	@Override
	public String toString() {
		return name() + ":" + round;
	}

	/* ************
	 *	实现方法  *
	 ************ */

	/**
	 * 该状态的发动与特定的施加源头没有关联<br>
	 */
	public EStateSource source() {
		return EStateSource.OTHER;
	}

	/**
	 * 可拦截的消息：<br>
	 * 1. 判断行动<br>
	 * 2. 回合结束时<br>
	 * 3. 计算附加状态释放几率、能否触发 TODO （一只精灵只能有一种异常状态）<br>
	 * 4. 等 TODO <br>
	 */
	public boolean canExecute(String msg) {
		switch (msg) {
		case CODE_JUDGE_MOVEABLE: case CODE_ROUND_END:
			return true;
		default:
			return false;
		}
	}

	@Override
	public String execute(Aperitif value, IStateInterceptable interceptor, BattlePlatform pf) {
		this.pf = pf;
		String head = value.getHead();
		switch (head) {
		case CODE_JUDGE_MOVEABLE:
			return judgeMoveable(value, interceptor);
		case CODE_ROUND_END:
			return roundEnd(value, interceptor);
		default:
			break;
		}
		return super.execute(value, interceptor, pf);
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
	 *	私有方法  *
	 ************ */

	/**
	 * 判断能否行动时触发<br>
	 * 判断精灵的状态，如果它的异常状态参数（睡眠回合数）为零，当回合即可移除该状态，并且能够行动<br>
	 * 否则不允许行动
	 * @param value
	 * @param interceptor
	 * @return
	 */
	private String judgeMoveable(Aperitif value, IStateInterceptable interceptor) {
		byte seat = (Byte) value.getMap().get("seat").getValue();
		
		if (round == 0) {
			// 移除睡眠状态
			pf.getEffectManage().forceAbnormal(seat, AbnormalMethods.CODE_NORMAL);
			// TODO CODE_FORCE_ABNORMAL, param: -m
			
			return interceptor.nextState();
		}
		
		value.replace("result", false);
		value.append("fail", name());
		return interceptor.getCommand();
	}

	/**
	 * 回合结束时触发
	 * 1. 将精灵的状态参数进行修改，参数（睡眠回合数 --）
	 * @param value
	 * @param interceptor
	 * @return
	 */
	private String roundEnd(Aperitif value, IStateInterceptable interceptor) {
		if (round > 0) {
			byte seat = pf.getAttendManager().seatForNo(getNo());
			Aperitif ap = pf.getEffectManage().newAperitif(CODE_STATE_SET, seat);
			ap.append("-seat", seat).append("-reduce", 1).append("state", name());
			pf.getRoot().readyCode(ap);
		}

		return interceptor.nextState();
	}
}
