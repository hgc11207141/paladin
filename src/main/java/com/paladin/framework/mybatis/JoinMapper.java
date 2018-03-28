package com.paladin.framework.mybatis;

import java.util.List;

import org.apache.ibatis.annotations.SelectProvider;

public interface JoinMapper<T> {

	@SelectProvider(type = JoinSelectProvider.class, method = "dynamicSQL")
    public List<T> selectJoinAll();
	
}
