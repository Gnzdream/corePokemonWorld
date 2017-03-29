package com.zdream.pmw.platform.translate.template;

import java.util.Map;

import com.zdream.pmw.platform.prototype.BattlePlatform;

/**
 * 状态和异常状态相关的模板选择<br>
 * 
 * @since v0.2.1
 * @author Zdream
 * @date 2017年3月26日
 * @version v0.2.1
 */
public class StateTemplateChooser implements ITemplateChooser {
	
	static final String H_FORCE = "force-state",
			H_SET = "state-set",
			H_REMOVE = "remove-state";

	@Override
	public String[] canHandle() {
		return new String[]{
			H_FORCE, H_SET, H_REMOVE
		};
	}

	@Override
	public String[] choose(String head, Map<String, String> context, BattlePlatform pf) {
		switch (head) {
		case H_FORCE:
			return forceState(context, pf);
		case H_SET:
			context.put("/mute", Boolean.toString(true));
			return null;
		case H_REMOVE:
			return removeState(context, pf);

		default:
			break;
		}
		return null;
	}

	/**
	 * 状态施加模板选择
	 * @param context
	 * @param pf
	 * @return
	 */
	private String[] forceState(Map<String, String> context, BattlePlatform pf) {
		String stateName = context.get("state.name");
		switch (stateName) {
		case "confusion":
			return new String[]{"force-state.confusion.default"};

		case "bound":
			return new String[]{"force-state.bound.default"};

		case "flinch":
			context.put("/mute", Boolean.toString(true));
			return new String[]{"force-state.flinch.default"};
		}
		
		return null;
	}

	/**
	 * 状态移除模板选择
	 * @param context
	 * @param pf
	 * @return
	 */
	private String[] removeState(Map<String, String> context, BattlePlatform pf) {
		String stateName = context.get("state.name");
		switch (stateName) {
		case "confusion":
			return new String[]{"remove-state.confusion.default"};

		case "bound":
			return new String[]{"remove-state.bound.default"};

		case "flinch":
			context.put("/mute", Boolean.toString(true));
			return new String[]{"remove-state.flinch.default"};
		}
		
		return null;
	}

}
