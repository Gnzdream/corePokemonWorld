package com.zdream.pmw.platform.effect.instruction;

import java.util.ArrayList;
import java.util.HashSet;

import com.zdream.pmw.monster.skill.ESkillCategory;
import com.zdream.pmw.platform.attend.ESkillRange;
import com.zdream.pmw.platform.effect.AInstruction;
import com.zdream.pmw.platform.effect.SkillReleasePackage;
import com.zdream.pmw.platform.order.event.IEvent;
import com.zdream.pmw.util.json.JsonArray;
import com.zdream.pmw.util.json.JsonObject;
import com.zdream.pmw.util.json.JsonValue;
import com.zdream.pmw.util.json.JsonValue.JsonType;

/**
 * <p>对于技能伤害和特殊效果的设定的指示
 * </p>
 * 
 * @since v0.2.3 [2017-04-27]
 * @author Zdream
 * @version v0.2.3 [2017-04-27]
 */
public class ReleaseHolderInstruction extends AInstruction {
	
	JsonObject damageParam, powerParam, xParam;
	JsonArray additionParams;
	
	/**
	 * 是否跳过计算伤害的部分
	 * 在旋风刀第一回合时需要设置这个参数为 true
	 */
	boolean skipDamage;
	
	// 单回合多段攻击需要的数据
	private int multiCount;
	private int multiMax;

	@Override
	public String name() {
		return "rehold";
	}
	
	@Override
	public void set(JsonObject args) {
		JsonValue v = args.getJsonValue("d");
		if (v != null) {
			damageParam = v.asObject();
		}
		
		v = args.getJsonValue("w");
		if (v != null) {
			powerParam = v.asObject();
		}
		
		v = args.getJsonValue("a");
		if (v != null) {
			additionParams = v.asArray();
		}
		
		Object o;
		o = args.get("skipDamage");
		if (o != null) {
			skipDamage = (boolean) o;
		}
		
		xParam = args;
	}
	
	@Override
	public void restore() {
		super.restore();
		damageParam = null;
		additionParams = null;
		powerParam = null;
		xParam = null;
		skipDamage = false;
		multiCount = 0;
		multiMax = 0;
	}

	@Override
	protected void execute() {
		ArrayList<IEvent> list = new ArrayList<IEvent>();
		
		boolean stop = false;
		
		stop = preMulti(list);
		if (!stop) {
			if (skipDamage) {
				putInfo("Skip damage progress");
			} else {
				handleDamage(list);
			}
			handleAddition(list);
			handleMulti(list);
		} else {
			putInfo("Force to stop multi-strike move");
		}

		putNextEvents(list);
	}

	protected void handleDamage(ArrayList<IEvent> list) {
		ESkillCategory c = pack.getSkill().getCategory();
		AInstruction ins;
		
		if (c != ESkillCategory.STATUS) {
			String[] reference;
			
			// damage 部分
			if (damageParam == null) {
				reference = reference("d.default");
			} else {
				reference = reference(damageParam.get("n").toString());
			}
			
			HashSet<String> refers = new HashSet<>();
			if (reference != null) {
				for (int i = 0; i < reference.length; i++) {
					refers.add(reference[i]);
				}
			}
			
			/* {
				"tg" : "target",
				"damageTarget" : true
			} */
			ins = (AInstruction) loadFormula("i.target");
			ins.setPackage(pack);
			{
				JsonObject jo = new JsonObject();
				jo.put("tg", "target");
				jo.put("damageTarget", true);
				ins.set(jo);
			}
			list.add(ins);
			
			// power 部分
			if (refers.contains("attack")) {
				ins = (AInstruction) loadFormula("i.attack");
				ins.setPackage(pack);
				ins.set(powerParam);
				list.add(ins);
			}
			
			if (refers.contains("defense")) {
				ins = (AInstruction) loadFormula("i.defense");
				ins.setPackage(pack);
				list.add(ins);
			}
			
			if (refers.contains("crit")) {
				ins = (AInstruction) loadFormula("i.crit");
				ins.setPackage(pack);
				list.add(ins);
			}
			
			if (refers.contains("correct")) {
				ins = (AInstruction) loadFormula("i.correct");
				ins.setPackage(pack);
				list.add(ins);
			}
			
			ins = (AInstruction) loadFormula("i.damage");
			ins.setPackage(pack);
			ins.set(damageParam);
			list.add(ins);
		}
	}
	
