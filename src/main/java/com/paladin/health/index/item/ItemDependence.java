package com.paladin.health.index.item;

import com.paladin.health.index.Item;
import com.paladin.health.index.item.StandardItem;

/**
 * 项目依赖关系
 * @author TontoZhou
 * @since 2018年4月17日
 */
public class ItemDependence {

	private Item target;								// 目标
	private StandardItem dependenceItem;		// 依赖项
	private String dependenceValue; 				// 依赖值
	private DependenceRelation dependRelation;	// 依赖关系

	public static enum DependenceRelation {
		KEY_EQUAL, STRING_EQUAL, NUMBER_EQUAL, NUMBER_GREAT, NUMBER_GREAT_EQUAL, NUMBER_LESS, NUMBER_LESS_EQUAL;
	}

	public Item getTarget() {
		return target;
	}

	public void setTarget(Item target) {
		this.target = target;
	}

	public StandardItem getDependenceItem() {
		return dependenceItem;
	}

	public void setDependenceItem(StandardItem dependenceItem) {
		this.dependenceItem = dependenceItem;
	}

	public String getDependenceValue() {
		return dependenceValue;
	}

	public void setDependenceValue(String dependenceValue) {
		this.dependenceValue = dependenceValue;
	}

	public DependenceRelation getDependRelation() {
		return dependRelation;
	}

	public void setDependRelation(DependenceRelation dependRelation) {
		this.dependRelation = dependRelation;
	}

	

}
