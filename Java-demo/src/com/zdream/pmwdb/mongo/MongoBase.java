package com.zdream.pmwdb.mongo;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Mongo 数据库连接的基础类<br>
 * 
 * @since v0.2
 * @author Zdream
 * @date 2017年3月1日
 * @version v0.2
 */
public class MongoBase {

	static MongoClient client;
	static MongoDatabase db;
	
	static {
		// 停止 log 输出
		Logger.getLogger("org.mongodb.driver").setLevel(Level.SEVERE);
		
		// 连接到 mongodb 服务
		client = new MongoClient("localhost", 27017);
		
		// 连接到数据库
		db = client.getDatabase("test");
		//System.out.println("Connect to database successfully");
	}
	
	public static MongoCollection<Document> getCollection(String colname) {
		return db.getCollection(colname);
	}

}
