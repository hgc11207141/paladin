package com.paladin.health.model.origin;

import javax.persistence.Id;

public class OriginDiseaseName {

	public static final String COLUMN_FIELD_TYPE = "type";	
	
	public static final int TYPE_DISEASE = 1;
	public static final int TYPE_SYMPTOM = 2;

	@Id
	private String nameKey;

	private String name;
	@Id
	private Integer type;

	public String getNameKey() {
		return nameKey;
	}

	public void setNameKey(String nameKey) {
		this.nameKey = nameKey;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{名称:").append(name).append(",关键字:").append(nameKey).append(",类型:").append(type == TYPE_DISEASE ? "疾病" : "症状").append("}");
		return sb.toString();
	}

}