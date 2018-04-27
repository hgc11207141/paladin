package com.paladin.health.service.origin;

import java.util.List;

import org.springframework.stereotype.Service;

import com.paladin.health.model.origin.OriginDiseaseKnowledgeContent;
import com.paladin.framework.common.GeneralCriteriaBuilder;
import com.paladin.framework.common.QueryType;
import com.paladin.framework.core.ServiceSupport;

@Service
public class OriginDiseaseKnowledgeContentService extends ServiceSupport<OriginDiseaseKnowledgeContent> {

	public List<OriginDiseaseKnowledgeContent> findKnowledgeContent(String knowledgeId) {
		return searchAll(new GeneralCriteriaBuilder.Condition(OriginDiseaseKnowledgeContent.COLUMN_FIELD_KNOWLEDGE_ID, QueryType.EQUAL, knowledgeId));
	}

	public List<OriginDiseaseKnowledgeContent> findDiseaseContent(String diseaseKey) {
		return searchAll(new GeneralCriteriaBuilder.Condition(OriginDiseaseKnowledgeContent.COLUMN_FIELD_DISEASE_KEY, QueryType.EQUAL, diseaseKey));
	}
	
}