package com.paladin.framework.shiro;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.paladin.framework.utils.WebUtil;
import com.paladin.framework.web.response.CommonResponse;

public class AjaxFormAuthenticationFilter extends FormAuthenticationFilter {

	private static final Logger log = LoggerFactory.getLogger(AjaxFormAuthenticationFilter.class);

	protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
		if (isLoginRequest(request, response)) {
			if (isLoginSubmission(request, response)) {
				if (log.isTraceEnabled()) {
					log.trace("Login submission detected.  Attempting to execute login.");
				}

				return executeLogin(request, response);
			} else {
				if (log.isTraceEnabled()) {
					log.trace("Login page view.");
				}

				return true;
			}
		} else {
			if (log.isTraceEnabled()) {
				log.trace("Attempting to access a path which requires authentication.  Forwarding to the " + "Authentication url [" + getLoginUrl() + "]");
			}

			if (WebUtil.isAjaxRequest((HttpServletRequest) request)) {
				WebUtil.sendJson((HttpServletResponse) response, CommonResponse.getUnLoginResponse("未登录或会话超时"));
			} else {
				saveRequestAndRedirectToLogin(request, response);
			}

			return false;
		}
	}

	protected boolean onLoginSuccess(AuthenticationToken token, Subject subject, ServletRequest request, ServletResponse response) throws Exception {

		if (WebUtil.isAjaxRequest((HttpServletRequest) request)) {
			WebUtil.sendJson((HttpServletResponse) response, CommonResponse.getSuccessResponse("成功登录"));
			return false;
		} else {
			issueSuccessRedirect(request, response);
			return false;
		}
	}

	protected boolean onLoginFailure(AuthenticationToken token, AuthenticationException e, ServletRequest request, ServletResponse response) {

		if (WebUtil.isAjaxRequest((HttpServletRequest) request)) {
			WebUtil.sendJson((HttpServletResponse) response, CommonResponse.getUnLoginResponse("登录失败,用户名或密码错误！"));
			return false;
		} else {
			//setFailureAttribute(request, e);
			//request.setAttribute("error", e.getMessage());
			return true;
		}

		
	}
}
