package com.allchange.guestbook.aquery.login;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.util.Log;

import com.allchange.guestbook.aquery.AqueryCallbackListener;
import com.allchange.guestbook.property.PropertyManager;
import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

public class AquerySessionCheck {
	private static final String TAG = "AquerySessionCheck";
	private AqueryCallbackListener mListener;

	public AquerySessionCheck(AqueryCallbackListener listener, AQuery aq,
			String url) {
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
		// cookie 첨부
		cb.cookies(PropertyManager.getInstance().getCookie());
		// request 전송
		aq.ajax(url, params, JSONObject.class, cb);
	}
}