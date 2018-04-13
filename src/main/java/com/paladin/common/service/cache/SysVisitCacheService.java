package com.paladin.common.service.cache;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.paladin.common.mapper.cache.SysVisitCacheMapper;
import com.paladin.common.model.cache.SysVisitCache;
import com.paladin.framework.core.ServiceSupport;

@Service
public class SysVisitCacheService extends ServiceSupport<SysVisitCache> {

	@Autowired
	SysVisitCacheMapper visitCacheMapper;

	public int putCache(HttpServletRequest request, String key, String content) {

		if (content == null || content.length() == 0 || content.length() > 400) {
			return 0;
		}

		String ip = getIpAddress(request);

		if (ip == null || ip.length() == 0) {
			return 0;
		}

		SysVisitCache cache = visitCacheMapper.getCache(key, ip);
		
		if(cache == null) {
			cache = new SysVisitCache();		
			cache.setCode(key);
			cache.setIp(ip);
		}
		
		cache.setValue(content);

		return saveOrUpdate(cache);
	}

	public String getCache(HttpServletRequest request, String key) {

		String ip = getIpAddress(request);

		if (ip == null || ip.length() == 0) {
			return null;
		}

		SysVisitCache cache = visitCacheMapper.getCache(key, ip);
		return cache == null ? null : cache.getValue();
	}

	/**
	 * 获取用户真实IP地址，不使用request.getRemoteAddr();的原因是有可能用户使用了代理软件方式避免真实IP地址, 参考文章：
	 * http://developer.51cto.com/art/201111/305181.htm
	 * 
	 * 可是，如果通过了多级反向代理的话，X-Forwarded-For的值并不止一个，而是一串IP值，究竟哪个才是真正的用户端的真实IP呢？
	 * 答案是取X-Forwarded-For中第一个非unknown的有效IP字符串。
	 * 
	 * 如：X-Forwarded-For：192.168.1.110, 192.168.1.120, 192.168.1.130, 192.168.1.100
	 * 
	 * 用户真实IP为： 192.168.1.110
	 * 
	 * @param request
	 * @return
	 */
	public static String getIpAddress(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_CLIENT_IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_X_FORWARDED_FOR");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		return ip;
	}

}