package com.paladin.health.model.origin;

import javax.persistence.Id;

public class OriginDiseaseKnowledgeContent {

	public static final String COLUMN_FIELD_KNOWLEDGE_ID = "knowledgeId";
	public static final String COLUMN_FIELD_DISEASE_KEY = "diseaseKey";

	@Id
	private Integer id;

	private String diseaseKey;

	private String content;

	private Integer knowledgeId;

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

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Integer getKnowledgeId() {
		return knowledgeId;
	}

	public void setKnowledgeId(Integer knowledgeId) {
		this.knowledgeId = knowledgeId;
	}

}