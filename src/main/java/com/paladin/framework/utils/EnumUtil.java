package com.paladin.framework.utils;

public class EnumUtil {

	public static boolean equals(String str, Enum<?>... constants) {

		for (Enum<?> constant : constants) {
			if (constant.name().equals(str))
				return true;
		}

		return false;
	}

}
