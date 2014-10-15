package com.gps.trace;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class Dtime {
	private static Dtime dTime;
	private SimpleDateFormat dTimeFormater = new SimpleDateFormat(
			"yyyyMMddHHmmss");
	private Calendar c;

	private static String LOCAL_TIME = "+09:00";

	public static Dtime instance() {
		if (dTime == null) {
			dTime = new Dtime();
		}
		return dTime;
	}

	public String getGMTDateTime() {
		c = Calendar.getInstance();
		c.setTime(new Date());
		dTimeFormater.setTimeZone(TimeZone.getTimeZone("GMT" + LOCAL_TIME));
		return dTimeFormater.format(c.getTime());
	}

}
