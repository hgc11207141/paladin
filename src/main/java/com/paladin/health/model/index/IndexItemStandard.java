package com.paladin.health.model.index;

import com.paladin.framework.common.BaseModel;
import javax.persistence.Id;

public class IndexItemStandard extends BaseModel {

	private String id;

	private String valueDefinitionId;

	private String key;

	private String name;

	@Id
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getValueDefinitionId() {
		return valueDefinitionId;
	}

	public void setValueDefinitionId(String valueDefinitionId) {
		this.valueDefinitionId = valueDefinitionId;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}