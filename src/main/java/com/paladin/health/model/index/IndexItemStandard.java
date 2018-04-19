package com.paladin.health.model.index;

import javax.persistence.Id;

public class IndexItemStandard {

	public final static String COLUMN_FIELD_VALUE_DEFINITION_ID = "valueDefinitionId";
	
	@Id
	private String id;

	private String valueDefinitionId;

	private String standardKey;

	private String name;

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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStandardKey() {
		return standardKey;
	}

	public void setStandardKey(String standardKey) {
		this.standardKey = standardKey;
	}

}