package com.zdream.pmw.platform.prototype.context;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.zdream.pmw.monster.prototype.EPokemonAbnormal;
import com.zdream.pmw.platform.attend.IState;
import com.zdream.pmw.platform.attend.Participant;
import com.zdream.pmw.platform.effect.state.AAbnormalState;
import com.zdream.pmw.platform.effect.state.ASubState;
import com.zdream.pmw.platform.prototype.BattlePlatform;

/**
 * TODO
 * @since v0.2.2
 * @author Zdream
 * @date 2017年3月28日
 * @version v0.2.2
 */
public class DefaultPlatformContext implements IPlatformContext {

	@Override
	public ParticipantContext getParticipantContext(byte seat) {
		ParticipantContext ctx = new ParticipantContext();
		
		Participant p = pf.getAttendManager().getParticipant(seat);
		ctx.id = p.getAttendant().getIdentifier();
		ctx.form = p.getAttendant().getForm();
		ctx.name = p.getNickname();
		ctx.level = p.getAttendant().getLevel();
		ctx.gender = p.getAttendant().getGender();
		
		ctx.hpi = p.getHpi();
		ctx.hpmax = p.getStat(Participant.HP);
		
		int[] is = new int[8];
		for (int i = 1; i < is.length; i++) {
			is[i] = p.getAbilityLevel(i);
		}
		ctx.statLevel = is;
		
		List<IState> states = p.getStates();
		List<String> list = new ArrayList<>();
		for (Iterator<IState> it = states.iterator(); it.hasNext();) {
			IState state = it.next();
			if (state.name().equals("exist")) {
				continue;
			}
			if (state instanceof AAbnormalState) {
				ctx.abnormal = EPokemonAbnormal.parseEnum(state.name());
				continue;
			}
			if (state instanceof ASubState) {
				continue;
			}
			list.add(state.toString());
		}
		String[] as = new String[list.size()];
		ctx.states = list.toArray(as);
		
		if (ctx.abnormal == null) {
			ctx.abnormal = EPokemonAbnormal.NONE;
		}
		
		return ctx;
	}

	/* ************
	 *	 初始化   *
	 ************ */
	
	BattlePlatform pf;
	
	public DefaultPlatformContext(BattlePlatform pf) {
		this.pf = pf;
	}

}
