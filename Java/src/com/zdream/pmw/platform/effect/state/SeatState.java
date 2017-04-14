package com.zdream.pmw.platform.effect.state;import com.zdream.pmw.platform.attend.IStateContainer;
import com.zdream.pmw.platform.attend.IStateInterceptable;
import com.zdream.pmw.platform.control.IPrintLevel;
import com.zdream.pmw.platform.effect.Aperitif;
import com.zdream.pmw.platform.prototype.BattlePlatform;

/**
 * <p>座位状态</p>
 * <p>一个座位建立就会带有该状态. 该状态会默认添加、一直保留到战斗结束.<br>
 * 和 {@link ExistState} 类似, 它的作用也是用于去重</p>
 * 
 * @since v0.2.2
 *   [2017-04-14]
 * @author Zdream
 * @version v0.2.2
 *   [2017-04-14]
 */
public class SeatState extends ASeatState {

	@Override
	public String name() {
		return "seat";
	}

	@Override
	public boolean canExecute(String msg) {
		if (CODE_CALC_ADDITION_RATE.equals(msg)) {
			return true;
		}
		return false;
	}
	
	@Override
	public String execute(Aperitif value, IStateInterceptable interceptor, BattlePlatform pf) {
		// CODE_CALC_ADDITION_RATE
		// 判断部分
		// 防御方限定
		byte seat = (byte) value.get("target");
		if (seat != this.seat) {
			return interceptor.nextState();
		}
		
		// 只处理状态
		if (!"state".equals(value.get("type")) && !"abnormal".equals(value.get("type"))) {
			return interceptor.nextState();
		}
		
		// 如果是删除状态, 则不处理 v0.2.2
		Object rm = value.get("remove");
		if (rm != null) {
			if ((boolean) rm) {
				return interceptor.nextState();
			}
		}

		// 处理部分
		if (!value.hasFilter("repeat") && handleRepeat(value, pf)) {
			// 不能施加状态
			value.append("reason", "repeat").replace("result", 1);
			pf.logPrintf(IPrintLevel.PRINT_LEVEL_INFO,
					"ExistState.calcAdditionRate(3) 由于怪兽 %s(seat=%d) 已经有 %s 状态, 不能再施加",
					pf.getAttendManager().getParticipant(seat).getNickname(), seat, name());
			return null;
		}
		
		return interceptor.nextState();
	}

	@Override
	public int priority(String msg) {
		return 0;
	}
	
	private boolean handleRepeat(Aperitif value, BattlePlatform pf) {
		// 获取状态的分类
		Object o = value.get("state-category");
		if (o == null) {
			o = value.get("state");
		}
		String stateCategory = o.toString();
		
		IStateContainer c = pf.getAttendManager().getSeatStates(seat);
		if (c.hasCategoryState(stateCategory)) {
			return true;
		}
		
		return false;
	}
	
	public SeatState(byte seat) {
		super(seat);
	}
	
	@Override
	public String toString() {
		return name() + ":" + seat;
	}

}
