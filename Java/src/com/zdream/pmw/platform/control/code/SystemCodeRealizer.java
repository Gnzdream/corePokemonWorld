package com.zdream.pmw.platform.control.code;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.zdream.pmw.platform.attend.AttendManager;
import com.zdream.pmw.platform.control.ICodeRealizer;
import com.zdream.pmw.platform.effect.Aperitif;
import com.zdream.pmw.platform.effect.state.FaintingProtectedState;
import com.zdream.pmw.platform.order.OrderManager;
import com.zdream.pmw.platform.order.event.FaintingExeuntEvent;
import com.zdream.pmw.platform.prototype.BattlePlatform;
import com.zdream.pmw.util.json.JsonValue;

/**
 * 系统的实现消息编辑与行动实现类<br>
 * <br>
 * v0.1.1 添加了回合结束的消息头与对应的实现方法<br>
 * <br>
 * <b>v0.2</b><br>
 *   删除了对 platform.instance 的直接引用<br>
 * 
 * @since v0.1.1
 * @author Zdream
 * @date 2016年4月19日
 * @version v0.2
 */
public class SystemCodeRealizer implements ICodeRealizer{
	
	private BattlePlatform pf;
	
	/* ************
	 *	实现方法  *
	 ************ */

	@Override
	public String[] codes() {
		return new String[] {
				CODE_ENTRANCE, CODE_PP_SUB, CODE_BROADCAST,
				CODE_SKILL_DAMAGE, CODE_EXEUNT_FAINT, CODE_RECOVER,
				CODE_ROUND_END, CODE_NONMOVES_DAMAGE, CODE_CONFIRM_SKILL,
				CODE_EXEUNT_EXCHANGE, CODE_RELEASE_SKILL
				};
	}

	@Override
	public void realize(String[] codes, BattlePlatform pf) {
		this.pf = pf;
		String code = codes[0];
		if (CODE_ENTRANCE.equals(code)) {
			entrance(codes);
		} else if (CODE_PP_SUB.equals(code)) {
			ppSub(codes);
		} else if (CODE_SKILL_DAMAGE.equals(code)) {
			skillDamage(codes);
		} else if (CODE_EXEUNT_FAINT.equals(code)) {
			exeunt(codes);
		} else if (CODE_RECOVER.equals(code)) {
			recover(codes);
		} else if (CODE_ROUND_END.equals(code)) {
			roundEnd(codes);
		} else if (CODE_NONMOVES_DAMAGE.equals(code)) {
			nonMovesDamage(codes);
		} else if (CODE_EXEUNT_EXCHANGE.equals(code)) {
			exeunt(codes);
		}
		
		// CODE_BROADCAST, CODE_RELEASE_SKILL:
		// do nothing
	}

	@Override
	public String commandLine(Aperitif value, BattlePlatform pf) {
		this.pf = pf;
		String code = value.getHead();
		if (CODE_ENTRANCE.equals(code)) {
			return entranceCommandLine(value);
		} else if (CODE_PP_SUB.equals(code)) {
			return ppSubCommandLine(value);
		} else if (CODE_BROADCAST.equals(code)) {
			return broadcastCommandLine(value);
		} else if (CODE_SKILL_DAMAGE.equals(code)) {
			return skillDamageCommandLine(value);
		} else if (CODE_EXEUNT_FAINT.equals(code)) {
			return exeuntFaintCommandLine(value);
		} else if (CODE_ROUND_END.equals(code)) {
			return roundEndCommandLine(value);
		} else if (CODE_NONMOVES_DAMAGE.equals(code)) {
			return nonMovesDamageCommandLine(value);
		} else if (CODE_RELEASE_SKILL.equals(code)) {
			return releaseSkillCommandLine(value);
		} else if (CODE_EXEUNT_EXCHANGE.equals(code)) {
			return exeuntExchangeCommandLine(value);
		}
		
		// CODE_CONFIRM_SKILL:
		// no command line
		
		return null;
	}

	/**
	 * 入场实现
	 */
	private void entrance(String[] codes) {
		pf.getAttendManager().entrance(
				Byte.valueOf(codes[2]), Byte.valueOf(codes[3]));
	}
	
	/**
	 * 入场指令
	 * @param value
	 * @return
	 */
	private String entranceCommandLine(JsonValue value) {
		Map<String, JsonValue> map = value.getMap();
		return String.format("%s %d %d %d", CODE_ENTRANCE, 
				map.get("team").getValue(),
				map.get("no").getValue(),
				map.get("seat").getValue());
	}
	
	/**
	 * 发动技能扣 PP 实现<br>
	 * 没有返回显示的文字
	 */
	private void ppSub(String[] codes) {
		AttendManager am = pf.getAttendManager();
		am.ppFail(
				Byte.parseByte(codes[1]), Byte.parseByte(codes[2]), Byte.parseByte(codes[3]));
	}
	
	/**
	 * 发动技能扣 PP 指令
	 * @param value
	 * @return
	 */
	private String ppSubCommandLine(JsonValue value) {
		Map<String, JsonValue> map = value.getMap();
		return String.format("%s %d %d %d", CODE_PP_SUB, 
				map.get("seat").getValue(),
				map.get("skillNum").getValue(),
				map.get("value").getValue());
	}
	
