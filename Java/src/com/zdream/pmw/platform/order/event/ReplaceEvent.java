package com.zdream.pmw.platform.order.event;

import com.zdream.pmw.platform.attend.AttendManager;
import com.zdream.pmw.platform.control.IPrintLevel;
import com.zdream.pmw.platform.prototype.BattlePlatform;

/**
 * 交换怪兽上场事件<br>
 * <p>交换怪兽事件是个优先度高的事件, 一般比技能先触发</p>
 * 
 * @since v0.2.1
 * @author Zdream
 * @date 2017年3月6日
 * @version v0.2.1
 */
public class ReplaceEvent extends APreviousEvent {

	/* ************
	 *	技能数据  *
	 ************ */
	/**
	 * 需要更换下场的 no 值<br>
	 */
	private byte no;
	/**
	 * 需要更换上场的怪兽的 no<br>
	 */
	private byte replaceNo;
	/**
	 * 座位号<br>
	 * <p>当没有退场怪兽, 只有入场怪兽时, seat 是必需的.</p>
	 */
	private byte seat;
	
	public byte getNo() {
		return no;
	}

	public void setNo(byte no) {
		this.no = no;
	}

	public byte getReplaceNo() {
		return replaceNo;
	}
	
	public void setReplaceNo(byte replaceNo) {
		this.replaceNo = replaceNo;
	}

	public byte getSeat() {
		return seat;
	}

	public void setSeat(byte seat) {
		this.seat = seat;
	}

	/* ************
	 *	实现方法  *
	 ************ */

	@Override
	public void action(BattlePlatform pf) {
		AttendManager am = pf.getAttendManager();
		if (replaceNo == -1) {
			pf.logPrintf(IPrintLevel.PRINT_LEVEL_DEBUG, 
					"ReplaceEvent.action(): 怪兽交换上场 %s(no=%d) 下场 %s(no=%d, seat=%d)",
					am.getAttendant(replaceNo).getNickname(), replaceNo,
					am.getAttendant(no).getNickname(), no, am.seatForNo(no));
			pf.getEffectManage().exchangeAct(no, replaceNo);
		} else {
			pf.logPrintf(IPrintLevel.PRINT_LEVEL_DEBUG, 
					"ReplaceEvent.action(): 怪兽交换上场 %s(no=%d) 到位置 %d",
					am.getAttendant(replaceNo).getNickname(), replaceNo, seat);
			pf.getEffectManage().enteranceAct(seat, replaceNo);
		}
	}

	@Override
	public int priority() {
		return 0;
	}
	
	/* ************
	 *	 初始化   *
	 ************ */
	public ReplaceEvent() {
		super();
	}

}
