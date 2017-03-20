package com.zdream.pmwdb.core;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * 运用反射方法, 用类似 spring 的方式获得 DAO, 进行解耦<br>
 * 
 * @since v0.2
 * @author Zdream
 * @date 2017年3月1日
 * @version v0.2
 */
public class DaoGetter {
	
	private static String use;
	
	private static Properties pro;
	
	private static Map<String, Object> map = new HashMap<>();
	
	static {
		pro = new Properties();
		try {
			pro.load(DaoGetter.class.getResourceAsStream("/com/zdream/pmwdb/core/db.properties"));
			use = pro.getProperty("use");
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> T getDao(Class<T> c) {
		String classname = c.getSimpleName();
		
		try {
			if (!map.containsKey(classname)) {
				String load = pro.getProperty(use + "." + classname);

				try {
					Class<T> clazz = (Class<T>) Class.forName(load);
					T obj = clazz.newInstance();
					map.put(classname, obj);
					return obj;
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			} else {
				return (T) map.get(classname);
			} 
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
