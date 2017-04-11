package com.zdream.pmwdb.asset.dao;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.zdream.pmw.core.tools.ItemMethods;
import com.zdream.pmw.monster.data.dao.ISkillDao;
import com.zdream.pmw.monster.prototype.EPokemonType;
import com.zdream.pmw.monster.skill.Skill;
import com.zdream.pmw.util.json.JsonValue;
import com.zdream.pmwdb.asset.io.SkillDataReader;

/**
 * 从文件读取技能的基础数据, 并返回<br>
 * 
 * @since v0.2.1
 * @author Zdream
 * @date 2017年3月19日
 * @version v0.2.1
 */
public class SkillDaoImpl implements ISkillDao {
	
	SkillDataReader container = new SkillDataReader();

	@Override
	public int addSkill(Skill skill) {
		// TODO 暂不实现
		return 0;
	}

	@Override
	public int[] addBatchSkills(List<Skill> skills) {
		// TODO 暂不实现
		return null;
	}

	@Override
	public Skill getSkill(short id) {
		JsonValue v = container.getSkillInfo(id);
		
		if (v != null) {
			return toModel(v);
		}
		
		return null;
	}

	@Override
	public List<Skill> getSkills(List<Short> ids) {
		ArrayList<JsonValue> list = new ArrayList<>();
		for (Iterator<Short> it = ids.iterator(); it.hasNext();) {
			Short id = it.next();
			list.add(container.getSkillInfo(id.intValue()));
		}
		
		return toModels(list);
	}

	@Override
	public int deleteSkill(short id) {
		// TODO 暂不实现
		return 0;
	}

	@Override
	public int updateSkill(Skill model) {
		// TODO 暂不实现
		return 0;
	}

	@Override
	public int[] updateBatchSkills(List<Skill> skills) {
		// TODO 暂不实现
		return null;
	}

	@Override
	public List<Integer> allId() {
		// TODO 暂不实现
		return null;
	}
	
	/* ************
	 *	私有方法  *
	 ************ */
	
	private Skill toModel(JsonValue v) {
		Map<String, JsonValue> map = v.getMap();
		
		Skill skill = new Skill();
		skill.setId(((Number) map.get("skill_id").getValue()).shortValue());
		skill.setTitle(map.get("title").getString());
		skill.setType(EPokemonType.parseEnum(map.get("type").getString()));
		
		skill.setPower(((Number) map.get("power").getValue()).shortValue());
		skill.setAccuracy(((Number) map.get("accuracy").getValue()).byteValue());
		skill.setPpMax(((Number) map.get("pp_max").getValue()).byteValue());
		skill.setCategory(ItemMethods.parseCategory(map.get("category").getString()));
		
		skill.setDescription(map.get("desc").getString());
		
		// effect
		skill.setRelease(map.get("effect"));
		
		return skill;
	}
	
	private ArrayList<Skill> toModels(List<JsonValue> list) {
		ArrayList<Skill> skills = new ArrayList<>(list.size());
		
		for (Iterator<JsonValue> it = list.iterator(); it.hasNext();) {
			JsonValue v = it.next();
			skills.add(toModel(v));
		}
		
		return skills;
	}
}
