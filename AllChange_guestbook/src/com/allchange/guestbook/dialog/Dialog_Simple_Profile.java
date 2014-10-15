package com.allchange.guestbook.dialog;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.allchange.guestbook.R;
import com.allchange.guestbook.aquery.AndroidQuery;
import com.allchange.guestbook.aquery.get.visit.AqueryGetVisitCallbackListener;
import com.allchange.guestbook.booking.search.map.MapImageCacheStore;
import com.allchange.guestbook.main.profiles.ChattingRoomPeopleData;
import com.allchange.guestbook.property.MyApplication;
import com.allchange.guestbook.url.Url;
import com.androidquery.callback.AjaxStatus;

public class Dialog_Simple_Profile extends DialogFragment implements
		AqueryGetVisitCallbackListener {
	public static final String TAG = "Dialog_Simple_Profile";
	Button btn;
	EditText editText;
	ImageView iv_for, iv_with, iv_seek;
	TextView tv_checkDate, tv_name, tv_accomo, tv_for, tv_with, tv_seek;
	ChattingRoomPeopleData mData;

	private int[] imageArr = { R.drawable.icon_1_n, R.drawable.icon_2_n,
			R.drawable.icon_3_n, R.drawable.icon_4_n, R.drawable.icon_5_n,
			R.drawable.icon_6_n, R.drawable.icon_7_n, R.drawable.icon_8_n,
			R.drawable.icon_9_n, R.drawable.icon_10_n, R.drawable.icon_11_n,
			R.drawable.icon_12_n };

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
	}

	@SuppressLint("Recycle")
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		final Dialog dialog = new Dialog(getActivity());
		Window window = dialog.getWindow();
		window.requestFeature(Window.FEATURE_NO_TITLE);
		window.setGravity(Gravity.BOTTOM);
		window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		dialog.setContentView(R.layout.dialog_simple_profile);

		window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

		dialog.setCanceledOnTouchOutside(false);
		tv_checkDate = (TextView) dialog
				.findViewById(R.id.dialog_profile_tv_days);
		tv_accomo = (TextView) dialog
				.findViewById(R.id.dialog_profile_tv_accomo);
		tv_name = (TextView) dialog.findViewById(R.id.dialog_profile_tv_name);
		tv_for = (TextView) dialog.findViewById(R.id.dialog_profile_tv_for);
		tv_with = (TextView) dialog.findViewById(R.id.dialog_profile_tv_with);
		tv_seek = (TextView) dialog.findViewById(R.id.dialog_profile_tv_seek);
		iv_for = (ImageView) dialog.findViewById(R.id.dialog_profile_iv_for);
		iv_with = (ImageView) dialog.findViewById(R.id.dialog_profile_iv_with);
		iv_seek = (ImageView) dialog.findViewById(R.id.dialog_profile_iv_seek);

		TypedArray purposeArr = getResources().obtainTypedArray(
				R.array.purposes);

		// tv_checkDate.setText(mData.);
		// tv_accomo.setText(mData.);
		tv_name.setText(mData.lastname + mData.firstname);
		iv_for.setImageDrawable(getResources().getDrawable(
				imageArr[Integer.parseInt(mData.gb_for) - 1]));
		iv_with.setImageDrawable(getResources().getDrawable(
				imageArr[Integer.parseInt(mData.gb_with) - 1]));
		iv_seek.setImageDrawable(getResources().getDrawable(
				imageArr[Integer.parseInt(mData.gb_seeking) - 1]));
		tv_for.setText(purposeArr.getString(Integer.parseInt(mData.gb_for) - 1));
		tv_with.setText(purposeArr.getString(Integer.parseInt(mData.gb_with) - 1));
		tv_seek.setText(purposeArr.getString(Integer.parseInt(mData.gb_seeking) - 1));

		ImageView iv_btn_exit = (ImageView) dialog
				.findViewById(R.id.dialog_profile_btn_exit);
		iv_btn_exit.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dismiss();

			}
		});

		ImageView iv = (ImageView) dialog
				.findViewById(R.id.dialog_profile_image);

		if (MapImageCacheStore.getInstance().get(mData.profile_img_path.trim()) != null) {
			iv.setImageBitmap(MyApplication.getCircledBitmap(MapImageCacheStore
					.getInstance().get(mData.profile_img_path.trim()),
					mData.profile_img_path.trim()));
		}

		TextView tv = (TextView) dialog
				.findViewById(R.id.dialog_profile_tv_name);
		tv.setText(mData.lastname + mData.firstname);

		Button btn = (Button) dialog.findViewById(R.id.dialog_profile_btn_chat);
		btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mListener != null) {
					mListener.onClickChat(mData);
					dialog.dismiss();
				}
			}
		});

		btn = (Button) dialog.findViewById(R.id.dialog_profile_btn_profile);
		btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
			}
		});

		AndroidQuery.getInstance().requestGetVisit(Dialog_Simple_Profile.this,
				Url.SERVER_GET_VISIT, mData.email_id);
		return dialog;
	}

	public void setProfileData(ChattingRoomPeopleData userProfileData) {
		mData = userProfileData;
	}

	public interface OnSimpleProfileListener {
		public void onClickChat(ChattingRoomPeopleData data);
	}

	OnSimpleProfileListener mListener;

	public void setOnSimpleProfileListener(OnSimpleProfileListener listener) {
		mListener = listener;
	}

	@Override
	public void AqueryGetVisitCallback(String url, JSONObject json,
			AjaxStatus status) {

		if (json != null) {
			try {
				if (json.getString("result").equals("success")) {
					tv_checkDate.setText(json.getString("user_check_indate")
							.substring(0, 4)
							+ "."
							+ json.getString("user_check_indate").substring(4,
									6)
							+ "."
							+ json.getString("user_check_indate").substring(6,
									8)
							+ " - "
							+ json.getString("user_check_outdate").substring(0,
									4)
							+ "."
							+ json.getString("user_check_outdate").substring(4,
									6)
							+ "."
							+ json.getString("user_check_outdate").substring(6,
									8));
					tv_accomo.setText(json.getString("user_accommodation"));
					// tv_sentence.setText(json.getString("user_visit_for") +
					// " "
					// + json.getString("user_visit_with") + " "
					// + json.getString("user_visit_seeking"));

				} else if (json.getString("result").equals("fail")) {
					Log.d(TAG, "fail : " + json.getString("result_msg"));
				}
			} catch (JSONException e) {
				Log.e(TAG, "parsing error");
				e.printStackTrace();
			}
		} else {
			Log.e(TAG, "json is null");
			Log.e(TAG, status.getMessage());
			Log.e(TAG, status.getRedirect());
		}
	}
}