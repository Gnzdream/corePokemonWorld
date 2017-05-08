package com.zdream.pmw.util.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
public class JsonValue implements Cloneable {
	
	public enum JsonType{
		ObjectType, ArrayType, StringType, ValueType, NullType
	}
	
	protected Object value;
	
	protected JsonType type;
	
	/**
	 * 是否强制不改变类型<br>
	 * 防止 type 在 ObjectType 与 StringType 中转换
	 */
	private boolean forceType;
	
	/**
	 * 利用类型创建 json 数据
	 * @param type
	 * @return
	 * @since v0.2.3
	 */
	public static JsonValue createJson(JsonType type) {
		switch (type) {
		case ObjectType:
			return new JsonObject();
		case ArrayType:
			return new JsonArray();
		default:
			return new JsonValue(type);
		}
	}
	
	/**
	 * 利用数据创建 json 数据, 支持创建 object 和 array 类型的数据
	 * @param obj
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static JsonValue createJson(Object obj) {
		JsonValue v;
		
		if (obj == null){
			v = new JsonValue(JsonType.NullType);
			return v;
		} else if (obj instanceof String){
			v = new JsonValue(JsonType.StringType);
			v.value = obj;
			return v;
		} else if (obj instanceof Enum){
			v = new JsonValue(JsonType.StringType);
			v.value = ((Enum<?>) obj).name();
			return v;
		} else if (obj instanceof JsonValue) {
			return createJson(((JsonValue) obj).value);
		} else if (obj instanceof Map) {
			v = new JsonObject();
			Map<String, JsonValue> map = (Map<String, JsonValue>) v.value;
			for (Iterator<Entry<?, ?>> it = ((Map)obj).entrySet().iterator(); it.hasNext();) {
				Entry<?, ?> entry = it.next();
				map.put(entry.getKey().toString(), createJson(entry.getValue()));
			}
			return v;
		} else if (obj instanceof List) {
			v = new JsonArray();
			List<JsonValue> list = (List<JsonValue>) v.value;
			for (Iterator it = ((List)obj).iterator(); it.hasNext();) {
				Object o = it.next();
				list.add(createJson(o));
			}
			return v;
		}
		
		// 兼容混乱代码
		v = new JsonValue(JsonType.ValueType);
		v.value = obj;
		return v;
	}
	
	/**
	 * 将其视为 JsonObject 数据
	 * @throws ClassCastException
	 *   当该 json 不是 ObjectType 类型时
	 * @since v0.2.3
	 */
	public JsonObject asObject() {
		if (type != JsonType.ObjectType) {
			throw new ClassCastException("type: " + this.type + " can not be case to jsonObject");
		}
		return (JsonObject) this;
	}
	
	/**
	 * 将其视为 JsonArray 数据
	 * @throws ClassCastException
	 *   当该 json 不是 ArrayType 类型时
	 * @since v0.2.3
	 */
	public JsonArray asArray() {
		if (type != JsonType.ArrayType) {
			throw new ClassCastException("type: " + this.type + " can not be case to jsonArray");
		}
		return (JsonArray) this;
	}
	
	@SuppressWarnings("rawtypes")
	private void set(Object value) {
		if (value == null){
			this.type = JsonType.NullType;
		} else if (value instanceof String){
			this.type = JsonType.StringType;
		} else if (value instanceof Enum){
			this.type = JsonType.StringType;
			this.value = ((Enum) value).name();
			return;
		} else if (value instanceof JsonValue) {
			JsonValue raw = (JsonValue) value;
			set(raw.getValue());
			return;
		} else if (value instanceof Map || value instanceof List) {
			throw new IllegalArgumentException("value:" + value + " can not be set as 'JsonValue'.");
		}
		
		this.value = value;
	}
	
	/**
	 * 指定 Json 类型
	 * @param type
	 */
	protected JsonValue(JsonType type) {
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
			set(value);
		} break;

		default:
			throw new IllegalArgumentException("value type: " + value.getClass().getName() + " is not allowed.");
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
	
	/**
	 * 该 json 数据是否为 null
	 * @return
	 * @since v0.2.3
	 */
	public boolean isNull() {
		return type == JsonType.NullType;
	}
	
	@Override
	public JsonValue clone() {
		try {
			return (JsonValue) super.clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}
	
	@Override
	public String toString() {
		return JsonBuilder.getDefaultInstance().getJson(this);
 	}
}
