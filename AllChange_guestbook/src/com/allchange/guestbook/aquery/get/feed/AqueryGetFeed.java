package com.allchange.guestbook.aquery.get.feed;

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

public class AqueryGetFeed extends AqueryRootRequest {
	public static final String TAG = "AqueryGetFeed";
	private AqueryGetFeedCallbackListener mListener;
	AqueryGetFeedCallbackListener listener;
	AqueryFailCallbackListener fListener;
	AQuery aq;
	String url;
	String message_dtime;

	public AqueryGetFeed(AqueryGetFeedCallbackListener listener, AQuery aq,
			String url, String message_dtime,
			AqueryFailCallbackListener fListener) {
		this.listener = listener;
		this.fListener = fListener;
		this.aq = aq;
		this.url = url;
		this.message_dtime = message_dtime;
		requestGetFeed(listener, aq, url, message_dtime);
	}

	public void requestGetFeed(AqueryGetFeedCallbackListener listener,
			AQuery aq, String url, String message_dtime) {
		mListener = listener;
		// callback 설정
		AjaxCallback<JSONObject> cb = new AjaxCallback<JSONObject>() {
			@Override
			public void callback(String url, JSONObject json, AjaxStatus status) {
				if (fListener != null) {
					fListener.AqueryFailCallback(url, json, status, TAG);
				}
				if (mListener != null)
					mListener.getFeedCallback(url, json, status);
				else {
					Log.e(TAG, "AqueryCallbackListener ERROR!");
				}
			}
		};

		Map<String, Object> mParams = new HashMap<String, Object>();

		if (message_dtime != null) {
			mParams.put("message_dtime", message_dtime);
		} else {
			mParams.put("message_dtime", 0);
		}

		cb.cookies(PropertyManager.getInstance().getCookie());
		// request 전송
		aq.ajax(url, mParams, JSONObject.class, cb);
	}

	@Override
	public void reRequest() {
		requestGetFeed(listener, aq, url, message_dtime);
		super.reRequest();
	}
}
