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
import com.zdream.pmw.util.json.JsonValue;
import com.zdream.pmwdb.mongo.BsonParser;
import com.zdream.pmwdb.mongo.MongoBase;

/**
 * 
 * @author Zdream
 */
public class DownloadSkill {
	
	@SuppressWarnings("unchecked")
	ArrayList<EJson>[] list = new ArrayList[10];

	public void mkdir() {
		File file = new File("assets/data/skill");
		if (!file.exists()) {
			file.mkdirs();
		}
	}
	
	public void read() {
		BsonParser parser = new BsonParser();
		FindIterable<Document> it = MongoBase.getCollection("skill").find();
		
		MongoCursor<Document> cursor = it.iterator();
		while (cursor.hasNext()) {
			Document doc = cursor.next();
			JsonValue v = parser.bson2json(doc);
			v.getMap().remove("_id");
			int id = (int) v.getMap().get("skill_id").getValue();
			
			EJson ej = new EJson();
			ej.id = id;
			ej.value = v;
			int idx = id / 50;
			
			// new condition
			if (idx != 1) {
				continue;
			}
			
			list[idx].add(ej);
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
				FileWriter writer = new FileWriter(new File("assets/data/skill/skills_" + i + ".json"));
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
	
	public DownloadSkill() {
		for (int i = 0; i < list.length; i++) {
			list[i] = new ArrayList<>(50);
		}
	}

	public static void main(String[] args) {
		DownloadSkill d = new DownloadSkill();
		d.mkdir();
		d.read();
		d.write();
	}
	
}
