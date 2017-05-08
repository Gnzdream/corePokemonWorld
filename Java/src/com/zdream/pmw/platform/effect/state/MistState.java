package com.zdream.pmw.platform.effect.state;

import com.zdream.pmw.platform.attend.IStateInterceptable;
import com.zdream.pmw.platform.effect.Aperitif;
import com.zdream.pmw.platform.prototype.BattlePlatform;
import com.zdream.pmw.util.json.JsonObject;

/**
 * <p>白雾状态</p>
 * <p>由白雾技能进行触发,
 * 处于白雾状态的场地上的怪兽的能力等级不会被来自对方的效果降低.<br>
 * 经过 5 回合后白雾状态消失</p>
 * 
 * @since v0.2.2
 *   [2017-04-14]
 * @author Zdream
 * @version v0.2.2
 *   [2017-04-14]
 */
public class MistState extends ASeatState implements IDuration {

	/* ************
	 *	  属性    *
	 ************ */
	
	/**
	 * 白雾状态将持续的时间
	 */
	private int round;
	
	@Override
	public int getRound() {
		return round;
	}
	
	@Override
	public void reduceRound(int num) {
		round -= num;
		if (round < 0) {
			round = 0;
		}
	}

	/* ************
	 *	实现方法  *
	 ************ */
	
	@Override
	public void set(JsonObject v, BattlePlatform pf) {
		IDuration.super.set(v, pf);
	}

	@Override
	public String name() {
		return "mist";
	}

	@Override
	public boolean canExecute(String msg) {
		switch (msg) {
		case CODE_CALC_ADDITION_RATE: case CODE_ROUND_END:
			return true;

		default:
			return false;
		}
	}
	
	@Override
	public String execute(Aperitif ap, IStateInterceptable interceptor, BattlePlatform pf) {
		switch (ap.getHead()) {
		case CODE_CALC_ADDITION_RATE:
			return calcAdditionRate(ap, interceptor, pf);
		case CODE_ROUND_END:
			return roundEnd(ap, interceptor, pf);
		}
		return super.execute(ap, interceptor, pf);
	}

	@Override
	public int priority(String msg) {
		if (CODE_CALC_ADDITION_RATE.equals(msg)) {
			return 5;
		}
		return 0;
	}

	/* ************
	 *	私有方法  *
	 ************ */
	
	private String calcAdditionRate(Aperitif ap, IStateInterceptable interceptor, BattlePlatform pf) {
		// 判断防御方
		byte dfseat = (byte) ap.get("target");
		if (seat != dfseat) {
			return interceptor.nextState();
		}
		
		// 判断攻击方: 如果攻方等于防方, 也就是自己导致的能力改变, 将忽略
		byte atseat = (byte) ap.get("atseat");
		if (dfseat == atseat) {
			return interceptor.nextState();
		}
		
		// 判断是否为能力变化 (不是设置)
		if (!"abilityLevel".equals(ap.get("type")) || !"-c".equals(ap.get("param"))) {
			return interceptor.nextState();
		}
		
		// 操作
		int[] items = (int[]) ap.get("items"),
				values = (int[]) ap.get("values");
		int len = 0;
		for (int i = 0; i < values.length; i++) {
			if (values[i] > 0) {
				len++;
			}
		}
		if (len > 0) {
			int[] titems = new int[len], tvalues = new int[len];
			int tidx = 0;
			for (int i = 0; i < values.length; i++) {
				if (values[i] > 0) {
					titems[tidx] = items[i];
					tvalues[tidx] = values[i];
					tidx++;
				}
			}
			ap.replace("items", titems);
			ap.replace("values", tvalues);
		} else {
			ap.replace("items", new int[]{});
			ap.replace("values", new int[]{});
		}
		ap.append("reason", name());
		ap.replace("result", 2);
		
		return interceptor.nextState();
	}
	
	private String roundEnd(Aperitif ap, IStateInterceptable interceptor, BattlePlatform pf) {
		
		if (round == 0) {
			pf.getEffectManage().sendRemoveSeatStateMessage(name(), seat);
		} else {
			Aperitif ap0 = pf.getEffectManage().newAperitif(CODE_STATE_SET, seat);
			ap0.append("-seat", seat).append("-reduce", 1).append("state", name());
			pf.getRoot().readyCode(ap0);
		}
		
		return interceptor.nextState();
	}

	/* ************
	 *	 初始化   *
	 ************ */
	
	public MistState() {
		// 默认的持续回合数为 5
		round = 5;
	}
	
	@Override
	public String toString() {
		return name() + ":" + round;
	}

}
