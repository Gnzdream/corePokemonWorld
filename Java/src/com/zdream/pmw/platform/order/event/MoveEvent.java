package com.zdream.pmw.platform.order.event;

import com.zdream.pmw.platform.control.IPrintLevel;
import com.zdream.pmw.platform.prototype.BattlePlatform;
import com.zdream.pmw.util.json.JsonValue;

/**
 * 怪兽发动技能事件<br>
 * 
 * @since v0.1
 * @author Zdream
 * @date 2016年4月2日
 * @version v0.1
 */
public class MoveEvent extends AEvent {

	public MoveEvent() {
		super(EventType.MOVE);
	}
	
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
	 * 该技能释放者的 no 值<br>
	 */
	private byte no;

	/**
	 * 指明了该技能释放者的技能<br>
	 * 该号码是指精灵释放的技能在精灵的列表的索引，而不是技能的号码
	 */
	private byte skillNum;
	
	/**
	 * 目标参数
	 */
	private byte target;
	
	/**
	 * 其它附加参数, 用于修改技能释放时的技能参数、触发方式、结算公式等
	 * @since v0.2.2
	 */
	private JsonValue param;
	
	public byte getNo() {
		return no;
	}

	public void setNo(byte no) {
		this.no = no;
	}

	public byte getSkillNum() {
		return skillNum;
	}

	public void setSkillNum(byte skillNum) {
		this.skillNum = skillNum;
	}

	public byte getTarget() {
		return target;
	}

	public void setTarget(byte target) {
		this.target = target;
	}
	
	public JsonValue getParam() {
		return param;
	}
	
	public void setParam(JsonValue param) {
		this.param = param;
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
