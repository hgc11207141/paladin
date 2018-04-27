package com.paladin.health.library.frame;

import java.util.ArrayList;
import java.util.List;

public class SlotMatchResult {

	private int totalWeight;
	
	private int weight;

	private List<String> explainContents = new ArrayList<>();

	public SlotMatchResult(int totalWeight) {
		this.totalWeight = totalWeight;
	}

	public int getProbability() {
		return weight * 100/ totalWeight;
	}
	
	public void addProbability(int weight) {
		this.weight += weight;
	}

	public List<String> getExplainContents() {
		return new ArrayList<>(explainContents);
	}
	
	public void addExplainContent(String explainContent) {
		explainContents.add(explainContent);
	}

}
