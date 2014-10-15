package com.allchange.guestbook.aquery.signup;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.util.Log;

import com.allchange.guestbook.aquery.AqueryCallbackListener;
import com.allchange.guestbook.aquery.EasySSLSocketFactory;
import com.allchange.guestbook.property.PropertyManager;
import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

public class AquerySignUp {
	private static final String TAG = "AquerySignUp";
	private AqueryCallbackListener mListener;

	public AquerySignUp(AqueryCallbackListener listener, AQuery aq, String url,
			String id, String pwd, String reg_id, String phoneNum,
			String firtName, String LastName, String sex, String location,
			String facebookId) {
		mListener = listener;
		// callback 설정
		AjaxCallback<JSONObject> cb = new AjaxCallback<JSONObject>() {
			@Override
			public void callback(String url, JSONObject json, AjaxStatus status) {
				// TODO Auto-generated method stub
				if (mListener != null)
					mListener.AqueryCallback(url, json, status);
				else {
					Log.e(TAG, "AqueryCallbackListener ERROR!");
				}
			}
		};

		Map<String, Object> mParams = new HashMap<String, Object>();
		mParams.put("user_id", id);
		mParams.put("user_facebook_id", facebookId);
		mParams.put("user_pwd", pwd);
		mParams.put("user_firstname", firtName);
		mParams.put("user_lastname", LastName);
		mParams.put("user_birth", "");
		mParams.put("user_sex", sex);
		mParams.put("user_sex", location);
		// mParams.put("user_profile_img", bitmapStream.toByteArray());
		mParams.put("user_phone", phoneNum);
		mParams.put("user_device_type", "1");
		mParams.put("user_reg_id", PropertyManager.getInstance().getRegistId());

		// cookie 첨부
		// ssl
		cb.setSSF(new EasySSLSocketFactory());
		// request 전송
		aq.ajax(url, mParams, JSONObject.class, cb);
	}
}
