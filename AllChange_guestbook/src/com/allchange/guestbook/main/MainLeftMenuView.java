package com.allchange.guestbook.main;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.allchange.guestbook.R;
import com.allchange.guestbook.booking.search.map.MapImageCacheStore;
import com.allchange.guestbook.bookingsitelist.BookingSiteListActivity;
import com.allchange.guestbook.chat.DBModel;
import com.allchange.guestbook.dialog.Dialog_Log_out;
import com.allchange.guestbook.dialog.Dialog_Log_out.OnLogoutListener;
import com.allchange.guestbook.property.MyApplication;
import com.allchange.guestbook.property.PropertyManager;
import com.allchange.guestbook.splash.SplashActivity;
import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

public class MainLeftMenuView extends FrameLayout implements
		View.OnClickListener, OnLogoutListener {

	private String TAG = "MainLeftMenuView";
	private LinearLayout btn_logOut, ll_configuration, ll_mytrips,
			ll_condition, ll_market;
	ImageButton btn_accommo;
	private TextView tv_name, tv_house, tv_dates, tv_sentence;
	private ImageView iv_profile;

	String[] words = { "", "Business", "Festival", "Sightseeing", "Myself",
			"Friends", "Family", "Advice", "Guestbook", "Both of them" };

	public MainLeftMenuView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout myView = (LinearLayout) inflater.inflate(
				R.layout.main_left_menu, null);
		this.addView(myView);

		String mProperties = PropertyManager.getInstance().getProperties();
		String[] arrProperties = mProperties
				.split(PropertyManager.SPLITE_STRING);
		// for (String string : arrProperties) {
		// Log.e(TAG, "str : " + string);
		// }

		btn_accommo = (ImageButton) myView
				.findViewById(R.id.leftMenu_btn_editAccommo);
		btn_logOut = (LinearLayout) myView
				.findViewById(R.id.leftMenu_btn_logOut);
		ll_configuration = (LinearLayout) myView
				.findViewById(R.id.leftMenu_configuration);
		ll_mytrips = (LinearLayout) myView.findViewById(R.id.leftMenu_mytrips);

		ll_market = (LinearLayout) myView
				.findViewById(R.id.leftMenu_marketPlace);

		tv_name = (TextView) myView.findViewById(R.id.leftMenu_name);

		tv_house = (TextView) myView.findViewById(R.id.leftMenu_house);

		tv_dates = (TextView) myView.findViewById(R.id.leftMenu_days);

		tv_sentence = (TextView) myView.findViewById(R.id.leftMenu_sentence);
		iv_profile = (ImageView) myView.findViewById(R.id.leftMenu_profile);

		ll_condition = (LinearLayout) myView
				.findViewById(R.id.leftMenu_term_and_condition);

		try {
			tv_name.setText(arrProperties[3] + arrProperties[4]);
			if (arrProperties[0].equals("null") || arrProperties[0] == null) {
			} else {
				tv_house.setText(arrProperties[0]);
			}

			tv_dates.setText(PropertyManager.getInstance().getCheckin()
					.substring(0, 4)
					+ "."
					+ PropertyManager.getInstance().getCheckin()
							.substring(4, 6)
					+ "."
					+ PropertyManager.getInstance().getCheckin()
							.substring(6, 8)
					+ "."
					+ " - "
					+ PropertyManager.getInstance().getCheckout()
							.substring(0, 4)
					+ "."
					+ PropertyManager.getInstance().getCheckout()
							.substring(4, 6)
					+ "."
					+ PropertyManager.getInstance().getCheckout()
							.substring(6, 8) + ".");
			tv_sentence.setText(words[Integer.parseInt(arrProperties[14])]
					+ words[Integer.parseInt(arrProperties[15])]
					+ words[Integer.parseInt(arrProperties[16])]);

			if (PropertyManager.getInstance().getCheckin() != null) {
				btn_accommo.setImageDrawable(getResources().getDrawable(
						R.drawable.sliding_btn_edit_accomo));
			}
		} catch (Exception e) {
			// TODO: handle exception
		}

		// 이미지 미리가져오기
		AjaxCallback<Bitmap> bc = new AjaxCallback<Bitmap>() {
			@Override
			public void callback(String url, Bitmap bitmap, AjaxStatus status) {
				if (bitmap != null) {
					Bitmap b = Bitmap.createScaledBitmap(bitmap, 200, 200,
							false);
					MapImageCacheStore.getInstance().put(url, b);
					iv_profile.setImageBitmap(MyApplication.getCircledBitmap(
							bitmap, url));
				}
			}
		};

		if (MapImageCacheStore.getInstance().get(arrProperties[7].trim()) != null) {
			iv_profile.setImageBitmap(MyApplication.getCircledBitmap(
					MapImageCacheStore.getInstance().get(
							arrProperties[7].trim()), arrProperties[7].trim()));
		} else {
			AQuery aquery = new AQuery(getContext());
			bc.cookies(PropertyManager.getInstance().getCookie());
			aquery.ajax(arrProperties[7].trim(), Bitmap.class, bc);
		}

		btn_logOut.setOnClickListener(this);
		ll_configuration.setOnClickListener(this);
		ll_mytrips.setOnClickListener(this);
		ll_market.setOnClickListener(this);
		iv_profile.setOnClickListener(this);
		ll_condition.setOnClickListener(this);
		btn_accommo.setOnClickListener(this);
	}

	private LeftMenuItemListener mListener;

	public interface LeftMenuItemListener {
		public void onClickleftMenuItem(View view);
	}

	public void setOnClickleftMenuItem(LeftMenuItemListener listener) {
		mListener = listener;
	}

	TripPalMainActivity mActivity;

	public void setTripPalMainActivity(TripPalMainActivity activity) {
		mActivity = activity;
	}

	@Override
	public void onClick(View v) {
		if (mListener != null)
			mListener.onClickleftMenuItem(v);
		else {
			Log.e(TAG, "onClick error");
		}
		Intent intent = new Intent(mActivity, SplashActivity.class);
		switch (v.getId()) {
		case R.id.leftMenu_btn_logOut:
			Dialog_Log_out dlo = new Dialog_Log_out();
			dlo.setOnLogoutListener(this);
			dlo.show(mActivity.getSupportFragmentManager(), TAG);

			break;
		case R.id.leftMenu_configuration:
			// TripPalMainActivity에서 처리함
			break;

		case R.id.leftMenu_mytrips:
			// TripPalMainActivity에서 처리함
			break;

		case R.id.leftMenu_marketPlace:
			// TripPalMainActivity에서 처리함
			break;

		case R.id.leftMenu_profile:
			// TripPalMainActivity에서 처리함
			break;

		case R.id.leftMenu_term_and_condition:
			// TripPalMainActivity에서 처리함
			// Dialog_TermOfService dts = new Dialog_TermOfService();
			// dts.show(mActivity.getSupportFragmentManager(), TAG);
			break;

		case R.id.leftMenu_btn_editAccommo:
			intent = new Intent(mActivity, BookingSiteListActivity.class);
			mActivity.startActivity(intent);
			break;

		default:
			break;
		}
	}

	@Override
	public void onLogout() {
		PropertyManager.getInstance().setCookie("", "");
		PropertyManager.getInstance().setId("");
		PropertyManager.getInstance().setLoginMethod("");
		PropertyManager.getInstance().setToken("");
		PropertyManager.getInstance().setProperties("");

		DBModel.getInstance().removeAll();
		// 시작위치로 가기
		Intent intent = new Intent(mActivity, SplashActivity.class);
		// intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
		// intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		mActivity.startActivity(intent);
		mActivity.finish();
	}
}
