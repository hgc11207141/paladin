package com.paladin.health.index.item;

import com.paladin.health.index.Item;
import com.paladin.health.index.ItemType;

public class CategoryItem extends Item{

	@Override
	public final ItemType getItemType() {
		return ItemType.CATEGORY;
	}
	
}
