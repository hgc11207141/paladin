package com.paladin.health.library.frame;

import com.paladin.framework.utils.StringParser;
import com.paladin.health.library.Condition;
import com.paladin.health.library.Relation;
import com.paladin.health.library.RelationUtil;

/**
 * 诊断侧写，硬性指标，例如定义血压高于多少为高血压
 * 
 * @author TontoZhou
 * @since 2018年4月13日
 */
public class BaseFacet implements Facet{

	private String itemKey;

	private String[] itemValue;

	private Relation relation;

	private String content;

	public BaseFacet(String itemKey, String[] itemValue, Relation relation) {
		this.itemKey = itemKey;
		this.relation = relation;
		this.itemValue = itemValue;
		
		this.content = relation.name() + StringParser.toString(itemValue);
	}

	@Override
	public FacetResult facetMatch(Condition condition) {
		String targetValue = condition.getCondition(itemKey);
		
		boolean isSatisfied = false;
		if (targetValue != null && targetValue.length() != 0) {
			String[] tvs = targetValue.split(",");
			if (tvs.length == 1) {
				isSatisfied = RelationUtil.isSatisfied(relation, targetValue, itemValue);
			} else {
				isSatisfied = RelationUtil.isSatisfied(relation, tvs, itemValue);
			}
		}
		
		return new FacetResult(isSatisfied, content);
	}

	public String getItemKey() {
		return itemKey;
	}

	public String[] getItemValue() {
		return itemValue;
	}

	public Relation getRelation() {
		return relation;
	}

}
