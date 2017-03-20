package com.zdream.pmw.monster.data;

import java.io.Serializable;
import java.util.Arrays;

import com.zdream.pmw.monster.prototype.EPokemonType;

/**
 * 精灵的静态数据模型：战斗相关<br>
 * 与 Pokemon 类不同的是，它储存的是不变的静态数据<br>
 * 
 * @since v0.1
 * @author Zdream
 * @date 2016年3月22日
 * @version v0.2
 */
public class PokemonBaseData implements Serializable{
	
	private static final long serialVersionUID = -2277026098568899467L;

	/**
	 * 精灵的 ID<br>
	 * 该表的联合主键之一
	 */
	private short speciesID;
	
	/**
	 * 形态<br>
	 * 该表的联合主键之一
	 */
	private byte form;
	
	/**
	 * 种族名称
	 */
	private String speciesName;
	
	/**
	 * 精灵的属性
	 */
	private EPokemonType[] types;
	
	/**
	 * 精灵的种族值
	 */
	private short[] speciesValue;
	
	/**
	 * 精灵的特性列表
	 */
	private short[] abilities;
	
	/**
	 * 重量
	 */
	private float wt;
	
	public PokemonBaseData() {
		super();
	}

	@Override
	public String toString() {
		return "PokemonBaseData [speciesID=" + speciesID + ", form=" + form + ", speciesName=" + speciesName
				+ ", types=" + Arrays.toString(types) + ", speciesValue=" + Arrays.toString(speciesValue)
				+ ", abilities=" + Arrays.toString(abilities) + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + form;
		result = prime * result + speciesID;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PokemonBaseData other = (PokemonBaseData) obj;
		if (form != other.form)
			return false;
		if (speciesID != other.speciesID)
			return false;
		return true;
	}

	public short getSpeciesID() {
		return speciesID;
	}

	public void setSpeciesID(short speciesID) {
		this.speciesID = speciesID;
	}

	public byte getForm() {
		return form;
	}

	public void setForm(byte form) {
		this.form = form;
	}

	public String getSpeciesName() {
		return speciesName;
	}

	public void setSpeciesName(String speciesName) {
		this.speciesName = speciesName;
	}

	public EPokemonType[] getTypes() {
		return types;
	}

	public void setTypes(EPokemonType[] types) {
		this.types = types;
	}

	public short[] getSpeciesValue() {
		return speciesValue;
	}

	public void setSpeciesValue(short[] speciesValue) {
		this.speciesValue = speciesValue;
	}

	public short[] getAbilities() {
		return abilities;
	}

	public void setAbilities(short[] abilities) {
		this.abilities = abilities;
	}

	public float getWt() {
		return wt;
	}

	public void setWt(float wt) {
		this.wt = wt;
	}
}
