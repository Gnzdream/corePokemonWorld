package com.zdream.pmw.trainer.item;

/**
 * 每个背包的单元<br>
 * 每个 Item 都会封装成 ItemElement 来装入背包<br>
 * 
 * @since v0.1
 * @author Zdream
 * @date 2016年4月3日
 * @version v0.1
 */
public class ItemElement {
	
	/* ************
	 *	  参数    *
	 ************ */
	/**
	 * 封装的物品
	 */
	private Item item;
	
	/**
	 * 数量
	 */
	private int quantity;
	
	/**
	 * 最大数量
	 */
	private int maxQuantity;
	
	/**
	 * 最大数量参数：无限
	 */
	public static final int MAX_QUANTITY_INFINITE = -1;

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public int getMaxQuantity() {
		return maxQuantity;
	}

	public void setMaxQuantity(int maxQuantity) {
		this.maxQuantity = maxQuantity;
	}

	public Item getItem() {
		return item;
	}

	/* ************
	 *	通用方法  *
	 ************ */
	
	public ItemElement(Item item) {
		this.item = item;
		this.quantity = 0;
		this.maxQuantity = MAX_QUANTITY_INFINITE;
	}
	
	/**
	 * 这个特定的物品和该 ItemElement 能收纳的物品是否相同
	 * @param item
	 * @return
	 */
	public boolean isItemTheSame(Item item) {
		return this.item.equals(item);
	}
	
	/**
	 * 该物品是否能被收纳<br>
	 * 当这个特定的物品和该 ItemElement 能收纳的物品相同时，并且空间足够时<br>
	 * 可以被收纳
	 * @param item
	 * @param quantity
	 * @return
	 *   能够收纳的个数<br>
	 *   如果 ItemElement 的空间足以将所有的特定物品收纳，返回值 = quantity；<br>
	 *   如果空间不足以再容纳任何一个特定物品，返回值 = 0
	 */
	public int canBeStoraged(Item item, int quantity) {
		if (isItemTheSame(item)) {
			if (this.maxQuantity == MAX_QUANTITY_INFINITE || 
					this.quantity + quantity <= this.maxQuantity) {
				return quantity;
			} else {
				return this.maxQuantity - this.quantity;
			}
		} else {
			return 0;
		}
	}

}
