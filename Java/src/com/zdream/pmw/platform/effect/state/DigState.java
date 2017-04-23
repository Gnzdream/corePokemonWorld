package com.zdream.pmw.platform.effect.state;

import com.zdream.pmw.platform.attend.IStateInterceptable;
import com.zdream.pmw.platform.effect.Aperitif;
import com.zdream.pmw.platform.prototype.BattlePlatform;

/**
 * <p>挖洞状态<br>
 * 属于不在场状态中的一类</p>
 * <p>由于怪兽使用了挖洞技能, 从而让自己挖洞, 逃离场地外. (判断时仍然与该座位绑定)</p>
 * <p>拥有该状态的怪兽在大部分情况下能够完全躲避攻击.</p>
 * 
 * @since v0.2.2 [2017-04-20]
 * @author Zdream
 * @version v0.2.2 [2017-04-20]
 */
public class DigState extends AParticipantState {

	@Override
	public String name() {
		return "dig";
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
	
	public DigState() {
		super();
	}
	
	public DigState(byte no) {
		super(no);
	}
	
	@Override
	public String toString() {
		return name();
	}

}
