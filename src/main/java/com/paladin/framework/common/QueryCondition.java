package com.paladin.framework.common;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface QueryCondition {
	
	public String name() default "";
	public QueryType type();
	
	public boolean nullable() default false;
	
	/**
	 * 由于between需要两个值，所以通过该ID找到两个值
	 * @return
	 */
	public String betweenId() default "";
}
