package com.allchange.guestbook.bookingActivity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ToggleButton;

import com.allchange.guestbook.R;
import com.allchange.guestbook.booking.search.gangneung.GangneungGuestHouseFragment;
import com.allchange.guestbook.booking.search.list.AccamodationSearchFragment;
import com.allchange.guestbook.booking.search.list.OnSetAccamoListener;
import com.allchange.guestbook.booking.search.map.ParsingData;
import com.allchange.guestbook.booking.search.map.TrippalMapFragment;
import com.allchange.guestbook.bookingActivity.BookingPurposeFragment.OnSetPurposeListener;

public class BookingFrameActivity extends FragmentActivity implements
		OnClickListener, OnSetAccamoListener, OnSetPurposeListener {
	public static final String TAG = "BookingFrameActivity";

	FrameLayout frame_accomo, frame_purpose, frame_calendar;
	ToggleButton btn_accomo, btn_purpose, btn_date;
	BookingPurposeFragment fragment_purpose;
	BookingCalendarFragment fragment_calendar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_book_framgnet);

		getActionBar().setDisplayHomeAsUpEnabled(true);
		// setTitle("das	fasdf");

		String site = getIntent().getStringExtra("site");

		if (site.equals("Airbnb")) {
			TrippalMapFragment fragment = new TrippalMapFragment();
			fragment.setOnSetAccamoListener(this);
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.booking_frame, fragment).commit();
		} else if (site.equals("Kozaza")) {
			AccamodationSearchFragment fragment = new AccamodationSearchFragment();
			fragment.setOnSetAccamoListener(this);
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.booking_frame, fragment).commit();
		} else {
			GangneungGuestHouseFragment fragment = new GangneungGuestHouseFragment();
			fragment.setOnSetAccamoListener(this);
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.booking_frame, fragment).commit();
		}

		fragment_purpose = new BookingPurposeFragment();
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.booking_frame_purpose, fragment_purpose).commit();

		fragment_calendar = new BookingCalendarFragment();
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.booking_frame_calendar, fragment_calendar)
				.commit();
		// fragment_calendar.setOnConfirmAccamodateListener(this);

		frame_accomo = (FrameLayout) findViewById(R.id.booking_frame);
		frame_purpose = (FrameLayout) findViewById(R.id.booking_frame_purpose);
		frame_calendar = (FrameLayout) findViewById(R.id.booking_frame_calendar);

		btn_accomo = (ToggleButton) findViewById(R.id.booking_btn_set_accomodate);
		btn_purpose = (ToggleButton) findViewById(R.id.booking_btn_set_purpose);
		btn_date = (ToggleButton) findViewById(R.id.booking_btn_set_date);

		btn_purpose.setEnabled(false);
		btn_date.setEnabled(false);

		btn_accomo.setOnClickListener(this);
		btn_purpose.setOnClickListener(this);
		btn_date.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.booking_btn_set_accomodate:
			turnOnSearchView();
			break;

		case R.id.booking_btn_set_purpose:
			turnOnPurposeView();
			break;

		case R.id.booking_btn_set_date:
			turnOnCalendarView();
			break;

		default:
			break;
		}
	}

	public void turnOnSearchView() {
		frame_accomo.setVisibility(View.VISIBLE);
		frame_purpose.setVisibility(View.GONE);
		frame_calendar.setVisibility(View.GONE);
	}

	public void turnOnPurposeView() {
		frame_accomo.setVisibility(View.GONE);
		frame_purpose.setVisibility(View.VISIBLE);
		frame_calendar.setVisibility(View.GONE);
	}

	public void turnOnCalendarView() {
		frame_accomo.setVisibility(View.GONE);
		frame_purpose.setVisibility(View.GONE);
		frame_calendar.setVisibility(View.VISIBLE);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		// Respond to the action bar's Up/Home button
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	// @Override
	// public void onConfirm() {
	// // TODO Auto-generated method stub
	// finish();
	// // 메인으로 돌아가야됨
	// }

	@Override
	public void onSetAccamodation(ParsingData data) {
		turnOnPurposeView();
		btn_purpose.setEnabled(true);
		fragment_purpose.setOnSetPurposeListener(this, data);
	}

	@Override
	public void onSetPurpose(ParsingData data) {
		btn_date.setEnabled(true);
		fragment_calendar.setInfoData(data);
		turnOnCalendarView();
	}
}
