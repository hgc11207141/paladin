package com.paladin.health.controller.index;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.paladin.framework.common.GeneralCriteriaBuilder.Condition;
import com.paladin.framework.common.QueryType;
import com.paladin.framework.exception.BusinessException;
import com.paladin.framework.utils.EnumUtil;
import com.paladin.framework.web.response.CommonResponse;
import com.paladin.health.library.index.ItemType;
import com.paladin.health.library.index.item.ItemValueDefinition.InputType;
import com.paladin.health.model.index.IndexItem;
import com.paladin.health.model.index.IndexItemStandard;
import com.paladin.health.model.index.IndexItemValueDefinition;
import com.paladin.health.service.index.IndexItemDependenceService;
import com.paladin.health.service.index.IndexItemService;
import com.paladin.health.service.index.IndexItemStandardService;
import com.paladin.health.service.index.IndexItemValueDefinitionService;
import com.paladin.health.service.index.pojo.ItemDependenceDetail;

@Controller
@RequestMapping("/health/index")
public class IndexItemController {

	@Autowired
	private IndexItemService itemService;

	@Autowired
	private IndexItemValueDefinitionService itemValueDefinitionService;

	@Autowired
	private IndexItemStandardService itemStandardService;

	@Autowired
	private IndexItemDependenceService itemDependenceService;

	@RequestMapping("/index")
	public String index() {
		return "/health/index/index_item_index";
	}

	@RequestMapping("/item/list")
	@ResponseBody
	public Object itemList() {
		return CommonResponse.getSuccessResponse(itemService.findAll());
	}

	@RequestMapping("/item/detail")
	@ResponseBody
	public Object itemDetail(@RequestParam String itemId) {

		IndexItem item = itemService.get(itemId);

		if (item == null) {
			throw new BusinessException("不存在指标项目[id:" + itemId + "]");
		}

		IndexItemValueDefinition itemValueDefinition = null;
		List<IndexItemStandard> itemStandardList = null;
		List<ItemDependenceDetail> itemDependenceDetailList = null;

		if (EnumUtil.equals(item.getItemType(), ItemType.STANDARD)) {
			List<IndexItemValueDefinition> itemValueDefinitionList = itemValueDefinitionService
					.searchAll(new Condition(IndexItemValueDefinition.COLUMN_FIELD_ITEM_ID, QueryType.EQUAL, itemId));
			if (itemValueDefinitionList == null || itemValueDefinitionList.size() == 0) {
				throw new BusinessException("标准指标项目[id:" + itemId + "]找不到对应的值定义数据");
			}

			itemValueDefinition = itemValueDefinitionList.get(0);

			if (EnumUtil.equals(itemValueDefinition.getInputType(), InputType.SELECT)) {
				itemStandardList = itemStandardService
						.searchAll(new Condition(IndexItemStandard.COLUMN_FIELD_VALUE_DEFINITION_ID, QueryType.EQUAL, itemValueDefinition.getId()));
			}

			itemDependenceDetailList = itemDependenceService.getDependencyRelation(itemId);

		}

		HashMap<String, Object> result = new HashMap<>();
		result.put("item", item);
		result.put("itemValueDefinition", itemValueDefinition);
		result.put("itemStandard", itemStandardList);
		result.put("itemDependenceDetail", itemDependenceDetailList);

		return CommonResponse.getSuccessResponse(result);
	}

}