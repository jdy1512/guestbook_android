package com.allchange.guestbook.aquery.get.properties;

import org.json.JSONObject;

import android.util.Log;

import com.allchange.guestbook.aquery.AqueryRootRequest;
import com.allchange.guestbook.property.PropertyManager;
import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

public class AqueryGetProperties extends AqueryRootRequest {
	public static final String TAG = "AqueryGetProperties";
	private AqueryGetPropertiesCallbackListener mListener;
	AqueryGetPropertiesCallbackListener listener;
	AQuery aq;
	String url;
	String date;

	public AqueryGetProperties(AqueryGetPropertiesCallbackListener listener,
			AQuery aq, String url) {
		this.listener = listener;
		this.aq = aq;
		this.url = url;
		reqeustGetProfiles(listener, aq, url, date);
	}

	public void reqeustGetProfiles(
			AqueryGetPropertiesCallbackListener listener, AQuery aq,
			String url, String date) {

		mListener = listener;
		// callback 설정
		AjaxCallback<JSONObject> cb = new AjaxCallback<JSONObject>() {
			@Override
			public void callback(String url, JSONObject json, AjaxStatus status) {
				if (mListener != null)
					mListener.AqueryGetPropertiesCallback(url, json, status);
				else {
					Log.e(TAG, "AqueryCallbackListener ERROR!");
				}
			}
		};

		cb.cookies(PropertyManager.getInstance().getCookie());
		// request 전송
		aq.ajax(url, JSONObject.class, cb);
	}

	@Override
	public void reRequest() {

		reqeustGetProfiles(listener, aq, url, date);
		super.reRequest();
	}
}
