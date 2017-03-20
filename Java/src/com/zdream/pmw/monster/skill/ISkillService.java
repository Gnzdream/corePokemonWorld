package com.zdream.pmw.monster.skill;

import java.util.List;

/**
 * 技能类的服务接口<br>
 * 
 * @since v0.1
 * @author Zdream
 * @date 2016年3月23日
 * @version v0.1
 */
public interface ISkillService {
	
	/**
	 * 向数据源中更新与添加新的技能对象<br>
	 * 由于规定在数据源中的技能对象的 id 字段是不允许重复的，因此由输入的 skills 的 id 来判断每个技能是添加还是更新
	 * @param skills
	 * @return
	 */
	public int addAndUpdateAll(List<Skill> skills);
	
}
