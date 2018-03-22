package com.paladin.framework.utils.test;

public class ExpendTimer {
	
	
	public static long expendTimeBySingle(int count, Runnable run) {

		long start = System.currentTimeMillis();

		for (; count > 0; count--)
			run.run();

		return System.currentTimeMillis() - start;
	}
	
}
