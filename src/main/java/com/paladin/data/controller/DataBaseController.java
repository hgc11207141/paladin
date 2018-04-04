package com.paladin.data.controller;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.paladin.data.model.DBConnection;
import com.paladin.data.service.DBConnectionService;
import com.paladin.framework.common.OffsetPage;
import com.paladin.framework.common.PageResult;
import com.paladin.framework.core.ControllerSupport;
import com.paladin.framework.web.response.CommonResponse;

@Controller
@RequestMapping("/data/connection")
public class DataBaseController extends ControllerSupport{
	
	@Autowired
	DBConnectionService connectionService;
	
	@RequestMapping(value = "/index")
	public String index(HttpServletRequest request) {
		return "/data/connection/index";
	}
	
	@RequestMapping("/search")
	@ResponseBody
	public Object searchAll(OffsetPage query) {
		return CommonResponse.getSuccessResponse(new PageResult(connectionService.searchPage(query)));
	}

	@RequestMapping("/view")
	public String view(@RequestParam String name, Model model) {	
		DBConnection connection  = connectionService.get(name);
		
		if(connection == null) {
			connection = new DBConnection();
		}
		
		model.addAttribute("connection", connection);
		return "data/connection/view";	
	}
	

	@RequestMapping("/add/input")
	public String addInput(Model model) {
		model.addAttribute("connection", new DBConnection());
		return  "data/connection/form";	
	}

	@RequestMapping("/edit/input")
	public String editInput(@RequestParam String name, Model model) {
		DBConnection connection  = connectionService.get(name);
		
		if(connection == null) {
			connection = new DBConnection();
		}
		
		model.addAttribute("connection", connection);
		return  "data/connection/form";	
	}

	@RequestMapping("/save")
	@ResponseBody
	public Object save(@Valid DBConnection orgUser, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			return validErrorHandler(bindingResult);
		}
		return CommonResponse.getResponse(connectionService.saveOrUpdate(orgUser));
	}

	@RequestMapping("/delete")
	@ResponseBody
	public Object delete(@RequestParam String name) {
		return CommonResponse.getResponse(connectionService.removeByPrimaryKey(name), "删除失败");
	}
	
}
