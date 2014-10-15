package com.allchange.guestbook.main.feedandsearch;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.allchange.guestbook.R;
import com.allchange.guestbook.main.feedchat.FeedFragment;
import com.allchange.guestbook.main.profiles.TripPalsListFragment;
import com.allchange.guestbook.property.PropertyManager;
import com.allchange.guestbook.setting.map.SettingMapActivity;
import com.allchange.guestbook.setting.map.SettingMapActivity.OnSuccessUpdateRageListener;

public class FragmentFeedAndSearch extends Fragment implements
		OnSuccessUpdateRageListener, OnClickListener {

	FragmentTabHost tabHost;
	FrameLayout fl_feed, fl_search, fl_untouch;
	TextView tv_feed, tv_search;
	ImageView iv_feed, iv_search;
	public static TextView tv_date, tv_bounds;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.tab_one, container, false);

		tabHost = (FragmentTabHost) v.findViewById(R.id.tabHost);
		tabHost.setup(getActivity().getApplicationContext(),
				getChildFragmentManager(), R.id.realtabcontent);
		tabHost.addTab(tabHost.newTabSpec("child1").setIndicator("ChildOne"),
				TripPalsListFragment.class, null);
		tabHost.addTab(tabHost.newTabSpec("child2").setIndicator("ChildTwo"),
				FeedFragment.class, null);

		tv_date = (TextView) v.findViewById(R.id.main_tv_date);
		tv_bounds = (TextView) v.findViewById(R.id.main_tv_bounds);

		tv_feed = (TextView) v.findViewById(R.id.main_tab_tv_newsfeed);
		tv_search = (TextView) v.findViewById(R.id.main_tab_tv_search);

		iv_feed = (ImageView) v.findViewById(R.id.main_tab_iv_newsfeed);
		iv_search = (ImageView) v.findViewById(R.id.main_tab_iv_search);

		Button btn = (Button) v.findViewById(R.id.main_btn_bounds);
		btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				if (isSetMyHouse()) {
					Intent intent = new Intent(getActivity(),
							SettingMapActivity.class);
					SettingMapActivity
							.setOnSuccessUpdateRageListener(FragmentFeedAndSearch.this);
					startActivityForResult(intent, 0);
				}
			}
		});

		fl_feed = (FrameLayout) v.findViewById(R.id.main_tab_btn_newsfeed);
		fl_feed.setOnClickListener(this);
		fl_search = (FrameLayout) v.findViewById(R.id.main_tab_btn_search);
		fl_search.setOnClickListener(this);
		fl_untouch = (FrameLayout) v.findViewById(R.id.main_layout_untouch);
		if (!isSetMyHouse()) {
			fl_untouch.setVisibility(View.VISIBLE);
		}

		init();

		return v;
	}

	// @Override
	// public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
	// inflater.inflate(R.menu.main1, menu);
	// super.onCreateOptionsMenu(menu, inflater);
	// }

	@SuppressLint("NewApi")
	@Override
	public void onClick(View v) {
		if (!isSetMyHouse()) {
			return;
		}

		switch (v.getId()) {
		case R.id.main_tab_btn_search:
			tabHost.setCurrentTab(0);
			fl_feed.setBackground(getResources().getDrawable(
					R.drawable.topbar_bg_default));
			tv_feed.setTextColor(getResources().getColor(R.color.main_darkgray));
			iv_feed.setImageDrawable(getResources().getDrawable(
					R.drawable.topbar_icon_1_default));

			fl_search.setBackground(getResources().getDrawable(
					R.drawable.topbar_bg_selected));
			tv_search.setTextColor(getResources().getColor(R.color.white));
			iv_search.setImageDrawable(getResources().getDrawable(
					R.drawable.topbar_icon_2_selected));
			break;

		case R.id.main_tab_btn_newsfeed:
			tabHost.setCurrentTab(1);
			fl_feed.setBackground(getResources().getDrawable(
					R.drawable.topbar_bg_selected));
			tv_feed.setTextColor(getResources().getColor(R.color.white));
			iv_feed.setImageDrawable(getResources().getDrawable(
					R.drawable.topbar_icon_1_selected));

			fl_search.setBackground(getResources().getDrawable(
					R.drawable.topbar_bg_default));
			tv_search.setTextColor(getResources().getColor(
					R.color.main_darkgray));
			iv_search.setImageDrawable(getResources().getDrawable(
					R.drawable.topbar_icon_2_default));
			break;

		default:
			break;
		}

	}

	private void init() {
		tv_bounds
				.setText(((float) Math.round((Double
						.parseDouble(PropertyManager.getInstance()
								.getRadiusMeter()) / 10)) / 100 + "km"));
		try {
			tv_date.setText(PropertyManager.getInstance().getCheckin()
					.substring(0, 4)
					+ "."
					+ PropertyManager.getInstance().getCheckin()
							.substring(4, 6)
					+ "."
					+ PropertyManager.getInstance().getCheckin()
							.substring(6, 8)
					+ "-"
					+ PropertyManager.getInstance().getCheckout()
							.substring(0, 4)
					+ "."
					+ PropertyManager.getInstance().getCheckout()
							.substring(4, 6)
					+ "."
					+ PropertyManager.getInstance().getCheckout()
							.substring(6, 8));
		} catch (Exception e) {
			// TODO: handle exception
		}

		// onChangedTripPalTap(0);
	}

	private boolean isSetMyHouse() {
		if (PropertyManager.getInstance().getCheckin().equals("")
				|| PropertyManager.getInstance().getCheckin().length() < 5) {
			return false;
		}
		return true;
	}

	@Override
	public void onResume() {
		if (isSetMyHouse()) {
			// Log.e(TAG, "onResume "
			// + PropertyManager.getInstance().getDistance());
			// tv_title.setText("반경 "
			// + (Double.parseDouble(PropertyManager.getInstance()
			// .getDistance()) / 1000) + "km 이내의 여행객");
		} else {
			tv_date.setText(getResources().getString(R.string.set_your_visit));
		}
		super.onResume();
	}

	@Override
	public void onSuccessUpdateRage() {
		tv_date.invalidate();
		// AndroidQuery.getInstance().requestGetPorfiles(this,
		// Url.SERVER_GET_PROFILES, mSelectedDate);
	}

}
