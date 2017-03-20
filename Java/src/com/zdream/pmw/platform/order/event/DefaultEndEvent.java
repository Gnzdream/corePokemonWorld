package com.zdream.pmw.platform.order.event;

import com.zdream.pmw.platform.attend.AttendManager;
import com.zdream.pmw.platform.effect.Aperitif;
import com.zdream.pmw.platform.prototype.BattlePlatform;

/**
 * 默认启动结尾处事件触发事件<br>
 * 该事件在每回合结尾时段触发<br>
 * 
 * @since v0.1
 * @author Zdream
 * @date 2016年4月2日
 * @version v0.1
 */
public class DefaultEndEvent extends AEvent {

	public DefaultEndEvent() {
		super(EventType.LAST);
	}

	/* ************
	 *	实现方法  *
	 ************ */

	@Override
	public void action(BattlePlatform pf) {
		actionEnd(pf);
	}

	/* ************
	 *	私有方法  *
	 ************ */
	
	/**
	 * 向系统发送回合结束的拦截前消息<br>
	 * 让其它在回合结束时触发的状态进行发动<br>
	 */
	private void actionEnd(BattlePlatform pf) {
		AttendManager am = pf.getAttendManager();
		
		Aperitif value = pf.getEffectManage().newAperitif(Aperitif.CODE_ROUND_END, am.existsSeat());
		pf.readyCode(value);
	}

}
