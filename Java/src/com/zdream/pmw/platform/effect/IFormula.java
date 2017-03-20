package com.zdream.pmw.platform.effect;

import com.zdream.pmw.util.json.JsonValue;

/**
 * 判定公式接口<br>
 * @since v0.2.1
 * @author Zdream
 * @date 2017年3月18日
 * @version v0.2.1
 */
public interface IFormula {
	
	/**
	 * 伤害的注册名称
	 * @return
	 */
	public String name();
	
	/**
	 * 参数设置<br>
	 * 注: 原来 args 是用 String[] 格式, 但是由于数据不完整, 修改为使用 JsonValue 数据
	 * @param args
	 */
	default public void set(JsonValue args){}
	
	/**
	 * 恢复成默认数值<br>
	 */
	default public void restore(){}

}
