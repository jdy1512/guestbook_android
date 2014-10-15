package com.allchange.guestbook.main.feedchat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.allchange.guestbook.R;
import com.allchange.guestbook.booking.search.map.ParsingData;

public class NewsFeedListItemUpdate extends FrameLayout {

	ImageView iv_backline;
	ToggleButton toggleBtn_update;
	TextView tv_update;

	public NewsFeedListItemUpdate(Context context) {
		super(context);
		init();
	}

	private void init() {
		View v = LayoutInflater.from(getContext()).inflate(
				R.layout.feed_update_view, this);
		toggleBtn_update = (ToggleButton) v
				.findViewById(R.id.feed_toggle_update);
		toggleBtn_update.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!toggleBtn_update.isChecked()) {
					toggleBtn_update.setSelected(true);
					tv_update.setText(getResources().getString(
							R.string.feed_refresh));
				} else {
					if (mListener != null) {
						mListener.onRefreshFeed();
					}
					// toggleBtn_update.setSelected(false);
					tv_update.setText(getResources().getString(
							R.string.feed_refreshing));
				}
				tv_update.invalidate();
			}
		});
		iv_backline = (ImageView) v.findViewById(R.id.feed_backline);
		tv_update = (TextView) v.findViewById(R.id.list_news_feed_Item_text);
	}

	public void setVisibleLine(boolean b) {
		if (b) {
			iv_backline.setVisibility(View.VISIBLE);
		} else {
			iv_backline.setVisibility(View.GONE);
		}
		iv_backline.invalidate();
	}

	public interface OnRefreshFeedListener {
		public void onRefreshFeed();
	}

	OnRefreshFeedListener mListener;

	public void setOnRefreshFeedListener(OnRefreshFeedListener listener) {
		mListener = listener;
	}
}
