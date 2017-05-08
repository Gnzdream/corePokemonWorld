package com.zdream.pmwdb.asset.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map.Entry;

import com.zdream.pmw.util.json.JsonBuilder;
import com.zdream.pmw.util.json.JsonObject;
import com.zdream.pmw.util.json.JsonValue;

/**
 * 读取文本<br>
 * 
 * @since v0.2.1
 * @author Zdream
 * @date 2017年3月19日
 * @version v0.2.1
 */
public abstract class ADataReader<I> {
	
	protected final JsonBuilder builder = JsonBuilder.getDefaultInstance();
	
	protected abstract String filePath(I key);
	protected abstract void onFileRead(I key);
	protected abstract void onDataStore(JsonObject v);

	protected JsonValue read(I key) {
		File file = new File(filePath(key));
		if (!file.exists()) {
			return null;
		}
		
		onFileRead(key);
		
		try {
			FileReader reader = new FileReader(file);
			char[] chs = new char[1000];
			StringBuilder builder = new StringBuilder();
			
			int len;
			while ((len = reader.read(chs)) != -1) {
				builder.append(chs, 0, len);
			}
			reader.close();
			
			JsonValue v = this.builder.parseJson(builder.toString());
			for (Iterator<Entry<String, JsonValue>> it = v.asObject().asMap().entrySet().iterator();
					it.hasNext();) {
				Entry<String, JsonValue> entry = it.next();
				
				try {
					onDataStore(entry.getValue().asObject());
				} catch (RuntimeException e) {
					e.printStackTrace();
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
