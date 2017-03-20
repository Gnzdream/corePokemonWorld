package com.zdream.pmwdb.mysql.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.ColumnListHandler;

import com.zdream.pmw.monster.data.dao.ISkillDao;
import com.zdream.pmw.monster.skill.Skill;
import com.zdream.pmwdb.mysql.DbBase;
import com.zdream.pmwdb.mysql.convert.SkillBuilder;
import com.zdream.pmwdb.mysql.model.SkillModel;

/**
 * Skill 数据表访问数据库的 DAO 层（PC 端特有）
 * @since v1.0
 * @author Zdream
 * @email 18042046922@163.com
 * @date: 2016年3月22日
 */
public class SkillDaoImpl implements ISkillDao{
	
	private static final String TABLE_NAME = "Skill";
	
	@Override
	public int addSkill(Skill model) {
		Connection conn = DbBase.getConnection();
		QueryRunner runner = DbBase.getRunner();
		int result = -1;
		try {
			result = runner.update(conn, "insert into " + TABLE_NAME
					+ "(skill_id,title,type,power,accuracy,category,ppmax,description,release_effect)"
					+ "values(?,?,?,?,?,?,?,?,?);",
					model.getId(), model.getTitle(), model.getType(), model.getPower(), model.getAccuracy(), 
					model.getCategory(), model.getPpMax(), model.getDescription(), model.getRelease().getString());
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	@Override
	public int[] addBatchSkills(List<Skill> skills) {
		Connection conn = DbBase.getConnection();
		QueryRunner runner = DbBase.getRunner();
		Object[][] params = new Object[skills.size()][9];
		
		Iterator<Skill> it = skills.iterator();
		for (int i = 0; i < params.length; i++) {
			Object[] objs = params[i];
			Skill skill = it.next();

			objs[0] = skill.getId();
			objs[1] = skill.getTitle();
			objs[2] = skill.getType();
			objs[3] = skill.getPower();
			objs[4] = skill.getAccuracy();
			objs[5] = skill.getCategory();
			objs[6] = skill.getPpMax();
			objs[7] = skill.getDescription();
			objs[8] = skill.getRelease().getString();
		}
		int result[] = null;
		try {
			result = runner.batch(conn, "insert into " + TABLE_NAME
					+ "(skill_id,title,type,power,accuracy,category,ppmax,description,release_effect)"
					+ "values(?,?,?,?,?,?,?,?,?);", params);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public Skill getSkill(final short id) {
		Connection conn = DbBase.getConnection();
		QueryRunner runner = DbBase.getRunner();
		SkillModel model = null;
		try {
			model = runner.query(conn, 
					"SELECT title,type,power,accuracy,category,ppmax,description,release_effect FROM "
					+ TABLE_NAME + " WHERE skill_id=?;",
					new ResultSetHandler<SkillModel>() {

						public SkillModel handle(ResultSet rs) throws SQLException {
							if (rs.next()) {
								SkillModel model = new SkillModel();
								model.setTitle(rs.getString(1));
								model.setType(rs.getByte(2));
								model.setPower(rs.getShort(3));
								model.setAccuracy(rs.getByte(4));
								model.setCategory(rs.getByte(5));
								model.setPpMax(rs.getByte(6));
								model.setDescription(rs.getString(7));
								model.setReleaseEffect(rs.getString(8));
								model.setId(id);
								return model;
							} else {
								return null;
							}
						}

					}, id);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		return SkillBuilder.convertFromModel(model);
	}
	
	@Override
	public List<Skill> getSkills(final List<Short> ids) {
		Connection conn = DbBase.getConnection();
		QueryRunner runner = DbBase.getRunner();
		final int length = ids.size();
		List<SkillModel> models = null;
		
		if (length == 0) {
			return new ArrayList<Skill>();
		}
		
		String sql = null;
		Object[] params = new Object[length];
		{
			StringBuilder builder = new StringBuilder(128);
			builder.append("SELECT title,type,power,accuracy,category,ppmax,description,release_effect,skill_id FROM "
					+ TABLE_NAME + " WHERE ");
			for (int i = 0; i < length - 1; i++) {
				builder.append("skill_id=? OR ");
				params[i] = ids.get(i);
			}
			builder.append("skill_id=?;");
			params[length - 1] = ids.get(length - 1);
			sql = builder.toString();
		}
		
		try {
			models = runner.query(conn, sql, new ResultSetHandler<List<SkillModel>>() {

				public List<SkillModel> handle(ResultSet rs) throws SQLException {
					List<SkillModel> list = new ArrayList<SkillModel>(length);
					while (rs.next()) {
						SkillModel model = new SkillModel();
						model.setTitle(rs.getString(1));
						model.setType(rs.getByte(2));
						model.setPower(rs.getShort(3));
						model.setAccuracy(rs.getByte(4));
						model.setCategory(rs.getByte(5));
						model.setPpMax(rs.getByte(6));
						model.setDescription(rs.getString(7));
						model.setReleaseEffect(rs.getString(8));
						model.setId(rs.getShort(9));
						list.add(model);
					}
					return list;
				}

			}, params);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		
		List<Skill> skills = new ArrayList<>(models.size());
		for (Iterator<SkillModel> it = models.iterator(); it.hasNext();) {
			SkillModel model = it.next();
			skills.add(SkillBuilder.convertFromModel(model));
		}
		
		
		return skills;
	}

	@Override
	public int deleteSkill(short id) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int updateSkill(Skill skill) {
		Connection conn = DbBase.getConnection();
		QueryRunner runner = DbBase.getRunner();
		int result = -1;
		try {
			result = runner.update(conn, "update " + TABLE_NAME + 
					" set title=?,type=?,power=?,accuracy=?,category=?,ppmax=?,description=?,release_effect=? "
					+ "where skill_id=?;", skill.getTitle(), skill.getType(), skill.getPower(), skill.getAccuracy(),
					skill.getCategory(), skill.getPpMax(), skill.getDescription(), skill.getRelease().getString()
					, skill.getId());
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	@Override
	public int[] updateBatchSkills(List<Skill> skills) {
		Connection conn = DbBase.getConnection();
		QueryRunner runner = DbBase.getRunner();
		Object[][] params = new Object[skills.size()][9];
		
		Iterator<Skill> it = skills.iterator();
		for (int i = 0; i < params.length; i++) {
			Object[] objs = params[i];
			Skill skill = it.next();
			
			objs[0] = skill.getTitle();
			objs[1] = skill.getType();
			objs[2] = skill.getPower();
			objs[3] = skill.getAccuracy();
			objs[4] = skill.getCategory();
			objs[5] = skill.getPpMax();
			objs[6] = skill.getDescription();
			objs[7] = skill.getRelease().getString();
			objs[8] = skill.getId();
		}
		int result[] = null;
		try {
			result = runner.batch(conn, "update " + TABLE_NAME + 
					" set title=?,type=?,power=?,accuracy=?,category=?,ppmax=?,description=?,release_effect=? "
					+ "where skill_id=?;", params);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public List<Integer> allId() {
		Connection conn = DbBase.getConnection();
		QueryRunner runner = DbBase.getRunner();
		List<Integer> result = null;
		try {
			result = runner.query(conn, "select skill_id from " + TABLE_NAME + ';', 
					new ColumnListHandler<Integer>());
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}
}
