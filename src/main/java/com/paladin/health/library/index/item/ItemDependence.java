package com.paladin.health.library.index.item;

import com.paladin.health.library.Relation;
import com.paladin.health.library.index.Item;
import com.paladin.health.library.index.item.StandardItem;

/**
 * 项目依赖关系
 * 
 * @author TontoZhou
 * @since 2018年4月17日
 */
public class ItemDependence {

	Item target; // 目标
	StandardItem dependenceItem; // 依赖项
	String[] dependenceValue; // 依赖值
	Relation dependRelation; // 依赖关系
	
	
	public Item getTarget() {
		return target;
	}
	public StandardItem getDependenceItem() {
		return dependenceItem;
	}
	public String[] getDependenceValue() {
		return dependenceValue;
	}
	public Relation getDependRelation() {
		return dependRelation;
	}

	
	
}
