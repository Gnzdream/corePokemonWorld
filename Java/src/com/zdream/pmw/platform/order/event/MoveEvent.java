package com.zdream.pmw.platform.order.event;

import com.zdream.pmw.platform.control.IPrintLevel;
import com.zdream.pmw.platform.prototype.BattlePlatform;

/**
 * 怪兽发动技能事件<br>
 * 
 * @since v0.1
 * @author Zdream
 * @date 2016年4月2日
 * @version v0.1
 */
public class MoveEvent extends AMoveEvent {
	
	/* ************
	 *	 优先度   *
	 ************ */
	
	/**
	 * 其它效果<br>
	 * 比如道具参数<br>
	 * 数值越高速度判定越快
	 */
	private byte addition;

	public byte getAddition() {
		return addition;
	}

	public void setAddition(byte addition) {
		this.addition = addition;
	}
	
	/* ************
	 *	技能数据  *
	 ************ */

	/**
	 * 指明了该技能释放者的技能<br>
	 * 该号码是指精灵释放的技能在精灵的列表的索引，而不是技能的号码
	 */
	private int skillNum;
	
	/**
	 * 目标参数
	 */
	private byte target;

	public int getSkillNum() {
		return skillNum;
	}

	public void setSkillNum(int skillNum) {
		this.skillNum = skillNum;
	}

	public byte getTarget() {
		return target;
	}

	public void setTarget(byte target) {
		this.target = target;
	}
	
	/* ************
	 *	  触发    *
	 ************ */
	@Override
	public void action(BattlePlatform pf) {
		pf.logPrintf(IPrintLevel.PRINT_LEVEL_DEBUG, 
				"MoveEvent.action(): 精灵发动技能事件 no = %d, skillNum = %d, target = %d",
				no, skillNum, target);
		// 触发技能
		pf.getEffectManage().moveAct(no, skillNum, target, param);
	}

}
