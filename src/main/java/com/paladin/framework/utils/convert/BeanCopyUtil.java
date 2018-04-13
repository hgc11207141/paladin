package com.paladin.framework.utils.convert;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.paladin.framework.utils.reflect.NameUtil;
import com.paladin.framework.utils.reflect.ReflectUtil;
import com.paladin.framework.utils.structure.SecMap;

public class BeanCopyUtil {

	private static SecMap<Class<?>, Class<?>, List<CopyUnit>> convertMap = new SecMap<>();

	public static <T> List<T> simpleCopyList(List<?> sourceList, Class<T> targetType) {
		if (sourceList.size() > 0) {

			Class<?> sourceType = sourceList.get(0).getClass();
			List<CopyUnit> copyUnits = getCopyUnit(sourceType, targetType);

			List<T> targetList = new ArrayList<>(sourceList.size());

			for (Object source : sourceList) {
				try {
					T target = targetType.newInstance();

					for (CopyUnit copyUnit : copyUnits) {

						Object value = copyUnit.getMethod.invoke(source);
						copyUnit.setMethod.invoke(target, value);

					}

					targetList.add(target);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return targetList;
		} else {
			return new ArrayList<>();
		}
	}

	public static Object simpleCopy(Object source, Class<?> targetType) {

		Object target = null;

		try {
			target = targetType.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}

		simpleCopy(source, target);

		return target;
	}

	public static void simpleCopy(Object source, Object target) {

		if (source == null || target == null) {
			return;
		}

		Class<?> sourceType = source.getClass();
		Class<?> targetType = target.getClass();

		List<CopyUnit> copyUnits = getCopyUnit(sourceType, targetType);
		for (CopyUnit copyUnit : copyUnits) {
			try {
				Object value = copyUnit.getMethod.invoke(source);
				copyUnit.setMethod.invoke(target, value);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private static List<CopyUnit> getCopyUnit(Class<?> sourceType, Class<?> targetType) {

		List<CopyUnit> copyUnits = convertMap.get(sourceType, targetType);

		if (copyUnits == null) {

			synchronized (convertMap) {
				copyUnits = convertMap.get(sourceType, targetType);
				if (copyUnits == null) {

					Method[] sourceMethods = sourceType.getMethods();
					Method[] targetMethods = targetType.getMethods();

					HashMap<String, Method> sourceGetMethodMap = new HashMap<>();

					for (Method method : sourceMethods) {
						if (ReflectUtil.isGetMethod(method)) {
							String methodName = method.getName();
							String name = NameUtil.removeGetOrSet(methodName);
							sourceGetMethodMap.put(name, method);
							
							if(methodName.startsWith("i")) {
								sourceGetMethodMap.put(methodName, method);
							}							
						}
					}

					copyUnits = new ArrayList<>(sourceGetMethodMap.size());

					for (Method method : targetMethods) {
						if (ReflectUtil.isSetMethod(method)) {
							String name = NameUtil.removeGetOrSet(method.getName());
							
							Method getMethod = sourceGetMethodMap.get(name);
							if (getMethod != null) {
								Class<?> returnType = getMethod.getReturnType();
								if (returnType.isPrimitive()) {
									returnType = ReflectUtil.getPackagePrimitive(returnType);
								}

								Class<?> setType = method.getParameterTypes()[0];
								if (setType.isPrimitive()) {
									setType = ReflectUtil.getPackagePrimitive(setType);
								}

								if (returnType == setType || setType.isAssignableFrom(returnType)) {
									CopyUnit copyUnit = new CopyUnit();
									copyUnit.getMethod = getMethod;
									copyUnit.setMethod = method;
									copyUnit.name = name;
									copyUnits.add(copyUnit);
								}
							}
						}
					}
				}
			}
		}

		return copyUnits;
	}

	private static class CopyUnit {
		Method getMethod;
		Method setMethod;
		@SuppressWarnings("unused")
		String name;
	}

}
