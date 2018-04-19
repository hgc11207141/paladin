package com.paladin.health.model.index;

import javax.persistence.Id;

public class IndexItemDependence {

	public final static String COLUMN_FIELD_TARGET_ID = "targetId";

	@Id
	private String id;

	private String targetId;

	private String dependenceId;

	private String dependenceRelation;

	private String dependenceValue;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTargetId() {
		return targetId;
	}

	public void setTargetId(String targetId) {
		this.targetId = targetId;
	}

	public String getDependenceId() {
		return dependenceId;
	}

	public void setDependenceId(String dependenceId) {
		this.dependenceId = dependenceId;
	}

	public String getDependenceRelation() {
		return dependenceRelation;
	}

	public void setDependenceRelation(String dependenceRelation) {
		this.dependenceRelation = dependenceRelation;
	}

	public String getDependenceValue() {
		return dependenceValue;
	}

	public void setDependenceValue(String dependenceValue) {
		this.dependenceValue = dependenceValue;
	}

}