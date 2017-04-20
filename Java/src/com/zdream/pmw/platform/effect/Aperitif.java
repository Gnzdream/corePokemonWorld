package com.zdream.pmw.platform.effect;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.zdream.pmw.platform.control.IMessageCode;
import com.zdream.pmw.util.json.JsonValue;

/**
 * 开胃酒 (消息), 原名为拦截前消息<br>
 * <p>由于消息种类繁多容易混淆, 从 v0.2.1 版本开始将逐步取消拦截前消息的叫法.</p>
 * <p>开胃酒将有一个循环队列来储存, 以降低分配内存带来的消耗</p>
 * 
 * @since v0.2.1
 * @author Zdream
 * @date 2017年3月15日
 * @version v0.2.1
 */
public final class Aperitif extends JsonValue implements IMessageCode {
	
	/* ************
	 *	数据结构  *
	 ************ */
	
	private String head;
	private byte[] scanSeats;
	private int scanLength = 0;
	
	/**
	 * filters 中存储了被过滤的触发原因<br>
	 * <p>如果 filters 中存储了某项原因，当开胃酒经过状态的拦截时，
	 * 就相当于给出某项声明，某些状态的触发是无效的</p>
	 * <p>举例来说, 腐蚀特性的效果是可以使钢属性和毒属性的宝可梦也陷入中毒状态。
	 * 但是在状态拦截的过程中，<code>ExistState</code> 当面对攻击方向防御方下毒的情况，
	 * 会直接返回该效果无效。此时应该让腐蚀特性所属的状态添加【无视属性陷入中毒】
	 * 的过滤，使在 <code>ExistState</code> 处理时，忽略属性的影响</p>
	 * <p>filters 中还可以存放某个状态的名称. 如果过滤器查到该名称与 filters 中
	 * 的某个元素相同时, 拦截器将会跳过该状态.</p>
	 */
	private Set<String> filters;
	
	public String getHead() {
		return head;
	}
	public void setHead(String head) {
		this.head = head;
	}
	public void putFilter(String... filters) {
		for (int i = 0; i < filters.length; i++) {
			this.filters.add(filters[i]);
		}
	}
	public boolean hasFilter(String filter) {
		return filters.contains(filter);
	}
	
	/**
	 * 获取一份有效数据的拷贝
	 * @return
	 */
	public byte[] getScanSeats() {
		if (scanLength == 0) {
			return new byte[0];
		} else {
			return Arrays.copyOf(scanSeats, scanLength);
		}
	}
	public void setScanSeats(byte[] scanSeats) {
		if (this.scanSeats.length < scanSeats.length) {
			this.scanSeats = Arrays.copyOf(scanSeats, scanSeats.length);
		} else {
			System.arraycopy(this.scanSeats, 0, this.scanSeats, 0, scanSeats.length);
		}
		this.scanLength = scanSeats.length;
	}
	public void putScanSeats(byte... scanSeats) {
		if (scanSeats.length == 0) {
			return;
		}
		int expectLength = scanSeats.length + scanLength;
		if (this.scanSeats.length < expectLength) {
			byte[] newArray = new byte[expectLength + 1];
			System.arraycopy(this.scanSeats, 0, newArray, 0, scanLength);
			this.scanSeats = newArray;
		}
		System.arraycopy(scanSeats, 0, this.scanSeats, scanLength, scanSeats.length);
		this.scanLength = expectLength;
	}
	
	/* ************
	 *	拓展方法  *
	 ************ */
	/**
	 * 指向 Aperitif.value
	 */
	private Map<String, JsonValue> map;
	
	public Aperitif append(String key, Object value) {
		map.put(key, new JsonValue(value));
		return this;
	}
	
	public Object get(String key) {
		JsonValue v = map.get(key);
		if (v != null) {
			return v.getValue();
		} else {
			return null;
		}
	}
	
	public Object replace(String key, Object value) {
		JsonValue v = map.get(key);
		if (v != null) {
			Object raw = v.getValue();
			v.setValue(value);
			return raw;
		} else {
			map.put(key, new JsonValue(value));
			return null;
		}
	}
	
	/**
	 * 不允许设置
	 */
	public void setValue(Object value) {};
	
	/* ************
	 *	 初始化   *
	 ************ */
	
	/**
	 * 包可见性
	 */
	Aperitif() {
		super(JsonType.ObjectType);
		map = getMap();
		scanSeats = new byte[2];
		filters = new HashSet<>();
	}
	
	public void restore(String head, byte... scanSeats) {
		getMap().clear();
		this.head = head;
		this.scanLength = 0;
		putScanSeats(scanSeats);
		filters.clear();
	}
	
	@Override
	public String toString() {
		String raw = super.toString();
		StringBuilder builder = new StringBuilder(raw.length() + 40);
		builder.append("Aperitif@").append(head).append(" scan:[");
		
		final int length = scanLength - 1;
		if (length >= 0) {
			for (int i = 0; i < length; i++) {
				builder.append(scanSeats[i]).append(",");
			}
			builder.append(scanSeats[length]);
		}
		builder.append("] ").append(raw);
		
		return builder.toString();
	}

}
