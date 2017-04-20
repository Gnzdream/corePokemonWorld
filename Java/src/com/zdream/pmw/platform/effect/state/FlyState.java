package com.zdream.pmw.platform.effect.state;

import com.zdream.pmw.platform.attend.IStateInterceptable;
import com.zdream.pmw.platform.effect.Aperitif;
import com.zdream.pmw.platform.prototype.BattlePlatform;

/**
 * <p>飞翔状态<br>
 * 属于不在场状态中的一类</p>
 * <p>由于怪兽使用了飞翔（飞空）技能, 从而让自己飞出场地外.</p>
 * <p>拥有该状态的怪兽在大部分情况下能够完全躲避攻击.</p>
 * 
 * @since v0.2.2
 *   [2017-04-12]
 * @author Zdream
 * @version v0.2.2
 *   [2017-04-12]
 */
public class FlyState extends AParticipantState {

	@Override
	public String name() {
		return "fly";
	}
	
	@Override
	public String ofCategory() {
		return "absent";
	}

	@Override
	public boolean canExecute(String msg) {
		if (CODE_CALC_HIDE.equals(msg)) {
			return true;
		}
		return false;
	}
	
	@Override
	public String execute(Aperitif ap, IStateInterceptable interceptor, BattlePlatform pf) {
		// CODE_CALC_HIDE
		
		boolean hide = (boolean) ap.get("abs");
		if (hide) {
			ap.replace("reason", ap.get("reason") + "," + ofCategory() + ":" + name());
		} else {
			ap.append("reason", ofCategory() + ":" + name());
			ap.append("abs", true);
		}
		
		return super.execute(ap, interceptor, pf);
	}

	@Override
	public int priority(String msg) {
		return 0;
	}

	/* ************
	 *	 初始化   *
	 ************ */
	
	public FlyState() {
		super();
	}
	
	public FlyState(byte no) {
		super(no);
	}
	
	@Override
	public String toString() {
		return name();
	}
}
