package com.allchange.guestbook.aquery.get.profiles;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.util.Log;

import com.allchange.guestbook.aquery.AqueryFailCallbackListener;
import com.allchange.guestbook.aquery.AqueryRootRequest;
import com.allchange.guestbook.property.PropertyManager;
import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

public class AqueryGetProfiles extends AqueryRootRequest {
	public static final String TAG = "AqueryGetProfiles";
	private AqueryGetProfilesCallbackListener mListener;
	AqueryGetProfilesCallbackListener listener;
	AqueryFailCallbackListener fListener;
	AQuery aq;
	String url;
	String date;

	public AqueryGetProfiles(AqueryGetProfilesCallbackListener listener,
			AQuery aq, String url, String date,
			AqueryFailCallbackListener fListener) {
		this.fListener = fListener;
		this.listener = listener;
		this.aq = aq;
		this.url = url;
		this.date = date;
		reqeustGetProfiles(listener, aq, url, date);
	}

	public void reqeustGetProfiles(AqueryGetProfilesCallbackListener listener,
			AQuery aq, String url, String date) {

		mListener = listener;
		// callback 설정
		AjaxCallback<JSONObject> cb = new AjaxCallback<JSONObject>() {
			@Override
			public void callback(String url, JSONObject json, AjaxStatus status) {
				// TODO Auto-generated method stub
				if (fListener != null) {
					fListener.AqueryFailCallback(url, json, status, TAG);
				}
				if (mListener != null)
					mListener.AqueryGetProfilesCallback(url, json, status);
				else {
					Log.e(TAG, "AqueryCallbackListener ERROR!");
				}
			}
		};

		// Log.d(TAG, "selected_date : " + date);

		Map<String, Object> mParams = new HashMap<String, Object>();
		mParams.put("selected_date", date);

		cb.cookies(PropertyManager.getInstance().getCookie());
		// request 전송
		aq.ajax(url, mParams, JSONObject.class, cb);
	}

	@Override
	public void reRequest() {

		reqeustGetProfiles(listener, aq, url, date);
		super.reRequest();
	}
}
