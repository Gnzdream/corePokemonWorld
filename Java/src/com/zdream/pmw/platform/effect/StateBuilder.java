package com.zdream.pmw.platform.effect;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.zdream.pmw.platform.attend.IState;
import com.zdream.pmw.platform.attend.StateHandler;
import com.zdream.pmw.platform.attend.service.EStateSource;
import com.zdream.pmw.platform.effect.state.ASeatState;
import com.zdream.pmw.platform.effect.state.AParticipantState;
import com.zdream.pmw.util.json.JsonBuilder;
import com.zdream.pmw.util.json.JsonValue;

/**
 * <p>状态生成器, 控制层</p>
 * 
 * <p><b>v0.2.2</b><br>
 * 从该版本开始将状态生成的实现层全部搬到
 * {@link com.zdream.pmw.platform.attend.StateHandler}</p>
 * 
 * @since v0.2.1
 *   [2017-03-02]
 * @author Zdream
 * @version v0.2.2
 *   [2017-04-09]
 */
public class StateBuilder {

	/* ************
	 *	  关联    *
	 ************ */
	
	StateHandler stateHandler;
	
	/* ************
	 *	公共方法  *
	 ************ */

	/**
	 * <p>发送施加状态拦截前消息, 控制层</p>
	 * <p>该方法为攻击方释放技能后, 为防御方施加状态.</p>
	 * @param args
	 *   原本存放于 skill 中的状态启动需要的静态数据
	 * @param pack
	 */
	@Deprecated
	public void sendForceStateMessage(final JsonValue args, SkillReleasePackage pack) {
		// 导入 pack 数据
		Aperitif value = pipePackage(pack);
		
		// 根据每个状态的需要, 从 args 中拿到需要的数据, 向 value 中放入
		putArgument(value, args);
		
		em.startCode(value);
	}

	/**
	 * <p>发送施加状态拦截前消息, 控制层</p>
	 * <p>此为由于技能释放的需要而的施加参与怪兽附属的状态 ({@link AParticipantState}) 方法</p>
	 * <p>该方法在进入实现层之前先要进行能否产生状态的判断</p>
	 * @param stateName
	 *   指定的状态名, {@link IState#name()}
	 * @param atseat
	 *   施加方 (攻击方) seat
	 * @param dfseat
	 *   被施加方 (不一定为防御方) seat
	 * @param skillID
	 *   技能 ID
	 * @param param
	 *   其它的附加参数.<br>
	 *   所有以 "-" 开头的数据都将被写入到命令数据中
	 */
	public void sendForceStateMessage(final String stateName,
			final byte atseat,
			final byte dfseat,
			final short skillID,
			final JsonValue param) {
		
		Aperitif ap = em.newAperitif(Aperitif.CODE_CALC_ADDITION_RATE, atseat, dfseat);
		ap.append("atseat", atseat).append("dfseat", dfseat).append("target", dfseat)
				.append("type", "state").append("state", stateName).append("result", 0);
		em.startCode(ap);
		
		int result = (int) ap.get("result");
		if (result != 0) {
			em.logPrintf(EffectManage.PRINT_LEVEL_INFO, "at: %s\r\n\t由于原因 %s, 将取消向 %s(seat=%d) 施加 %s 状态",
					Thread.currentThread().getStackTrace()[1],
					ap.get("reason"), em.getAttends().getParticipant(dfseat).getNickname(),
					dfseat, stateName);
			return;
		}
		
		// 正式施加
		ap = em.newAperitif(Aperitif.CODE_FORCE_STATE, atseat, dfseat);
		ap.append("atseat", atseat).append("dfseat", dfseat).append("state", stateName)
				.append("no", em.getAttends().noForSeat(dfseat))
				.append("source", EStateSource.SKILL.name())
				.append("skillID", skillID);
		
		for (Iterator<Entry<String, JsonValue>> it = param.getMap().entrySet().iterator(); it.hasNext();) {
			Entry<String, JsonValue> entry = it.next();
			String key = entry.getKey();
			if (key.startsWith("-") && !Character.isDigit(key.charAt(1))) {
				ap.add(key, entry.getValue());
			}
		}
		
		em.startCode(ap);
	}

	/**
	 * 发送清除状态的拦截前消息<br>
	 * <p>清除指定怪兽的状态</p>
	 * @param stateName
	 *   状态名
	 * @param no
	 *   指定怪兽的 no 号
	 */
	public void sendRemoveParticipantStateMessage(final String stateName, final byte no) {
		byte seat = em.getAttends().seatForNo(no);
		
		// 导入 pack 数据
		Aperitif value = em.newAperitif(Aperitif.CODE_REMOVE_STATE, seat);
		value.append("seat", seat).append("state", stateName);
		
		em.startCode(value);
	}

	/**
	 * 发送清除状态的拦截前消息<br>
	 * <p>清除指定怪兽的状态</p>
	 * @param stateName
	 *   状态名
	 * @param seat
	 *   指定座位号
	 */
	public void sendRemoveSeatStateMessage(final String stateName, final byte seat) {
		// 导入 pack 数据
		Aperitif value = em.newAperitif(Aperitif.CODE_REMOVE_STATE, seat);
		value.append("seat", seat).append("state", stateName);
		
		em.startCode(value);
	}
	
