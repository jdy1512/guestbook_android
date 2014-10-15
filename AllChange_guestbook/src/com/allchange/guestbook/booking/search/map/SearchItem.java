package com.allchange.guestbook.booking.search.map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.allchange.guestbook.R;

public class SearchItem extends FrameLayout {

	TextView tv_location, tv_listNum;
	String mData = null;

	public SearchItem(Context context, String d) {
		super(context);
		mData = d;
		init();
	}

	private void init() {

		View v = LayoutInflater.from(getContext()).inflate(
				R.layout.search_map_list_item, this);
		TextView tv = (TextView) v.findViewById(R.id.search_map_txetView);
		tv.setText(mData);

	}
}
