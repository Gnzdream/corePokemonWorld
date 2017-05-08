package com.zdream.pmw.platform.effect.instruction;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import com.zdream.pmw.monster.skill.ESkillCategory;
import com.zdream.pmw.platform.attend.ESkillRange;
import com.zdream.pmw.platform.attend.SkillRelease;
import com.zdream.pmw.platform.effect.AInstruction;
import com.zdream.pmw.platform.effect.Aperitif;
import com.zdream.pmw.platform.effect.SkillReleasePackage;
import com.zdream.pmw.platform.order.event.IEvent;
import com.zdream.pmw.util.json.JsonArray;
import com.zdream.pmw.util.json.JsonObject;
import com.zdream.pmw.util.json.JsonValue;
import com.zdream.pmw.util.json.JsonValue.JsonType;

/**
 * <p>确定释放的技能的指挥部分</p>
 * 
 * @since v0.2.3 [2017-04-23]
 * @author Zdream
 * @version v0.2.3 [2017-05-04]
 */
public final class ConfirmSkillInstruction extends AInstruction {
	
	SkillRelease skr;
	
	/* 外部用 set 方法注入的参数. 下面是合法的参数
	{
    	"t" : "confsk",
    	"pp-sub" : false,
        "_mode" : "set",
        "_value" : [ 
            {
                "n" : "d.default",
                "t" : "d"
            }
        ]
    } */
	JsonObject args;
	
	// 最终合并的数据, 将直接按照 key 放入到各个 instruction 中
	JsonObject params = new JsonObject();
	
	// 从 skill.release 中复制过来的参数
	JsonArray additionParams = new JsonArray();
	
	public SkillRelease getSkr() {
		return skr;
	}

	@Override
	public String name() {
		return CODE_CONFIRM_SKILL;
	}
	
	@Override
	public void set(JsonObject args) {
		this.args = args;
	}
	
	@Override
	public void restore() {
		super.restore();
		params = new JsonObject();
		additionParams = new JsonArray();
	}

	@Override
	protected void execute() {
		short skillId = comfirmSkill();
		skr = am.getSkillRelease(skillId);
		putData(SkillReleasePackage.DATA_KEY_SKILL_ID, skillId);
		
		confirmFormula();
		putData0();

		putNextInstructions();
	}
	
	private short comfirmSkill() {
		byte atseat = pack.getAtStaff().getSeat();
		Aperitif ap = em.newAperitif(name(), atseat);
		
		short skillID = am.getParticipant(atseat).getAttendant()
				.getSkill()[pack.getSkillNum()];
		ap.put("seat", atseat);
		ap.put("skillID", skillID);
		em.getRoot().readyCode(ap);
		
		// 像混乱触发、变身等状态触发时, 能够修改释放的技能
		return (short) ap.get("skillID");
	}
	
	/* ************
	 *	设置参数  *
	 ************ */
	
	private void confirmFormula() {
		Object o;
		String mode = (args == null || (o = args.get("_mode")) == null) ?
				"default" : o.toString();
		
		if (!"set".equals(mode)) {
			JsonObject[] os = skr.getAdditions();
			JsonArray arr;
			if (os == null) {
				arr = new JsonArray();
			} else {
				arr = new JsonArray(os); // 里面使用了 clone 方法
			}
			putFormulas(arr, "default");
			
			if (args != null && (o = args.get("_value")) != null) {
				putFormulas((JsonArray) o, mode);
			}
		} else {
			// set
			putFormulas((JsonArray) args.get("_value"), "set");
		}
		
		putData(SkillReleasePackage.DATA_KEY_PARAM, params);
	}
	
	/**
	 * 将 arr 中的 release 参数按照模式放入到 params 中.
	 * @param arr
	 * @param mode
	 */
	private void putFormulas(JsonArray arr, String mode) {
		int len = arr.size();
		ArrayList<JsonObject> as = new ArrayList<>();
		
		switch (mode) {
		case "set":
			params.clear();
			break;
			
		case "merge":
			mergeParam(arr);
			return;

		default:
			break;
		}
		
		for (int i = 0; i < len; i++) {
			JsonObject jo = (JsonObject) arr.get(i);
			
			String key = jo.get("t").toString();
			
			if ("a".equals(key)) {
				// addition
				as.add(jo);
			} else {
				params.put(key, jo);
			}
		}
		
		if (!as.isEmpty()) {
			this.additionParams.asList().addAll(as);
		}
	}
	
