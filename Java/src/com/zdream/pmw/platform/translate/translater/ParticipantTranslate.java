package com.zdream.pmw.platform.translate.translater;

import java.util.Map;

import com.zdream.pmw.platform.attend.Attendant;
import com.zdream.pmw.platform.attend.Participant;
import com.zdream.pmw.platform.prototype.BattlePlatform;
import com.zdream.pmw.platform.translate.MessageRepository;

/**
 * 翻译精灵相关的数据<br>
 * 
 * @since v0.2
 * @author Zdream
 * @date 2017年2月26日
 * @version v0.2
 */
public class ParticipantTranslate implements ITranslate {
	
	static final String
			H_NAME = "pm.name",
			H_AT_NAME = "damage.at.name",
			H_DF_NAME = "damage.df.name";
	
	static final String 
			K_SEAT = "pm.seat",
			K_NO = "pm.no",
			K_TEAM = "pm.team",
			K_AT_SEAT = "damage.atseat",
			K_DF_SEAT = "damage.dfseat";

	@Override
	public String[] canHandle() {
		return new String[]{H_NAME, H_AT_NAME, H_DF_NAME};
	}

	@Override
	public String translate(String handle, Map<String, String> map,
			BattlePlatform pf, MessageRepository repository) {
		switch (handle) {
		case H_NAME: {
			String seatstr = map.get(K_SEAT);
			if (seatstr != null) {
				String s = findNameBySeat(map.get(K_SEAT), pf);
				if (s != null) {
					return s;
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
		
		case H_AT_NAME: {
			String s = findNameBySeat(map.get(K_AT_SEAT), pf);
			if (s != null) {
				return s;
			}
		} break;
		
		case H_DF_NAME: {
			String s = findNameBySeat(map.get(K_DF_SEAT), pf);
			if (s != null) {
				return s;
			}
		} break;

		default:
			break;
		}
		return null;
	}
	
	private String findNameBySeat(String seatstr, BattlePlatform pf) {
		Participant p = pf.getAttendManager().getParticipant(Byte.parseByte(seatstr));
		if (p != null) {
			return p.getAttendant().getNickname();
		}
		return null;
	}

}
