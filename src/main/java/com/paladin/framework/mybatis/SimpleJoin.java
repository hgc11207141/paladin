package com.paladin.framework.mybatis;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface SimpleJoin {
	
	/**
	 * 基于实体类
	 * @return
	 */
	public Class<?> baseType();
	/**
	 * 连接实体类
	 * @return
	 */
	public Class<?> joinType();
	
	/**
	 * 连接字段
	 * @return
	 */
	public String joinField();
	
}
