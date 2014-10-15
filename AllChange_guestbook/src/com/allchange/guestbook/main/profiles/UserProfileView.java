package com.allchange.guestbook.main.profiles;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.location.Location;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.allchange.guestbook.R;
import com.allchange.guestbook.booking.search.map.MapImageCacheStore;
import com.allchange.guestbook.property.MyApplication;
import com.allchange.guestbook.property.PropertyManager;
import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

public class UserProfileView extends FrameLayout {

	private static final String TAG = "UserProfileView";
	private Context mContext;
	private TextView tv_name, tv_distance;
	private ImageView iv_for, iv_with, iv_seek;
	ChattingRoomPeopleData mData;

	private int[] imageArr = { R.drawable.icon_1_n, R.drawable.icon_2_n,
			R.drawable.icon_3_n, R.drawable.icon_4_n, R.drawable.icon_5_n,
			R.drawable.icon_6_n, R.drawable.icon_7_n, R.drawable.icon_8_n,
			R.drawable.icon_9_n, R.drawable.icon_10_n, R.drawable.icon_11_n,
			R.drawable.icon_12_n };

	public UserProfileView(Context context, ChattingRoomPeopleData d) {
		super(context);

		mContext = context;
		mData = d;
		init();
	}

	private void init() {
		View v = LayoutInflater.from(getContext()).inflate(
				R.layout.profile_view, this);

		final ImageView iv = (ImageView) v.findViewById(R.id.profile_imageView);

		tv_name = (TextView) v.findViewById(R.id.profile_tv_name);
		tv_distance = (TextView) v.findViewById(R.id.profile_tv_distance);

		iv_for = (ImageView) v.findViewById(R.id.profile_iv_for);
		iv_with = (ImageView) v.findViewById(R.id.profile_iv_with);
		iv_seek = (ImageView) v.findViewById(R.id.profile_iv_seek);
		tv_name.setText(mData.lastname + mData.firstname);

		iv_for.setImageDrawable(getResources().getDrawable(
				imageArr[Integer.parseInt(mData.gb_for) - 1]));
		iv_with.setImageDrawable(getResources().getDrawable(
				imageArr[Integer.parseInt(mData.gb_with) - 1]));
		iv_seek.setImageDrawable(getResources().getDrawable(
				imageArr[Integer.parseInt(mData.gb_seeking) - 1]));

		float[] results = new float[1];
		Location.distanceBetween(Double.parseDouble(PropertyManager
				.getInstance().getLatitude()), Double
				.parseDouble(PropertyManager.getInstance().getLongitude()),
				mData.gb_latitude, mData.gb_longitude, results);

		tv_distance
				.setText(((float) Math.round((results[0] / 10)) / 100 + "km"));

		// 이미지 미리가져오기
		AjaxCallback<Bitmap> bc = new AjaxCallback<Bitmap>() {
			@Override
			public void callback(String url, Bitmap bitmap, AjaxStatus status) {
				if (bitmap != null) {
					Bitmap b = Bitmap.createScaledBitmap(bitmap, 200, 200,
							false);
					MapImageCacheStore.getInstance().put(url, b);
					iv.setImageBitmap(MyApplication.getCircledBitmap(bitmap,
							url));
				}
			}
		};
		// String mProperties = PropertyManager.getInstance().getProperties();
		// String[] arrProperties = mProperties
		// .split(PropertyManager.SPLITE_STRING);
		// if (mData.email_id.equals("kl4684@gmail.com")) {
		// nameBar.setBackgroundColor(Color.argb(55, 00, 00, 0xff));
		// }

		if (MapImageCacheStore.getInstance().get(mData.profile_img_path.trim()) != null) {
			iv.setImageBitmap(MyApplication.getCircledBitmap(MapImageCacheStore
					.getInstance().get(mData.profile_img_path.trim()),
					mData.profile_img_path.trim()));
		} else {
			AQuery aquery = new AQuery(mContext);
			bc.cookies(PropertyManager.getInstance().getCookie());
			aquery.ajax(mData.profile_img_path.trim(), Bitmap.class, bc);
		}
	}

	Bitmap getCircledBitmap(Bitmap bitmap) {

		// 메모리 관리 중요
		Bitmap result = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Bitmap.Config.ARGB_8888);

		Canvas canvas = new Canvas(result);

		int color = Color.BLUE;
		Paint paint = new Paint();
		Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		// canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
				bitmap.getHeight() / 2, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);

		return result;
	}
}
