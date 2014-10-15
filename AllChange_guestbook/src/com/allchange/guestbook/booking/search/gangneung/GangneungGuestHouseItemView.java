package com.allchange.guestbook.booking.search.gangneung;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.allchange.guestbook.R;
import com.allchange.guestbook.booking.search.map.ParsingData;
import com.allchange.guestbook.property.MyApplication;
import com.androidquery.AQuery;

public class GangneungGuestHouseItemView extends FrameLayout {

	TextView titleView;
	TextView snippetView;
	ImageView iv;
	ParsingData mData;

	public GangneungGuestHouseItemView(Context context) {
		super(context);
		// mContext = context;
		init();
	}

	private void init() {
		LayoutInflater.from(getContext()).inflate(R.layout.info_window_layout,
				this);
		titleView = (TextView) findViewById(R.id.map_title);
		snippetView = (TextView) findViewById(R.id.map_snippet);
		iv = (ImageView) findViewById(R.id.map_imageView);
	}

	public void setMyData(ParsingData data) {
		mData = data;

		titleView.setText(data.title);
		snippetView.setText(data.address);
		iv.setImageResource(Integer.parseInt(data.image_url));
	}
}
