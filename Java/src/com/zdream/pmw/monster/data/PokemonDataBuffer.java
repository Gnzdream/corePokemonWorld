package com.zdream.pmw.monster.data;

import java.util.HashMap;
import java.util.Map;

import com.zdream.pmw.monster.data.dao.IPokemonDataDao;
import com.zdream.pmwdb.core.DaoGetter;

/**
 * 默认的精灵信息的缓存池<br>
 * 面向数据库存储、单例模式<br>
 * <br>
 * <b>v0.2</b>
 *   删除存储数据库固定的 Model 容器<br>
 * 
 * @since v0.1
 * @author Zdream
 * @date 2016年3月29日
 * @version v0.2
 */
public class PokemonDataBuffer implements IPokemonDataContainer {
	
	/* ************
	 *	单例模式  *
	 ************ */
	private static PokemonDataBuffer instance = new PokemonDataBuffer();
	
	public static PokemonDataBuffer getInstance() {
		return instance;
	}
	
	/**
	 * 启用单例模式
	 */
	private PokemonDataBuffer() {
		super();
		// private
	}
	
	/* ************
	 *	数据结构  *
	 ************ */
	class Form {
		short speciesID;
		byte form;
		public Form(short speciesID, byte form) {
			super();
			this.speciesID = speciesID;
			this.form = form;
		}
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
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
			Form other = (Form) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (form != other.form)
				return false;
			if (speciesID != other.speciesID)
				return false;
			return true;
		}
		private PokemonDataBuffer getOuterType() {
			return PokemonDataBuffer.this;
		}
	}
	
	/**
	 * 缓存输出的 PokemonBaseData 的数据缓存器<br>
	 * 结果集
	 */
	private Map<Form, PokemonBaseData> pmBaseDataMap = new HashMap<Form, PokemonBaseData>();
	
	/* ************
	 *	其它调用  *
	 ************ */
	private IPokemonDataDao dao = DaoGetter.getDao(IPokemonDataDao.class);
	
	
	/* ************
	 *	外部访问  *
	 ************ */
	/**
	 * 获取容器中 <code>PokemonBaseData</code> 的数据<br>
	 * 该容器将会按以下的步骤进行查询：<br>
	 * 1. 如果容器的结果集中含有此数据，直接返回；<br>
	 * 2. 如果结果集没有，缓存并返回从 DAO 获取的数据；<br>
	 * <br>
	 * 组装 <code>PokemonBaseData</code> 所需要的数据在
	 * <code>PokemonDataModel</code> 和 <code>PokemonSpeciesInfoModel</code> 中
	 */
	public PokemonBaseData getBaseData(short speciesID, byte form) {
		Form f = new Form(speciesID, form);
		if (pmBaseDataMap.containsKey(f)) {
			return pmBaseDataMap.get(f);
		} else {
			PokemonBaseData data = dao.getData(speciesID, form);
			pmBaseDataMap.put(f, data);
			return data;
		}
	}
	
	public static void main(String[] args) {
		PokemonDataBuffer buffer = PokemonDataBuffer.getInstance();
		System.out.println(buffer.getBaseData((short)1, (byte) 0));
	}

}
