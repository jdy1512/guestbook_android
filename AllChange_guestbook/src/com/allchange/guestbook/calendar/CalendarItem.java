package com.allchange.guestbook.calendar;

import java.util.ArrayList;
import java.util.Calendar;

public class CalendarItem {
	public static final int TYPE_OCCUPIED_DAY = 0;
	public static final int TYPE_UNOCCUPIED_DAY = 1;
	public static final int TYPE_RESERVE_DAY_START = 2;
	public static final int TYPE_RESERVE_DAY_END = 3;
	public static final int TYPE_RESERVE_DAY_BETWEEN = 4;

	public int year;
	public int month;
	public int dayOfMonth;
	public int dayOfWeek;
	public boolean inMonth;
	public int typeOfDay = TYPE_OCCUPIED_DAY;
	public ArrayList items = new ArrayList();

	public String getMonthString() {
		if (month + 1 > 9) {
			return (month + 1) + "";
		} else {
			return "0" + (month + 1);
		}
	}

	public String getDayString() {
		if (dayOfMonth > 9) {
			return dayOfMonth + "";
		} else {
			return "0" + dayOfMonth;
		}
	}

	public static int GetDifferenceOfDate(int nYear1, int nMonth1, int nDate1,
			int nYear2, int nMonth2, int nDate2) {

		Calendar cal = Calendar.getInstance();
		int nTotalDate1 = 0, nTotalDate2 = 0, nDiffOfYear = 0, nDiffOfDay = 0;

		if (nYear1 > nYear2) {
			for (int i = nYear2; i < nYear1; i++) {
				cal.set(i, 12, 0);
				nDiffOfYear += cal.get(Calendar.DAY_OF_YEAR);
			}
			nTotalDate1 += nDiffOfYear;
		} else if (nYear1 < nYear2) {
			for (int i = nYear1; i < nYear2; i++) {
				cal.set(i, 12, 0);
				nDiffOfYear += cal.get(Calendar.DAY_OF_YEAR);
			}
			nTotalDate2 += nDiffOfYear;
		}

		cal.set(nYear1, nMonth1 - 1, nDate1);
		nDiffOfDay = cal.get(Calendar.DAY_OF_YEAR);
		nTotalDate1 += nDiffOfDay;

		cal.set(nYear2, nMonth2 - 1, nDate2);
		nDiffOfDay = cal.get(Calendar.DAY_OF_YEAR);
		nTotalDate2 += nDiffOfDay;

		return nTotalDate1 - nTotalDate2;
	}
}
