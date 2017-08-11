package com.zdream.pmw.platform.effect.instruction;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.zdream.pmw.platform.effect.AInstruction;
import com.zdream.pmw.platform.effect.Aperitif;
import com.zdream.pmw.platform.effect.EffectManage;
import com.zdream.pmw.platform.order.event.IEvent;
import com.zdream.pmw.util.json.JsonObject;
import com.zdream.pmw.util.json.JsonValue;

/**
 * <p>初始化数据指示
 * <p>现在确定的是, 多回合蓄力、单回合多轮攻击的技能需要初始化它们自己的 instruction.
 * </p>
 * 
 * @since v0.2.3 [2017-04-25]
 * @author Zdream
 * @version v0.2.3 [2017-04-25]
 */
public class InitInstruction extends AInstruction {
	
	JsonObject param;

	@Override
	public String name() {
		return "init";
	}
	
	@Override
	public void set(JsonObject args) {
		if (args == null) {
			return;
		}
		
		if (param == null) {
			param = args;
		} else {
			param.putAll(args);
		}
		param.remove("t");
	}
	
	@Override
	public void restore() {
		param = null;
	}

	@Override
	protected void execute() {
		sendMessageForRelease();
		
		// 其它初始化项
		if (param != null) {
			em.logPrintf(EffectManage.PRINT_LEVEL_INFO, "其它初始化项有 %s\r\n\tat %s",
					param, Thread.currentThread().getStackTrace()[1]);
			createInstruction();
		}
	}
	
	/**
	 * 发布将要释放技能的开胃酒消息
	 */
	private void sendMessageForRelease() {
		byte seat = pack.getAtStaff().getSeat();
		Aperitif ap = em.newAperitif(Aperitif.CODE_RELEASE_SKILL, seat);
		
		short skillID = pack.getSkill().getId();
		ap.put("seat", seat);
		ap.put("skillID", skillID);
		em.getRoot().readyCode(ap);
	}
	
	private void createInstruction() {
		ArrayList<IEvent> list = new ArrayList<IEvent>();
		Map<String, JsonValue> map = param.asMap();
		for (Iterator<Entry<String, JsonValue>> it = map.entrySet().iterator(); it.hasNext();) {
			Entry<String, JsonValue> entry = it.next();
			String key = entry.getKey();
			
			if ("t".equals(key)) {
				continue;
			}
			if (key.startsWith("_")) {
				continue;
			}
			
			createInstruction(key, entry.getValue().asObject(), list);
		}
		
		if (!list.isEmpty()) {
			putNextEvents(list);
		}
	}
	
	private void createInstruction(String key, JsonObject jo, ArrayList<IEvent> list) {
		AInstruction ins = (AInstruction) loadFormula("i." + key);
		ins.setPackage(pack);
		
		// 关于 instruction 放的位置
		String pos = em.getConfig("pos-after.inst." + key);
		if (pos == null) {
			list.add(ins);
		} else {
			boolean b = pf.getOrderManager().putAfterInstructions(pos, ins);
			if (!b) {
				em.logPrintf(EffectManage.PRINT_LEVEL_WARN, "%s 事件无法添加到指定位置: %s 之后 \r\n\t%s",
						key, pos, Thread.currentThread().getStackTrace()[1]);
			}
		}
		
		JsonObject jo0 = super.getParameterOrCreate(key);
		jo0.putAll(jo);
	}
}