	/**
	 * 获得施加状态的命令行类型消息<br>
	 * 调用方是 {@code com.zdream.pmw.platform.control.codeStateCodeRealizer}<br>
	 * @param value
	 * @return
	 */
	public String forceStateCommandLine(Aperitif value) {
		Map<String, JsonValue> map = value.getMap();
		String statestr = map.get("state").getString();

		Class<?> clazz = stateHandler.stateClass(statestr);
		
		if (!IStateMessageFormater.class.isAssignableFrom(clazz)) {
			// 默认的生成方式
			StringBuilder cmd = new StringBuilder(40);
			if (AParticipantState.class.isAssignableFrom(clazz)) {
				// force-state confusion -no 1 -source SKILL -skillID 60
				cmd.append("force-state").append(' ').append(statestr).append(' ')
						.append("-no").append(' ').append(map.get("no").getValue()).append(' ')
						.append("-source").append(' ')
						.append(map.get("source").getString()).append(' ')
						.append("-skillID").append(' ')
						.append(map.get("skillID").getValue());
			} else if (ASeatState.class.isAssignableFrom(clazz)) {
				// force-state mist -seat 1 -source SKILL -skillID 54
				cmd.append("force-state").append(' ').append(statestr).append(' ')
						.append("-seat").append(' ').append(map.get("dfseat").getValue()).append(' ')
						.append("-source").append(' ')
						.append(map.get("source").getString()).append(' ')
						.append("-skillID").append(' ')
						.append(map.get("skillID").getValue());
			} else {
				// 其它
				throw new IllegalArgumentException("对象不是怪兽本体、不是座位的状态现在还没写呢！");
			}
			
			// 将所有以 "-" 开头的参数进行转化
			for (Iterator<Entry<String, JsonValue>> it = map.entrySet().iterator(); it.hasNext();) {
				Entry<String, JsonValue> entry = it.next();
				String key = entry.getKey();
				JsonBuilder b = null;
				if (key.startsWith("-") && !Character.isDigit(key.charAt(1))) {
					cmd.append(' ').append(key).append(' ');
					JsonValue v = entry.getValue();
					switch (v.getType()) {
					case ObjectType: case ArrayType:
						if (b == null) {
							b = new JsonBuilder();
						}
						cmd.append(b.getJson(v));
						break;
					default:
						cmd.append(v.getValue());
						break;
					}
				}
			}
			
			return cmd.toString();
			
		} else {
			try {
				IStateMessageFormater formater = (IStateMessageFormater) clazz.newInstance();
				return formater.forceCommandLine(value, em.getRoot());
			} catch (InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		
		return "";
	}
	
	/* ************
	 *	私有方法  *
	 ************ */
	
	/**
	 * 利用 pack 生成 json 拦截前消息数据
	 * @param pack
	 * @return
	 */
	private Aperitif pipePackage(SkillReleasePackage pack) {
		int thiz = pack.getThiz();
		byte dfseat = pack.getDfStaff(thiz).getSeat();
		
		Aperitif ap = em.newAperitif(Aperitif.CODE_FORCE_STATE, dfseat);
		ap.append("atseat", pack.getAtStaff().getSeat())
				.append("dfseat", dfseat)
				.append("skillID", pack.getSkill().getId()) // short
				.append("power", pack.getPower(thiz)) // int
				.append("damage", pack.getDamage(thiz)); // int

		// TODO 不确定这些参数是放在这个函数中的
		ap.append("source", EStateSource.SKILL.name()); // string
		
		return ap;
	}
	
	/**
	 * 根据状态和 args 中对状态的介绍, 生成 value<br>
	 * @param value
	 * @param args
	 */
	private void putArgument(JsonValue value, final JsonValue args) {
		Map<String, JsonValue> map = value.getMap();
		String statestr = args.getMap().get("v").getString(); // 格式: xxx
		map.put("state", new JsonValue(statestr));
		
		Class<?> clazz = stateHandler.stateClass(statestr);
		
		if (!IStateMessageFormater.class.isAssignableFrom(clazz)) {
			// 默认的生成方式
			if (AParticipantState.class.isAssignableFrom(clazz)) {
				// no
				byte dfseat = (Byte) map.get("dfseat").getValue();
				map.put("no", new JsonValue(em.getRoot().getAttendManager().noForSeat(dfseat)));
				// 
			} else {
				// 对象不是怪兽本体的状态
			}

			// 将所有以 "-" 开头的数据放入参数中
			for (Iterator<Entry<String, JsonValue>> it = args.getMap().entrySet().iterator(); it.hasNext();) {
				Entry<String, JsonValue> entry = it.next();
				String key = entry.getKey();
				if (key.startsWith("-") && !Character.isDigit(key.charAt(1))) {
					map.put(key, entry.getValue());
				}
			}
		} else {
			// IStateMessageFormater formater = (IStateMessageFormater)
			// clazz.newInstance();
			// TODO, 用 IStateMessageFormater 的方法处理 value(map)
		}
	}

	/* ************
	 *	 初始化   *
	 ************ */
	
	EffectManage em;

	public StateBuilder(EffectManage em, StateHandler stateHandler) {
		this.em = em;
		this.stateHandler = stateHandler;
	}
}
