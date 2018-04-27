package com.paladin.health.library;

import java.util.Map;

/**
 * <h2>匹配条件，即病人情况</h2>
 * <p>DiseaseKey:疾病代码，直接获取需要的疾病健康知识</p>
 * <p>Conditions:病人条件，KEY-VALUE形式，指向与{@link com.paladin.health.library.index.Item}</p>
 * @author TontoZhou
 * @since 2018年4月20日
 */
public class Condition {

	private String diseaseKey;
	
	private Map<String,String> conditions;

	public String getDiseaseKey() {
		return diseaseKey;
	}

	public String getCondition(String itemKey) {
		return conditions.get(itemKey);
	}

}
