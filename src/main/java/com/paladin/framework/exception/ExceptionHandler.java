package com.paladin.framework.exception;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.paladin.framework.utils.WebUtil;
import com.paladin.framework.web.response.CommonResponse;


@Component
public class ExceptionHandler extends ExceptionHandlerExceptionResolver {

	private String defaultErrorView;

	public String getDefaultErrorView() {
		return defaultErrorView;
	}

	public void setDefaultErrorView(String defaultErrorView) {
		this.defaultErrorView = defaultErrorView;
	}

	@Override
	protected ModelAndView doResolveHandlerMethodException(HttpServletRequest request, HttpServletResponse response,
			HandlerMethod handlerMethod, Exception ex) {

		String page = null;
		CommonResponse responseObject = null;
		
		if(ex instanceof BusinessException) {
			responseObject = CommonResponse.getFailResponse(ex.getMessage());
			page = "/business_error";
		} else if(ex instanceof BusinessException) {
			responseObject = CommonResponse.getErrorResponse(ex.getMessage());
			page = "/system_error";
		} else {
			responseObject = CommonResponse.getErrorResponse("系统未知异常");
			page = "/error";
		}		
		
		if(WebUtil.isAjaxRequest(request)) {
			sendJson(response, responseObject);
		} else {
			return new ModelAndView(page, "error", responseObject.getResult());
		}
		
		return null;
	}

	private final static ObjectMapper objectMapper = new ObjectMapper();

	private void sendJson(HttpServletResponse response, Object obj) {

		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.setHeader("Cache-Control", "no-cache");

		try {
			objectMapper.writeValue(response.getWriter(), obj);
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
