package com.zdream.pmw.monster.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.zdream.pmw.monster.data.dao.ISkillDao;
import com.zdream.pmw.monster.skill.Skill;
import com.zdream.pmw.util.json.JsonValue;
import com.zdream.pmwdb.core.DaoGetter;

/**
 * 默认的技能信息的缓存池<br>
 * 面向数据库存储、单例模式<br>
 * 
 * @since v0.1
 * @author Zdream
 * @date 2016年4月3日
 * @version v0.2
 */
public class SkillDataBuffer {
	
	/* ************
	 *	单例模式  *
	 ************ */
	private static SkillDataBuffer instance = new SkillDataBuffer();
	
	public static SkillDataBuffer getInstance() {
		return instance;
	}
	
	/**
	 * 启用单例模式
	 */
	private SkillDataBuffer() {
		super();
		// private
	}
	
	/* ************
	 *	数据结构  *
	 ************ */
	
	/**
	 * 缓存输出的 SkillModel 的数据缓存器<br>
	 * 结果集
	 */
	private Map<Short, Skill> skillMap = new HashMap<Short, Skill>();
	
	/* ************
	 *	其它调用  *
	 ************ */
	private ISkillDao dao = DaoGetter.getDao(ISkillDao.class);
	

	/* ************
	 *	外部访问  *
	 ************ */
	/**
	 * 获取容器中 <code>Skill</code> 的数据<br>
	 * 该容器将会按以下的步骤进行查询：<br>
	 * 1. 如果容器的结果集中含有此数据，直接返回；<br>
	 * 2. 如果结果集没有，但是原始数据集含有组装数据必要的原料，该方法将组装好数据并返回；<br>
	 * 3. 如果缺少必要的组装材料，该方法将从 DAO 层查询并组装、转换必要的数据；<br>
	 * <br>
	 * 组装 <code>Skill</code> 所需要的数据在
	 * <code>SkillModel</code>中
	 * @param skillID
	 * @return
	 */
	public Skill getBaseData(short skillID) {
		Short s = Short.valueOf(skillID);
		if (skillMap.containsKey(s)) {
			return skillMap.get(s);
		} else {
			Skill result = dao.getSkill(skillID);
			skillMap.put(s, result);
			return result;
		}
	}
	
	/**
	 * 获得对应技能的释放参数
	 * @param skillID
	 * @return
	 *   可能为空
	 */
	public JsonValue getReleaseData(short skillID) {
		Short s = Short.valueOf(skillID);
		if (skillMap.containsKey(s)) {
			return skillMap.get(s).getRelease();
		} else {
			Skill result = dao.getSkill(skillID);
			skillMap.put(s, result);
			return result.getRelease();
		}
	}
	
	/**
	 * 提前缓存技能数据
	 * @param skillIDs
	 * @return 
	 *   添加缓存技能的数量
	 */
	public int preLoad(List<Short> skillIDs) {
		Set<Short> set = new HashSet<Short>();
		for (Iterator<Short> it = skillIDs.iterator(); it.hasNext();) {
			Short s = it.next();
			if (!skillMap.containsKey(s)) {
				set.add(s);
			}
		}
		
		List<Skill> skills = dao.getSkills(new ArrayList<Short>(set));
		for (Iterator<Skill> it = skills.iterator(); it.hasNext();) {
			Skill skill = it.next();
			skillMap.put(skill.getId(), skill);
		}
		
		return skills.size();
	}
	
	/* ************
	 *	通用方法  *
	 ************ */
	@Override
	public String toString() {
		return "SkillDataBuffer [skill:" + skillMap.size() + "]";
	}

}
