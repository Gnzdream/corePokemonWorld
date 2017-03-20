package com.zdream.pmw.monster.skill;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.zdream.pmw.monster.data.dao.ISkillDao;

/**
 * 技能类的服务接口实现类<br>
 * 
 * @since v0.1
 * @author Zdream
 * @date 2016年3月23日
 * @version v0.1
 */
public class SkillServiceImpl implements ISkillService{
	
	/* ************
	 *	单例模式  *
	 ************ */
	
	private SkillServiceImpl() {
		super();
		// private 
	}
	
	private static SkillServiceImpl instance = new SkillServiceImpl();
	
	private ISkillDao dao;
	
	public static SkillServiceImpl getInstance() {
		return instance;
	}
	
	public static SkillServiceImpl getInstance(ISkillDao dao) {
		instance.dao = dao;
		return instance;
	}
	
	/* ************
	 *	实现方法  *
	 ************ */
	
	@Override
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
