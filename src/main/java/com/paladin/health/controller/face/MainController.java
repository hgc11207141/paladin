package com.paladin.health.controller.face;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.paladin.health.controller.FaceController;

@Controller
@RequestMapping("/face/")
public class MainController extends FaceController{
	
	@RequestMapping(value = "/main")
	public Object main(HttpServletRequest request) {
		return "/face/main";
	}
	
}
