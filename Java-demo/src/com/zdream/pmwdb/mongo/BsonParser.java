package com.zdream.pmwdb.mongo;

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map.Entry;
import java.util.Set;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.zdream.pmw.util.json.JsonArray;
import com.zdream.pmw.util.json.JsonObject;
import com.zdream.pmw.util.json.JsonValue;
import com.zdream.pmw.util.json.JsonValue.JsonType;

/**
 * bson 与 json 间的转化;<br>
 * 不支持:<br>
 *   ObjectId: json -> bson<br>
 *   Date: json <-> bson<br>
 * 
 * @since v0.2
 * @author Zdream
 * @date 2017年3月1日
 * @version v0.2
 */
public class BsonParser {

	public JsonValue bson2json(Object doc) {
		if (doc == null) {
			return null;
		}
		return parseObject(doc);
	}
	
	private JsonValue parseObject(Object obj) {
		if (obj == null) {
			return JsonValue.createJson(JsonType.NullType);
		}
		
		if (obj instanceof Integer || obj instanceof Double ||
				obj instanceof String || obj instanceof Boolean) {
			return parseValue(obj);
		} else if (obj instanceof ObjectId) {
			return parseValue(((ObjectId) obj).toHexString());
		} else if (obj instanceof List<?>) {
			return parseArray((List<?>) obj);
		} else if (obj instanceof Document) {
			return parseDocument((Document) obj);
		}
		return null;
	}

	private JsonValue parseValue(Object i) {
		return JsonValue.createJson(i);
	}

	private JsonValue parseDocument(Document doc) {
		JsonObject value = new JsonObject();
		
		Set<Entry<String, Object>> set = doc.entrySet();
		for (Iterator<Entry<String, Object>> it = set.iterator(); it.hasNext();) {
			Entry<String, Object> entry = it.next();
			JsonValue v = parseObject(entry.getValue());
			value.add(entry.getKey(), v);
		}
		
		return value;
	}
	
	private JsonValue parseArray(List<?> arr) {
		JsonArray value = new JsonArray();
		List<JsonValue> list = value.asList();
		
		for (ListIterator<?> it = arr.listIterator(); it.hasNext();) {
			Object object = it.next();
			list.add(parseObject(object));
		}
		
		return value;
	}

}
