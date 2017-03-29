package com.zdream.pmw.platform.translate.template;

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;

import com.zdream.pmw.platform.prototype.BattlePlatform;
import com.zdream.pmw.util.io.AssetReader;
import com.zdream.pmw.util.json.JsonBuilder;
import com.zdream.pmw.util.json.JsonValue;
import com.zdream.pmw.util.json.JsonValue.JsonType;

/**
 * 模板选择<br>
 * 
 * @since v0.2.1
 * @author Zdream
 * @date 2017年3月23日
 * @version v0.2.1
 */
public class DefaultTemplateChooser implements ITemplateChooser {
	
	/* ************
	 *	  属性    *
	 ************ */
	
	JsonValue rules;
	
	/* ************
	 *	实现方法  *
	 ************ */
	
	@Override
	public String[] canHandle() {
		return null;
	}
	
	public String[] choose(String head, Map<String, String> context, BattlePlatform pf) {
		// 预处理部分
		preHandle(context);
		
		// 选择部分
		JsonValue temp = chooseTemplateEntry(head, context);
		
		if (temp == null) {
			return null;
		} else {
			return new String[]{temp.getMap().get("id").getString()};
		}
	}
	
	/* ************
	 *	私有方法  *
	 ************ */
	
	private void preHandle(Map<String, String> context) {
		if (context.containsKey("pm.team") && context.containsKey("/team")) {
			String t = context.get("pm.team"), rt = context.get("/team");
			if (t.equals(rt)) {
				context.put("/self", Boolean.toString(true));
			} else {
				context.put("/enemy", Boolean.toString(true));
			}
		}
	}
	
	private JsonValue chooseTemplateEntry(String head, Map<String, String> context) {
		JsonValue rawTemplate = rules.getMap().get(head);
		if (rawTemplate == null) {
			return null;
		}
		
		List<JsonValue> list = rawTemplate.getArray();
		for (ListIterator<JsonValue> it = list.listIterator(); it.hasNext();) {
			JsonValue jv = it.next();
			if (judgeTemplate(jv, context)) {
				return jv;
			}
		}
		
		return null;
	}
	
	private boolean judgeTemplate(JsonValue jv, Map<String, String> context) {
		if (jv.getType() != JsonType.ObjectType) {
			return false;
		}
		
		Map<String, JsonValue> map = jv.getMap();
		for (Iterator<Entry<String, JsonValue>> it = map.entrySet().iterator(); it.hasNext();) {
			Entry<String, JsonValue> entry = it.next();
			String key = entry.getKey();
			if (key.equals("id")) {
				continue;
			}
			
			// 待匹配
			String s = context.get(key);
			if (s == null) {
				return false;
			}
			
			JsonValue value = entry.getValue();
			String s2;
			switch (value.getType()) {
			case StringType:
				s2 = value.getString();
				break;
			default:
				s2 = value.getValue().toString();
				break;
			}
			
			if (!s.equals(s2)) {
				return false;
			}
		}
		
		return true;
	}
	
	/* ************
	 *	 初始化   *
	 ************ */
	
	public DefaultTemplateChooser() {
		JsonBuilder builder = new JsonBuilder();
		String m = AssetReader.read("data/static/translate-chooser.json", 2000).toString();
		rules = builder.parseJson(m);
	}

}
