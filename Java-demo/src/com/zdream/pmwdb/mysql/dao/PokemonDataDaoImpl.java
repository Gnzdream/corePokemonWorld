package com.zdream.pmwdb.mysql.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;

import com.zdream.pmw.monster.data.PokemonBaseData;
import com.zdream.pmw.monster.data.dao.IPokemonDataDao;
import com.zdream.pmwdb.mysql.DbBase;
import com.zdream.pmwdb.mysql.convert.PokemonDataBuilder;
import com.zdream.pmwdb.mysql.model.PokemonDataModel;

/**
 * pm_data 数据表访问数据库的 DAO 层（PC 端特有）<br>
 * 启用单例模式<br>
 * <br>
 * <b>v0.2</b><br>
 *   修改了所在包<br>
 * 
 * @since v0.1
 * @author Zdream
 * @date 2016年3月29日
 * @version v0.2
 */
public class PokemonDataDaoImpl implements IPokemonDataDao {
	
	@Override
	public PokemonBaseData getData(short speciesID) {
		return getData(speciesID, (byte) 0);
	}

	@Override
	public PokemonBaseData getData(final short speciesID, final byte form) {
		Connection conn = DbBase.getConnection();
		QueryRunner runner = DbBase.getRunner();
		PokemonDataModel model1 = null, model2 = null;
		try {
			model1 = runner.query(conn, "SELECT ability1,ability2,ability_secret,data,wt FROM pm_data WHERE species_id=? AND form=?;",
					new ResultSetHandler<PokemonDataModel>() {

						public PokemonDataModel handle(ResultSet rs) throws SQLException {
							if (rs.next()) {
								PokemonDataModel model = new PokemonDataModel();
								model.setSpeciesID(speciesID);
								model.setForm(form);
								model.setAbility1(rs.getShort(1));
								model.setAbility2(rs.getShort(2));
								model.setAbilitySecret(rs.getShort(3));
								model.setData(rs.getBytes(4));
								model.setWt(rs.getBigDecimal(5));
								return model;
							} else {
								return null;
							}
						}

					}, speciesID, form);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		
		try {
			model2 = runner.query(conn, "SELECT root_species_id,species_name FROM pm_species_info WHERE species_id=?;",
					new ResultSetHandler<PokemonDataModel>() {

						public PokemonDataModel handle(ResultSet rs) throws SQLException {
							if (rs.next()) {
								PokemonDataModel model = new PokemonDataModel();
								model.setRootSpeciesId(rs.getShort(1));
								model.setSpeciesName(rs.getString(2));
								return model;
							} else {
								return null;
							}
						}
					}, speciesID);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		if (model1 != null && model2 != null) {
			model1.setRootSpeciesId(model2.getRootSpeciesId());
			model1.setSpeciesName(model2.getSpeciesName());
			return PokemonDataBuilder.convertFromModel(model1);
		}
		return null;
	}

}
