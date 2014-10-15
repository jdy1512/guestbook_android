package com.allchange.guestbook.picker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class Dtime {
	private static Dtime dTime;
	private static final String FORMAT = "yyyyMMdd";
	private SimpleDateFormat dateFormater;
	private Calendar c;

	private static String LOCAL_TIME = "+09:00";

	public static Dtime instance() {
		if (dTime == null) {
			dTime = new Dtime();
		}
		return dTime;
	}

	public Dtime() {
		c = Calendar.getInstance();
		dateFormater = new SimpleDateFormat(FORMAT);
		dateFormater.setTimeZone(TimeZone.getTimeZone("GMT" + LOCAL_TIME));
	}

	public int diffOfDate(String begin, String end) {
		try {
			Date beginDate = dateFormater.parse(begin);
			Date endDate = dateFormater.parse(end);

			long diff = endDate.getTime() - beginDate.getTime();
			int diffDays = (int) (diff / (24 * 60 * 60 * 1000));

			return diffDays;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public String getGMTDate() {
		c = Calendar.getInstance();
		return dateFormater.format(c.getTime());
	}

	public Calendar getGMTCalendar() {
		c = Calendar.getInstance();
		return c;
	}

	public String getGMTAddDate(int index) {
		c = Calendar.getInstance();
		c.add(c.DAY_OF_MONTH, index);
		return dateFormater.format(c.getTime());
	}

	public String getGMTAddDate(String curDate, int index) {
		try {
			c.setTime(dateFormater.parse(curDate));
			c.add(c.DAY_OF_MONTH, index);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return dateFormater.format(c.getTime());
	}
}
