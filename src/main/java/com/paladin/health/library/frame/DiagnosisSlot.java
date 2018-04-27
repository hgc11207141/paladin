package com.paladin.health.library.frame;

import java.util.List;

import com.paladin.health.library.Condition;

public class DiagnosisSlot implements Slot {

	int weight;
	List<DiagnosisFacet> diagnosisFacets;

	@Override
	public SlotMatchResult match(Condition condition) {

		SlotMatchResult result = new SlotMatchResult(weight);

		for (DiagnosisFacet facet : diagnosisFacets) {
			FacetResult facetResult = facet.facetMatch(condition);
			if (facetResult != null && facetResult.isSatisfied()) {
				result.addProbability(facet.getWeight());
				result.addExplainContent(facetResult.getExplainContent());		
			}
		}
		
		return result;
	}

}
