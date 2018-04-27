package com.paladin.health.library.frame;

import java.util.List;

import com.paladin.health.library.Condition;
import com.paladin.health.library.LibraryConstant;
import com.paladin.health.library.index.ItemContainer;
import com.paladin.health.library.index.item.ItemStandard;
import com.paladin.health.library.index.item.StandardItem;

/**
 * 症状侧写，诊断症状项目，只是简单查找是否存在该症状，返回
 * 
 * @author TontoZhou
 * @since 2018年4月20日
 */
public class SymptomFacet {

	private int weight;
	private String symptomValue;
	private String symptomName;

	public SymptomFacet(int weight, String symptomValue) {
		this.weight = weight;
		this.symptomValue = symptomValue;

		StandardItem symptomItem = ItemContainer.getStandardItem(LibraryConstant.ITEM_KEY_SYMPTOM);

		List<ItemStandard> standards = symptomItem.getItemValueDefinition().getStandards();
		for (ItemStandard standard : standards) {
			if (standard.getKey().equals(symptomValue)) {
				symptomName = standard.getName();
				break;
			}
		}
	}

	/**
	 * 是否满足条件
	 * 
	 * @param condition
	 * @return true/false：是或否
	 */
	public boolean isSatisfied(Condition condition) {
		String targetValue = condition.getCondition(LibraryConstant.ITEM_KEY_SYMPTOM);
		if (targetValue != null && targetValue.length() != 0) {
			String[] tvs = targetValue.split(",");
			for (String tv : tvs) {
				if (symptomValue.equals(tv)) {
					return true;
				}
			}
		}
		return false;
	}

	public int getWeight() {
		return weight;
	}

	public String getSymptomValue() {
		return symptomValue;
	}

	public String getSymptomName() {
		return symptomName;
	}

}