	/**
	 * 广播指令
	 * @param value
	 * @return
	 */
	private String broadcastCommandLine(Aperitif value) {
		StringBuilder builder = new StringBuilder(40);
		builder.append(CODE_BROADCAST).append(' ').append(value.get("type"));
		
		Map<String, JsonValue> map = value.getMap();
		// 所有 map 中以 '-' 开头的 key 都是要的
		for (Iterator<Entry<String, JsonValue>> it = map.entrySet().iterator(); it.hasNext();) {
			Entry<String, JsonValue> entry = it.next();
			String key = entry.getKey();
			if (key.startsWith("-") && !Character.isDigit(key.charAt(1))) {
				builder.append(' ').append(entry.getKey()).append(' ').append(entry.getValue().getValue());
			}
		}
		
		return builder.toString();
	}
	
	/**
	 * 发动技能伤害实现<br>
	 */
	private void skillDamage(String[] codes) {
		byte seat = Byte.parseByte(codes[1]);
		short value = Short.parseShort(codes[2]);
		AttendManager am = pf.getAttendManager();
		am.hpFail(seat, value);
		
		// 检测是否挂了
		if (am.getParticipant(seat).getAttendant().getHpi() == 0) {
			// seat 座位上的精灵已经挂了
			am.getParticipant(seat).pushState(new FaintingProtectedState(seat), pf);
			pf.getOrderManager().pushEvent(new FaintingExeuntEvent(seat));
		}
	}
	
	/**
	 * 发动技能伤害实现指令<br>
	 * @param value
	 * @return
	 */
	private String skillDamageCommandLine(Aperitif value) {
		StringBuilder builder = new StringBuilder();
		
		builder.append(CODE_SKILL_DAMAGE).append(' ')
			.append(value.get("seat")).append(' ')
			.append(value.get("value")).append(' ')
			.append(value.get("ctable")).append(' ')
			.append(value.get("effect"));
		Object o = value.get("reason");
		if (o != null) {
			builder.append(' ').append(o);
		}
		
		return builder.toString();
	}
	
	/**
	 * 处于濒死状态或交换下场等原因
	 * 而退场的实现
	 * @param codes
	 */
	private void exeunt(String[] codes) {
		AttendManager am = pf.getAttendManager();
		OrderManager om = pf.getOrderManager();
		byte seat = Byte.valueOf(codes[2]);
		byte no = am.noForSeat(seat);
		
		om.removeMoveEvent(no);
		am.exeunt(seat);
	}
	
	/**
	 * 处于濒死状态而退场的指令
	 * @param value
	 * @return
	 */
	private String exeuntFaintCommandLine(JsonValue value) {
		Map<String, JsonValue> map = value.getMap();
		return String.format("%s %d %d", CODE_EXEUNT_FAINT,
				map.get("team").getValue(),
				map.get("seat").getValue());
	}

	/**
	 * 加血
	 * @param codes
	 */
	private void recover(String[] codes) {
		if (codes[1].equals("-no")) {
			// TODO 为不在场的怪兽加血
		} else {
			byte seat = Byte.parseByte(codes[1]);
			int num = Integer.parseInt(codes[2]);
			pf.getAttendManager().hpRecover(seat, num);
		}
	}

	/**
	 * 回合结束、设置下一回合的指令
	 * @param value
	 * @return
	 */
	private String roundEndCommandLine(JsonValue value) {
		return String.format("%s %d", CODE_ROUND_END,
				pf.getOrderManager().getRound() + 1);
	}

	/**
	 * 由于非技能收到伤害的实现
	 * @param codes
	 */
	private void roundEnd(String[] codes) {
		pf.getOrderManager().roundCount();
	}

	/**
	 * 由于非技能收到伤害的指令
	 * @param value
	 * @return
	 */
	private String nonMovesDamageCommandLine(JsonValue value) {
		Map<String, JsonValue> map = value.getMap();
		return String.format("%s %d %d %s", CODE_NONMOVES_DAMAGE,
				map.get("seat").getValue(),
				map.get("hp").getValue(),
				map.get("reason").getValue());
	}

	/**
	 * 由于非技能收到伤害的实现
	 * @param codes
	 */
	private void nonMovesDamage(String[] codes) {
		byte seat = Byte.parseByte(codes[1]);
		int hp = Integer.parseInt(codes[2]);
		AttendManager am = pf.getAttendManager();
		am.hpFail(seat, hp);
		
		// 检测是否挂了
		if (am.getParticipant(seat).getAttendant().getHpi() == 0) {
			// seat 座位上的精灵已经挂了
			am.getParticipant(seat).pushState(new FaintingProtectedState(seat), pf);
			pf.getOrderManager().pushEvent(new FaintingExeuntEvent(seat));
		}
	}

	/**
	 * 技能释放的指令
	 * @param value
	 * @return
	 */
	private String releaseSkillCommandLine(JsonValue value) {
		Map<String, JsonValue> map = value.getMap();
		return String.format("%s %d %d", CODE_RELEASE_SKILL,
				map.get("seat").getValue(),
				map.get("skillID").getValue());
	}

	/**
	 * @param value
	 * @return
	 */
	private String exeuntExchangeCommandLine(JsonValue value) {
		Map<String, JsonValue> map = value.getMap();
		return String.format("%s %d %d", CODE_EXEUNT_EXCHANGE,
				map.get("team").getValue(),
				map.get("seat").getValue());
	}
	
	/* ************
	 *	 构造器   *
	 ************ */

	public SystemCodeRealizer() {
		
	}

}
