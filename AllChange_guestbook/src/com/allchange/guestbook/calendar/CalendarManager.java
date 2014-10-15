package com.allchange.guestbook.calendar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

public class CalendarManager {

	private static final String TAG = "CalendarManager";

	private static final int NUM_STANDARD_SAME = 0;
	private static final int NUM_STANDARD_AFTER = 1;
	private static final int NUM_STANDARD_PREVIOUSE = 2;

	public interface CalendarComparable<T> {
		public int compareDate(int year, int month, int day);

		public int compareToUsingCalendar(T another);
	}

	public static class NoComparableObjectException extends Exception {

		public NoComparableObjectException(String detailMessage) {
			super(detailMessage);
			// TODO Auto-generated constructor stub
		}

	}

	public ArrayList<CalendarItem> arrayClickItems = new ArrayList<CalendarItem>();

	private static CalendarManager instance;

	private Calendar mCalendar;

	private ArrayList mData = new ArrayList();

	public static CalendarManager getInstance() {
		if (instance == null) {
			instance = new CalendarManager();
		}
		return instance;
	}

	private CalendarManager() {
		mCalendar = Calendar.getInstance();
	}

	public void setDataObject(ArrayList data)
			throws NoComparableObjectException {
		for (int i = 0; i < data.size(); i++) {
			Object o = data.get(i);
			if (!(o instanceof CalendarComparable)) {
				throw new NoComparableObjectException(
						"Object not implements CalendarComparable");
			}
		}
		mData.clear();
		mData.addAll(data);
		Collections.sort(mData, new Comparator() {

			@Override
			public int compare(Object lhs, Object rhs) {
				// TODO Auto-generated method stub
				CalendarComparable clhs = (CalendarComparable) lhs;
				CalendarComparable crhs = (CalendarComparable) rhs;
				return clhs.compareToUsingCalendar(crhs);
			}
		});
	}

	public CalendarData getCalendarData(int year, int month) {
		mCalendar.set(Calendar.YEAR, year);
		mCalendar.set(Calendar.MONTH, month);
		mCalendar.set(Calendar.DAY_OF_MONTH, 1);
		return getCalendarData();
	}

	public CalendarData getLastMonthCalendarData() {
		mCalendar.add(Calendar.MONTH, -1);
		return getCalendarData();
	}

	public CalendarData getNextMonthCalendarData() {
		mCalendar.add(Calendar.MONTH, 1);
		return getCalendarData();
	}

