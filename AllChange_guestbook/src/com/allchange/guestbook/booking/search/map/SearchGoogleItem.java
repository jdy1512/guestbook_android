package com.allchange.guestbook.booking.search.map;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import com.allchange.guestbook.R;

public class SearchGoogleItem extends FrameLayout {

	public SearchGoogleItem(Context context) {
		super(context);
		init();
	}

	private void init() {
		LayoutInflater.from(getContext()).inflate(R.layout.search_google_item,
				this);
	}
}
