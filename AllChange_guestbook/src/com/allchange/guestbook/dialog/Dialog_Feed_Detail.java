package com.allchange.guestbook.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.allchange.guestbook.R;
import com.allchange.guestbook.booking.search.map.MapImageCacheStore;
import com.allchange.guestbook.main.feedchat.MessageData;
import com.allchange.guestbook.property.PropertyManager;
import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

public class Dialog_Feed_Detail extends DialogFragment {
	public static final String TAG = "Dialog_Feed_Detail";
	private Context mContext;
	MessageData mData = null;
	TextView tv_name, tv_text, tv_date;
	ImageView iv;

	public void setDetailData(MessageData data) {
		mData = data;
	}

	private void setViewData() {
		tv_name.setText(mData.lastname + mData.firstname);
		tv_date.setText(mData.last_message_dtime);
		tv_text.setText(mData.last_message);

		AjaxCallback<Bitmap> bc = new AjaxCallback<Bitmap>() {
			@Override
			public void callback(String url, Bitmap bitmap, AjaxStatus status) {
				if (bitmap != null) {
					MapImageCacheStore.getInstance().put(url, bitmap);
					iv.setImageBitmap(bitmap);
				}
			}
		};
		if (MapImageCacheStore.getInstance().get(mData.profile_img_path.trim()) != null) {
			iv.setImageBitmap(MapImageCacheStore.getInstance().get(
					mData.profile_img_path.trim()));
		} else {
			AQuery aquery = new AQuery(mContext);
			bc.cookies(PropertyManager.getInstance().getCookie());
			aquery.ajax(mData.profile_img_path.trim(), Bitmap.class, bc);
		}
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		final Dialog dialog = new Dialog(getActivity());

		Window window = dialog.getWindow();
		window.requestFeature(Window.FEATURE_NO_TITLE);
		window.setGravity(Gravity.CENTER);
		window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		dialog.setContentView(R.layout.dialog_feed_detail);

		window.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

		// dialog.setCanceledOnTouchOutside(false);

		tv_text = (TextView) dialog
				.findViewById(R.id.dialog_feed_detail_textView);
		tv_name = (TextView) dialog.findViewById(R.id.dialog_feed_detail_name);
		tv_date = (TextView) dialog.findViewById(R.id.dialog_feed_detail_date);

		iv = (ImageView) dialog.findViewById(R.id.dialog_feed_detail_image);
		setViewData();
		return dialog;
	}
}