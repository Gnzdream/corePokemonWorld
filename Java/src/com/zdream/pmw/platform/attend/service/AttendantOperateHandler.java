package com.zdream.pmw.platform.attend.service;

import com.zdream.pmw.monster.prototype.EPokemonAbnormal;
import com.zdream.pmw.monster.prototype.IPokemonDataType;
import com.zdream.pmw.platform.attend.AttendManager;
import com.zdream.pmw.platform.attend.Attendant;
import com.zdream.pmw.platform.attend.IState;
import com.zdream.pmw.platform.attend.Participant;
import com.zdream.pmw.platform.common.AbnormalMethods;
import com.zdream.pmw.platform.common.ItemMethods;
import com.zdream.pmw.platform.control.IPrintLevel;
import com.zdream.pmw.platform.effect.state.AAbnormalState;
import com.zdream.pmw.platform.effect.state.BadlyPoisonState;
import com.zdream.pmw.platform.effect.state.BurnState;
import com.zdream.pmw.platform.effect.state.FreezeState;
import com.zdream.pmw.platform.effect.state.ParalysisState;
import com.zdream.pmw.platform.effect.state.PoisonState;
import com.zdream.pmw.platform.effect.state.SleepState;
import com.zdream.pmw.util.json.JsonValue;

/**
 * 参与精灵的数据直接操作类<br>
 * 该接口定义的所有方法是直接操作精灵的数据，因此请保证调用前已经完成状态的拦截与修正<br>
 * <br>
 * <b>v0.2</b><br>
 *   将该类直接移至 <code>AttendManager</code> 下进行管理, 删除接口;<br>
 * 
 * @since v0.1
 * @author Zdream
 * @date 2016年4月8日
 * @version v0.2
 */
public class AttendantOperateHandler {
	
	AttendManager am;
	
	public AttendantOperateHandler(AttendManager am) {
		this.am = am;
	}
	
	/* ************
	 *	  HP PP   *
	 ************ */
	
	/**
	 * 扣 PP
	 * @param seat
	 *   扣 PP 的主体，该精灵的 seat
	 * @param skillNum
	 *   精灵第几个技能，不是技能 ID<br>
	 * @param value
	 *   扣多少 PP
	 */
	public void ppSub(byte seat, byte skillNum, byte value) {
		Attendant attendant = am.getParticipant(seat).getAttendant();
		am.logPrintf(IPrintLevel.PRINT_LEVEL_VERBOSE, 
				"精灵 %s[seat=%d] 的技能 %s[num=%d] PP 扣 %d", 
				attendant.getNickname(), seat,
				am.getSkillRelease(attendant.getSkill()[skillNum]).getSkill().getTitle(),
				skillNum, value);
		
		byte[] pps = attendant.getSkillPP();
		pps[skillNum] -= value;
		attendant.setSkillPP(pps);
	}
	
	/**
	 * HP 变化
	 * @param seat
	 *   扣 HP 的主体，该精灵的 seat
	 * @param value
	 *   扣多少 HP
	 */
	public void hpChange(byte seat, int value) {
		Attendant attendant = am.getParticipant(seat).getAttendant();
		short hpi = attendant.getHpi();
		short hpm = attendant.getStat()[IPokemonDataType.HP];
		if (value > 0) {
			hpi += value;
			if (hpi > hpm) {
				hpi = hpm;
			}
			am.getRoot().logPrintf(IPrintLevel.PRINT_LEVEL_VERBOSE, 
					"精灵 %s[seat=%d] HP +%d，剩余 %d / %d", 
					attendant.getNickname(), seat, value, hpi, hpm);
		} else if (value < 0)  {
			hpi += value;
			if (hpi < 0) {
				hpi = 0;
			}
			am.getRoot().logPrintf(IPrintLevel.PRINT_LEVEL_VERBOSE, 
					"精灵 %s[seat=%d] HP %d，剩余 %d / %d", 
					attendant.getNickname(), seat, value, hpi, hpm);
		}
		
		attendant.setHpi(hpi);
	}
	
	/* ************
	 *	  状态    *
	 ************ */
	
