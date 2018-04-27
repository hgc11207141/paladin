package com.paladin.health.library.frame;

public class FacetResult {
	
	private boolean isVetoed;
	private boolean isSatisfied;	
	private String explainContent;
	
	public FacetResult(boolean isSatisfied, String explainContent) {
		this.isSatisfied = isSatisfied;
		this.explainContent = explainContent;
	}

	public String getExplainContent() {
		return explainContent;
	}

	public boolean isVetoed() {
		return isVetoed;
	}

	public void setVetoed(boolean isVetoed) {
		this.isVetoed = isVetoed;
	}

	public void setExplainContent(String explainContent) {
		this.explainContent = explainContent;
	}

	public boolean isSatisfied() {
		return isSatisfied;
	}

	public void setSatisfied(boolean isSatisfied) {
		this.isSatisfied = isSatisfied;
	}
	
	
}
