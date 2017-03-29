package com.zdream.pmw.platform.translate;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;

import com.zdream.pmw.platform.translate.template.DefaultTemplateChooser;
import com.zdream.pmw.platform.translate.template.ITemplateChooser;
import com.zdream.pmw.platform.translate.translater.ITranslate;
import com.zdream.pmw.util.io.AssetReader;
import com.zdream.pmw.util.json.JsonBuilder;
import com.zdream.pmw.util.json.JsonValue;

/**
 * 消息翻译仓库<br>
 * <p>存储所有的翻译数据、消息含义</p>
 * 
 * @since v0.2.1
 * @author Zdream
 * @date 2017年3月21日
 * @version v0.2.1
 */
public class MessageRepository {
	
	/*
	 * Dictionaries
	 */
	JsonValue dicts;
	
	/*
	 * translate class
	 */
	Map<String, ITranslate> trm = new HashMap<>();
	
	/*
	 * template chooser class
	 */
	Map<String, ITemplateChooser> chs = new HashMap<>();
	
	/*
	 * templates
	 */
	Map<String, String> templates = new HashMap<>();
	
	/*
	 * default template chooser
	 */
	DefaultTemplateChooser dch;
	
	public JsonValue getDictionary(String head) {
		Map<String, JsonValue> v = dicts.getMap();
		return v.get(head);
	}
	
	/**
	 * 获得模板文本
	 * @param tempIds
	 *   单个模板 ID
	 * @return
	 *   单个模板文本, 数量等同于 <code>tempIds.length</code>
	 */
	public String getTemplate(String tempId) {
		return templates.get(tempId);
	}
	
	/**
	 * 获得模板文本
	 * @param tempIds
	 *   多个模板 ID
	 * @return
	 *   多个模板文本, 数量等同于 <code>tempIds.length</code>
	 */
	public String[] getTemplates(String[] tempIds) {
		final int length = tempIds.length;
		String[] temps = new String[length];
		for (int i = 0; i < length; i++) {
			temps[i] = getTemplate(tempIds[i]);
		}
		return temps;
	}
	
	public ITranslate getTranslator(String handle) {
		return trm.get(handle);
	}
	
	public ITemplateChooser getChooser(String head) {
		ITemplateChooser ch = chs.get(head);
		if (ch != null) {
			return ch;
		}
		return dch;
	}
	
	MessageRepository() {
		// 初始化模板
		Properties pro = new Properties();
		InputStream in = getClass().getResourceAsStream("/com/zdream/pmw/platform/translate/trans.properties");
		try {
			pro.load(in);
            in.close();
            
            for (Iterator<Entry<Object, Object>> it = pro.entrySet().iterator(); it.hasNext();) {
            	Entry<Object, Object> entry = it.next();
				
            	String key = entry.getKey().toString();
            	try {
					if (key.startsWith("translate.")) {
						// ITranslate
						ITranslate t = (ITranslate) Class.forName(entry.getValue().toString()).newInstance();
						String[] handlers = t.canHandle();
						for (int j = 0; j < handlers.length; j++) {
							trm.put(handlers[j], t);
						}
					} else if (key.startsWith("template.")) {
						// ITemplateChooser
						ITemplateChooser t = (ITemplateChooser) Class.forName(entry.getValue().toString()).newInstance();
						String[] handlers = t.canHandle();
						for (int j = 0; j < handlers.length; j++) {
							chs.put(handlers[j], t);
						}
					}
				} catch (RuntimeException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
            
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		JsonBuilder builder = new JsonBuilder();
		String m = AssetReader.read("data/static/translate-dicts.json", 2000).toString();
		dicts = builder.parseJson(m);
		
		m = AssetReader.read("data/static/translate-templates.json", 2000).toString();
		JsonValue v = builder.parseJson(m);
		for (Iterator<Entry<String, JsonValue>> it = v.getMap().entrySet().iterator(); it.hasNext();) {
			Entry<String, JsonValue> entry = it.next();
			templates.put(entry.getKey(), entry.getValue().getString());
		}
		
		dch = new DefaultTemplateChooser();
	}
}
