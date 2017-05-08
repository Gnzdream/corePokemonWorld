package com.zdream.pmw.platform.translate.parse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zdream.pmw.util.json.JsonArray;
import com.zdream.pmw.util.json.JsonObject;
import com.zdream.pmw.util.json.JsonValue;
import com.zdream.pmw.util.json.JsonValue.JsonType;

/**
 * 默认的解读器<br>
 * 
 * @since v0.2.1
 * @author Zdream
 * @date 2017年3月22日
 * @version v0.2.1
 */
public class DefaultParser {
	
	String[] msg;
	int msgIdx;
	
	JsonObject info;
	JsonValue now;
	ArrayList<JsonValue> searchs;
	
	Map<String, String> context;

	public Map<String, String> parse(String[] msg, JsonArray dict) {
		initContext();
		msgIdx = 1;
		this.msg = msg;
		searchs = new ArrayList<>();
		
		now = dict;
		if (dict == null) {
			return context;
		}
		
		try {
			parseNext();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return context;
	}
	
	private void initContext() {
		context = new HashMap<>();
		context.put("/mute", Boolean.toString(false));
	}
	
	private String nextMsg() {
		if (msgIdx == msg.length) {
			return null;
		}
		return msg[msgIdx++];
	}
	
	private void parseNext() {
		switch (now.getType()) {
		case ArrayType:
			parseArray();
			break;
			
		case StringType:
			parseString();
			break;
			
		case ObjectType:
			parseObject();
			break;

		default:
			break;
		}
	}

	private void parseArray() {
		parseArray(0);
	}


	private void parseArray(int offset) {
		// now.type = array
		List<JsonValue> list = now.asArray().asList();
		int length = list.size();
		for (int i = offset; i < length; i++) {
			JsonValue v = list.get(i);
			now = v;
			searchs.add(v);
			parseNext();
			
			if (info != null) {
				Map<String, JsonValue> map = now.asObject().asMap();
				String cmd = map.get("/mean").getString();
				switch (cmd) {
				case "loop": {
					int value = (int) map.get("/value").getValue();
					handleLoop(list, i - value, value);
				} break;
				}
				info = null;
			}
		}
	}
	
	private void parseString() {
		String meaning = now.getString();
		context.put(meaning, nextMsg());
	}

	private void parseObject() {
		Map<String, JsonValue> map = now.asObject().asMap();
		String cmd = map.get("/mean").getString();
		switch (cmd) {
		case "set":
			context.put(map.get("/key").getString(), map.get("/value").getString());
			break;
		case "option":
			judgeOption();
			break;
		case "loop":
			info = now.asObject();
			break;
		case "switch":
			judgeSwitch();
			break;
		default:
			break;
		}
		
	}
	
	private void judgeOption() {
		Map<String, JsonValue> map = now.asObject().asMap();
		String ik = null;
		String msg = nextMsg(); // msg 可能为空
		for (int i = 0;; i++) {
			ik = Integer.toString(i);
			JsonValue v = map.get(ik);
			if (v == null || v.getType() == JsonType.NullType) {
				return; // 没有匹配
			}
			if (msg == null) { // v != null
				continue;
			}
			
			JsonValue judgev = v.asArray().asList().get(0);
			if (judgev.getType() == JsonType.NullType) {
				if (msgIdx == msg.length()) {
					// 匹配成功
					break;
				} else {
					continue;
				}
			}
			
			String judge = judgev.getString();
			if (judge.startsWith("'")) {
				if (judge.substring(1).equals(msg)) {
					// 匹配成功
					break;
				} else {
					continue;
				}
			} else {
				// 匹配成功
				context.put(judge, msg);
				break;
			}
		}
		
		now = map.get(ik);
		parseArray(1);
	}
	
	private void handleLoop(List<JsonValue> list, int curIdx, int between) {
		String msg;
		int idx = 0;
		while ((msg = nextMsg()) != null) {
			String k = list.get(curIdx + idx).getString();
			
			String v = context.get(k);
			context.put(k, v + "," + msg);
			idx++;
			if (idx >= between) {
				idx = 0;
			}
		}
	}
	
	private void judgeSwitch() {
		Map<String, JsonValue> m = now.asObject().asMap();
		// 处理部分
		Map<String, JsonValue> map = new HashMap<>();
		for (int i = 0; ; i++) {
			String ik = Integer.toString(i);
			if (!m.containsKey(ik)) {
				break;
			}
			
			JsonValue v = m.get(ik);
			String k = v.asArray().asList().get(0).getString();
			if (k.startsWith("'")) {
				k = k.substring(1);
			}
			map.put(k, v);
		}
		
		// 选择
		String next;
		while ((next = nextMsg()) != null) {
			JsonValue v = map.get(next);
			
			if (v != null) {
				searchs.add(v);
				now = v;
				parseArray(1);
			}
		}
	}

}
