package com.allchange.guestbook.property;

import java.util.HashMap;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;

import com.androidquery.AQuery;

public class MyApplication extends Application {
	private static Context mContext;

	public enum AppManager {
		INSTANCE;
		public AQuery aquery;
	}

	static AQuery aq;

	@Override
	public void onCreate() {
		super.onCreate();
		mContext = this;

		AppManager.INSTANCE.aquery = new AQuery(mContext);
	}

	public static Context getContext() {
		return mContext;
	}

	static HashMap<String, Bitmap> mapBitmap = new HashMap<String, Bitmap>();

	public static Bitmap getCircledBitmap(Bitmap bitmap, String key) {

		Bitmap result;
		if (mapBitmap.containsKey(key)) {
			result = mapBitmap.get(key);
			return result;
		} else {
			result = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(),
					Bitmap.Config.ARGB_8888);
			mapBitmap.put(key, result);
		}

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
