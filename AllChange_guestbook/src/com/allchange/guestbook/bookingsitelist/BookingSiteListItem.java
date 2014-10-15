package com.allchange.guestbook.bookingsitelist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.allchange.guestbook.R;

public class BookingSiteListItem extends FrameLayout {

	TextView tv_location, tv_listNum;
	BookingSiteListData mData = null;

	public BookingSiteListItem(Context context, BookingSiteListData d) {
		super(context);
		mData = d;
		init();
	}

	private void init() {

		View v = LayoutInflater.from(getContext()).inflate(
				R.layout.booking_list_item, this);
		TextView tv = (TextView) v
				.findViewById(R.id.booking_list_Item_txetView);
		tv.setText(mData.name);
	}

}
