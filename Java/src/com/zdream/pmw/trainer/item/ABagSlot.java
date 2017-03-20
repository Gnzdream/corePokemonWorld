package com.zdream.pmw.trainer.item;

import java.util.ArrayList;
import java.util.List;

/**
 * ABagSlot 类是贮存 Item 的一个集合类的超类<br>
 * 储存随意添加 Item 的包槽<br>
 * 【需要数据和方法解耦】TODO<br>
 * 
 * @since v0.1
 * @author Zdream
 * @date 2016年4月3日
 * @version v0.1
 */
public abstract class ABagSlot {
	
	/* ************
	 *	  参数    *
	 ************ */
	
	/**
	 * 允许放入的物品数量总数<br>
	 * 即放入的 ItemElement 的总数
	 */
	private int maxCapacity;

	/**
	 * 最大容量参数：无限
	 */
	public static final int MAX_CAPACITY_INFINITE = -1;

	public int getMaxCapacity() {
		return maxCapacity;
	}

	public void setMaxCapacity(int maxCapacity) {
		this.maxCapacity = maxCapacity;
	}

	/**
	 * 测试是否是允许加入的 Item 类型
	 * 即测试类型是否符合
	 * @param i
	 * @return
	 */
	public abstract boolean canBeAdded(Item item);
	
	/* ************
	 *	  容器    *
	 ************ */
	
	private List<ItemElement> container = new ArrayList<ItemElement>();
	
	protected List<ItemElement> getContainer() {
		return container;
	}
	
	/**
	 * 查询容器中是否有该物品的背包的单元
	 * @param item
	 * @param begin
	 *   开始查找的索引<br>
	 *   如果要重头找，该参数为 0
	 * @return
	 *   指出该物品单元在背包的索引<br>
	 *   如果指定的单元不存在，返回 -1
	 */
	protected int nextItemElement(Item item, int begin) {
		int length = container.size();
		for (int i = begin; i < length; i++) {
			ItemElement element = container.get(i);
			if (element.isItemTheSame(item)){
				return i;
			}
		}
		return -1;
	}
	
	/**
	 * 添加一个新的背包的单元，并返回
	 * @param item
	 * @return
	 *   如果背包槽满了，无法添加新的单元，返回 null
	 */
	protected ItemElement newItemElement(Item item) {
		if (maxCapacity == MAX_CAPACITY_INFINITE || container.size() < maxCapacity) {
			ItemElement element = new ItemElement(item);
			container.add(element);
			return element;
		}
		return null;
	}
	
	/**
	 * 添加
	 * @throws IllegalAccessException 
	 */
	protected void addItem(final ItemElement element, final Item item, final int quantity) {
		if (!element.isItemTheSame(item)) {
			throw new IllegalArgumentException("The type of item will be added is illegal");
		}
		element.setQuantity(element.getQuantity() + quantity);
	}
	
	/* ************
	 *	  方法    *
	 ************ */
	
	/**
	 * 向背包中添加 Item
	 * @param item
	 * @param quantity
	 * @return
	 *   已经添加的 item 数量<br>
	 *   如果所有的 item 已经被添加，则返回 quantity<br>
	 *   如果没有 item 能够被添加，则返回 0<br>
	 */
	public abstract int addItem(final Item item, final int quantity);
	
	public abstract boolean removeItem(int index, int quantity);
	
	public abstract boolean useItem(int index, int quantity);
	
	
	/* ************
	 *	通用方法  *
	 ************ */
	public ABagSlot() {
		maxCapacity = MAX_CAPACITY_INFINITE;
	}
	
}