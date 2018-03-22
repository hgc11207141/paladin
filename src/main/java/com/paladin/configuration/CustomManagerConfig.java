package com.paladin.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.paladin.framework.spring.SpringBeanHelper;
import com.paladin.framework.spring.SpringContainerManager;


@Configuration
@EnableTransactionManagement
public class CustomManagerConfig {
	
	@Bean
	public SpringContainerManager springContainerManager() {
		return new SpringContainerManager();
	}
	
	@Bean
	public SpringBeanHelper springBeanHolder() {
		return new SpringBeanHelper();
	}
	
}
