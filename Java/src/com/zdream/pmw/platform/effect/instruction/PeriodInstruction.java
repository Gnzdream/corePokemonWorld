package com.zdream.pmw.platform.effect.instruction;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.zdream.pmw.platform.effect.AInstruction;
import com.zdream.pmw.util.json.JsonArray;
import com.zdream.pmw.util.json.JsonObject;
import com.zdream.pmw.util.json.JsonValue;
import com.zdream.pmw.util.random.RanValue;

/**
 * <p>处理蓄力技能数据的指示
 * <p>包括蓄力、反作用类型等, 完全释放技能需要多于 1 个回合的技能<br>
 * 该指示需要完成的工作有:
 * <li>处理和过滤 param 的参数;
 * <li>在蓄力的第一回合为攻击方施加蓄力状态, (补充 addition);
 * </li></p>
 * 
 * @since v0.2.3 [2017-05-06]
 * @author Zdream
 * @version v0.2.3 [2017-05-06]
 */
public class PeriodInstruction extends AInstruction {
	
	JsonObject args;
	// 探测现在是蓄力的第几个回合
	int count;
	
	JsonArray param;
	
	/**
	 * 模式
	 */
	private int mode;
	
	/**
	 * <p>像旋风刀一样一回合不做任何动作, 二回合攻击的是 charge</p>
	 * <p>默认值</p>
	 * <p>参数值 "c"</p>
	 */
	public static final int MODE_CHARGE = 0;
	
	/**
	 * <p>像大闹一番技能 (暴走) 一样, 2 至 3 回合连续攻击,
	 * 最后一轮攻击结束后自己混乱</p>
	 * <p>参数值 "t"</p>
	 */
	public static final int MODE_THRASH = 1;
	
	/**
	 * <p>像破坏光线技能一样, 第一回合攻击,
	 * 第二回合将不能行动</p>
	 * <p>参数值 "hb"</p>
	 */
	public static final int MODE_HYPER_BEAM = 2;
	
	@Override
	public void set(JsonObject args) {
		this.args = args;
	}
	
	@Override
	public void restore() {
		this.args = null;
		this.param = null;
		this.mode = MODE_CHARGE;
		this.count = 1;
	}

	@Override
	public String name() {
		return "period";
	}

	@Override
	protected void execute() {
		init();
		filterParam();
		forceState();
	}
	
	/**
	 * 初始化 count 参数
	 */
	private void init() {
		Object o;
		
		if (args == null) {
			return;
		}
		
		// count
		if ((o = getData("period_count")) != null) {
			count = (int) o;
		}
		
		// mode
		if ((o = args.get("m")) != null) {
			if (o instanceof Number) {
				int i = ((Number) o).intValue();
				this.mode = i;
			} else {
				String s = o.toString();
				switch (s) {
				case "c": case "charge":
					this.mode = MODE_CHARGE;
					break;
				case "t": case "thrash":
					this.mode = MODE_THRASH;
					break;
				case "hb": case "hyper_beam":
					this.mode = MODE_HYPER_BEAM;
					break;
				}
			}
		}
	}
	
	/* ************
	 *	修改参数  *
	 ************ */
	
	/**
	 * 根据模式与现在所处的蓄力回合数, 对所有 instruction 的参数进行修改
	 */
	private void filterParam() {
		switch (mode) {
		case MODE_THRASH:
			filterParamForThrash();
			break;
		case MODE_HYPER_BEAM:
			filterParamForHyper();
			break;

		case MODE_CHARGE:
		default:
			filterParamForCharge();
			break;
		}
	}

	/**
	 * 第一回合设置为不攻击;
	 * 第二回合设置攻击
	 */
	private void filterParamForCharge() {
		JsonObject root = getParameter();
		
		if (count == 1) {
			// 跳过攻击部分
			JsonObject d = root.getJsonValue("rehold").asObject();
			d.put("skipDamage", true);
			
			// 不判断命中和免疫
			HashSet<String> set = new HashSet<>();
			set.add(CODE_CALC_HITRATE);
			set.add("immuse");
			removeInstructions(set);

			/* {
		    	"t" : "confsk",
		    	"pp-sub" : false
		    } */
			param = new JsonArray();
			JsonObject jo = new JsonObject();
			jo.put("t", "confsk");
			jo.put("pp-sub", false);
			param.add(jo);
		}
		
		// 其它
		countFilter(root);
	}

	/**
	 * 第一回合攻击;
	 * 第二回合僵直不能移动
	 */
	private void filterParamForHyper() {
		JsonObject root = getParameter();
		
		if (count == 1) {
			int x = RanValue.random(0, 2); // 0 或 1
			
			param = new JsonArray();
			/* 第二回合
			{
		    	"t" : "confsk",
		    	"pp-sub" : false,
		    	"_value" : [
		    		{
		    			"n":"m.hesitant",
						"t":"m"
		    		}
		    	]
		    }
		    */
			
			if (x == 1) {
				JsonObject jo = new JsonObject();
				jo.put("t", "confsk");
				jo.put("pp-sub", false);
				param.add(jo);
			}
			
			JsonObject jo = new JsonObject(), _value0 = new JsonObject();
			JsonArray _value = new JsonArray();
			jo.put("t", "confsk");
			jo.put("pp-sub", false);
			_value0.put("n", "m.hesitant");
			_value0.put("t", "m");
			_value.add(_value0);
			jo.add("_value", _value);
			param.add(jo);
		}
		
		// 其它
		countFilter(root);
	}
	
