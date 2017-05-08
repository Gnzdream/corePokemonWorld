package com.zdream.pmw.util.json;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.zdream.pmw.util.json.JsonValue.JsonType;

/**
 * JSON 字符串的拼接、转换通用工具<br>
 * <br>
 * <b>v0.1.2 (2016-09-06)</b><br>
 *   解析负数时出错的 BUG 修复<br>
 * <br>
 * <b>v0.2.2</b><br>
 *   完成对解析字符串的重构, 有更大的容错性<br>
 * 
 * @since v0.1
 * @author Zdream
 * @date 2016年3月16日
 * @version v0.2.2
 */
public class JsonBuilder {
	
	/**
	 * 最顶层的组织结构 是否是数组<br>
	 * 即最外层是否是 [] （数组）<br>
	 * 在开始时确定<br>
	 * 默认为 false
	 */
	private boolean rootArray = false;
	
	/**
	 * 新添加的类
	 */
	private JsonValue values;
	
	/**
	 * 普通类型的前括号
	 */
	private static final char LEFT_BRACKET = '{';
	/**
	 * 普通类型的后括号
	 */
	private static final char RIGHT_BRACKET = '}';
	/**
	 * 数组类型的前括号
	 */
	private static final char LEFT_ARRAY = '[';
	/**
	 * 数组类型的后括号
	 */
	private static final char RIGHT_ARRAY = ']';
	/**
	 * 引号
	 */
	private static final char QUOTATION = '\"';
	/**
	 * 冒号
	 */
	private static final char COLON = ':';
	/**
	 * 逗号
	 */
	private static final char DELIMITER = ',';
	/**
	 * 空标识
	 */
	private static final String NULL = "null";
	
	private static JsonBuilder instance = new JsonBuilder();
	
	/**
	 * 获得全局唯一实例
	 * @return
	 * @since v0.2.3
	 */
	public static JsonBuilder getDefaultInstance() {
		return instance;
	}
	
	public boolean isRootArray() {
		return rootArray;
	}

	/**
	 * 创建一个用于建造 Json 语句的工具<br>
	 *   根组织结构为映射 (map)
	 */
	public JsonBuilder() {
		this(false);
	}
	
	/**
	 * 创建一个用于建造 Json 语句的工具，并指定根组织结构
	 * @param rootArray
	 *   根组织结构是否为数组<br>
	 *   为 true 时，创造一个根组织结构为数组 (array) 的 Json 对象<br>
	 *   否则，创造一个根组织结构为映射 (map) 的 Json 对象
	 */
	public JsonBuilder(boolean rootArray) {
		super();
		this.rootArray = rootArray;
		if (rootArray){
			values = new JsonArray();
		} else {
			values = new JsonObject();
		}
	}

	/**
	 * 添加 key-value 对<br>
	 * 仅当根组织结果不为数组时使用
	 * @param key
	 *   键值
	 * @param value
	 *   String 类型的值
	 */
	public JsonBuilder addEntry(String key, String value){
		values.asObject().add(key, JsonValue.createJson(value));
		return this;
	}

	/**
	 * 添加 key-value 对<br>
	 * 仅当根组织结果不为数组时使用
	 * @since v0.1.2
	 * @param key
	 *   键值
	 * @param value
	 *   JsonValue 类型<br>
	 *   可用 convertFromObject 方法的结果作为此输入参数
	 */
	public JsonBuilder addEntry(String key, JsonValue value){
		values.asObject().add(key, value);
		return this;
	}
	
	/**
	 * 添加 key-value 对<br>
	 * 仅当根组织结果不为数组时使用
	 * @param key
	 *   键值
	 * @param value
	 *   键对应的值所封装的 Object<br>
	 * @param fields
	 *   所要获取的这个封装类的属性，必须有 public 的 get 方法
	 */
	public JsonBuilder addEntry(String key, Object obj){
		this.values.asObject().add(key, JsonValue.createJson(obj));
		return this;
	}
	
	/**
	 * 添加 key-value 对<br>
	 * 对一个 POJO 类，指定其对应的属性来构造 Json 语句<br>
	 * 并存储在构造器内部
	 * @param key
	 *   键值
	 * @param value
	 *   键对应的值所封装的 Object<br>
	 * @param fields
	 *   所要获取的这个封装类的属性，必须有 public 的 get 方法
	 */
	public JsonBuilder addEntry(String key, Object obj, String ... fields){
		JsonValue value = createObjectValue(obj, fields);
		this.values.asObject().add(key, value);
		
		return this;
	}
	
