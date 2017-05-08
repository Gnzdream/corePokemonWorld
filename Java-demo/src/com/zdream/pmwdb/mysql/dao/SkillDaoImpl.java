package com.zdream.pmwdb.mysql.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;

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
}
