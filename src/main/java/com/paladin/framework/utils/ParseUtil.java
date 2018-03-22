package com.paladin.framework.utils;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 转化值工具类
 * 
 * @author TontoZhou
 * 
 */
public class ParseUtil {


	private static final SimpleDateFormat default_date_format = new SimpleDateFormat("yyyy-MM-dd");
	private static final SimpleDateFormat default_date_time_format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	private static final Map<String, Class<?>> primitives = new HashMap<String,Class<?>>(8);

	static {
		primitives.put("byte", Byte.TYPE);
		primitives.put("char", Character.TYPE);
		primitives.put("double", Double.TYPE);
		primitives.put("float", Float.TYPE);
		primitives.put("int", Integer.TYPE);
		primitives.put("long", Long.TYPE);
		primitives.put("short", Short.TYPE);
		primitives.put("boolean", Boolean.TYPE);
	}

	/**
	 * 
	 * @param str
	 * @param type
	 * @return
	 */
	public static Object parseString(String str, Class<?> type) {
		
		if (str == null || type == null)
			return null;
		
		if (type == String.class) {
			return str;
		}
		
		if("".equals(str))
			return null;
	
		if (type == Date.class) {

			try {
				long time = Long.parseLong(str);
				return new Date(time);
			} catch (Exception e) {
			}

			try {
				return default_date_time_format.parse(str);
			} catch (ParseException e) {
				try {
					return default_date_format.parse(str);
				} catch (ParseException e1) {
					return null;
				}
			}
		}
	
		try {
			
			Class<?> newType = primitives.get(type.getSimpleName());
			
			if (newType != null) {
				type = newType;
			}

			if (type == Double.class) {
				return Double.parseDouble(str);
			} else if (type == Integer.class) {
				return Integer.parseInt(str);
			} else if (type == Long.class) {
				return Long.parseLong(str);
			} else if (type == Boolean.class) {
				return Boolean.parseBoolean(str);
			} else if (type == Short.class) {
				return Short.parseShort(str);
			} else if (type == Float.class) {
				return Float.parseFloat(str);
			} else if (type == Character.class) {
				return str.charAt(0);
			} else if (type == BigDecimal.class) {
				return new BigDecimal(str);		
			}

		} catch (Exception e) {
			return null;
		}

		return null;
	}
	
	public static String parse2String(Object value) {
		
		if(value == null)
			return null;
		
		if(value instanceof Date)
		{
			return default_date_time_format.format((Date)value);
		}
		
		return value.toString();
		
	}
	

}
