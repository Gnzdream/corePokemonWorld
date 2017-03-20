package com.zdream.pmw.platform.effect;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.zdream.pmw.platform.attend.AttendManager;
import com.zdream.pmw.platform.attend.IState;
import com.zdream.pmw.platform.attend.service.EStateSource;
import com.zdream.pmw.platform.control.IPrintLevel;
import com.zdream.pmw.platform.effect.state.ParticipantState;
import com.zdream.pmw.util.json.JsonValue;
import com.zdream.pmw.util.json.JsonValue.JsonType;

/**
 * 状态生成器<br>
 * 
 * @since v0.2.1
 * @author Zdream
 * @date 2017年3月2日
 * @version v0.2.1
 */
public class StateBuilder {

	/* ************
	 *	  属性    *
	 ************ */
	
	Properties pro;
	
	private static final String PREFIX_STATE = "state.";
	
	/**
	 * state 名称 (无前缀) : class
	 */
	private Map<String, Class<?>> clazzes;
	
	/* ************
	 *	公共方法  *
	 ************ */

	/**
	 * 工厂方式创建状态类<br>
	 * @param args
	 * @return
	 * @since v0.2.1
	 */
	public IState buildState(JsonValue args) {
		Map<String, JsonValue> map = args.getMap();
		String name = map.get("state").getString();
		
		IState state = buildState0(name);
		if (state == null) {
			return null;
		}
		
		state.set(args, em.getRoot());
		return state;
	}

	/**
	 * 发送施加状态拦截前消息<br>
	 * @param args
	 *   原本存放于 skill 中的状态启动需要的静态数据
	 * @param pack
	 */
	public void sendForceStateMessage(final JsonValue args, SkillReleasePackage pack) {
		// 导入 pack 数据
		Aperitif value = pipePackage(pack);
		
		// 根据每个状态的需要, 从 args 中拿到需要的数据, 向 value 中放入
		putArgument(value, args);
		
		pack.getEffects().startCode(value);
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
		value.append("no", no).append("state", stateName);
		
		em.startCode(value);
	}
	
	/**
	 * 获得施加状态的命令行类型消息<br>
	 * 调用方是 {@code com.zdream.pmw.platform.control.codeStateCodeRealizer}<br>
	 * @param value
	 * @return
	 */
	public String forceStateCommandLine(JsonValue value) {
		Map<String, JsonValue> map = value.getMap();
		String statestr = map.get("state").getString();

		Class<?> clazz = stateClass(statestr);
		
		if (!IStateMessageFormater.class.isAssignableFrom(clazz)) {
			// 默认的生成方式
			if (ParticipantState.class.isAssignableFrom(clazz)) {
				// force-state confusion -no 1 -source SKILL -skillID 60
				String cmd = String.format("force-state %s -no %d -source %s -skillID %d",
						statestr, map.get("no").getValue(),
						map.get("source").getString(), map.get("skillID").getValue());
				return cmd;
			} else {
				// 对象不是怪兽本体的状态
			}
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
	
	/**
	 * 按照施加状态的命令行数据, 完成施加状态的任务<br>
	 * 调用方是 {@code com.zdream.pmw.platform.control.codeStateCodeRealizer}<br>
	 * @param codes
	 */
	public void forceStateRealize(String[] codes) {
		String statestr = codes[1];

		Class<?> clazz = stateClass(statestr);
		JsonValue value = new JsonValue(JsonType.ObjectType);
		value.add("state", new JsonValue(statestr));
		
		if (!IStateMessageFormater.class.isAssignableFrom(clazz)) {
			// 默认的生成方式
			if (ParticipantState.class.isAssignableFrom(clazz)) {
				// force-state confusion -no 1 -source SKILL -skillID 60
				
				int argv = 0;
				String option = null;
				for (int i = 2; i < codes.length; i++) {
					String s = codes[i];
					
					if (s.startsWith("-")) {
						option = s.substring(1);
						argv = 0;
					} else {
						switch (option) {
						case "no":
							if (argv == 1) {
								value.add(option, new JsonValue(Byte.valueOf(s)));
							}
							break;
						case "skillID":
							if (argv == 1) {
								value.add(option, new JsonValue(Short.valueOf(s)));
							}
							break;
						case "source":
							if (argv == 1) {
								value.add(option, new JsonValue(s));
							}
							break;
						default:
							break;
						}
					}
					argv++;
				}
			} else {
				// 对象不是怪兽本体的状态
			}
		} else {
			IStateMessageFormater formater;
			try {
				formater = (IStateMessageFormater) clazz.newInstance();
				formater.forceRealize(codes, value, em.getRoot());
			} catch (InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		
		// 产生 state
		IState state = buildState(value);
		
		// 施加 state
		forceState(state);
	}
	
	/* ************
	 *	私有方法  *
	 ************ */
	
	/**
	 * @param name
	 *   格式 xxx (无前缀)
	 * @return
	 */
	private IState buildState0(String name) {
		Class<?> clazz = stateClass(name);
		
		try {
			return (IState) clazz.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		} catch (RuntimeException e) {
			em.logPrintf(IPrintLevel.PRINT_LEVEL_WARN, e);
		}
		
		return null;
	}
	
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
		
		Class<?> clazz = stateClass(statestr);
		
		if (!IStateMessageFormater.class.isAssignableFrom(clazz)) {
			// 默认的生成方式
			if (ParticipantState.class.isAssignableFrom(clazz)) {
				// no
				byte dfseat = (Byte) map.get("dfseat").getValue();
				map.put("no", new JsonValue(em.getRoot().getAttendManager().noForSeat(dfseat)));
			} else {
				// 对象不是怪兽本体的状态
			}
		} else {
			// IStateMessageFormater formater = (IStateMessageFormater)
			// clazz.newInstance();
			// TODO, 用 IStateMessageFormater 的方法处理 value(map)
		}
	}
	
	private Class<?> stateClass(String statestr) {
		Class<?> clazz = clazzes.get(PREFIX_STATE + statestr);
		
		if (clazz == null) {
			try {
				clazz = Class.forName(pro.getProperty(PREFIX_STATE + statestr));
				clazzes.put(statestr, clazz);
				return clazz;
			} catch (ClassNotFoundException e) {
				em.logPrintf(IPrintLevel.PRINT_LEVEL_WARN, e);
				em.logPrintf(IPrintLevel.PRINT_LEVEL_WARN, "无法创建 %s 状态", statestr);
			}
		}
		return clazz;
	}
	
	private void forceState(IState state) {
		AttendManager am = em.getAttends();
		
		if (state instanceof ParticipantState) {
			// 施加给怪兽
			ParticipantState pstate = (ParticipantState) state;
			byte no = pstate.getNo();
			
			am.forceStateForParticipant(no, pstate);
		}
	}

	/* ************
	 *	 初始化   *
	 ************ */
	
	EffectManage em;

	public StateBuilder(EffectManage em) {
		this.em = em;
		loadProperties();
		clazzes = new HashMap<>();
	}
	
	private void loadProperties() {
		pro = new Properties();
		InputStream in = getClass().getResourceAsStream("/com/zdream/pmw/platform/effect/state/state.properties");
		try {
			pro.load(in);
            in.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (RuntimeException e) {
			e.printStackTrace();
			return;
		}
	}
}
