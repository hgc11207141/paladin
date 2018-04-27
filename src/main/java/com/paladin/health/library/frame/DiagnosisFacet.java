package com.paladin.health.library.frame;

import java.util.ArrayList;
import java.util.List;

import com.paladin.health.library.Condition;

public class DiagnosisFacet implements Facet {
	
	boolean isVetoed;
	int weight;
	List<BaseFacet> baseFacets;

	@Override
	public FacetResult facetMatch(Condition condition) {
		for (BaseFacet facet : baseFacets) {
			FacetResult result = facet.facetMatch(condition);
			if (result != null && result.isSatisfied()) {				
				return result;
			}
		}

		return null;
	}

	public int getWeight() {
		return weight;
	}

	public List<BaseFacet> getBaseFacets() {
		return new ArrayList<>(baseFacets);
	}

}
