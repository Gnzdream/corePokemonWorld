package com.zdream.pmw.platform.prototype;

/**
 * 战场的规则数据接口<br>
 * 
 * @since v0.1
 * @author Zdream
 * @date 2016年3月30日
 * @version v0.2.2
 */
public interface IBattleRule {
	
	/**
	 * 测试类战斗<br>
	 * 仅在调试的状态下进行<br>
	 * 模式：1 on 1<br>
	 * 我方精灵采用我方 Trainer 自带、控制；<br>
	 * 敌方精灵采用 Trainer 给定、低级 AI<br>
	 * 采用单线程模式运行<br>
	 */
	public static final int RULE_DEBUG = 0;
	
	/**
	 * 测试类战斗，启用多线程模式调试.<br>
	 * 仅在调试的状态下进行<br>
	 * 模式：1 on 1<br>
	 * 我方精灵采用我方 Trainer 自带、控制；<br>
	 * 敌方精灵采用 Trainer 给定、低级 AI<br>
	 * 采用多线程模式运行<br>
	 */
	public static final int RULE_DEBUG_MULTI = 0x40000000;
	
}
