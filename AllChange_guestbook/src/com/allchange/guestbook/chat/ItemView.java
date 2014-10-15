package com.allchange.guestbook.chat;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Checkable;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.allchange.guestbook.R;
import com.allchange.guestbook.booking.search.map.MapImageCacheStore;
import com.allchange.guestbook.picker.Dtime;
import com.allchange.guestbook.property.MyApplication;
import com.allchange.guestbook.property.PropertyManager;
import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

public class ItemView extends FrameLayout implements Checkable {
	/**
	 * 본인
	 */
	ImageView iconView;
	TextView titleView;
	TextView descView;
	TextView dateView;
	MyData mData;
	boolean isChecked = false;
	Context mContext;

	public interface OnImageClickListener {
		public void onImageClick(View v, MyData d);
	}

	OnImageClickListener mListener;

	public void setOnImageClickListener(OnImageClickListener listener) {
		mListener = listener;
	}

	public ItemView(Context context) {
		super(context);
		mContext = context;
		init();
	}

	private void init() {
		// Context context = getContext();
		// LayoutInflater inflater = LayoutInflater.from(context);
		// inflater.inflate(R.layout.item_layout, this);
		LayoutInflater.from(getContext()).inflate(R.layout.item2_layout, this);
		iconView = (ImageView) findViewById(R.id.iconView);
		iconView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mListener != null) {
					mListener.onImageClick(ItemView.this, mData);
				}

			}
		});
		titleView = (TextView) findViewById(R.id.titleView);
		descView = (TextView) findViewById(R.id.descView);
		dateView = (TextView) findViewById(R.id.dateView);
	}

	public void setMyData(MyData data) {
		// iconView.setImageResource(data.imageId);
		if (data.name.equals("null") || data.name == null) {
		} else {
			titleView.setText(data.name);
		}
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

		mData = data;
		isChecked = false;
		setSelectedColor();

		AjaxCallback<Bitmap> bc = new AjaxCallback<Bitmap>() {
			@Override
			public void callback(String url, Bitmap bitmap, AjaxStatus status) {
				if (bitmap != null) {
					MapImageCacheStore.getInstance().put(url, bitmap);
					iconView.setImageBitmap(MyApplication.getCircledBitmap(
							bitmap, url));
					bitmap.recycle();
				}
			}
		};

		try {
			if (MapImageCacheStore.getInstance().get(data.imageUrl.trim()) != null) {
				iconView.setImageBitmap(MyApplication.getCircledBitmap(
						MapImageCacheStore.getInstance().get(
								data.imageUrl.trim()), data.imageUrl.trim()));
			} else {
				AQuery aquery = new AQuery(mContext);
				bc.cookies(PropertyManager.getInstance().getCookie());
				aquery.ajax(data.imageUrl.trim(), Bitmap.class, bc);
			}
		} catch (Exception e) {
		}

	}

	@Override
	public boolean isChecked() {
		return isChecked;
	}

	@Override
	public void setChecked(boolean checked) {
		if (isChecked == checked)
			return;
		isChecked = checked;
		setSelectedColor();
	}

	@Override
	public void toggle() {
		isChecked = !isChecked;
		setSelectedColor();
	}

	private void setSelectedColor() {
		if (isChecked) {
			setBackgroundColor(Color.DKGRAY);
		} else {
			setBackgroundColor(Color.WHITE);
		}
	}
}