	/**
	 * 添加 value 值<br>
	 * 仅当根组织结果为数组时使用，且仅能使用一次
	 * @param obj
	 *   对象数组
	 * @param fields
	 *   所要获取的这些封装类的共同属性，必须有 public 的 get 方法
	 */
	public JsonBuilder addBatchEntry(String[] key, Object[] obj, String ... fields){
		JsonObject root = this.values.asObject();
		final int length = key.length;
		for (int i = 0; i < length; i++) {
			Object value = obj[i];
			JsonValue value0 = createObjectValue(value, fields);
			root.add(key[i], value0);
		}
		
		return this;
	}
	
	/**
	 * 添加 value 值<br>
	 * 仅当根组织结果为数组时使用，且仅能使用一次
	 * @param objs
	 *   对象数组
	 * @param fields
	 *   所要获取的这些封装类的共同属性，必须有 public 的 get 方法
	 */
	public JsonBuilder addBatchEntry(String[] key, Collection<?> objs, String ... fields){
		JsonObject root = this.values.asObject();
		final int length = key.length;
		Iterator<?> it = objs.iterator();
		for (int i = 0; i < length; i++) {
			Object value = it.next();
			JsonValue value0 = createObjectValue(value, fields);
			root.add(key[i], value0);
		}
		return this;
	}
	
	/**
	 * 指定一个对象的成员变量，将该对象转成对应的 json 语句
	 * @param obj
	 *   指定的对象
	 * @param fields
	 *   该对象的成员变量
	 * @return
	 *   返回对应的 json 语句<br>
	 *   该结果可以作为 addEntry 方法的输入参数 value<br>
	 */
	public JsonValue convertFromObject(Object obj, String ... fields){
		JsonValue value = createObjectValue(obj, fields);
		return value;
	}
	
	/**
	 * 指定一个对象的成员变量，将该对象组成的数组转成对应的 JsonValue[]<br>
	 * @param objs
	 *   指定的对象组成的数组
	 * @param fields
	 *   该对象的成员变量
	 * @return
	 *   返回对应的 JsonValue 变量数组<br>
	 *   该结果可以作为 addEntry 方法的输入参数 value<br>
	 */
	public JsonValue[] batchConvertFromObject(Object[] objs, String ... fields){
		JsonValue[] values = new JsonValue[objs.length];
		for (int i = 0; i < values.length; i++) {
			values[i] = this.createObjectValue(objs[i], fields);
		}
		return values;
	}
	
	/**
	 * 指定一个对象的成员变量，将该对象组成的集合转成对应的 JsonValue[]<br>
	 * @param objs
	 *   指定的对象组成的集合
	 * @param fields
	 *   该对象的成员变量
	 * @return
	 *   返回对应的 JsonValue 变量数组<br>
	 *   该结果可以作为 addEntry 方法的输入参数 value<br>
	 */
	public JsonValue[] batchConvertFromCollection(Collection<?> objs, String ... fields){
		JsonValue[] values = new JsonValue[objs.size()];
		Iterator<?> it = objs.iterator();
		for (int i = 0; i < values.length; i++) {
			Object obj = it.next();
			values[i] = this.createObjectValue(obj, fields);
		}
		return values;
	}
	
	/**
	 * 向 builder 中写入 JsonValue 对应的值
	 * @since v0.1.2
	 * @param obj
	 * @param builder
	 */
	private void writeObject(JsonValue obj, StringBuilder builder){
		builder.append(LEFT_BRACKET);
		
		Map<String, JsonValue> map = obj.asObject().asMap();
		Set<String> set = map.keySet();
		
		int borden = set.size() - 1;
		Iterator<String> it = set.iterator();
		
		for (int i = 0; i < borden; i++) {
			String key = it.next();
			writePair(key, map.get(key), builder);
			builder.append(DELIMITER);
		}
		if (borden >= 0){
			String key = it.next();
			writePair(key, map.get(key), builder);
		}
		builder.append(RIGHT_BRACKET);
	}
	
	/**
	 * 将一个 Object 组成的数组写入 StringBuilder 中<br>
	 * 例如：[v1,v2,...]
	 * @param obj
	 * @param builder
	 */
	private void writeArray(Object[] obj, StringBuilder builder){
		builder.append(LEFT_ARRAY);
		int length = obj.length - 1;
		for (int i = 0; i < length; i++) {
			writeValue(obj[i], builder);
			builder.append(DELIMITER);
		}
		if (length >= 0){
			writeValue(obj[length], builder);
		}
		builder.append(RIGHT_ARRAY);
	}
	
