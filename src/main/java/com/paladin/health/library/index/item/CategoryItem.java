package com.paladin.health.library.index.item;

import com.paladin.health.library.index.Item;
import com.paladin.health.library.index.ItemType;

/**
 * 分类项目，只做分类用处，无实际业务作用
 * @author TontoZhou
 * @since 2018年4月20日
 */
public class CategoryItem extends Item{

	@Override
	public final ItemType getItemType() {
		return ItemType.CATEGORY;
	}
	
}
