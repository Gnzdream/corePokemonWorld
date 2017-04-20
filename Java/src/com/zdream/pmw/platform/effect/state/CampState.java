package com.zdream.pmw.platform.effect.state;

import com.zdream.pmw.platform.attend.IStateContainer;
import com.zdream.pmw.platform.attend.IStateInterceptable;
import com.zdream.pmw.platform.effect.Aperitif;
import com.zdream.pmw.platform.prototype.BattlePlatform;

/**
 * <p>阵营状态容器的初始状态</p>
 * <p>一个阵营建立就会带有该状态. 该状态会默认添加、一直保留到战斗结束.<br>
 * 
 * @since v0.2.2 [2017-04-14]
 * @author Zdream
 * @version v0.2.2 [2017-04-14]
 */
public final class CampState extends ABaseState {

	/* ************
	 *	  属性    *
	 ************ */
	
	byte camp;
	
	public byte getCamp() {
		return camp;
	}

	/* ************
	 *	实现方法  *
	 ************ */

	@Override
	public String name() {
		return "camp";
	}

	@Override
	public String execute(Aperitif ap, IStateInterceptable interceptor, BattlePlatform pf) {
		return calcAdditionRate(ap, interceptor, pf);
	}

	@Override
	protected IStateContainer stateContainer(BattlePlatform pf) {
		return pf.getAttendManager().getCampStates(camp);
	}

	@Override
	protected boolean confirmTarget(Aperitif ap, BattlePlatform pf) {
		Object o = ap.get("camp");
		if (o != null) {
			return (byte) o == camp;
		}
		
		byte seat = (byte) ap.get("target");
		return pf.getAttendManager().campForSeat(seat) == camp;
	}

	/* ************
	 *	 初始化   *
	 ************ */
	
	public CampState(byte camp) {
		this.camp = camp;
	}
	
	@Override
	public String toString() {
		return name() + ":" + camp;
	}

}
