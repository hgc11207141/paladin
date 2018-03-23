package com.paladin.framework.redis;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * 方便使用redis-template的工厂类
 * @author TontoZhou
 * @since 2018年3月16日
 */
@Component
public class RedisTemplateFactory {

	@Resource
	JedisConnectionFactory jedisConnectionFactory;

	static RedisTemplateFactory factory;

	@Resource(name = "jsonRedisTemplate")
	RedisTemplate<String, Object> jsonRedisTemplate;
	
	@Resource(name = "jdkRedisTemplate")
	RedisTemplate<String, Object> jdkRedisTemplate;
	 
	@Resource(name = "stringRedisTemplate")
	RedisTemplate<String, String> stringRedisTemplate;

	@PostConstruct
	public void setStatic() {
		factory = this;	
	}

	
	public static RedisTemplate<String, Object> getJsonRedisTemplate() {
		return factory.jsonRedisTemplate;
	}

	public static RedisTemplate<String, Object> getJdkRedisTemplate() {
		return factory.jdkRedisTemplate;
	}

	public static RedisTemplate<String, String> getStringRedisTemplate() {
		return factory.stringRedisTemplate;
	}
}
