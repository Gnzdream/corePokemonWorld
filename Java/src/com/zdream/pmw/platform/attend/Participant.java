package com.zdream.pmw.platform.attend;

import java.util.ListIterator;

import com.zdream.pmw.monster.prototype.EPokemonType;
import com.zdream.pmw.monster.prototype.IPokemonDataType;
import com.zdream.pmw.platform.effect.state.ExistState;
import com.zdream.pmw.platform.effect.state.ISubStateCreater;
import com.zdream.pmw.platform.prototype.BattlePlatform;

/**
 * 在场精灵的数据模型<br>
 * <br>
 * <b>v0.1.1</b><br>
 *   完善 toString 方法<br>
 * <br>
 * <b>v0.1.2</b><br>
 *   补充了状态列表属性，实现状态列表处理接口<br>
 * <br>
 * <b>v0.2</b><br>
 *   添加了 seat 属性, 修改了关于 seat 获取的方法<br>
 * <br>
 * <b>v0.2.1</b>
 * <p>继承 {@link StateContainer} 类</p>
 * 
 * @since v0.1.1
 * @author Zdream
 * @date 2016年4月12日
 * @version v0.2
 */
public class Participant extends StateContainer implements IPokemonDataType{
	
	private ExistState exist;
	
	/**
	 * 精灵模型
	 */
	private Attendant attendant;
	
	/**
	 * 精灵属性
	 */
	private EPokemonType[] types;
	
	/**
	 * 座位
	 */
	private byte seat;
	
	public byte getNo() {
		return attendant.getNo();
	}
	
	public void setSeat(byte seat) {
		this.seat = seat;
	}
	
	public byte getSeat() {
		return seat;
	}

	public byte getAbilityLevel(int item) {
		return exist.getAbilityLevel(item);
	}

	public void setAbilityLevel(int item, byte level) {
		exist.setAbilityLevel(item, level);
	}
	
	public Attendant getAttendant() {
		return attendant;
	}
	
	public void setAttendant(Attendant attendant) {
		this.attendant = attendant;
	}

	public EPokemonType[] getTypes() {
		return types;
	}

	public void setTypes(EPokemonType[] types) {
		this.types = types;
	}

	public void setAbilityLevel(byte[] abilityLevel) {
		exist.setAbilityLevels(abilityLevel);
	}
	
	public void putExistState(ExistState state) {
		this.states.add(state);
		this.exist = state;
	}

	/* ************
	 *	代理方法  *
	 ************ */

	public short getSpeciesID() {
		return attendant.getSpeciesID();
	}

	public byte getForm() {
		return attendant.getForm();
	}

	public String getNickname() {
		return attendant.getNickname();
	}

	public short getHpi() {
		return attendant.getHpi();
	}

	public short getSkill(int skillNum) {
		return attendant.getSkill()[skillNum];
	}

	public short getAbility() {
		return attendant.getAbility();
	}
	
	public int getStat(int item) {
		return attendant.getStat()[item];
	}

	/* ************
	 *	实用方法  *
	 ************ */
	
	/**
	 * 删除一位在场怪兽的全部状态, 不包括 {@link ExistState}.<br>
	 * 实现层<br>
	 * @since v0.2.2
	 */
	void removeAllStates(BattlePlatform pf) {
		for (ListIterator<IState> it = states.listIterator(); it.hasNext();) {
			IState s = it.next();
			if (exist == s) {
				continue;
			}
			if (s instanceof ISubStateCreater) {
				((ISubStateCreater) s).handleDestroySubStates(pf);
			}
			s.onDestroy();
		}
		states.clear();
		states.add(exist);
	}

	/* ************
	 *	 初始化   *
	 ************ */
	public Participant(Attendant attendant) {
		this(attendant, (byte) -1);
	}
	
	public Participant(Attendant attendant, byte seat) {
		super();
		this.attendant = attendant;
		this.seat = seat;
		
		// 添加存在状态
		ExistState state = new ExistState(attendant.getNo());
		states.add(state);
		this.exist = state;
	}

	@Override
	public String toString() {
		return attendant.toString();
	}

}
