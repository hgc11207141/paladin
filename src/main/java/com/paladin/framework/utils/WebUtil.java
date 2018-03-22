package com.paladin.framework.utils;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;

public class WebUtil {
	
	public static boolean isAjaxRequest(HttpServletRequest request) {
		return "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
	}

	private static ObjectMapper objectMapper = new ObjectMapper();

	public static void sendJson(HttpServletResponse response, Object obj) {

		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.setHeader("Cache-Control", "no-cache");

		try {
			objectMapper.writeValue(response.getWriter(), obj);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}
