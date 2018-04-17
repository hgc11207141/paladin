package com.paladin.configuration;

import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.authc.pam.AuthenticationStrategy;
import org.apache.shiro.authc.pam.FirstSuccessfulStrategy;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.SessionContext;
import org.apache.shiro.session.mgt.SessionFactory;
import org.apache.shiro.session.mgt.SessionKey;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.filter.mgt.FilterChainManager;
import org.apache.shiro.web.filter.mgt.FilterChainResolver;
import org.apache.shiro.web.filter.mgt.PathMatchingFilterChainResolver;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.mgt.WebSecurityManager;
import org.apache.shiro.web.servlet.AbstractShiroFilter;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.apache.shiro.web.session.mgt.WebSessionKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import com.paladin.framework.shiro.AjaxFormAuthenticationFilter;
import com.paladin.health.core.SysUserRealm;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

/**
 * 
 * <h2>shiro配置</h2>
 * <p>
 * 修改了部分shiro的代码，从而提高效率，减少session的重复读取
 * </p>
 * 
 * @author TontoZhou
 * @since 2018年3月21日
 */
@Configuration
public class ShiroConfiguration {

	private static Logger logger = LoggerFactory.getLogger(ShiroConfiguration.class);

	@Bean(name = "redisSessionDAO")
	public ShiroRedisSessionDAO getRedisSessionDAO() {
		logger.debug("ShiroConfiguration.redisSessionDAO()");
		ShiroRedisSessionDAO sessionDao = new ShiroRedisSessionDAO();
		return sessionDao;
	}

	@Bean
	public Realm getUserRealm() {
		logger.debug("ShiroConfiguration.getUserRealm()");

		SysUserRealm realm = new SysUserRealm();

		HashedCredentialsMatcher hashedCredentialsMatcher = new HashedCredentialsMatcher();
		hashedCredentialsMatcher.setHashAlgorithmName("md5");// 散列算法:这里使用MD5算法;
		hashedCredentialsMatcher.setHashIterations(1);// 散列的次数，当于 m比如散列两次，相d5("");

		realm.setCredentialsMatcher(hashedCredentialsMatcher);

		return realm;
	}

	@Bean
	public ShiroConfigProperties getShiroConfigProperties() {
		logger.debug("ShiroConfiguration.getShiroConfigProperties()");
		return new ShiroConfigProperties();
	}

	/**
	 * @see DefaultWebSessionManager
	 * @return
	 */
	@Bean(name = "sessionManager")
	public DefaultWebSessionManager defaultWebSessionManager() {
		logger.debug("ShiroConfiguration.defaultWebSessionManager()");

		DefaultWebSessionManager sessionManager = null;

		ShiroConfigProperties shiroConfigProperties = getShiroConfigProperties();

		if (shiroConfigProperties.isClusterShare()) {

			sessionManager = new DefaultWebSessionManager() {

				/**
				 * 重写检索session方法，在request上缓存session对象，从而减少session的读取
				 */
				protected Session retrieveSession(SessionKey sessionKey) throws UnknownSessionException {

					Serializable sessionId = getSessionId(sessionKey);
					if (sessionId == null) {
						if (logger.isDebugEnabled()) {
							logger.debug("Unable to resolve session ID from SessionKey [{}].  Returning null to indicate a session could not be found.",
									sessionKey);
						}

						return null;
					}

					/*
					 * 首先从request中获取session，否则从数据库中检索
					 */

					ServletRequest request = null;
					if (sessionKey instanceof WebSessionKey) {
						request = ((WebSessionKey) sessionKey).getServletRequest();
					}

					if (request != null) {
						Object s = request.getAttribute(sessionId.toString());
						if (s != null) {
							return (Session) s;
						}
					}

					Session s = retrieveSessionFromDataSource(sessionId);
					if (s == null) {
						// session ID was provided, meaning one is expected to be found, but
						// we couldn't find one:
						String msg = "Could not find session with ID [" + sessionId + "]";
						throw new UnknownSessionException(msg);
					}

					// 保存session到request
					if (request != null) {
						request.setAttribute(sessionId.toString(), s);
					}

					return s;
				}

			};

			/**
			 * 如果设置集群共享session，需要redis来存放session
			 */
			sessionManager.setSessionDAO(getRedisSessionDAO());
			sessionManager.setSessionFactory(new SessionFactory() {

				/*
				 * 使用 {@link com.paladin.configuration.ShiroRedisSessionDAO.ControlledSession}
				 * 控制session update次数
				 */

				@Override
				public Session createSession(SessionContext initData) {
					if (initData != null) {
						String host = initData.getHost();
						if (host != null) {

							if (logger.isDebugEnabled()) {
								logger.debug("创建ControlledSession[HOST:" + host + "]");
							}

							return new com.paladin.configuration.ShiroRedisSessionDAO.ControlledSession(host);
						}
					}

					if (logger.isDebugEnabled()) {
						logger.info("创建ControlledSession[HOST:无]");
					}
					return new com.paladin.configuration.ShiroRedisSessionDAO.ControlledSession();
				}

			});
		} else {
			sessionManager = new DefaultWebSessionManager();
		}

		// session 监听
		// Collection<SessionListener> sessionListeners = new ArrayList<>();
		// sessionListeners.add(new CustomSessionListener());
		// sessionManager.setSessionListeners(sessionListeners);

		// 单位为毫秒（1秒=1000毫秒） 3600000毫秒为1个小时
		sessionManager.setSessionValidationInterval(3600000);
		// 3600000 milliseconds = 1 hour
		sessionManager.setGlobalSessionTimeout(3600000);
		// 是否删除无效的，默认也是开启
		sessionManager.setDeleteInvalidSessions(true);
		// 是否开启 检测，默认开启
		sessionManager.setSessionValidationSchedulerEnabled(true);

		// 是否在url上显示检索得到的sessionid
		sessionManager.setSessionIdUrlRewritingEnabled(true);

		return sessionManager;
	}

