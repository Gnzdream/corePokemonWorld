package com.zdream.pmw.monster.data;

/**
 * 精灵信息的缓存池接口
 * 
 * @since v0.1
 * @author Zdream
 * @date 2016年3月29日
 */
public interface IPokemonDataContainer {
	
	/**
	 * 从容器中获取指定的 PokemonBaseData 的数据
	 * @param speciesID
	 *   精灵的种族 ID
	 * @param form
	 *   精灵的形态
	 * @return
	 */
	public PokemonBaseData getBaseData(short speciesID, byte form);
	
}
