package com.paladin.health.index;

import java.util.ArrayList;
import java.util.List;

public abstract class Item {
	
	private String id;
	
	private String name;
	
	private Item parent;
	
	private List<Item> children = new ArrayList<>();
	 
	public abstract ItemType getItemType() ;
		
	public List<Item> getChildren() {
		return new ArrayList<>(children);
	}

	public void addChild(Item child) {
		children.add(child);
	}
	
	public void addChilds(List<Item> children) {
		children.addAll(children);
	}

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

	public Item getParent() {
		return parent;
	}

	public void setParent(Item parent) {
		this.parent = parent;
	}

	
}
