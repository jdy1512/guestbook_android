package com.allchange.guestbook.aquery.facebook.login;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.util.Log;

import com.allchange.guestbook.aquery.EasySSLSocketFactory;
import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

public class AqueryFBLogin {

	public static final String TAG = "AqueryFBLogin";
	FBLoginCallbackListener mListener;

	public AqueryFBLogin(FBLoginCallbackListener listener, AQuery aquery,
			String url, String token) {

		mListener = listener;
		AjaxCallback<JSONObject> cb = new AjaxCallback<JSONObject>() {
			@Override
			public void callback(String url, JSONObject json, AjaxStatus status) {
				if (mListener != null)
					mListener.FBLoginCallback(url, json, status);
				else {
					Log.e(TAG, "AqueryCallbackListener ERROR!");
				}
			}
		};
		// params 생성
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("token", token);
		// cookie 첨부
		// ssl
		cb.setSSF(new EasySSLSocketFactory());
		// request 전송
		aquery.ajax(url, params, JSONObject.class, cb);
	}

	public void reRequest() {
	}
}
