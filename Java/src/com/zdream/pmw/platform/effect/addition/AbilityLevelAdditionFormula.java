package com.zdream.pmw.platform.effect.addition;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import com.zdream.pmw.monster.prototype.IPokemonDataType;
import com.zdream.pmw.monster.skill.ESkillCategory;
import com.zdream.pmw.platform.attend.Participant;
import com.zdream.pmw.platform.common.ItemMethods;
import com.zdream.pmw.platform.control.IPrintLevel;
import com.zdream.pmw.platform.effect.Aperitif;
import com.zdream.pmw.platform.effect.EffectManage;
import com.zdream.pmw.platform.effect.SkillReleasePackage;
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
 * @version v0.2
 */
public class AbilityLevelAdditionFormula implements IAdditionFormula, IPokemonDataType {
	
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
	 * 目标，设置向谁施加能力等级变化（默认对方）<br>
	 * 该参数具有给定值<br>
	 */
	private String target;
	
	/**
	 * target 值的给定项，（全体）对方
	 */
	public static final String TARGET_ENEMY = "e";
	
	/**
	 * target 值的给定项，自己
	 */
	public static final String TARGET_SELF = "s";
	
	/**
	 * target 值的给定项，对方和自己
	 */
	public static final String TARGET_SELF_AND_ENEMY = "se";
	
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
	
	public String getTarget() {
		return target;
	}
	
	public void setTarget(String target) {
		this.target = target;
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
	
	/* ************
	 *	实现方法  *
	 ************ */
	
	@Override
	public String name() {
		return "ability-level";
	}

	@Override
	public void addition(SkillReleasePackage pack) {
		this.pack = pack;
		if (canTrigger()) {
			force();
		}
		this.pack = null;
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
	public void set(JsonValue value) {
		Set<Entry<String, JsonValue>> set = value.getMap().entrySet();

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
				case "tg": // target
					setTarget(v.getString());
					break;
				case "r": // rate
					setRate((Integer) v.getValue());
					break;
				case "v": // values
					vs = parseValues(v);
					break;

				default:
					break;
				}
				
			} catch (RuntimeException e) {
				e.printStackTrace(); // warn
			}
		}
		
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
		for (int i = 0; i < items.length; i++) {
			if (items[i] == 0) {
				break;
			}
			items[i] = 0;
			values[i] = 0;
		}
		setParam(PARAM_CHANGE);
		setTarget(TARGET_ENEMY);
		setMode(MODE_AND);
		setRate(100);
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
			List<JsonValue> list = value.getArray();
			is = new int[list.size()];
			int index = 0;
			
