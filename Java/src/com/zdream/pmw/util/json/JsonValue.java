package com.zdream.pmw.util.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 构造 JSON 结构的节点<br>
 * 与 JsonBuilder 可以共同使用
 * <b>v0.1.1</b><br>
 *   允许直接将 JsonValue 转成 String<br>
 *   即 getString 方法启用
 * 
 * @since v0.1
 * @author Zdream
 * @date 2016年3月16日
 * @version v0.1.1
 */
public class JsonValue {
	
	public enum JsonType{
		ObjectType, ArrayType, StringType, ValueType, NullType
	}
	
	private Object value;
	
	private JsonType type;
	
	/**
	 * 是否强制不改变类型<br>
	 * 防止 type 在 ObjectType 与 StringType 中转换
	 */
	private boolean forceType;
	
	/**
	 * 当该 JsonValue 的类型是 object 或 array 时，添加一个 key-value 对<br>
	 * 当该 JsonValue 的类型是 array 时，添加一个 value 
	 * @param key
	 *   当 JsonValue 的类型是 object 时，key 值不允许为空<br>
	 *   当 JsonValue 的类型是 array 时，key 值会被忽略；
	 * @param value
	 * @throws NullPointerException
	 *   当 (key == null && type == JsonType.ObjectType)
	 */
	public void add(String key, JsonValue value){
		switch (type) {
		case ObjectType:{
			getMap().put(key, value);
		} break;
		case ArrayType:{
			getArray().add(value);
		}
		default:
			throw new RuntimeException("type: " + type.name() + " is not allowed to add value");
		}
	}
	
	/**
	 * 添加多个 key-value 对<br>
	 * @param key
	 *   当 JsonValue 的类型是 object 时，key 长度等同于 values<br>
	 *     并且每个元素不为空
	 *   当 JsonValue 的类型是 array 时，key 值会被忽略；
	 * @param value
	 * @throws NullPointerException
	 *   当 (type == JsonType.ObjectType) 时，key 或 key 的任何一个元素为空
	 */
	public void addBatch(String[] keys, JsonValue[] values){
		switch (type) {
		case ObjectType: {
			assert(keys.length == values.length);
			Map<String, JsonValue> map = getMap();
			
			for (int i = 0; i < keys.length; i++) {
				map.put(keys[i], values[i]);
			}
		} break;
		
		case ArrayType: {
			// TODO
		} break;

		default:
			break;
		}
	}
	
	/**
	 * 添加多个 key-value 对<br>
	 * @param key
	 *   当 JsonValue 的类型是 object 时，key 的元素个数等同于 values<br>
	 *     并且每个元素不为空
	 *   当 JsonValue 的类型是 array 时，key 值会被忽略；
	 * @param value
	 * @throws NullPointerException
	 *   当 (type == JsonType.ObjectType) 时，key 或 key 的任何一个元素为空
	 */
	public void addBatch(String[] keys, List<JsonValue> values){
		switch (type) {
		case ObjectType: {
			assert(keys.length == values.size());
			Map<String, JsonValue> map = getMap();
			
			Iterator<JsonValue> it = values.iterator();
			for (int i = 0; i < keys.length; i++) {
				JsonValue value = it.next();
				map.put(keys[i], value);
			}
		} break;
		
		case ArrayType: {
			// TODO
		} break;

		default:
			break;
		}
	}
	
	/**
	 * 当该 JsonValue 的类型是 array 时，容器中添加一个 value <br>
	 * @param values
	 * @throws ClassCastException
	 *   当该 JsonValue 的类型不是 JsonValue.ArrayType
	 */
	public void add(JsonValue values){
		getArray().add(values);
	}
	
	/**
	 * 当该 JsonValue 的类型是 array 时，添加多个 value 元素<br>
	 * @param value
	 * @throws ClassCastException
	 *   当该 JsonValue 的类型不是 JsonValue.ArrayType
	 */
	public void addBatch(List<JsonValue> values){
		getArray().addAll(values);
	}
	
	/**
	 * 当该 JsonValue 的类型是 array 时，添加多个 value 元素<br>
	 * @param values
	 * @throws ClassCastException
	 *   当该 JsonValue 的类型不是 JsonValue.ArrayType
	 */
	public void addBatch(JsonValue[] values){
		List<JsonValue> list = getArray();
		for (int i = 0; i < values.length; i++) {
			list.add(values[i]);
		}
	}
	
	/**
	 * 默认的 Json 类型为 JsonType.ObjectType 
	 */
	public JsonValue() {
		this(JsonType.ValueType);
	}
	
	/**
	 * 默认的 Json 类型为 JsonType.ObjectType，参数为 value
	 * @param value
	 */
	@SuppressWarnings("rawtypes")
	public JsonValue(Object value) {
		this(JsonType.ValueType);
		
		if (value == null){
			this.type = JsonType.NullType;
		} else if (value instanceof String){
			this.type = JsonType.StringType;
		} else if (value instanceof Enum){
			this.type = JsonType.StringType;
			this.value = ((Enum) value).name();
			return;
		}
		
		this.value = value;
	}
	
	/**
	 * 指定 Json 类型
	 * @param type
	 */
	public JsonValue(JsonType type) {
		if (type == null){
			this.type = JsonType.NullType;
			return;
		}
		this.type = type;
		
		switch (type) {
		case ObjectType:
			value = new HashMap<String, JsonValue>();
			break;
		case ArrayType:
			value = new ArrayList<JsonValue>();
			break;
		default:
			break;
		}
	}
	
	@Override
	public String toString() {
		return ((value == null) ? "" : value.toString());
 	}
	
	@SuppressWarnings("unchecked")
	public Map<String, JsonValue> getMap(){
		assert(type == JsonType.ObjectType);
		return (Map<String, JsonValue>) this.value;
	}
	
	@SuppressWarnings("unchecked")
	public List<JsonValue> getArray(){
		assert(type == JsonType.ObjectType);
		return (List<JsonValue>) this.value;
	}
	
	public Object getValue() {
		return value;
	}
	
	public String getString() {
		assert(type == JsonType.StringType);
		return (String) this.value;
	}
	
	/**
	 * 设置参数的属性
	 * @param value
	 * @throws RuntimeException
	 *   当 JsonValue 的类型是 JsonValue.ArrayType 或 ObjectType 时
	 */
	public void setValue(Object value) {
		switch (type) {
		case StringType: case ValueType: case NullType:{
			this.value = value;
			if (!forceType){
				if (value == null){
					this.type = JsonType.NullType;
				} else if (value instanceof String){
					this.type = JsonType.StringType;
				} else {
					this.type = JsonType.ValueType;
				}
			}
		} break;

		default:
			throw new RuntimeException("value type: " + value.getClass().getName() + " is not allowed.");
		}
	}
	
	public JsonType getType() {
		return type;
	}
	
	public boolean isForceType() {
		return forceType;
	}
	
	public void setForceType(boolean forceType) {
		this.forceType = forceType;
	}
}
