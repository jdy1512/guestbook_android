package com.allchange.guestbook.main.feedchat;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.allchange.guestbook.R;
import com.allchange.guestbook.booking.search.map.MapImageCacheStore;
import com.allchange.guestbook.dialog.Dialog_Feed_Detail;
import com.allchange.guestbook.property.MyApplication;
import com.allchange.guestbook.property.PropertyManager;
import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

public class NewsFeedListItem extends FrameLayout {

	private static final String TAG = "NewsFeedListItem";
	TextView tv_text, tv_date, tv_name;
	ImageView iv, iv_backline;
	MessageData mData = null;
	FragmentActivity mContext;
	LinearLayout ll_layout;

	public NewsFeedListItem(FragmentActivity context, MessageData d) {
		super(context);
		mContext = context;
		mData = d;
		init();
	}

	public void setVisibleLine(boolean b) {
		if (b) {
			iv_backline.setVisibility(View.VISIBLE);
		} else {
			iv_backline.setVisibility(View.GONE);
		}
		iv_backline.invalidate();
	}

	public void setMessageData(MessageData data) {
		mData = data;
		setViewData();
	}

	@SuppressLint("NewApi")
	private void setViewData() {
		if (PropertyManager.getInstance().getId().equals(mData.email_id)) {
			ll_layout.setBackground(getResources().getDrawable(
					R.drawable.message_bg_mine));
		} else {
			ll_layout.setBackground(getResources().getDrawable(
					R.drawable.message_bg_guest));
		}
		// ninepatch image 때문에
		ll_layout.setPadding(0, 0, 0, 0);

		tv_name.setText(mData.lastname + mData.firstname);
		tv_text.setText(mData.last_message);
		Calendar c = Calendar.getInstance();
		Date today = c.getTime();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		Date convertedDate = new Date();
		try {
			convertedDate = dateFormat.parse(mData.last_message_dtime);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		long diff = today.getTime() - convertedDate.getTime();
		long seconds = diff / 1000;
		long minutes = seconds / 60;
		long hours = minutes / 60;
		long days = hours / 24;

		if (seconds < 0) {
			tv_date.setText(getResources().getString(R.string.time_0sec));
		} else if (seconds < 60) {
			tv_date.setText(seconds
					+ getResources().getString(R.string.time_sec));
		} else if (minutes < 60) {
			tv_date.setText(minutes
					+ getResources().getString(R.string.time_min));
		} else if (hours > 24) {
			tv_date.setText(hours
					+ getResources().getString(R.string.time_hour));
		} else {
			DateFormat df = new SimpleDateFormat("yyyy/MM/dd");
			tv_date.setText(df.format(convertedDate.getTime()));
		}

		AjaxCallback<Bitmap> bc = new AjaxCallback<Bitmap>() {
			@Override
			public void callback(String url, Bitmap bitmap, AjaxStatus status) {
				if (bitmap != null) {
					MapImageCacheStore.getInstance().put(url, bitmap);
					iv.setImageBitmap(MyApplication.getCircledBitmap(bitmap,
							url));
				}
			}
		};
		if (MapImageCacheStore.getInstance().get(mData.profile_img_path.trim()) != null) {
			iv.setImageBitmap(MyApplication.getCircledBitmap(MapImageCacheStore
					.getInstance().get(mData.profile_img_path.trim()),
					mData.profile_img_path.trim()));
		} else {
			AQuery aquery = new AQuery(mContext);
			bc.cookies(PropertyManager.getInstance().getCookie());
			aquery.ajax(mData.profile_img_path.trim(), Bitmap.class, bc);
		}

		this.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// Dialog_Feed_Detail dfd = new Dialog_Feed_Detail();
				// dfd.setDetailData(mData);
				// dfd.show(mContext.getSupportFragmentManager(), TAG);
			}
		});
	}

	private void init() {
		View v = LayoutInflater.from(getContext()).inflate(
				R.layout.room_newsfeed_list_item, this);
		tv_name = (TextView) v.findViewById(R.id.list_news_feed_Item_name);
		tv_text = (TextView) v.findViewById(R.id.list_news_feed_Item_text);
		tv_date = (TextView) v.findViewById(R.id.list_news_feed_Item_date);
		ll_layout = (LinearLayout) v
				.findViewById(R.id.list_news_feed_Item_bubblebox);

		iv = (ImageView) v.findViewById(R.id.list_news_feed_Item_image);
		iv_backline = (ImageView) v.findViewById(R.id.feed_backline);

		setViewData();
	}

}
