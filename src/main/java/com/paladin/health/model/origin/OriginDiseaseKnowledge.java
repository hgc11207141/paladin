package com.paladin.health.model.origin;

import javax.persistence.Id;

public class OriginDiseaseKnowledge {
	
	public static final String COLUMN_FIELD_DISEASE_KEY = "diseaseKey";	

	public static final String COLUMN_FIELD_CATEGORY_KEY = "categoryKey";	

	@Id
	private Integer id;

	private String diseaseKey;
	
	private String categoryKey;

	private String name;

	private Integer parentId;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getDiseaseKey() {
		return diseaseKey;
	}

	public void setDiseaseKey(String diseaseKey) {
		this.diseaseKey = diseaseKey;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getParentId() {
		return parentId;
	}

	public void setParentId(Integer parentId) {
		this.parentId = parentId;
	}

	public String getCategoryKey() {
		return categoryKey;
	}

	public void setCategoryKey(String categoryKey) {
		this.categoryKey = categoryKey;
	}

}