package com.paladin.health.service.origin;

import java.util.List;

import org.springframework.stereotype.Service;

import com.paladin.health.model.origin.OriginDiseaseName;
import com.paladin.framework.common.GeneralCriteriaBuilder;
import com.paladin.framework.common.QueryType;
import com.paladin.framework.core.ServiceSupport;

@Service
public class OriginDiseaseNameService extends ServiceSupport<OriginDiseaseName> {

	public List<OriginDiseaseName> findAllDiseaseName() {
		return searchAll(new GeneralCriteriaBuilder.Condition(OriginDiseaseName.COLUMN_FIELD_TYPE, QueryType.EQUAL, OriginDiseaseName.TYPE_DISEASE));
	}

	public List<OriginDiseaseName> findAllSymptomName() {
		return searchAll(new GeneralCriteriaBuilder.Condition(OriginDiseaseName.COLUMN_FIELD_TYPE, QueryType.EQUAL, OriginDiseaseName.TYPE_SYMPTOM));
	}
	
}