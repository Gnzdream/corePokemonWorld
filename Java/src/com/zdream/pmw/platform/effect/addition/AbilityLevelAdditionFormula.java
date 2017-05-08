package com.zdream.pmw.platform.effect.addition;

import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import com.zdream.pmw.core.tools.ItemMethods;
import com.zdream.pmw.monster.prototype.IPokemonDataType;
import com.zdream.pmw.platform.attend.Participant;
import com.zdream.pmw.platform.control.IPrintLevel;
import com.zdream.pmw.platform.effect.Aperitif;
import com.zdream.pmw.platform.effect.EffectManage;
import com.zdream.pmw.util.json.JsonObject;
import com.zdream.pmw.util.json.JsonValue;
import com.zdream.pmw.util.random.RanValue;

/**
 * 能力等级变化 / 设置附加效果判定实现类<br>
 * 向我方或敌方施加能力等级变化的效果<br>
 * <br>
 * <b>v0.2</b><br>
 *   取消对 pf.instance 的引用<br>
 * 
 * @since v0.1
 * @author Zdream
 * @date 2016年4月24日
 * @version v0.2.2
 */
public class AbilityLevelAdditionFormula extends AAdditionFormula implements IPokemonDataType {
	
	/*
	 * ver 0.1.1: (2016-09-16)
	 * 
	 * 从 version 0.1.1 之后, 储存的数据发生变化
	 * 增加触发模式项
	 */
	
	/* ************
	 *	数据结构  *
	 ************ */
	
	/**
	 * 判定哪个能力项发生了变化
	 * 能力项，<code>IPokemonDataType</code> 里面的参数<br>
	 */
	private int[] items;
	
	/**
	 * 能力等级变化还是设置（默认变化）<br>
	 * 该参数具有给定值<br>
	 */
	private String param;
	
	/**
	 * param 值的给定项，能力等级变化 (默认)
	 */
	public static final String PARAM_CHANGE = "-c";
	
	/**
	 * param 值的给定项，能力等级设置
	 */
	public static final String PARAM_SET = "-s";
	
	/**
	 * 变化 / 设置能力等级数<br>
	 */
	private int[] values;
	
	/**
	 * 触发几率（百分比）
	 */
	private int rate;
	
	/**
	 * 触发模式<br>
	 * 如果变化能力项比较多的话, 是多个变化同时发生, 还是多个变化选择一个发生<br>
	 * 有两种模式, 具有给定值
	 * @version 1.1
	 */
	private String mode;
	
	/**
	 * 触发模式的给定项，表示要么所有能力变化一同触发, 要么都不触发
	 */
	public static final String MODE_AND = "a";
	
	/**
	 * 触发模式的给定项，表示所有能力变化中，最多只有一项触发
	 */
	public static final String MODE_OR = "o";
	
	{
		items = new int[3];
		values = new int[3];
	}
	
	/**
	 * 设置对应能力项和相应的能力等级变化数值
	 * @param item
	 *   <code>IPokemonDataType</code> 里面的参数
	 * @param value
	 */
	public void put(int item, int value) {
		int index = -1;
		for (int i = 0; i < items.length; i++) {
			if (items[i] == 0) {
				index = i;
				break;
			}
		}

		if (index == -1) {
			// append
			int length = items.length << 1;
			
			int[] is = new int[length];
			int[] vs = new int[length];
			
			System.arraycopy(is, 0, items, 0, items.length);
			System.arraycopy(vs, 0, values, 0, items.length);
			
			index = items.length;
			
			items = is;
			values = vs;
		}
		
		items[index] = item;
		values[index] = value;
	}

	public String getParam() {
		return param;
	}

	public void setParam(String param) {
		this.param = param;
	}
	
	public int getRate() {
		return rate;
	}
	
	public void setRate(int rate) {
		this.rate = rate;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}
	
	/**
	 * 存储的能力变化项数
	 * @return
	 */
	public int size() {
		for (int i = 0; i < items.length; i++) {
			if (items[i] == 0) {
				return i;
			}
		}
		return items.length;
	}
	
	// v0.2.2
	/**
	 * 对应父类的 seats, 每个目标是否触发
	 */
	boolean[] forces;

	/**
	 * 对应上面的 seats, 分别缓存各个目标施加的能力项和变化等级数
	 */
	int[][] titems, tvalues;
	
	/* ************
	 *	实现方法  *
	 ************ */
	
	@Override
	public String name() {
		return "ablc";
	}
	
