package com.zdream.pmwdb.asset.io;

import java.util.HashMap;
import java.util.Map;

import com.zdream.pmw.util.json.JsonObject;
import com.zdream.pmw.util.json.JsonValue;

/**
 * 缓存、读取技能的文本数据<br>
 * 
 * @since v0.2.1
 * @author Zdream
 * @date 2017年3月19日
 * @version v0.2.1
 */
public class SkillDataReader extends ADataReader<Integer> {
	
	private Map<Integer, JsonValue> map = new HashMap<>();
	private Map<Integer, Boolean> hasRead = new HashMap<>();
	
	public JsonValue getSkillInfo(int skillId) {
		if (map.containsKey(skillId)) {
			return map.get(skillId);
		} else if (hasRead.containsKey(skillId / 50)) {
			return null;
		}
		
		read(skillId);
		return map.get(skillId);
	}

	@Override
	protected String filePath(Integer key) {
		return "assets/data/skill/skills_" + (key / 50) + ".json";
	}

	@Override
	protected void onFileRead(Integer key) {
		hasRead.put(key / 50, true);
	}

	@Override
	protected void onDataStore(JsonObject v) {
		map.put((int) v.asMap().get("skill_id").getValue(), v);
	}
}
