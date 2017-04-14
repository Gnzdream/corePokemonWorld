package com.zdream.pmw.platform.effect.state;

import com.zdream.pmw.monster.prototype.EPokemonAbnormal;
import com.zdream.pmw.platform.attend.IStateInterceptable;
import com.zdream.pmw.platform.attend.service.EStateSource;
import com.zdream.pmw.platform.effect.Aperitif;
import com.zdream.pmw.platform.prototype.BattlePlatform;
import com.zdream.pmw.util.random.RanValue;

/**
 * 麻痹异常状态<br>
 * 
 * @since v0.1
 * @author Zdream
 * @date 2016年4月17日
 * @version v0.2
 */
public class ParalysisState extends AAbnormalState {

	/* ************
	 *	 初始化   *
	 ************ */

	public ParalysisState() {
		super(EPokemonAbnormal.PARALYSIS);
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
	 * 1. 判断速度<br>
	 * 2. 判断行动<br>
	 * 3. 等 TODO <br>
	 */
	public boolean canExecute(String msg) {
		if (CODE_CALC_SP.equals(msg) || CODE_JUDGE_MOVEABLE.equals(msg)) {
			return true;
		}
		return false;
	}

	@Override
	public String execute(Aperitif value, IStateInterceptable interceptor, BattlePlatform pf) {
		String head = value.getHead();
		if (CODE_CALC_SP.equals(head)) {
			return calcSp(value, interceptor);
		} else if (CODE_JUDGE_MOVEABLE.equals(head)) {
			return judgeMoveable(value, interceptor);
		}

		return null;
	}

	@Override
	public int priority(String msg) {
		return 0;
	}

	/* ************
	 *	私有方法  *
	 ************ */
	
	/**
	 * 速度计算时，修正为原来的 0.5 倍
	 * @param value
	 * @param interceptor
	 */
	private String calcSp(Aperitif value, IStateInterceptable interceptor) {
		float rate = (float) value.get("rate");
		rate *= 0.5f;
		value.replace("rate", rate);
		
		return interceptor.nextState();
	}
	
	/**
	 * 判断能否行动时，有 50% 的可能无法行动
	 * @param value
	 * @param interceptor
	 * @return
	 */
	private String judgeMoveable(Aperitif value, IStateInterceptable interceptor) {
		if (RanValue.isSmaller(50)) {
			// 不能行动
			value.replace("result", false);
			value.append("fail", name());
			return interceptor.getCommand();
		}
		
		return interceptor.nextState();
	}
	
	@Override
	public String toString() {
		return name();
	}

}