	/**
	 * 将一个 List 类型的 JsonValue 写入 StringBuilder 中<br>
	 * 写入格式：[v1,v2,...]
	 * @since v0.1.2
	 * @param obj
	 * @param builder
	 */
	private void writeArray(JsonValue obj, StringBuilder builder){
		builder.append(LEFT_ARRAY);
		
		List<JsonValue> list = obj.asArray().asList();
		int borden = list.size() - 1;
		Iterator<JsonValue> it = list.iterator();
		
		for (int i = 0; i < borden; i++) {
			writeJsonValue(it.next(), builder);
			builder.append(DELIMITER);
		}
		if (borden >= 0){
			writeJsonValue(it.next(), builder);
		}
		
		builder.append(RIGHT_ARRAY);
	}
	
	/**
	 * 将一个 Collection 写入 StringBuilder 中<br>
	 * 写入格式：[v1,v2,...]
	 * @param collection
	 * @param builder
	 */
	private void writeCollection(Collection<?> collection, StringBuilder builder){
		builder.append(LEFT_ARRAY);
		int length = collection.size() - 1;
		int i = 0;
		Iterator<?> it = collection.iterator();
		for (; i < length; i++) {
			writeValue(it.next(), builder);
			builder.append(DELIMITER);
		}
		if (length >= 0){
			writeValue(it.next(), builder);
		}
		builder.append(RIGHT_ARRAY);
	}
	
	/**
	 * 向 builder 中写入一个键值对
	 * @since v0.1.2
	 * @param key
	 * @param value
	 * @param builder
	 */
	private void writePair(String key, JsonValue value, StringBuilder builder) {
		writeString(key, builder);
		builder.append(COLON);
		writeJsonValue(value, builder);
	}
	
	/**
	 * 根据 obj 的类型，选择写入的值的类型<br>
	 * @since v0.1.2
	 * @param obj
	 * @param builder
	 */
	private void writeJsonValue(JsonValue obj, StringBuilder builder) {
		switch (obj.getType()) {
		case StringType:
			writeString(obj, builder);
			break;
		case ValueType:
			writeValue(obj, builder);
			break;
		case ObjectType:
			writeObject(obj, builder);
			break;
		case ArrayType:
			writeArray(obj, builder);
			break;
		case NullType:
			writeNull(builder);
			break;
		default:
			break;
		}
	}
	
	/**
	 * 将一个对象的一个成员变量转成 json 的形式，写入到 StringBuilder 中<br>
	 * 在内部检测它的类型
	 * @param obj
	 * @param builder
	 */
	private void writeValue(Object obj, StringBuilder builder){
		if (obj == null){
			return;
		}
		if (obj instanceof String){
			writeString((String) obj, builder);
		} else if (obj instanceof Object[]){
			writeArray((Object[]) obj, builder);
		} else if (obj instanceof Collection<?>){
			writeCollection((Collection<?>) obj, builder);
		} else { // 基础类型（假定）
			builder.append(obj.toString());
		}
	}
	
	/**
	 * 将数值（非字符串类型）写入到 StringBuilder 中<br>
	 * 一般写入值为该类的 toString 方法返回值
	 * @since v0.1.2
	 * @param obj
	 * @param builder
	 */
	private void writeValue(JsonValue obj, StringBuilder builder){
		builder.append(obj.getValue().toString());
	}
	
	/**
	 * 将一个 String 类型的对象，写入到 StringBuilder 中<br>
	 * @param value
	 * @param builder
	 */
	private void writeString(String value, StringBuilder builder){
		builder.append(QUOTATION);
		checkValue(value, builder);
		builder.append(QUOTATION);
	}
	
	/**
	 * 将一个 String 类型的对象，写入到 StringBuilder 中<br>
	 * @since v0.1.2
	 * @param str
	 *   封装 String 的 JsonValue
	 * @param builder
	 */
	private void writeString(JsonValue str, StringBuilder builder){
		builder.append(QUOTATION);
		checkValue((String) str.getValue(), builder);
		builder.append(QUOTATION);
	}
	
	/**
	 * 将 null 写入到 StringBuilder 中<br>
	 * @param builder
	 */
	private void writeNull(StringBuilder builder){
		builder.append(NULL);
	}
	
	/**
	 * 对值进行写入检查
	 * @param value
	 * @param builder
	 */
	private void checkValue(String value, StringBuilder builder){
		// TODO check the value
		builder.append(value);
	}
	
