package com.zdream.pmwdb.mysql;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import com.zdream.pmwdb.mysql.model.SkillModel;

/**
 * Mysql 等关系型数据库的连接基础类<br>
 * 
 * @since v0.1
 * @author Zdream
 * @date 2016年1月1日 (不确定)
 * @version v0.1
 */
public class DbBase {

	private static String DB_USER;
	private static String DB_PSWD;
	private static String DB_DRIVER;
	private static String DB_URL;
	
	private static QueryRunner RUNNER = new QueryRunner();
	
	static {
		InputStream in = null;
		Properties p = new Properties();
		
		try {
			in = DbBase.class.getResourceAsStream("/db.properties");
			p.load(in);
			
			DB_USER = p.getProperty("jdbc.user");
			DB_PSWD = p.getProperty("jdbc.password");
			DB_DRIVER = p.getProperty("jdbc.driverClass");
			DB_URL = p.getProperty("jdbc.url");
			DbUtils.loadDriver(DB_DRIVER);
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 获取与数据库的连接的实例
	 * @return
	 */
	public static Connection getConnection() {
		try {
			return DriverManager.getConnection(DB_URL, DB_USER, DB_PSWD);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 关闭与数据库的连接
	 */
	public static void closeConnection(Connection conn) {
		if (conn != null) {
			DbUtils.closeQuietly(conn);
		}
	}
	
	/**
	 * 获得 QueryRunner 的实例
	 * @return
	 */
	public static QueryRunner getRunner() {
		return RUNNER;
	}
	
	/**
	 * 测试用
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println(DB_USER);
		System.out.println(DB_PSWD);
		System.out.println(DB_DRIVER);
		System.out.println(DB_URL);
		
		Connection conn = getConnection();
		QueryRunner qr = getRunner();
		
		try {
			List<SkillModel> results = (List<SkillModel>) qr.query(conn, 
					"select id,title,type,power,accuracy,category,ppMax,description from Skill",
					new BeanListHandler<SkillModel>(SkillModel.class));
			for (int i = 0; i < results.size(); i++) {
				SkillModel st = (SkillModel) results.get(i);
				System.out.println(st.toString());
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeConnection(conn);
		}
		
	}
}
