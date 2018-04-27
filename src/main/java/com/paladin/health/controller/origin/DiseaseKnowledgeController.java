package com.paladin.health.controller.origin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.paladin.framework.web.response.CommonResponse;
import com.paladin.health.service.origin.OriginDiseaseKnowledgeContentService;
import com.paladin.health.service.origin.OriginDiseaseKnowledgeService;
import com.paladin.health.service.origin.OriginDiseaseNameService;

@Controller
@RequestMapping("/health/knowledge")
public class DiseaseKnowledgeController {

	@Autowired
	OriginDiseaseKnowledgeService diseaseKnowledgeService;

	@Autowired
	OriginDiseaseNameService diseaseNameService;

	@Autowired
	OriginDiseaseKnowledgeContentService diseaseKnowledgeContentService;

	@RequestMapping("/index")
	public String index() {
		return "/health/origin/disease_knowledge_index";
	}

	@RequestMapping("/list/disease")
	@ResponseBody
	public Object diseaseList() {
		return CommonResponse.getSuccessResponse(diseaseNameService.findAllDiseaseName());
	}

	@RequestMapping("/list/knowledge")
	@ResponseBody
	public Object knowledgeList(@RequestParam String diseaseKey) {
		return CommonResponse.getSuccessResponse(diseaseKnowledgeService.findAllDiseaseKnowledge(diseaseKey));
	}

	@RequestMapping("/knowledge/content")
	@ResponseBody
	public Object knowledgeContent(@RequestParam String knowledgeId) {
		return CommonResponse.getSuccessResponse(diseaseKnowledgeContentService.findKnowledgeContent(knowledgeId));
	}

	@RequestMapping("/disease/content")
	@ResponseBody
	public Object diseaseContent(@RequestParam String diseaseKey) {
		return CommonResponse.getSuccessResponse(diseaseKnowledgeContentService.findDiseaseContent(diseaseKey));
	}
	
}
