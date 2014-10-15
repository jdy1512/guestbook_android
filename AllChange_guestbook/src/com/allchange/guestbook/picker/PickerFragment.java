package com.allchange.guestbook.picker;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.allchange.guestbook.R;
import com.allchange.guestbook.property.PropertyManager;

public class PickerFragment extends Fragment {
	private static final float SIZE = 0.2f; // 반드시 1에서 홀수로 나눴을때 나오는 값중 하나로 설정
											// ex)0.2f = 1/5 screen_width
	private static final int MAX_POS = (int) (Math.round(1 / SIZE) / 2);
	private static final int MAX_VISIBLE_CNT = ((MAX_POS * 2) + 1);
	private static final String TAG = "PickerFragment";

	private int screen_x;
	private String minDate, maxDate;
	private GuestBookPickerListener mListener;
	private List<Date> listDate;
	private String today;
	ViewAdapter mAdapter;
	ViewPager mViewPager;

	public interface GuestBookPickerListener {
		public void pickerCallback(String date);
	}

	public PickerFragment() {
	}

	public void init(int screen_x, String minDate, String maxDate) {
		// 기본 정상적인 날짜 세팅
		this.screen_x = screen_x;
		this.minDate = minDate;
		this.maxDate = maxDate;
		this.today = Dtime.instance().getGMTDate();
		// 오늘 날짜가 과거이면 그게 최초의 날로 변경
		updateCheckinOut(this.minDate, this.maxDate);
		// 뷰가 5개씩 보여지기 때문에 양 끝에 2개씩 날을 추가
		add2Days(this.minDate, this.maxDate);
		// 기본 정상적인 날짜 세팅
		listDate = getDates(this.minDate, this.maxDate);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		getView().post(new Runnable() {
			@Override
			public void run() {
				// code you want to run when view is visible for the first time
				scrollTo(PropertyManager.getInstance().getCheckin());
			}
		});
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.e(TAG, "onCreateView");
		View rootView = inflater.inflate(R.layout.picker, container, false);
		mViewPager = (ViewPager) rootView;
		ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(screen_x,
				screen_x / MAX_VISIBLE_CNT);
		mViewPager.setLayoutParams(params);
		mAdapter = new ViewAdapter();
		mViewPager.setAdapter(mAdapter);
		mViewPager.setOffscreenPageLimit(MAX_VISIBLE_CNT * 3);
		mViewPager.setPageTransformer(false, new ViewPager.PageTransformer() {

			@SuppressLint("NewApi")
			@Override
			public void transformPage(View view, float v) {
				final float normalizedposition = (SIZE * MAX_POS)
						- Math.abs(v - (SIZE * MAX_POS));
				view.setScaleX(normalizedposition / ((SIZE * MAX_POS) * 2)
						+ 0.5f);
				view.setScaleY(normalizedposition / ((SIZE * MAX_POS) * 2)
						+ 0.5f);
			}
		});

		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@SuppressLint("NewApi")
			@Override
			public void onPageSelected(int position) {
				if (mListener != null) {

					// date를 넘겨주는데 포맷은 20140101형식으로
					DateFormat df1 = new SimpleDateFormat("yyyyMMdd");
					// +2를 하는 이유는 실제 seleted된 페이지는 중앙이 아니라 가장 왼쪽 페이지이기 때문에
					mListener.pickerCallback(df1.format(listDate
							.get(position + 2)));

					// 중앙에 있는 뷰의 배경 이미지를 바꿔준다
					mapViews.get(position + 2).iv
							.setImageDrawable(getResources().getDrawable(
									R.drawable.main_range_mark));
					mapViews.get(position + 2).iv
							.setBackgroundColor(0x00000000);
					mapViews.get(position + 2).iv_shadow
							.setVisibility(View.VISIBLE);

					if (seletedView != null) {
						// 전에 선택된 뷰의 배경을 다시 복구 시킨다
						seletedView.iv.setImageDrawable(getResources()
								.getDrawable(R.drawable.range));
						seletedView.iv
								.setBackgroundColor(getResources().getColor(
										R.color.calendar_text_date_out_month));
						seletedView.iv_shadow.setVisibility(View.INVISIBLE);
					}

					seletedView = mapViews.get(position + 2);
				}
			}

			@Override
			public void onPageScrolled(int position, float positionOffset,
					int positionOffsetPixels) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});

		return rootView;
	}

	@Override
	public void onResume() {
		// Handler handler = new Handler();
		// handler.postDelayed(new Runnable() {
		// @Override
		// public void run() {
		// scrollTo(PropertyManager.getInstance().getCheckin());
		// }
		// }, 500);
		super.onResume();
	}

	public void scrollTo(String date) {

		if (!date.equals("today")) {

			try {

				// 오늘 날
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
				Date strDate = sdf.parse(date);
				Calendar c = Calendar.getInstance();
				Calendar c1 = Dtime.instance().getGMTCalendar();
				c.setTime(strDate);
				int days = 0;
				while (c.after(c1)) {
					days += 1;
					c1.add(Calendar.DAY_OF_MONTH, 1);
				}
				Log.e(TAG, "days : " + days);
				Log.e(TAG, "viewPager : " + mViewPager.toString());
				mViewPager.setCurrentItem(days, true);

			} catch (ParseException e) {
				e.printStackTrace();
			}
		} else {
			mViewPager.setCurrentItem(0, true);
		}

	}

	// seleted됐을 때 백그라운드 바꿔주기위함
	HashMap<Integer, PickerItem> mapViews = new HashMap<Integer, PickerItem>();
	PickerItem seletedView;

	private class ViewAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return listDate.size();
		}

		@Override
		public float getPageWidth(int position) {
			return (SIZE);
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return (view == object);
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			PickerItem view = new PickerItem(getActivity());
			view.tv.setGravity(Gravity.CENTER);

			DateFormat df1 = new SimpleDateFormat("dd");
			view.tv.setText(df1.format(listDate.get(position)) + "");
			view.tv.setTextColor(Color.WHITE);

			((ViewPager) container).addView(view, 0);
			mapViews.put(position, view);
			return view;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			((ViewPager) container).removeView((PickerItem) object);
			mapViews.remove(position);
		}
	}

	public void setOnPickerListener(GuestBookPickerListener listener) {
		mListener = listener;
	}

	// check_in이 today다 미래면 today를 넣음
	public void updateCheckinOut(String inDate, String outDate) {
		if (inDate.equals("")) {
			this.minDate = today;
			this.maxDate = today;
		} else {

			try {
				if (Integer.parseInt(this.minDate) > Integer.parseInt(today)) {
					this.minDate = today;
				}
			} catch (Exception e) {
				this.minDate = today;
			}

			this.maxDate = outDate;
		}

	}

	@SuppressLint("SimpleDateFormat")
	private List<Date> getDates(String dateString1, String dateString2) {
		ArrayList<Date> dates = new ArrayList<Date>();
		DateFormat df1 = new SimpleDateFormat("yyyyMMdd");

		Date date1 = null;
		Date date2 = null;

		try {
			date1 = df1.parse(dateString1);
			date2 = df1.parse(dateString2);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		Calendar cal1 = Calendar.getInstance();
		cal1.setTime(date1);

		Calendar cal2 = Calendar.getInstance();
		cal2.setTime(date2);

		while (!cal1.after(cal2)) {
			dates.add(cal1.getTime());
			cal1.add(Calendar.DATE, 1);
		}
		return dates;
	}

	/***
	 * 보여지는게 5개라서.. 4개는 고를수 없다 따라서 4개를 양끝에 2개씩 추가하기 위한작업
	 * 
	 * @param dateString1
	 *            start date
	 * @param dateString2
	 *            end date
	 */
	public void add2Days(String dateString1, String dateString2) {
		DateFormat df1 = new SimpleDateFormat("yyyyMMdd");

		Date date1 = null;
		Date date2 = null;

		Log.e(TAG, "dateString1 : " + dateString1);
		Log.e(TAG, "dateString2 : " + dateString2);

		try {
			date1 = df1.parse(dateString1);
			date2 = df1.parse(dateString2);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		Calendar cal1 = Calendar.getInstance();
		cal1.setTime(date1);
		cal1.add(Calendar.DATE, -2);

		Calendar cal2 = Calendar.getInstance();
		cal2.setTime(date2);
		cal2.add(Calendar.DATE, 2);

		this.minDate = df1.format(cal1.getTime());
		this.maxDate = df1.format(cal2.getTime());

	}
}