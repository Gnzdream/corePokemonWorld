package com.zdream.pmw.platform.effect.instruction;

import com.zdream.pmw.platform.effect.AInstruction;
import com.zdream.pmw.platform.effect.SkillReleasePackage;
import com.zdream.pmw.platform.effect.range.IRangeFormula;
import com.zdream.pmw.util.json.JsonObject;

/**
 * <p>最终目标的锁定指示
 * <p>对原有计算的技能释放范围进行修正和过滤, 得出最终目标的锁定指示.
 * </p>
 * 
 * @since v0.2.3 [2017-05-05]
 * @author Zdream
 * @version v0.2.3 [2017-05-05]
 */
public class TargetsInstruction extends AInstruction {
	
	IRangeFormula targetf;
	/*
	 * 由于需要兼容以前的版本的数据结构, 因此有三种合法的格式.
	 * 推荐使用的格式 (v0.2.3 及以后)
	 * 一般的 JsonObject 格式
	 * 
	 * 默认格式 (v0.2.3 及以后)
	 * {
	 *   "a" : <addition formula 的 name, 格式 a.xxx>
	 *   <"ignoreHit"> : <boolean>
	 * }
	 * 
	 * 兼容格式 (v0.2.2 及以前)
	 * 在以前使用 formula 时, 将目标标示在 tg 字段中, 为 JsonValue 的单值.
	 * 为转化成 JsonObject, 格式如下:
	 * {
	 *   "tg" : <原来的值>
	 *   <"ignoreHit"> : <boolean>
	 * }
	 */
	JsonObject param;
	
	/**
	 * 如果是判断伤害的目标, 为 true
	 */
	boolean damageTarget;

	@Override
	public String name() {
		return "targets";
	}
	
	@Override
	public void set(JsonObject args) {
		this.param = args;
	}
	
	@Override
	public void restore() {
		param = null;
		damageTarget = false;
	}

	@Override
	protected void execute() {
		if (param != null)
			loadTarget();
		else
			targetf = (IRangeFormula) loadFormula("ra.target");
		
		putInfo("Target formula: " + targetf.toString());
		byte[] targets = targetf.range(pack, em);
		putData(SkillReleasePackage.DATA_KEY_TARGETS, targets);
		
		if (targets.length == 0) {
			
			emptyTargets();
		}
	}
	
	private void loadTarget() {
		Object o;
		if ((o = param.get("tg")) != null) {
			targetf = (IRangeFormula) loadFormula("ra." + o.toString());
			targetf.set(param);
		} else if ((o = param.get("a")) != null) {
			String aname = o.toString();
			targetf = defaultTargetsFormula(aname);
			targetf.set(param);
		} else {
			targetf = (IRangeFormula) loadFormula(param);
		}
		
		if ((o = param.get("damageTarget")) != null) {
			this.damageTarget = (boolean) o;
		}
	}
	
	/**
	 * 处理没有攻击目标的情况
	 * 为了单回合连续攻击中, 如果没有攻击对象后, 让攻击停止.
	 */
	private void emptyTargets() {
		if (!damageTarget) {
			return;
		}
		
		Object o;
		// 查询是否在单回合多轮攻击中
		if ((o = getData(SkillReleasePackage.DATA_KEY_MULTI_COUNT)) == null) {
			return;
		}
		
		// 实际向外报告的攻击次数
		putData(SkillReleasePackage.DATA_KEY_MULTI_ACTUAL_COUNT, ((int) o) - 1);
	}

}
