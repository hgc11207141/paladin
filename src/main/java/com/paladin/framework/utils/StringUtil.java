package com.paladin.framework.utils;

public class StringUtil {

	/**
	 * 空
	 *
	 * @param str
	 * @return
	 */
	public static boolean isEmpty(String str) {
		return str == null || str.length() == 0;
	}

	/**
	 * 非空
	 *
	 * @param str
	 * @return
	 */
	public static boolean isNotEmpty(String str) {
		return !isEmpty(str);
	}

	/**
	 * 强力trim，可去除全角下的空格
	 * @param str
	 * @return
	 */
	public static String strongTrim(String str) {
		if(str == null) {
			return null;
		}
		
		char[] val = str.toCharArray();

		int len = val.length;
		int st = 0;

		while ((st < len) && (val[st] <= ' ' || val[st] == 12288)) {
			st++;
		}
		while ((st < len) && (val[len - 1] <= ' ' || val[len - 1] == 12288)) {
			len--;
		}
		return ((st > 0) || (len < val.length)) ? str.substring(st, len) : str;
	}

}
