package com.paladin.health.service.index;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.paladin.health.library.Relation;
import com.paladin.health.library.index.item.ItemValueDefinition.InputType;
import com.paladin.health.mapper.index.IndexItemStandardMapper;
import com.paladin.health.model.index.IndexItem;
import com.paladin.health.model.index.IndexItemDependence;
import com.paladin.health.model.index.IndexItemStandard;
import com.paladin.health.model.index.IndexItemValueDefinition;
import com.paladin.health.service.index.pojo.ItemDependenceDetail;
import com.paladin.framework.common.QueryType;
import com.paladin.framework.common.GeneralCriteriaBuilder.Condition;
import com.paladin.framework.core.ServiceSupport;
import com.paladin.framework.exception.BusinessException;
import com.paladin.framework.utils.EnumUtil;

@Service
public class IndexItemDependenceService extends ServiceSupport<IndexItemDependence> {

	@Autowired
	private IndexItemStandardMapper itemStandardMapper;

	@Autowired
	private IndexItemService itemService;

	@Autowired
	private IndexItemValueDefinitionService itemValueDefinitionService;

	public List<ItemDependenceDetail> getDependencyRelation(String itemId) {

		List<IndexItemDependence> dependences = searchAll(new Condition(IndexItemDependence.COLUMN_FIELD_TARGET_ID, QueryType.EQUAL, itemId));
		List<ItemDependenceDetail> dependenceDetails = new ArrayList<>(dependences.size());

		for (IndexItemDependence dependence : dependences) {
			/*
			 * 查找依赖项目详情
			 */
			String dependenceItemId = dependence.getDependenceId();					
			IndexItem dependenceItem = itemService.get(dependenceItemId);
						
			List<IndexItemValueDefinition> itemValueDefinitionList = itemValueDefinitionService
					.searchAll(new Condition(IndexItemValueDefinition.COLUMN_FIELD_ITEM_ID, QueryType.EQUAL, dependenceItemId));
			if (itemValueDefinitionList == null || itemValueDefinitionList.size() == 0) {
				throw new BusinessException("标准指标项目[id:" + itemId + "]找不到对应的值定义数据");
			}
			
			IndexItemValueDefinition itemValueDefinition= itemValueDefinitionList.get(0);		
			ItemDependenceDetail detail = new ItemDependenceDetail(dependenceItem, itemValueDefinition, dependence);
			
			String valueDefinitionId = itemValueDefinition.getId();
			String inputType = itemValueDefinition.getInputType();
			String relation = dependence.getDependenceRelation();

			if(EnumUtil.equals(inputType, InputType.SELECT)) {

				if (EnumUtil.equals(relation, Relation.EQUAL, Relation.NOT_EQUAL)) {

					String key = dependence.getDependenceValue();
					IndexItemStandard standard = itemStandardMapper.getValueDefinitionStandard(valueDefinitionId, key);

					if (standard != null) {
						detail.setDependenceStandard(standard);
					}

				} else if (EnumUtil.equals(relation, Relation.IN)) {

					String keyStr = dependence.getDependenceValue();
					String[] keys = keyStr.split(",");

					List<IndexItemStandard> standards = itemStandardMapper.findValueDefinitionStandard(valueDefinitionId);
					List<IndexItemStandard> validStandards = new ArrayList<>();

					for (String key : keys) {
						for (IndexItemStandard standard : standards) {
							if (standard.getStandardKey().equals(key)) {
								validStandards.add(standard);
								break;
							}
						}
					}

					detail.setDependenceStandard(validStandards);
				}
			}
			

			dependenceDetails.add(detail);
		}

		return dependenceDetails;
	}

}