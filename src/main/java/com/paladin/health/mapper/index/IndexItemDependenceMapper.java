package com.paladin.health.mapper.index;

import com.paladin.health.model.index.IndexItemDependence;
import com.paladin.framework.mybatis.CustomMapper;

public interface IndexItemDependenceMapper extends CustomMapper<IndexItemDependence>{

	public void getDependencyRelation(String itemId);

}