	/**
	 * 向 seat 对应的在场怪兽强制施加 / 清除异常状态<br>
	 * 实现层<br>
	 * 如果怪兽本身有其它的异常状态，需要将该状态移除，随后进行施加异常状态码、添加状态实例<br>
	 * @param seat
	 * @param abnormal
	 *   施加何种异常状态<br>
	 *   当该参数为 <code>EPokemonAbnormal.NONE</code> 就是强制清除异常状态
	 */
	public void forceAbnormal(byte seat, byte abnormal) {
		Participant participant = am.getParticipant(seat);
		if (participant != null) {
			// 首先删除原始状态实例
			EPokemonAbnormal ab;
			if ((ab = am.abnormal(seat)) != EPokemonAbnormal.NONE) {
				participant.removeState(participant.getState(ab.name()));
			}
			
			// 写入状态码
			participant.getAttendant().setAbnormal(abnormal);
			
			AAbnormalState state = null;
			ab = AbnormalMethods.toAbnormal(abnormal);
			am.logPrintf(IPrintLevel.PRINT_LEVEL_WARN, 
					"AttendantOperateHandler.forceAbnormal(2): 精灵添加异常状态这里仍在施工, 状态: %s", ab.name());
			switch (ab) {
			case POISON:
				state = new PoisonState();
				break;
			case BADLY_POISON:
				state = new BadlyPoisonState();
				break;
			case PARALYSIS:
				state = new ParalysisState();
				break;
			case SLEEP:
				state = new SleepState();
				break;
			case BURN:
				state = new BurnState();
				break;
			case FREEZE:
				state = new FreezeState();
				break;

			default:
				break;
			}
			
			if (state != null) {
				state.setNo(participant.getNo());
				participant.pushState(state);
			}
		}
	}
	
	/**
	 * 向一位在场的怪兽施加状态<br>
	 * 实现层<br>
	 * <p>这里将不判断任何状态重复、相悖之类的问题, 只是将状态绑定到对应的怪兽上.
	 * 如果有这类问题, 请确保在调用该函数之前就已经审查完毕. 一般的做法是,
	 * 发送 {@code count-addition-rate} 这类拦截前消息, 用状态栈拦截后查看效果.</p>
	 * @param no
	 *   怪兽的 no
	 * @param state
	 *   施加的状态
	 * @since v0.2.1
	 */
	public void forceStateForParticipant(byte no, IState state) {
		Participant participant = am.getParticipant(am.seatForNo(no));
		
		participant.pushState(state);
	}
	
	/**
	 * 删除一位在场怪兽的指定状态<br>
	 * 实现层<br>
	 * <p>这里将怪兽的状态列表中删除<b>所有</b>符合 {@code statename} 的状态</p>
	 * @param seat
	 *   怪兽的 seat
	 * @param stateName
	 *   所要删除的状态的名称
	 * @since v0.2.1
	 */
	public void removeStateFromParticipant(byte seat, String stateName) {
		Participant participant = am.getParticipant(seat);
		
		while (true) {
			IState state = participant.getState(stateName);
			if (state == null) {
				break;
			}
			participant.removeState(state);
		}
	}
	
	/**
	 * 设置一位在场怪兽的指定状态, 为其操作状态使其某些属性变化<br>
	 * 实现层<br>
	 * <p>这里将怪兽的状态列表中删除<b>所有</b>符合 {@code statename} 的状态</p>
	 * @param seat
	 *   怪兽的 seat
	 * @param stateName
	 *   所要删除的状态的名称
	 * @param value
	 *   所要设置给状态的参数
	 * @since v0.2.1
	 */
	public void setStateFromParticipant(byte seat, String stateName, JsonValue value) {
		Participant participant = am.getParticipant(seat);
		
		participant.setState(stateName, value, am.getRoot());
	}
	
	/* ************
	 *	能力等级  *
	 ************ */
	
	/**
	 * 向 seat 对应的在场怪兽强制改变指定能力项的能力等级<br>
	 * 实现层<br>
	 * @param seat
	 * @param item
	 *   能力项，该数值在 <code>IPokemonDataType</code> 里面定义
	 * @param value
	 *   变化的数值
	 */
	public void changeAbilityLevel(byte seat, int item, int value) {
		Participant participant = am.getParticipant(seat);
		
		participant.setAbilityLevel(item, 
				(byte)(participant.getAbilityLevel(item) + value));
		
		am.logPrintf(IPrintLevel.PRINT_LEVEL_VERBOSE, 
				"ParticipantHSI.changeAbilityLevel(3) 精灵%s(seat=%d) 的%s能力变化%d，为%d",
				participant.getAttendant().getNickname(),
				seat, ItemMethods.parseItemName(item), value, participant.getAbilityLevel(item));
	}
	
	/**
	 * 向 seat 对应的在场怪兽强制设置 (重置) 指定能力项的能力等级<br>
	 * 实现层<br>
	 * @param seat
	 * @param item
	 *   能力项，该数值在 <code>IPokemonDataType</code> 里面定义
	 * @param value
	 *   设置后, 该能力变成的数值
	 * @since v0.2.1
	 */
	public void setAbilityLevel(byte seat, int item, int value) {
		Participant participant = am.getParticipant(seat);
		
		am.logPrintf(IPrintLevel.PRINT_LEVEL_VERBOSE, 
				"ParticipantHSI.setAbilityLevel(3) 精灵%s(seat=%d) 的%s能力设置为%d，原来为%d",
				participant.getAttendant().getNickname(),
				seat, ItemMethods.parseItemName(item), value, participant.getAbilityLevel(item));
		
		participant.setAbilityLevel(item, (byte)value);
	}

}
