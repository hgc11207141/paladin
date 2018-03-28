package com.paladin.framework.exception;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.paladin.framework.utils.WebUtil;
import com.paladin.framework.web.response.CommonResponse;

@Component
public class ExceptionHandler implements HandlerExceptionResolver {

	private static Logger logger = LoggerFactory.getLogger(ExceptionHandler.class);
	
	private String defaultErrorView;

	public String getDefaultErrorView() {
		return defaultErrorView;
	}

	public void setDefaultErrorView(String defaultErrorView) {
		this.defaultErrorView = defaultErrorView;
	}

	private final static ObjectMapper objectMapper = new ObjectMapper();

	private void sendJson(HttpServletResponse response, Object obj) {

		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.setHeader("Cache-Control", "no-cache");

		try {
			objectMapper.writeValue(response.getOutputStream(), obj);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
		// 需要注释？
		
		String page = null;
		CommonResponse responseObject = null;

		if (ex instanceof BusinessException) {
			responseObject = CommonResponse.getFailResponse(ex.getMessage());
			page = "/business_error";
		} else if (ex instanceof BusinessException) {
			responseObject = CommonResponse.getErrorResponse(ex.getMessage());
			page = "/system_error";
		} else {
			responseObject = CommonResponse.getErrorResponse("系统未知异常");
			page = "/error";
			
			logger.error("系统未知异常",ex);
		}
		
		if(logger.isDebugEnabled()) {
			ex.printStackTrace();
		}

		if (WebUtil.isAjaxRequest(request)) {
			sendJson(response, responseObject);
		} else {
			return new ModelAndView(page, "error", responseObject.getResult());
		}

		return null;
	}

}
