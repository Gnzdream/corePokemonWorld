package com.zdream.pmwdb.asset.dao;

import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import com.zdream.pmw.monster.data.PokemonBaseData;
import com.zdream.pmw.monster.data.dao.IPokemonDataDao;
import com.zdream.pmw.monster.prototype.EPokemonType;
import com.zdream.pmw.util.json.JsonValue;
import com.zdream.pmwdb.asset.io.PokemonDataReader;

/**
 * 从文件读取怪兽基础数据, 并返回<br>
 * 
 * @since v0.2.1
 * @author Zdream
 * @date 2017年3月19日
 * @version v0.2.1
 */
public class PokemonDataDaoImpl implements IPokemonDataDao {
	
	PokemonDataReader container = new PokemonDataReader();

	@Override
	public PokemonBaseData getData(short speciesId) {
		JsonValue v = container.getPokemonInfo(speciesId);
		if (v == null) {
			return null;
		}
		return toModel(v);
	}

	@Override
	public PokemonBaseData getData(short speciesId, byte form) {
		return getData(speciesId);
	}
	
	/* ************
	 *	私有方法  *
	 ************ */
	
	private PokemonBaseData toModel(JsonValue v) {
		PokemonBaseData data = new PokemonBaseData();
		Map<String, JsonValue> map = v.getMap();
		data.setSpeciesID(((Number) map.get("species_id").getValue()).shortValue());
		data.setForm(((Number) map.get("form").getValue()).byteValue());
		
		// 属性
		List<JsonValue> typearr = map.get("type").getArray();
		EPokemonType[] types = new EPokemonType[typearr.size()];
		for (ListIterator<JsonValue> ittype = typearr.listIterator(); ittype.hasNext();) {
			JsonValue obj = ittype.next();
			String typestr = obj.getString();
			
			EPokemonType type = EPokemonType.parseEnum(typestr);
			types[ittype.previousIndex()] = type;
		}
		data.setTypes(types);
		
		// 种族值
		Map<String, JsonValue> valdoc = map.get("val").getMap();
		short[] speciesValue = new short[]
				{
						((Number) valdoc.get("hp").getValue()).shortValue(),
						((Number) valdoc.get("at").getValue()).shortValue(),
						((Number) valdoc.get("df").getValue()).shortValue(),
						((Number) valdoc.get("sa").getValue()).shortValue(),
						((Number) valdoc.get("sd").getValue()).shortValue(),
						((Number) valdoc.get("sp").getValue()).shortValue()
				};
		data.setSpeciesValue(speciesValue);
		
		data.setWt(((Number) map.get("wt").getValue()).floatValue());
		data.setSpeciesName(map.get("species_name").getString());
		
		return data;
	}

}
