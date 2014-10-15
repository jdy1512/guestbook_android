package com.allchange.guestbook.bookingsitelist;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.allchange.guestbook.R;
import com.allchange.guestbook.bookingActivity.BookingFrameActivity;

public class BookingSiteListActivity extends FragmentActivity {
	public static final String TAG = "BookingSiteListActivity";

	ListView bookingList;
	BookingSiteListAdapter mAdapter;
	public static String[] bookingSite = { "Airbnb", "Kozaza",
			"Gangneung Guesthouse" };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bookinglist);
		getActionBar().setDisplayHomeAsUpEnabled(true);

		bookingList = (ListView) findViewById(R.id.bookingList_listView);

		ArrayList<BookingSiteListData> data = new ArrayList<BookingSiteListData>();
		BookingSiteListData bookingData;

		for (int i = 0; i < bookingSite.length; i++) {
			bookingData = new BookingSiteListData();
			if (bookingSite[i].equals("Gangneung Guesthouse")) {
				bookingData.name = getResources().getString(
						R.string.actionbar_title_gangneung_guesthouse);
			} else {
				bookingData.name = bookingSite[i];
			}
			data.add(bookingData);
		}

		mAdapter = new BookingSiteListAdapter(this, data);
		bookingList.setAdapter(mAdapter);

		bookingList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(BookingSiteListActivity.this,
						BookingFrameActivity.class);
				intent.putExtra("site", bookingSite[position]);
				BookingSiteListActivity.this.startActivity(intent);
			}
		});
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

}