	@Override
	protected void postHandle() {
		forces = null;
		titems = null;
		tvalues = null;
	}

	/**
	 * 参数的格式（key-value 对）：<br>
	 * { "n":"ablc", "t":"a", "tg":"e", "r":100, "i":5, "v":-2 }, <br>
	 * 说明：<br>
	 * tg(target): 目标， String, 可以为: e 敌方<br>
	 * r(rate): 击中的概率, int, 默认是 100<br>
	 * i(item): 哪个能力发生变化, 可以为:<br>
	 * 		1 攻击能力<br>
	 * 		[1,2] 攻击、防御能力<br>
	 * 		"AT" 攻击能力 "AT,DF"<br>
	 * 		攻击、防御能力<br>
	 * v(value):<br>
	 * 		变化数值, int, 可为正和负数<br>
	 * 
	 * @param value
	 */
	public void set(JsonObject value) {
		super.set(value);
		Set<Entry<String, JsonValue>> set = value.asMap().entrySet();

		String k;
		JsonValue v;
		int[] is = null, vs = null;
		
		for (Iterator<Entry<String, JsonValue>> it = set.iterator(); it.hasNext();) {
			Entry<String, JsonValue> entry = it.next();
			
			k = entry.getKey();
			v = entry.getValue();
			
			try {
				switch (k) {
				case "i": // items
					is = parseItems(v);
					break;
				case "r": // rate
					setRate((Integer) v.getValue());
					break;
				case "v": // values
					vs = parseValues(v);
					break;
				case "p": // param
					if ("s".equals(v.getString())) {
						param = PARAM_SET;
					}
					break;

				default:
					break;
				}
				
			} catch (RuntimeException e) {
				e.printStackTrace(); // warn
			}
		}

		items = new int[is.length];
		values = new int[is.length];
		if (vs.length > 1) {
			for (int i = 0; i < is.length; i++) {
				put(is[i], vs[i]);
			}
		} else {
			for (int i = 0; i < is.length; i++) {
				put(is[i], vs[0]);
			}
		}
	}
	
	@Override
	public void restore() {
		items = null;
		values = null;
		setParam(PARAM_CHANGE);
		setMode(MODE_AND);
		setRate(100);
		super.restore();
	}
	
	/* ************
	 *	私有方法  *
	 ************ */
	
	/**
	 * 能力发生变化的参数转化<br>
	 * 将能力项数值转化为能力项数组
	 * @param value
	 * @return
	 */
	private int[] parseItems(JsonValue value) {
		int[] is;
		
		switch (value.getType()	) {
		case ValueType: { // Integer
			is = new int[]{(Integer) value.getValue()};
		} break;
		
		case ArrayType: {
			List<JsonValue> list = value.asArray().asList();
			is = new int[list.size()];
			int index = 0;
			
			for (Iterator<JsonValue> it = list.iterator(); it.hasNext();) {
				JsonValue jv = it.next();
				try {
					int item = (Integer) jv.getValue();
					is[index++] = item;
				} catch (RuntimeException e) {
					em.getAttends().logPrintf(IPrintLevel.PRINT_LEVEL_WARN, 
							"AbilityLevelAdditionF.parseItems(1): 将 %s 解析为能力项出错",
							jv.getValue().toString());
					continue;
				}
			}
		} break;
		
		case StringType: {
			String[] strs = value.getString().split(",");
			is = new int[strs.length];
			
			for (int i = 0; i < strs.length; i++) {
				String item = strs[i];
				is[i] = ItemMethods.parseItemCode(item);
			}
		} break;

		default:
			is = null;
		}
		
		return is;
	}
	
	/**
	 * 解析能力变化值
	 * @param value
	 * @return
	 */
	private int[] parseValues(JsonValue value) {
		int[] vs;
		
		switch (value.getType()	) {
		case ValueType: { // Integer
			vs = new int[]{(Integer) value.getValue()};
		} break;
		
		case ArrayType: {
			List<JsonValue> list = value.asArray().asList();
			vs = new int[list.size()];
			int index = 0;
			
			for (Iterator<JsonValue> it = list.iterator(); it.hasNext();) {
				JsonValue jv = it.next();
				try {
					int v = (Integer) jv.getValue();
					vs[index++] = v;
				} catch (RuntimeException e) {
					em.logPrintf(IPrintLevel.PRINT_LEVEL_WARN, 
							"AbilityLevelAdditionF.parseItems(1): 将 %s 解析为数值出错",
							jv.getValue().toString());
					continue;
				}
			}
		} break;
		
		default:
			vs = null;
		}
		
		return vs;
	}
	
