package com.zdream.pmw.platform.translate.template;

import java.util.Map;

import com.zdream.pmw.platform.prototype.BattlePlatform;

/**
 * 能力升降模板选择器<br>
 * 
 * @since v0.2.1
 * @author Zdream
 * @date 2017年3月25日
 * @version v0.2.1
 */
public class AbilityLevelTemplateChooser implements ITemplateChooser {

	@Override
	public String[] canHandle() {
		return new String[]{"ability-level"};
	}

	@Override
	public String[] choose(String head, Map<String, String> context, BattlePlatform pf) {
		if ("ability-set".equals(context.get("damage.addition"))) {
			return null;
		}
		
		String[] itemStrs = context.get("pm.item").split(",");
		String[] valueStrs = context.get("addition.statChangeValue").split(",");
		final int length = itemStrs.length;
		
		for (int i = 0; i < length; i++) {
			context.put("pm.item." + i, itemStrs[i]);
			context.put("addition.statChangeValue." + i, valueStrs[i]);
			itemStrs[i] = "ability-level.change";
		}
		
		context.put("addition.statChangeItem.index", "0");
		context.put("addition.statChangeRise.index", "0");
		return itemStrs;
	}

}
