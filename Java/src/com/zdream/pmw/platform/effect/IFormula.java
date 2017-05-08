package com.zdream.pmw.platform.effect;

import com.zdream.pmw.util.json.JsonObject;

/**
 * 判定公式接口<br>
 * @since v0.2.1
 * @author Zdream
 * @date 2017年3月18日
 * @version v0.2.3
 */
public interface IFormula {
	
	/**
	 * <p>注册名称, 标示该公式的类型
	 * <p>一般而言, 公式的名称一般等同于 ef.properties 中标示的名称.
	 * 而在 v0.2.3 版本及以后, 这个约束将不作必须.
	 * 
	 * @return
	 *   该公式的名称
	 */
	public String name();
	
	/**
	 * 参数设置<br>
	 * 注: 原来 args 是用 String[] 格式, 但是由于数据不完整,
	 * 修改为使用 {@link JsonObject} 数据
	 * @param args
	 */
	default public void set(JsonObject args){}
	
	/**
	 * 恢复成默认数值<br>
	 */
	default public void restore(){}

}
