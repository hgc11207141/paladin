package com.paladin.configuration;

import java.util.Properties;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.paladin.framework.mybatis.CustomMapper;
import com.paladin.framework.mybatis.JoinMapper;

import tk.mybatis.spring.mapper.MapperScannerConfigurer;

/**
 * MyBatis扫描接口，使用的tk.mybatis.spring.mapper.MapperScannerConfigurer，如果你不使用通用Mapper
 */
@Configuration
// TODO 注意，由于MapperScannerConfigurer执行的比较早，所以必须有下面的注解
@AutoConfigureAfter(MyBatisConfiguration.class)
public class MyBatisMapperScannerConfig {

	@Bean
	public MapperScannerConfigurer mapperScannerConfigurer() {

		MapperScannerConfigurer mapperScannerConfigurer = new MapperScannerConfigurer();
		mapperScannerConfigurer.setSqlSessionFactoryBeanName("sqlSessionFactory");
		mapperScannerConfigurer.setBasePackage("com.paladin.health.mapper,com.paladin.common.mapper,com.paladin.data.mapper");

		Properties mapperProperties = new Properties();
		mapperProperties.setProperty("mappers", CustomMapper.class.getName() + "," + JoinMapper.class.getName());
		mapperProperties.setProperty("notEmpty", "false");
		mapperProperties.setProperty("IDENTITY", "MYSQL");
		mapperScannerConfigurer.setProperties(mapperProperties);

		return mapperScannerConfigurer;
	}

}
