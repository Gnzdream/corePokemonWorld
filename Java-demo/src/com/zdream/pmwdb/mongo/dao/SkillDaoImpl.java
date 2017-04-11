package com.zdream.pmwdb.mongo.dao;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bson.Document;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCursor;
import com.zdream.pmw.core.tools.ItemMethods;
import com.zdream.pmw.monster.data.dao.ISkillDao;
import com.zdream.pmw.monster.prototype.EPokemonType;
import com.zdream.pmw.monster.skill.Skill;
import com.zdream.pmwdb.mongo.BsonParser;
import com.zdream.pmwdb.mongo.MongoBase;

/**
 * 基于 Mongo 的技能基础数据 DAO 实现<br>
 * 
 * @since v0.2
 * @author Zdream
 * @date 2017年3月1日
 * @version v0.2
 */
public class SkillDaoImpl implements ISkillDao {

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
		Document document = new Document("skill_id", id);
		FindIterable<Document> it = MongoBase.getCollection("skill").find(document);
		
		MongoCursor<Document> cursor = it.iterator();
		if (cursor.hasNext()) {
			return toModel(cursor.next());
		}
		
		return null;
	}

	@Override
	public List<Skill> getSkills(List<Short> ids) {
		// db.col.find({$or:[{"skill_id":?},{"skill_id":?}, ...]})
		ArrayList<Object> list = new ArrayList<>();
		for (Iterator<Short> it = ids.iterator(); it.hasNext();) {
			Short id = it.next();
			list.add(new Document("skill_id", Integer.valueOf(id)));
		}
		Document document = new Document("$or", list);
		
		FindIterable<Document> it = MongoBase.getCollection("skill").find(document);
		return toModels(it);
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
	
	private Skill toModel(Document doc) {
		Skill skill = new Skill();
		skill.setId(doc.getInteger("skill_id").shortValue());
		skill.setTitle(doc.getString("title"));
		skill.setType(EPokemonType.parseEnum(doc.getString("type")));
		
		skill.setPower(doc.getInteger("power").shortValue());
		skill.setAccuracy(doc.getInteger("accuracy").byteValue());
		skill.setPpMax(doc.getInteger("pp_max").byteValue());
		skill.setCategory(ItemMethods.parseCategory(doc.getString("category")));
		
		skill.setDescription(doc.getString("desc"));
		
		// effect
		Object obj = doc.get("effect");
		if (obj != null) {
			skill.setRelease(new BsonParser().bson2json(obj));
		}
		
		return skill;
	}
	
	private ArrayList<Skill> toModels(FindIterable<Document> it) {
		ArrayList<Skill> skills = new ArrayList<>();
		
		MongoCursor<Document> cursor = it.iterator();
		while (cursor.hasNext()) {
			Document doc = cursor.next();
			skills.add(toModel(doc));
		}
		
		return skills;
	}

}
