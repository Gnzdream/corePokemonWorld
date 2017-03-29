package com.zdream.pmw.platform.translate.template;

import java.util.Map;

import com.zdream.pmw.platform.prototype.BattlePlatform;

/**
 * 广播相关的模板选择<br>
 * 
 * @since v0.2.1
 * @author Zdream
 * @date 2017年3月26日
 * @version v0.2.1
 */
public class BroadcastTemplateChooser implements ITemplateChooser {
	
	static final String H_BOARDCAST = "broadcast";

	@Override
	public String[] canHandle() {
		return new String[]{
			H_BOARDCAST
		};
	}

	@Override
	public String[] choose(String head, Map<String, String> context, BattlePlatform pf) {
		String reason = context.get("view.reason");
		switch (reason) {
		case "immune": case "miss": case "invalid":
			return new String[]{H_BOARDCAST + "." + reason};

		default:
			break;
		}
		
		
		return null;
	}

}
