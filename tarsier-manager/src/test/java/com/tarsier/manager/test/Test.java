package com.tarsier.manager.test;

import java.text.ParseException;

import com.tarsier.util.DateUtil;

public class Test {

	@org.junit.Test
	public void test() {
		try {
			System.out.println(DateUtil.DATE_TIME_TZZ.parse("2017-08-15T15:23:35.714993254+08:00"));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
