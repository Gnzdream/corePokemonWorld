package com.zdream.pmw.platform.effect.state;

import com.zdream.pmw.platform.attend.IState;
import com.zdream.pmw.platform.attend.IStateInterceptable;
import com.zdream.pmw.platform.attend.service.EStateSource;
import com.zdream.pmw.platform.effect.Aperitif;
import com.zdream.pmw.platform.prototype.BattlePlatform;

/**
 * 濒死保护状态<br>
 * 当上场的精灵因为各种原因而陷入濒死状态时<br>
 * 就会启动濒死保护状态<br>
 * 该状态能够：<br>
 * * 防止别的精灵附加异常状态<br>
 * * 防止别的精灵附加能力等级变化状态<br>
 * * 防止连续攻击的精灵对其进行攻击（在判定范围时，该怪兽会从判定范围中移出）<br>
 * 等<br>
 * 状态生命周期：<br>
 * * 跟随精灵<br>
 * * 当精灵退场后，状态自动清理掉<br>
 * 
 * @since v0.1
 * @author Zdream
 * @date 2016年4月12日
 * @version v0.1
 */
public class FaintingProtectedState implements IState {

	/* ************
	 *	  属性    *
	 ************ */
	
	private byte seat;
	
	/**
	 * 该由于濒死而触发退场事件的精灵的 seat<br>
	 * @return
	 */
	public byte getSeat() {
		return seat;
	}

	/* ************
	 *	实现方法  *
	 ************ */

	@Override
	public String name() {
		return "濒死保护";
	}

	/**
	 * 该状态由于什么而产生没有直接关系<br>
	 * 因此选择 <code>OTHER</code><br>
	 */
	public EStateSource source() {
		return EStateSource.OTHER;
	}

	@Override
	public boolean canExecute(String msg) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String execute(Aperitif value, IStateInterceptable interceptor, BattlePlatform pf) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int priority(String msg) {
		return -1;
	}

	/* ************
	 *	 初始化   *
	 ************ */

	public FaintingProtectedState(byte seat) {
		this.seat = seat;
	}

}
