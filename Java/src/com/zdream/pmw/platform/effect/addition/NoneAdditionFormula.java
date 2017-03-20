package com.zdream.pmw.platform.effect.addition;

import com.zdream.pmw.platform.effect.SkillReleasePackage;
import com.zdream.pmw.util.json.JsonValue;

/**
 * 默认附加状态 / A 类附加效果判定实现类<br>
 * 无附加状态<br>
 * 
 * @since v0.1
 * @author Zdream
 * @date 2016年4月16日
 * @version v0.1
 */
public class NoneAdditionFormula implements IAdditionFormula {

	@Override
	public String name() {
		return "none";
	}

	@Override
	public void addition(SkillReleasePackage package1) {
		// none
	}

	@Override
	public void set(JsonValue args) {
		
	}
	
	@Override
	public void restore() {
		
	}
	
	/* ************
	 *	 初始化   *
	 ************ */
	
	public NoneAdditionFormula() {}

}
