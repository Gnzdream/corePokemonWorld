package com.zdream.pmw.platform.translate.translater;

import java.util.Map;

import com.zdream.pmw.monster.prototype.EPokemonAbnormal;
import com.zdream.pmw.platform.attend.Attendant;
import com.zdream.pmw.platform.attend.Participant;
import com.zdream.pmw.platform.common.AbnormalMethods;
import com.zdream.pmw.platform.prototype.BattlePlatform;
import com.zdream.pmw.platform.translate.MessageRepository;

/**
 * 状态和异常状态相关的数据<br>
 * 
 * @since v0.2.1
 * @author Zdream
 * @date 2017年3月26日
 * @version v0.2.1
 */
public class StateTranslate implements ITranslate {
	
	static final String H_AB_F_DESC = "abnormal.force.desc";
	static final String H_ST_F_DESC = "force.desc";

	@Override
	public String[] canHandle() {
		return new String[] {
			H_AB_F_DESC
		};
	}

	@Override
	public String translate(String handle, Map<String, String> param,
			BattlePlatform pf, MessageRepository repository) {
		switch (handle) {
		case H_AB_F_DESC:
			return abnormalForceDesc(param, pf, repository);

		default:
			break;
		}
		return null;
	}
	
	private String abnormalForceDesc(Map<String, String> context,
			BattlePlatform pf, MessageRepository repository) {
		// 1. 确定是哪个异常状态
		EPokemonAbnormal ab = null;
		String str = context.get("abnormal.code");
		if (str != null) {
			ab = AbnormalMethods.parseAbnormal(Byte.parseByte(str));
		}
		
		// 2. 翻译
		if (ab == null) {
			return H_AB_F_DESC;
		}
		
		if (ab == EPokemonAbnormal.NONE) {
			// 3. 确定这个怪兽以前的异常状态
			EPokemonAbnormal rab = getRecentAbnormal(context, pf);
			switch (rab) {
			case SLEEP: case FREEZE:
				return repository.getTemplate("abnormal.remove.desc." + rab.name());

			default:
				return repository.getTemplate("abnormal.remove.desc.default");
			}
		}
		
		return repository.getTemplate(H_AB_F_DESC + "." + ab.name());
	}
	
	/**
	 * 确定指定怪兽以前的异常状态
	 */
	private EPokemonAbnormal getRecentAbnormal(Map<String, String> context,
			BattlePlatform pf) {
		String seatstr = context.get(ParticipantTranslate.K_SEAT);
		if (seatstr != null) {
			Participant p = pf.getAttendManager().getParticipant(Byte.parseByte(seatstr));
			if (p != null) {
				return AbnormalMethods.parseAbnormal(p.getAttendant().getAbnormal());
			}
		}
		
		String nostr = context.get(ParticipantTranslate.K_NO);
		if (nostr != null) {
			Attendant att = pf.getAttendManager().getAttendant(Byte.parseByte(nostr));
			if (att != null) {
				return AbnormalMethods.parseAbnormal(att.getAbnormal());
			}
		}
		return null;
	}

}