			for (Iterator<JsonValue> it = list.iterator(); it.hasNext();) {
				JsonValue jv = it.next();
				try {
					int item = (Integer) jv.getValue();
					is[index++] = item;
				} catch (RuntimeException e) {
					pack.getAttends().logPrintf(IPrintLevel.PRINT_LEVEL_WARN, 
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
			List<JsonValue> list = value.getArray();
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
	 * 施加对象的 seat 数组
	 * @return
	 */
	private byte[] targetSeats() {
		if (TARGET_ENEMY.equals(target)) {
			List<Byte> list = new ArrayList<Byte>();
			int length = pack.dfStaffLength();
			for (int i = 0; i < length; i++) {
				if (pack.getSkill().getSkill().getCategory() == ESkillCategory.STATUS) {
					if (pack.isHitable(i)) {
						list.add(pack.getDfStaff(i).getSeat());
					}
				} else {
					if (pack.isHitable(i) && pack.getRate(i) != 0) {
						list.add(pack.getDfStaff(i).getSeat());
					}
				}
			}
			
			byte[] result = new byte[list.size()];
			int index = 0;
			for (Iterator<Byte> it = list.iterator(); it.hasNext();) {
				Byte b = it.next();
				result[index++] = b;
			}
			return result;
			
		} else if (TARGET_SELF.equals(target)) {
			return new byte[]{atseat()};
		} else if (TARGET_SELF_AND_ENEMY.equals(target)) {
			List<Byte> list = new ArrayList<Byte>();
			int length = pack.dfStaffLength();
			for (int i = 0; i < length; i++) {
				if (pack.isHitable(i) && pack.getRate(i) != 0) {
					list.add(pack.getDfStaff(i).getSeat());
				}
			}
			
			byte[] result = new byte[list.size() + 1];
			result[0] = atseat();
			int index = 1;
			for (Iterator<Byte> it = list.iterator(); it.hasNext();) {
				Byte b = it.next();
				result[index++] = b;
			}
			return result;
		}
		
		return null;
	}
	
	/**
	 * 按随机数，计算异常状态是否能够触发
	 * @return
	 */
	private boolean canTrigger() {
		// 计算附加状态真实释放几率
		byte atseat = pack.getAtStaff().getSeat();
		byte[] dfseats = targetSeats();
		
		byte[] scanSeats = new byte[dfseats.length + 1];
		System.arraycopy(dfseats, 0, scanSeats, 0, dfseats.length);
		scanSeats[dfseats.length] = atseat;

		Aperitif value = pack.getEffects().newAperitif(
				Aperitif.CODE_CALC_ADDITION_RATE, scanSeats);
		
		// item
		int itemLen = size();
		int[] items = new int[itemLen];
		int[] values = new int[itemLen];
		System.arraycopy(this.items, 0, items, 0, itemLen);
		System.arraycopy(this.values, 0, values, 0, itemLen);
		
		if (dfseats.length == 1) {
			value.append("dfseat", dfseats[0]);
		} else {
			value.append("dfseat", (byte) -1);
			value.append("dfseats", dfseats);
		}
		
		value.append("atseat", atseat);
		value.append("type", "abilityLevel");
		value.append("param", param);
		value.append("items", items);
		value.append("values", values);
		value.append("rate", rate);
		value.append("result", 0);
		pack.getEffects().startCode(value);

		// 计算
		int result = (int) value.get("result");
		if (result == 0) {
			int instanceRate = (int) value.get("rate");
			return RanValue.isSmaller(instanceRate);
		}

		return false;
	}
	
	/**
	 * 施加能力变化
	 */
	private void force() {
		byte[] targets = targetSeats();
		int size = size();
		
		if (targets == null) {
			em.logPrintf(IPrintLevel.PRINT_LEVEL_ERROR, 
					"AbilityLevelAdditionF.force(): 释放的对象无法找到，对象为 %s", this.target);
			return;
		}
		
		if (size == 0) {
			em.logPrintf(IPrintLevel.PRINT_LEVEL_WARN, 
					"AbilityLevelAdditionF.force(): 无能力变化数值");
			return;
		}
		
		for (int i = 0; i < targets.length; i++) {
			byte dfseat = targets[i];
			
			// item
			int itemLen = size();
			int[] items = new int[itemLen];
			int[] values = new int[itemLen];
			System.arraycopy(this.items, 0, items, 0, itemLen);
			System.arraycopy(this.values, 0, values, 0, itemLen);
			
			check(items, values, dfseat);
			
			// 进行修改
			Aperitif value = pack.getEffects().newAperitif(
					Aperitif.CODE_ABILITY_LEVEL, targets);
			value.append("param", param);
			value.append("dfseat", dfseat);
			value.append("items", items);
			value.append("values", values);
			pack.getEffects().startCode(value);
		}
		
	}

	/**
	 * 攻击方的 seat
	 * @return
	 */
	private byte atseat() {
		return pack.getAtStaff().getSeat();
	}
	
	/**
	 * 检查，能力值再加完之后出现越界情况，将对值进行修改
	 * @param items
	 * @param values
	 */
	private void check(final int[] items, final int[] values, byte dfseat) {
		int length = items.length;
		Participant participant = 
				this.pack.getAttends().getParticipant(dfseat);
		
		for (int i = 0; i < length; i++) {
			int item = items[i];
			
			int c = participant.getAbilityLevel(item) + values[i];
			if (c > LEVEL_UPPER_LIMIT) {
				values[i] = LEVEL_UPPER_LIMIT - participant.getAbilityLevel(item);
			} else if (c < LEVEL_LOWER_LIMIT) {
				values[i] = LEVEL_LOWER_LIMIT - participant.getAbilityLevel(item);
			}
		}
		
	}
	
	/* ************
	 *	 初始化   *
	 ************ */
	
	private SkillReleasePackage pack;
	private EffectManage em;
	
	public AbilityLevelAdditionFormula(EffectManage em) {
		this.em = em;
	}
	
}
