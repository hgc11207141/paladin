package com.paladin.health.library;

import java.util.List;

public abstract class Entry {

	String name;

	EntryType type;

	String content;

	Entry parent;

	List<Entry> children;

	public abstract EntryType getEntryType();

	public static enum EntryType {
		CATEGORY, ILLUSTRATE;
	}

}
