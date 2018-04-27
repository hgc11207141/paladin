package com.paladin.health.library.frame;

import java.util.List;

import com.paladin.health.library.Condition;

public class SymptomSlot implements  Slot{

	int weight;
	List<SymptomFacet> symptomFacets;

	@Override
	public SlotMatchResult match(Condition condition) {
		SlotMatchResult result = new SlotMatchResult(weight);
		
		for (SymptomFacet facet : symptomFacets) {
			if (facet.isSatisfied(condition)) {
				result.addProbability(facet.getWeight());
				result.addExplainContent(facet.getSymptomName());		
			}
		}
		
		return result;
	}
	
	
}
