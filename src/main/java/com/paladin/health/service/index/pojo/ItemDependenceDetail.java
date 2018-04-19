package com.paladin.health.service.index.pojo;

import java.util.ArrayList;
import java.util.List;

import com.paladin.health.model.index.IndexItem;
import com.paladin.health.model.index.IndexItemDependence;
import com.paladin.health.model.index.IndexItemStandard;
import com.paladin.health.model.index.IndexItemValueDefinition;

public class ItemDependenceDetail {

	private IndexItem dependenceItem;
	
	private IndexItemValueDefinition dependenceValueDefinition;
	
	private IndexItemDependence itemDependence;

	private List<IndexItemStandard> dependenceStandard;

	public ItemDependenceDetail(IndexItem dependenceItem, IndexItemValueDefinition dependenceValueDefinition, IndexItemDependence itemDependence) {
		this.dependenceItem = dependenceItem;
		this.dependenceValueDefinition = dependenceValueDefinition;
		this.itemDependence = itemDependence;
	}

	public IndexItemDependence getItemDependence() {
		return itemDependence;
	}

	public List<IndexItemStandard> getDependenceStandard() {
		return dependenceStandard;
	}

	public void setDependenceStandard(List<IndexItemStandard> dependenceStandard) {
		this.dependenceStandard = dependenceStandard;
	}

	public void setDependenceStandard(IndexItemStandard dependenceStandard) {
		this.dependenceStandard = new ArrayList<>();
		this.dependenceStandard.add(dependenceStandard);
	}

	public IndexItem getDependenceItem() {
		return dependenceItem;
	}

	public IndexItemValueDefinition getDependenceValueDefinition() {
		return dependenceValueDefinition;
	}


	
}
