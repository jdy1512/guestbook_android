package com.allchange.guestbook.main.feedchat;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.allchange.guestbook.R;
import com.allchange.guestbook.booking.search.map.MapImageCacheStore;
import com.allchange.guestbook.property.PropertyManager;
import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

public class ProfileListItem extends FrameLayout {

	TextView tv_location, tv_listNum;
	ProfileData mData = null;
	Context mContext;

	public ProfileListItem(Context context, ProfileData d) {
		super(context);
		mContext = context;
		mData = d;
		init();
	}

	private void init() {

		View v = LayoutInflater.from(getContext()).inflate(
				R.layout.room_profile_list_item, this);
		TextView tv = (TextView) v.findViewById(R.id.list_profile_Item_name);
		tv.setText(mData.user_firstname);
		tv = (TextView) v.findViewById(R.id.list_profile_Item_text);
		tv.setText(mData.last_message);
		tv = (TextView) v.findViewById(R.id.list_profile_Item_date);
		tv.setText(mData.last_message_dtime);
		tv = (TextView) v.findViewById(R.id.list_profile_Item_email);
		tv.setText(mData.user_email);

		MapEmailImage.getInstance().put(mData.user_email,
				mData.user_profile_img);

		final ImageView iv = (ImageView) v
				.findViewById(R.id.list_profile_Item_image);
		// AQuery aq = new AQuery(getContext());
		// aq.id(iv).image(
		// mData.user_profile_img,
		// true,
		// true,
		// 0,
		// 0,
		// BitmapFactory.decodeResource(getContext().getResources(),
		// R.drawable.profile_defalut_image), 0, AQuery.FADE_IN);

		// 이미지 미리가져오기
		AjaxCallback<Bitmap> bc = new AjaxCallback<Bitmap>() {
			@Override
			public void callback(String url, Bitmap bitmap, AjaxStatus status) {
				if (bitmap != null) {
					MapImageCacheStore.getInstance().put(url, bitmap);
					iv.setImageBitmap(bitmap);
				}
			}
		};

		if (MapImageCacheStore.getInstance().get(mData.user_profile_img.trim()) != null) {
			iv.setImageBitmap(MapImageCacheStore.getInstance().get(
					mData.user_profile_img.trim()));
		} else {
			AQuery aquery = new AQuery(mContext);
			bc.cookies(PropertyManager.getInstance().getCookie());
			aquery.ajax(mData.user_profile_img.trim(), Bitmap.class, bc);
		}

	}

	// public interface OnFiltersButtonClickListener {
	// public void onFiltersButtonClick(View v, DiscoverItemData d);
	// }
	//
	// OnFiltersButtonClickListener mListener;
	//
	// public void setOnFiltersButtonClickListener(
	// OnFiltersButtonClickListener listener) {
	// mListener = listener;
	// }
}
