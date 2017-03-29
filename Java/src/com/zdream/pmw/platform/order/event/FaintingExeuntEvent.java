package com.zdream.pmw.platform.order.event;

import com.zdream.pmw.platform.attend.AttendManager;
import com.zdream.pmw.platform.control.IPrintLevel;
import com.zdream.pmw.platform.effect.Aperitif;
import com.zdream.pmw.platform.prototype.BattlePlatform;

/**
 * 怪兽由于濒死（hpi=0）而产生的退场事件<br>
 * 
 * @since v0.1
 * @author Zdream
 * @date 2016年4月12日
 * @version v0.1
 */
public class FaintingExeuntEvent extends APreviousEvent {
	
	BattlePlatform pf;

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

	/**
	 * 触发事件：启动退场手续
	 */
	public void action(BattlePlatform pf) {
		this.pf = pf;
		AttendManager am = pf.getAttendManager();
		byte team = am.teamForSeat(seat);
		sendExeuntMsg(team);
		
		if (!am.isExistForTeam(team)) {
			// 该队伍已经失败
			pf.logPrintf(IPrintLevel.PRINT_LEVEL_INFO, 
					"FaintingExeuntE.action() 胜负已定，%d 号队伍已经失败", team);
			byte camp = am.successCamp();
			if (camp != -1) {
				// 胜负已定，camp 阵营获得了胜利 TODO
				pf.getControlManager().requestEnd(camp);
				pf.setFinish(true);
			}
		}
		this.pf = null;
	}
	
	@Override
	public int priority() {
		return -1;
	}
	
	/**
	 * 向系统发送因为濒死而退场的拦截前消息
	 * @param team
	 *   该濒死精灵所属的队伍
	 */
	private void sendExeuntMsg(byte team) {
		Aperitif value = pf.getEffectManage().newAperitif(Aperitif.CODE_EXEUNT_FAINT, seat);
		value.append("seat", seat);
		value.append("team", team);
		pf.readyCode(value);
	}

	/* ************
	 *	 初始化   *
	 ************ */

	public FaintingExeuntEvent(byte seat) {
		super();
		this.seat = seat;
	}

}
