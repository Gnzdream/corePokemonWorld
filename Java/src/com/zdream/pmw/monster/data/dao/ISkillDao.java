package com.zdream.pmw.monster.data.dao;

import java.util.List;

import com.zdream.pmw.monster.skill.Skill;

/**
 * Skill 查询的 DAO 接口<br>
 * <br>
 * <b>v0.2</b><br>
 *   将返回的怪兽数据从 SkillModel 修正为 Skill<br>
 * 
 * @since v0.1
 * @author Zdream
 * @date 2016年3月22日
 * @version v0.2
 */
public interface ISkillDao {
	
	/**
	 * 向数据库添加 SkillModel 数据
	 * @param skill
	 * @return
	 *   数据库返回的编码
	 */
	public int addSkill(Skill skill);
	
	/**
	 * 向数据库添加多个 SkillModel 数据
	 * @param skills
	 * @return
	 *   数据库返回的编码
	 */
	public int[] addBatchSkills(List<Skill> skills);
	
	/**
	 * 查询 id 对应的 skill 数据
	 * @param id
     *   指定的、已经在数据库存在的 skill 的 id
	 * @return
	 */
    public Skill getSkill(short id);
	
	/**
	 * 查询 ids 所有元素对应的 skill 数据<br>
	 * 以列表的形式返回
	 * @param ids
     *   指定的、已经在数据库存在的 skill 的 id 列表
	 * @return
	 */
    public List<Skill> getSkills(List<Short> ids);
    
    /**
     * 删除 id 对应的 skill 数据
     * @param id
     *   指定的、已经在数据库存在的 skill 的 id
     * @return
	 *   数据库返回的编码
     */
    public int deleteSkill(short id);
    
    /**
     * 修改 id 对应的 skill 数据
     * @param model
     *   skill 的数据模型
     * @return
	 *   数据库返回的编码
     */
    public int updateSkill(Skill model);
    
    /**
     * 修改多个 id 对应的 skill 数据
     * @param skills
     *   多个 skill 的数据模型
     * @return
	 *   数据库返回的编码
     */
    public int[] updateBatchSkills(List<Skill> skills);
    
    /**
     * 获得数据源中所有存储的 Id 集合
     * @return
     */
    public List<Integer> allId();
}