	/**
	 * @see org.apache.shiro.mgt.SecurityManager
	 * @return
	 */
	@Bean(name = "securityManager")
	public DefaultWebSecurityManager getDefaultWebSecurityManage(DefaultWebSessionManager defaultWebSessionManager) {
		logger.debug("ShiroConfiguration.getDefaultWebSecurityManage()");

		DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
		securityManager.setRealm(getUserRealm());

		// 注入缓存管理器;
		// securityManager.setCacheManager(redisCacheManager());
		securityManager.setSessionManager(defaultWebSessionManager());
		return securityManager;
	}

	/**
	 * ShiroFilterFactoryBean 处理拦截资源文件问题。 注意：单独一个ShiroFilterFactoryBean配置是或报错的，以为在
	 * 初始化ShiroFilterFactoryBean的时候需要注入：SecurityManager Filter Chain定义说明
	 * 1、一个URL可以配置多个Filter，使用逗号分隔 2、当设置多个过滤器时，全部验证通过，才视为通过 3、部分过滤器可指定参数，如perms，roles
	 */
	@Bean(name = "shiroFilter")
	public ShiroFilterFactoryBean shirFilter(DefaultWebSecurityManager securityManager) {
		logger.debug("ShiroConfiguration.shirFilter()");

		// ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();

		// 如果有shiro过滤有明确前缀，而不是默认的"\*"，则不需要改写shiro去手动排除某些请求 ShiroFilterFactoryBean
		ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean() {
			/*
			 * 在请求进入shiro流程前，判断请求是否为静态资源，如果是则跳出shiro流程
			 */
			protected AbstractShiroFilter createInstance() throws Exception {

				SecurityManager securityManager = getSecurityManager();
				if (securityManager == null) {
					String msg = "SecurityManager property must be set.";
					throw new BeanInitializationException(msg);
				}

				if (!(securityManager instanceof WebSecurityManager)) {
					String msg = "The security manager does not implement the WebSecurityManager interface.";
					throw new BeanInitializationException(msg);
				}

				FilterChainManager manager = createFilterChainManager();

				PathMatchingFilterChainResolver chainResolver = new PathMatchingFilterChainResolver();
				chainResolver.setFilterChainManager(manager);

				return new UnStaticShiroFilter((WebSecurityManager) securityManager, chainResolver);
			}

			class UnStaticShiroFilter extends AbstractShiroFilter {

				protected UnStaticShiroFilter(WebSecurityManager webSecurityManager, FilterChainResolver resolver) {
					super();
					if (webSecurityManager == null) {
						throw new IllegalArgumentException("WebSecurityManager property cannot be null.");
					}
					setSecurityManager(webSecurityManager);
					if (resolver != null) {
						setFilterChainResolver(resolver);
					}
				}

				protected void doFilterInternal(ServletRequest servletRequest, ServletResponse servletResponse, final FilterChain chain)
						throws ServletException, IOException {

					// -------------- add by TontoZhou-----------------
					if (servletRequest instanceof HttpServletRequest) {
						HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
						String requestUrl = httpRequest.getRequestURI();

						// 过滤静态资源，防止静态资源读取session等操作
						if (!requestUrl.startsWith("/health/")) {
							chain.doFilter(servletRequest, servletResponse);
							return;
						}
					}
					// -------------- add by TontoZhou -----------------

					super.doFilterInternal(servletRequest, servletResponse, chain);
				}
			}

		};

		// 必须设置 SecurityManager
		shiroFilterFactoryBean.setSecurityManager(securityManager);
		// 如果不设置默认会自动寻找Web工程根目录下的"/login.jsp"页面
		shiroFilterFactoryBean.setLoginUrl("/health/login");
		// 登录成功后要跳转的链接
		shiroFilterFactoryBean.setSuccessUrl("/health/index");
		// 未授权界面;
		shiroFilterFactoryBean.setUnauthorizedUrl("/no_permission.html");

		// 增加自定义过滤
		Map<String, Filter> filters = new HashMap<>();
		filters.put("authc", new AjaxFormAuthenticationFilter());

		shiroFilterFactoryBean.setFilters(filters);
		// 拦截器.
		Map<String, String> filterChainDefinitionMap = new LinkedHashMap<String, String>();

		// 配置退出过滤器,其中的具体的退出代码Shiro已经替我们实现了
		/**
		 * anon（匿名） org.apache.shiro.web.filter.authc.AnonymousFilter authc（身份验证）
		 * org.apache.shiro.web.filter.authc.FormAuthenticationFilter
		 * authcBasic（http基本验证）org.apache.shiro.web.filter.authc.BasicHttpAuthenticationFilter
		 * org.apache.shiro.web.filter.authc.LogoutFilter logout（退出）
		 * org.apache.shiro.web.filter.session.NoSessionCreationFilter
		 * noSessionCreation（不创建session）
		 * org.apache.shiro.web.filter.authz.PermissionsAuthorizationFilter perms(许可验证)
		 * org.apache.shiro.web.filter.authz.PortFilter port（端口验证）
		 * org.apache.shiro.web.filter.authz.HttpMethodPermissionFilter rest (rest方面)
		 * org.apache.shiro.web.filter.authz.RolesAuthorizationFilter roles（权限验证）
		 * org.apache.shiro.web.filter.authz.SslFilter ssl （ssl方面）
		 * org.apache.shiro.web.filter.authc.UserFilter user member（用户方面）
		 * 表示用户不一定已通过认证,只要曾被Shiro记住过登录状态的用户就可以正常发起请求,比如rememberMe
		 */

		// <!-- 过滤链定义，从上向下顺序执行，一般将 /**放在最为下边 -->:这是一个坑呢，一不小心代码就不好使了;
		// <!-- authc:所有url都必须认证通过才可以访问; anon:所有url都都可以匿名访问-->
		// filterChainDefinitionMap.put("/static/**", "anon");
		// filterChainDefinitionMap.put("/file/**", "anon");
		// filterChainDefinitionMap.put("/favicon.ico", "anon");

		filterChainDefinitionMap.put("/health/logout", "logout");
		// 配置记住我或认证通过可以访问的地址
		filterChainDefinitionMap.put("/**", "authc");

		shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);

