package com.paladin.health.library.index.item;

import java.util.ArrayList;
import java.util.List;

import com.paladin.health.library.Relation;
import com.paladin.health.library.index.Item;
import com.paladin.health.library.index.ItemType;
import com.paladin.health.library.index.item.ItemValueDefinition.InputType;

/**
 * 标准项目，实际作用项目
 * 
 * @author TontoZhou
 * @since 2018年4月20日
 */
public class StandardItem extends Item {

	String key;

	ItemValueDefinition itemValueDefinition;

	List<ItemDependence> itemDependence = new ArrayList<>();

	public StandardItem(String key) {
		this.key = key;
	}

	@Override
	public final ItemType getItemType() {
		return ItemType.STANDARD;
	}

	public String getKey() {
		return key;
	}

	public ItemValueDefinition getItemValueDefinition() {
		return itemValueDefinition;
	}

	public void setItemValueDefinition(ItemValueDefinition itemValueDefinition) {
		this.itemValueDefinition = itemValueDefinition;
	}

	public List<ItemDependence> getItemDependence() {
		return new ArrayList<>(itemDependence);
	}

	public void addItemDependence(ItemDependence itemDependence) {
		this.itemDependence.add(itemDependence);
	}

	public void addItemDependences(List<ItemDependence> itemDependences) {
		this.itemDependence.addAll(itemDependences);
	}

	public String toContent(String... values) {

		StringBuilder sb = new StringBuilder(getName());

		sb.append(":");

		if (itemValueDefinition.inputType == InputType.INPUT) {

			if (itemValueDefinition.template != null) {

				if (values != null && values.length > 0) {
					sb.append(itemValueDefinition.template.replace("{?}", values[0]));
				} else {
					sb.append(itemValueDefinition.template.replace("{?}", "______"));
				}
			}

			if (itemValueDefinition.unit != null) {
				sb.append(itemValueDefinition.unit);
			}

		} else if (itemValueDefinition.inputType == InputType.SELECT) {

			List<ItemStandard> standards = itemValueDefinition.getStandards();
			if (standards.size() > 0) {
				if (values == null || values.length == 0) {

					for (ItemStandard standard : standards) {
						sb.append(standard.name).append(",");
					}

					sb.deleteCharAt(sb.length() - 1);
				} else {

					int c = 0;
					for (ItemStandard standard : standards) {
						String skey = standard.key;
						for (String v : values) {
							if (skey.equals(v)) {
								sb.append(standard.name).append(",");
								c++;
								break;
							}
						}
					}

					if (c > 0) {
						sb.deleteCharAt(sb.length() - 1);
					}
				}
			}
		}

		return sb.toString();
	}
	
	
	public String toRelationContent(Relation relation, String... values) {

		StringBuilder sb = new StringBuilder(getName());

		sb.append(":");

		if (itemValueDefinition.inputType == InputType.INPUT) {

			
			for(int i = 0;i<values.length;i++) {
				
				if(relation == Relation.EQUAL) {
					
				}
				
				
			}
			
			
			
			
			
			if (itemValueDefinition.template != null) {

				
				
				
				if (values != null && values.length > 0) {
					sb.append(itemValueDefinition.template.replace("{?}", values[0]));
				} else {
					sb.append(itemValueDefinition.template.replace("{?}", "______"));
				}
			}

			if (itemValueDefinition.unit != null) {
				sb.append(itemValueDefinition.unit);
			}

		} else if (itemValueDefinition.inputType == InputType.SELECT) {

			List<ItemStandard> standards = itemValueDefinition.getStandards();
			if (standards.size() > 0) {
				if (values == null || values.length == 0) {

					for (ItemStandard standard : standards) {
						sb.append(standard.name).append(",");
					}

					sb.deleteCharAt(sb.length() - 1);
				} else {

					int c = 0;
					for (ItemStandard standard : standards) {
						String skey = standard.key;
						for (String v : values) {
							if (skey.equals(v)) {
								sb.append(standard.name).append(",");
								c++;
								break;
							}
						}
					}

					if (c > 0) {
						sb.deleteCharAt(sb.length() - 1);
					}
				}
			}
		}

		return sb.toString();
	}
}
