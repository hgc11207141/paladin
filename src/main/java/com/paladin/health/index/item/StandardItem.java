package com.paladin.health.index.item;

import java.util.ArrayList;
import java.util.List;

import com.paladin.health.index.Item;
import com.paladin.health.index.ItemType;

public class StandardItem extends Item{
	
	private String key;
		
	private ItemValueDefinition itemValueDefinition;
	
	private List<ItemDependence> itemDependence = new ArrayList<>();
	
	public StandardItem(String key) {
		this.key = key;
	}
	
	@Override
	public final ItemType getItemType() {
		return ItemType.STANDARD;
	}
			
	public String getKey() {
		return key;
	}

	public ItemValueDefinition getItemValueDefinition() {
		return itemValueDefinition;
	}

	public void setItemValueDefinition(ItemValueDefinition itemValueDefinition) {
		this.itemValueDefinition = itemValueDefinition;
	}

	public List<ItemDependence> getItemDependence() {
		return new ArrayList<>(itemDependence);
	}

	public void addItemDependence(ItemDependence itemDependence) {
		this.itemDependence.add(itemDependence);
	}

	public void addItemDependences(List<ItemDependence> itemDependences) {
		this.itemDependence.addAll(itemDependences);
	}
}
