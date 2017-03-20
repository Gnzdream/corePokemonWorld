package com.zdream.pmw.platform.effect.state;

import com.zdream.pmw.monster.prototype.EPokemonAbnormal;

/**
 * 抽象异常状态<br>
 * 当精灵被施加异常状态时，该精灵会添加此类状态的子类实例<br>
 * 该状态只能添加到精灵上，而不能添加在座位、阵营、场地上<br>
 * <br>
 * <b>v0.2</b><br>
 *   添加状态的归属属性, 这个状态是哪只精灵的<br>
 * 
 * @since v0.1
 * @author Zdream
 * @date 2016年4月17日
 * @version v0.2
 */
public abstract class AAbnormalState extends ParticipantState {

	/* ************
	 *	  属性    *
	 ************ */
	
	/**
	 * 异常状态
	 */
	private final EPokemonAbnormal abnormal;
	
	public EPokemonAbnormal getAbnormal() {
		return abnormal;
	}

	/* ************
	 *	 初始化   *
	 ************ */

	public AAbnormalState(EPokemonAbnormal abnormal) {
		if (abnormal == null) {
			throw new NullPointerException("abnormal is null");
		}
		
		this.abnormal = abnormal;
	}

	/* ************
	 *	实现方法  *
	 ************ */
	
	@Override
	public final String name() {
		return abnormal.name();
	}
	
	@Override
	public String ofCategory() {
		return "abnormal";
	}

}
