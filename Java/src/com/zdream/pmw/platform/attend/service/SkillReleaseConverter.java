package com.zdream.pmw.platform.attend.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.zdream.pmw.monster.data.SkillDataBuffer;
import com.zdream.pmw.monster.skill.Skill;
import com.zdream.pmw.platform.attend.AttendManager;
import com.zdream.pmw.platform.attend.ESkillRange;
import com.zdream.pmw.platform.attend.SkillRelease;
import com.zdream.pmw.platform.control.IPrintLevel;
import com.zdream.pmw.util.json.JsonArray;
import com.zdream.pmw.util.json.JsonObject;
import com.zdream.pmw.util.json.JsonValue;

/**
 * 释放技能数据的转换器服务接口实现类<br>
 * 完成 <code>Skill</code>、<code>SkillRelease</code> 数据间的转换<br>
 * 该类将 DAO 层完全屏蔽，和原来利用 DAO 直接获得数据相比<br>
 * 本次采用了 <code>SkillDataBuffer</code> 缓存池，比原来效率更高<br>
 * 并且从 <code>EffectManage</code>(EM) 迁过来的途中<br>
 * 将 <code>com.zdream.pmw.platform.effect.SkillReleaseFactory</code> 合并<br>
 * <br>
 * <b>v0.2</b><br>
 *   将名称改为 <code>SkillReleaseConverter</code>，并停用单例模式<br>
 * 
 * <p><b>v0.2.3</b><br>
 * 将 {@code release} 部分的数据的解析方式做了修改, 和原先不同的是,
 * 放入 {@code SkillRelease.addition} 的数据采用了黑名单的方式,
 * 而非原来的白名单的方式</p>
 * 
 * @since v0.1 [2016-04-11]
 * @author Zdream
 * @version v0.2.3 [2017-05-04]
 */
public class SkillReleaseConverter {
	
	AttendManager am;
	
	public SkillReleaseConverter(AttendManager am) {
		this.am = am;
	}
	
	/* ************
	 *	实现方法  *
	 ************ */
	
	public SkillRelease getSkillRelease(short skillID) {
		Skill skill = buffer.getBaseData(skillID);
		
		if (map.containsKey(skill.getId())) {
			return map.get(skill.getId());
		}
		
		SkillRelease release = new SkillRelease(skill);
		JsonArray data = skill.getRelease();
		parseData(release, data);
		map.put(skill.getId(), release);
		
		return release;
	}

	public void preloadSkills(List<Short> skillIDs) {
		int amount = buffer.preLoad(skillIDs);
		am.logPrintf(IPrintLevel.PRINT_LEVEL_INFO,
				"skrConvertSI.preloadSkills(1) 预加载技能数据%d项", amount);
	}
	
	/* ************
	 *	私有方法  *
	 ************ */
	
	/**
	 * 解析释放数据字符串
	 * @param release
	 * @param data
	 */
	private void parseData(SkillRelease release, JsonArray a) {
		ArrayList<JsonObject> additions = new ArrayList<>();
		
		if (a != null) {
			List<JsonValue> list = a.asArray().asList();
			for (Iterator<JsonValue> it = list.iterator(); it.hasNext();) {
				JsonObject o = it.next().asObject();
				
				Map<String, JsonValue> map = o.asMap();
				switch (o.get("t").toString()) {
				case "c": { // crit
					int v = (int) map.get("v").getValue();
					release.setCritLevel((byte) v);
				} break;
				
				// v0.2 新加
				case "p": { // priority
					int v = (Integer) map.get("v").getValue();
					release.setPriority((byte) v);
				} break;
				
				// v0.2.1 新加
				case "r": { // range
					String r = map.get("v").getString();
					release.setRange(ESkillRange.parseEnum(r));
					if (map.size() > 2)
						additions.add(o);
				} break;
				
				// v0.2.3 新加
				case "x": {// v0.2.3 用于补充特殊, 该类数据被合并至 SkillRelease.property 中
					JsonObject jo = release.getProperty();
					for (Iterator<Entry<String, JsonValue>> it0 = map.entrySet().iterator(); it.hasNext();) {
						Entry<String, JsonValue> entry = it0.next();
						String key = entry.getKey();
						if ("t".equals(key)) {
							continue;
						}
						jo.add(key, entry.getValue());
					}
				} break;
				
				// v0.2.3 新加
				case "#": // v0.2.2 用于注释, 直接忽略即可
					break;

				// TODO add more
					
				default:
				// 包括各种 formula 参数 (a d m w u) 等
					additions.add(o);
					break;
				}
			}
		}
		
		// 处理 addition
		if (!additions.isEmpty()) {
			JsonObject[] os = new JsonObject[additions.size()];
			additions.toArray(os);
			release.setAdditions(os);
		}
		
		setDefaultData(release);
	}
	
	/**
	 * 将数据中未提及的属性设置默认的参数
	 * @param release
	 */
	private void setDefaultData(SkillRelease release) {
		if (release.getRange() == null) {
			release.setRange(ESkillRange.SINGLE);
		}
		// TODO add more
	}
	
	/* ************
	 *	调用结构  *
	 ************ */
	/**
	 * 技能数据的缓存池
	 */
	private SkillDataBuffer buffer = SkillDataBuffer.getInstance();
	
	/**
	 * SkillRelease 数据的库存<br>
	 * 已经产生的数据库存在此能够缓存，等待下次获取<br>
	 */
	private Map<Short, SkillRelease> map = new HashMap<Short, SkillRelease>();

}
