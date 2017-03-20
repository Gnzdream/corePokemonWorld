package com.zdream.pmwdb.mysql.model;

import java.math.BigDecimal;
import java.util.Arrays;

/**
 * 精灵的静态数据数据库模型<br>
 * <br>
 * <b>v0.2</b><br>
 *   合并 PokemonSpeciesInfoModel<br>
 * 
 * @since v0.1
 * @author Zdream
 * @date 2016年3月24日
 * @verison v0.2
 */
public class PokemonDataModel {
	
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
	 * 根种群 ID<br>
	 * 即该精灵的最终退化的种群 ID
	 */
	private short rootSpeciesId;
	
	/**
	 * 种群名称
	 */
	private String speciesName;
	
	/**
	 * 属性 1 和 2 和种族值
	 */
	private byte[] data;
	
	/**
	 * 特性 1 和 2，隐藏特性
	 */
	private short ability1, ability2, abilitySecret;
	
	/**
	 * 体重
	 */
	private BigDecimal wt;

	public PokemonDataModel() {
		super();
	}

	@Override
	public String toString() {
		return "PokemonDataModel [speciesID=" + speciesID + ", form=" + form
				+ ", data=" + Arrays.toString(data) + ", ability1=" + ability1 + ", ability2=" + ability2
				+ ", abilitySecret=" + abilitySecret + ", wt=" + wt + "]";
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
		PokemonDataModel other = (PokemonDataModel) obj;
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

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public short getAbility1() {
		return ability1;
	}

	public void setAbility1(short ability1) {
		this.ability1 = ability1;
	}

	public short getAbility2() {
		return ability2;
	}

	public void setAbility2(short ability2) {
		this.ability2 = ability2;
	}

	public short getAbilitySecret() {
		return abilitySecret;
	}

	public void setAbilitySecret(short abilitySecret) {
		this.abilitySecret = abilitySecret;
	}

	public BigDecimal getWt() {
		return wt;
	}

	public void setWt(BigDecimal wt) {
		this.wt = wt;
	}

	public short getRootSpeciesId() {
		return rootSpeciesId;
	}

	public void setRootSpeciesId(short rootSpeciesId) {
		this.rootSpeciesId = rootSpeciesId;
	}

	public String getSpeciesName() {
		return speciesName;
	}

	public void setSpeciesName(String speciesName) {
		this.speciesName = speciesName;
	}
}
