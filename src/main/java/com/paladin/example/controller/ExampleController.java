package com.paladin.example.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.paladin.example.controller.pojo.ExampleQuery;
import com.paladin.example.model.OrgUser;
import com.paladin.example.service.ExampleService;
import com.paladin.framework.common.PageResult;
import com.paladin.framework.core.container.ConstantsContainer;
import com.paladin.framework.utils.WebUtil;
import com.paladin.framework.web.response.CommonResponse;


@Controller
@RequestMapping("/")
public class ExampleController {
	
	@Autowired
	ExampleService exampleService;
	
	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public Object loginInput(HttpServletRequest request, HttpServletResponse response) {		
		return "/example/login";
	}

	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public Object login(HttpServletRequest request, HttpServletResponse response, Model model) {
		Subject subject = SecurityUtils.getSubject();
				
		boolean isAjax = WebUtil.isAjaxRequest(request);

		if (subject.isAuthenticated()) {
			if (isAjax) {
				WebUtil.sendJson(response, CommonResponse.getSuccessResponse("登录成功"));
				return null;
			} else {
				return null;
			}
		} else {
			if (isAjax) {
				WebUtil.sendJson(response, CommonResponse.getFailResponse("登录失败,用户名或密码错误！"));
				return null;
			} else {
				model.addAttribute("isError", true);				
				return "/example/login";
			}
		}
	}
	
	
	@RequestMapping(value = "/main")
	public Object login(Model model) {	
		return "/example/main";
	}
	
	@RequestMapping("/search")
	@ResponseBody
	public Object getPage(ExampleQuery query) {
		return CommonResponse.getSuccessResponse(new PageResult(exampleService.searchPage(query)));
	}
	
	@RequestMapping(value = "/edit/input")
	public Object editInput(@RequestParam String id, Model model) {	
		OrgUser user = exampleService.get(id);
		model.addAttribute("user", user);
		return "/example/edit";
	}
	
	@RequestMapping(value = "/save")
	@ResponseBody
	public Object save(Model model) {	
		return CommonResponse.getSuccessResponse();
	}
	
	
	@RequestMapping(value ="/system/constants/enum")
	@ResponseBody
	public Object enumConstants(@RequestParam("code") String[] code) {		
		return CommonResponse.getSuccessResponse(ConstantsContainer.getTypeChildren(code));	
	}
	
}
