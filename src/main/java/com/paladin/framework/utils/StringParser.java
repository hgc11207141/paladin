package com.paladin.framework.utils;

import java.lang.reflect.Array;

public class StringParser {

	public static String toString(Object obj) {
		if (obj == null)
			return "null";

		if (obj.getClass().isArray()) {
			int size = Array.getLength(obj);
			StringBuilder sb = new StringBuilder("[");
			for (int i = 0; i < size; i++)
				sb.append(toString(Array.get(obj, i))).append(",");
			if (size > 0)
				sb.deleteCharAt(sb.length() - 1);
			return sb.append("]").toString();
		}

		return obj.toString();

	}
	

}
