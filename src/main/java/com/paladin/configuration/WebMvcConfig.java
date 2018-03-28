package com.paladin.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.paladin.framework.exception.ExceptionHandler;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Value("${web.upload.path.file}")
	private String webUploadPathFile;

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {

		String fielPath = "file:" + webUploadPathFile;
		String staticPath = "classpath:/static/";

		registry.addResourceHandler("/static/**").addResourceLocations(staticPath);
		registry.addResourceHandler("/file/**").addResourceLocations(fielPath);
		registry.addResourceHandler("/favicon.ico").addResourceLocations("classpath:favicon.ico");
		
		logger.info("文件资源存放地址：" + fielPath);
		logger.info("静态资源存放地址：" + staticPath);

	}

	@Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("forward:/face/main");
        registry.setOrder(Ordered.HIGHEST_PRECEDENCE);       
    } 
    
    @Bean(name = "handlerExceptionResolver")
    public HandlerExceptionResolver exceptionHandler(){
        logger.debug("WebMvcConfig.exceptionHandler()");
        return new ExceptionHandler();
    }
    
}
