package com.allchange.guestbook.chat;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.allchange.guestbook.R;
import com.allchange.guestbook.booking.search.map.MapImageCacheStore;
import com.allchange.guestbook.picker.Dtime;
import com.allchange.guestbook.property.PropertyManager;
import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

public class ItemRightView extends FrameLayout {
	/**
	 * 상대방
	 */

	ImageView iconView;
	TextView titleView;
	TextView descView, dateView;
	MyData mData;
	Context mContext;

	public ItemRightView(Context context) {
		super(context);
		mContext = context;
		init();
	}

	private void init() {
		LayoutInflater.from(getContext()).inflate(R.layout.item_right_layout,
				this);
		iconView = (ImageView) findViewById(R.id.iconView);
		titleView = (TextView) findViewById(R.id.titleView);
		descView = (TextView) findViewById(R.id.descView);
		dateView = (TextView) findViewById(R.id.dateView);
	}

	public void setMyData(MyData data) {
		// iconView.setImageResource(data.imageId);
		titleView.setText(data.name);
		descView.setText(data.description);

		// 계산하기 위해서String millisecond를 calendar형으로 바꿔준다.
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(Long.parseLong(data.date));

		// 하루가 지났는지 확인하고 결과에 따라 형태를 바꿔준다
		if (Dtime.instance().getGMTCalendar().compareTo(calendar) > 1) {
			DateFormat df = new SimpleDateFormat("yyyy/MM/dd");
			dateView.setText(df.format(calendar.getTime()));
		} else {
			DateFormat df = new SimpleDateFormat("aaa hh:mm");
			dateView.setText(df.format(calendar.getTime()));
		}

		AjaxCallback<Bitmap> bc = new AjaxCallback<Bitmap>() {
			@Override
			public void callback(String url, Bitmap bitmap, AjaxStatus status) {
				if (bitmap != null) {
					MapImageCacheStore.getInstance().put(url, bitmap);
					iconView.setImageBitmap(bitmap);
					bitmap.recycle();
				}
			}
		};
		if (MapImageCacheStore.getInstance().get(data.imageUrl.trim()) != null) {
			iconView.setImageBitmap(MapImageCacheStore.getInstance().get(
					data.imageUrl.trim()));
		} else {
			AQuery aquery = new AQuery(mContext);
			bc.cookies(PropertyManager.getInstance().getCookie());
			aquery.ajax(data.imageUrl.trim(), Bitmap.class, bc);
		}
	}

	// 몇 시간 몇분전으로 보여주는 형태
	private void uselessSetTimeText() {
		// Calendar c = Calendar.getInstance();
		// Date today = c.getTime();
		//
		// Date messageDate = new Date(Long.parseLong(data.date));
		//
		// long diff = today.getTime() - Long.parseLong(data.date);
		// long seconds = diff / 1000;
		// long minutes = seconds / 60;
		// long hours = minutes / 60;
		// long days = hours / 24;
		//
		// if (seconds < 0) {
		// dateView.setText("0초 전");
		// } else if (seconds < 60) {
		// dateView.setText(seconds + "초 전");
		// } else if (minutes < 60) {
		// dateView.setText(minutes + "분 전");
		// } else if (hours < 24) {
		// dateView.setText(hours + "시간 전");
		// } else {
		// dateView.setText(messageDate.getYear() + "년"
		// + messageDate.getMonth() + "월" + messageDate.getDay() + "일");
		// }
	}
}
