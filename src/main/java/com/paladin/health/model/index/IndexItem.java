package com.paladin.health.model.index;

import com.paladin.framework.common.UnDeleteModel;
import javax.persistence.Id;

public class IndexItem extends UnDeleteModel {

	private String id;

	private String name;

	private String itemType;

	private String parentId;

	private String key;

	@Id
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

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

}