	public CalendarData getCalendarData() {
		CalendarData data = new CalendarData();
		int currentMonthYear, currentMonth, lastMonthYear, lastMonth, nextMonthYear, nextMonth;

		mCalendar.set(Calendar.DAY_OF_MONTH, 1);
		int dayOfWeek = mCalendar.get(Calendar.DAY_OF_WEEK);
		int thisMonthLastDay = mCalendar
				.getActualMaximum(Calendar.DAY_OF_MONTH);

		mCalendar.add(Calendar.MONTH, -1);
		int lastMonthLastDay = mCalendar
				.getActualMaximum(Calendar.DAY_OF_MONTH);
		lastMonthYear = mCalendar.get(Calendar.YEAR);
		lastMonth = mCalendar.get(Calendar.MONTH);

		mCalendar.add(Calendar.MONTH, 2);
		nextMonthYear = mCalendar.get(Calendar.YEAR);
		nextMonth = mCalendar.get(Calendar.MONTH);

		mCalendar.add(Calendar.MONTH, -1);
		currentMonthYear = mCalendar.get(Calendar.YEAR);
		currentMonth = mCalendar.get(Calendar.MONTH);

		data.year = currentMonthYear;
		data.month = currentMonth;

		for (int i = Calendar.SUNDAY; i < dayOfWeek; i++) {
			CalendarItem item = new CalendarItem();
			item.typeOfDay = CalendarItem.TYPE_UNOCCUPIED_DAY;
			item.year = lastMonthYear;
			item.month = lastMonth;
			item.dayOfWeek = i;
			item.dayOfMonth = lastMonthLastDay - dayOfWeek + i + 1;
			item.inMonth = false;

			// 예약 불가능한 날짜 세팅
			CalendarItem startItem = null;
			CalendarItem endItem = null;
			if (arrayClickItems.size() == 1) {
				startItem = arrayClickItems.get(0);
				if (compareCalendarItem(item, startItem) == NUM_STANDARD_PREVIOUSE) {
					item.typeOfDay = CalendarItem.TYPE_OCCUPIED_DAY;
				}
			} else if (arrayClickItems.size() == NUM_STANDARD_PREVIOUSE) {
				startItem = arrayClickItems.get(0);
				endItem = arrayClickItems.get(1);
				if (compareCalendarItem(item, endItem) == NUM_STANDARD_AFTER) {
					item.typeOfDay = CalendarItem.TYPE_OCCUPIED_DAY;
				} else if (compareCalendarItem(item, endItem) == NUM_STANDARD_PREVIOUSE) {
					item.typeOfDay = CalendarItem.TYPE_RESERVE_DAY_BETWEEN;
				}
				if (compareCalendarItem(item, startItem) == NUM_STANDARD_PREVIOUSE) {
					item.typeOfDay = CalendarItem.TYPE_OCCUPIED_DAY;

				}
			}
			CalendarItem todayItme = new CalendarItem();
			Calendar c = Calendar.getInstance();
			// c.add(Calendar.DAY_OF_MONTH, 1);
			todayItme.year = c.get(Calendar.YEAR);
			todayItme.month = c.get(Calendar.MONTH);
			todayItme.dayOfMonth = c.get(Calendar.DAY_OF_MONTH);
			if (compareCalendarItem(item, todayItme) == NUM_STANDARD_PREVIOUSE) {
				item.typeOfDay = CalendarItem.TYPE_OCCUPIED_DAY;
			}

			item = checkClickItems(item);

			data.days.add(item);
		}

		for (int i = 0; i < thisMonthLastDay; i++) {
			CalendarItem item = new CalendarItem();
			item.typeOfDay = CalendarItem.TYPE_UNOCCUPIED_DAY;
			item.year = currentMonthYear;
			item.month = currentMonth;
			item.dayOfWeek = Calendar.SUNDAY
					+ ((i + dayOfWeek - Calendar.SUNDAY) % 7);
			item.dayOfMonth = i + 1;
			item.inMonth = true;

			// 예약 불가능한 날짜 세팅
			CalendarItem startItem = null;
			CalendarItem endItem = null;
			if (arrayClickItems.size() == 1) {
				startItem = arrayClickItems.get(0);
				if (compareCalendarItem(item, startItem) == 2) {
					item.typeOfDay = CalendarItem.TYPE_OCCUPIED_DAY;
				}
			} else if (arrayClickItems.size() == 2) {
				startItem = arrayClickItems.get(0);
				endItem = arrayClickItems.get(1);
				if (compareCalendarItem(item, endItem) == 1) {
					item.typeOfDay = CalendarItem.TYPE_OCCUPIED_DAY;
				} else if (compareCalendarItem(item, endItem) == 2) {
					item.typeOfDay = CalendarItem.TYPE_RESERVE_DAY_BETWEEN;
				}
				if (compareCalendarItem(item, startItem) == 2) {
					item.typeOfDay = CalendarItem.TYPE_OCCUPIED_DAY;

				}
			}
			CalendarItem todayItme = new CalendarItem();

			Calendar c = Calendar.getInstance();
			// c.add(Calendar.DAY_OF_MONTH, 1);

			todayItme.year = c.get(Calendar.YEAR);
			todayItme.month = c.get(Calendar.MONTH);
			todayItme.dayOfMonth = c.get(Calendar.DAY_OF_MONTH);
			if (compareCalendarItem(item, todayItme) == NUM_STANDARD_PREVIOUSE) {
				item.typeOfDay = CalendarItem.TYPE_OCCUPIED_DAY;
			}

			item = checkClickItems(item);

			data.days.add(item);
		}

		int startNextWeek = Calendar.SUNDAY
				+ ((dayOfWeek - Calendar.SUNDAY + thisMonthLastDay) % 7);
		int count = (Calendar.SATURDAY + 1 - startNextWeek) % 7;

		for (int i = 0; i < count; i++) {
			CalendarItem item = new CalendarItem();
			item.typeOfDay = CalendarItem.TYPE_UNOCCUPIED_DAY;
			item.year = nextMonthYear;
			item.month = nextMonth;
			item.dayOfWeek = i + startNextWeek;
			item.dayOfMonth = i + 1;
			item.inMonth = false;

			// 예약 불가능한 날짜 세팅
			CalendarItem startItem = null;
			CalendarItem endItem = null;
			if (arrayClickItems.size() == 1) {
				startItem = arrayClickItems.get(0);
				if (compareCalendarItem(item, startItem) == 2) {
					item.typeOfDay = CalendarItem.TYPE_OCCUPIED_DAY;

				}
			} else if (arrayClickItems.size() == 2) {
				startItem = arrayClickItems.get(0);
				endItem = arrayClickItems.get(1);
				if (compareCalendarItem(item, endItem) == 1) {
					item.typeOfDay = CalendarItem.TYPE_OCCUPIED_DAY;
				} else if (compareCalendarItem(item, endItem) == 2) {
					item.typeOfDay = CalendarItem.TYPE_RESERVE_DAY_BETWEEN;
				}
				if (compareCalendarItem(item, startItem) == 2) {
					item.typeOfDay = CalendarItem.TYPE_OCCUPIED_DAY;

				}
			}
			CalendarItem todayItme = new CalendarItem();
			Calendar c = Calendar.getInstance();
			// c.add(Calendar.DAY_OF_MONTH, 1);
			todayItme.year = c.get(Calendar.YEAR);
			todayItme.month = c.get(Calendar.MONTH);
			todayItme.dayOfMonth = c.get(Calendar.DAY_OF_MONTH);
			if (compareCalendarItem(item, todayItme) == NUM_STANDARD_PREVIOUSE) {
				item.typeOfDay = CalendarItem.TYPE_OCCUPIED_DAY;
			}

			item = checkClickItems(item);

			data.days.add(item);
		}

		for (int calendarIndex = 0, dataIndex = 0; calendarIndex < data.days
				.size() && dataIndex < mData.size();) {
			CalendarComparable cc = (CalendarComparable) mData.get(dataIndex);
			CalendarItem item = data.days.get(calendarIndex);
			int compare = cc
					.compareDate(item.year, item.month, item.dayOfMonth);
			if (compare < 0) {
				dataIndex++;
			} else if (compare > 0) {
				calendarIndex++;
			} else {
				item.items.add(cc);
				dataIndex++;
			}

			// Log.e(TAG, arrayClickItems.toString());
		}

		return data;
	}

	private int compareCalendarItem(CalendarItem item, CalendarItem standardItem) {
		if (item.year == standardItem.year) {
			if (item.month == standardItem.month) {
				if (item.dayOfMonth == standardItem.dayOfMonth) {
					// 두날이 같은날
					return NUM_STANDARD_SAME;
				} else if (item.dayOfMonth > standardItem.dayOfMonth) {
					// standardItem 날보다 이후 - 예약가능
					return NUM_STANDARD_AFTER;
				} else {
					// 예약 불가능
					return NUM_STANDARD_PREVIOUSE;
				}
			} else if (item.month > standardItem.month) {
				return NUM_STANDARD_AFTER;
			} else {
				return NUM_STANDARD_PREVIOUSE;
			}
		} else if (item.year > standardItem.year) {
			return NUM_STANDARD_AFTER;
		} else {
			return NUM_STANDARD_PREVIOUSE;
		}
	}

	private CalendarItem checkClickItems(CalendarItem item) {
		for (CalendarItem listItem : arrayClickItems) {
			if (item.year == listItem.year && item.month == listItem.month
					&& item.dayOfMonth == listItem.dayOfMonth) {
				// Log.e(TAG, "CalendarItem : "+);
				return listItem;
			}
		}
		return item;
	}
}
