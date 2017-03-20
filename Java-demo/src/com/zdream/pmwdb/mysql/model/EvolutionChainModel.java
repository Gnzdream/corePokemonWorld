package com.zdream.pmwdb.mysql.model;

/**
 * 进化链与进化条件的静态数据库模型<br>
 * 
 * @since v0.1
 * @author Zdream
 * @date 2016年3月28日
 * @version v0.2
 */
public class EvolutionChainModel {
	
	/**
	 * 进化后的种族 ID<br>
	 * 作为该表的主键
	 */
	private short afterId;
	
	/**
	 * 进化前的种族 ID
	 */
	private short beforeId;
	
	/**
	 * 进化条件
	 */
	private byte evolutionCondition;
	
	/**
	 * 进化参数<br>
	 * 该参数的意义取决于进化条件
	 */
	private byte evolutionParam;
	
	public EvolutionChainModel() {}

	@Override
	public String toString() {
		return "EvolutionChainModel [afterId=" + afterId + ", beforeId=" + beforeId + ", evolutionCondition="
				+ evolutionCondition + ", evolutionParam=" + evolutionParam + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + afterId;
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
		EvolutionChainModel other = (EvolutionChainModel) obj;
		if (afterId != other.afterId)
			return false;
		return true;
	}

	public short getAfterId() {
		return afterId;
	}

	public void setAfterId(short afterId) {
		this.afterId = afterId;
	}

	public short getBeforeId() {
		return beforeId;
	}

	public void setBeforeId(short beforeId) {
		this.beforeId = beforeId;
	}

	public byte getEvolutionCondition() {
		return evolutionCondition;
	}

	public void setEvolutionCondition(byte evolutionCondition) {
		this.evolutionCondition = evolutionCondition;
	}

	public byte getEvolutionParam() {
		return evolutionParam;
	}

	public void setEvolutionParam(byte evolutionParam) {
		this.evolutionParam = evolutionParam;
	}

}
