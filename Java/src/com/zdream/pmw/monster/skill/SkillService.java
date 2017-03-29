package com.zdream.pmw.monster.skill;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.zdream.pmw.monster.data.dao.ISkillDao;

/**
 * 技能类的服务<br>
 * 
 * @since v0.1
 * @author Zdream
 * @date 2016年3月23日
 * @version v0.1
 */
public class SkillService {
	
	/* ************
	 *	单例模式  *
	 ************ */
	/*
	 * singleton pattern
	 */
	private SkillService() {
		super();
		// private 
	}
	
	private static SkillService instance = new SkillService();
	
	private ISkillDao dao;
	
	public static SkillService getInstance() {
		return instance;
	}
	
	public static SkillService getInstance(ISkillDao dao) {
		instance.dao = dao;
		return instance;
	}
	
	/* ************
	 *	实现方法  *
	 ************ */
	
	/**
	 * 向数据源中更新与添加新的技能对象<br>
	 * <p>由于规定在数据源中的技能对象的 id 字段是不允许重复的,
	 * 因此由输入的 skills 的 id 来判断每个技能是添加还是更新</p>
	 * @param skills
	 * @return
	 */
	public int addAndUpdateAll(List<Skill> skills) {
		List<Skill> adds = new LinkedList<Skill>(),
				updates = new LinkedList<Skill>();
		int result = -1;
		
		List<Integer> list = dao.allId();
		Set<Integer> set = new HashSet<Integer>(list);
		
		// Iterator<Skill> it = skills.iterator();
		for (Iterator<Skill> it = skills.iterator(); it.hasNext();) {
			Skill skill = it.next();
			
			if (set.contains((int) skill.getId())) {
				updates.add(skill);
			} else {
				adds.add(skill);
			}
		}
		if (adds.size() > 0) {
			dao.addBatchSkills(adds);
		}
		if (updates.size() > 0) {
			dao.updateBatchSkills(updates);
		}
		
		return result;
	}
	
}
