package com.paladin.framework.core;

//import java.beans.PropertyEditorSupport;
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.util.Date;
import java.util.List;

import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
//import org.springframework.web.bind.WebDataBinder;
//import org.springframework.web.bind.annotation.InitBinder;

import com.paladin.framework.web.response.CommonResponse;


public class ControllerSupport {

//	private static EmptyPropertyEditor emptyPropertyEditor = new EmptyPropertyEditor();
//	private static DatePropertyEditor datePropertyEditor = new DatePropertyEditor();
//
//	@InitBinder
//	public void bindingPreparation(WebDataBinder binder) {
//		/*
//		 * 设定String类型空字符串过滤，不一定是最好的方式？
//		 */
//		binder.registerCustomEditor(String.class, emptyPropertyEditor);
//		binder.registerCustomEditor(Date.class, datePropertyEditor);
//	}

	/**
	 * 验证异常处理
	 * 
	 * @param bindingResult
	 * @return
	 */
	public Object validErrorHandler(BindingResult bindingResult) {

		List<FieldError> errors = bindingResult.getFieldErrors();

		String[][] result = new String[errors.size()][3];

		int i = 0;

		for (FieldError error : bindingResult.getFieldErrors()) {
			result[i++] = new String[] { error.getCode(), error.getField(), error.getDefaultMessage() };
		}

		return CommonResponse.getValidFailResponse(result);
	}

	
//	private static class DatePropertyEditor extends PropertyEditorSupport {
//
//		private final static ThreadLocal<SimpleDateFormat> dateFormat = new ThreadLocal<SimpleDateFormat>() {
//			public SimpleDateFormat initialValue() {
//				return new SimpleDateFormat("yyyy-MM-dd");
//			}
//		};
//
//		private final static ThreadLocal<SimpleDateFormat> timeFormat = new ThreadLocal<SimpleDateFormat>() {
//			public SimpleDateFormat initialValue() {
//				return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//			}
//		};
//
//		@Override
//		public void setAsText(String text) {
//			if (text != null) {
//				text = text.trim();
//				if (!text.equals("")) {
//					if (text.length() > 10) {
//						try {
//							setValue(timeFormat.get().parse(text));
//							return;
//						} catch (ParseException e) {
//						}
//					} else {
//						try {
//							setValue(dateFormat.get().parse(text));
//							return;
//						} catch (ParseException e) {
//						}
//					}
//				}
//			}
//			setValue(null);
//		}
//	}
//
//	private static class EmptyPropertyEditor extends PropertyEditorSupport {
//		@Override
//		public void setAsText(String text) {
//			if (text == null) {
//				setValue(null);
//			} else {
//
//				text = text.trim();
//
//				if (text.equals(""))
//					setValue(null);
//				else
//					setValue(text);
//			}
//		}
//	}
}
