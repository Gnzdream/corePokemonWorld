package com.zdream.pmw.platform.prototype.context;

import com.zdream.pmw.monster.prototype.EPokemonAbnormal;
import com.zdream.pmw.monster.prototype.EPokemonGender;

/**
 * <p>在场怪兽的状态</p>
 * 
 * @since v0.2.2
 * @author Zdream
 * @date 2017年3月28日
 * @version v0.2.2
 */
public class ParticipantContext {
	public int id;
	public int form;
	public String name;
	public int level;
	public EPokemonGender gender;
	public EPokemonAbnormal abnormal;
	
	public int hpi;
	public int hpmax;
	
	public int[] statLevel;
	public String[] states;
}
