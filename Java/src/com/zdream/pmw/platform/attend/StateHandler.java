package com.zdream.pmw.platform.attend;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.zdream.pmw.platform.control.IPrintLevel;
import com.zdream.pmw.platform.effect.IStateMessageFormater;
import com.zdream.pmw.platform.effect.state.AParticipantState;
import com.zdream.pmw.platform.effect.state.ASeatState;
import com.zdream.pmw.util.json.JsonBuilder;
import com.zdream.pmw.util.json.JsonObject;
import com.zdream.pmw.util.json.JsonValue;

/**
 * <p>处理状态部分的实现</p>
 * <p>它的操作层在 {@link com.zdream.pmw.platform.effect.StateBuilder}</p>
 * 
 * @since v0.2.2
 *   [2017-04-09]
 * @author Zdream
 * @version v0.2.2
 *   [2017-04-09]
 */
public class StateHandler {

	/* ************
	 *	  属性    *
	 ************ */
	
	Properties pro;
	
	private static final String PREFIX_STATE = "state.";
	
	/**
	 * state 名称 (无前缀) : class
	 */
	private Map<String, Class<?>> clazzes;
	
	JsonBuilder builder = new JsonBuilder();
	
	/* ************
	 *	公共方法  *
	 ************ */

	/**
	 * 工厂方式创建状态类<br>
	 * @param args
	 * @return
	 */
	public IState buildState(JsonObject args) {
		Map<String, JsonValue> map = args.asMap();
		String name = map.get("state").getString();
		
		IState state = buildState0(name);
		if (state == null) {
			return null;
		}
		
		state.set(args, am.getRoot());
		return state;
	}
	
	/**
	 * 按照施加状态的命令行数据, 完成施加状态的任务<br>
	 * 一般调用方是 {@code com.zdream.pmw.platform.control.codeStateCodeRealizer}<br>
	 * @param codes
	 */
	public void forceState(String[] codes) {
		String statestr = codes[1];

		Class<?> clazz = stateClass(statestr);
		JsonObject obj = new JsonObject();
		obj.add("state", JsonValue.createJson(statestr));
		
		if (!IStateMessageFormater.class.isAssignableFrom(clazz)) {
			// 默认的生成方式
			if (AParticipantState.class.isAssignableFrom(clazz)) {
				// force-state confusion -no 1 -source SKILL -skillID 60
				
				int argv = 0;
				String option = null;
				for (int i = 2; i < codes.length; i++) {
					String s = codes[i];
					
					if (s.startsWith("-") && !Character.isDigit(s.charAt(1))) {
						option = s.substring(1);
						argv = 0;
					} else {
						switch (option) {
						case "no":
							if (argv == 1) {
								obj.add(option, JsonValue.createJson(Byte.valueOf(s)));
							}
							break;
						case "skillID":
							if (argv == 1) {
								obj.add(option, JsonValue.createJson(Short.valueOf(s)));
							}
							break;
						case "source": default:
							if (argv == 1) {
								obj.add(option, JsonValue.createJson(s));
							}
							break;
						}
					}
					argv++;
				}
			} else if (ASeatState.class.isAssignableFrom(clazz)) {
				// force-state mist -seat 1 -source SKILL -skillID 54
				
				int argv = 0;
				String option = null;
				for (int i = 2; i < codes.length; i++) {
					String s = codes[i];
					
					if (s.startsWith("-") && !Character.isDigit(s.charAt(1))) {
						option = s.substring(1);
						argv = 0;
					} else {
						switch (option) {
						case "seat":
							if (argv == 1) {
								obj.add(option, JsonValue.createJson(Byte.valueOf(s)));
							}
							break;
						case "skillID":
							if (argv == 1) {
								obj.add(option, JsonValue.createJson(Short.valueOf(s)));
							}
							break;
						case "source": default:
							if (argv == 1) {
								obj.add(option, JsonValue.createJson(s));
							}
							break;
						}
					}
					argv++;
				}
			} else {
				// 其它
				throw new IllegalArgumentException("对象不是怪兽本体、不是座位的状态现在还没写呢！");
			}
		} else {
			IStateMessageFormater formater;
			try {
				formater = (IStateMessageFormater) clazz.newInstance();
				formater.forceRealize(codes, obj, am.getRoot());
			} catch (InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		
		// 产生 state
		IState state = buildState(obj);
		
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
			am.logPrintf(IPrintLevel.PRINT_LEVEL_WARN, e);
		}
		
		return null;
	}
	
	/**
	 * TODO 将实现层的方法移到 am 中！
	 * @param state
	 */
	private void forceState(IState state) {
		if (state instanceof AParticipantState) {
			// 施加给怪兽
			AParticipantState pstate = (AParticipantState) state;
			byte no = pstate.getNo();
			
			am.forceStateForParticipant(no, pstate);
		} else if (state instanceof ASeatState) {
			// 施加给座位
			ASeatState sstate = (ASeatState) state;
			byte seat = sstate.getSeat();
			
			am.forceStateForSeat(seat, sstate);
		} else {
			// 其它
			throw new IllegalArgumentException("对象不是怪兽本体、不是座位的状态现在还没写呢！");
		}
	}
	
	/**
	 * 该方法仅会对 {@code effect.StateBuilder} 开放
	 * @param statestr
	 * @return
	 */
	public Class<?> stateClass(String statestr) {
		Class<?> clazz = clazzes.get(PREFIX_STATE + statestr);
		
		if (clazz == null) {
			try {
				clazz = Class.forName(pro.getProperty(PREFIX_STATE + statestr));
				clazzes.put(statestr, clazz);
				return clazz;
			} catch (ClassNotFoundException e) {
				am.logPrintf(IPrintLevel.PRINT_LEVEL_WARN, e);
				am.logPrintf(IPrintLevel.PRINT_LEVEL_WARN, "无法创建 %s 状态", statestr);
			}
		}
		return clazz;
	}

	/* ************
	 *	 初始化   *
	 ************ */
	
	AttendManager am;
	
	public StateHandler(AttendManager am) {
		this.am = am;
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
