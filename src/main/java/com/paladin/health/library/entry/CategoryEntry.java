package com.paladin.health.library.entry;

import com.paladin.health.library.Entry;

public class CategoryEntry extends Entry{
	
	private String key;

	@Override
	public final EntryType getEntryType() {
		return EntryType.CATEGORY;
	}

	public String getKey() {
		return key;
	}


	
}
