package com.allchange.guestbook.picker;

import java.util.HashMap;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.allchange.guestbook.R;

public class TimePickerFragment extends Fragment {
	private static final String TAG = "TimePickerFragment";
	private static final float SIZE = 0.2f; // 반드시 1에서 홀수로 나눴을때 나오는 값중 하나로 설정
											// ex)0.2f = 1/5 screen_width
	private static final int MAX_POS = (int) (Math.round(1 / SIZE) / 2);
	private static final int MAX_VISIBLE_CNT = ((MAX_POS * 2) + 1);

	private int screen_x;
	private TimePickerListener mListener;
	private String[] listDate = { "22:00", "23:00", "00:00", "01:00", "02:00",
			"03:00", "04:00", "05:00", "06:00", "07:00", "08:00", "09:00",
			"10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00",
			"17:00", "18:00", "19:00", "20:00", "21:00", "22:00", "23:00",
			"00:00", "01:00" };
	ViewAdapter mAdapter;
	ViewPager viewPager;

	public interface TimePickerListener {
		public void pickerCallback(String hour);
	}

	public TimePickerFragment() {
	}

	public void init(int screen_x) {
		// 기본 정상적인 날짜 세팅
		this.screen_x = screen_x;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.picker, container, false);
		viewPager = (ViewPager) rootView;
		ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(screen_x,
				screen_x / MAX_VISIBLE_CNT);
		viewPager.setLayoutParams(params);
		mAdapter = new ViewAdapter();
		viewPager.setAdapter(mAdapter);
		viewPager.setOffscreenPageLimit(MAX_VISIBLE_CNT * 3);
		viewPager.setPageTransformer(false, new ViewPager.PageTransformer() {

			@SuppressLint("NewApi")
			@Override
			public void transformPage(View page, float position) {
				final float normalizedposition = (SIZE * MAX_POS)
						- Math.abs(position - (SIZE * MAX_POS));
				final float result = 0.75f;
				page.setScaleX(result);
				page.setScaleY(result);
				// Log.e(TAG, "MAX_POS : " + MAX_POS + "\nv : " + position
				// + " \nnormalizedposition : " + normalizedposition
				// + "\nresult : " + normalizedposition
				// / ((SIZE * MAX_POS) * 2) + 0.5f);
			}
		});

		viewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@SuppressLint("NewApi")
			@Override
			public void onPageSelected(int position) {
				if (mListener != null) {
					// int pageCount = mAdapter.getCount();
					// if (position == 0) {
					// viewPager.setCurrentItem(pageCount - 1, false);
					// } else if (position == pageCount - 5) {
					// viewPager.setCurrentItem(0, false);
					// }
					try {
						// +2를 하는 이유는 실제 seleted된 페이지는 중앙이 아니라 가장 왼쪽 페이지이기 때문에
						mListener.pickerCallback("" + (position) % 24);

						// 중앙에 있는 뷰의 배경 이미지를 바꿔준다
						mapViews.get(position + 2).iv
								.setImageDrawable(getResources().getDrawable(
										R.drawable.range));
						mapViews.get(position + 2).iv
								.setBackgroundColor(getResources().getColor(
										R.color.feed_text_time));

						if (seletedView != null) {
							// 전에 선택된 뷰의 배경을 다시 복구 시킨다
							seletedView.iv.setImageDrawable(getResources()
									.getDrawable(R.drawable.range));
							seletedView.iv
									.setBackgroundColor(getResources()
											.getColor(
													R.color.calendar_text_date_out_month));
							// seletedView.iv_shadow.setVisibility(View.INVISIBLE);
						}

						seletedView = mapViews.get(position + 2);
					} catch (Exception e) {
						// TODO: handle exception
					}

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
		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				viewPager.setCurrentItem(0);
			}
		}, 500);
		super.onResume();
	}

	// seleted됐을 때 백그라운드 바꿔주기위함
	HashMap<Integer, PickerItem> mapViews = new HashMap<Integer, PickerItem>();
	PickerItem seletedView;

	private class ViewAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return listDate.length;
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
			view.tv.setTextSize(
					TypedValue.COMPLEX_UNIT_PX,
					getResources().getDimensionPixelSize(
							R.dimen.market_map_fragment_picker_text_size));
			view.tv.setText(listDate[position]);
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

	public void setOnPickerListener(TimePickerListener listener) {
		mListener = listener;
	}

}