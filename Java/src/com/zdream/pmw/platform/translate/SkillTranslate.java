package com.zdream.pmw.platform.translate;

import java.util.Map;

import com.zdream.pmw.platform.attend.SkillRelease;
import com.zdream.pmw.platform.prototype.BattlePlatform;

/**
 * 翻译技能相关的数据<br>
 * 
 * @since v0.2
 * @author Zdream
 * @date 2017年3月1日
 * @version v0.2
 */
public class SkillTranslate implements ITranslate {
	
	static final String H_TITLE = "skill.title";
	
	static final String 
			K_ID = "skill.id";

	@Override
	public String[] canHandle() {
		return new String[]{H_TITLE};
	}

	@Override
	public String translate(String handle, Map<String, String> param, BattlePlatform pf) {
		switch (handle) {
		case H_TITLE: {
			String idstr = param.get(K_ID);
			if (idstr != null) {
				SkillRelease skr = pf.getAttendManager().getSkillRelease(Short.parseShort(idstr));
				if (skr != null) {
					return skr.getSkill().getTitle();
				}
			}
		} break;

		default:
			break;
		}
		return null;
	}

}
