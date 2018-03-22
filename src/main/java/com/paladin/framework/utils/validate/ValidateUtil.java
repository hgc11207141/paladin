package com.paladin.framework.utils.validate;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.regex.Pattern;

/**
 * 
 * 验证工具类
 * 
 * @author TontoZhou
 * 
 */
public class ValidateUtil {

	// 手机正则
	private static Pattern phonePattern = Pattern
			.compile("^(13[0-9]|14[5|7]|15[0|1|2|3|5|6|7|8|9]|17[0|6|7|8]|18[0|1|2|3|5|6|7|8|9])\\d{8}$");

	// 邮箱正则
	private static Pattern emailPattern = Pattern
			.compile("^([a-zA-Z0-9]+[_|\\_|\\.]?)*[a-zA-Z0-9]+@([a-zA-Z0-9]+[_|\\_|\\.]?)*[a-zA-Z0-9]+\\.[a-zA-Z]{2,3}$");

	/**
	 * 手机号码验证
	 * 
	 * @param number
	 * @return
	 */
	public static boolean validCellphone(String number) {
		return phonePattern.matcher(number).matches();
	}

	/**
	 * 邮箱验证
	 * 
	 * @param email
	 * @return
	 */
	public static boolean validEmail(String email) {
		return emailPattern.matcher(email).matches();
	}

	/**
	 * 判断一个数组中的全部数字是否在另一个数组中，或判断一个数是否在一个数组中
	 * 
	 * @param value
	 *            可以是一个{@link Number}的实现类，或者数组
	 * @param array
	 * @return
	 */
	public static boolean validContainInt(Object value, int[] array) {

		if (value != null && array != null) {
			Class<?> clazz = value.getClass();
			if (Number.class.isAssignableFrom(clazz)) {
				Number num = (Number) value;
				int val = num.intValue();
				for (int i : array)
					if (i == val)
						return true;
			} else if (clazz.isArray()) {
				int len = Array.getLength(value);
				if (len == 0)
					return false;
				for (int i = 0; i < len; i++) {
					Object obj = Array.get(value, i);
					if (!validContainInt(obj, array))
						return false;
				}
				return true;
			} else if (Collection.class.isAssignableFrom(clazz)) {
				Collection<?> coll = (Collection<?>) value;
				if (coll.size() == 0)
					return false;
				for (Object obj : coll) {
					if (!validContainInt(obj, array))
						return false;
				}
				return true;
			}

		}

		return false;
	}

	/**
	 * 验证长度是否范围内
	 * 
	 * @param max
	 * @param min
	 * @param inclusive
	 *            是否边界包含，闭区间
	 * @param value
	 * @return
	 */
	public static boolean validLengthRange(int max, int min, boolean inclusive, Object value) {
		int length = value.toString().length();
		return inclusive ? (length <= max && length >= min) : (length < max && length > min);
	}

	/**
	 * 验证长度是否大于
	 * 
	 * @param max
	 * @param inclusive
	 * @param value
	 * @return
	 */
	public static boolean validGreatLength(int max, boolean inclusive, Object value) {
		int length = value.toString().length();
		return inclusive ? length <= max : length < max;
	}

	/**
	 * 验证长度是否小于
	 * 
	 * @param min
	 * @param inclusive
	 * @param value
	 * @return
	 */
	public static boolean validLessLength(int min, boolean inclusive, Object value) {
		int length = value.toString().length();
		return inclusive ? length >= min : length > min;
	}

	/**
	 * 验证数值是否范围内
	 * 
	 * @param max
	 * @param min
	 * @param inclusive
	 * @param value
	 * @return
	 */
	public static boolean validDigitRange(BigDecimal max, BigDecimal min, boolean inclusive, Object value) {
		try {
			BigDecimal val = new BigDecimal(value.toString());
			int maxResult = val.compareTo(max);
			int minResult = val.compareTo(min);
			return inclusive ? (maxResult <= 0 && minResult >= 0) : (maxResult < 0 && minResult > 0);
		} catch (NumberFormatException e) {
			return false;
		}
	}

	/**
	 * 判断值是否大于最大
	 * 
	 * @param max
	 * @param inclusive
	 * @param value
	 * @return
	 */
	public static boolean validGreatNumber(BigDecimal max, boolean inclusive, Object value) {
		try {
			int comparisonResult = new BigDecimal(value.toString()).compareTo(max);
			return inclusive ? comparisonResult <= 0 : comparisonResult < 0;
		} catch (NumberFormatException nfe) {
			return false;
		}
	}

	/**
	 * 判断值是否小于最小
	 * 
	 * @param min
	 * @param inclusive
	 * @param value
	 * @return
	 */
	public static boolean validLessNumber(BigDecimal min, boolean inclusive, Object value) {
		try {
			int comparisonResult = new BigDecimal(value.toString()).compareTo(min);
			return inclusive ? comparisonResult >= 0 : comparisonResult > 0;
		} catch (NumberFormatException nfe) {
			return false;
		}
	}
	
}
