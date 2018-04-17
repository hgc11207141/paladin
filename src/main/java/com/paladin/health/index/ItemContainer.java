package com.paladin.health.index;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.paladin.health.index.item.StandardItem;

public class ItemContainer {
	
	Map<String, Item> itemMap = new ConcurrentHashMap<>();
	Map<String, Item> standardItemMap = new ConcurrentHashMap<>();

	
	public void register(Item item) {		
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
	
	
	
}