	protected void handleAddition(ArrayList<IEvent> list) {
		AInstruction ins;
		
		if (additionParams != null) {
			final int len = additionParams.size();
			for (int i = 0; i < len; i++) {
				JsonObject jo = additionParams.getJsonValue(i).asObject();
				String aname = jo.get("n").toString();
				
				// targets
				JsonValue o;
				JsonObject to = null;
				if ((o = jo.getJsonValue("tg")) != null) {
					if (o.getType() == JsonType.ObjectType) {
						to = o.asObject();
					} else if (o.getType() == JsonType.StringType) {
						to = new JsonObject();
						to.put("tg", o.getString());
					}
				}
				if (o == null) {
					to = new JsonObject();
					
					// 如果是变化技能, 目标根据技能的释放范围而定
					if (pack.getSkill().getCategory() == ESkillCategory.STATUS) {
						if (pack.getSkill().getRange() == ESkillRange.SELF) {
							to.put("tg", "self");
						} else {
							to.put("tg", "enemy");
						}
					} else {
						to.put("a", aname);
					}
				}
				
				// 参数: 是否在未命中或免疫的状态下依然强制执行
				if ((o = jo.getJsonValue("_ignoreHit")) != null) {
					to.add("ignoreHit", o);
				}
				
				ins = (AInstruction) loadFormula("i.target");
				ins.setPackage(pack);
				ins.set(to); // 由于 target 有很多实例, 参数很难准确注入, 因此要在此 set
				list.add(ins);
				
				// addition
				ins = (AInstruction) loadFormula(aname);
				ins.setPackage(pack);
				ins.set(jo); // 由于 addition 可能在一次攻击中出现相同的, 参数很难准确注入, 因此要在此 set
				list.add(ins);
			}
		}
	}

	/**
	 * 初始化单回合多段攻击需要的数据
	 */
	private boolean preMulti(ArrayList<IEvent> list) {
		Object o = getData(SkillReleasePackage.DATA_KEY_MULTI_MAX);
		if (o == null) {
			return false;
		}
		multiMax = (int) o;
		
		if ((o = getData(SkillReleasePackage.DATA_KEY_MULTI_COUNT)) != null) {
			// 不是第一轮
			multiCount = ((int) o) + 1;
		} else {
			// 第一轮
			multiCount = 1;
			
			// 在 list 开头放上开始连续攻击的 instruction
			JsonObject jo = new JsonObject();
			jo.put("type", "multi");
			jo.put("-tag", "begin");
			
			AInstruction ins = (AInstruction) loadFormula("i.broadcast");
			ins.setPackage(pack);
			ins.set(jo);
			list.add(ins);
		}
		
		if ((o = getData(SkillReleasePackage.DATA_KEY_MULTI_ACTUAL_COUNT)) != null) {
			// stop
			JsonObject jo = new JsonObject();
			jo.put("type", "multi");
			jo.put("-tag", "end");
			jo.put("-round", o); // o: int
			
			AInstruction ins = (AInstruction) loadFormula("i.broadcast");
			ins.setPackage(pack);
			ins.set(jo);
			list.add(ins);
			
			return true;
		} else if (multiCount > multiMax) {
			// 该次攻击判定后, 连续攻击结束
			// 在 list 结尾放上能报告攻击了几次的 instruction
			
			JsonObject jo = new JsonObject();
			jo.put("type", "multi");
			jo.put("-tag", "end");
			jo.put("-round", multiCount - 1); // o: int
			
			AInstruction ins = (AInstruction) loadFormula("i.broadcast");
			ins.setPackage(pack);
			ins.set(jo);
			list.add(ins);
			
			return true;
		}
		
		return false;
	}

	/**
	 * 处理单回合内连续攻击的请求, 最后放入 i.rehold 进行下一轮的判定
	 */
	private void handleMulti(ArrayList<IEvent> list) {
		if (multiMax == 0) {
			return;
		}
		
		if (multiCount <= multiMax) {
			AInstruction ins = (AInstruction) loadFormula("i.rehold");
			ins.setPackage(pack);
			list.add(ins);
		}
		putData(SkillReleasePackage.DATA_KEY_MULTI_COUNT, multiCount);
	}

}
