package com.paladin.framework.utils.properties.copy;

import java.lang.reflect.Method;
import java.util.Map;

import com.paladin.framework.utils.reflect.NameUtil;
import com.paladin.framework.utils.reflect.ReflectUtil;

public class PropertyCopy {

	
	public static <T> T copy(Class<T> targetClass, Object source)
	{						
		try {
			T target = targetClass.newInstance();
			copy(target,source,null,null);
			return target;
		} catch (InstantiationException | IllegalAccessException e) {
			return null;
		}		
	}
	
	public static void copy(Object target, Object source)
	{		
		copy(target,source,null,null);
	}
	
	
	public static void copy(Object target, Object source, String prefix, Map<String, String> convertNameMap) {
		
		if (target == null)
			throw new IllegalArgumentException("Target Object can't be null");

		if (source == null)
			throw new IllegalArgumentException("Source Object can't be null");

		Class<?> targetClass = target.getClass();
		Class<?> sourceClass = source.getClass();

		Method[] targetMethods = targetClass.getMethods();

		for (Method targetMethod : targetMethods) {
			if (ReflectUtil.isSetMethod(targetMethod)) {

				String name = NameUtil.removeGetOrSet(targetMethod.getName());
				
				//对于名称不一样的属性，通过转换名称取值
				if (convertNameMap != null) {
					String cname = convertNameMap.get(prefix == null ? name : prefix + "." + name);
					if (cname != null)
						name = cname;
				}

				Method getMethod = ReflectUtil.getGetMethod(name, sourceClass);
				if (getMethod != null) {

					try {
						Object getValue = getMethod.invoke(source);

						if (getValue != null) {

							Class<?> valueClass = getValue.getClass();
							Class<?> setClass = targetMethod.getParameterTypes()[0];
							if (!setClass.isAssignableFrom(valueClass)) {
								if (setClass == String.class) {
									getValue = getValue.toString();
								} else {
									
									// 这里省略了基础类型间的转换，待补充
									// 
									
									if (ReflectUtil.isBaseClass(valueClass) || ReflectUtil.isBaseClass(setClass)) {
										continue;
									}
									
									// 都是非基础类型，则进行递归COPY
									Object subTarget = setClass.newInstance();
									copy(subTarget, getValue, name, convertNameMap);
									getValue = subTarget;
								}

							}
						}

						targetMethod.invoke(target, getValue);
					} catch (Exception e) {
						continue;
					}
				}

			}

		}

		
		
	
		
	}

	// private Object convertValue(Object value, Class<?> toClass)
	// {
	// if(toClass == String.class)
	// return value.toString();
	//
	// Class<?> fromClass = value.getClass();
	//
	// if(Number.class.isAssignableFrom(toClass))
	// {
	// if(fromClass == String.class)
	//
	//
	// }
	// }

}
