package com.paladin.health.library.index.item;

import java.util.ArrayList;
import java.util.List;

/**
 * <h2>项目值定义</h2>
 * <p>项目值分为SELECT和INPUT两种，分别为选择标准值和输入值两种。</p>
 * <p>标准值是一套拥有有意义KEY的键值对，见{@link ItemStandard}</p>
 * @author TontoZhou
 * @since 2018年4月17日
 */
public class ItemValueDefinition {
	
	InputType inputType;		// 输入类型
	
	public ItemValueDefinition(InputType inputType) {
		this.inputType = inputType;
	}
	
	public InputType getInputType() {
		return inputType;
	}	
	
	// ------------------------ SELECT ----------------------------
	
	List<ItemStandard> standards = new ArrayList<>();		// 标准值
	boolean single;													// 是否单选
	
	public List<ItemStandard> getStandards() {
		return new ArrayList<>(standards);
	}
	
	public void addStandard(ItemStandard standard) {
		standards.add(standard);
	}
	
	public void addStandards(List<ItemStandard> standards) {
		standards.addAll(standards);
	}

	public boolean isSingle() {
		return single;
	}

	public void setSingle(boolean single) {
		this.single = single;
	}
	
	// ------------------------ SELECT ----------------------------
	
	
	// ------------------------ INPUT ----------------------------
	
	String template;					// 输入模板
	String unit;							// 单位
	ValueType valueType;			// 值类型

	public ValueType getValueType() {
		return valueType;
	}

	public void setValueType(ValueType valueType) {
		this.valueType = valueType;
	}

	public String getTemplate() {
		return template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}
	
	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	// ------------------------ INPUT ----------------------------
	
	
	public static enum InputType {
		SELECT,INPUT;
	}
	
	public static enum ValueType {
		TEXT,NUMBER;
	}


}
