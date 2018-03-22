package com.paladin.framework.utils.time;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

/**
 * 年龄工具类
 * 
 * @author TontZhou
 * 
 */
public class DateTimeUtil {

	/**
	 * 从身份证号码获取出生日期
	 * 
	 * @param idCardNumber
	 * @return
	 */
	public static Date getBirthDayFromIdCardNumber(String idCardNumber) {

		if (idCardNumber != null && idCardNumber.length() > 14) {
			try {
				return DateFormatUtil.getThreadSafeFormat("yyyyMMdd").parse(idCardNumber.substring(6, 14));
			} catch (ParseException e) {
				throw new IllegalArgumentException("The idCardNumber [" + idCardNumber + "] is invalid!", e);
			}
		}

		throw new IllegalArgumentException("The idCardNumber [" + idCardNumber + "] is invalid!");
	}

	/**
	 * 根据出生日期计算年龄
	 * 
	 * @param birthDay
	 * @return
	 */
	public static int getAge(Date birthDay) {

		Calendar cal = Calendar.getInstance();

		if (cal.before(birthDay)) {
			throw new IllegalArgumentException("The birthDay is before Now.It's unbelievable!");
		}
		int yearNow = cal.get(Calendar.YEAR);
		int monthNow = cal.get(Calendar.MONTH);
		int dayOfMonthNow = cal.get(Calendar.DAY_OF_MONTH);
		cal.setTime(birthDay);

		int yearBirth = cal.get(Calendar.YEAR);
		int monthBirth = cal.get(Calendar.MONTH);
		int dayOfMonthBirth = cal.get(Calendar.DAY_OF_MONTH);

		int age = yearNow - yearBirth;

		if (monthNow <= monthBirth) {
			if (monthNow == monthBirth) {
				if (dayOfMonthNow < dayOfMonthBirth)
					age--;
			} else {
				age--;
			}
		}
		return age;

	}

	private final static long ONE_DAY = 24 * 60 * 60 * 1000;

	public static boolean isInOneWeek(Date d1, Date d2) {

		long t1 = d1.getTime();
		long t2 = d2.getTime();

		t1 -= t1 % ONE_DAY;
		t2 -= t2 % ONE_DAY;

		long d = Math.abs(t1 - t2);

		if (d > ONE_DAY * 7)
			return false;

		int day1 = (int) ((t1 / ONE_DAY + 4) % 7);
		int day2 = (int) ((t2 / ONE_DAY + 4) % 7);

		if (day1 == 0) day1 = 7;
		if (day2 == 0) day2 = 7;

		int x = Math.abs(day1 - day2);

		return x == (int) (d / ONE_DAY);

	}

}