	/**
	 * 根据 POJO 对象和指定属性，将单个 obj 转化成 JsonValue 的集合
	 * @param obj
	 * @param fields
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private JsonValue createObjectValue(Object obj, String[] fields) {
		JsonObject mother = new JsonObject();
		if (fields == null || fields.length == 0){
			return mother;
		}
		
		Class cl = obj.getClass();
		for (int i = 0; i < fields.length; i++) {
			String field = fields[i];
			try {
				String prefix;
				Class type = cl.getDeclaredField(field).getType();
				if (type == Boolean.class){
					prefix = "is";
				} else {
					prefix = "get";
				}
				String behind = Character.toUpperCase(field.charAt(0)) + field.substring(1);
				String methodName = prefix + behind;
				
				Method method = cl.getDeclaredMethod(methodName);
				Object result = method.invoke(obj);
				
				mother.add(field, JsonValue.createJson(result));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return mother;
	}
	
	/**
	 * 获得 Json 格式的字符串
	 * @return
	 */
	public String getJson(){
		StringBuilder builder = new StringBuilder();
		writeJsonValue(values, builder);
		return builder.toString();
	}
	
	/**
	 * 将指定的 JsonValue 转化成 Json 格式的字符串
	 * @since v0.2
	 * @param value
	 * @return
	 */
	public String getJson(JsonValue value){
		StringBuilder builder = new StringBuilder();
		writeJsonValue(value, builder);
		return builder.toString();
	}
	
	/**
	 * 将指定的字符串转成 Json.<br>
	 * 该函数是该族函数的入口函数
	 * @since v0.2
	 * @param str
	 *   需要转化的字符串
	 * @return
	 *   转化后的 JsonValue 数据
	 */
	public JsonValue parseJson(String str){
		data = new JsonData();
		data.index = 0;
		data.str = str;
		JsonValue value = null;
		
		int ch = nextChar();
		switch (getNextType(ch, data.index)) {
		case ArrayType:
			value = this.parseArray();
			break;
		case ObjectType:
			value = this.parseObject();
			break;
		default:
			break;
		}
		
		data = null;
		return value;
	}
	
	/**
	 * 测试下一个元素的类型，并将指针 data.index 移到数据的首个字符<br>
	 * 
	 * @param ch
	 *   需要判定的字符
	 * @param index
	 *   取到该字符时的位置
	 * @return
	 *   下一个元素的类型
	 * @since v0.2.2
	 */
	private JsonValue.JsonType getNextType(int ch, int index) {
		if (ch <= '9' && ch >= '0' || ch == '.') {
			return JsonType.ValueType;
		}
		
		switch (ch) {
		case '[':
			return JsonType.ArrayType;
		case '{':
			return JsonType.ObjectType;
		case '\"':
			return JsonType.StringType;
		case 't':{
			if (data.str.indexOf("true", index) == index){
				return JsonType.ValueType;
			}
		} break;
		case 'f':{
			if (data.str.indexOf("false", index) == index){
				return JsonType.ValueType;
			}
		} break;
		case 'n':{
			if (data.str.indexOf("null", index) == index){
				return JsonType.ValueType;
			}
		} break;
		case '-': case '+':
			return JsonType.ValueType;
		}
		
		if (ch <= '9' && ch >= '0' || ch == '.' || ch == '.') {
			return JsonType.ValueType;
		}
		
		System.err.println("can not handle with: '" + (char) ch + "' at index: " + index);
		return null;
	}
	
	/**
	 * 按照数组的方式解析字符串
	 * @since v0.2
	 * @return
	 *   根组织形式为数组的 JsonValue
	 */
	private JsonArray parseArray() {
		// 此时 data.index 指向 '['
		data.index++;
		
		JsonArray value = new JsonArray();
		final int length = data.str.length();
		
		for (; data.index < length;) {
			int ch = nextChar();
			if (ch == ']') {
				data.index++;
				return value;
			}
			
			switch (getNextType(ch, data.index)) {
			case ArrayType:
				value.add(this.parseArray());
				break;
			case ObjectType:
				value.add(this.parseObject());
				break;
			case StringType:
				value.add(JsonValue.createJson(this.parseString()));
				break;
			case ValueType: case NullType:
				value.add(this.parseValue());
				break;
			default:
				break;
			}
			
			ch = nextChar();
			if (ch == ']'){
				data.index ++;
				return value;
			} else if (ch != ','){
				System.err.println("can not found \',\' in a array!");
			}
			data.index ++;
		}

		return value;
	}

