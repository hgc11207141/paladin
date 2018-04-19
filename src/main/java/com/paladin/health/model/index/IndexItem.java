package com.paladin.health.model.index;

import javax.persistence.Id;

import com.paladin.framework.common.UnDeleteModel;

public class IndexItem extends UnDeleteModel{

	@Id
	private String id;

	private String name;

	private String itemType;

	private String parentId;

	private String itemKey;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getItemType() {
		return itemType;
	}

	public void setItemType(String itemType) {
		this.itemType = itemType;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public String getItemKey() {
		return itemKey;
	}

	public void setItemKey(String itemKey) {
		this.itemKey = itemKey;
	}

}