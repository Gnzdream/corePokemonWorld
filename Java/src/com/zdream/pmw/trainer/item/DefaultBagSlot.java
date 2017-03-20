package com.zdream.pmw.trainer.item;

/**
 * 默认的背包类<br>
 * 【未完成】TODO<br>
 * 
 * @since v0.1
 * @author Zdream
 * @date 2016年4月3日
 * @version v0.1
 */
public class DefaultBagSlot extends ABagSlot {
	
	/* ************
	 *	  参数    *
	 ************ */
	
	/**
	 * 允许加入的 Item 类型
	 */
	public EItemType[] allowTypes;
	
	public EItemType[] getAllowTypes() {
		return this.allowTypes;
	}
	
	public void setAllowTypes(EItemType[] allowTypes) {
		this.allowTypes = allowTypes;
	}
	
	/* ************
	 *	实现方法  *
	 ************ */
	
	@Override
	public boolean canBeAdded(Item item) {
		for (EItemType type : allowTypes) {
			if (type.equals(item.getType())) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public int addItem(final Item item, final int quantity) {
		if (!canBeAdded(item)) {
			return 0;
		}
		int begin = 0, 
				index, 
				remain = quantity, // 剩下的数量
				storage; // 暂存每个单元可以被收纳的个数
		ItemElement element;
		while ((index = this.nextItemElement(item, begin)) != -1) {
			element = getContainer().get(index);
			storage = element.canBeStoraged(item, remain);
			if (storage == remain) {
				this.addItem(element, item, remain);
				return quantity;
			} else if (storage != 0) {
				this.addItem(element, item, storage);
				remain -= storage;
			}
			++begin;
		}
		
		element = newItemElement(item);
		while (element != null) {
			storage = element.canBeStoraged(item, remain);
			if (storage == remain) {
				this.addItem(element, item, remain);
				return quantity;
			} else if (storage != 0) {
				this.addItem(element, item, storage);
				remain -= storage;
			}
		}
		
		return quantity - remain;
	}

	@Override
	public boolean removeItem(int index, int value) {
		// TODO
		return false;
	}
	
	@Override
	public boolean useItem(int index, int quantity) {
		// TODO Auto-generated method stub
		return false;
	}

	
	/* ************
	 *	通用方法  *
	 ************ */
	public DefaultBagSlot() {
		
	}
	
}