	/**
	 * 按照映射的方式解析字符串
	 * @since v0.2
	 * @return
	 *   根组织形式为映射的 JsonValue
	 */
	private JsonObject parseObject() {
		// 此时 data.index 指向 '{'
		data.index++;
		
		JsonObject obj = new JsonObject();
		
		String str = data.str;
		final int length = str.length();
		String key;
		
		for (; data.index < length;) {
			int ch = nextChar();
			if (ch == '}'){
				data.index++;
				return obj;
			}
			
			switch (getNextType(ch, data.index)) {
			case StringType:
				key = parseString();
				break;
			default:
				key = null;
				System.err.println("key is null!");
				break;
			}
			
			// 寻找并跳过为 ':' 的字符的位置
			ch = nextChar();
			data.index ++;
			if (ch == '}'){
				data.index ++;
				System.err.println("can not found value!");
				return obj;
			} else if (ch != ':'){
				System.err.println("can not found \':\' in a object!");
			}
			
			ch = nextChar();
			switch (getNextType(ch, data.index)) {
			case StringType:
				obj.add(key, JsonValue.createJson(parseString()));
				break;
			case ArrayType:
				obj.add(key, parseArray());
				break;
			case ObjectType:
				obj.add(key, parseObject());
				break;
			case ValueType: case NullType:
				obj.add(key, parseValue());
				break;
			default:
				break;
			}

			ch = nextChar();
			if (ch == '}'){
				data.index ++;
				return obj;
			} else if (ch != ','){
				System.err.println("can not found \',\' in a object!");
			}
			data.index ++;
		}
		return obj;
	}

	/**
	 * 按照字符串的方式解析字符串
	 * @since v0.2
	 * @return
	 *   字符串 JsonValue
	 */
	private String parseString() {
		// 此时 data.index 指向第一个 '"'
		data.index++;
		
		String str = data.str;
		int length = str.length();
		StringBuilder builder = new StringBuilder();
		
		FIND:
		for (; data.index < length; data.index++) {
			char ch = str.charAt(data.index);
			if (ch == '\\') {
				data.index++;
				builder.append(str.charAt(data.index));
				data.index++;
			} else {
				if (ch == '\"') {
					data.index++;
					break FIND;
				} else {
					builder.append(str.charAt(data.index));
				}
			}
		}
		
		return builder.toString();
	}

	/**
	 * 按照数据的方式解析字符串<br>
	 * 数据类型可能是整数、浮点数、布尔值和空<br>
	 * 
	 * @since v0.2
	 * @return
	 *   数据或空的 JsonValue
	 */
	private JsonValue parseValue() {
		/*
		 * 整数、小数、负数  以数字、'.'、'-' 开头 flag=1
		 * 布尔值            以't' 'f' 开头 flag=2
		 * 空                以'n' 开头 flag=3
		 */
		JsonValue value = null;
		int ch = nextChar();
		int flag = 0;
		
		if (Character.isDigit(ch) || ch == '.' || ch == '-'){
			flag = 1;
		} else if (ch == 't' || ch == 'f') {
			flag = 2;
		} else if (ch == 'n') {
			flag = 3;
		}
		
		// 寻找边界
		int rindex = data.index;
		String str = data.str;
		int length = str.length();
		for (rindex++; rindex < length; rindex++) {
			ch = str.charAt(rindex);
			if (!Character.isLetterOrDigit(ch)) {
				if (ch == '.' || ch == '-') {
					continue;
				}
				break;
			}
		}
		String mstr = str.substring(data.index, rindex);
		data.index = rindex;
		
		try {
			switch (flag) {
			case 1:
				if (mstr.contains(".")) {
					value = JsonValue.createJson(Double.parseDouble(mstr));
				} else {
					value = JsonValue.createJson(Integer.parseInt(mstr));
				}
				break;
				
			case 2:
				value = JsonValue.createJson(Boolean.parseBoolean(mstr));
				break;
				
			case 3:
				value = new JsonValue(JsonType.NullType);

			default:
				break;
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		
		return value;
	}
	
	/**
	 * 返回下一个非空字符
	 * @since v0.2
	 * @return
	 *   下一个非空字符
	 *   -1 为没找到
	 */
	private int nextChar() {
		String str = data.str;
		int length = str.length();
		for (; data.index < length; data.index++) {
			if (Character.isWhitespace(str.charAt(data.index))) {
				continue;
			} else {
				return str.charAt(data.index);
			}
		}
		return -1;
	}
	
	/**
	 * 解析字符串的时候存储的数据单元
	 * @since v0.2
	 */
	private class JsonData {
		int index;
		String str;
	}
	private JsonData data;
}