		return shiroFilterFactoryBean;
	}
	
//  该方法可以手动设置filter，从而修改shiro的urlpatterns，但是并没实现，还需要研究
//	@SuppressWarnings({ "unchecked", "rawtypes" })
//	@Bean
//	public FilterRegistrationBean filterRegistrationBean() {
//		FilterRegistrationBean filterRegistration = new FilterRegistrationBean();
//		filterRegistration.setFilter(new DelegatingFilterProxy("shiroFilter"));
//		// 该值缺省为false,表示生命周期由SpringApplicationContext管理,设置为true则表示由ServletContainer管理
//		// filterRegistration.addInitParameter("targetFilterLifecycle", "true");
//		filterRegistration.setEnabled(true);
//		filterRegistration.addUrlPatterns("/manage/*");// 可以自己灵活的定义很多，避免一些根本不需要被Shiro处理的请求被包含进来
//		return filterRegistration;
//	}

	/**
	 * Shiro默认提供了三种 AuthenticationStrategy 实现： AtLeastOneSuccessfulStrategy
	 * ：其中一个通过则成功。 FirstSuccessfulStrategy ：其中一个通过则成功，但只返回第一个通过的Realm提供的验证信息。
	 * AllSuccessfulStrategy ：凡是配置到应用中的Realm都必须全部通过。 authenticationStrategy
	 * 
	 * @return
	 */
	@Bean(name = "authenticationStrategy")
	public AuthenticationStrategy authenticationStrategy() {
		logger.debug("ShiroConfiguration.authenticationStrategy()");
		return new FirstSuccessfulStrategy();
	}

	/**
	 * 开启shiro aop注解支持. 使用代理方式;所以需要开启代码支持;
	 * 
	 * @param securityManager
	 * @return
	 */
	@Bean(name = "authorizationAttributeSourceAdvisor")
	public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(DefaultWebSecurityManager securityManager) {
		logger.debug("ShiroConfiguration.authorizationAttributeSourceAdvisor()");

		AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
		authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
		return authorizationAttributeSourceAdvisor;
	}

	/**
	 * 注入LifecycleBeanPostProcessor
	 * 
	 * @return
	 */
	@Bean(name = "lifecycleBeanPostProcessor")
	public LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
		logger.debug("ShiroConfiguration.lifecycleBeanPostProcessor()");
		return new LifecycleBeanPostProcessor();
	}

	@ConditionalOnMissingBean
	@Bean(name = "defaultAdvisorAutoProxyCreator")
	@DependsOn("lifecycleBeanPostProcessor")
	public DefaultAdvisorAutoProxyCreator getDefaultAdvisorAutoProxyCreator() {
		logger.debug("ShiroConfiguration.getDefaultAdvisorAutoProxyCreator()");
		DefaultAdvisorAutoProxyCreator daap = new DefaultAdvisorAutoProxyCreator();
		daap.setProxyTargetClass(true);
		return daap;
	}

}