	/**
	 * 按随机数，计算异常状态是否能够触发
	 * @return
	 *   如果所有目标都不能发动, 为 false
	 */
	protected boolean canTrigger() {
		int seatLen = targets.length;
		forces = new boolean[seatLen];
		titems = new int[seatLen][];
		tvalues = new int[seatLen][];
		
		// 计算附加状态真实释放几率
		byte atseat = pack.getAtStaff().getSeat();
		byte[] dfseats = pack.dfSeats();
		
		// item
		int itemLen = size();
		int[] items = new int[itemLen];
		int[] values = new int[itemLen];
		System.arraycopy(this.items, 0, items, 0, itemLen);
		System.arraycopy(this.values, 0, values, 0, itemLen);
		
		/*
		 * 如果所有目标都不能发动, 为 false
		 */
		boolean exist = false;
		
		for (int i = 0; i < seatLen; i++) {
			byte target = targets[i];
			
			Aperitif value = em.newAperitif(
					Aperitif.CODE_CALC_ADDITION_RATE, atseat, target);
			
			value.append("atseat", atseat)
				.append("dfseats", dfseats)
				.append("target", target)
				.append("type", "abilityLevel")
				.append("param", param)
				.append("items", items)
				.append("values", values)
				.append("rate", rate)
				.append("result", 0);
			em.startCode(value);
			
			int result = (int) value.get("result");
			if (result != 1) {
				int instanceRate = (int) value.get("rate");
				if (RanValue.isSmaller(instanceRate)) {
					exist = true;
					forces[i] = true;
					titems[i] = (int[]) value.get("items");
					tvalues[i] = (int[]) value.get("values");
					
					if (result == 2) {
						Aperitif ap = em.newAperitif(Aperitif.CODE_BROADCAST);
						ap.append("type", value.get("reason"));
						em.startCode(ap);
					}
				}
			}	
		}

		return exist;
	}
	
	/**
	 * 施加能力变化<br>
	 * <p>(v0.2.2 之后) 对所有判定对象分别进行判定</p>
	 */
	protected void force() {
		int seatLen = targets.length;
		int itemLen = size();
		
		if (itemLen == 0) {
			em.logPrintf(IPrintLevel.PRINT_LEVEL_INFO, 
					"AbilityLevelAdditionF.force(): 无能力变化数值");
			return;
		}
		
		for (int i = 0; i < seatLen; i++) {
			if (forces[i] == false) {
				continue;
			}
			
			byte dfseat = targets[i];
			
			// item
			int[] items = titems[i];
			int[] values = tvalues[i];
			check(items, values, dfseat);
			if (items.length == 0) {
				continue;
			}
			
			// 进行修改
			Aperitif value = em.newAperitif(
					Aperitif.CODE_ABILITY_LEVEL, dfseat);
			value.append("param", param)
				.append("dfseat", dfseat)
				.append("items", items)
				.append("values", values);
			em.startCode(value);
		}
		
	}
	
	/**
	 * 检查，能力值再加完之后出现越界情况，将对值进行修改
	 * @param items
	 * @param values
	 */
	private void check(final int[] items, final int[] values, byte dfseat) {
		if (PARAM_CHANGE.equals(param)) {
			final int length = items.length;
			Participant participant = 
					em.getAttends().getParticipant(dfseat);
			
			for (int i = 0; i < length; i++) {
				int item = items[i];
				
				int c = participant.getAbilityLevel(item) + values[i];
				if (c > LEVEL_UPPER_LIMIT) {
					values[i] = LEVEL_UPPER_LIMIT - participant.getAbilityLevel(item);
				} else if (c < LEVEL_LOWER_LIMIT) {
					values[i] = LEVEL_LOWER_LIMIT - participant.getAbilityLevel(item);
				}
			}
		} else {
			final int length = items.length;
			for (int i = 0; i < length; i++) {
				int v = values[i];
				if (v > LEVEL_UPPER_LIMIT) {
					values[i] = LEVEL_UPPER_LIMIT;
				} else if (v < LEVEL_LOWER_LIMIT) {
					values[i] = LEVEL_LOWER_LIMIT;
				}
			}
		}
	}
	
	/* ************
	 *	 初始化   *
	 ************ */
	
	private EffectManage em;
	
	public AbilityLevelAdditionFormula(EffectManage em) {
		this.em = em;
	}
	
}
