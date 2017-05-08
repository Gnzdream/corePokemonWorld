package com.zdream.pmw.platform.effect.state;

import com.zdream.pmw.platform.attend.IState;
import com.zdream.pmw.platform.attend.IStateContainer;
import com.zdream.pmw.platform.attend.IStateInterceptable;
import com.zdream.pmw.platform.attend.service.EStateSource;
import com.zdream.pmw.platform.effect.Aperitif;
import com.zdream.pmw.platform.prototype.BattlePlatform;

/**
 * <p>基本状态</p>
 * <p>在一个状态容器的建立就会带有该状态. 该状态会默认添加、一直保留到战斗结束.<br>
 * 它的主要作用是用于去重</p>
 * 
 * @since v0.2.2 [2017-04-17]
 * @author Zdream
 * @version v0.2.2 [2017-04-17]
 */
public abstract class ABaseState implements IState {
	
	protected abstract IStateContainer stateContainer(BattlePlatform pf);
	
	@Override
	public boolean canExecute(String msg) {
		if (CODE_CALC_ADDITION_RATE.equals(msg)) {
			return true;
		}
		return false;
	}
	
	protected String calcAdditionRate(Aperitif ap, IStateInterceptable interceptor, BattlePlatform pf) {
		// CODE_CALC_ADDITION_RATE
		// 判断部分
		// 防御方限定
		if (!confirmTarget(ap, pf)) {
			return interceptor.nextState();
		}
		
		// 只处理状态
		if (!"state".equals(ap.get("type"))) {
			return interceptor.nextState();
		}
		
		// 如果是删除状态, 则不处理 v0.2.2
		Object rm = ap.get("remove");
		if (rm != null) {
			if ((boolean) rm) {
				return interceptor.nextState();
			}
		}

		// 处理部分
		if (!ap.hasFilter("repeat") && handleRepeat(ap, pf)) {
			// 不能施加状态
			return onRepeat(ap, interceptor, pf);
		}
		
		return interceptor.nextState();
	}

	/**
	 * <p>判断本次处理的是否为该目标</p>
	 * <p>进行去重, 是在状态添加的时候,
	 * 检查状态施加目标是否为该基本类处理的容器对应的目标</p>
	 */
	protected abstract boolean confirmTarget(Aperitif ap, BattlePlatform pf);

	/**
	 * <p>判断已经重复时, 处理的操作和需要返回的数据</p>
	 */
	protected String onRepeat(Aperitif ap, IStateInterceptable interceptor, BattlePlatform pf) {
		ap.append("reason", "repeat").append("result", 1);
		return null;
	}

	/**
	 * 比大多数状态都要提早触发
	 */
	public int priority(String msg) {
		return 10;
	}
	
	@Override
	public String ofCategory() {
		return "base";
	}

	/**
	 * 发动源: 无<br>
	 * 在怪兽上场后, 该状态就会一直保留, 直到濒死退场或交换退场
	 */
	public EStateSource source() {
		return EStateSource.OTHER;
	}
	
	private boolean handleRepeat(Aperitif value, BattlePlatform pf) {
		// 获取状态的分类
		Object o = value.get("state-category");
		if (o == null) {
			o = value.get("state");
		}
		String stateCategory = o.toString();
		
		if (stateContainer(pf).hasCategoryState(stateCategory)) {
			return true;
		}
		
		return false;
	}
}
