package com.allchange.guestbook.aquery.searchlist;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.util.Log;

import com.allchange.guestbook.aquery.AqueryCallbackListener;
import com.allchange.guestbook.aquery.AqueryFailCallbackListener;
import com.allchange.guestbook.aquery.AqueryRootRequest;
import com.allchange.guestbook.property.PropertyManager;
import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

public class AquerySearchList extends AqueryRootRequest {
	public static final String TAG = "AquerySearchList";
	private AqueryCallbackListener mListener;
	AqueryFailCallbackListener fListener;
	AqueryCallbackListener listener;
	AQuery aq;
	String url, text;

	public AquerySearchList(AqueryCallbackListener listener, AQuery aq,
			String url, String text, AqueryFailCallbackListener fListener) {
		this.listener = listener;
		this.fListener = fListener;
		this.aq = aq;
		this.url = url;
		this.text = text;
		requestSearchList(listener, aq, url, text);
	}

	public void requestSearchList(AqueryCallbackListener listener, AQuery aq,
			String url, String text) {
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
					mListener.AqueryCallback(url, json, status);
				else {
					Log.e(TAG, "AqueryCallbackListener ERROR!");
				}
			}
		};

		Map<String, Object> mParams = new HashMap<String, Object>();
		mParams.put("search_word ", text);

		cb.cookies(PropertyManager.getInstance().getCookie());
		// request 전송
		aq.ajax(url, mParams, JSONObject.class, cb);
	}

	@Override
	public void reRequest() {
		requestSearchList(listener, aq, url, text);
	}
}
