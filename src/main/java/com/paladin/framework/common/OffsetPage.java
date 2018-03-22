package com.paladin.framework.common;

public class OffsetPage extends QuerySort{
	
	public final static int DEFAULT_LIMIT = 20;
	
	int limit;
	int offset;
	

	public int getLimit() {
		return limit;
	}
	
	public void setLimit(int limit) {
		this.limit = limit;
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}
	
}
