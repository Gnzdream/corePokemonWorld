package com.zdream.pmw.platform.translate;

import java.util.Map;

import com.zdream.pmw.platform.attend.Attendant;
import com.zdream.pmw.platform.attend.Participant;
import com.zdream.pmw.platform.prototype.BattlePlatform;

/**
 * 翻译精灵相关的数据<br>
 * 
 * @since v0.2
 * @author Zdream
 * @date 2017年2月26日
 * @version v0.2
 */
public class ParticipantTranslate implements ITranslate {
	
	static final String H_NAME = "pm.name";
	
	static final String 
			K_SEAT = "pm.seat",
			K_NO = "pm.no",
			K_TEAM = "pm.team";

	@Override
	public String[] canHandle() {
		return new String[]{H_NAME};
	}

	@Override
	public String translate(String handle, Map<String, String> map, BattlePlatform pf) {
		switch (handle) {
		case H_NAME: {
			String seatstr = map.get(K_SEAT);
			if (seatstr != null) {
				Participant p = pf.getAttendManager().getParticipant(Byte.parseByte(seatstr));
				if (p != null) {
					return p.getAttendant().getNickname();
				}
			}
			
			String nostr = map.get(K_NO);
			if (nostr != null) {
				Attendant att = pf.getAttendManager().getAttendant(Byte.parseByte(nostr));
				if (att != null) {
					return att.getNickname();
				}
			}
		} break;

		default:
			break;
		}
		return null;
	}

}
