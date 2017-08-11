package com.zdream.pmw.platform.effect.state;

import com.zdream.pmw.platform.attend.IStateInterceptable;
import com.zdream.pmw.platform.effect.Aperitif;
import com.zdream.pmw.platform.prototype.BattlePlatform;

/**
 * <p>光墙状态
 * <p>由光墙技能释放而触发的状态,
 * 为状态所有的阵营的所有怪兽判断特殊攻击的最后修正环节有伤害降低的额外加成
 * <p>由于判断特防时, 该状态对类似精神冲击、精神击破、神秘之剑等
 * 判断特攻物防的技能也有效, 为了不打破原有的体系, 将其移到判断修正数中
 * <p>截至第七世代, 能够清除光墙的技能有劈瓦和清除浓雾.</p>
 * 
 * @since v0.2.3 [2017-05-12]
 * @author Zdream
 * @version v0.2.3 [2017-05-12]
 */
public class LightScreenState extends ACampState {

	@Override
	public String name() {
		return "light_screen";
	}

	@Override
	public boolean canExecute(String msg) {
		if (CODE_CALC_CORRENT.equals(msg)) {
			return true;
		}
		return false;
	}
	
	@Override
	public String execute(Aperitif ap, IStateInterceptable interceptor, BattlePlatform pf) {
		String head = ap.getHead();
		
		switch (head) {
		case CODE_CALC_CORRENT:
			return calcCorrent(ap, interceptor, pf);

		default:
			break;
		}
		
		return super.execute(ap, interceptor, pf);
	}
	
	/**
	 * 处于光墙场地的怪兽受到特殊技能攻击时,
	 * 如果己方场地上的怪兽只有一只, 修正数乘 50%;
	 * 如果己方场地上的怪兽大于一只, 修正数 2/3.
	 */
	private String calcCorrent(Aperitif ap, IStateInterceptable interceptor, BattlePlatform pf) {
		
		
		return interceptor.nextState();
	}
	

	@Override
	public int priority(String msg) {
		return 0;
	}

}
