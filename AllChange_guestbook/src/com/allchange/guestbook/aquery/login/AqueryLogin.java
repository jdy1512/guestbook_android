package com.allchange.guestbook.aquery.login;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.util.Log;

import com.allchange.guestbook.aquery.AqueryCallbackListener;
import com.allchange.guestbook.aquery.EasySSLSocketFactory;
import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

public class AqueryLogin {
	private AqueryCallbackListener mListener;
	private static final String TAG = "AqueryLogin";

	public AqueryLogin(AqueryCallbackListener listener, AQuery aq, String url,
			String id, String pwd, String reg_id) {
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
		// params 생성
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("master_id", id);
		params.put("master_pwd", pwd);
		params.put("reg_id", reg_id);
		// ssl
		cb.setSSF(new EasySSLSocketFactory());
		// request 전송
		aq.ajax(url, params, JSONObject.class, cb);
	}
}
