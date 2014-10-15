package com.allchange.guestbook.aquery.google.login;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.util.Log;

import com.allchange.guestbook.aquery.EasySSLSocketFactory;
import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

public class AqueryGoogleLogin {

	public static final String TAG = "AqueryGoogleLogin";
	GoogleLoginCallbackListener mListener;

	public AqueryGoogleLogin(GoogleLoginCallbackListener listener,
			AQuery aquery, String url, final String email_id, String firstname,
			String lastname, int sex, String profile_img_path, String code) {

		mListener = listener;
		AjaxCallback<JSONObject> cb = new AjaxCallback<JSONObject>() {
			@Override
			public void callback(String url, JSONObject json, AjaxStatus status) {
				if (mListener != null)
					mListener.GoogleLoginCallback(url, json, status, email_id);
				else {
					Log.e(TAG, "ERROR!");
				}
			}
		};

		// params 생성
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("email_id", email_id);
		params.put("firstname", firstname);
		params.put("lastname", lastname);
		params.put("sex", sex);
		params.put("profile_img_path", profile_img_path);
		params.put("code", code);
		// cookie 첨부
		// ssl
		cb.setSSF(new EasySSLSocketFactory());
		// request 전송
		aquery.ajax(url, params, JSONObject.class, cb);
	}

	public void reRequest() {
	}
}
