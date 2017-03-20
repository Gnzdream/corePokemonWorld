package com.zdream.pmw.trainer.item;

/**
 * Item 是所有物品的抽象超类<br>
 * 该类定义了物品的基础属性<br>
 * 包括其属于哪一个类型和它的编号与名称<br>
 * 该类是静态数据类<br>
 * 
 * @since v0.1
 * @author Zdream
 * @date 2016年4月3日
 * @version v0.1
 */
public class Item {
	
	/* ************
	 *	基础类型  *
	 ************ */
	/**
	 * 该物品的类型
	 */
	private EItemType type;

	/**
	 * 该物品的 ID 号
	 */
	private short itemID;

	/**
	 * 该物品的名称
	 */
	private String name;

	/**
	 * 该物品的描述
	 */
	private String description;
	
	/* ************
	 *	通用方法  *
	 ************ */
	
	public Item() {
		
	}
	
	@Override
	public String toString() {
		return "Item [type=" + type + ", name=" + name + ", description=" + description + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + itemID;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Item other = (Item) obj;
		if (itemID != other.itemID)
			return false;
		return true;
	}

	public EItemType getType() {
		return type;
	}
	
	public void setType(EItemType type) {
		this.type = type;
	}

	public short getItemID() {
		return itemID;
	}

	public void setItemID(short itemID) {
		this.itemID = itemID;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
}
