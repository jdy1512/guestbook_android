package com.allchange.guestbook.login;

import org.json.JSONException;
import org.json.JSONObject;

import android.accounts.AccountManager;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.allchange.guestbook.R;
import com.allchange.guestbook.aquery.AndroidQuery;
import com.allchange.guestbook.aquery.FBGoogleCallbackListener;
import com.allchange.guestbook.aquery.facebook.login.FBLoginCallbackListener;
import com.allchange.guestbook.aquery.get.properties.AqueryGetPropertiesCallbackListener;
import com.allchange.guestbook.aquery.google.login.GoogleLoginCallbackListener;
import com.allchange.guestbook.chat.MessageActivity;
import com.allchange.guestbook.chat.XMPPManager;
import com.allchange.guestbook.dialog.Dialog_Loading;
import com.allchange.guestbook.main.TripPalMainActivity;
import com.allchange.guestbook.property.MyApplication;
import com.allchange.guestbook.property.PropertyManager;
import com.allchange.guestbook.url.Url;
import com.androidquery.callback.AjaxStatus;
import com.facebook.Session;
import com.facebook.UiLifecycleHelper;

public class MainLoginActivity extends FragmentActivity implements
		FBGoogleCallbackListener, FBLoginCallbackListener,
		AqueryGetPropertiesCallbackListener, GoogleLoginCallbackListener {

	public static final String TAG = "MainLoginActivity";
	private UiLifecycleHelper uiHelper;
	LinearLayout btnFacebook, btnGoogle;
	ImageView iv_splash;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// status bar 숨기기

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		super.onCreate(savedInstanceState);
		if (Dialog_Loading.getInstance().isOnScreen) {
			try {

				Dialog_Loading.getInstance().dismiss();
			} catch (NullPointerException e) {
			}
		}
		uiHelper = new UiLifecycleHelper(this,
				FacebookLogin.getInstance(MainLoginActivity.this).callback);
		if (savedInstanceState != null) {
			uiHelper.onCreate(savedInstanceState);
		}

		setContentView(R.layout.activity_login_main);

		Typeface face = Typeface
				.createFromAsset(getAssets(), "Lobster 1.3.otf");
		TextView text = (TextView) findViewById(R.id.SignUpLoginIn_tv_1);
		text.setTypeface(face);
		text = (TextView) findViewById(R.id.SignUpLoginIn_tv_2);
		text.setTypeface(face);
		text = (TextView) findViewById(R.id.SignUpLoginIn_tv_3);
		text.setTypeface(face);
		text = (TextView) findViewById(R.id.SignUpLoginIn_tv_4);
		text.setTypeface(face);
		text = (TextView) findViewById(R.id.SignUpLoginIn_tv_5);
		text.setTypeface(face);

		iv_splash = (ImageView) findViewById(R.id.SignUpLoginIn_iv_splash);

		btnGoogle = (LinearLayout) findViewById(R.id.SignUpLoginIn_btn_Google);
		btnGoogle.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// SessionCheck.instance().check(
				// ChangeState.getInstance().getHandler(), TAG);

				if (!Dialog_Loading.getInstance().isOnScreen) {
					Dialog_Loading.getInstance().show(
							getSupportFragmentManager(), TAG);
				}

				GoogleLogin.getInstance(MainLoginActivity.this).getUsername();
			}
		});

		btnFacebook = (LinearLayout) findViewById(R.id.SignUpLoginIn_btn_Facebook);
		btnFacebook.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!Dialog_Loading.getInstance().isOnScreen) {
					Dialog_Loading.getInstance().show(
							getSupportFragmentManager(), TAG);
				}

				FacebookLogin.getInstance(MainLoginActivity.this)
						.onClickContectFacebook();
			}
		});

		if (PropertyManager.getInstance().getLoginMethod()
				.equals(PropertyManager.METHOD_FACEBOOK)) {
			iv_splash.setVisibility(View.VISIBLE);
			if (!Dialog_Loading.getInstance().isOnScreen) {
				Dialog_Loading.getInstance().show(getSupportFragmentManager(),
						TAG);
			}
			Log.e(TAG, "METHOD_FACEBOOK");
			AndroidQuery.getInstance().requestFBLogin(this,
					Url.SERVER_FACEBOOK_LOGIN,
					PropertyManager.getInstance().getToken());
		} else if (PropertyManager.getInstance().getLoginMethod()
				.equals(PropertyManager.METHOD_GOOGLE)) {
			Log.e(TAG, "METHOD_GOOGLE");
			iv_splash.setVisibility(View.VISIBLE);
			if (!Dialog_Loading.getInstance().isOnScreen) {
				Dialog_Loading.getInstance().show(getSupportFragmentManager(),
						TAG);
			}
			String mProperties = PropertyManager.getInstance().getProperties();
			String[] arrProperties = mProperties
					.split(PropertyManager.SPLITE_STRING);
			try {
				AndroidQuery.getInstance().requestGoogleLogin(this,
						Url.SERVER_GOOGLE_LOGIN,
						PropertyManager.getInstance().getId(),
						arrProperties[3], arrProperties[4],
						Integer.parseInt(arrProperties[6]), arrProperties[7],
						PropertyManager.getInstance().getGoogleId());
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}

		}
	}

	public void RequestSignUp() {

	}

	@Override
	public void onResume() {
		super.onResume();

		Session session = Session.getActiveSession();
		if (session != null && (session.isOpened() || session.isClosed())) {
			FacebookLogin.getInstance(MainLoginActivity.this)
					.onSessionStateChange(session, session.getState(), null);
		}

		uiHelper.onResume();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		uiHelper.onDestroy();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		uiHelper.onSaveInstanceState(outState);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.i(TAG, "onActivityResult");
		if (requestCode == GoogleLogin.REQUEST_CODE_PICK_ACCOUNT) {
			if (resultCode == RESULT_OK) {
				GoogleLogin.getInstance(MainLoginActivity.this).mEmail = data
						.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
				// Log.e(TAG, "mEmail : " + mEmail);
				GoogleLogin.getInstance(MainLoginActivity.this).getUsername();
			} else if (resultCode == RESULT_CANCELED) {
				Toast.makeText(this,
						getResources().getString(R.string.login_pick_account),
						Toast.LENGTH_SHORT).show();
			}
		} else if ((requestCode == GoogleLogin.REQUEST_CODE_RECOVER_FROM_AUTH_ERROR || requestCode == GoogleLogin.REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR)
				&& resultCode == RESULT_OK) {
			GoogleLogin.getInstance(MainLoginActivity.this)
					.handleAuthorizeResult(resultCode, data);

			if (Dialog_Loading.getInstance().isOnScreen) {
				try {

					Dialog_Loading.getInstance().dismiss();
				} catch (NullPointerException e) {
				}
			}
			return;
		}
		super.onActivityResult(requestCode, resultCode, data);
		uiHelper.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void FBGoogleCallback(String source, String token) {
		Log.i(TAG, "FBGoogleCallback");
		//
		// Log.e(TAG, "from : " + source + "  " + id + firtName + LastName + sex
		// + location + sourceId + "token : " + token);
		// if (sex.equals("male")) {
		// sex = "1";
		// } else {
		// sex = "0";
		// }

		if (source.equals(MyApplication.getContext().getResources()
				.getString(R.string.facebook))) {
			Log.i(TAG, "FBGoogleCallback1");
			PropertyManager.getInstance().setToken(token);
			AndroidQuery.getInstance().requestFBLogin(this,
					Url.SERVER_FACEBOOK_LOGIN, token);
		}

	}

	@Override
	public void FBLoginCallback(String url, JSONObject json, AjaxStatus status) {
		Log.e(TAG, "FBLoginCallback");
		String TAG_DETAIL = "FBLoginCallback";
		if (json != null) {
			try {
				if (json.getString("result").equals("success")) {
					PropertyManager.getInstance().setCookie(
							status.getCookies().get(0).getName(),
							status.getCookies().get(0).getValue());
					PropertyManager.getInstance().setId(
							json.getString("user_email"));
					PropertyManager.getInstance().setLoginMethod(
							PropertyManager.METHOD_FACEBOOK);
					// TODO
					// // 채팅 유저목록에도 가입시키기
					XMPPManager.getInstance().addAccount(
							PropertyManager.getInstance().getId().split("@")[0]
									+ MessageActivity.CHAT_ID_TAIL
									+ PropertyManager.getInstance().getId()
											.split("@")[1], "allchange", null);
					AndroidQuery.getInstance().requestGetProperties(this,
							Url.SERVER_GET_PROPERTIES);
				} else if (json.getString("result").equals("fail")) {
					Log.d(TAG + TAG_DETAIL, json.getString("result_msg"));
					iv_splash.setVisibility(View.GONE);
				}
			} catch (JSONException e) {
				Log.e(TAG + TAG_DETAIL, "parsing error");
				e.printStackTrace();
			}
		} else {
			Log.e(TAG + TAG_DETAIL, "json is null");
			Log.e(TAG + TAG_DETAIL, status.getMessage());
			Log.e(TAG + TAG_DETAIL, "" + status.getCode());
			Log.e(TAG + TAG_DETAIL, status.getRedirect());
		}
	}

	@Override
	public void AqueryGetPropertiesCallback(String url, JSONObject json,
			AjaxStatus status) {
		String TAG_DETAIL = "AqueryGetPropertiesCallback";
		if (Dialog_Loading.getInstance().isOnScreen) {
			Dialog_Loading.getInstance().dismiss();
		}
		if (json != null) {
			try {
				if (json.getString("result").equals("success")) {

					// Log.e(TAG + TAG_DETAIL,
					// "AqueryGetPropertiesCallback \r\nid : "
					// + PropertyManager.getInstance().getId()
					// + "checkin : "
					// + PropertyManager.getInstance()
					// .getCheckin()
					// + "checkout : "
					// + PropertyManager.getInstance()
					// .getCheckout()
					// + "distance : "
					// + PropertyManager.getInstance()
					// .getRadiusMeter()
					// + "properties : "
					// + PropertyManager.getInstance()
					// .getProperties());

					// 일단 보류 (반경 계산하기)
					// float[] result = new float[1];
					// Location.distanceBetween(
					// Double.parseDouble(json.getString("gb_lat_start")),
					// Double.parseDouble(json.getString("gb_lon_start")),
					// Double.parseDouble(json.getString("gb_lat_end")),
					// Double.parseDouble(json.getString("gb_lon_end")),
					// result);
					//
					// PropertyManager
					// .getInstance()
					// .setDistance(
					// ""
					// + ((float) Math
					// .round((result[0] / 2) * 100) / 100));

					PropertyManager.getInstance().setCheckout(
							json.getString("gb_check_outdate"));
					PropertyManager.getInstance().setCheckin(
							json.getString("gb_check_indate"));
					PropertyManager.getInstance().setLatitude(
							json.getString("gb_latitude"));
					PropertyManager.getInstance().setLongitude(
							json.getString("gb_longitude"));

					PropertyManager.getInstance().setId(
							json.getString("user_email"));
					PropertyManager.getInstance().setProperties(
							json.getString("gb_name")
									+ PropertyManager.SPLITE_STRING
									+ json.getString("gb_address")
									+ PropertyManager.SPLITE_STRING
									+ json.getString("user_city")
									+ PropertyManager.SPLITE_STRING
									+ json.getString("user_lastname")
									+ PropertyManager.SPLITE_STRING
									+ json.getString("user_firstname")
									+ PropertyManager.SPLITE_STRING
									+ json.getString("user_birthday")
									+ PropertyManager.SPLITE_STRING
									+ json.getString("user_sex")
									+ PropertyManager.SPLITE_STRING
									+ json.getString("user_profile_img_path")
									+ PropertyManager.SPLITE_STRING
									+ json.getString("gb_channel")
									+ PropertyManager.SPLITE_STRING
									+ json.getString("gb_lat_start")
									+ PropertyManager.SPLITE_STRING
									+ json.getString("gb_lat_end")
									+ PropertyManager.SPLITE_STRING
									+ json.getString("gb_lon_start")
									+ PropertyManager.SPLITE_STRING
									+ json.getString("gb_lon_end")
									+ PropertyManager.SPLITE_STRING
									+ json.getString("user_nationality")
									+ PropertyManager.SPLITE_STRING
									+ json.getString("gb_for")
									+ PropertyManager.SPLITE_STRING
									+ json.getString("gb_with")
									+ PropertyManager.SPLITE_STRING
									+ json.getString("gb_seeking"));

					Intent i = new Intent();
					i.setClass(MainLoginActivity.this,
							TripPalMainActivity.class);
					startActivity(i);
					this.finish();
					// i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				} else if (json.getString("result").equals("fail")) {
				}
			} catch (JSONException e) {
			}
		} else {
		}
	}

	@Override
	public void GoogleLoginCallback(String url, JSONObject json,
			AjaxStatus status, String email) {

		if (json != null) {
			try {
				if (json.getString("result").equals("success")) {
					PropertyManager.getInstance().setCookie(
							status.getCookies().get(0).getName(),
							status.getCookies().get(0).getValue());
					PropertyManager.getInstance().setId(email);
					PropertyManager.getInstance().setLoginMethod(
							PropertyManager.METHOD_GOOGLE);

					// TODO
					// // 채팅 유저목록에도 가입시키기
					XMPPManager.getInstance().addAccount(
							PropertyManager.getInstance().getId().split("@")[0]
									+ MessageActivity.CHAT_ID_TAIL
									+ PropertyManager.getInstance().getId()
											.split("@")[1], "allchange", null);
					AndroidQuery.getInstance().requestGetProperties(this,
							Url.SERVER_GET_PROPERTIES);
				} else if (json.getString("result").equals("fail")) {
					Log.d(TAG, json.getString("result_msg"));
					iv_splash.setVisibility(View.GONE);
				}
			} catch (JSONException e) {
				Log.e(TAG, "parsing error");
				e.printStackTrace();
			}
		} else {
			Log.e(TAG, "json is null");
			Log.e(TAG, status.getMessage());
			Log.e(TAG, "" + status.getCode());
			Log.e(TAG, status.getRedirect());
		}

	}

}