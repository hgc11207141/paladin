package com.paladin.health.library.frame;

import com.paladin.health.library.Condition;

public interface Facet {
	
	/**
	 * 侧写匹配
	 * @param condition
	 * @return
	 */
	public FacetResult facetMatch(Condition condition);
	
}
