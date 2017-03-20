package com.zdream.pmw.monster.data.dao;

import com.zdream.pmw.monster.data.PokemonBaseData;

/**
 * PokemonBaseData 查询的 DAO 接口<br>
 * 通用查询窗口（不包换修改）<br>
 * <br>
 * <b>v0.2</b><br>
 *   将返回的怪兽数据从 PokemonDataModel 修正为 PokemonBaseData<br>
 * 
 * @since v0.1
 * @author Zdream
 * @date 2016年3月29日
 * @version v0.2
 */
public interface IPokemonDataDao {
	
	/**
	 * 查询 id 对应的 pm_data 数据<br>
	 * 默认给出最普通形态（form = 0）的精灵
	 * @param speciesID
     *   指定的、已经在数据库存在的 pm_data 的 id
	 * @return
	 */
	public PokemonBaseData getData(short speciesID);
	
	/**
	 * 查询 id, 指定形态对应的 pm_data 数据
	 * @param speciesID
     *   指定的、已经在数据库存在的 pm_data 的 id
	 * @param form
     *   指定的形态
	 * @return
	 */
	public PokemonBaseData getData(short speciesID, byte form);
	
}
