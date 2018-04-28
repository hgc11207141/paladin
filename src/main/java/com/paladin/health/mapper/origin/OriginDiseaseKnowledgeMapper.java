package com.paladin.health.mapper.origin;

import com.paladin.health.model.origin.OriginDiseaseKnowledge;

import org.apache.ibatis.annotations.Param;

import com.paladin.framework.mybatis.CustomMapper;

public interface OriginDiseaseKnowledgeMapper extends CustomMapper<OriginDiseaseKnowledge>{
	
	public Integer getMaxId(@Param("id") int underId);	
	public Integer getContentMaxId(@Param("id") int underId);	

	public int deleteDiseaseKnowledge(@Param("disease") String disease);	
	public int deleteDiseaseContent(@Param("disease") String disease);	

}