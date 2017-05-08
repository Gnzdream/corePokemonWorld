package com.zdream.pmwdb.mysql.convert;

import java.util.Map;

import com.zdream.pmw.monster.prototype.EPokemonType;
import com.zdream.pmw.monster.skill.ESkillCategory;
import com.zdream.pmw.monster.skill.Skill;
import com.zdream.pmw.platform.attend.SkillRelease;
import com.zdream.pmw.util.json.JsonBuilder;
import com.zdream.pmw.util.json.JsonObject;
import com.zdream.pmw.util.json.JsonValue;
import com.zdream.pmwdb.mysql.model.SkillModel;

/**
 * 技能模型的构造类<br>
 * <br>
 * <b>v0.2</b><br>
 *   修改了所在包<br>
 * 
 * @since v0.1
 * @author Zdream
 * @date 2016年3月18日
 * @version v0.2
 */
public class SkillBuilder {

	/**
	 * 用 Json 的数据构造一个技能类
	 * @param value
	 * @return
	 */
	public static Skill build(JsonObject value) {
		Skill skill = new Skill();
		Map<String, JsonValue> map = value.asMap();
		
		skill.setId(((Integer)(map.get("id").getValue())).shortValue());
		skill.setTitle((String)(map.get("title").getValue()));
		skill.setType(EPokemonType.parseEnum((String)(map.get("type").getValue())));
		skill.setPower(((Integer)(map.get("power").getValue())).shortValue());
		skill.setAccuracy(((Integer)(map.get("accuracy").getValue())).byteValue());
		skill.setCategory(ESkillCategory.parseEnum((String)(map.get("category").getValue())));
		skill.setPpMax(((Integer)(map.get("ppMax").getValue())).byteValue());
		skill.setDescription((String)(map.get("description").getValue()));
		
		System.out.println(skill);
		return skill;
	}
	
	/**
	 * 将技能转成数据库数据
	 * @param skill
	 * @return
	 */
	public static SkillModel convertToModel(Skill skill) {
		SkillModel model = new SkillModel();
		model.setId(skill.getId());
		model.setTitle(skill.getTitle());
		model.setType((byte) skill.getType().ordinal());
		model.setPower(skill.getPower());
		model.setAccuracy(skill.getAccuracy());
		model.setCategory((byte) skill.getCategory().ordinal());
		model.setPpMax(skill.getPpMax());
		model.setDescription(skill.getDescription());
		return model;
	}
	
	/**
	 * 将数据库数据转成技能模型
	 * @param model
	 * @return
	 */
	public static Skill convertFromModel(SkillModel model) {
		Skill skill = new Skill();
		skill.setId(model.getId());
		skill.setTitle(model.getTitle());
		skill.setType(EPokemonType.parseEnum(model.getType()));
		skill.setPower(model.getPower());
		skill.setAccuracy(model.getAccuracy());
		skill.setCategory(ESkillCategory.parseEnum(model.getCategory()));
		skill.setPpMax(model.getPpMax());
		skill.setDescription(model.getDescription());
		
		JsonValue value = new JsonBuilder().parseJson(model.getReleaseEffect());
		if (value != null) {
			skill.setRelease(value.asArray());
		}
		
		return skill;
	}
	
	/**
	 * 将数据库数据转成技能释放模型
	 * @param model
	 * @return
	 */
	public static SkillRelease 
			convertReleaseFromModel(SkillModel model) {
		SkillRelease release = 
				new SkillRelease(convertFromModel(model));
		
		
		return release;
	}

}
