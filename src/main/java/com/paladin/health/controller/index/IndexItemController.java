package com.paladin.health.controller.index;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/health/index")
public class IndexItemController{

	@RequestMapping("/index")
	public String index() {
		return "/health/index/index_item_index";
	}
	
}