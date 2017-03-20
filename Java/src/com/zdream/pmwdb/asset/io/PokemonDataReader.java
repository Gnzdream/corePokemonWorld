package com.zdream.pmwdb.asset.io;

import java.util.HashMap;
import java.util.Map;

import com.zdream.pmw.util.json.JsonValue;

/**
 * 缓存、读取怪兽的文本数据<br>
 * 
 * @since v0.2.1
 * @author Zdream
 * @date 2017年3月19日
 * @version v0.2.1
 */
public class PokemonDataReader extends ADataReader<Integer> {
	
	private Map<Integer, JsonValue> map = new HashMap<>();
	private Map<Integer, Boolean> hasRead = new HashMap<>();
	
	public JsonValue getPokemonInfo(int speciesId) {
		if (map.containsKey(speciesId)) {
			return map.get(speciesId);
		} else if (hasRead.containsKey(speciesId / 50)) {
			return null;
		}
		
		read(speciesId);
		return map.get(speciesId);
	}

	@Override
	protected String filePath(Integer key) {
		return "assets/data/monster/monsters_" + (key / 50) + ".json";
	}

	@Override
	protected void onFileRead(Integer key) {
		hasRead.put(key / 50, true);
	}

	@Override
	protected void onDataStore(JsonValue v) {
		map.put((int) v.getMap().get("species_id").getValue(), v);
	}
}
