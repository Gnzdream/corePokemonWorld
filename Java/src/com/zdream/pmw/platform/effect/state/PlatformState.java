package com.zdream.pmw.platform.effect.state;

import com.zdream.pmw.platform.attend.IStateContainer;
import com.zdream.pmw.platform.attend.IStateInterceptable;
import com.zdream.pmw.platform.effect.Aperitif;
import com.zdream.pmw.platform.prototype.BattlePlatform;

/**
 * <p>全场状态容器的初始状态</p>
 * <p>该状态会默认添加、一直保留到战斗结束.<br>
 * 
 * @since v0.2.2 [2017-04-14]
 * @author Zdream
 * @version v0.2.2 [2017-04-14]
 */
public final class PlatformState extends ABaseState {

	@Override
	public String name() {
		return "platform";
	}

	@Override
	public String execute(Aperitif ap, IStateInterceptable interceptor, BattlePlatform pf) {
		return calcAdditionRate(ap, interceptor, pf);
	}

	@Override
	protected IStateContainer stateContainer(BattlePlatform pf) {
		return pf.getAttendManager().getPlatformStates();
	}

	@Override
	protected boolean confirmTarget(Aperitif ap, BattlePlatform pf) {
		return true;
	}

	/* ************
	 *	 初始化   *
	 ************ */
	
	@Override
	public String toString() {
		return name();
	}

}
