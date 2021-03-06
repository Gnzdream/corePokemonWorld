package com.zdream.pmwdb.mongo.tofile;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import org.bson.Document;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCursor;
import com.zdream.pmw.util.json.JsonBuilder;
import com.zdream.pmw.util.json.JsonObject;
import com.zdream.pmwdb.mongo.BsonParser;
import com.zdream.pmwdb.mongo.MongoBase;

/**
 * 
 * @author Zdream
 */
public class DownloadMonster {
	
	@SuppressWarnings("unchecked")
	ArrayList<EJson>[] list = new ArrayList[15];

	public void mkdir() {
		File file = new File("assets/data/skill");
		if (!file.exists()) {
			file.mkdirs();
		}
	}
	
	public void read() {
		BsonParser parser = new BsonParser();
		FindIterable<Document> it = MongoBase.getCollection("pm").find();
		
		MongoCursor<Document> cursor = it.iterator();
		while (cursor.hasNext()) {
			Document doc = cursor.next();
			JsonObject v = parser.bson2json(doc).asObject();
			v.asMap().remove("_id");
			int id = (int) v.asMap().get("species_id").getValue();
			
			EJson ej = new EJson();
			ej.id = id;
			ej.value = v;
			list[id / 50].add(ej);
		}
		
		// sort
		EJsonComparator comparator = new EJsonComparator();
		for (int i = 0; i < list.length; i++) {
			list[i].sort(comparator);
		}
	}
	
	public void write() {
		JsonBuilder builder = new JsonBuilder();
		
		for (int i = 0; i < list.length; i++) {
			if (list[i].size() == 0) {
				continue;
			}
			
			try {
				FileWriter writer = new FileWriter(new File("assets/data/monster/monsters_" + i + ".json"));
				writer.append("{\r\n");
				
				int count = 0;
				for (Iterator<EJson> it = list[i].iterator(); it.hasNext();) {
					EJson ej = it.next();
					
					writer.append("\"").append(Integer.toString(ej.id)).append("\":");
					writer.append(builder.getJson(ej.value));
					if (it.hasNext()) {
						writer.append(",");
					}
					writer.append("\r\n");
					count++;
				}
				System.out.println(String.format("%2d: complated, %d object(s).", i, count));
				
				writer.append("}");
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public DownloadMonster() {
		for (int i = 0; i < list.length; i++) {
			list[i] = new ArrayList<>(50);
		}
	}

	public static void main(String[] args) {
		DownloadMonster d = new DownloadMonster();
		d.mkdir();
		d.read();
		d.write();
	}

}