	private void mergeParam(JsonArray arr) {
		int len = arr.size();
		JsonValue v;
		JsonObject vo;
		
		for (int i = 0; i < len; i++) {
			JsonObject jo = (JsonObject) arr.get(i);
			
			String key = jo.get("t").toString();
			if ((v = params.getJsonValue(key)) == null) {
				vo = new JsonObject();
				params.add(key, vo);
			} else {
				vo = v.asObject();
			}
			vo.putAll(jo);
		}
	}
	
	/* ************
	 *	  其它    *
	 ************ */
	
	/*
	 * 在 events 列表中放入下面将执行的事件
	 */
	private void putNextInstructions() {
		// 添加事件以向下执行: 
		// 判断能否行动、数据初始化、扣 PP、
		// 检查是否发动失败、判断属性免疫和命中
		
		ArrayList<IEvent> list = new ArrayList<IEvent>();
		AInstruction ins;
		JsonValue o = null;
		
		JsonObject confsk = null;
		if ((o = params.getJsonValue("confsk")) != null) {
			confsk = o.asObject();
		}
		
		ins = (AInstruction) loadFormula("i.moveable");
		ins.setPackage(pack);
		list.add(ins);
		
		ins = (AInstruction) loadFormula("i.init");
		ins.setPackage(pack);
		list.add(ins);
		
		if (hasInstruction(confsk, "pp-sub", true)) {
			ins = (AInstruction) loadFormula("i.ppsub");
			ins.setPackage(pack);
			list.add(ins);
		}
		
		if (hasInstruction(confsk, "range", true)) {
			ins = (AInstruction) loadFormula("i.range");
			ins.setPackage(pack);
			list.add(ins);
		}
		
		if (hasInstruction(confsk, "fail", false)) {
			// 大部分技能没有这个部分的判断
			ins = (AInstruction) loadFormula("i.fail");
			ins.setPackage(pack);
			list.add(ins);
		}
		
		if (hasInstruction(confsk, "immuse", skr.getCategory() != ESkillCategory.STATUS)) {
			// 默认: 变化技能没有这个判断, 物理和特殊技能有
			ins = (AInstruction) loadFormula("i.immuse");
			ins.setPackage(pack);
			list.add(ins);
		}
		
		if (hasInstruction(confsk, "hit", skr.getRange() != ESkillRange.SELF)) {
			// 默认: 当技能目标为自己时, 不判断
			ins = (AInstruction) loadFormula("i.hit");
			ins.setPackage(pack);
			list.add(ins);
		}
		
		ins = (AInstruction) loadFormula("i.rehold");
		ins.setPackage(pack);
		if (skr.getCategory() != ESkillCategory.STATUS || additionParams.size() > 0) {
			JsonObject p = getParameterOrCreate("rehold");
			if (additionParams.size() > 0) {
				p.add("a", additionParams);
			}
			JsonValue v;
			if ((v = params.getJsonValue("d")) != null) {
				p.add("d", v);
			}
			if ((v = params.getJsonValue("w")) != null) {
				p.add("w", v);
			}
		}
		list.add(ins);
		
		ins = (AInstruction) loadFormula("i.finish");
		ins.setPackage(pack);
		list.add(ins);
		
		putNextEvents(list);
	}
	
	/**
	 * @param confsk
	 */
	private void putData0() {
		if (args == null) {
			return;
		}
		Object o = args.get("_data");
		
		if (o != null) {
			JsonObject jo = (JsonObject) o;
			Map<String, JsonValue> m = jo.asMap();
			for (Iterator<String> it = m.keySet().iterator(); it.hasNext();) {
				String key = it.next();
				putData(key, jo.get(key));
			}
		}
	}

	public boolean hasInstruction(JsonObject confsk, String key, boolean def) {
		if (confsk == null) {
			return def;
		}
		
		JsonValue v = null;
		if ((v = confsk.getJsonValue(key)) != null) {
			if (v.getType() == JsonType.ObjectType) {
				return true;
			} else {
				return (boolean) v.getValue();
			}
		}
		
		return def;
	}
	
	@Override
	public String toString() {
		return name();
	}

}
