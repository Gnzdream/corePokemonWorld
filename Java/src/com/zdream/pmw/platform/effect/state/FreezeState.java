package com.zdream.pmw.platform.effect.state;

import com.zdream.pmw.core.tools.AbnormalMethods;
import com.zdream.pmw.monster.prototype.EPokemonAbnormal;
import com.zdream.pmw.platform.attend.IStateInterceptable;
import com.zdream.pmw.platform.attend.service.EStateSource;
import com.zdream.pmw.platform.effect.Aperitif;
import com.zdream.pmw.platform.prototype.BattlePlatform;
import com.zdream.pmw.util.random.RanValue;

/**
 * 冻伤异常状态<br>
 * 冻伤是一种异常状态。关于异常状态，您可以查看：<code>EPokemonAbnormal</code><br>
 * 
 * @see
 *   EPokemonAbnormal
 * @since v0.2
 * @author Zdream
 * @date 2017年2月27日
 * @version v0.2
 */
public class FreezeState extends AAbnormalState {
	
	/**
	 * 回合结束拦截前消息头
	 */
	private static final String PERIOD_JUDGE_MOVABLE = "judge-movable";

	/* ************
	 *	实现方法  *
	 ************ */

	@Override
	public EStateSource source() {
		/*
		 * 该状态的发动与特定的施加源头没有关联
		 */
		return EStateSource.OTHER;
	}

	/**
	 * 可拦截的消息：<br>
	 * 1. 判断行动 (冻伤时不能行动)<br>
	 * 2. 计算附加状态释放几率、能否触发 TODO （一只精灵只能有一种异常状态）<br>
	 */
	public boolean canExecute(String msg) {
		switch (msg) {
		case PERIOD_JUDGE_MOVABLE:
			return true;
		}
		return false;
	}

	@Override
	public String execute(Aperitif value, IStateInterceptable interceptor, BattlePlatform pf) {
		this.pf = pf;
		switch (value.getHead()) {
		case PERIOD_JUDGE_MOVABLE:
			return judgeMovable(value, interceptor);
		}
		return interceptor.nextState();
	}

	@Override
	public int priority(String msg) {
		return 0;
	}

	/* ************
	 *	私有函数  *
	 ************ */
	/**
	 * 每回合解冻概率变为 25%
	 * @param value
	 * @param interceptor
	 * @return
	 */
	private String judgeMovable(Aperitif value, IStateInterceptable interceptor) {
		if (RanValue.isSmaller(75)) {
			// 不能行动
			value.replace("result", false);
			value.append("fail", name());
			return interceptor.getCommand();
		} else {
			// 冻伤解除
			pf.getEffectManage().forceAbnormal(pf.getAttendManager().seatForNo(getNo()), 
					AbnormalMethods.toBytes(EPokemonAbnormal.NONE));
		}
		
		return interceptor.nextState();
	}

	/* ************
	 *	 初始化   *
	 ************ */
	
	BattlePlatform pf;

	public FreezeState() {
		super(EPokemonAbnormal.FREEZE);
	}

}
