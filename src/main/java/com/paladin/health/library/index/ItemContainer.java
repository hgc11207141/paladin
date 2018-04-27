package com.paladin.health.library.index;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.paladin.health.library.index.item.StandardItem;

public class ItemContainer {
	
	static Map<String, Item> itemMap = new ConcurrentHashMap<>();
	static Map<String, StandardItem> standardItemMap = new ConcurrentHashMap<>();

	
	public static void register(Item item) {		
		String id = item.getId();	
		itemMap.put(id, item);
		
		if(item instanceof StandardItem)
		{
			StandardItem standardItem = (StandardItem) item;		
			String key  =standardItem.getKey();
			standardItemMap.put(key, standardItem);
		}
		
		for(Item child: item.getChildren()) {
			register(child);			
		}		
	}
	
	public static StandardItem getStandardItem(String key) {
		return standardItemMap.get(key);
	}
	
	
	
}
