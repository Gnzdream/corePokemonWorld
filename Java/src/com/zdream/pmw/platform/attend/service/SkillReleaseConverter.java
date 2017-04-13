package com.zdream.pmw.platform.attend.service;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.zdream.pmw.monster.data.SkillDataBuffer;
import com.zdream.pmw.monster.skill.Skill;
import com.zdream.pmw.platform.attend.AttendManager;
import com.zdream.pmw.platform.attend.ESkillRange;
import com.zdream.pmw.platform.attend.SkillRelease;
import com.zdream.pmw.platform.control.IPrintLevel;
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
 * @since v0.1
 * @author Zdream
 * @date 2016年4月11日
 * @version v0.2
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
		JsonValue data = buffer.getReleaseData(skill.getId());
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
	private void parseData(SkillRelease release, JsonValue v) {
		
		if (v != null) {
			try {
				switch (v.getType()) {
				case ArrayType: {
					List<JsonValue> list = v.getArray();
					for (Iterator<JsonValue> it = list.iterator(); it.hasNext();) {
						JsonValue vs = it.next();
						parseDataPair(release, vs);
					}
					
				} break;
				case ObjectType:
					parseDataPair(release, v);
					break;

				default:
					break;
				}
				
			} catch (RuntimeException e) {
				e.printStackTrace();
			}
		}
		
		
		setDefaultData(release);
		
		
	}
	
	private void parseDataPair(SkillRelease release, JsonValue data) {
//		if (data.startsWith(HEAD_CRIT)) {
//			release.setCritLevel(Byte.parseByte(data.substring(HEAD_CRIT.length())));
//		} else if (data.startsWith(HEAD_ADDITION)) {
//			release.setAddition(data.substring(HEAD_ADDITION.length()));
//		}
		
		Map<String, JsonValue> map = data.getMap();
		switch (map.get("t").getString()) {
		case "a": case "d": case "m": case "w": case "u": // addition +
			release.addAddition(data);
			break;
		case "c": { // crit
			int v = (Integer) data.getMap().get("v").getValue();
			release.setCritLevel((byte) v);
		} break;
		
		// v0.2 新加
		case "p": { // priority
			int v = (Integer) data.getMap().get("v").getValue();
			release.setPriority((byte) v);
		} break;
		
		// v0.2.1 新加
		case "r": { // range
			String r = data.getMap().get("v").getString();
			release.setRange(ESkillRange.parseEnum(r));;
		} break;
		
		default:
			break;
		}
		
		// TODO add more
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
