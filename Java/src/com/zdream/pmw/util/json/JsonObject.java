package com.zdream.pmw.util.json;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * <p>Map 格式的 json 数据</p>
 * 
 * @since v0.2.3 [2017-04-20]
 * @author Zdream
 * @version v0.2.3 [2017-04-20]
 */
public class JsonObject extends JsonValue {
	
	@SuppressWarnings("unchecked")
	public JsonObject() {
		super(JsonType.ObjectType);
		map = (Map<String, JsonValue>) value;
	}
	
	Map<String, JsonValue> map;
	
	public Map<String, JsonValue> asMap() {
		return map;
	}
	
	/**
	 * 添加一个 key-value 对<br>
	 * @param key
	 *   key 值, 不允许为空<br>
	 * @param value
	 * @throws NullPointerException
	 *   当 (key == null)
	 */
	public void add(String key, JsonValue value){
		map.put(key, value);
	}
	
	/**
	 * 添加一个 key-value 对<br>
	 * 添加数据时, 会将 value 重新封装, 如果使用 JsonValue 作为参数, 将直接生成一个新的引用.
	 * 如果仅仅想将 JsonValue 作为引用放入, 则需要使用 {@link #add(String, JsonValue)} 方法.
	 * @param key
	 *   key 值, 不允许为空<br>
	 * @param value
	 * @throws NullPointerException
	 *   当 (key == null)
	 */
	public void put(String key, Object value){
		map.put(key, JsonValue.createJson(value));
	}

	/**
	 * 将 o 中的键值对全部添加到 value 中.<br>
	 * 如果 key 值重复, 将覆盖掉原来的数据
	 * @param o
	 *   待添加的数据
	 */
	public void putAll(JsonObject o){
		map.putAll(o.map);
	}
	
	public void remove(String key) {
		map.remove(key);
	}
	
	public void clear() {
		map.clear();
	}
	
	/**
	 * <p>得到 map 中的值
	 * <p>如果子元素是 JsonArray 或者 JsonObject, 则返回 JsonArray 或者 JsonObject
	 * 而不是 List 和 Map
	 * @param key
	 * @return
	 */
	public Object get(String key) {
		JsonValue c = map.get(key);
		if (c == null) {
			return null;
		}
		
		switch (c.getType()) {
		case ObjectType:
		case ArrayType:
			return c;

		default:
			return c.getValue();
		}
	}
	
	public Object getOrDefault(String key, Object def) {
		Object result = get(key);
		if (result == null) {
			return def;
		} else {
			return result;
		}
	}
	
	public JsonValue getJsonValue(String key) {
		return map.get(key);
	}
	
	/**
	 * 添加多个 key-value 对<br>
	 * @param keys
	 *   keys 长度需要等同于 values, 并且每个元素不为空
	 * @param values
	 * @throws NullPointerException
	 *   当 key 或 key 的任何一个元素为空
	 */
	public void addBatch(String[] keys, JsonValue[] values){
		for (int i = 0; i < keys.length; i++) {
			map.put(keys[i], values[i]);
		}
	}
	
	/**
	 * 添加多个 key-value 对<br>
	 * @param key
	 *   key 的元素个数需要等同于 values, 并且每个元素不为空
	 * @param value
	 * @throws NullPointerException
	 *   当 key 或 key 的任何一个元素为空
	 */
	public void addBatch(String[] keys, List<JsonValue> values){
		Iterator<JsonValue> it = values.iterator();
		for (int i = 0; i < keys.length; i++) {
			JsonValue value = it.next();
			map.put(keys[i], value);
		}
	}
	
	@Override
	public JsonObject clone() {
		JsonObject o = new JsonObject();
		Map<String, JsonValue> r = o.map;
		
		for (Iterator<Entry<String, JsonValue>> it = map.entrySet().iterator(); it.hasNext();) {
			Entry<String, JsonValue> entry = it.next();
			JsonValue v = entry.getValue();
			if (v == null) {
				r.put(entry.getKey(), null);
			} else {
				r.put(entry.getKey(), v.clone());
			}
		}
		return o;
	}
	
}
