package com.paladin.health.service.origin;

import java.util.List;

import org.springframework.stereotype.Service;

import com.paladin.health.model.origin.OriginDiseaseKnowledge;
import com.paladin.framework.common.GeneralCriteriaBuilder;
import com.paladin.framework.common.QueryType;
import com.paladin.framework.core.ServiceSupport;

@Service
public class OriginDiseaseKnowledgeService extends ServiceSupport<OriginDiseaseKnowledge> {

	public List<OriginDiseaseKnowledge> findAllDiseaseKnowledge(String diseaseKey) {
		return searchAll(new GeneralCriteriaBuilder.Condition(OriginDiseaseKnowledge.COLUMN_FIELD_DISEASE_KEY, QueryType.EQUAL, diseaseKey));
	}
}