	/**
	 * 第一回合到第二回合至第三回合连续攻击,
	 * 最后一个回合攻击完后混乱
	 */
	private void filterParamForThrash() {
		JsonObject root = getParameter();
		
		if (count == 1) {
			int x = RanValue.random(0, 2); // 0 或 1
			
			param = new JsonArray();
			/* 非最后回合
			{
		    	"t" : "confsk",
		    	"pp-sub" : false
		    }
		    
		    最后回合
		    {
		    	"t" : "confsk",
		    	"pp-sub" : false,
		    	"_value" : [
		    		{
		    			"n":"a.state",
						"t":"a",
						"tg":"self",
						"v":"confusion"
		    		}
		    	]
		    }
		    */
			
			if (x == 1) {
				JsonObject jo = new JsonObject();
				jo.put("t", "confsk");
				jo.put("pp-sub", false);
				param.add(jo);
			}
			
			JsonObject jo = new JsonObject(), _value0 = new JsonObject();
			JsonArray _value = new JsonArray();
			jo.put("t", "confsk");
			jo.put("pp-sub", false);
			_value0.put("n", "a.state");
			_value0.put("t", "a");
			_value0.put("tg", "self");
			_value0.put("v", "confusion");
			_value.add(_value0);
			jo.add("_value", _value);
			param.add(jo);
		}
		
		// 其它
		countFilter(root);
	}
	
	private void countFilter(JsonObject root) {
		Object o;
		Map<String, JsonValue> map = root.asMap();
		HashSet<String> rms = new HashSet<>();
		
		for (Iterator<Entry<String, JsonValue>> it = map.entrySet().iterator(); it.hasNext();) {
			Entry<String, JsonValue> entry = it.next();
			String key = entry.getKey();
			
			if ("confsk".equals(key)) {
				continue;
			}
			
			JsonObject jo = entry.getValue().asObject();
			if ("rehold".equals(key)) {
				countFilterRehold(jo);
				continue;
			}
			
			if ((o = jo.get("_period")) != null) {
				if (o instanceof JsonArray) {
					List<JsonValue> list = ((JsonArray) o).asList();
					boolean b = false;
					for (Iterator<JsonValue> it0 = list.iterator(); it0.hasNext();) {
						JsonValue j = it0.next();
						if (count == (int) j.getValue()) {
							b = true;
							break;
						}
					}
					
					if (!b) {
						rms.add(key);
					}
				} else {
					int p = (int) o;
					if (p != count) {
						rms.add(key);
					}
				}
			}
		}
		
		for (Iterator<String> it = rms.iterator(); it.hasNext();) {
			String s = it.next();
			map.remove(s);
		}
	}
	
	private void countFilterRehold(JsonObject rehold) {
		// addition
		JsonValue v = rehold.getJsonValue("a");
		Object o;
		if (v != null) {
			JsonArray a = v.asArray();
			int len = a.size();
			for (int i = 0; i < len; i++) {
				JsonObject jo = a.getJsonValue(i).asObject();
				
				if ((o = jo.get("_period")) != null) {
					if (o instanceof JsonArray) {
						List<JsonValue> list = ((JsonArray) o).asList();
						boolean b = false;
						for (Iterator<JsonValue> it0 = list.iterator(); it0.hasNext();) {
							JsonValue j = it0.next();
							if (count == (int) j.getValue()) {
								b = true;
								break;
							}
						}
						
						if (!b) {
							a.remove(i);
							i--;
							len--;
						}
					} else {
						int p = (int) o;
						if (p != count) {
							a.remove(i);
							i--;
							len--;
						}
					}
				}
			}
			
			if (len == 0) {
				rehold.remove("a");
			}
		}
	}
	
	/* ************
	 *	状态施加  *
	 ************ */
	
	/**
	 * 施加蓄力状态, 仅在蓄力的第一回合操作
	 */
	private void forceState() {
		if (count != 1) {
			return;
		}
		
		/*
		{
			"n":"a.state",
			"t":"a",
			"tg":"enemy",
			"v":"confusion",
			"r":100,
			"rm":false
		},
		 */
		
		JsonObject jo = new JsonObject();
		jo.put("n", "a.state");
		jo.put("t", "a");
		jo.put("tg", "self");
		jo.put("v", "period");
		jo.put("-param", param);
		jo.put("-target", pack.getOriginTarget());
		
		JsonObject rehold = getParameter().getJsonValue("rehold").asObject();
		JsonValue v = rehold.getJsonValue("a");
		JsonArray a;
		if (v != null) {
			a = v.asArray();
		} else {
			a = new JsonArray();
			rehold.add("a", a);
		}
		a.add(jo);
	}

}
