package com.zdream.pmw.util.json;

import java.util.Iterator;
import java.util.List;

/**
 * <p>Array 格式的 json 数据</p>
 * 
 * @since v0.2.3 [2017-04-20]
 * @author Zdream
 * @version v0.2.3 [2017-04-20]
 */
public class JsonArray extends JsonValue {
	
	@SuppressWarnings("unchecked")
	public JsonArray() {
		super(JsonType.ArrayType);
		list = (List<JsonValue>) value;
	}
	
	public JsonArray(JsonValue[] vs) {
		this();
		
		for (int i = 0; i < vs.length; i++) {
			list.add(vs[i].clone());
		}
	}
	
	List<JsonValue> list;
	
	public List<JsonValue> asList() {
		return list;
	}
	
	/**
	 * 在该容器中添加一个 value <br>
	 * @param value
	 */
	public void add(JsonValue value){
		list.add(value);
	}
	
	/**
	 * 在该容器中添加一个 value<br>
	 * 添加数据时, 会将 value 重新封装, 如果使用 JsonValue 作为参数, 将直接生成一个新的引用.
	 * 如果仅仅想将 JsonValue 作为引用放入, 则需要使用 {@link #add(JsonValue)} 方法.
	 * @param o
	 */
	public void put(Object o){
		list.add(JsonValue.createJson(o));
	}
	
	/**
	 * 在该容器中添加多个 value 元素<br>
	 * @param values
	 */
	public void addBatch(List<JsonValue> values){
		list.addAll(values);
	}
	
	/**
	 * 在该容器中添加多个 value 元素<br>
	 * @param values
	 */
	public void addBatch(JsonValue[] values){
		for (int i = 0; i < values.length; i++) {
			list.add(values[i]);
		}
	}
	
	/**
	 * <p>得到 list 中的值
	 * <p>如果子元素是 JsonArray 或者 JsonObject, 则返回 JsonArray 或者 JsonObject
	 * 而不是 List 和 Map
	 * @param idx
	 *   索引
	 * @return
	 */
	public Object get(int idx) {
		JsonValue c = list.get(idx);
		switch (c.getType()) {
		case ObjectType:
		case ArrayType:
			return c;

		default:
			return c.getValue();
		}
	}
	
	public Object remove(int idx) {
		JsonValue c = list.remove(idx);
		switch (c.getType()) {
		case ObjectType:
		case ArrayType:
			return c;

		default:
			return c.getValue();
		}
	}
	
	public void clear() {
		list.clear();
	}
	
	public JsonValue getJsonValue(int idx) {
		return list.get(idx);
	}
	
	public int size() {
		return list.size();
	}
	
	@Override
	public JsonArray clone() {
		JsonArray o = new JsonArray();
		List<JsonValue> r = o.list;
		
		for (Iterator<JsonValue> it = list.iterator(); it.hasNext();) {
			JsonValue v = it.next();
			if (v == null) {
				r.add(null);
			} else {
				r.add(v.clone());
			}
		}
		return o;
	}

}
