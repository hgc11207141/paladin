package com.paladin.health.mapper.index;

import com.paladin.health.model.index.IndexItemStandard;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.paladin.framework.mybatis.CustomMapper;

public interface IndexItemStandardMapper extends CustomMapper<IndexItemStandard>{

	/**
	 * 找到项目标准选项
	 * @param itemId
	 * @return
	 */
	List<IndexItemStandard> findValueDefinitionStandard(@Param("valueDefinitionId") String valueDefinitionId);

	/**
	 * 找到项目指定标准选项
	 * @param itemId
	 * @param key
	 * @return
	 */
	IndexItemStandard getValueDefinitionStandard(@Param("valueDefinitionId") String valueDefinitionId, @Param("standardKey") String key);
}