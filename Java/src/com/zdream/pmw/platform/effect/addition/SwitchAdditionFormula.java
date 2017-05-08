package com.zdream.pmw.platform.effect.addition;

import java.util.Arrays;

import com.zdream.pmw.platform.attend.AttendManager;
import com.zdream.pmw.platform.effect.Aperitif;
import com.zdream.pmw.util.random.RanValue;

/**
 * <p>强制换人的附加效果判断公式</p>
 * 
 * @since v0.2.2
 *   [2017-04-12]
 * @author Zdream
 * @version v0.2.2
 *   [2017-04-12]
 */
public class SwitchAdditionFormula extends AAdditionFormula {
	
	/* ************
	 *	数据结构  *
	 ************ */
	
	/**
	 * 模式
	 * @see #MODE_DEFAULT
	 */
	int mode;
	
	/**
	 * <p>默认模式</p>
	 * <p>当交换下场方为攻击方时, 下场后可由攻方自行选择上场怪兽;<br>
	 * 当交换下场方为防御方时, 下场后自动选择上场怪兽</p>
	 */
	public static final int MODE_DEFAULT = 0;
	
	/*
	 * 缓存数据
	 */
	/**
	 * 候选人列表, no[]
	 */
	byte[] candidates = new byte[6];
	int length = 0; // candidates 有效长度
	
	/* ************
	 *	实现方法  *
	 ************ */

	@Override
	public String name() {
		return "switch";
	}

	@Override
	protected boolean canTrigger() {
		if (calcCandidates() > 0) {
			return sentVaildMessage();
		}
		return false;
	}

	@Override
	protected void force() {
		int len = targets.length;
		if (len != 1) {
			throw new IllegalStateException(
					"目标为 " + Arrays.toString(targets) + " 不唯一");
		}
		
		// 选择 noIn
		byte noIn = candidates[RanValue.random(0, length)];
		byte noOut = (targets[0] == pack.getAtStaff().getSeat()) ?
				pack.getAtStaff().getNo() : am.noForSeat(targets[0]);
		em.exchangeAct(noOut, noIn);
	}
	
	@Override
	public void restore() {
		super.restore();
		mode = MODE_DEFAULT;
		length = 0;
	}
	
	/* ************
	 *	私有方法  *
	 ************ */
	
	private int calcCandidates() {
		byte team;
		AttendManager am = em.getAttends();
		byte atseat = pack.getAtStaff().getSeat();
		
		if (targets[0] == atseat) {
			team = am.teamForNo(pack.getAtStaff().getNo());
		} else {
			team = am.teamForSeat(targets[0]);
		}
		
		// 全场参与怪兽人数
		int len = am.attendantLength();
		for (byte no = 0; no < len; no++) {
			if (am.teamForNo(no) == team && 
					am.seatForNo(no) == -1 && 
					am.getAttendant(no).getHpi() > 0) {
				candidates[length++] = no;
			}
		}
		
		return length;
	}
	
	private boolean sentVaildMessage() {
		byte atseat = pack.getAtStaff().getSeat();
		byte[] dfseats = pack.dfSeats();
		
		Aperitif ap = em.newAperitif(Aperitif.CODE_ADDITION_SETTLE, dfseats);
		ap.putScanSeats(atseat);
		ap.append("atseat", atseat).append("dfseats", dfseats)
			.append("category", "switch");
		
		if (atseat == targets[0]) {
			ap.append("target", atseat);
		} else {
			if (dfseats.length != 1) {
				throw new IllegalArgumentException(
						"dfseats.length = " + dfseats.length + ": 攻击目标不唯一的交换附加效果没有完成");
			}
			ap.append("target", dfseats[0]);
		}
		
		em.startCode(ap);
		Integer abs = (Integer) ap.get("abs");
		if (abs == null || abs != 2) {
			return true;
		}
		
		return false;
	}

}
