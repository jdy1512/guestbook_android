package com.allchange.guestbook.main.feedchat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.allchange.guestbook.R;

public class NewsFeedListItemLoading extends FrameLayout {

	public NewsFeedListItemLoading(Context context) {
		super(context);
		init();
	}

	private void init() {
		View v = LayoutInflater.from(getContext()).inflate(
				R.layout.feed_loading_view, this);
	}
}
