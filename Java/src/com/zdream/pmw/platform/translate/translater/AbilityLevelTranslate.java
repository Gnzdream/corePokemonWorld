package com.zdream.pmw.platform.translate.translater;

import java.util.Map;

import com.zdream.pmw.platform.attend.Participant;
import com.zdream.pmw.platform.common.ItemMethods;
import com.zdream.pmw.platform.prototype.BattlePlatform;
import com.zdream.pmw.platform.translate.MessageRepository;

/**
 * 能力变化相关的翻译<br>
 * 
 * @since v0.2.1
 * @author Zdream
 * @date 2017年3月25日
 * @version v0.2.1
 */
public class AbilityLevelTranslate implements ITranslate {

	@Override
	public String[] canHandle() {
		return new String[]{"addition.statChangeItem", "addition.statChangeRise"};
	}

	@Override
	public String translate(String handle, Map<String, String> param,
			BattlePlatform pf, MessageRepository repository) {
		boolean isRise = "addition.statChangeRise".equals(handle);
		String indexStr = param.get(handle + ".index");
		
		String result;
		if (isRise) {
			int i = Integer.parseInt(param.get("addition.statChangeValue." + indexStr));
			if (i < -3) {
				i = -3;
			} else if (i > 3) {
				i = 3;
			}
			
			if (i != 0) {
				result = repository.getTemplate("addition.statChangeValue." + i);
			} else {
				// 可能是无法提高或无法降低
				byte seat = Byte.parseByte(param.get("pm.seat"));
				int item = Integer.parseInt(param.get("pm.item." + i));
				byte curAbility = pf.getAttendManager().getParticipant(seat).getAbilityLevel(item);
				if (curAbility == Participant.LEVEL_UPPER_LIMIT) {
					result = repository.getTemplate("addition.statChangeValue.noup");
				} else if (curAbility == Participant.LEVEL_LOWER_LIMIT) {
					result = repository.getTemplate("addition.statChangeValue.nodown");
				} else {
					result = repository.getTemplate("addition.statChangeValue.nochange");
				}
			}
			param.remove("addition.statChangeItem"); // TODO 我觉得后面不应该用这么投机取巧的办法
		} else {
			int i = Integer.parseInt(param.get("pm.item." + indexStr));
			String s = ItemMethods.parseItemName(i);
			
			result = repository.getTemplate("pm.item." + s);
			param.remove("addition.statChangeRise"); // TODO 我觉得后面不应该用这么投机取巧的办法
		}
		
		param.put(handle + ".index", Integer.toString(Integer.parseInt(indexStr) + 1));
		return result;
	}

}
