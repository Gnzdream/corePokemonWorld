package com.zdream.pmw.platform.effect.state;import com.zdream.pmw.platform.attend.IStateContainer;
import com.zdream.pmw.platform.attend.IStateInterceptable;
import com.zdream.pmw.platform.effect.Aperitif;
import com.zdream.pmw.platform.prototype.BattlePlatform;

/**
 * <p>座位状态容器的初始状态</p>
 * <p>一个座位建立就会带有该状态. 该状态会默认添加、一直保留到战斗结束.<br>
 * 和 {@link ExistState} 类似, 它的作用也是用于去重</p>
 * 
 * @since v0.2.2 [2017-04-14]
 * @author Zdream
 * @version v0.2.2 [2017-04-14]
 */
public final class SeatState extends ABaseState {

	/* ************
	 *	  属性    *
	 ************ */
	
	byte seat;
	
	public byte getSeat() {
		return seat;
	}

	/* ************
	 *	实现方法  *
	 ************ */

	@Override
	public String name() {
		return "seat";
	}

	@Override
	public String execute(Aperitif ap, IStateInterceptable interceptor, BattlePlatform pf) {
		return calcAdditionRate(ap, interceptor, pf);
	}

	@Override
	protected IStateContainer stateContainer(BattlePlatform pf) {
		return pf.getAttendManager().getSeatStates(seat);
	}

	@Override
	protected boolean confirmTarget(Aperitif ap, BattlePlatform pf) {
		byte seat = (byte) ap.get("target");
		return seat == this.seat;
	}

	/* ************
	 *	 初始化   *
	 ************ */
	
	public SeatState(byte seat) {
		this.seat = seat;
	}
	
	@Override
	public String toString() {
		return name() + ":" + seat;
	}

}
