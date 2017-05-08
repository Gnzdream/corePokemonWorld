package com.zdream.pmw.platform.effect.instruction;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.zdream.pmw.platform.effect.AInstruction;
import com.zdream.pmw.platform.effect.Aperitif;
import com.zdream.pmw.util.json.JsonObject;
import com.zdream.pmw.util.json.JsonValue;
import com.zdream.pmw.util.json.JsonValue.JsonType;

/**
 * <p>广播消息的指示</p>
 * 
 * @since v0.2.3 [2017-05-08]
 * @author Zdream
 * @version v0.2.3 [2017-05-08]
 */
public class BroadcastInstruction extends AInstruction {
	
	JsonObject args;

	@Override
	public String name() {
		return CODE_BROADCAST;
	}
	
	@Override
	public void set(JsonObject args) {
		this.args = args;
	}
	
	@Override
	public void restore() {
		args = null;
	}

	@Override
	protected void execute() {
		if (args == null) {
			return;
		}
		
		Aperitif ap = em.newAperitif(CODE_BROADCAST);
		Map<String, JsonValue> map = args.asMap();
		for (Iterator<Entry<String, JsonValue>> it = map.entrySet().iterator(); it.hasNext();) {
			Entry<String, JsonValue> entry = it.next();
			
			String key = entry.getKey();
			if (key.startsWith("_")) {
				continue;
			}
			
			JsonValue v = entry.getValue();
			if (v.getType() == JsonType.ArrayType || v.getType() == JsonType.ObjectType) {
				ap.append(key, v);
			} else {
				ap.append(key, v.getValue());
			}
		}

		em.startCode(ap);
	}
}
